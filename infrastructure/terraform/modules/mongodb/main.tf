terraform {
  required_providers {
    mongodbatlas = {
      source = "mongodb/mongodbatlas"
    }
  }
}

# Add IP access list entry
resource "mongodbatlas_project_ip_access_list" "main" {
  project_id = mongodbatlas_project.main.id
  ip_address = "200.37.188.250"
  comment    = "Terraform deployment IP"
}

resource "mongodbatlas_project" "main" {
  name   = "${var.project_name}-${var.environment}"
  org_id = var.mongodb_atlas_org_id
}

resource "mongodbatlas_cluster" "main" {
  project_id   = mongodbatlas_project.main.id
  name         = "${var.project_name}-${var.environment}"
  provider_name = "AWS"
  provider_region_name = "US_WEST_2"
  provider_instance_size_name = "M10"
  mongo_db_major_version = var.mongodb_version

  tags {
    key   = "Environment"
    value = var.environment
  }
}

resource "mongodbatlas_database_user" "app" {
  username           = "${var.project_name}-${var.environment}"
  password           = var.mongodb_password
  project_id         = mongodbatlas_project.main.id
  auth_database_name = "admin"
  roles {
    role_name     = "readWrite"
    database_name = "kitchensink"
  }
}

# Create a private endpoint for MongoDB Atlas
resource "mongodbatlas_privatelink_endpoint" "main" {
  project_id    = mongodbatlas_project.main.id
  provider_name = "AWS"
  region        = "us-west-2"
}

# Create AWS VPC endpoint for the private endpoint
resource "aws_vpc_endpoint" "mongodb" {
  vpc_id             = var.vpc_id
  service_name       = mongodbatlas_privatelink_endpoint.main.endpoint_service_name
  vpc_endpoint_type  = "Interface"
  subnet_ids         = var.private_subnet_ids
  security_group_ids = [aws_security_group.mongodb.id]
}

# Create the private endpoint service
resource "mongodbatlas_privatelink_endpoint_service" "main" {
  project_id          = mongodbatlas_project.main.id
  private_link_id     = mongodbatlas_privatelink_endpoint.main.private_link_id
  endpoint_service_id = aws_vpc_endpoint.mongodb.id
  provider_name       = "AWS"
}

resource "aws_security_group" "mongodb" {
  name        = "${var.project_name}-${var.environment}-mongodb-sg"
  description = "Security group for MongoDB Atlas private endpoint"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-mongodb-sg"
    Environment = var.environment
  }
}

output "connection_string" {
  description = "MongoDB Atlas connection string"
  value       = try(mongodbatlas_cluster.main.connection_strings[0].private_endpoint[0].srv_connection_string, mongodbatlas_cluster.main.connection_strings[0].standard)
  sensitive   = true
}

output "database_name" {
  description = "MongoDB database name"
  value       = "kitchensink"
}

output "username" {
  description = "MongoDB username"
  value       = mongodbatlas_database_user.app.username
}