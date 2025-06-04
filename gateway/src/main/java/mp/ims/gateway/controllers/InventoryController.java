package mp.ims.gateway.controllers;

import mp.ims.gateway.kafka.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final KafkaProducer kafkaProducer;

    public InventoryController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping
    public ResponseEntity<Boolean> checkKafkaConnection(){
        boolean success = kafkaProducer.sendEvent("1", "inv_check", "hello from java");

        return ResponseEntity.ok(success);
    }
}
