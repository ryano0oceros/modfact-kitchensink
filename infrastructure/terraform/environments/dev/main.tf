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

provider "aws" {
  region = var.aws_region
}

provider "mongodbatlas" {
  public_key  = var.mongodb_atlas_public_key
  private_key = var.mongodb_atlas_private_key
  region      = "US_WEST_2"
}

# Networking
module "networking" {
  source = "../../modules/networking"

  environment   = var.environment
  project_name  = var.project_name
  vpc_cidr      = var.vpc_cidr
  azs           = var.availability_zones
}

# MongoDB Atlas
module "mongodb" {
  source = "../../modules/mongodb"

  environment           = var.environment
  project_name         = var.project_name
  aws_region          = var.aws_region
  vpc_cidr            = var.vpc_cidr
  vpc_id              = module.networking.vpc_id
  mongodb_atlas_org_id = var.mongodb_atlas_org_id
  mongodb_password     = var.mongodb_password
  instance_size       = "M10"
  mongodb_version     = "6.0"
  private_subnet_ids  = module.networking.private_subnet_ids
  private_subnet_cidrs = module.networking.private_subnet_cidrs
}

module "ecr" {
  source = "../../modules/ecr"

  environment   = var.environment
  project_name  = var.project_name
}

module "alb" {
  source = "../../modules/alb"

  environment    = var.environment
  project_name   = var.project_name
  vpc_id         = module.networking.vpc_id
  public_subnets = module.networking.public_subnet_ids
}

# ECS
module "ecs" {
  source = "../../modules/ecs"

  project_name    = var.project_name
  environment     = var.environment
  aws_region      = var.aws_region

  vpc_id               = module.networking.vpc_id
  ecr_repository_url    = module.ecr.repository_url
  mongodb_uri          = module.mongodb.connection_string
  mongodb_password     = var.mongodb_password

  app_security_group_id = module.networking.app_security_group_id
  private_subnets      = module.networking.private_subnet_ids
  target_group_arn     = module.alb.target_group_arn

  depends_on = [module.ecr]
}

module "iam" {
  source = "../../modules/iam"

  aws_account_id = "083732173806"
  aws_region     = var.aws_region
  github_org     = "modfact"
  github_repo    = "modfact-kitchensink"
} 