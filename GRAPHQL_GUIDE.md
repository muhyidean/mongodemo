# GraphQL Guide for MongoDB Demo Application

## Table of Contents
1. [What is GraphQL?](#what-is-graphql)
2. [GraphQL vs REST API](#graphql-vs-rest-api)
3. [GraphQL Architecture in This Project](#graphql-architecture-in-this-project)
4. [Getting Started](#getting-started)
5. [GraphQL Queries](#graphql-queries)
6. [GraphQL Mutations](#graphql-mutations)
7. [GraphQL Schema](#graphql-schema)
8. [Testing GraphQL](#testing-graphql)
9. [Best Practices](#best-practices)

---

## What is GraphQL?

**GraphQL** is a query language for APIs and a runtime for executing those queries. It was developed by Facebook in 2012 and open-sourced in 2015.

### Key Concepts:

1. **Single Endpoint**: Unlike REST which has multiple endpoints, GraphQL uses a single endpoint (typically `/graphql`)
2. **Client-Driven Queries**: Clients specify exactly what data they need
3. **Strongly Typed**: GraphQL schemas define the structure of data
4. **Introspection**: GraphQL APIs are self-documenting

### Example Comparison:

**REST API:**
```
GET /api/articles          → Returns all articles with all fields
GET /api/articles/123      → Returns article 123 with all fields
GET /api/articles/123/comments → Returns comments for article 123
```

**GraphQL:**
```
POST /graphql
{
  article(id: "123") {
    id
    title
    author
    comments {
      content
      author
    }
  }
}
```

---

## GraphQL vs REST API

### Advantages of GraphQL:

1. **Over-fetching Prevention**: Get only the fields you need
   ```graphql
   # Only get title and author, not the full content
   {
     articles {
       title
       author
     }
   }
   ```

2. **Under-fetching Prevention**: Get related data in a single request
   ```graphql
   {
     article(id: "123") {
       title
       person {
         name
         job
       }
       comments {
         content
         author
       }
     }
   }
   ```

3. **Single Request**: Fetch multiple resources in one query
   ```graphql
   {
     articles {
       title
     }
     persons {
       name
     }
     comments {
       content
     }
   }
   ```

4. **Type Safety**: Schema ensures type correctness
5. **Self-Documenting**: Schema serves as API documentation

### When to Use GraphQL:

- ✅ Mobile applications (reduce data transfer)
- ✅ Complex data relationships
- ✅ Multiple client applications with different data needs
- ✅ Real-time applications (with subscriptions)

### When to Use REST:

- ✅ Simple CRUD operations
- ✅ Caching requirements (REST is more cache-friendly)
- ✅ File uploads/downloads
- ✅ Existing REST infrastructure

---

## GraphQL Architecture in This Project

### Project Structure:

```
src/main/
├── java/edu/miu/mongodemo/
│   ├── graphql/
│   │   ├── ArticleResolver.java    # Article queries & mutations
│   │   ├── PersonResolver.java     # Person queries & mutations
│   │   └── CommentResolver.java    # Comment queries & mutations
│   ├── model/                      # MongoDB entities
│   ├── repository/                 # MongoDB repositories
│   └── service/                    # Business logic
└── resources/
    ├── graphql/
    │   └── schema.graphqls         # GraphQL schema definition
    └── application.properties      # GraphQL configuration
```

### How It Works:

1. **Schema Definition** (`schema.graphqls`):
   - Defines types, queries, and mutations
   - Acts as a contract between client and server

2. **Resolvers** (`*Resolver.java`):
   - Implement the schema operations
   - Use `@QueryMapping` for queries
   - Use `@MutationMapping` for mutations
   - Delegate to service layer for business logic

3. **Spring GraphQL**:
   - Automatically maps schema to resolvers
   - Handles request parsing and response formatting
   - Provides GraphiQL UI for testing

---

## Getting Started

### 1. Start the Application

```bash
mvn spring-boot:run
```

### 2. Access GraphQL Endpoints

- **GraphQL Endpoint**: `http://localhost:8080/graphql`
- **GraphiQL UI** (Interactive Playground): `http://localhost:8080/graphiql`

### 3. GraphiQL Interface

GraphiQL provides:
- Interactive query editor
- Auto-completion
- Schema explorer
- Query history
- Response viewer

---

## GraphQL Queries

### Basic Query: Get All Articles

```graphql
query {
  articles {
    id
    title
    author
    viewCount
    status
  }
}
```

**Response:**
```json
{
  "data": {
    "articles": [
      {
        "id": "123",
        "title": "GraphQL Introduction",
        "author": "John Doe",
        "viewCount": 42,
        "status": "PUBLISHED"
      }
    ]
  }
}
```

### Query with Arguments: Get Article by ID

```graphql
query {
  article(id: "123") {
    id
    title
    content
    author
    publishedDate
    tags
    viewCount
    status
  }
}
```

### Query with Filtering: Get Articles by Author

```graphql
query {
  articlesByAuthor(author: "John Doe") {
    id
    title
    author
    viewCount
  }
}
```

### Query with Status Filter

```graphql
query {
  articlesByStatus(status: PUBLISHED) {
    id
    title
    status
    publishedDate
  }
}
```

### Search Query

```graphql
query {
  searchArticles(title: "Spring") {
    id
    title
    author
  }
}
```

### Query with Nested Data

```graphql
query {
  article(id: "123") {
    id
    title
    person {
      id
      name
      job
    }
  }
}
```

### Multiple Queries in One Request

```graphql
query {
  articles {
    id
    title
  }
  persons {
    id
    name
  }
  comments {
    id
    content
  }
}
```

### Query with Variables (Recommended for Production)

```graphql
query GetArticle($articleId: ID!) {
  article(id: $articleId) {
    id
    title
    content
  }
}
```

**Variables:**
```json
{
  "articleId": "123"
}
```

---

## GraphQL Mutations

### Create Article

```graphql
mutation {
  createArticle(article: {
    title: "GraphQL Best Practices"
    content: "Here are some best practices..."
    author: "Jane Smith"
    tags: ["graphql", "api", "best-practices"]
    status: PUBLISHED
  }) {
    id
    title
    author
    status
  }
}
```

### Update Article (Partial Update)

```graphql
mutation {
  updateArticle(
    id: "123"
    article: {
      title: "Updated Title"
      status: PUBLISHED
    }
  ) {
    id
    title
    status
  }
}
```

### Delete Article

```graphql
mutation {
  deleteArticle(id: "123")
}
```

**Response:**
```json
{
  "data": {
    "deleteArticle": true
  }
}
```

### Increment View Count

```graphql
mutation {
  incrementViewCount(id: "123") {
    id
    viewCount
  }
}
```

### Create Person

```graphql
mutation {
  createPerson(person: {
    name: "Alice Johnson"
    job: "GraphQL Developer"
  }) {
    id
    name
    job
  }
}
```

### Create Comment

```graphql
mutation {
  createComment(comment: {
    contentId: "123"
    contentType: ARTICLE
    author: "Bob Wilson"
    content: "Great article! Very informative."
  }) {
    id
    author
    content
    likes
  }
}
```

### Like Comment

```graphql
mutation {
  likeComment(id: "456") {
    id
    likes
  }
}
```

### Mutation with Variables

```graphql
mutation CreateArticle($article: ArticleInput!) {
  createArticle(article: $article) {
    id
    title
    author
  }
}
```

**Variables:**
```json
{
  "article": {
    "title": "New Article",
    "content": "Article content...",
    "author": "John Doe",
    "tags": ["tag1", "tag2"],
    "status": "PUBLISHED"
  }
}
```

---

## GraphQL Schema

### Type System

The schema defines the structure of data:

```graphql
type Article {
    id: ID!              # ! means required (non-nullable)
    title: String!
    content: String!
    author: String!
    publishedDate: String!
    tags: [String!]!     # Array of non-null strings
    viewCount: Int!
    status: ArticleStatus!
    person: Person       # Optional (nullable)
}
```

### Enums

```graphql
enum ArticleStatus {
    DRAFT
    PUBLISHED
    ARCHIVED
}
```

### Input Types

Used for mutations:

```graphql
input ArticleInput {
    title: String!
    content: String!
    author: String!
    tags: [String!]
    status: ArticleStatus
    personId: ID
}
```

### Scalar Types

- `ID`: Unique identifier (serialized as String)
- `String`: Text data
- `Int`: Integer number
- `Float`: Floating-point number
- `Boolean`: true/false

---

## Testing GraphQL

### Using GraphiQL (Browser UI)

1. Navigate to `http://localhost:8080/graphiql`
2. Write queries in the left panel
3. Click "Play" or press `Ctrl+Enter`
4. View results in the right panel

### Using cURL

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ articles { id title author } }"
  }'
```

### Using Postman

1. Method: `POST`
2. URL: `http://localhost:8080/graphql`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "query": "{ articles { id title author } }"
}
```

### Using JavaScript (Fetch API)

```javascript
fetch('http://localhost:8080/graphql', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    query: `
      {
        articles {
          id
          title
          author
        }
      }
    `
  })
})
.then(res => res.json())
.then(data => console.log(data));
```

### Using Apollo Client (React Example)

```javascript
import { ApolloClient, InMemoryCache, gql } from '@apollo/client';

const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',
  cache: new InMemoryCache()
});

const GET_ARTICLES = gql`
  query {
    articles {
      id
      title
      author
    }
  }
`;

client.query({ query: GET_ARTICLES })
  .then(result => console.log(result.data));
```

---

## Best Practices

### 1. Use Variables

❌ **Bad:**
```graphql
query {
  article(id: "123") { ... }
}
```

✅ **Good:**
```graphql
query GetArticle($id: ID!) {
  article(id: $id) { ... }
}
```

### 2. Request Only Needed Fields

❌ **Bad:**
```graphql
query {
  articles {
    id
    title
    content
    author
    publishedDate
    tags
    viewCount
    status
  }
}
```

✅ **Good:**
```graphql
query {
  articles {
    id
    title
    author
  }
}
```

### 3. Use Fragments for Reusability

```graphql
fragment ArticleFields on Article {
  id
  title
  author
  viewCount
}

query {
  articles {
    ...ArticleFields
  }
  article(id: "123") {
    ...ArticleFields
    content
  }
}
```

### 4. Handle Errors Gracefully

GraphQL returns errors in a structured format:

```json
{
  "data": null,
  "errors": [
    {
      "message": "Article not found with id: 999",
      "path": ["article"],
      "extensions": {
        "classification": "DataFetchingException"
      }
    }
  ]
}
```

### 5. Use Aliases for Multiple Queries

```graphql
query {
  published: articlesByStatus(status: PUBLISHED) {
    id
    title
  }
  draft: articlesByStatus(status: DRAFT) {
    id
    title
  }
}
```

### 6. Implement Pagination

For large datasets, consider implementing pagination:

```graphql
type ArticleConnection {
  edges: [ArticleEdge!]!
  pageInfo: PageInfo!
}

type ArticleEdge {
  node: Article!
  cursor: String!
}
```

### 7. Use Directives for Conditional Fields

```graphql
query ($includeContent: Boolean!) {
  article(id: "123") {
    id
    title
    content @include(if: $includeContent)
  }
}
```

---

## Common GraphQL Patterns

### 1. Field Resolvers

For complex field resolution, you can create field resolvers:

```java
@SchemaMapping(typeName = "Article", field = "person")
public Person person(Article article) {
    // Resolve person field when requested
    return article.getPerson();
}
```

### 2. Error Handling

```java
@QueryMapping
public Article article(@Argument String id) {
    return articleService.getArticleById(id)
        .orElseThrow(() -> new RuntimeException("Article not found"));
}
```

### 3. DataLoader Pattern (N+1 Problem)

For batch loading related data:

```java
@Bean
public DataLoader<String, Person> personDataLoader() {
    return DataLoader.newDataLoader(ids -> 
        Mono.just(personRepository.findAllById(ids))
    );
}
```

---

## Summary

GraphQL provides a powerful, flexible way to query and mutate data. Key benefits:

1. **Efficiency**: Get exactly what you need
2. **Flexibility**: Single endpoint, multiple operations
3. **Type Safety**: Strong typing prevents errors
4. **Developer Experience**: Self-documenting, easy to use

This implementation demonstrates:
- ✅ Type definitions and schema
- ✅ Query operations
- ✅ Mutation operations
- ✅ Integration with MongoDB
- ✅ Spring Boot integration

For more information, visit: https://graphql.org/



