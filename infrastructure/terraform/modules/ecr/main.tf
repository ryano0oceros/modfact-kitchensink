resource "aws_ecr_repository" "app" {
  name = "${var.environment}-${var.project_name}"
  
  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name        = "${var.environment}-${var.project_name}"
    Environment = var.environment
  }
}

output "repository_url" {
  description = "URL of the ECR repository"
  value       = aws_ecr_repository.app.repository_url
} 