package edu.miu.mongodemo.repository;

import edu.miu.mongodemo.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, Long> {
    Flux<Customer> findByFirstname(String firstname);
    Flux<Customer> findByLastname(String lastname);
    Flux<Customer> findByAge(int age);
}





