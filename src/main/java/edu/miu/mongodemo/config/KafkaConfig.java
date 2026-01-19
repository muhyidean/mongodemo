package edu.miu.mongodemo.config;

import edu.miu.mongodemo.model.Person;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Configuration
 * 
 * Configures Kafka producers and consumers for Person events
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Producer Factory for publishing Person objects to Kafka
     */
    @Bean
    public ProducerFactory<String, Person> personProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate for sending Person messages to Kafka
     */
    @Bean
    public KafkaTemplate<String, Person> kafkaTemplate() {
        return new KafkaTemplate<>(personProducerFactory());
    }

    /**
     * Consumer Factory for consuming Person objects from Kafka
     */
    @Bean
    public ConsumerFactory<String, Person> personConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "person-websocket-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka Listener Container Factory for @KafkaListener annotations
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Person> personKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Person> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(personConsumerFactory());
        // Don't fail if Kafka is not available - will retry automatically
        factory.setAutoStartup(true);
        return factory;
    }
}

