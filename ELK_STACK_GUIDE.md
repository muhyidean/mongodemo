# ELK Stack Integration Guide

This guide explains how to use the ELK (Elasticsearch, Logstash, Kibana) stack for centralized logging, monitoring, and analysis in the MongoDB Demo application.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Setup and Installation](#setup-and-installation)
4. [Configuration Details](#configuration-details)
5. [Elasticsearch Indexes](#elasticsearch-indexes)
6. [Real-Life Monitoring Examples](#real-life-monitoring-examples)
7. [Kibana Dashboards](#kibana-dashboards)
8. [Troubleshooting](#troubleshooting)

## Overview

The ELK stack provides:
- **Elasticsearch**: Distributed search and analytics engine for storing logs
- **Logstash**: Data processing pipeline that ingests, transforms, and sends logs to Elasticsearch
- **Kibana**: Visualization and exploration tool for Elasticsearch data
- **Filebeat**: Lightweight log shipper that sends logs to Logstash

## Architecture

```
Spring Boot App → Log Files → Filebeat → Logstash → Elasticsearch → Kibana
```

1. **Spring Boot Application** generates JSON logs to `logs/mongodemo-application.log`
2. **Filebeat** monitors log files and ships them to Logstash
3. **Logstash** processes, enriches, and parses logs before sending to Elasticsearch
4. **Elasticsearch** stores logs in time-based indexes
5. **Kibana** provides visualization and query interface

## Setup and Installation

### Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM (Elasticsearch requires memory)

### Starting the ELK Stack

1. **Start all services** (including ELK stack):
   ```bash
   docker-compose up -d
   ```

2. **Verify services are running**:
   ```bash
   docker-compose ps
   ```

3. **Check service health**:
   - Elasticsearch: http://localhost:9200
   - Kibana: http://localhost:5601
   - Logstash: http://localhost:9600/_node/stats

4. **Wait for services to be ready** (may take 1-2 minutes):
   ```bash
   # Check Elasticsearch
   curl http://localhost:9200/_cluster/health
   
   # Check Logstash
   curl http://localhost:9600/_node/stats
   ```

### Starting the Spring Boot Application

1. **Build the application**:
   ```bash
   ./mvnw clean package
   ```

2. **Run the application**:
   ```bash
   java -jar target/mongodemo-0.0.1-SNAPSHOT.jar
   ```

   Or use Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Generate some logs** by making API calls:
   ```bash
   # Get all articles
   curl http://localhost:8080/api/articles
   
   # Create an article
   curl -X POST http://localhost:8080/api/articles \
     -H "Content-Type: application/json" \
     -d '{"title":"Test Article","author":"John Doe","content":"Test content","status":"DRAFT"}'
   
   # Search articles
   curl "http://localhost:8080/api/articles/search?title=Test"
   ```

## Configuration Details

### Spring Boot Logging Configuration

The application uses **logback-spring.xml** for structured JSON logging:

- **Console Appender**: Outputs JSON logs to console
- **File Appender**: Writes JSON logs to `logs/mongodemo-application.log`
- **MDC (Mapped Diagnostic Context)**: Adds contextual information to logs

### Filebeat Configuration

Located at `filebeat/filebeat.yml`:
- Monitors `logs/mongodemo-application.log`
- Ships logs to Logstash on port 5044
- Handles multiline log entries

### Logstash Pipeline

Located at `logstash/pipeline/logstash.conf`:
- Receives logs from Filebeat
- Parses JSON logs
- Extracts HTTP request/response information
- Adds geoip information for IP addresses
- Sends processed logs to Elasticsearch

### Elasticsearch Index Template

Located at `elasticsearch/templates/mongodemo-logs-template.json`:
- Defines index pattern: `mongodemo-logs-*`
- Configures field mappings (keyword, text, date, etc.)
- Sets up index lifecycle management

## Elasticsearch Indexes

### Index Pattern

Logs are stored in daily indexes following the pattern:
```
mongodemo-logs-YYYY.MM.dd
```

Example: `mongodemo-logs-2024.01.15`

### Index Mappings

The index template defines the following fields:

| Field | Type | Description |
|-------|------|-------------|
| `@timestamp` | date | Log timestamp |
| `application` | keyword | Application name (mongodemo) |
| `environment` | keyword | Environment (development/production) |
| `log_level` | keyword | Log level (INFO, ERROR, WARN, DEBUG) |
| `logger_name` | keyword | Logger class name |
| `log_message` | text | Log message content |
| `http_method` | keyword | HTTP method (GET, POST, etc.) |
| `http_path` | keyword | Request path |
| `http_status` | integer | HTTP response status code |
| `http_remote_address` | ip | Client IP address |
| `response_time_ms` | long | Response time in milliseconds |
| `geoip` | object | Geographic information from IP |

### Viewing Indexes

```bash
# List all indexes
curl http://localhost:9200/_cat/indices?v

# View index mapping
curl http://localhost:9200/mongodemo-logs-*/_mapping?pretty

# Count documents in index
curl http://localhost:9200/mongodemo-logs-*/_count?pretty
```

## Real-Life Monitoring Examples

### 1. Setting Up Kibana Index Pattern

1. **Open Kibana**: http://localhost:5601
2. **Go to Stack Management** → **Index Patterns**
3. **Create Index Pattern**:
   - Pattern: `mongodemo-logs-*`
   - Time field: `@timestamp`
   - Click **Create index pattern**

### 2. Monitoring API Performance

#### Example Query: Find Slow API Requests

**KQL (Kibana Query Language)**:
```
log_level: INFO AND http_method: * AND response_time_ms: >1000
```

**Elasticsearch Query**:
```json
{
  "query": {
    "bool": {
      "must": [
        { "term": { "log_level": "INFO" } },
        { "exists": { "field": "http_method" } },
        { "range": { "response_time_ms": { "gt": 1000 } } }
      ]
    }
  },
  "sort": [{ "response_time_ms": { "order": "desc" } }]
}
```

**Visualization**:
- Create a **Vertical Bar Chart**
- X-axis: `http_path.keyword`
- Y-axis: Average of `response_time_ms`
- Filter: `response_time_ms > 1000`

#### Example Query: API Request Count by Endpoint

**KQL**:
```
http_method: * AND http_path: *
```

**Visualization**:
- Create a **Data Table**
- Rows: `http_path.keyword`
- Metric: Count
- Sort by: Count (descending)

### 3. Error Monitoring and Alerting

#### Example Query: Find All Errors

**KQL**:
```
log_level: ERROR
```

**Elasticsearch Query**:
```json
{
  "query": {
    "term": { "log_level": "ERROR" }
  },
  "sort": [{ "@timestamp": { "order": "desc" } }]
}
```

#### Example Query: Errors by Exception Type

**KQL**:
```
log_level: ERROR AND exception: *
```

**Visualization**:
- Create a **Pie Chart**
- Slice by: `exception.keyword`
- Size by: Count

#### Example Query: Error Rate Over Time

**Visualization**:
- Create a **Line Chart**
- X-axis: `@timestamp` (Date Histogram)
- Y-axis: Count
- Filter: `log_level: ERROR`

### 4. User Activity Monitoring

#### Example Query: Track Article Operations

**KQL**:
```
logger_name: ArticleController AND log_message: *
```

**Visualization**:
- Create a **Timeline**
- X-axis: `@timestamp`
- Y-axis: `log_message.keyword`
- Filter by: `logger_name: ArticleController`

#### Example Query: Most Active Authors

**KQL**:
```
http_path: "/api/articles" AND http_method: POST
```

**Elasticsearch Query**:
```json
{
  "query": {
    "bool": {
      "must": [
        { "term": { "http_path": "/api/articles" } },
        { "term": { "http_method": "POST" } }
      ]
    }
  },
  "aggs": {
    "authors": {
      "terms": {
        "field": "custom_article_author.keyword",
        "size": 10
      }
    }
  }
}
```

### 5. Geographic Analysis

#### Example Query: Requests by Country

**KQL**:
```
geoip.country_name: *
```

**Visualization**:
- Create a **Map** visualization
- Layer: Coordinate Map
- Geo coordinates: `geoip.location`
- Color by: Count

### 6. Search Query Analysis

#### Example Query: Popular Search Terms

**KQL**:
```
http_path: "/api/articles/search" AND http_method: GET
```

**Elasticsearch Query**:
```json
{
  "query": {
    "bool": {
      "must": [
        { "term": { "http_path": "/api/articles/search" } },
        { "term": { "http_method": "GET" } }
      ]
    }
  },
  "aggs": {
    "search_terms": {
      "terms": {
        "field": "custom_search_term.keyword",
        "size": 20
      }
    }
  }
}
```

### 7. Performance Trends

#### Example Query: Average Response Time Over Time

**Visualization**:
- Create a **Line Chart**
- X-axis: `@timestamp` (Date Histogram, 1 hour interval)
- Y-axis: Average of `response_time_ms`
- Split by: `http_path.keyword`

#### Example Query: Response Time Distribution

**Visualization**:
- Create a **Histogram**
- X-axis: `response_time_ms` (Histogram, 100ms intervals)
- Y-axis: Count

### 8. Security Monitoring

#### Example Query: Failed Authentication Attempts

**KQL**:
```
http_status: 401 OR http_status: 403
```

#### Example Query: Suspicious IP Addresses

**KQL**:
```
http_status: >=400 AND http_remote_address: *
```

**Visualization**:
- Create a **Data Table**
- Rows: `http_remote_address.keyword`
- Metric: Count
- Filter: `http_status >= 400`

### 9. Database Query Performance

#### Example Query: Slow Database Operations

**KQL**:
```
logger_name: *Repository AND response_time_ms: >500
```

#### Example Query: MongoDB Connection Issues

**KQL**:
```
log_message: *MongoException* OR log_message: *ConnectionException*
```

### 10. Real-Time Monitoring Dashboard

Create a comprehensive dashboard with:

1. **Error Rate Gauge**
   - Metric: Count of `log_level: ERROR`
   - Time range: Last 1 hour

2. **Request Rate**
   - Line chart: Count over time
   - Filter: `http_method: *`

3. **Top 5 Slow Endpoints**
   - Data table: `http_path.keyword`
   - Sort by: Average `response_time_ms`

4. **Error Log**
   - Data table: Recent errors
   - Columns: `@timestamp`, `log_message`, `exception`

5. **Geographic Distribution**
   - Map: Requests by country

## Kibana Dashboards

### Creating a Dashboard

1. **Go to Dashboard** → **Create Dashboard**
2. **Add Visualizations**:
   - Click **Add** → **Create new visualization**
   - Choose visualization type
   - Configure fields and filters
   - Save visualization
3. **Add to Dashboard**:
   - Click **Add** → **Add existing visualization**
   - Select saved visualizations
4. **Save Dashboard**

### Pre-built Dashboard Queries

#### API Performance Dashboard

**Visualizations**:
1. Request rate (line chart)
2. Average response time (line chart)
3. Top endpoints by request count (bar chart)
4. Slow requests table (data table)
5. Error rate (metric)

**Filters**:
- Time range: Last 24 hours
- Application: mongodemo

#### Error Analysis Dashboard

**Visualizations**:
1. Error count over time (line chart)
2. Errors by type (pie chart)
3. Errors by endpoint (bar chart)
4. Recent errors (data table)
5. Error rate trend (line chart)

**Filters**:
- `log_level: ERROR`

#### User Activity Dashboard

**Visualizations**:
1. Requests by hour (line chart)
2. Top users/authors (bar chart)
3. Request methods distribution (pie chart)
4. Geographic map (map)
5. Activity timeline (timeline)

## Advanced Queries

### Complex Aggregations

#### Example: Response Time Percentiles

```json
{
  "size": 0,
  "aggs": {
    "response_time_stats": {
      "percentiles": {
        "field": "response_time_ms",
        "percents": [50, 75, 90, 95, 99]
      }
    }
  }
}
```

#### Example: Error Rate by Hour

```json
{
  "size": 0,
  "query": {
    "range": {
      "@timestamp": {
        "gte": "now-24h"
      }
    }
  },
  "aggs": {
    "errors_by_hour": {
      "date_histogram": {
        "field": "@timestamp",
        "calendar_interval": "1h"
      },
      "aggs": {
        "error_count": {
          "filter": {
            "term": { "log_level": "ERROR" }
          }
        }
      }
    }
  }
}
```

### Machine Learning Anomaly Detection

Kibana includes machine learning capabilities for anomaly detection:

1. **Go to Machine Learning** → **Anomaly Detection**
2. **Create Job**:
   - Data source: `mongodemo-logs-*`
   - Analysis type: Population
   - Field: `response_time_ms`
3. **Detect anomalies** in response times

## Troubleshooting

### Common Issues

#### 1. Logs Not Appearing in Kibana

**Check Filebeat**:
```bash
docker logs filebeat
```

**Check Logstash**:
```bash
docker logs logstash
```

**Verify log file exists**:
```bash
ls -la logs/mongodemo-application.log
```

#### 2. Elasticsearch Connection Issues

**Check Elasticsearch health**:
```bash
curl http://localhost:9200/_cluster/health?pretty
```

**Check if index exists**:
```bash
curl http://localhost:9200/_cat/indices/mongodemo-logs-*?v
```

#### 3. High Memory Usage

**Reduce Elasticsearch heap size** in `docker-compose.yml`:
```yaml
environment:
  - "ES_JAVA_OPTS=-Xms256m -Xmx256m"
```

#### 4. Logstash Pipeline Errors

**Check Logstash logs**:
```bash
docker logs logstash | grep -i error
```

**Test Logstash configuration**:
```bash
docker exec logstash logstash --config.test_and_exit --path.settings=/usr/share/logstash/config
```

### Useful Commands

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f elasticsearch
docker-compose logs -f logstash
docker-compose logs -f filebeat
docker-compose logs -f kibana

# Restart a service
docker-compose restart logstash

# Check Elasticsearch indexes
curl http://localhost:9200/_cat/indices?v

# Delete an index (if needed)
curl -X DELETE http://localhost:9200/mongodemo-logs-2024.01.15

# Check Logstash pipeline status
curl http://localhost:9600/_node/pipelines?pretty
```

## Best Practices

1. **Index Lifecycle Management**: Set up policies to delete old logs (e.g., keep 30 days)
2. **Log Rotation**: Configure log rotation to prevent disk space issues
3. **Structured Logging**: Always use structured JSON logs with consistent fields
4. **MDC Usage**: Use MDC for adding contextual information (user ID, request ID, etc.)
5. **Error Handling**: Log exceptions with full stack traces
6. **Performance**: Monitor log volume and adjust Filebeat/Logstash accordingly
7. **Security**: In production, enable Elasticsearch security features

## Next Steps

1. **Set up alerts** in Kibana for critical errors
2. **Create custom dashboards** for your specific use cases
3. **Implement log retention policies** using Index Lifecycle Management
4. **Add more log sources** (database logs, system logs, etc.)
5. **Integrate with monitoring tools** (Prometheus, Grafana)

## Additional Resources

- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Logstash Documentation](https://www.elastic.co/guide/en/logstash/current/index.html)
- [Kibana Documentation](https://www.elastic.co/guide/en/kibana/current/index.html)
- [Filebeat Documentation](https://www.elastic.co/guide/en/beats/filebeat/current/index.html)

