# Prometheus & Grafana Monitoring Guide

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Prerequisites](#prerequisites)
4. [Setup Instructions](#setup-instructions)
5. [Configuration Details](#configuration-details)
6. [Accessing the Services](#accessing-the-services)
7. [Using Grafana Dashboards](#using-grafana-dashboards)
8. [Available Metrics](#available-metrics)
9. [Creating Custom Dashboards](#creating-custom-dashboards)
10. [Troubleshooting](#troubleshooting)
11. [Best Practices](#best-practices)

---

## Overview

This guide explains how to set up **Prometheus** and **Grafana** to monitor your Spring Boot application. Prometheus collects metrics from your application's Actuator endpoints, and Grafana provides beautiful visualizations and dashboards for analytics.

### What You'll Get:

- **Real-time Metrics**: Monitor your application's performance in real-time
- **Beautiful Dashboards**: Visualize metrics with Grafana's powerful dashboards
- **Historical Data**: Track metrics over time to identify trends
- **Alerting**: Set up alerts for critical metrics (optional)
- **JVM Metrics**: Monitor memory, threads, and garbage collection
- **HTTP Metrics**: Track request rates, response times, and status codes
- **System Metrics**: Monitor CPU, disk, and network usage

---

## Architecture

```
┌─────────────────┐
│  Spring Boot    │
│   Application   │───┐
│   (Port 8080)   │   │
└─────────────────┘   │
                          │  Scrapes metrics
┌─────────────────┐       │  every 10 seconds
│   Prometheus    │◄──────┘
│   (Port 9090)   │
└─────────────────┘
         │
         │ Queries metrics
         │
┌─────────────────┐
│    Grafana      │
│   (Port 3000)   │
└─────────────────┘
```

### Flow:
1. **Spring Boot Application** exposes metrics at `/actuator/prometheus`
2. **Prometheus** scrapes metrics from the application every 10 seconds
3. **Grafana** queries Prometheus to display metrics in dashboards

---

## Prerequisites

- Docker and Docker Compose installed
- Spring Boot application running (or will be started)
- Ports available: 8080, 9090, 3000

---

## Setup Instructions

### Step 1: Verify Dependencies

The following dependencies have been added to your `pom.xml`:

```xml
<!-- Spring Boot Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Prometheus -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Step 2: Verify Application Configuration

Your `application.properties` should have:

```properties
# Prometheus Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}
management.metrics.enable.jvm=true
management.metrics.enable.system=true
management.metrics.enable.http=true
```

### Step 3: Start Your Spring Boot Application

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

Or if you have the JAR file:

```bash
java -jar target/mongodemo-0.0.1-SNAPSHOT.jar
```

**Verify the Prometheus endpoint is working:**
```bash
curl http://localhost:8080/actuator/prometheus
```

You should see Prometheus-formatted metrics.

### Step 4: Start Prometheus and Grafana

```bash
# Start Prometheus and Grafana using Docker Compose
docker-compose up -d prometheus grafana
```

Or start all services (including Kafka, Zookeeper):

```bash
docker-compose up -d
```

### Step 5: Verify Services are Running

```bash
# Check running containers
docker-compose ps

# Check Prometheus logs
docker-compose logs prometheus

# Check Grafana logs
docker-compose logs grafana
```

---

## Configuration Details

### Prometheus Configuration (`prometheus/prometheus.yml`)

```yaml
global:
  scrape_interval: 15s        # How often to scrape targets
  evaluation_interval: 15s    # How often to evaluate rules

scrape_configs:
  - job_name: 'mongodemo-application'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
    static_configs:
      - targets: ['host.docker.internal:8080']
        labels:
          application: 'mongodemo'
          service: 'spring-boot-app'
```

**Key Points:**
- `host.docker.internal:8080` allows Docker containers to access services on the host machine
- If running on Linux, you may need to use `172.17.0.1:8080` or your host IP
- The scrape interval is set to 10 seconds for more frequent updates

### Grafana Datasource (`grafana/provisioning/datasources/prometheus.yml`)

This automatically configures Prometheus as a datasource in Grafana:

```yaml
datasources:
  - name: Prometheus
    type: prometheus
    url: http://prometheus:9090
    isDefault: true
```

### Grafana Dashboard Provisioning (`grafana/provisioning/dashboards/dashboard.yml`)

This automatically loads dashboards from the `grafana/dashboards` directory.

---

## Accessing the Services

### 1. Spring Boot Application

- **URL**: http://localhost:8080
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus
- **Health Check**: http://localhost:8080/actuator/health

### 2. Prometheus

- **URL**: http://localhost:9090
- **Targets**: http://localhost:9090/targets (check if scraping is working)
- **Graph**: http://localhost:9090/graph (query metrics)

**Test a Query in Prometheus:**
```
http_server_requests_seconds_count{application="mongodemo"}
```

### 3. Grafana

- **URL**: http://localhost:3000
- **Username**: `admin`
- **Password**: `admin` (change on first login)

**First Login:**
1. Navigate to http://localhost:3000
2. Login with `admin`/`admin`
3. You'll be prompted to change the password (optional)

---

## Using Grafana Dashboards

### Accessing the Pre-configured Dashboard

1. **Login to Grafana** at http://localhost:3000
2. **Navigate to Dashboards** (click the menu icon → Dashboards)
3. **Find "Spring Boot Application Metrics"** dashboard
4. **Click to open** and view your metrics

### Dashboard Panels

The pre-configured dashboard includes:

1. **HTTP Request Rate**: Requests per second by endpoint
2. **HTTP Response Time (p95)**: 95th percentile response time
3. **JVM Memory Usage**: Heap and non-heap memory
4. **JVM Threads**: Live and daemon threads
5. **HTTP Status Codes**: 2xx, 4xx, 5xx response rates
6. **CPU Usage**: Process and system CPU usage

### Refreshing Data

- **Auto-refresh**: Set to 10 seconds by default
- **Manual refresh**: Click the refresh icon in the top right
- **Time range**: Use the time picker to view different time ranges

---

## Available Metrics

### HTTP Metrics

| Metric | Description |
|--------|-------------|
| `http_server_requests_seconds_count` | Total number of HTTP requests |
| `http_server_requests_seconds_sum` | Total time spent processing requests |
| `http_server_requests_seconds_max` | Maximum request duration |
| `http_server_requests_seconds_bucket` | Request duration histogram |

**Labels:**
- `method`: HTTP method (GET, POST, etc.)
- `uri`: Request URI
- `status`: HTTP status code
- `application`: Application name

### JVM Metrics

| Metric | Description |
|--------|-------------|
| `jvm_memory_used_bytes` | Memory used in bytes |
| `jvm_memory_max_bytes` | Maximum memory available |
| `jvm_threads_live_threads` | Number of live threads |
| `jvm_threads_daemon_threads` | Number of daemon threads |
| `jvm_gc_pause_seconds` | GC pause duration |

**Labels:**
- `area`: Memory area (heap, nonheap)
- `id`: Memory pool ID
- `application`: Application name

### System Metrics

| Metric | Description |
|--------|-------------|
| `process_cpu_usage` | Process CPU usage (0-1) |
| `system_cpu_usage` | System CPU usage (0-1) |
| `process_uptime_seconds` | Application uptime |
| `system_load_average_1m` | System load average |

### Custom Metrics

You can add custom metrics in your application:

```java
@Service
public class ArticleService {
    
    private final Counter articleCounter;
    private final Timer articleTimer;
    
    public ArticleService(MeterRegistry meterRegistry) {
        this.articleCounter = Counter.builder("articles.created")
            .description("Number of articles created")
            .tag("application", "mongodemo")
            .register(meterRegistry);
            
        this.articleTimer = Timer.builder("articles.processing.time")
            .description("Time taken to process articles")
            .register(meterRegistry);
    }
    
    public Article createArticle(Article article) {
        articleCounter.increment();
        return Timer.Sample.start(articleTimer)
            .stop(() -> {
                // Your article creation logic
                return articleRepository.save(article);
            });
    }
}
```

---

## Creating Custom Dashboards

### Method 1: Using Grafana UI

1. **Login to Grafana**
2. **Click "+" → "Create Dashboard"**
3. **Click "Add visualization"**
4. **Select Prometheus datasource**
5. **Enter a PromQL query**, for example:
   ```
   rate(http_server_requests_seconds_count{application="mongodemo"}[5m])
   ```
6. **Configure visualization** (graph, table, stat, etc.)
7. **Save the dashboard**

### Method 2: Import JSON Dashboard

1. **Click "+" → "Import"**
2. **Upload a JSON file** or paste JSON
3. **Select Prometheus datasource**
4. **Click "Import"**

### Useful PromQL Queries

**Request Rate:**
```promql
rate(http_server_requests_seconds_count{application="mongodemo"}[5m])
```

**Average Response Time:**
```promql
rate(http_server_requests_seconds_sum{application="mongodemo"}[5m]) 
/ 
rate(http_server_requests_seconds_count{application="mongodemo"}[5m])
```

**95th Percentile Response Time:**
```promql
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket{application="mongodemo"}[5m])
)
```

**Error Rate:**
```promql
rate(http_server_requests_seconds_count{application="mongodemo", status=~"5.."}[5m])
```

**Memory Usage Percentage:**
```promql
(jvm_memory_used_bytes{application="mongodemo", area="heap"} 
/ 
jvm_memory_max_bytes{application="mongodemo", area="heap"}) * 100
```

**Thread Count:**
```promql
jvm_threads_live_threads{application="mongodemo"}
```

---

## Troubleshooting

### Prometheus Can't Scrape Metrics

**Problem**: Prometheus shows target as "DOWN"

**Solutions:**
1. **Verify application is running:**
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

2. **Check Prometheus configuration:**
   - Verify `host.docker.internal:8080` is correct
   - On Linux, try `172.17.0.1:8080` or your host IP
   - Check if firewall is blocking connections

3. **Check Prometheus targets:**
   - Go to http://localhost:9090/targets
   - Check the error message

4. **Verify network:**
   ```bash
   # From Prometheus container
   docker-compose exec prometheus wget -O- http://host.docker.internal:8080/actuator/prometheus
   ```

### Grafana Can't Connect to Prometheus

**Problem**: Grafana shows "Data source is not working"

**Solutions:**
1. **Verify Prometheus is running:**
   ```bash
   docker-compose ps prometheus
   curl http://localhost:9090
   ```

2. **Check Grafana datasource configuration:**
   - Go to Configuration → Data Sources
   - Verify URL is `http://prometheus:9090`
   - Click "Save & Test"

3. **Check network connectivity:**
   ```bash
   docker-compose exec grafana ping prometheus
   ```

### No Metrics Showing in Grafana

**Solutions:**
1. **Verify metrics are being collected:**
   - Check Prometheus at http://localhost:9090/graph
   - Query: `http_server_requests_seconds_count{application="mongodemo"}`

2. **Check time range:**
   - Ensure you're viewing the correct time range
   - Metrics might not exist for the selected time range

3. **Verify application label:**
   - Ensure queries use the correct label: `application="mongodemo"`

### Application Not Exposing Metrics

**Solutions:**
1. **Verify dependencies:**
   ```bash
   mvn dependency:tree | grep prometheus
   ```

2. **Check application.properties:**
   - Ensure `management.endpoints.web.exposure.include` includes `prometheus`
   - Ensure `management.metrics.export.prometheus.enabled=true`

3. **Check application logs:**
   - Look for any errors related to Actuator or Prometheus

### Docker Network Issues (Linux)

If `host.docker.internal` doesn't work on Linux:

1. **Use host network mode** (in docker-compose.yml):
   ```yaml
   prometheus:
     network_mode: "host"
   ```

2. **Or use your host IP:**
   ```yaml
   static_configs:
     - targets: ['192.168.1.100:8080']  # Your host IP
   ```

3. **Or add extra_hosts:**
   ```yaml
   prometheus:
     extra_hosts:
       - "host.docker.internal:host-gateway"
   ```

---

## Best Practices

### 1. Security

**Production Recommendations:**
- Change Grafana default password
- Secure Prometheus and Grafana with authentication
- Use HTTPS for all services
- Restrict access to actuator endpoints
- Use network policies to limit access

### 2. Resource Management

- **Retention**: Configure Prometheus data retention
  ```yaml
  # In prometheus.yml or docker-compose
  --storage.tsdb.retention.time=30d
  ```

- **Scrape Intervals**: Balance between detail and resource usage
  - Too frequent: High resource usage
  - Too infrequent: Missing important events

### 3. Monitoring Strategy

- **Key Metrics to Monitor:**
  - Request rate and latency
  - Error rates (4xx, 5xx)
  - JVM memory usage
  - Thread count
  - CPU usage

- **Alerting**: Set up alerts for:
  - High error rates (> 1%)
  - High response times (p95 > 1s)
  - Memory usage > 80%
  - Application down

### 4. Dashboard Organization

- Create separate dashboards for:
  - Application metrics
  - Infrastructure metrics
  - Business metrics
- Use folders to organize dashboards
- Add descriptions and tags

### 5. Performance

- **Query Optimization**: Use recording rules for complex queries
- **Dashboard Performance**: Limit number of panels and queries
- **Data Retention**: Balance retention period with storage

---

## Quick Reference

### Useful URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Spring Boot App | http://localhost:8080 | - |
| Prometheus Metrics | http://localhost:8080/actuator/prometheus | - |
| Prometheus UI | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin/admin |

### Common Commands

```bash
# Start all services
docker-compose up -d

# Start only monitoring services
docker-compose up -d prometheus grafana

# View logs
docker-compose logs -f prometheus
docker-compose logs -f grafana

# Stop services
docker-compose down

# Restart a service
docker-compose restart prometheus

# Check service status
docker-compose ps
```

### Prometheus Queries

```promql
# Total requests
sum(rate(http_server_requests_seconds_count{application="mongodemo"}[5m]))

# Error rate
sum(rate(http_server_requests_seconds_count{application="mongodemo", status=~"5.."}[5m]))

# Memory usage
jvm_memory_used_bytes{application="mongodemo", area="heap"}

# CPU usage
process_cpu_usage{application="mongodemo"} * 100
```

---

## Next Steps

1. **Customize Dashboards**: Create dashboards specific to your application
2. **Add Alerts**: Set up alerting rules in Prometheus
3. **Add Custom Metrics**: Instrument your application with custom metrics
4. **Set Up Retention**: Configure data retention policies
5. **Secure Access**: Add authentication and authorization
6. **Monitor Multiple Services**: Add more services to Prometheus

---

## Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [PromQL Query Language](https://prometheus.io/docs/prometheus/latest/querying/basics/)

---

## Summary

You now have a complete monitoring setup with:
- ✅ Prometheus collecting metrics from your Spring Boot application
- ✅ Grafana visualizing metrics with beautiful dashboards
- ✅ Pre-configured dashboard for common metrics
- ✅ Automatic datasource and dashboard provisioning

**Next Steps:**
1. Start your Spring Boot application
2. Start Prometheus and Grafana: `docker-compose up -d prometheus grafana`
3. Access Grafana at http://localhost:3000
4. Explore the pre-configured dashboard
5. Create custom dashboards for your specific needs

Happy Monitoring! 📊

