#!/bin/bash

# Script to manually create Elasticsearch index template
# This can be used if Logstash doesn't automatically create the template

ELASTICSEARCH_URL="http://localhost:9200"
TEMPLATE_FILE="../elasticsearch/templates/mongodemo-logs-template.json"

echo "Creating Elasticsearch index template..."

# Check if Elasticsearch is running
if ! curl -s "$ELASTICSEARCH_URL/_cluster/health" > /dev/null; then
    echo "Error: Elasticsearch is not running at $ELASTICSEARCH_URL"
    exit 1
fi

# Create the template
curl -X PUT "$ELASTICSEARCH_URL/_index_template/mongodemo-logs-template" \
  -H 'Content-Type: application/json' \
  -d @$TEMPLATE_FILE

echo ""
echo "Template creation complete!"
echo "Verify with: curl $ELASTICSEARCH_URL/_index_template/mongodemo-logs-template?pretty"

