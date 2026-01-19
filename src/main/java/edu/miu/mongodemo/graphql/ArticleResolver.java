package edu.miu.mongodemo.graphql;

import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.model.Person;
import edu.miu.mongodemo.repository.PersonRepository;
import edu.miu.mongodemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Resolver for Article operations
 * 
 * This resolver handles all GraphQL queries and mutations related to Articles.
 * Spring GraphQL automatically maps the methods to the schema definitions.
 * 
 * Key concepts:
 * - @QueryMapping: Maps to Query type in schema
 * - @MutationMapping: Maps to Mutation type in schema
 * - @Argument: Binds GraphQL arguments to method parameters
 */
@Controller
public class ArticleResolver {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private PersonRepository personRepository;

    // ============================================================================
    // QUERIES
    // ============================================================================

    /**
     * Query: Get all articles
     * GraphQL Query:
     * {
     *   articles {
     *     id
     *     title
     *     author
     *   }
     * }
     */
    @QueryMapping
    public List<Article> articles() {
        return articleService.getAllArticles();
    }

    /**
     * Query: Get article by ID
     * GraphQL Query:
     * {
     *   article(id: "123") {
     *     id
     *     title
     *     content
     *   }
     * }
     */
    @QueryMapping
    public Article article(@Argument String id) {
        return articleService.getArticleById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
    }

    /**
     * Query: Get articles by author
     * GraphQL Query:
     * {
     *   articlesByAuthor(author: "John Doe") {
     *     id
     *     title
     *   }
     * }
     */
    @QueryMapping
    public List<Article> articlesByAuthor(@Argument String author) {
        return articleService.getArticlesByAuthor(author);
    }

    /**
     * Query: Get articles by status
     * GraphQL Query:
     * {
     *   articlesByStatus(status: PUBLISHED) {
     *     id
     *     title
     *     status
     *   }
     * }
     */
    @QueryMapping
    public List<Article> articlesByStatus(@Argument String status) {
        return articleService.getArticlesByStatus(status);
    }

    /**
     * Query: Search articles by title
     * GraphQL Query:
     * {
     *   searchArticles(title: "Spring") {
     *     id
     *     title
     *   }
     * }
     */
    @QueryMapping
    public List<Article> searchArticles(@Argument String title) {
        return articleService.searchArticlesByTitle(title);
    }

    /**
     * Query: Get articles by tag
     * GraphQL Query:
     * {
     *   articlesByTag(tag: "java") {
     *     id
     *     title
     *     tags
     *   }
     * }
     */
    @QueryMapping
    public List<Article> articlesByTag(@Argument String tag) {
        return articleService.getArticlesByTag(tag);
    }

    // ============================================================================
    // MUTATIONS
    // ============================================================================

    /**
     * Mutation: Create a new article
     * GraphQL Mutation:
     * mutation {
     *   createArticle(article: {
     *     title: "GraphQL Introduction"
     *     content: "GraphQL is..."
     *     author: "John Doe"
     *     tags: ["graphql", "api"]
     *     status: PUBLISHED
     *   }) {
     *     id
     *     title
     *   }
     * }
     */
    @MutationMapping
    public Article createArticle(@Argument("article") ArticleInput input) {
        Article article = new Article();
        article.setTitle(input.title());
        article.setContent(input.content());
        article.setAuthor(input.author());
        
        if (input.tags() != null) {
            article.setTags(input.tags());
        }
        
        if (input.status() != null) {
            article.setStatus(input.status());
        }
        
        // Handle person reference if provided
        if (input.personId() != null) {
            // Convert reactive repository to blocking call for GraphQL
            Person person = personRepository.findById(input.personId())
                    .block(); // Blocking call - in production, consider using reactive GraphQL
            if (person != null) {
                article.setPerson(person);
            }
        }
        
        return articleService.createArticle(article);
    }

    /**
     * Mutation: Update an existing article
     * GraphQL Mutation:
     * mutation {
     *   updateArticle(id: "123", article: {
     *     title: "Updated Title"
     *     status: PUBLISHED
     *   }) {
     *     id
     *     title
     *   }
     * }
     */
    @MutationMapping
    public Article updateArticle(@Argument String id, @Argument("article") ArticleUpdateInput input) {
        Article existingArticle = articleService.getArticleById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
        
        if (input.title() != null) {
            existingArticle.setTitle(input.title());
        }
        if (input.content() != null) {
            existingArticle.setContent(input.content());
        }
        if (input.author() != null) {
            existingArticle.setAuthor(input.author());
        }
        if (input.tags() != null) {
            existingArticle.setTags(input.tags());
        }
        if (input.status() != null) {
            existingArticle.setStatus(input.status());
        }
        if (input.personId() != null) {
            Person person = personRepository.findById(input.personId()).block();
            if (person != null) {
                existingArticle.setPerson(person);
            }
        }
        
        return articleService.updateArticle(id, existingArticle);
    }

    /**
     * Mutation: Delete an article
     * GraphQL Mutation:
     * mutation {
     *   deleteArticle(id: "123")
     * }
     */
    @MutationMapping
    public Boolean deleteArticle(@Argument String id) {
        articleService.deleteArticle(id);
        return true;
    }

    /**
     * Mutation: Increment view count
     * GraphQL Mutation:
     * mutation {
     *   incrementViewCount(id: "123") {
     *     id
     *     viewCount
     *   }
     * }
     */
    @MutationMapping
    public Article incrementViewCount(@Argument String id) {
        return articleService.incrementViewCount(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
    }

    // ============================================================================
    // INPUT RECORDS (Data Transfer Objects)
    // ============================================================================

    /**
     * Input DTO for creating articles
     * Maps to ArticleInput in GraphQL schema
     */
    public record ArticleInput(
            String title,
            String content,
            String author,
            List<String> tags,
            String status,
            String personId
    ) {}

    /**
     * Input DTO for updating articles
     * Maps to ArticleUpdateInput in GraphQL schema
     * All fields are optional for partial updates
     */
    public record ArticleUpdateInput(
            String title,
            String content,
            String author,
            List<String> tags,
            String status,
            String personId
    ) {}
}

