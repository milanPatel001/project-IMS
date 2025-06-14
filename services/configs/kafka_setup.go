package configs

import (
	"context"
	"log"
	"sync"
	"time"

	"github.com/IBM/sarama"
)

type KafkaManager struct {
	eventListeners  []string
	serviceName     string
	groupId         string
	mutex           sync.RWMutex
	isRunning       bool
	cancel          context.CancelFunc
	consumerHandler sarama.ConsumerGroupHandler
}

func NewKafkaManager(serviceName, groupId string, eventListeners []string, consumerHandler sarama.ConsumerGroupHandler) *KafkaManager {
	return &KafkaManager{
		eventListeners:  eventListeners,
		serviceName:     serviceName,
		groupId:         groupId,
		isRunning:       false,
		consumerHandler: consumerHandler,
		mutex:           sync.RWMutex{},
	}
}

func (km *KafkaManager) Start(ctx context.Context) {
	km.mutex.Lock()
	defer km.mutex.Unlock()

	if km.isRunning {
		return
	}

	kafkaCtx, cancel := context.WithCancel(ctx)
	km.cancel = cancel

	go LaunchKafkaClient(kafkaCtx, km.serviceName, km.groupId, km.eventListeners, km.consumerHandler)
	km.isRunning = true
}

func (km *KafkaManager) Stop() {
	km.mutex.Lock()
	defer km.mutex.Unlock()

	if !km.isRunning {
		return
	}

	if km.cancel != nil {
		km.cancel()
		km.cancel = nil
	}
	km.isRunning = false
}

func (km *KafkaManager) Restart(ctx context.Context) {
	km.Stop()
	time.Sleep(100 * time.Millisecond)
	km.Start(ctx)
}

func (km *KafkaManager) IsRunning() bool {
	km.mutex.RLock()
	defer km.mutex.RUnlock()
	return km.isRunning
}

func LaunchKafkaClient(ctx context.Context, service string, groupId string, topicArr []string, consumer sarama.ConsumerGroupHandler) {
	brokers := []string{"localhost:9092"} // TODO : kafka client url

	config := sarama.NewConfig()
	config.Version = sarama.V4_0_0_0
	config.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	config.Consumer.Offsets.Initial = sarama.OffsetOldest

	kafkaAdmin, err := NewKafkaAdmin(brokers, config.Version)

	if err != nil {
		log.Fatalf("Error creating kafka admin: %v", err)
	}
	defer kafkaAdmin.Close()

	consumerGroup, err := sarama.NewConsumerGroup(brokers, groupId, config)
	if err != nil {
		log.Fatalf("Error creating consumer group: %v", err)
	}
	defer consumerGroup.Close()

	handler := consumer //&Consumer{}

	topics, err := kafkaAdmin.GetMatchingTopicsBySuffixes(topicArr)
	if err != nil {
		log.Fatalf("Failed to fetch topics: %v", err)
	}
	if len(topics) == 0 {
		log.Println("No matching topics found.")
		return
	}

	log.Printf("\n%v : Kafka client has started successfully...", service)

	for {
		if err := consumerGroup.Consume(ctx, topics, handler); err != nil {
			log.Printf("Error during consumption: %v", err)
		}
		if ctx.Err() != nil {
			return
		}
	}
}
