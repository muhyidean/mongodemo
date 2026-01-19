package edu.miu.mongodemo.service;

import edu.miu.mongodemo.model.Person;
import edu.miu.mongodemo.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;

/**
 * Service that consumes from MongoDB tailable cursor and publishes to Kafka
 * 
 * This service:
 * 1. Subscribes to the tailable cursor from PersonRepository
 * 2. Publishes each new Person document to Kafka topic
 * 3. Runs asynchronously to not block the main thread
 */
@Service
public class PersonKafkaService {

    private static final Logger logger = LoggerFactory.getLogger(PersonKafkaService.class);
    private static final String KAFKA_TOPIC = "person-events";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private KafkaTemplate<String, Person> kafkaTemplate;

    private Disposable subscription;

    /**
     * Initialize the tailable cursor subscription after bean creation
     * This will start listening for new Person documents
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing PersonKafkaService - starting tailable cursor subscription");
        // Start subscription asynchronously to avoid blocking bean initialization
        new Thread(this::startTailableCursorSubscription).start();
    }

    /**
     * Start subscribing to the tailable cursor
     * This creates a non-blocking subscription that will emit new Person documents
     */
    private void startTailableCursorSubscription() {
        Flux<Person> tailableCursor = personRepository.findWithTailableCursorBy();
        
        subscription = tailableCursor
                .doOnNext(person -> {
                    logger.info("Received new Person from tailable cursor: {}", person);
                    publishToKafka(person);
                })
                .doOnError(error -> {
                    logger.error("Error in tailable cursor subscription", error);
                    // Re-subscribe after error (tailable cursors can fail)
                    try {
                        Thread.sleep(1000); // Wait 1 second before re-subscribing
                        startTailableCursorSubscription();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Interrupted while re-subscribing", e);
                    }
                })
                .doOnComplete(() -> {
                    logger.warn("Tailable cursor completed (unexpected for capped collections)");
                })
                .subscribe(
                        person -> logger.debug("Processing person: {}", person),
                        error -> logger.error("Subscription error", error),
                        () -> logger.info("Subscription completed")
                );
        
        logger.info("Tailable cursor subscription started");
    }

    /**
     * Publish Person to Kafka topic
     * 
     * @param person The Person document to publish
     */
    private void publishToKafka(Person person) {
        try {
            // Use person ID as the key for Kafka partitioning
            String key = person.getId() != null ? person.getId() : "unknown";
            
            CompletableFuture<SendResult<String, Person>> future = 
                    kafkaTemplate.send(KAFKA_TOPIC, key, person);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Successfully published Person {} to Kafka topic {}", 
                            person.getId(), KAFKA_TOPIC);
                } else {
                    logger.error("Failed to publish Person {} to Kafka", person.getId(), exception);
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing Person to Kafka: {}", person, e);
        }
    }

    /**
     * Cleanup subscription on bean destruction
     */
    @PreDestroy
    public void cleanup() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            logger.info("Tailable cursor subscription disposed");
        }
    }
}

