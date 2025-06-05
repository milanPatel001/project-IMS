package main

import (
	"log"

	"github.com/IBM/sarama"
)

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
