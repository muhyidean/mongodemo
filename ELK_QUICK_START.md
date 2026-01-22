# ELK Stack Quick Start

## Quick Commands

### Start ELK Stack
```bash
docker-compose up -d elasticsearch logstash kibana filebeat
```

### Check Service Status
```bash
# Elasticsearch
curl http://localhost:9200/_cluster/health?pretty

# Logstash
curl http://localhost:9600/_node/stats?pretty

# Kibana (open in browser)
open http://localhost:5601
```

### View Logs
```bash
# All ELK services
docker-compose logs -f elasticsearch logstash kibana filebeat

# Individual services
docker-compose logs -f elasticsearch
docker-compose logs -f logstash
docker-compose logs -f filebeat
```

### Common Elasticsearch Queries

```bash
# List all indexes
curl http://localhost:9200/_cat/indices?v

# View index mapping
curl http://localhost:9200/mongodemo-logs-*/_mapping?pretty

# Count documents
curl http://localhost:9200/mongodemo-logs-*/_count?pretty

# Search for errors
curl -X GET "http://localhost:9200/mongodemo-logs-*/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "term": { "log_level": "ERROR" }
  },
  "size": 10
}
'

# Search for slow requests
curl -X GET "http://localhost:9200/mongodemo-logs-*/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "range": { "response_time_ms": { "gt": 1000 } }
  },
  "sort": [{ "response_time_ms": { "order": "desc" } }]
}
'
```

### Restart Services
```bash
# Restart all ELK services
docker-compose restart elasticsearch logstash kibana filebeat

# Restart specific service
docker-compose restart logstash
```

### Stop Services
```bash
docker-compose stop elasticsearch logstash kibana filebeat
```

### Clean Up (Remove all data)
```bash
# Stop and remove containers
docker-compose down

# Remove volumes (deletes all Elasticsearch data)
docker-compose down -v
```

## First Steps in Kibana

1. **Open Kibana**: http://localhost:5601

2. **Create Index Pattern**:
   - Go to Stack Management → Index Patterns
   - Create pattern: `mongodemo-logs-*`
   - Select time field: `@timestamp`
   - Click Create

3. **Explore Data**:
   - Go to Discover
   - Select index pattern: `mongodemo-logs-*`
   - View logs in real-time

4. **Create Visualizations**:
   - Go to Visualize Library
   - Create visualization
   - Select data source: `mongodemo-logs-*`
   - Choose visualization type

## Generate Test Logs

```bash
# Start the Spring Boot application
./mvnw spring-boot:run

# In another terminal, make API calls to generate logs
curl http://localhost:8080/api/articles
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","author":"John","content":"Content","status":"DRAFT"}'
curl http://localhost:8080/api/articles/search?title=Test
```

## Troubleshooting

### Logs not appearing?
1. Check if log file exists: `ls -la logs/mongodemo-application.log`
2. Check Filebeat: `docker logs filebeat`
3. Check Logstash: `docker logs logstash`
4. Check Elasticsearch: `curl http://localhost:9200/mongodemo-logs-*/_count`

### Service won't start?
1. Check Docker resources (memory, disk space)
2. Check ports are not in use: `lsof -i :9200 -i :5601 -i :5044`
3. Check logs: `docker-compose logs [service-name]`

### High memory usage?
Reduce Elasticsearch heap in `docker-compose.yml`:
```yaml
environment:
  - "ES_JAVA_OPTS=-Xms256m -Xmx256m"
```

