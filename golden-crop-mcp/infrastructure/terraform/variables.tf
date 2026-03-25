variable "aws_region" {
  type        = string
  description = "AWS region for ECR and App Runner"
  default     = "eu-north-1"
}

variable "name_prefix" {
  type        = string
  description = "Prefix for resource names"
  default     = "golden-crop"
}

variable "repository_name" {
  type        = string
  default     = "golden-crop-api"
}

variable "service_name" {
  type        = string
  default     = "golden-crop-api"
}

variable "image_tag" {
  type        = string
  description = "Container tag you pushed to ECR (e.g. latest)"
  default     = "latest"
}

variable "create_apprunner_service" {
  type        = bool
  description = "Set false for first apply (ECR + IAM only), push image, then true and apply again"
  default     = true
}

variable "cors_origins" {
  type        = string
  description = "Comma-separated origins or * ; empty disables CORS"
  default     = ""
}

variable "api_key" {
  type        = string
  description = "API key for HTTP auth (X-API-Key or Bearer). Comma-separated = multiple valid keys. Empty = no auth"
  default     = ""
  sensitive   = true
}

variable "cpu" {
  type        = string
  description = "App Runner CPU (e.g. 256 = 0.25 vCPU)"
  default     = "256"
}

variable "memory" {
  type        = string
  description = "App Runner memory in MB (e.g. 512)"
  default     = "512"
}
