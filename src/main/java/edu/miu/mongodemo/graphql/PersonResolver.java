package edu.miu.mongodemo.graphql;

import edu.miu.mongodemo.model.Person;
import edu.miu.mongodemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Resolver for Person operations
 * 
 * Note: PersonRepository is reactive (ReactiveMongoRepository),
 * so we need to convert reactive types to blocking calls for GraphQL.
 * In a production environment, consider using reactive GraphQL.
 */
@Controller
public class PersonResolver {

    @Autowired
    private PersonRepository personRepository;

    // ============================================================================
    // QUERIES
    // ============================================================================

    /**
     * Query: Get all persons
     * GraphQL Query:
     * {
     *   persons {
     *     id
     *     name
     *     job
     *   }
     * }
     */
    @QueryMapping
    public List<Person> persons() {
        // Convert reactive Flux to List
        return personRepository.findAll()
                .collectList()
                .block(); // Blocking call - converts reactive to blocking
    }

    /**
     * Query: Get person by ID
     * GraphQL Query:
     * {
     *   person(id: "456") {
     *     id
     *     name
     *     job
     *   }
     * }
     */
    @QueryMapping
    public Person person(@Argument String id) {
        return personRepository.findById(id)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
    }

    // ============================================================================
    // MUTATIONS
    // ============================================================================

    /**
     * Mutation: Create a new person
     * GraphQL Mutation:
     * mutation {
     *   createPerson(person: {
     *     name: "Jane Smith"
     *     job: "Software Engineer"
     *   }) {
     *     id
     *     name
     *     job
     *   }
     * }
     */
    @MutationMapping
    public Person createPerson(@Argument("person") PersonInput input) {
        Person person = new Person(input.name(), input.job());
        return personRepository.save(person).block();
    }

    // ============================================================================
    // INPUT RECORDS
    // ============================================================================

    /**
     * Input DTO for creating persons
     * Maps to PersonInput in GraphQL schema
     */
    public record PersonInput(
            String name,
            String job
    ) {}
}

