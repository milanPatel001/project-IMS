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
	topic := "1.inv_check"
	groupID := "ims-inv"

	config := sarama.NewConfig()
	config.Version = sarama.V4_0_0_0
	config.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	config.Consumer.Offsets.Initial = sarama.OffsetOldest

	consumerGroup, err := sarama.NewConsumerGroup(brokers, groupID, config)
	if err != nil {
		log.Fatalf("Error creating consumer group: %v", err)
	}
	defer consumerGroup.Close()

	ctx, cancel := context.WithCancel(context.Background())
	go func() {
		sigchan := make(chan os.Signal, 1)
		signal.Notify(sigchan, os.Interrupt)
		<-sigchan
		cancel()
	}()

	handler := &Consumer{}

	for {
		if err := consumerGroup.Consume(ctx, []string{topic}, handler); err != nil {
			log.Printf("Error during consumption: %v", err)
		}
		if ctx.Err() != nil {
			return
		}
	}

}

// Consumer implements sarama.ConsumerGroupHandler
type Consumer struct{}

func (c *Consumer) Setup(sarama.ConsumerGroupSession) error   { return nil }
func (c *Consumer) Cleanup(sarama.ConsumerGroupSession) error { return nil }

func (c *Consumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for message := range claim.Messages() {
		log.Printf("Message received:\nTopic: %s\nPartition: %d\nOffset: %d\nKey: %s\nValue: %s\n\n",
			message.Topic, message.Partition, message.Offset, string(message.Key), string(message.Value))

		// Mark message as processed
		session.MarkMessage(message, "")
	}
	return nil
}
