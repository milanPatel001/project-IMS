package configs

import (
	"log"
	"strings"

	"github.com/IBM/sarama"
)

type KafkaAdmin struct {
	admin sarama.ClusterAdmin
}

func NewKafkaAdmin(brokers []string, version sarama.KafkaVersion) (*KafkaAdmin, error) {
	config := sarama.NewConfig()
	config.Version = version

	admin, err := sarama.NewClusterAdmin(brokers, config)
	if err != nil {
		return nil, err
	}

	return &KafkaAdmin{admin: admin}, nil
}

func (ka *KafkaAdmin) GetMatchingTopicsBySuffixes(suffixes []string) ([]string, error) {
	topics, err := ka.admin.ListTopics()
	if err != nil {
		return nil, err
	}

	var matches []string
	for topic := range topics {
		for _, suffix := range suffixes {
			if strings.HasSuffix(topic, suffix) {
				matches = append(matches, topic)
				break
			}
		}
	}

	return matches, nil
}

func (ka *KafkaAdmin) ListTopics() error {
	topics, err := ka.admin.ListTopics()
	if err != nil {
		return err
	}

	for topic, detail := range topics {
		log.Printf("FOUND: Topic: %s | Partitions: %d | Replication: %d\n",
			topic, detail.NumPartitions, detail.ReplicationFactor)
	}

	return nil
}

func (ka *KafkaAdmin) DeleteTopic(name string) error {
	err := ka.admin.DeleteTopic(name)
	if err != nil {
		return err
	}

	log.Printf("Topic %s deleted\n", name)
	return nil
}

func (ka *KafkaAdmin) DescribeTopicConfig(name string) error {
	res, err := ka.admin.DescribeConfig(sarama.ConfigResource{
		Type: sarama.TopicResource,
		Name: name,
	})
	if err != nil {
		return err
	}

	for _, entry := range res {
		log.Printf("%s = %s (default=%t)\n", entry.Name, entry.Value, entry.Default)
	}

	return nil
}

func (ka *KafkaAdmin) Close() {
	if err := ka.admin.Close(); err != nil {
		log.Printf("Error closing Kafka admin: %v", err)
	}
}
