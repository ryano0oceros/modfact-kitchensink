# Kitchensink Application

A modernized Java application built with Quarkus, deployed on AWS ECS, and using MongoDB Atlas for data storage.

## Prerequisites

- Java 21
- Maven
- Docker
- MongoDB (local or Atlas)
- AWS CLI (for deployment)
- Terraform (for infrastructure)

## Local Development

1. Start MongoDB:
```bash
# Start MongoDB as replica set
mongod --replSet rs0 --dbpath ~/mongodb/data
mongosh --eval "rs.initiate()"
```

2. Run the application:
```bash
# Development mode
./mvnw clean quarkus:dev

# Or build and run
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

3. Seed the database:
```bash
# Using MongoDB script
./seed-db.sh

# Or using Quarkus command
./mvnw quarkus:dev -Dquarkus.args="seed"
```

## Testing

```bash
# Run tests
./mvnw test

# Run integration tests
./mvnw verify
```

## Deployment

1. Configure AWS credentials:
```bash
aws configure
```

2. Deploy infrastructure:
```bash
cd infrastructure/terraform
terraform init
terraform plan
terraform apply
```

3. Deploy application:
```bash
./deployment/scripts/deploy.sh
```

## Health Checks

```bash
# Check application health
./deployment/scripts/health-check.sh
```

## Documentation

- [Architecture](docs/architecture/README.md)
- [Deployment Guide](docs/deployment/README.md)
- [Operations Guide](docs/operations/README.md)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
