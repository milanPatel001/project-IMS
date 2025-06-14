package main

import (
	"log"

	"github.com/IBM/sarama"
)

// InventoryConsumer implements sarama.ConsumerGroupHandler
type InventoryConsumer struct{}

func (c *InventoryConsumer) Setup(sarama.ConsumerGroupSession) error   { return nil }
func (c *InventoryConsumer) Cleanup(sarama.ConsumerGroupSession) error { return nil }

func (c *InventoryConsumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for message := range claim.Messages() {
		log.Printf("Message received:\nTopic: %s\nPartition: %d\nOffset: %d\nKey: %s\nValue: %s\n\n",
			message.Topic, message.Partition, message.Offset, string(message.Key), string(message.Value))

		// Mark message as processed
		session.MarkMessage(message, "")
	}
	return nil
}
