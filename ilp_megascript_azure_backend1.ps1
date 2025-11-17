<#
===========================================================
 ILP MEGA-SCRIPT AZURE BACKEND
 Autor: ChatGPT – Plataforma ILP
 Objetivo: Infraestructura DevOps + AKS + ACR + Terraform
===========================================================
#>

$ErrorActionPreference = "Stop"

Write-Host "==============================================="
Write-Host " ILP MEGA-SCRIPT AZURE BACKEND (DEVOPS + IAC)"
Write-Host "===============================================" -ForegroundColor Cyan

# ============================
# 0. Project Root
# ============================
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "ProjectRoot: $projectRoot"
Set-Location $projectRoot

# ============================
# 1. Verificar estructura
# ============================
Write-Host "Step 1 - Verificando estructura de módulos..."

$commonsDto = "$projectRoot\commons-service\src\main\java\com\inclusive\common\dto"
$adaptiveSvc = "$projectRoot\adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

if (Test-Path $commonsDto) {
    Write-Host "[INFO] commons-service DTO path OK: $commonsDto"
} else { throw "[ERROR] No existe commons-service DTO path: $commonsDto" }

if (Test-Path $adaptiveSvc) {
    Write-Host "[INFO] adaptive-education-service Java path OK: $adaptiveSvc"
} else { throw "[ERROR] No existe adaptive-education-service path: $adaptiveSvc" }

# ============================
# 2. Crear manifiestos K8s
# ============================
Write-Host "Step 2 - Creando manifiestos Kubernetes (AKS) para adaptive-education-service..."

$k8sDir = "$projectRoot\k8s"
New-Item -ItemType Directory -Force -Path $k8sDir | Out-Null

$deploymentYaml = @"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: adaptive-education-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: adaptive-education-service
  template:
    metadata:
      labels:
        app: adaptive-education-service
    spec:
      containers:
      - name: adaptive-education-service
        image: \$(ACR_LOGIN_SERVER)/adaptive-education-service:\$(Build.BuildId)
        ports:
        - containerPort: 8080
"@

$serviceYaml = @"
apiVersion: v1
kind: Service
metadata:
  name: adaptive-education-service
spec:
  type: LoadBalancer
  selector:
    app: adaptive-education-service
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
"@

$deploymentYaml | Out-File "$k8sDir\adaptive-education-service-deployment.yml" -Encoding utf8
$serviceYaml     | Out-File "$k8sDir\adaptive-education-service-service.yml" -Encoding utf8

Write-Host "[OK]   Wrote: $k8sDir\adaptive-education-service-deployment.yml"
Write-Host "[OK]   Wrote: $k8sDir\adaptive-education-service-service.yml"

# ============================
# 3. Terraform infra
# ============================
Write-Host "Step 3 - Creando estructura Terraform (AKS + ACR + Postgres)..."

$tfRoot = "$projectRoot\infra\terraform\aks"
New-Item -ItemType Directory -Force -Path $tfRoot | Out-Null

@"
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~>3.80"
    }
  }

  required_version = ">=1.5.0"
}

provider "azurerm" {
  features {}
}
"@ | Out-File "$tfRoot\provider.tf" -Encoding utf8

@"
variable "resource_group" {}
variable "location" {}
variable "aks_name" {}
variable "acr_name" {}
"@ | Out-File "$tfRoot\variables.tf" -Encoding utf8

@"
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group
  location = var.location
}

resource "azurerm_kubernetes_cluster" "aks" {
  name                = var.aks_name
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name

  default_node_pool {
    name       = "system"
    node_count = 2
    vm_size    = "Standard_DS2_v2"
  }

  identity {
    type = "SystemAssigned"
  }
}

resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
}

"@ | Out-File "$tfRoot\main.tf" -Encoding utf8

@"
output "aks_name" {
  value = azurerm_kubernetes_cluster.aks.name
}

output "acr_name" {
  value = azurerm_container_registry.acr.login_server
}
"@ | Out-File "$tfRoot\outputs.tf" -Encoding utf8

Write-Host "[OK]   Terraform infra generada."

# ============================
# 4. AZURE PIPELINES (YAML)
# ============================
Write-Host "Step 4 - Creando pipeline Azure DevOps (azure-pipelines.yml)..."

$pipelineYaml = @"
trigger:
- main

pool:
  vmImage: 'ubuntu-latest'

variables:
  dockerImageName: 'adaptive-education-service'
  dockerImageTag: '\$(Build.BuildId)'
  vmImage: 'ubuntu-latest'
  javaVersion: '17'
  mavenOptions: '-Xmx1024m'
  terraformWorkingDirectory: 'infra/terraform/aks'

stages:
- stage: Build
  jobs:
  - job: BuildJava
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - task: JavaToolInstaller@0
      inputs:
        versionSpec: '\$(javaVersion)'
        jdkArchitectureOption: 'x64'

    - task: Maven@3
      inputs:
        mavenPomFile: 'pom.xml'
        goals: 'clean package'
        options: '\$(mavenOptions)'

    - task: PublishPipelineArtifact@1
      inputs:
        targetPath: '$(System.DefaultWorkingDirectory)'
        artifact: 'backend'

- stage: Docker
  dependsOn: Build
  jobs:
  - job: BuildDocker
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - script: az acr login --name \$(acrName)
      displayName: 'Login ACR'

    - task: Docker@2
      inputs:
        command: 'buildAndPush'
        repository: '\$(dockerImageName)'
        dockerfile: '**/Dockerfile'
        containerRegistry: '\$(acrServiceConnection)'
        buildContext: '$(System.DefaultWorkingDirectory)'
        tags: |
          \$(dockerImageTag)

- stage: DeployAKS
  dependsOn: Docker
  jobs:
  - job: Deploy
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - script: |
        az aks get-credentials -g \$(resourceGroup) -n \$(aksClusterName) --overwrite-existing
        kubectl apply -f k8s/adaptive-education-service-deployment.yml
        kubectl apply -f k8s/adaptive-education-service-service.yml
      displayName: 'Deploy to AKS'
"@

$pipelineYaml | Out-File "$projectRoot\azure-pipelines.yml" -Encoding utf8

Write-Host "[OK]   Wrote: $projectRoot\azure-pipelines.yml"

# ============================
# 5. Dockerfile
# ============================
Write-Host "Step 5 - Verificando Dockerfile de adaptive-education-service..."

$dockerfilePath = "$projectRoot\adaptive-education-service\Dockerfile"

if (-not (Test-Path $dockerfilePath)) {
@"
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/adaptive-education-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
"@ | Out-File $dockerfilePath -Encoding utf8

Write-Host "[OK] Dockerfile generado: $dockerfilePath"
} else {
Write-Host "[INFO] Dockerfile ya existente, no se sobrescribe."
}

# ============================
# 6. README DevOps
# ============================
Write-Host "Step 6 - Creando README_DEVOPS.md..."

@"
# ILP DevOps Infrastructure

Este entorno contiene:

- Azure DevOps Pipeline
- AKS Kubernetes Deployment
- Azure Container Registry (ACR)
- Terraform IaC
- Dockerfile
"@ | Out-File "$projectRoot\README_DEVOPS.md" -Encoding utf8

Write-Host "[OK]   README_DEVOPS.md generado."

# ============================
# 7. Validación Maven final
# ============================
Write-Host "Step 7 - Validando build Maven..."

mvn clean package -pl adaptive-education-service -am -q

Write-Host "==============================================="
Write-Host " MEGA-SCRIPT ILP AZURE BACKEND COMPLETADO 🎉"
Write-Host "===============================================" -ForegroundColor Green
