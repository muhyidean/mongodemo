package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Person;
import edu.miu.mongodemo.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonRepository personRepository;

    /**
     * Example: Get all persons with job="Developer" from MongoDB repository (reactive)
     * This endpoint streams persons as they are retrieved from the database
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Person> getAllPersons() {
        logger.info("Fetching all persons with job='Developer' (reactive stream)");
        MDC.put("job", "Developer");
        long startTime = System.currentTimeMillis();
        try {
            Flux<Person> persons = personRepository.findByJob("Developer");
            return persons
                .doOnNext(person -> logger.debug("Streaming person: {}", person.getName()))
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logger.info("Completed streaming persons in {}ms", duration);
                    MDC.put("response_time_ms", String.valueOf(duration));
                })
                .doOnError(error -> {
                    logger.error("Error streaming persons", error);
                    MDC.put("error", error.getMessage());
                })
                .doFinally(signalType -> {
                    logger.debug("Stream completed with signal: {}", signalType);
                    MDC.clear();
                });
        } catch (Exception e) {
            logger.error("Error fetching persons", e);
            MDC.clear();
            throw e;
        }
    }
}





