package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.kafka.PersonSender;
import edu.miu.mongodemo.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for sending Person objects to Kafka
 * 
 * This controller provides endpoints to send Person objects to Kafka topics.
 * Useful for testing and integration with other services.
 * 
 * Example usage:
 * POST /api/kafka/person/send
 * Body: {
 *   "topic": "person-topic",
 *   "name": "John Doe",
 *   "job": "Software Engineer"
 * }
 */
@RestController
@RequestMapping("/api/kafka/person")
public class PersonKafkaController {

    @Autowired
    private PersonSender personSender;

    /**
     * Sends a Person object to a Kafka topic
     * 
     * @param topic The Kafka topic name
     * @param person The Person object to send
     * @return Success response
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendPerson(
            @RequestParam(defaultValue = "person-topic") String topic,
            @RequestBody Person person) {
        try {
            personSender.send(topic, person);
            return ResponseEntity.ok("Person sent to topic '" + topic + "' successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending person: " + e.getMessage());
        }
    }

    /**
     * Sends a Person object to Kafka with a specific key
     * 
     * @param topic The Kafka topic name
     * @param key The message key (useful for partitioning)
     * @param person The Person object to send
     * @return Success response
     */
    @PostMapping("/send-with-key")
    public ResponseEntity<String> sendPersonWithKey(
            @RequestParam(defaultValue = "person-topic") String topic,
            @RequestParam String key,
            @RequestBody Person person) {
        try {
            personSender.send(topic, key, person);
            return ResponseEntity.ok("Person sent to topic '" + topic + "' with key '" + key + "' successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending person: " + e.getMessage());
        }
    }

    /**
     * Quick test endpoint to send a sample Person
     * 
     * @param topic The Kafka topic name (default: person-topic)
     * @return Success response
     */
    @PostMapping("/test")
    public ResponseEntity<String> testSend(
            @RequestParam(defaultValue = "person-topic") String topic) {
        try {
            Person testPerson = new Person("Test User", "Test Job");
            personSender.send(topic, testPerson);
            return ResponseEntity.ok("Test person sent to topic '" + topic + "' successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending test person: " + e.getMessage());
        }
    }
}


