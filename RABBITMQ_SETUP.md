# RabbitMQ Setup for WebSocket Chat (Production)

## Why RabbitMQ for WebSocket Chat?

**RabbitMQ is the recommended choice** for WebSocket chat applications because:

1. ✅ **Native STOMP Support** - Spring WebSocket uses STOMP, and RabbitMQ has built-in STOMP support
2. ✅ **Low Latency** - Sub-millisecond message delivery (perfect for real-time chat)
3. ✅ **Horizontal Scaling** - Multiple Spring Boot instances can share the same broker
4. ✅ **Message Persistence** - Messages can survive server restarts
5. ✅ **Easy Spring Integration** - Simple configuration change

## Setup Steps

### 1. Install RabbitMQ

**macOS:**
```bash
brew install rabbitmq
brew services start rabbitmq
```

**Docker:**
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -p 61613:61613 \
  rabbitmq:3-management
```

**Linux:**
```bash
sudo apt-get install rabbitmq-server
sudo systemctl start rabbitmq-server
```

### 2. Enable STOMP Plugin

```bash
rabbitmq-plugins enable rabbitmq_stomp
rabbitmq-plugins enable rabbitmq_web_stomp  # Optional: Web STOMP
```

Verify:
```bash
rabbitmq-plugins list
```

### 3. Add Dependency to pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 4. Update WebSocketConfig

Replace `enableSimpleBroker()` with `enableStompBrokerRelay()`:

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableStompBrokerRelay("/topic", "/queue")
            .setRelayHost("localhost")
            .setRelayPort(61613)  // STOMP port
            .setClientLogin("guest")
            .setClientPasscode("guest");
    
    config.setApplicationDestinationPrefixes("/app");
}
```

### 5. Update application.properties

```properties
# RabbitMQ Configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### 6. Verify Connection

Check RabbitMQ Management UI: http://localhost:15672
- Username: `guest`
- Password: `guest`

## Architecture with RabbitMQ

```
┌─────────────┐         ┌─────────────┐
│  Client 1  │────────▶│  Spring     │
└─────────────┘         │  Boot App 1 │
                        └──────┬──────┘
┌─────────────┐                │
│  Client 2  │────────▶│  Spring     │      ┌──────────────┐
└─────────────┘         │  Boot App 2 │─────▶│  RabbitMQ    │
                        └──────┬──────┘      │  (STOMP)     │
┌─────────────┐                │              │              │
│  Client 3  │────────▶│  Spring     │      │  /topic/     │
└─────────────┘         │  Boot App 3 │      │  chat/       │
                        └─────────────┘      │  messages    │
                                             └──────────────┘
```

**Benefits:**
- Multiple Spring Boot instances can share the same broker
- Messages persist across server restarts
- Better for production scalability

## Kafka Alternative (Not Recommended for Chat)

Kafka could be used, but it's **not ideal** for WebSocket chat because:

❌ No native STOMP support
❌ Higher latency (10-100ms vs 1-10ms)
❌ More complex setup
❌ Designed for event streaming, not real-time messaging
❌ Requires custom adapters

**Use Kafka when:**
- You need event sourcing
- You need to replay message history
- You're doing stream processing/analytics
- You need very high throughput (millions/second)

## Hybrid Approach (Best of Both Worlds)

You could use **both**:

```
WebSocket Chat ──▶ RabbitMQ (Real-time messaging)
                      │
                      │ (Publish events)
                      ▼
                   Kafka (Event sourcing, analytics)
```

This gives you:
- Real-time chat via RabbitMQ
- Event history and analytics via Kafka

## Performance Comparison

| Metric | SimpleBroker | RabbitMQ | Kafka |
|--------|--------------|----------|-------|
| Latency | <1ms | 1-10ms | 10-100ms |
| Throughput | Limited | High | Very High |
| Scalability | Single server | Multi-server | Multi-server |
| Persistence | No | Yes | Yes |
| STOMP Support | Yes | Yes | No |

## Conclusion

**For WebSocket chat: Use RabbitMQ**

It's the perfect fit for real-time messaging with Spring WebSocket.
