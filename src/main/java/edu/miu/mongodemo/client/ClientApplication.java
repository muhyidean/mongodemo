package edu.miu.mongodemo.client;

import edu.miu.mongodemo.model.Customer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

/**
 * Client application example that consumes the reactive Customer API
 * 
 * This demonstrates how to use WebClient to consume a reactive endpoint
 * that returns a Flux of customers.
 * 
 * To run this as a standalone application, you can create a separate main class
 * or run this from the main application.
 */
public class ClientApplication {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting client to consume reactive Customer API...");
        
        // Create WebClient to consume the reactive endpoint
        Flux<Customer> result = WebClient.create("http://localhost:8080/customers")
                .get()
                .retrieve()
                .bodyToFlux(Customer.class);
        
        // Subscribe to the Flux and print each customer as it arrives
        result.subscribe(s -> {
            System.out.print(LocalDateTime.now() + " : ");
            System.out.println(s);
        });
        
        // Keep the main thread alive for 15 seconds to see the streaming results
        Thread.sleep(15000);
        
        System.out.println("Client finished.");
    }
}




