package edu.miu.mongodemo.config;

import edu.miu.mongodemo.model.Person;
import edu.miu.mongodemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PersonDataInitializer {

    @Autowired
    private PersonRepository personRepository;

    private AtomicInteger x = new AtomicInteger(10);

    /**
     * Scheduled task to save a new person every 3 seconds
     * Similar to the example provided in the requirements
     */
    @Scheduled(fixedRate = 3000)
    private void savePerson() {
        int id = x.getAndIncrement();
        Person person = new Person("Person" + id, "Developer");
        personRepository.save(person)
                .subscribe(p -> System.out.println("Saved person: " + p));
    }
}


