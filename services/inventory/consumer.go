package main

import (
	"encoding/json"
	"log"

	"github.com/IBM/sarama"
)

// InventoryConsumer implements sarama.ConsumerGroupHandler

type MessageWrapper struct {
	EventType string           `json:"eventType"`
	InvMsg    InventoryMessage `json:"invMsg"`
}

type InventoryMessage struct {
	Sku         int    `json:"sku"`
	Description string `json:"description"`
}

type InventoryConsumer struct{}

func (c *InventoryConsumer) Setup(sarama.ConsumerGroupSession) error   { return nil }
func (c *InventoryConsumer) Cleanup(sarama.ConsumerGroupSession) error { return nil }

func (c *InventoryConsumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for message := range claim.Messages() {
		log.Printf("Message received:\nTopic: %s\nPartition: %d\nOffset: %d\nKey: %s\nValue: %s\n\n",
			message.Topic, message.Partition, message.Offset, string(message.Key), string(message.Value))

		// TODO
		var invMsg MessageWrapper

		err := json.Unmarshal(message.Value, &invMsg)
		if err != nil {
			log.Println(err)
		}

		switch invMsg.EventType {
		case "inv_check":
		case "order_fulfilled":
		}

		// Mark message as processed
		session.MarkMessage(message, "")
	}
	return nil
}
