#!/bin/bash

# Exit on error
set -e

# Check if AWS credentials are configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "AWS credentials not configured. Please run 'aws configure' first."
    exit 1
fi

# Check if MongoDB Atlas credentials are set
if [ -z "$MONGODB_ATLAS_PUBLIC_KEY" ] || [ -z "$MONGODB_ATLAS_PRIVATE_KEY" ]; then
    echo "MongoDB Atlas credentials not set. Please set MONGODB_ATLAS_PUBLIC_KEY and MONGODB_ATLAS_PRIVATE_KEY environment variables."
    exit 1
fi

# Initialize Terraform
echo "Initializing Terraform..."
terraform init

# Create terraform.tfvars with sensitive data
cat > terraform.tfvars << EOF
mongodb_atlas_public_key = "$MONGODB_ATLAS_PUBLIC_KEY"
mongodb_atlas_private_key = "$MONGODB_ATLAS_PRIVATE_KEY"
mongodb_password = "$MONGODB_PASSWORD"
ecr_repository_url = "$ECR_REPOSITORY_URL"
target_group_arn = "$TARGET_GROUP_ARN"
EOF

# Plan the changes
echo "Planning changes..."
terraform plan -out=tfplan

# Apply the changes
echo "Applying changes..."
terraform apply tfplan

echo "Deployment completed successfully!" 