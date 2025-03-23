variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}

variable "project_name" {
  description = "Project name"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID for network peering"
  type        = string
}

variable "mongodb_atlas_org_id" {
  description = "MongoDB Atlas organization ID"
  type        = string
}

variable "mongodb_password" {
  description = "MongoDB Atlas admin password"
  type        = string
  sensitive   = true
}

variable "instance_size" {
  description = "MongoDB Atlas instance size"
  type        = string
}

variable "mongodb_version" {
  description = "MongoDB version"
  type        = string
  default     = "6.0"
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs for the VPC endpoint"
  type        = list(string)
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
}

variable "app_security_group_id" {
  description = "Security group ID of the ECS task"
  type        = string
} 