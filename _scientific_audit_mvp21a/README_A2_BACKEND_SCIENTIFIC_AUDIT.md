# FASE A2 — Auditoría Científica Profunda del Backend

## Propósito

Esta fase congela la línea base funcional MVP-21A y transforma el backend en una plataforma auditable para investigación doctoral, ciencia reproducible, Machine Learning, Deep Learning futuro y tesis por compendio de artículos.

## Regla principal

No se debe añadir funcionalidad nueva antes de terminar la auditoría.

## Capas auditadas

1. Microservicio
2. Paquete
3. Controller
4. Service / Engine
5. Use Case
6. Entity
7. Repository
8. DTO
9. Variable científica
10. Dataset
11. Paper asociado
12. Decisión de estado

## Datasets doctorales identificados

1. CORE_EDUCATIONAL_DATASET
2. DATASET_KOLB_LONGITUDINAL
3. DATASET_LEARNING_PREFERENCES
4. DATASET_VOCATIONAL_PROFILE
5. DATASET_RAW_ASSESSMENT_RESPONSES
6. DATASET_FEATURE_STORE
7. DATASET_RECOMMENDATIONS
8. DATASET_INTERVENTIONS
9. DATASET_RESEARCH_EVIDENCE
10. DATASET_EXPERIMENT_REGISTRY
11. DATASET_MULTIMODAL

## Decisiones

- FREEZE: componente funcional y protegido.
- AUDIT: componente funcional o parcial con valor científico que requiere revisión.
- MVP22: componente valioso pero no necesario para cierre MVP-21A.
- FREEZE_WITH_CAUTION: componente funcional pero sensible a cambios.

## Principios aplicados

- IEEE 730
- SOLID
- DDD
- Arquitectura Hexagonal
- FIRST
- AAA
- Pirámide de Cohn
- DevOps
- IaC
- Ciencia reproducible
