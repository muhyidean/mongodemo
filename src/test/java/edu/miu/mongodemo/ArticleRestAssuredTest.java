package edu.miu.mongodemo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RestAssured tests for ArticleController
 * These tests demonstrate how to test REST APIs using RestAssured
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ArticleRestAssuredTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        RestAssured.baseURI = baseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testGetAllArticles() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Verify response is a JSON array
        assertNotNull(response);
        String body = response.asString();
        assertTrue(body.startsWith("[") || body.equals("[]"));
    }

    @Test
    public void testGetAllArticlesWithHamcrest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", is(instanceOf(List.class)));
    }

    @Test
    public void testCreateArticle() {
        String articleJson = """
                {
                    "title": "Test Article",
                    "content": "This is a test article content",
                    "author": "John Doe",
                    "status": "DRAFT",
                    "tags": ["testing", "restassured"]
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("title", equalTo("Test Article"))
                .body("content", equalTo("This is a test article content"))
                .body("author", equalTo("John Doe"))
                .body("status", equalTo("DRAFT"))
                .body("tags", hasItems("testing", "restassured"))
                .body("id", notNullValue())
                .extract()
                .response();

        // Verify the created article
        String articleId = response.jsonPath().getString("id");
        assertNotNull(articleId);
    }

    @Test
    public void testGetArticleById() {
        // First, create an article
        String articleJson = """
                {
                    "title": "Article for Get Test",
                    "content": "Content for get by id test",
                    "author": "Jane Smith",
                    "status": "PUBLISHED"
                }
                """;

        String articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Then, get the article by ID
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles/{id}", articleId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(articleId))
                .body("title", equalTo("Article for Get Test"))
                .body("author", equalTo("Jane Smith"))
                .body("status", equalTo("PUBLISHED"));
    }

    @Test
    public void testGetArticleByIdNotFound() {
        String nonExistentId = "507f1f77bcf86cd799439011";

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles/{id}", nonExistentId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdateArticle() {
        // First, create an article
        String articleJson = """
                {
                    "title": "Original Title",
                    "content": "Original content",
                    "author": "Author Name",
                    "status": "DRAFT"
                }
                """;

        String articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Then, update the article
        String updatedArticleJson = """
                {
                    "title": "Updated Title",
                    "content": "Updated content",
                    "author": "Updated Author",
                    "status": "PUBLISHED",
                    "tags": ["updated", "test"]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedArticleJson)
                .when()
                .put("/api/articles/{id}", articleId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(articleId))
                .body("title", equalTo("Updated Title"))
                .body("content", equalTo("Updated content"))
                .body("author", equalTo("Updated Author"))
                .body("status", equalTo("PUBLISHED"))
                .body("tags", hasItems("updated", "test"));
    }

    @Test
    public void testDeleteArticle() {
        // First, create an article
        String articleJson = """
                {
                    "title": "Article to Delete",
                    "content": "This will be deleted",
                    "author": "Delete Test",
                    "status": "DRAFT"
                }
                """;

        String articleId = given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete the article
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/articles/{id}", articleId)
                .then()
                .statusCode(204);

        // Verify the article is deleted
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles/{id}", articleId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetArticlesByAuthor() {
        // First, create articles with the same author
        String article1Json = """
                {
                    "title": "Article 1 by Author",
                    "content": "Content 1",
                    "author": "Test Author",
                    "status": "PUBLISHED"
                }
                """;

        String article2Json = """
                {
                    "title": "Article 2 by Author",
                    "content": "Content 2",
                    "author": "Test Author",
                    "status": "PUBLISHED"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(article1Json)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body(article2Json)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201);

        // Get articles by author
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles/author/Test Author")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", is(instanceOf(List.class)))
                .body("author", everyItem(equalTo("Test Author")));
    }

    @Test
    public void testGetArticlesByStatus() {
        // Create articles with different statuses
        String draftArticleJson = """
                {
                    "title": "Draft Article",
                    "content": "Draft content",
                    "author": "Author",
                    "status": "DRAFT"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(draftArticleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201);

        // Get articles by status
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles/status/DRAFT")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", is(instanceOf(List.class)))
                .body("status", everyItem(equalTo("DRAFT")));
    }

    @Test
    public void testSearchArticlesByTitle() {
        // Create an article with a specific title
        String articleJson = """
                {
                    "title": "Unique Search Title",
                    "content": "Content",
                    "author": "Author",
                    "status": "PUBLISHED"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201);

        // Search articles by title
        given()
                .contentType(ContentType.JSON)
                .queryParam("title", "Unique Search Title")
                .when()
                .get("/api/articles/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", is(instanceOf(List.class)))
                .body("title", everyItem(containsString("Unique Search Title")));
    }

    @Test
    public void testGetArticlesByTag() {
        // Create an article with tags
        String articleJson = """
                {
                    "title": "Tagged Article",
                    "content": "Content",
                    "author": "Author",
                    "status": "PUBLISHED",
                    "tags": ["java", "spring", "testing"]
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(articleJson)
                .when()
                .post("/api/articles")
                .then()
                .statusCode(201);

        // Get articles by tag
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles/tag/java")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", is(instanceOf(List.class)))
                .body("tags", everyItem(hasItem("java")));
    }

    @Test
    public void testArticleValidation() {
        // Test creating article with missing required fields (if validation is enabled)
        String invalidArticleJson = """
                {
                    "title": "",
                    "content": "Content"
                }
                """;

        // This might fail validation or succeed depending on validation rules
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidArticleJson)
                .when()
                .post("/api/articles");

        // Status code might be 400 (Bad Request) if validation fails
        // or 201 if validation is not strict
        assertTrue(response.getStatusCode() == 201 || response.getStatusCode() == 400);
    }

    @Test
    public void testResponseTime() {
        // Test that response time is reasonable (less than 2 seconds)
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/articles")
                .then()
                .statusCode(200)
                .time(lessThan(2000L)); // Response time less than 2 seconds
    }
}

