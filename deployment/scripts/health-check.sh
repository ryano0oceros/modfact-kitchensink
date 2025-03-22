#!/bin/bash

# Exit on error
set -e

# Default values
HOST=${HOST:-"localhost"}
PORT=${PORT:-"8081"}
TIMEOUT=${TIMEOUT:-"30"}
INTERVAL=${INTERVAL:-"5"}

echo "Starting health check for $HOST:$PORT"
echo "Timeout: $TIMEOUT seconds"
echo "Check interval: $INTERVAL seconds"

# Function to check health
check_health() {
    curl -s -f "http://$HOST:$PORT/health" > /dev/null
    return $?
}

# Wait for the application to be ready
start_time=$(date +%s)
while true; do
    if check_health; then
        echo "Application is healthy!"
        exit 0
    fi
    
    current_time=$(date +%s)
    elapsed_time=$((current_time - start_time))
    
    if [ $elapsed_time -ge $TIMEOUT ]; then
        echo "Health check timed out after $TIMEOUT seconds"
        exit 1
    fi
    
    echo "Waiting for application to be healthy... ($elapsed_time seconds elapsed)"
    sleep $INTERVAL
done 