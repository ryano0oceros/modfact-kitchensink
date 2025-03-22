terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 1.0"
    }
  }
}

# AWS Provider configuration
provider "aws" {
  region = var.aws_region
}

# MongoDB Atlas Provider configuration
provider "mongodbatlas" {
  public_key  = var.mongodb_atlas_public_key
  private_key = var.mongodb_atlas_private_key
}

# VPC Module
module "networking" {
  source = "./modules/networking"
  
  environment     = var.environment
  vpc_cidr       = var.vpc_cidr
  azs            = var.availability_zones
}

# ECS Module
module "ecs" {
  source = "./modules/ecs"
  
  environment     = var.environment
  vpc_id         = module.networking.vpc_id
  private_subnets = module.networking.private_subnet_ids
  app_port       = var.app_port
}

# MongoDB Atlas Module
module "mongodb" {
  source = "./modules/mongodb"
  
  environment     = var.environment
  project_name   = var.project_name
  mongodb_version = var.mongodb_version
  instance_size  = var.mongodb_instance_size
} 