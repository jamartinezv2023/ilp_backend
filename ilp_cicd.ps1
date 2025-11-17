<#
  ILP CICD SCRIPT (Azure DevOps + Kubernetes)
  -------------------------------------------
  Uso:
    PS> ./ilp_cicd.ps1
#>

param(
  [string]$ProjectRoot = $(Split-Path -Parent $PSCommandPath)
)

Write-Host "==============================================="
Write-Host " ILP CI/CD SCRIPT — AZURE DEVOPS + K8S"
Write-Host "==============================================="
Write-Host "ProjectRoot: $ProjectRoot" -ForegroundColor Yellow

# -------------------------------
# K8s manifests
# -------------------------------
$k8sDir = Join-Path $ProjectRoot "k8s"
if (-not (Test-Path $k8sDir)) { New-Item -ItemType Directory -Path $k8sDir | Out-Null }

$deploymentPath = Join-Path $k8sDir "adaptive-education-service-deployment.yml"
$servicePath    = Join-Path $k8sDir "adaptive-education-service-service.yml"

$deploymentContent = @'
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
        image: $(ACR_LOGIN_SERVER)/adaptive-education-service:$(Build.BuildId)
        ports:
        - containerPort: 8080
'@

$serviceContent = @'
apiVersion: v1
kind: Service
metadata:
  name: adaptive-education-service
spec:
  type: ClusterIP
  ports:
    - port: 80
      targetPort: 8080
  selector:
    app: adaptive-education-service
'@

Set-Content $deploymentPath $deploymentContent -Encoding UTF8
Set-Content $servicePath    $serviceContent    -Encoding UTF8

Write-Host "[OK] Manifiestos Kubernetes generados." -ForegroundColor Green

# -------------------------------
# Azure Pipeline
# -------------------------------
$pipelinePath = Join-Path $ProjectRoot "azure-pipelines.yml"

$pipelineContent = @'
trigger:
  branches:
    include:
      - main
      - master

stages:
- stage: Build
  jobs:
  - job: Build_Job
    pool:
      vmImage: ubuntu-latest
    steps:
    - task: Maven@4
      inputs:
        mavenPomFile: 'adaptive-education-service/pom.xml'
        goals: 'clean package'

- stage: Deploy
  dependsOn: Build
  jobs:
  - job: Deploy_K8s
    pool:
      vmImage: ubuntu-latest
    steps:
    - task: Kubernetes@1
      inputs:
        command: apply
        useConfigurationFile: true
        configuration: 'k8s/*.yml'
'@

Set-Content -Path $pipelinePath -Value $pipelineContent -Encoding UTF8
Write-Host "[OK] Azure Pipeline generado." -ForegroundColor Green

Write-Host "==============================================="
Write-Host " ILP CI/CD SCRIPT COMPLETADO"
Write-Host "==============================================="
