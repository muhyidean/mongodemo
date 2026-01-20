package edu.miu.mongodemo.kafka;

import edu.miu.mongodemo.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka Producer Service for Person Objects
 * 
 * This service sends Person objects to Kafka topics.
 * Other applications can consume these messages from Kafka.
 * 
 * Usage:
 * - Inject PersonSender into your controller or service
 * - Call send(topic, person) to send a Person object to Kafka
 * 
 * Example:
 *   personSender.send("person-topic", new Person("John Doe", "Engineer"));
 */
@Service
public class PersonSender {
    
    @Autowired
    private KafkaTemplate<String, Person> kafkaTemplate;

    /**
     * Sends a Person object to a Kafka topic
     * 
     * @param topic The Kafka topic name to send the message to
     * @param person The Person object to send
     */
    public void send(String topic, Person person) {
        kafkaTemplate.send(topic, person);
        System.out.println("Person sent to topic '" + topic + "': " + person);
    }

    /**
     * Sends a Person object to a Kafka topic with a specific key
     * 
     * @param topic The Kafka topic name to send the message to
     * @param key The message key (useful for partitioning)
     * @param person The Person object to send
     */
    public void send(String topic, String key, Person person) {
        kafkaTemplate.send(topic, key, person);
        System.out.println("Person sent to topic '" + topic + "' with key '" + key + "': " + person);
    }
}


