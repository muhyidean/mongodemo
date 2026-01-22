# GraphQL Quick Reference - Example Queries

## Quick Start

1. Start the application: `mvn spring-boot:run`
2. Open GraphiQL: `http://localhost:8080/graphiql`
3. Copy and paste the examples below

---

## Example Queries

### 1. Get All Articles (Simple)

```graphql
{
  articles {
    id
    title
    author
    viewCount
  }
}
```

### 2. Get Article by ID

```graphql
{
  article(id: "YOUR_ARTICLE_ID") {
    id
    title
    content
    author
    tags
    viewCount
    status
  }
}
```

### 3. Search Articles

```graphql
{
  searchArticles(title: "Spring") {
    id
    title
    author
  }
}
```

### 4. Get Articles by Status

```graphql
{
  articlesByStatus(status: PUBLISHED) {
    id
    title
    status
    publishedDate
  }
}
```

### 5. Get Articles with Person

```graphql
{
  articles {
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

### 6. Get All Persons

```graphql
{
  persons {
    id
    name
    job
  }
}
```

### 7. Get Comments for Content

```graphql
{
  commentsByContent(contentId: "YOUR_CONTENT_ID") {
    id
    author
    content
    likes
    isApproved
  }
}
```

---

## Example Mutations

### 1. Create Article

```graphql
mutation {
  createArticle(article: {
    title: "My New Article"
    content: "This is the article content..."
    author: "John Doe"
    tags: ["graphql", "spring", "mongodb"]
    status: PUBLISHED
  }) {
    id
    title
    author
    status
  }
}
```

### 2. Update Article

```graphql
mutation {
  updateArticle(
    id: "YOUR_ARTICLE_ID"
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

### 3. Delete Article

```graphql
mutation {
  deleteArticle(id: "YOUR_ARTICLE_ID")
}
```

### 4. Increment View Count

```graphql
mutation {
  incrementViewCount(id: "YOUR_ARTICLE_ID") {
    id
    viewCount
  }
}
```

### 5. Create Person

```graphql
mutation {
  createPerson(person: {
    name: "Jane Smith"
    job: "Software Engineer"
  }) {
    id
    name
    job
  }
}
```

### 6. Create Comment

```graphql
mutation {
  createComment(comment: {
    contentId: "YOUR_ARTICLE_ID"
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

### 7. Like Comment

```graphql
mutation {
  likeComment(id: "YOUR_COMMENT_ID") {
    id
    likes
  }
}
```

---

## Using Variables (Recommended)

### Query with Variables

```graphql
query GetArticle($id: ID!) {
  article(id: $id) {
    id
    title
    content
    author
  }
}
```

**Variables:**
```json
{
  "id": "YOUR_ARTICLE_ID"
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
    "content": "Article content here...",
    "author": "John Doe",
    "tags": ["tag1", "tag2"],
    "status": "PUBLISHED"
  }
}
```

---

## Testing with cURL

### Query Example

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ articles { id title author } }"
  }'
```

### Mutation Example

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createArticle(article: { title: \"Test\", content: \"Content\", author: \"Author\" }) { id title } }"
  }'
```

---

## Tips

1. **Use GraphiQL** for interactive testing: `http://localhost:8080/graphiql`
2. **Request only needed fields** to reduce data transfer
3. **Use variables** for production applications
4. **Check the schema** in GraphiQL's documentation explorer
5. **Handle errors** - GraphQL returns errors in a structured format

For detailed documentation, see `GRAPHQL_GUIDE.md`



