<#
  ILP INFRA SCRIPT (Terraform IaC)
  --------------------------------
  Uso:
    PS> ./ilp_infra.ps1
#>

param(
  [string]$ProjectRoot = $(Split-Path -Parent $PSCommandPath)
)

Write-Host "==============================================="
Write-Host " ILP INFRA SCRIPT — TERRAFORM"
Write-Host "==============================================="
Write-Host "ProjectRoot: $ProjectRoot" -ForegroundColor Yellow

$infraDir = Join-Path $ProjectRoot "infra/terraform/aks"
if (-not (Test-Path $infraDir)) {
    New-Item -ItemType Directory -Path $infraDir | Out-Null
    Write-Host "[DIR] Created: $infraDir"
}

# provider.tf
$providerTf = @'
terraform {
  required_version = ">= 1.5.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
}

provider "azurerm" {
  features {}
}
'@
Set-Content -Path "$infraDir/provider.tf" -Value $providerTf -Encoding UTF8

# variables.tf
$variablesTf = @'
variable "resource_group_name" { type = string }
variable "location" { type = string, default = "eastus" }
variable "aks_cluster_name" { type = string, default = "ilp-aks-cluster" }
variable "acr_name" { type = string }
variable "postgres_server_name" { type = string, default = "ilp-postgres" }
variable "postgres_admin_user" { type = string }
variable "postgres_admin_password" { type = string, sensitive = true }
'@
Set-Content -Path "$infraDir/variables.tf" -Value $variablesTf -Encoding UTF8

# main.tf
$mainTf = @'
resource "azurerm_resource_group" "ilp_rg" {
  name     = var.resource_group_name
  location = var.location
}

resource "azurerm_container_registry" "ilp_acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.ilp_rg.name
  location            = azurerm_resource_group.ilp_rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "azurerm_kubernetes_cluster" "ilp_aks" {
  name                = var.aks_cluster_name
  location            = azurerm_resource_group.ilp_rg.location
  resource_group_name = azurerm_resource_group.ilp_rg.name

  default_node_pool {
    name       = "system"
    node_count = 2
    vm_size    = "Standard_DS2_v2"
  }

  identity { type = "SystemAssigned" }
}
'@
Set-Content -Path "$infraDir/main.tf" -Value $mainTf -Encoding UTF8

# outputs.tf
$outputsTf = @'
output "aks_cluster_name"   { value = azurerm_kubernetes_cluster.ilp_aks.name }
output "acr_login_server"   { value = azurerm_container_registry.ilp_acr.login_server }
output "resource_group_name" { value = azurerm_resource_group.ilp_rg.name }
'@
Set-Content -Path "$infraDir/outputs.tf" -Value $outputsTf -Encoding UTF8

Write-Host "[OK] Archivos Terraform generados." -ForegroundColor Green
Write-Host "==============================================="
Write-Host " ILP INFRA SCRIPT COMPLETADO"
Write-Host "==============================================="
