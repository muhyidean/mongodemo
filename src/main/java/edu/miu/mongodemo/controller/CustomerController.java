package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Customer;
import edu.miu.mongodemo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Example 1: Static data with delay - generates customers every 3 seconds
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> getAllCustomersStream() {
        Flux<Customer> customerFlux = Flux.just(
                new Customer(1L, "Walter", "White", 29),
                new Customer(2L, "Skyler", "White", 24),
                new Customer(3L, "Saul", "Goodman", 27),
                new Customer(4L, "Jesse", "Pinkman", 24)
        ).delayElements(Duration.ofSeconds(3));
        return customerFlux;
    }

    /**
     * Example 2: Get all customers from MongoDB repository (reactive)
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Example 3: Get all customers as JSON array (non-streaming)
     */
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Customer> getAllCustomersJson() {
        return customerRepository.findAll();
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public Mono<Customer> getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Create a new customer
     */
    @PostMapping
    public Mono<Customer> createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Find customers by firstname
     */
    @GetMapping(value = "/firstname/{firstname}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> getCustomersByFirstname(@PathVariable String firstname) {
        return customerRepository.findByFirstname(firstname);
    }

    /**
     * Delete customer by ID
     */
    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable Long id) {
        return customerRepository.deleteById(id);
    }
}


