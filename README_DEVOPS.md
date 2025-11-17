# ILP Backend â€“ DevOps & IaC

Este proyecto implementa un backend modular y escalable para una plataforma
educativa inclusiva (SaaS) con soporte para:

- Accesibilidad (WCAG 2.1 AA / inclusiÃ³n)
- Microservicios (commons-service, adaptive-education-service, etc.)
- Dataset rico para Machine Learning / Deep Learning
- Despliegue continuo en Azure (AKS + ACR + PostgreSQL flexible)
- Infraestructura como cÃ³digo (Terraform)
- OrquestaciÃ³n con Kubernetes

## Scripts generados por `ilp_megascript_azure_backend.ps1`

- `k8s/adaptive-education-service-deployment.yml`
- `k8s/adaptive-education-service-service.yml`
- `infra/terraform/aks/provider.tf`
- `infra/terraform/aks/variables.tf`
- `infra/terraform/aks/main.tf`
- `infra/terraform/aks/outputs.tf`
- `azure-pipelines.yml`
- `adaptive-education-service/Dockerfile` (si no existÃ­a)
- `README_DEVOPS.md` (este archivo)

## Flujo recomendado (alto nivel)

1. **Infraestructura (Terraform)**
   - Configura variables en `infra/terraform/aks/variables.tf` o vÃ­a `tfvars`.
   - Ejecuta:
     ```bash
     cd infra/terraform/aks
     terraform init
     terraform plan -out=tfplan
     terraform apply -auto-approve tfplan
     ```

2. **Registro de contenedores (ACR)**
   - ObtÃ©n el `login_server` del ACR desde la salida de Terraform
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
     - Stage `Build` â†’ compila y ejecuta tests Maven.
     - Stage `Infra` â†’ Terraform init/plan/apply.
     - Stage `DockerBuildAndPush` â†’ build & push Docker a ACR.
     - Stage `Deploy` â†’ `kubectl apply -f k8s/*.yml` sobre AKS.

4. **VerificaciÃ³n de despliegue**
   - Usa `kubectl get pods,svc -n <namespace>` para confirmar que
     `adaptive-education-service` estÃ¡ en ejecuciÃ³n.
   - Opcional: expone un Ingress o LoadBalancer para acceso externo.

## Accesibilidad e inclusiÃ³n

La arquitectura de datos de `Student` y `StudentDTO` estÃ¡ pensada para:

- Representar:
  - estilos de aprendizaje (Felder-Silverman, Kolb),
  - perfiles vocacionales (Kuder),
  - nivel socioeconÃ³mico, estructura familiar,
  - uso de tecnologÃ­a de apoyo, etc.
- Permitir monitoreo:
  - tasa de asistencia,
  - rendimiento por Ã¡rea,
  - riesgo de deserciÃ³n,
  - clÃºster de engagement,
  - estado emocional longitudinal.
- Facilitar la construcciÃ³n de datasets para:
  - modelos supervisados (riesgo de deserciÃ³n, recomendaciÃ³n de ruta),
  - modelos no supervisados (clÃºsteres de perfiles),
  - sistemas de recomendaciÃ³n adaptativa.

## Notas adicionales

- Este repo estÃ¡ preparado para evolucionar hacia:
  - multi-tenant (SaaS),
  - mÃ¡s microservicios (assessment, notification, report, tenant, etc.),
  - observabilidad (Prometheus, Grafana, logging estructurado),
  - seguridad avanzada (Key Vault, identidad federada, etc.).
