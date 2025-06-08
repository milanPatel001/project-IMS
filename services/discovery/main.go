package main

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"sync"
	"time"
)

type ServerInstance struct {
	HostName  string
	IP        string
	Port      string `json:"port"`
	Id        string `json:"id"`
	LastSeen  time.Time
	isAlive   bool
	GroupName string `json:"groupName"`
}

type ServerRegistry struct {
	Map   map[string][]ServerInstance
	Mutex sync.Mutex
}

type DiscoveryBody struct {
	Id        string `json:"id"`
	GroupName string `json:"groupName"`
	Port      string `json:"port"`
}

func main() {

	serverRegistry := &ServerRegistry{Map: make(map[string][]ServerInstance), Mutex: sync.Mutex{}}

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	go heartBeatCheck(ctx, serverRegistry)

	http.HandleFunc("/ping", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("pong"))
	})

	http.HandleFunc("/register", registerService(serverRegistry))

	http.HandleFunc("/restart", restartServices(serverRegistry))

	log.Println("HTTP server running on :8000")
	if err := http.ListenAndServe(":8000", nil); err != nil {
		log.Fatalf("HTTP server error: %v", err)
	}
}

func restartServices(s *ServerRegistry) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

		groupName := r.URL.Query().Get("group")

		if groupName != "Inventory" && groupName != "Logging" && groupName != "Ordering" && groupName != "Supplier" {
			http.Error(w, "Bad request", http.StatusBadRequest)
			return
		}

		// TODO : do a rolling restart instead of this - choose one and then rest of them
		for _, inst := range s.Map[groupName] {
			err := restartRequest(inst)
			if err != nil {
				log.Printf("\n%w\n", err)
			}
		}

	}
}

func restartRequest(inst ServerInstance) error {
	url := "http://" + inst.HostName + ":" + inst.Port + "/restart"

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return err
	}

	client := &http.Client{Timeout: 10 * time.Second}

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("%v:%v server : Not able to reach the server...", inst.GroupName, inst.Id)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return fmt.Errorf("%v:%v server : %w", inst.GroupName, inst.Id, err)
	}

	if string(body) != "Success" {
		return fmt.Errorf("%v:%v server : Didn't receive correct response...", inst.GroupName, inst.Id)
	}

	return nil
}

func registerService(s *ServerRegistry) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {

		if r.Method != http.MethodPost {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return
		}

		var inst ServerInstance
		err := json.NewDecoder(r.Body).Decode(&inst)

		if err != nil {
			log.Printf("\n/registerService: %v", err)
			return
		}

		if inst.HostName == "" {
			inst.HostName = "localhost"
		}

		inst.LastSeen = time.Now()
		inst.isAlive = true

		s.Mutex.Lock()

		val, exists := s.Map[inst.GroupName]
		if !exists {
			s.Map[inst.GroupName] = []ServerInstance{inst}
		} else {
			s.Map[inst.GroupName] = append(val, inst)
		}

		s.Mutex.Unlock()

		log.Printf("\n%v : %v registered for the first time...\n", inst.GroupName, inst.Id)
		w.Write([]byte("Registered Successfully !!"))

	}
}

func heartBeatCheck(ctx context.Context, s *ServerRegistry) {

	ticker := time.NewTicker(20 * time.Second)
	defer ticker.Stop()

	for {
		select {
		case <-ticker.C:

			for group, instances := range s.Map {
				if instances == nil || len(instances) == 0 {
					continue
				}

				for index, instance := range instances {
					if !instance.isAlive {
						continue
					}

					err := heartBeatRequest(instance)
					if err != nil {
						s.Map[group][index].isAlive = false
						log.Println(err)
					} else {
						s.Map[group][index].LastSeen = time.Now()
					}
				}

			}

		case <-ctx.Done():
			log.Println("Shutting Down heartbeat checks...")
			return
		}
	}
}

func heartBeatRequest(inst ServerInstance) error {

	url := "http://" + inst.HostName + ":" + inst.Port + "/ping"

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return err
	}

	client := &http.Client{Timeout: 10 * time.Second}

	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("%v:%v Heartbeat : Not able to reach the server...", inst.GroupName, inst.Id)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return fmt.Errorf("%v:%v Heartbeat : %w", inst.GroupName, inst.Id, err)
	}

	if string(body) != "pong" {
		return fmt.Errorf("%v:%v Heartbeat : Didn't receive correct response...", inst.GroupName, inst.Id)
	}

	return nil
}

func checkIfRestarted(s *ServerRegistry, inst ServerInstance) bool {
	arr := s.Map[inst.GroupName]
	for _, service := range arr {
		if service.IP == inst.IP && service.Port == inst.Port {
			return true
		}
	}

	return false
}
