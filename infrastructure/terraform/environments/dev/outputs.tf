output "mongodb_connection_string" {
  description = "MongoDB Atlas connection string"
  value       = module.mongodb.connection_string
  sensitive   = true
}

output "mongodb_database_name" {
  description = "MongoDB database name"
  value       = module.mongodb.database_name
}

output "mongodb_username" {
  description = "MongoDB username"
  value       = module.mongodb.username
}

output "github_actions_role_arn" {
  description = "ARN of the GitHub Actions IAM role"
  value       = module.iam.github_actions_role_arn
} 