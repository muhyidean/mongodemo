package edu.miu.mongodemo.kafka;

import edu.miu.mongodemo.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner to send Person objects to Kafka on application startup
 * 
 * Similar to the Order example - sends Person objects to Kafka when the application starts.
 * 
 * This will run automatically when the application starts.
 * You can comment out @Component to disable it.
 * 
 * To use this:
 * 1. Make sure Kafka is running on localhost:9092
 * 2. Create a topic named "person-topic" (or change the topic name below)
 * 3. Start the application
 * 4. Check your Kafka consumer to see the Person objects
 */
@Component
public class PersonKafkaRunner implements CommandLineRunner {

    @Autowired
    private PersonSender personSender;

    @Override
    public void run(String... args) throws Exception {
        // Send Person objects to Kafka on startup
        Person person1 = new Person("John Doe", "Software Engineer");
        personSender.send("person-topic", person1);
        System.out.println("Person has been sent");
        
        Person person2 = new Person("Jane Smith", "Data Scientist");
        personSender.send("person-topic", person2);
        System.out.println("Person has been sent");
    }
}

