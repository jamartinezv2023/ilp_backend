##############################################################
#  STORAGE AUDIT BACKEND (Azure Blob + AWS S3)
#  --------------------------------------------
#  Crea almacenamiento redundante y versionado
#  para los reportes audit_report.html generados
#  por tus pipelines CI/CD (Azure y GitHub Actions)
##############################################################

terraform {
  required_version = ">= 1.9.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.110"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.62"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

##############################
#   AZURE BLOB STORAGE
##############################
provider "azurerm" {
  features {}
}

# 1. Grupo de recursos
resource "azurerm_resource_group" "audit_rg" {
  name     = "inclusive-audit-rg"
  location = "East US"

  tags = {
    environment = "ci-cd"
    project     = "inclusive-learning"
  }
}

# 2. Cuenta de almacenamiento
resource "azurerm_storage_account" "audit_storage" {
  name                     = "inclusiveaudit${random_string.suffix.result}"
  resource_group_name      = azurerm_resource_group.audit_rg.name
  location                 = azurerm_resource_group.audit_rg.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  min_tls_version          = "TLS1_2"

  network_rules {
    default_action             = "Deny"
    bypass                     = ["AzureServices"]
    ip_rules                   = []
    virtual_network_subnet_ids = []
  }

  blob_properties {
    container_delete_retention_policy {
      days = 7
    }
    delete_retention_policy {
      days = 7
    }
    versioning_enabled  = true
    change_feed_enabled = true
  }

  tags = {
    environment = "ci-cd"
    project     = "inclusive-learning"
  }
}

# 3. Contenedor privado para auditorías
resource "azurerm_storage_container" "audit_container" {
  name                  = "ci-audits"
  storage_account_name  = azurerm_storage_account.audit_storage.name
  container_access_type = "private"
}

# 4. Retención de blobs (30 días por defecto)
resource "azurerm_storage_management_policy" "audit_retention" {
  storage_account_id = azurerm_storage_account.audit_storage.id

  rule {
    name    = "retention-policy"
    enabled = true

    filters {
      prefix_match = ["audits/"]
      blob_types   = ["blockBlob"]
    }

    actions {
      base_blob {
        delete_after_days_since_modification_greater_than = 30
      }
    }
  }
}

##############################
#   AWS S3 BUCKET
##############################
provider "aws" {
  region = "us-east-1"
}

# 5. Bucket S3 para auditorías CI/CD
resource "aws_s3_bucket" "audit_bucket" {
  bucket        = "inclusive-audit-reports-${random_string.suffix.result}"
  force_destroy = true

  tags = {
    Environment = "ci-cd"
    Project     = "inclusive-learning"
  }
}

# 6. Versionamiento habilitado
resource "aws_s3_bucket_versioning" "audit_versioning" {
  bucket = aws_s3_bucket.audit_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

# 7. Política de ciclo de vida (retención 30 días)
resource "aws_s3_bucket_lifecycle_configuration" "retention_policy" {
  bucket = aws_s3_bucket.audit_bucket.id

  rule {
    id     = "audit-retention"
    status = "Enabled"

    filter {
      prefix = "audits/"
    }

    expiration {
      days = 30
    }
  }
}

# 8. Bloqueo de acceso público (seguridad)
resource "aws_s3_bucket_public_access_block" "audit_public_block" {
  bucket                  = aws_s3_bucket.audit_bucket.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

##############################
#   RECURSOS AUXILIARES
##############################
resource "random_string" "suffix" {
  length  = 5
  upper   = false
  special = false
}

##############################
#   OUTPUTS ÚTILES
##############################
output "azure_storage_account_name" {
  description = "Nombre de la cuenta de almacenamiento Azure"
  value       = azurerm_storage_account.audit_storage.name
}

output "azure_container_name" {
  description = "Nombre del contenedor de auditorías Azure"
  value       = azurerm_storage_container.audit_container.name
}

output "aws_bucket_name" {
  description = "Nombre del bucket S3 de auditorías"
  value       = aws_s3_bucket.audit_bucket.bucket
}
