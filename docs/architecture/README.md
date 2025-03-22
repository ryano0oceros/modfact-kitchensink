# Application Architecture

## Overview
The Kitchensink application is a modernized Java application built with Quarkus, deployed on AWS ECS, and using MongoDB Atlas for data storage.

## Components

### Application Layer
- Quarkus-based REST application
- Containerized using Docker
- Deployed on AWS ECS
- Health check endpoints for monitoring

### Data Layer
- MongoDB Atlas for data storage
- Replica set configuration for high availability
- Connection string managed via environment variables

### Infrastructure
- AWS VPC for network isolation
- ECS for container orchestration
- Security groups for access control
- IAM roles for service permissions

## Deployment Architecture
```
[Client] → [Application Load Balancer] → [ECS Service] → [MongoDB Atlas]
```

## Security
- Network isolation through VPC
- IAM roles for AWS services
- MongoDB Atlas network access controls
- Environment variable management for secrets

## Monitoring
- Application health checks
- AWS CloudWatch integration
- MongoDB Atlas monitoring
- Container metrics collection

## Scaling
- ECS service auto-scaling
- MongoDB Atlas auto-scaling
- Load balancer configuration 