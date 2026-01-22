# Spring Boot Actuator Guide

## Table of Contents
1. [What is Spring Boot Actuator?](#what-is-spring-boot-actuator)
2. [Features and Benefits](#features-and-benefits)
3. [Getting Started](#getting-started)
4. [Available Endpoints](#available-endpoints)
5. [Health Endpoint](#health-endpoint)
6. [Metrics Endpoint](#metrics-endpoint)
7. [Info Endpoint](#info-endpoint)
8. [Environment Endpoint](#environment-endpoint)
9. [Loggers Endpoint](#loggers-endpoint)
10. [Configuration](#configuration)
11. [Security Considerations](#security-considerations)
12. [Custom Health Indicators](#custom-health-indicators)
13. [Best Practices](#best-practices)

---

## What is Spring Boot Actuator?

**Spring Boot Actuator** is a production-ready feature that provides monitoring, metrics, and management capabilities for Spring Boot applications. It exposes operational information about your running application through HTTP endpoints or JMX.

### Key Concepts:

1. **Production Monitoring**: Monitor application health, metrics, and performance
2. **Operational Insights**: Get detailed information about application internals
3. **Management Endpoints**: Expose various endpoints for different types of information
4. **Metrics Collection**: Collect and expose application metrics
5. **Health Checks**: Monitor application and dependency health

---

## Features and Benefits

### Benefits:

- **Health Monitoring**: Check if your application is running correctly
- **Metrics Collection**: Track application performance and usage
- **Environment Inspection**: View configuration properties
- **Logging Management**: Dynamically change log levels
- **Application Shutdown**: Gracefully shutdown the application (if enabled)
- **Production Ready**: Built-in support for production monitoring

---

## Getting Started

### 1. Dependency

The Spring Boot Actuator dependency has been added to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 2. Configuration

The actuator has been configured in `application.properties`:

```properties
# Base path for actuator endpoints
management.endpoints.web.base-path=/actuator

# Expose all endpoints via HTTP
management.endpoints.web.exposure.include=*

# Enable health endpoint details
management.endpoint.health.show-details=always

# Enable shutdown endpoint
management.endpoint.shutdown.enabled=true

# Enable info endpoint
management.info.env.enabled=true

# Enable Prometheus metrics
management.metrics.export.prometheus.enabled=true
```

### 3. Accessing Endpoints

Once your application is running, actuator endpoints are available at:

```
http://localhost:8080/actuator
```

---

## Available Endpoints

Spring Boot Actuator provides many built-in endpoints. Here are the most commonly used ones:

### Core Endpoints:

| Endpoint | Description | Default Enabled |
|----------|-------------|-----------------|
| `/actuator/health` | Application health information | ✅ |
| `/actuator/info` | Application information | ✅ |
| `/actuator/metrics` | Application metrics | ✅ |
| `/actuator/env` | Environment properties | ❌ |
| `/actuator/loggers` | View and configure loggers | ❌ |
| `/actuator/beans` | List all Spring beans | ❌ |
| `/actuator/configprops` | Configuration properties | ❌ |
| `/actuator/mappings` | URL mappings | ❌ |
| `/actuator/threaddump` | Thread dump | ❌ |
| `/actuator/heapdump` | Heap dump | ❌ |
| `/actuator/shutdown` | Graceful shutdown | ❌ |

### Discovering Endpoints

To see all available endpoints, visit:

```
GET http://localhost:8080/actuator
```

This will return a JSON response with links to all available endpoints.

**Example Response:**
```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/actuator",
      "templated": false
    },
    "health": {
      "href": "http://localhost:8080/actuator/health",
      "templated": false
    },
    "health-path": {
      "href": "http://localhost:8080/actuator/health/{*path}",
      "templated": true
    },
    "info": {
      "href": "http://localhost:8080/actuator/info",
      "templated": false
    },
    "metrics": {
      "href": "http://localhost:8080/actuator/metrics",
      "templated": false
    }
  }
}
```

---

## Health Endpoint

The health endpoint provides information about the application's health status.

### Basic Health Check

```
GET http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

### Detailed Health Check

With `management.endpoint.health.show-details=always`, you get more information:

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MongoDB",
        "validationQuery": "ismaster()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500107862016,
        "free": 250000000000,
        "threshold": 10485760,
        "exists": true
      }
    },
    "mongo": {
      "status": "UP",
      "details": {
        "version": "4.4.0"
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Health Status Values

- **UP**: Application is healthy
- **DOWN**: Application is unhealthy
- **OUT_OF_SERVICE**: Application is temporarily unavailable
- **UNKNOWN**: Health status cannot be determined

### Checking Specific Components

You can check the health of specific components:

```
GET http://localhost:8080/actuator/health/db
GET http://localhost:8080/actuator/health/mongo
GET http://localhost:8080/actuator/health/diskSpace
```

---

## Metrics Endpoint

The metrics endpoint provides access to application metrics.

### List All Metrics

```
GET http://localhost:8080/actuator/metrics
```

**Response:**
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "jvm.threads.live",
    "http.server.requests",
    "process.cpu.usage",
    "system.cpu.usage"
  ]
}
```

### Get Specific Metric

```
GET http://localhost:8080/actuator/metrics/jvm.memory.used
```

**Response:**
```json
{
  "name": "jvm.memory.used",
  "description": "The amount of used memory",
  "baseUnit": "bytes",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 123456789
    }
  ],
  "availableTags": [
    {
      "tag": "area",
      "values": ["heap", "nonheap"]
    }
  ]
}
```

### Get Metric with Tags

```
GET http://localhost:8080/actuator/metrics/jvm.memory.used?tag=area:heap
```

### Common Metrics

- **JVM Metrics**: Memory usage, thread count, GC statistics
- **HTTP Metrics**: Request count, response times, error rates
- **System Metrics**: CPU usage, disk usage
- **Custom Metrics**: Application-specific metrics

### HTTP Request Metrics

```
GET http://localhost:8080/actuator/metrics/http.server.requests
```

This shows metrics for all HTTP requests, including:
- Total requests
- Response times
- Status codes
- Request methods

---

## Info Endpoint

The info endpoint provides general information about the application.

### Basic Info

```
GET http://localhost:8080/actuator/info
```

**Default Response:**
```json
{}
```

### Adding Custom Info

You can add custom information in `application.properties`:

```properties
# Application Info
info.app.name=mongodemo
info.app.description=Demo project for Spring Boot
info.app.version=0.0.1-SNAPSHOT
info.app.encoding=@project.build.sourceEncoding@
info.app.java.version=@java.version@
```

**Response:**
```json
{
  "app": {
    "name": "mongodemo",
    "description": "Demo project for Spring Boot",
    "version": "0.0.1-SNAPSHOT",
    "encoding": "UTF-8",
    "java": {
      "version": "17.0.1"
    }
  }
}
```

### Git Information

You can also include Git information using the `git-commit-id-plugin`:

```xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
</plugin>
```

Then configure:
```properties
management.info.git.enabled=true
```

---

## Environment Endpoint

The environment endpoint shows all configuration properties.

### View Environment

```
GET http://localhost:8080/actuator/env
```

**Response:**
```json
{
  "activeProfiles": [],
  "propertySources": [
    {
      "name": "server.ports",
      "properties": {
        "local.server.port": {
          "value": 8080
        }
      }
    },
    {
      "name": "applicationConfig: [classpath:/application.properties]",
      "properties": {
        "spring.application.name": {
          "value": "mongodemo"
        },
        "server.port": {
          "value": 8080
        }
      }
    }
  ]
}
```

### View Specific Property

```
GET http://localhost:8080/actuator/env/spring.application.name
```

---

## Loggers Endpoint

The loggers endpoint allows you to view and modify log levels at runtime.

### List All Loggers

```
GET http://localhost:8080/actuator/loggers
```

**Response:**
```json
{
  "levels": ["OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"],
  "loggers": {
    "ROOT": {
      "configuredLevel": "ERROR",
      "effectiveLevel": "ERROR"
    },
    "edu.miu.mongodemo": {
      "configuredLevel": null,
      "effectiveLevel": "ERROR"
    }
  }
  }
}
```

### Get Specific Logger

```
GET http://localhost:8080/actuator/loggers/edu.miu.mongodemo
```

**Response:**
```json
{
  "configuredLevel": null,
  "effectiveLevel": "ERROR"
}
```

### Change Log Level

You can change log levels at runtime:

```
POST http://localhost:8080/actuator/loggers/edu.miu.mongodemo
Content-Type: application/json

{
  "configuredLevel": "DEBUG"
}
```

This is useful for debugging production issues without restarting the application.

---

## Configuration

### Endpoint Exposure

Control which endpoints are exposed:

```properties
# Expose all endpoints
management.endpoints.web.exposure.include=*

# Expose specific endpoints
management.endpoints.web.exposure.include=health,info,metrics

# Exclude specific endpoints
management.endpoints.web.exposure.exclude=env,beans
```

### Endpoint Base Path

Change the base path for actuator endpoints:

```properties
# Default is /actuator
management.endpoints.web.base-path=/actuator

# Custom path
management.endpoints.web.base-path=/management
```

### Health Endpoint Configuration

```properties
# Show health details (always, when-authorized, never)
management.endpoint.health.show-details=always

# Enable/disable specific health indicators
management.health.mongo.enabled=true
management.health.db.enabled=true
management.health.diskspace.enabled=true
```

### Metrics Configuration

```properties
# Enable Prometheus metrics
management.metrics.export.prometheus.enabled=true

# Metrics tags
management.metrics.tags.application=mongodemo
management.metrics.tags.environment=production
```

### Server Port for Actuator

You can run actuator on a different port:

```properties
# Management server on different port
management.server.port=8081
```

---

## Security Considerations

### ⚠️ Important Security Notes

1. **Production Environment**: Never expose sensitive endpoints in production without proper security
2. **Shutdown Endpoint**: The shutdown endpoint should be disabled or secured in production
3. **Sensitive Endpoints**: Endpoints like `/env`, `/configprops`, `/heapdump` expose sensitive information
4. **Authentication**: Consider adding Spring Security to protect actuator endpoints

### Securing Actuator Endpoints

Add Spring Security dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Configure security:

```java
@Configuration
public class ActuatorSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .httpBasic();
        return http.build();
    }
}
```

### Recommended Production Configuration

```properties
# Only expose safe endpoints
management.endpoints.web.exposure.include=health,info,metrics

# Don't show detailed health information
management.endpoint.health.show-details=never

# Disable shutdown endpoint
management.endpoint.shutdown.enabled=false

# Use different port for management
management.server.port=8081
```

---

## Custom Health Indicators

You can create custom health indicators to check the health of your application components.

### Example: Custom Database Health Indicator

```java
@Component
public class CustomMongoHealthIndicator implements HealthIndicator {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public Health health() {
        try {
            // Perform a simple operation to check MongoDB
            mongoTemplate.getCollectionNames();
            return Health.up()
                .withDetail("database", "MongoDB")
                .withDetail("status", "Connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "MongoDB")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### Example: Custom Service Health Indicator

```java
@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    public Health health() {
        try {
            // Check Kafka connectivity
            kafkaTemplate.send("health-check", "test");
            return Health.up()
                .withDetail("kafka", "Connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("kafka", "Disconnected")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

---

## Best Practices

### 1. Endpoint Exposure

- **Development**: Expose all endpoints for debugging
- **Production**: Only expose necessary endpoints (health, info, metrics)

### 2. Health Checks

- Use health checks for monitoring and alerting
- Integrate with load balancers and orchestration platforms
- Create custom health indicators for critical components

### 3. Metrics

- Use metrics for performance monitoring
- Export metrics to monitoring systems (Prometheus, InfluxDB, etc.)
- Set up alerts based on metric thresholds

### 4. Security

- Always secure sensitive endpoints
- Use authentication and authorization
- Consider using a separate management port

### 5. Logging

- Use the loggers endpoint for runtime log level changes
- Be careful when changing log levels in production
- Monitor log volume when enabling DEBUG/TRACE

### 6. Monitoring Integration

- Integrate with monitoring tools (Prometheus, Grafana, etc.)
- Set up dashboards for key metrics
- Configure alerts for critical issues

### 7. Production Checklist

- [ ] Secure actuator endpoints
- [ ] Disable sensitive endpoints
- [ ] Configure health endpoint appropriately
- [ ] Set up metrics export
- [ ] Test all endpoints
- [ ] Document endpoint usage
- [ ] Set up monitoring and alerting

---

## Testing Actuator Endpoints

### Using cURL

```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info
```

### Using Browser

Simply navigate to:
- `http://localhost:8080/actuator` - List all endpoints
- `http://localhost:8080/actuator/health` - Health status
- `http://localhost:8080/actuator/metrics` - Available metrics

### Using REST Client

You can use tools like Postman, Insomnia, or HTTPie to test endpoints.

---

## Troubleshooting

### Endpoints Not Available

1. Check if actuator dependency is in `pom.xml`
2. Verify endpoint exposure configuration
3. Check if endpoints are enabled
4. Verify the base path is correct

### Health Check Failing

1. Check component-specific health indicators
2. Review application logs
3. Verify database/connection health
4. Check disk space

### Metrics Not Showing

1. Verify metrics endpoint is exposed
2. Check if metrics are being collected
3. Review metrics configuration
4. Ensure application is generating metrics

---

## Additional Resources

- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring Boot Actuator API Reference](https://docs.spring.io/spring-boot/docs/current/actuator-api/html/)
- [Production-Ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)

---

## Summary

Spring Boot Actuator provides powerful monitoring and management capabilities for your application. By following this guide, you can:

- Monitor application health
- Collect and analyze metrics
- Manage application configuration
- Debug production issues
- Integrate with monitoring systems

Remember to always secure actuator endpoints in production environments!

