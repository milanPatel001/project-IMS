package configs

import (
	"bytes"
	"context"
	"encoding/json"
	"log"
	"net/http"
	"strings"
	"time"

	"github.com/jackc/pgx/v5"
)

type DiscoveryBody struct {
	Id        string `json:"id"`
	GroupName string `json:"groupName"`
	Port      string `json:"port"`
}

func SetupHttpServer(ctx context.Context, port string, service string, id string, kafkaManager *KafkaManager, conn *pgx.Conn, extraHandlers map[string]http.HandlerFunc) {

	err := sendDiscoveryRequest(id, port, service)

	if err != nil {
		log.Fatal(err)
	}

	http.HandleFunc("/ping", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("pong"))
	})

	http.HandleFunc("/restart", func(w http.ResponseWriter, r *http.Request) {

		log.Println("Restarting Kafka client...")

		kafkaManager.Restart(ctx)

		w.Write([]byte("Success"))
	})

	if extraHandlers != nil {
		for route, handler := range extraHandlers {
			log.Printf("\nRegistering route: %s", route)
			http.HandleFunc(route, handler)
		}
	}

	log.Printf("\nHTTP server running on :%v\n", port)
	if err := http.ListenAndServe(":"+port, nil); err != nil {
		log.Fatalf("HTTP server error: %v", err)
	}
}

func sendDiscoveryRequest(id string, port string, groupName string) error {

	jsonData, err := json.Marshal(DiscoveryBody{Id: id, Port: port, GroupName: groupName})

	if err != nil {
		return err
	}

	req, err := http.NewRequest("POST", "http://localhost:8000/register", bytes.NewBuffer(jsonData))
	if err != nil {
		return err
	}

	client := &http.Client{Timeout: 10 * time.Second}

	resp, err := client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if strings.HasPrefix(resp.Status, "20") {
		log.Println("Registered to discovery service...")
	}

	return nil
}
