package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for Person Kafka Events
 * 
 * This controller:
 * 1. Listens to Kafka topic "person-events" 
 * 2. Broadcasts received Person messages to WebSocket clients
 * 3. Provides a WebSocket endpoint for clients to subscribe
 * 
 * Flow:
 * MongoDB (Capped Collection) -> Tailable Cursor -> Kafka -> WebSocket -> UI Client
 */
@Controller
public class PersonKafkaWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(PersonKafkaWebSocketController.class);
    private static final String KAFKA_TOPIC = "person-events";
    private static final String WEBSOCKET_TOPIC = "/topic/persons/kafka";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Kafka Listener that consumes Person events from Kafka
     * 
     * This method is called whenever a new Person is published to the Kafka topic.
     * It then broadcasts the Person to all WebSocket clients subscribed to the topic.
     * 
     * Note: If Kafka is not running, this listener will not receive messages.
     * The application will continue to run and will automatically connect when Kafka becomes available.
     * 
     * @param person The Person object received from Kafka
     */
    @KafkaListener(
        topics = KAFKA_TOPIC, 
        groupId = "person-websocket-group",
        containerFactory = "personKafkaListenerContainerFactory"
    )
    public void consumePersonEvent(Person person) {
        logger.info("Received Person from Kafka: {}", person);
        
        try {
            // Broadcast to all WebSocket clients subscribed to /topic/persons/kafka
            messagingTemplate.convertAndSend(WEBSOCKET_TOPIC, person);
            logger.debug("Broadcasted Person {} to WebSocket topic {}", person.getId(), WEBSOCKET_TOPIC);
        } catch (Exception e) {
            logger.error("Error broadcasting Person to WebSocket: {}", person, e);
        }
    }

    /**
     * WebSocket message handler for client subscriptions
     * 
     * Clients can send a message to /app/person/subscribe to explicitly subscribe
     * (though subscription to /topic/persons/kafka works without this)
     */
    @MessageMapping("/person/subscribe")
    public void subscribeToPersonEvents() {
        logger.info("Client subscribed to person events");
        // The actual subscription happens on the client side via STOMP
        // This endpoint can be used for acknowledgment or logging
    }
}

