package mp.ims.gateway.controllers;

import mp.ims.gateway.DTO.ItemRequestBody;
import mp.ims.gateway.kafka.KafkaProducer;
import mp.ims.gateway.kafka.KafkaService;
import mp.ims.gateway.models.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public ResponseEntity<?> createNewItem(@RequestBody ItemRequestBody itemRequestBody) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails c = (CustomUserDetails) authentication.getPrincipal();

        boolean messageSent = kafkaService.sendPayload(c.getOrgId(), "order_placed", itemRequestBody);

        if(!messageSent) ResponseEntity.status(500).body("Not sent");

        return ResponseEntity.ok("Done");
    }


}
