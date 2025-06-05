package main

import (
	"context"
	"log"
	"os"
	"os/signal"

	"github.com/IBM/sarama"
)

func main() {
	brokers := []string{"localhost:9092"}
	//topic := "1.inv_check"
	groupID := "ims-inv"

	config := sarama.NewConfig()
	config.Version = sarama.V4_0_0_0
	config.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	config.Consumer.Offsets.Initial = sarama.OffsetOldest

	kafkaAdmin, err := NewKafkaAdmin(brokers, config.Version)

	if err != nil {
		log.Fatalf("Error creating kafka admin: %v", err)
	}
	defer kafkaAdmin.Close()

	consumerGroup, err := sarama.NewConsumerGroup(brokers, groupID, config)
	if err != nil {
		log.Fatalf("Error creating consumer group: %v", err)
	}
	defer consumerGroup.Close()

	ctx, cancel := context.WithCancel(context.Background())
	go handleInterrupt(ctx, cancel)

	handler := &Consumer{}

	topics, err := kafkaAdmin.GetMatchingTopicsBySuffixes([]string{
		"order_placed",
		"order_fulfilled",
		"order_cancelled",
	})
	if err != nil {
		log.Fatalf("Failed to fetch topics: %v", err)
	}
	if len(topics) == 0 {
		log.Println("No matching topics found.")
		return
	}

	log.Println("Inventory Service has started...")

	for {
		if err := consumerGroup.Consume(ctx, topics, handler); err != nil {
			log.Printf("Error during consumption: %v", err)
		}
		if ctx.Err() != nil {
			return
		}
	}

}

func handleInterrupt(ctx context.Context, cancel context.CancelFunc) {
	sigchan := make(chan os.Signal, 1)
	signal.Notify(sigchan, os.Interrupt)
	<-sigchan
	cancel()
}
