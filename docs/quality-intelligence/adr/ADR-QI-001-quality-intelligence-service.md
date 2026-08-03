# ADR-QI-001: Quality Intelligence Service

## Estado

Propuesto y aprobado para diseño arquitectónico.

La implementación funcional se aplaza hasta disponer de datos
longitudinales suficientes y de un periodo base estable.

## Contexto

Inclusive Learning Platform necesita integrar:

- ingeniería de software;
- investigación científica;
- pedagogía;
- calidad de datos;
- control estadístico;
- MLOps;
- apoyo a decisiones educativas.

El servicio debe analizar el comportamiento longitudinal de métricas
educativas, operativas y algorítmicas sin reemplazar el juicio humano.

## Decisión

Diseñar un componente lógico denominado Quality Intelligence Service.

Durante las fases 1 y 2 se prepararán:

- contratos de eventos;
- diccionario longitudinal;
- catálogo de métricas;
- reglas de calidad;
- trazabilidad;
- versionado de instrumentos;
- definición de periodos base.

La construcción de cartas de control comenzará en la fase 3.

## Responsabilidades futuras

1. Calidad e integridad de datos.
2. Agregación de métricas longitudinales.
3. Control estadístico de procesos.
4. Cartas I-MR, Xbar-R, Xbar-S, P, NP, C y U.
5. Análisis EWMA y CUSUM.
6. Detección de causas especiales.
7. Auditoría científica y reproducibilidad.
8. Monitoreo de desempeño de modelos.
9. Detección de data drift, prediction drift y concept drift.
10. Publicación de señales para Decision Intelligence.

## Restricciones

- No emitir decisiones pedagógicas irreversibles automáticamente.
- Mantener supervisión humana.
- Seudonimizar datos usados en analítica.
- Versionar instrumentos, métricas, datasets y modelos.
- Registrar fecha, cohorte, fuente, versión y contexto.
- Distinguir variación natural de causas especiales.
- No interpretar una señal estadística como causalidad automática.

## Consecuencias arquitectónicas

Assessment, Adaptive Education, Fieldwork y Feature Store deberán producir
registros y eventos consistentes, versionados y trazables.

Quality Intelligence consumirá datos analíticos; no será propietario de las
transacciones operativas originales.