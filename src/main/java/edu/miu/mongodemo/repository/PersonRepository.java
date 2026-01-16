package edu.miu.mongodemo.repository;

import edu.miu.mongodemo.model.Person;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PersonRepository extends ReactiveMongoRepository<Person, String> {
    Flux<Person> findByJob(String job);
}


