package edu.miu.mongodemo.config;

import edu.miu.mongodemo.model.Customer;
import edu.miu.mongodemo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CustomerDataInitializer implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    private AtomicInteger customerCounter = new AtomicInteger(10);

    @Override
    public void run(String... args) throws Exception {
        // Initialize with some sample customers
        customerRepository.deleteAll()
                .thenMany(Flux.just(
                        new Customer(1L, "Walter", "White", 29),
                        new Customer(2L, "Skyler", "White", 24),
                        new Customer(3L, "Saul", "Goodman", 27),
                        new Customer(4L, "Jesse", "Pinkman", 24)
                ))
                .flatMap(customerRepository::save)
                .thenMany(customerRepository.findAll())
                .subscribe(System.out::println);
    }

    /**
     * Scheduled task to save a new customer every 3 seconds
     * This simulates continuous data generation similar to the Person example
     */
    @Scheduled(fixedRate = 3000)
    private void saveCustomer() {
        int x = customerCounter.getAndIncrement();
        Customer customer = new Customer(
                (long) x,
                "Customer" + x,
                "LastName" + x,
                20 + (x % 30)
        );
        customerRepository.save(customer)
                .subscribe(c -> System.out.println("Saved customer: " + c));
    }
}




