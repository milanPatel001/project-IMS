package mp.ims.gateway.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    // blocks until ack
    public boolean sendEvent(String tenantId, String eventType, String message) {
        String topic = tenantId + "." + eventType;

        boolean isMessageRegistered = true;
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, message).get();
            System.out.println("Message sent successfully to topic: " + result.getRecordMetadata().topic() +
                    " at offset: " + result.getRecordMetadata().offset());

        } catch (Exception e) {
            System.err.println("Failed to send message to Kafka: " + e.getMessage());
            isMessageRegistered = false;
        }

        return isMessageRegistered;

    }

//    public void sendAsync(String topic, String message) {
//        kafkaTemplate.send(topic, message).addCallback(
//                result -> {
//                    if (result != null) {
//                        RecordMetadata metadata = result.getRecordMetadata();
//                        System.out.println("Sent to " + metadata.topic() +
//                                " partition " + metadata.partition() +
//                                " at offset " + metadata.offset());
//                    }
//                },
//                ex -> System.err.println("Failed to send message: " + ex.getMessage())
//        );
//    }

}
