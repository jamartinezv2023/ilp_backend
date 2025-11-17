<#
    ilp_megascript_azure_backend.ps1
    --------------------------------
    MEGA-SCRIPT ILP BACKEND (DevOps + IaC + AKS + ACR + CI/CD)

    - Genera manifiestos Kubernetes para adaptive-education-service
    - Genera estructura Terraform para AKS + ACR + PostgreSQL
    - Genera pipeline azure-pipelines.yml para Azure DevOps
    - Verifica/crea Dockerfile de adaptive-education-service
    - Genera README_DEVOPS.md con guía de uso
    - Ejecuta build Maven del adaptive-education-service para validar

    Uso:
        PS> cd C:\temp_ilp\inclusive-learning-platform-backend
        PS> .\ilp_megascript_azure_backend.ps1
#>

param(
    [string]$ProjectRoot = $(Split-Path -Parent $PSCommandPath)
)

$ErrorActionPreference = "Stop"

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host " ILP MEGA-SCRIPT AZURE BACKEND (DEVOPS + IAC)" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "ProjectRoot: $ProjectRoot" -ForegroundColor Yellow

# ---------------------------------------------------------
# Step 1 - Verificar estructura de módulos
# ---------------------------------------------------------
Write-Host "Step 1 - Verificando estructura de módulos..." -ForegroundColor Cyan

$commonsDtoPath = Join-Path $ProjectRoot "commons-service\src\main\java\com\inclusive\common\dto"
$adaptiveJavaPath = Join-Path $ProjectRoot "adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

if (-not (Test-Path $commonsDtoPath)) {
    Write-Host "[WARN] No existe ruta DTO commons-service: $commonsDtoPath" -ForegroundColor Yellow
} else {
    Write-Host "[INFO] commons-service DTO path OK: $commonsDtoPath" -ForegroundColor Green
}

if (-not (Test-Path $adaptiveJavaPath)) {
    Write-Host "[WARN] No existe ruta Java adaptive-education-service: $adaptiveJavaPath" -ForegroundColor Yellow
} else {
    Write-Host "[INFO] adaptive-education-service Java path OK: $adaptiveJavaPath" -ForegroundColor Green
}

# Asegurar carpetas auxiliares
$k8sDir          = Join-Path $ProjectRoot "k8s"
$infraAksDir     = Join-Path $ProjectRoot "infra\terraform\aks"

foreach ($dir in @($k8sDir, $infraAksDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir | Out-Null
        Write-Host "[DIR] Created: $dir" -ForegroundColor Green
    } else {
        Write-Host "[DIR] Exists:  $dir" -ForegroundColor DarkGreen
    }
}

# ---------------------------------------------------------
# Step 2 - Manifiestos Kubernetes (AKS) para adaptive-education-service
# ---------------------------------------------------------
Write-Host "Step 2 - Creando manifiestos Kubernetes (AKS) para adaptive-education-service..." -ForegroundColor Cyan

$deploymentPath = Join-Path $k8sDir "adaptive-education-service-deployment.yml"
$servicePath    = Join-Path $k8sDir "adaptive-education-service-service.yml"

# IMPORTANTE:
# Usamos here-string con comillas simples (@') para que PowerShell
# NO interprete $(...) como subexpresiones, sino como texto literal.
$k8sDeployment = @'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: adaptive-education-service
  labels:
    app: adaptive-education-service
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
          image: $(ACR_LOGIN_SERVER)/adaptive-education-service:$(Build.BuildId)
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: SPRING_DATASOURCE_URL
              value: "jdbc:postgresql://$(POSTGRES_HOST):5432/$(POSTGRES_DB)"
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: adaptive-education-service-secrets
                  key: db-username
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: adaptive-education-service-secrets
                  key: db-password
            - name: SERVER_PORT
              value: "8080"
            - name: MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE
              value: "health,info,metrics,prometheus"
          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "500m"
              memory: "1Gi"
      imagePullSecrets:
        - name: acr-auth
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: adaptive-education-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: adaptive-education-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
'@

$k8sService = @'
apiVersion: v1
kind: Service
metadata:
  name: adaptive-education-service
  labels:
    app: adaptive-education-service
spec:
  type: ClusterIP
  selector:
    app: adaptive-education-service
  ports:
    - name: http
      port: 80
      targetPort: 8080
'@

Set-Content -Path $deploymentPath -Value $k8sDeployment -Encoding UTF8
Write-Host "[OK]   Wrote: $deploymentPath" -ForegroundColor Green

Set-Content -Path $servicePath -Value $k8sService -Encoding UTF8
Write-Host "[OK]   Wrote: $servicePath" -ForegroundColor Green

# ---------------------------------------------------------
# Step 3 - Terraform (AKS + ACR + PostgreSQL)
# ---------------------------------------------------------
Write-Host "Step 3 - Creando estructura Terraform (AKS + ACR + Postgres)..." -ForegroundColor Cyan

$providerTfPath = Join-Path $infraAksDir "provider.tf"
$variablesTfPath = Join-Path $infraAksDir "variables.tf"
$mainTfPath = Join-Path $infraAksDir "main.tf"
$outputsTfPath = Join-Path $infraAksDir "outputs.tf"

$providerTf = @'
terraform {
  required_version = ">= 1.5.0"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }

  backend "azurerm" {
    # Configurar remotamente en Azure Storage si se desea
  }
}

provider "azurerm" {
  features {}
}
'@

$variablesTf = @'
variable "resource_group_name" {
  type        = string
  description = "Nombre del Resource Group para ILP."
}

variable "location" {
  type        = string
  description = "Región de Azure para los recursos."
  default     = "eastus"
}

variable "aks_cluster_name" {
  type        = string
  description = "Nombre del clúster AKS."
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
'@

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
  dns_prefix          = "ilp-aks"

  default_node_pool {
    name       = "system"
    node_count = 2
    vm_size    = "Standard_DS2_v2"
  }

  identity {
    type = "SystemAssigned"
  }

  network_profile {
    network_plugin    = "azure"
    load_balancer_sku = "standard"
  }
}

resource "azurerm_postgresql_flexible_server" "ilp_pg" {
  name                = var.postgres_server_name
  resource_group_name = azurerm_resource_group.ilp_rg.name
  location            = azurerm_resource_group.ilp_rg.location

  administrator_login          = var.postgres_admin_user
  administrator_login_password = var.postgres_admin_password

  sku_name   = "B_Standard_B1ms"
  storage_mb = 32768
  version    = "16"

  backup {
    backup_retention_days = 7
  }

  high_availability {
    mode = "Disabled"
  }

  maintenance_window {
    day_of_week  = 0
    start_hour   = 0
    start_minute = 0
  }

  authentication {
    password_auth_enabled = true
  }
}

resource "azurerm_postgresql_flexible_server_database" "ilp_pg_db" {
  name      = "bd_colegios"
  server_id = azurerm_postgresql_flexible_server.ilp_pg.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
'@

$outputsTf = @'
output "resource_group_name" {
  value = azurerm_resource_group.ilp_rg.name
}

output "aks_cluster_name" {
  value = azurerm_kubernetes_cluster.ilp_aks.name
}

output "acr_login_server" {
  value = azurerm_container_registry.ilp_acr.login_server
}

output "postgres_fqdn" {
  value = azurerm_postgresql_flexible_server.ilp_pg.fqdn
}
'@

Set-Content -Path $providerTfPath  -Value $providerTf  -Encoding UTF8
Write-Host "[OK]   Wrote: $providerTfPath" -ForegroundColor Green
Set-Content -Path $variablesTfPath -Value $variablesTf -Encoding UTF8
Write-Host "[OK]   Wrote: $variablesTfPath" -ForegroundColor Green
Set-Content -Path $mainTfPath      -Value $mainTf      -Encoding UTF8
Write-Host "[OK]   Wrote: $mainTfPath" -ForegroundColor Green
Set-Content -Path $outputsTfPath   -Value $outputsTf   -Encoding UTF8
Write-Host "[OK]   Wrote: $outputsTfPath" -ForegroundColor Green

# ---------------------------------------------------------
# Step 4 - Pipeline Azure DevOps (azure-pipelines.yml)
# ---------------------------------------------------------
Write-Host "Step 4 - Creando pipeline Azure DevOps (azure-pipelines.yml)..." -ForegroundColor Cyan

$azurePipelinesPath = Join-Path $ProjectRoot "azure-pipelines.yml"

$azurePipelinesContent = @'
trigger:
  branches:
    include:
      - main
      - master
  paths:
    include:
      - adaptive-education-service/*
      - commons-service/*
      - azure-pipelines.yml
      - infra/terraform/aks/*
      - k8s/*

variables:
  vmImage: 'ubuntu-latest'
  javaVersion: '17'
  mavenOptions: '-Xmx1024m'
  dockerImageName: 'adaptive-education-service'
  dockerImageTag: '$(Build.BuildId)'
  terraformWorkingDirectory: 'infra/terraform/aks'

stages:
  - stage: Build
    displayName: 'Build & Test (Maven)'
    jobs:
      - job: Build_Job
        displayName: 'Build ILP Backend (adaptive-education-service)'
        pool:
          vmImage: $(vmImage)
        steps:
          - task: JavaToolInstaller@0
            displayName: 'Install Java $(javaVersion)'
            inputs:
              versionSpec: '$(javaVersion)'
              jdkArchitectureOption: 'x64'
              jdkSourceOption: 'PreInstalled'

          - task: Maven@4
            displayName: 'Maven clean package (adaptive-education-service)'
            inputs:
              mavenPomFile: 'adaptive-education-service/pom.xml'
              goals: 'clean package'
              options: '$(mavenOptions)'
              publishJUnitResults: true
              testResultsFiles: '**/surefire-reports/TEST-*.xml'
              javaHomeOption: 'Path'
              mavenVersionOption: 'Default'

          - task: PublishBuildArtifacts@1
            displayName: 'Publicar artefactos (JARs)'
            inputs:
              PathtoPublish: '$(System.DefaultWorkingDirectory)'
              ArtifactName: 'drop'
              publishLocation: 'Container'

  - stage: Infra
    displayName: 'Provisionar Infraestructura (Terraform AKS + ACR + Postgres)'
    dependsOn: Build
    condition: succeeded()
    jobs:
      - job: Infra_Job
        displayName: 'Terraform plan & apply'
        pool:
          vmImage: $(vmImage)
        steps:
          - task: TerraformInstaller@1
            displayName: 'Instalar Terraform'
            inputs:
              terraformVersion: '1.7.0'

          - script: |
              cd $(terraformWorkingDirectory)
              terraform init
            displayName: 'Terraform init'

          - script: |
              cd $(terraformWorkingDirectory)
              terraform plan -out=tfplan
            displayName: 'Terraform plan'

          - script: |
              cd $(terraformWorkingDirectory)
              terraform apply -auto-approve tfplan
            displayName: 'Terraform apply'

  - stage: DockerBuildAndPush
    displayName: 'Build & Push Docker Image'
    dependsOn: Infra
    condition: succeeded()
    jobs:
      - job: Docker_Job
        displayName: 'Build & Push Docker Image to ACR'
        pool:
          vmImage: $(vmImage)
        steps:
          - task: AzureCLI@2
            displayName: 'Login to ACR'
            inputs:
              azureSubscription: '$(AZURE_SERVICE_CONNECTION)'
              scriptType: 'bash'
              scriptLocation: 'inlineScript'
              inlineScript: |
                az acr login --name $(acrName)

          - task: Docker@2
            displayName: 'Build and Push adaptive-education-service image'
            inputs:
              containerRegistry: '$(ACR_SERVICE_CONNECTION)'
              repository: '$(dockerImageName)'
              command: 'buildAndPush'
              Dockerfile: 'adaptive-education-service/Dockerfile'
              buildContext: '$(System.DefaultWorkingDirectory)'
              tags: |
                $(dockerImageTag)

  - stage: Deploy
    displayName: 'Desplegar en AKS'
    dependsOn: DockerBuildAndPush
    condition: succeeded()
    jobs:
      - job: Deploy_Job
        displayName: 'kubectl apply en AKS'
        pool:
          vmImage: $(vmImage)
        steps:
          - task: AzureCLI@2
            displayName: 'Configurar kubectl (AKS)'
            inputs:
              azureSubscription: '$(AZURE_SERVICE_CONNECTION)'
              scriptType: 'bash'
              scriptLocation: 'inlineScript'
              inlineScript: |
                az aks get-credentials -g $(resourceGroup) -n $(aksClusterName) --overwrite-existing

          - task: Kubernetes@1
            displayName: 'kubectl apply -f k8s/'
            inputs:
              connectionType: 'Azure Resource Manager'
              azureSubscriptionEndpoint: '$(AZURE_SERVICE_CONNECTION)'
              azureResourceGroup: '$(resourceGroup)'
              kubernetesCluster: '$(aksClusterName)'
              command: 'apply'
              useConfigurationFile: true
              configuration: 'k8s/*.yml'
              secretType: 'dockerRegistry'
              containerRegistryType: 'Azure Container Registry'
              dockerRegistryEndpoint: '$(ACR_SERVICE_CONNECTION)'
'@

Set-Content -Path $azurePipelinesPath -Value $azurePipelinesContent -Encoding UTF8
Write-Host "[OK]   Wrote: $azurePipelinesPath" -ForegroundColor Green

# ---------------------------------------------------------
# Step 5 - Dockerfile adaptive-education-service
# ---------------------------------------------------------
Write-Host "Step 5 - Verificando Dockerfile de adaptive-education-service..." -ForegroundColor Cyan

$dockerfilePath = Join-Path $ProjectRoot "adaptive-education-service\Dockerfile"

if (-not (Test-Path $dockerfilePath)) {
    $dockerfileContent = @'
# Dockerfile para adaptive-education-service
# Multi-stage build para imágenes ligeras

FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY ../commons-service/pom.xml ./commons-service/pom.xml
COPY ../pom.xml ./parent-pom.xml

COPY src ./src

RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY target/adaptive-education-service-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
'@
    Set-Content -Path $dockerfilePath -Value $dockerfileContent -Encoding UTF8
    Write-Host "[OK]   Wrote: $dockerfilePath" -ForegroundColor Green
} else {
    Write-Host "[INFO] Dockerfile ya existente, no se sobrescribe." -ForegroundColor Yellow
}

# ---------------------------------------------------------
# Step 6 - README_DEVOPS.md
# ---------------------------------------------------------
Write-Host "Step 6 - Creando README_DEVOPS.md..." -ForegroundColor Cyan

$readmePath = Join-Path $ProjectRoot "README_DEVOPS.md"

$readmeContent = @'
# ILP Backend – DevOps & IaC

Este proyecto implementa un backend modular y escalable para una plataforma
educativa inclusiva (SaaS) con soporte para:

- Accesibilidad (WCAG 2.1 AA / inclusión)
- Microservicios (commons-service, adaptive-education-service, etc.)
- Dataset rico para Machine Learning / Deep Learning
- Despliegue continuo en Azure (AKS + ACR + PostgreSQL flexible)
- Infraestructura como código (Terraform)
- Orquestación con Kubernetes

## Scripts generados por `ilp_megascript_azure_backend.ps1`

- `k8s/adaptive-education-service-deployment.yml`
- `k8s/adaptive-education-service-service.yml`
- `infra/terraform/aks/provider.tf`
- `infra/terraform/aks/variables.tf`
- `infra/terraform/aks/main.tf`
- `infra/terraform/aks/outputs.tf`
- `azure-pipelines.yml`
- `adaptive-education-service/Dockerfile` (si no existía)
- `README_DEVOPS.md` (este archivo)

## Flujo recomendado (alto nivel)

1. **Infraestructura (Terraform)**
   - Configura variables en `infra/terraform/aks/variables.tf` o vía `tfvars`.
   - Ejecuta:
     ```bash
     cd infra/terraform/aks
     terraform init
     terraform plan -out=tfplan
     terraform apply -auto-approve tfplan
     ```

2. **Registro de contenedores (ACR)**
   - Obtén el `login_server` del ACR desde la salida de Terraform
     (`acr_login_server`).
   - Configura en Azure DevOps las variables/secretos necesarios.

3. **Pipeline Azure DevOps**
   - Importa `azure-pipelines.yml` en tu proyecto de Azure DevOps.
   - Configura:
     - `AZURE_SERVICE_CONNECTION`
     - `ACR_SERVICE_CONNECTION`
     - `acrName`
     - `resourceGroup`
     - `aksClusterName`
   - Lanza el pipeline:
     - Stage `Build` → compila y ejecuta tests Maven.
     - Stage `Infra` → Terraform init/plan/apply.
     - Stage `DockerBuildAndPush` → build & push Docker a ACR.
     - Stage `Deploy` → `kubectl apply -f k8s/*.yml` sobre AKS.

4. **Verificación de despliegue**
   - Usa `kubectl get pods,svc -n <namespace>` para confirmar que
     `adaptive-education-service` está en ejecución.
   - Opcional: expone un Ingress o LoadBalancer para acceso externo.

## Accesibilidad e inclusión

La arquitectura de datos de `Student` y `StudentDTO` está pensada para:

- Representar:
  - estilos de aprendizaje (Felder-Silverman, Kolb),
  - perfiles vocacionales (Kuder),
  - nivel socioeconómico, estructura familiar,
  - uso de tecnología de apoyo, etc.
- Permitir monitoreo:
  - tasa de asistencia,
  - rendimiento por área,
  - riesgo de deserción,
  - clúster de engagement,
  - estado emocional longitudinal.
- Facilitar la construcción de datasets para:
  - modelos supervisados (riesgo de deserción, recomendación de ruta),
  - modelos no supervisados (clústeres de perfiles),
  - sistemas de recomendación adaptativa.

## Notas adicionales

- Este repo está preparado para evolucionar hacia:
  - multi-tenant (SaaS),
  - más microservicios (assessment, notification, report, tenant, etc.),
  - observabilidad (Prometheus, Grafana, logging estructurado),
  - seguridad avanzada (Key Vault, identidad federada, etc.).
'@

Set-Content -Path $readmePath -Value $readmeContent -Encoding UTF8
Write-Host "[OK]   Wrote: $readmePath" -ForegroundColor Green

# ---------------------------------------------------------
# Step 7 - Validar build Maven (adaptive-education-service)
# ---------------------------------------------------------
Write-Host "Step 7 - Validando build Maven (adaptive-education-service)..." -ForegroundColor Cyan

Push-Location $ProjectRoot
try {
    mvn clean package -pl adaptive-education-service -am
    Write-Host "[OK] Build Maven de adaptive-education-service completado correctamente." -ForegroundColor Green
}
catch {
    Write-Host "[ERROR] Falló el build Maven de adaptive-education-service." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}
finally {
    Pop-Location
}

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host " MEGA-SCRIPT ILP AZURE BACKEND COMPLETADO" -ForegroundColor Cyan
Write-Host " Archivos clave generados:" -ForegroundColor Yellow
Write-Host "  - azure-pipelines.yml" -ForegroundColor Yellow
Write-Host "  - infra/terraform/aks/*.tf" -ForegroundColor Yellow
Write-Host "  - k8s/adaptive-education-service-*.yml" -ForegroundColor Yellow
Write-Host "  - adaptive-education-service/Dockerfile (si no existía)" -ForegroundColor Yellow
Write-Host "  - README_DEVOPS.md" -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Cyan
