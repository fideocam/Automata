output "ecr_repository_url" {
  description = "docker build -t APP . && docker tag APP:latest <this>/<image_tag>"
  value       = aws_ecr_repository.golden_crop.repository_url
}

output "app_runner_service_url" {
  description = "HTTPS base URL (empty if create_apprunner_service is false)"
  value       = try(aws_apprunner_service.golden_crop[0].service_url, null)
}

output "iam_apprunner_ecr_role_arn" {
  value = aws_iam_role.apprunner_ecr.arn
}

output "docker_login" {
  description = "Pipe to docker login"
  value       = "aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
}
