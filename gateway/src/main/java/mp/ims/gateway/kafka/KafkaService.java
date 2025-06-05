package mp.ims.gateway.kafka;

import jakarta.annotation.PostConstruct;
import mp.ims.gateway.dao.OrganizationDao;
import mp.ims.gateway.models.Organization;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaService {
    private final OrganizationDao organizationDao;
    private final KafkaProducer kafkaProducer;
    private final KafkaAdmin kafkaAdmin;

    @Autowired
    public KafkaService(OrganizationDao organizationDao, KafkaProducer kafkaProducer, KafkaAdmin kafkaAdmin) {
        this.organizationDao = organizationDao;
        this.kafkaProducer = kafkaProducer;
        this.kafkaAdmin = kafkaAdmin;
    }

    @PostConstruct
    public void createTenantTopics() {
        List<Organization> tenants = organizationDao.findAll();

        tenants.forEach(tenant -> {
            createTopicIfNotExists(tenant.getId() + ".order_placed");
            createTopicIfNotExists(tenant.getId() + ".order_fulfilled");
            createTopicIfNotExists(tenant.getId() + ".order_cancelled");
        });
    }

    public boolean sendPayload(Long tenantId, String eventType, Object eventPayload){
        return kafkaProducer.sendEvent(String.valueOf(tenantId), eventType, eventPayload);
    }

    private void createTopicIfNotExists(String topicName) {
        try (var adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())){

            var existingTopics = adminClient.listTopics().names().get();

            if (!existingTopics.contains(topicName)) {
                var newTopic = new NewTopic(topicName, 1, (short) 1);
                adminClient.createTopics(List.of(newTopic)).all().get();

                System.out.println("Created topic: " + topicName);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
