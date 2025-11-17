variable "resource_group_name" {
  type        = string
  description = "Nombre del Resource Group para ILP."
}

variable "location" {
  type        = string
  description = "RegiÃ³n de Azure para los recursos."
  default     = "eastus"
}

variable "aks_cluster_name" {
  type        = string
  description = "Nombre del clÃºster AKS."
  default     = "ilp-aks-cluster"
}

variable "acr_name" {
  type        = string
  description = "Nombre del Azure Container Registry."
}

variable "postgres_server_name" {
  type        = string
  description = "Nombre del servidor PostgreSQL flexible."
  default     = "ilp-postgres-flex"
}

variable "postgres_admin_user" {
  type        = string
  description = "Usuario administrador de PostgreSQL."
}

variable "postgres_admin_password" {
  type        = string
  description = "Password administrador de PostgreSQL."
  sensitive   = true
}
