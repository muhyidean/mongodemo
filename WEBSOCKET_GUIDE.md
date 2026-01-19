# WebSocket Implementation Guide

This guide explains the WebSocket implementation in the Reactive Content Platform, demonstrating real-time communication patterns using Spring WebSocket with STOMP protocol.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Key Components](#key-components)
4. [How WebSockets Work](#how-websockets-work)
5. [Usage Examples](#usage-examples)
6. [Testing](#testing)

## Overview

WebSockets provide **full-duplex, bidirectional communication** between client and server over a single TCP connection. Unlike HTTP (request-response), WebSockets allow the server to push data to clients without them requesting it.

### Why WebSockets?

- **Real-time Updates**: Instant notifications, live feeds, collaborative editing
- **Lower Latency**: No HTTP overhead, persistent connection
- **Efficient**: Single connection instead of multiple HTTP requests
- **Bidirectional**: Server can initiate communication

### When to Use WebSockets

✅ **Good for:**
- Real-time chat/commenting
- Live notifications
- Collaborative editing
- Live dashboards/analytics
- Stock tickers, sports scores
- Online gaming

❌ **Not ideal for:**
- Simple CRUD operations (use REST)
- One-time data fetching
- Static content delivery

## Architecture

```
Client                    Server
  |                         |
  |---- WebSocket Connect -->|
  |<--- Connection ACK ------|
  |                         |
  |---- Subscribe to Topic -->|
  |                         |
  |---- Send Message ------->|
  |      (/app/comment/add)  |
  |                         |
  |                         | Process & Save to DB
  |                         |
  |<--- Broadcast Message ---|
  |   (/topic/comments/123)  |
  |                         |
```

### Components

1. **WebSocket Endpoint** (`/ws`): Initial connection point
2. **STOMP Protocol**: Messaging protocol over WebSocket
3. **Message Broker**: Routes messages to subscribers
4. **Topics**: Pub/Sub destinations (`/topic/*`)
5. **Application Destinations**: Server endpoints (`/app/*`)

## Key Components

### 1. WebSocketConfig.java

**Purpose**: Configures WebSocket infrastructure

**Key Features**:
- Enables STOMP message broker
- Registers WebSocket endpoint (`/ws`)
- Configures message routing prefixes
- Enables SockJS fallback for compatibility

**Configuration**:
```java
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // Message broker: /topic, /queue
    // Application prefix: /app
    // Endpoint: /ws
}
```

### 2. WebSocketCommentController.java

**Purpose**: Handles real-time comment operations

**Endpoints**:
- `/app/comment/add`: Receive new comments
- `/app/comment/like`: Handle comment likes

**Topics**:
- `/topic/comments/{contentId}`: Broadcast comments for specific content

**Patterns Demonstrated**:
- `@MessageMapping`: Receive messages from clients
- `SimpMessagingTemplate`: Programmatic message sending
- Dynamic topic routing based on content ID

### 3. WebSocketArticleController.java

**Purpose**: Handles real-time article updates

**Endpoints**:
- `/app/article/publish`: Publish new articles
- `/app/article/status`: Update article status
- `/app/article/update`: Update article content

**Topics**:
- `/topic/articles/new`: New article notifications
- `/topic/articles/all`: All article updates
- `/topic/article/{id}`: Specific article updates
- `/topic/article/{id}/views`: View count updates

### 4. ArticleService.java (Integration Example)

**Purpose**: Demonstrates WebSocket integration in service layer

**Key Method**:
```java
public Optional<Article> incrementViewCount(String id) {
    // Update database
    // Broadcast via WebSocket
    webSocketController.broadcastViewCount(id, newCount);
}
```

## How WebSockets Work

### Connection Flow

1. **Client Connects**
   ```javascript
   const socket = new SockJS('/ws');
   const stompClient = Stomp.over(socket);
   stompClient.connect({}, onConnect);
   ```

2. **Subscribe to Topics**
   ```javascript
   stompClient.subscribe('/topic/comments/article123', function(message) {
       const comment = JSON.parse(message.body);
       // Display comment
   });
   ```

3. **Send Messages**
   ```javascript
   stompClient.send('/app/comment/add', {}, JSON.stringify(comment));
   ```

4. **Receive Broadcasts**
   - Server processes message
   - Saves to database
   - Broadcasts to topic
   - All subscribers receive it

### Message Flow Example: Adding a Comment

```
1. User types comment and clicks "Send"
   ↓
2. Client sends to: /app/comment/add
   {
     "contentId": "article123",
     "author": "John",
     "content": "Great article!"
   }
   ↓
3. Server receives in @MessageMapping("/comment/add")
   ↓
4. Server saves to MongoDB
   ↓
5. Server broadcasts to: /topic/comments/article123
   ↓
6. All clients subscribed to that topic receive the comment
   ↓
7. UI updates in real-time for all users viewing article123
```

## Usage Examples

### Client-Side (JavaScript)

#### Connect to WebSocket
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
});
```

#### Subscribe to Comments
```javascript
stompClient.subscribe('/topic/comments/article123', function(message) {
    const comment = JSON.parse(message.body);
    console.log('New comment:', comment);
    // Update UI
});
```

#### Send a Comment
```javascript
const comment = {
    contentId: 'article123',
    contentType: 'ARTICLE',
    author: 'John Doe',
    content: 'This is a great article!'
};

stompClient.send('/app/comment/add', {}, JSON.stringify(comment));
```

#### Publish an Article
```javascript
const article = {
    title: 'New Article',
    author: 'Jane Smith',
    content: 'Article content here...',
    tags: ['web', 'technology']
};

stompClient.send('/app/article/publish', {}, JSON.stringify(article));
```

### Server-Side (Java)

#### Receive and Broadcast
```java
@MessageMapping("/comment/add")
public void addComment(@Payload CommentDTO commentDTO) {
    // Save to database
    Comment saved = commentService.createComment(comment);
    
    // Broadcast to subscribers
    String topic = "/topic/comments/" + saved.getContentId();
    messagingTemplate.convertAndSend(topic, saved);
}
```

#### Using @SendTo (Automatic Broadcast)
```java
@MessageMapping("/article/publish")
@SendTo("/topic/articles/new")
public ArticleDTO publishArticle(@Payload ArticleDTO articleDTO) {
    Article saved = articleService.createArticle(articleDTO);
    return convertToDTO(saved);
    // Automatically broadcast to /topic/articles/new
}
```

## Testing

### Using the HTML Demo Client

1. **Start the Application**
   ```bash
   mvn spring-boot:run
   ```

2. **Open Browser**
   Navigate to: `http://localhost:8080/websocket-demo.html`

3. **Connect**
   - Click "Connect" button
   - Status should show "Connected"

4. **Test Comments**
   - Enter an Article ID (e.g., "article123")
   - Click "Subscribe to Comments"
   - Enter your name and comment
   - Click "Send Comment"
   - Comment appears in real-time

5. **Test Articles**
   - Click "Subscribe to New Articles"
   - Fill in article details
   - Click "Publish Article"
   - Article appears in feed instantly

6. **Test Multiple Clients**
   - Open multiple browser tabs/windows
   - Connect all of them
   - Subscribe to same topics
   - Send messages from one client
   - See them appear in all other clients

### Using cURL (for testing endpoints)

```bash
# Note: WebSocket requires a WebSocket client, not HTTP
# Use the HTML demo or a WebSocket testing tool
```

### Using Postman

Postman supports WebSocket testing:
1. Create new WebSocket request
2. Connect to: `ws://localhost:8080/ws`
3. Send STOMP frames manually

## Key Concepts Explained

### STOMP (Simple Text Oriented Messaging Protocol)

STOMP is a text-based protocol that provides:
- **Frame-based messaging**: Structured message format
- **Pub/Sub pattern**: Topics and queues
- **Message routing**: Like REST endpoints but for real-time
- **Acknowledgment**: Message delivery confirmation

### Topics vs Queues

- **Topics** (`/topic/*`): Pub/Sub - all subscribers receive message
- **Queues** (`/queue/*`): Point-to-point - only one subscriber receives

### SockJS

SockJS provides WebSocket emulation with fallbacks:
- Tries WebSocket first
- Falls back to HTTP streaming/polling if blocked
- Works through firewalls and proxies
- Transparent to application code

### Message Broker

- **In-memory** (used here): Simple, good for development
- **External** (RabbitMQ, ActiveMQ): Better for production, scalability

## Best Practices

1. **Error Handling**: Always handle connection errors
2. **Reconnection**: Implement automatic reconnection logic
3. **Security**: Use authentication/authorization
4. **Rate Limiting**: Prevent message flooding
5. **Scalability**: Use external message broker for production
6. **CORS**: Configure allowed origins properly
7. **Heartbeat**: Keep connections alive

## Production Considerations

1. **Use External Message Broker**: RabbitMQ or ActiveMQ
2. **Authentication**: Integrate with Spring Security
3. **CORS**: Configure specific origins, not "*"
4. **Monitoring**: Track connection counts, message rates
5. **Load Balancing**: Use sticky sessions or shared broker
6. **Error Handling**: Comprehensive error handling and logging

## Troubleshooting

### Connection Fails
- Check server is running
- Verify endpoint URL (`/ws`)
- Check CORS configuration
- Verify WebSocket support in browser

### Messages Not Received
- Verify subscription topic matches broadcast topic
- Check message format (JSON)
- Verify connection is active
- Check server logs for errors

### Multiple Instances
- Use external message broker
- Configure load balancer with sticky sessions
- Or use Redis for shared state

## Additional Resources

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol Specification](https://stomp.github.io/)
- [SockJS Documentation](https://github.com/sockjs/sockjs-client)

