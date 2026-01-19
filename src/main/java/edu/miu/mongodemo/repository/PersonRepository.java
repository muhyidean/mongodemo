package edu.miu.mongodemo.repository;

import edu.miu.mongodemo.model.Person;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PersonRepository extends ReactiveMongoRepository<Person, String> {
    Flux<Person> findByJob(String job);
    
    /**
     * Tailable cursor query
     * 
     * @Tailable annotation creates a tailable cursor that stays open
     * and continuously returns new documents as they are inserted.
     * This only works with capped collections.
     * 
     * The cursor will block and wait for new documents to appear.
     * When a new Person is inserted, it will be emitted through this Flux.
     */
    @Tailable
    Flux<Person> findWithTailableCursorBy();
}


