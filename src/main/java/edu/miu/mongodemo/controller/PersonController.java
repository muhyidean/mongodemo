package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Person;
import edu.miu.mongodemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Example: Get all persons with job="Developer" from MongoDB repository (reactive)
     * This endpoint streams persons as they are retrieved from the database
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Person> getAllPersons() {
        return personRepository.findByJob("Developer");
    }
}




