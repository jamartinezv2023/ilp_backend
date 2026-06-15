# MVP-21A.3.1 - Matriz de contratos Frontend Backend

Objetivo:
Comparar automaticamente endpoints consumidos por React contra endpoints reales expuestos por los microservicios.

Estados:
- OK: existe y responde.
- WARNING: existe, pero el servicio no esta levantado.
- MISSING: frontend lo consume, pero no existe backend equivalente.
- FREEZE: congelar para trabajo de campo.
- CRITICAL: imprescindible para MVP-21A.

Regla:
No arrancar servicios a ciegas. Primero identificar contrato, microservicio responsable, puerto local, estado y prioridad.
