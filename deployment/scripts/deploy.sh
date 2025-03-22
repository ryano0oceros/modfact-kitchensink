#!/bin/bash

# Exit on error
set -e

# Load environment variables
if [ -f .env ]; then
    source .env
fi

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

# Build Docker image
echo "Building Docker image..."
docker build -t kitchensink-app:latest -f deployment/Dockerfile .

# Push to ECR (if AWS credentials are configured)
if [ -n "$AWS_ACCESS_KEY_ID" ]; then
    echo "Pushing to ECR..."
    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPOSITORY_URI}
    docker tag kitchensink-app:latest ${ECR_REPOSITORY_URI}:latest
    docker push ${ECR_REPOSITORY_URI}:latest
fi

echo "Deployment completed successfully" 