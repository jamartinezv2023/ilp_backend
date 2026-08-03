# Quality Intelligence Service: contrato API preliminar

## Estado

Diseño arquitectónico. No habilitar en producción durante las fases 1 y 2.

## Métricas

GET /api/v1/quality/metrics
GET /api/v1/quality/metrics/{metricCode}
GET /api/v1/quality/metrics/{metricCode}/series
POST /api/v1/quality/metrics/{metricCode}/observations

## Calidad de datos

GET /api/v1/quality/data-quality/summary
GET /api/v1/quality/data-quality/violations
GET /api/v1/quality/data-quality/rules
POST /api/v1/quality/data-quality/evaluate

## Cartas de control

POST /api/v1/quality/control-charts/definitions
GET /api/v1/quality/control-charts
GET /api/v1/quality/control-charts/{chartId}
POST /api/v1/quality/control-charts/{chartId}/baseline
POST /api/v1/quality/control-charts/{chartId}/evaluate
GET /api/v1/quality/control-charts/{chartId}/points
GET /api/v1/quality/control-charts/{chartId}/signals

## Deriva y modelos

GET /api/v1/quality/drift/data
GET /api/v1/quality/drift/predictions
GET /api/v1/quality/drift/concept
GET /api/v1/quality/models/{modelVersion}/performance

## Reproducibilidad

GET /api/v1/quality/reproducibility/runs
GET /api/v1/quality/reproducibility/runs/{runId}
GET /api/v1/quality/research/audit-trail

## Condiciones previas

- Contratos de eventos estabilizados.
- Diccionario longitudinal aprobado.
- Captura temporal confiable.
- Datos seudonimizados para analítica.
- Periodo base disponible.
- Protocolo de interpretación aprobado.