package main

import (
	"context"
	"os"
	"os/signal"

	"github.com/hashicorp/go-uuid"
	"github.com/ims/configs"
)

func main() {

	port := "9000"
	service := "Inventory"
	id, _ := uuid.GenerateUUID()
	groupId := "ims-inv"

	eventListeners := []string{"order_placed", "order_fulfilled", "order_cancelled"}

	ctx, cancel := context.WithCancel(context.Background())
	kafkaManager := configs.NewKafkaManager(service, groupId, eventListeners)

	kafkaManager.Start(ctx) // launches a goroutine

	go configs.SetupHttpServer(ctx, port, service, id, kafkaManager, nil)

	sigchan := make(chan os.Signal, 1)
	signal.Notify(sigchan, os.Interrupt)
	<-sigchan
	cancel()

}
