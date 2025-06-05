package mp.ims.gateway.controllers;

import mp.ims.gateway.kafka.KafkaProducer;
import mp.ims.gateway.kafka.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final KafkaService kafkaService;

    @Autowired
    public InventoryController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping
    public ResponseEntity<Boolean> checkKafkaConnection(@RequestParam Long id, @RequestParam String body, @RequestParam String event){
        boolean success = kafkaService.sendPayload(id, event, body);

        return ResponseEntity.ok(success);
    }
}
