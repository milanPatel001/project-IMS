package main

import (
	"context"
	"log"
	"os"
	"os/signal"

	"github.com/hashicorp/go-uuid"
	"github.com/ims/configs"
	"github.com/jackc/pgx/v5"
	"github.com/joho/godotenv"
)

func main() {

	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	service := "Inventory"
	port := os.Getenv("INV_PORT")
	groupId := os.Getenv("INV_GROUP_ID")
	dbUrl := os.Getenv("DB_URL")

	id, err := uuid.GenerateUUID()
	if err != nil {
		log.Fatal("Not able to generate UUID")
	}

	eventListeners := []string{"inv_check", "order_fulfilled"}

	ctx, cancel := context.WithCancel(context.Background())

	conn, err := pgx.Connect(ctx, dbUrl)
	if err != nil {
		log.Fatal(err)
	}
	defer conn.Close(ctx)

	invConsumer := &InventoryConsumer{}
	kafkaManager := configs.NewKafkaManager(service, groupId, eventListeners, invConsumer)

	kafkaManager.Start(ctx) // launches a goroutine

	go configs.SetupHttpServer(ctx, port, service, id, kafkaManager, conn, nil)

	sigchan := make(chan os.Signal, 1)
	signal.Notify(sigchan, os.Interrupt)
	<-sigchan
	cancel()

}
