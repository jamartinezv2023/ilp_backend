# Ficha de trazabilidad: respuesta de evaluación persistida

**Estado:** contrato y reconstrucción técnica verificados con datos sintéticos. La vinculación con preguntas de investigación (RQ) es provisional. Esta ficha no habilita recolección escolar ni acredita validez psicométrica, utilidad educativa o resultados de tesis.

**Unidad observable:** un intento de evaluación identificado por `administrationId`. La ruta de escritura es `POST /api/v1/assessment-submissions`; las dos proyecciones de lectura son `GET /api/v1/assessment-responses/{id}` (respuestas) y `GET /api/v1/participants/{id}/assessment-scientific-history` (observación derivada). La unión comprobada es `administrationId`; la identidad escolar (`studentId`) y el seudónimo de investigación (`participantId` de la historia) tienen funciones distintas y no deben equipararse.

| Campo observable | Procedencia y unidad | Persistencia y recuperación | Uso analítico provisional |
| --- | --- | --- | --- |
| `administrationId` | Identificador del intento enviado; una cadena por intento | Respuesta persistida e historia científica, misma clave | Unión y detección de duplicados; no es una variable de resultado |
| `studentId` y `researchParticipantUuid` | Identidad operativa y referencia de consentimiento en el envío; el servicio resuelve esta última a un sujeto seudónimo | `studentId` aparece en la lectura de respuestas; `participantId` seudónimo aparece en historia; la referencia UUID no se expone en esas lecturas | Agrupación longitudinal provisional para RQ1, sujeta a gobernanza de identidad y consentimiento |
| `assessmentCode`, `assessmentVersion` | Identidad y versión declaradas del instrumento | Ambas lecturas; se contrasta la versión con la definición activa antes de guardar | Estratificación por instrumento; no comparar puntuaciones entre versiones sin estudio de equivalencia |
| `responses` | Pregunta y opción contestadas; una selección sintética en la prueba | Respuesta persistida bajo `answers`; la historia científica contiene puntuaciones derivadas, **no** las respuestas crudas | Insumo candidato para variables de RQ1/RQ2; operacionalización y validez pendientes |
| `submittedAt` | Instante UTC del intento (en la prueba, valor fijo); si falta en la petición, el contrato asigna la hora actual | Ambas lecturas y límites `firstSubmittedAt`/`lastSubmittedAt` de historia | Orden temporal candidato de RQ1; no equivale por sí solo a duración ni a cronología validada |
| `context.source`, `fieldworkPhase`, `language`, `translationVersion` | Metadatos declarados por el cliente; `TEST_ONLY` y fuente sintética en la prueba | Contexto de la observación; `translationVersion` dentro de `completeContext` | Procedencia, filtrado y reproducibilidad; una etiqueta sintética no constituye autorización de campo |
| `scoringAlgorithmVersion`, puntuaciones e interpretación | Salida del motor de puntuación; versión explícita en historia | Resultado científico derivado, enlazado al mismo intento | Posibles características para RQ1/RQ2; requieren validación independiente y control de fuga temporal |

## Preguntas de decisión del ciclo

1. **Problema:** evitar que una respuesta guardada pierda su vínculo con el instrumento, la identidad seudónima, el tiempo o su derivación.
2. **Variable y evidencia:** los campos de la tabla son observables técnicos; se comprueba igualdad exacta del identificador, versión y tiempo entre el envío y las dos lecturas. Ninguna puntuación sintética estima aprendizaje.
3. **RQ:** RQ1 (trayectoria temporal) es el vínculo provisional principal; RQ2 (predicción) sólo podría usar respuestas y puntuaciones tras definición de variables, cortes temporales y validación. RQ3–RQ5 no quedan sustentadas por este ciclo.
4. **Validación científica pendiente:** definir constructos, población, medición, equivalencia entre idiomas y versiones, datos faltantes, sesgos y protocolo de análisis antes de formular inferencias.
5. **Artículo candidato:** un futuro trabajo metodológico sobre calidad y trazabilidad de datos longitudinales; no se reivindica contribución publicable con la prueba sintética.
6. **Mecanismo e hipótesis refutable:** si cada intento conserva identificador, sujeto seudónimo, instrumento/versionado y tiempo, podrá reconstruirse una secuencia técnica; un desacuerdo entre proyecciones refuta esa condición necesaria.
7. **Reproducibilidad:** fijar commit, versión del instrumento, motor, configuración y datos de prueba; ejecutar la prueba indicada abajo y conservar salida JUnit. La réplica científica exigirá además protocolo, autorizaciones y datos gobernados por separado.

## Prueba y límites

Ejecutar desde la raíz del backend:

```sh
./gradlew :adaptive-education-service:test --tests '*SyntheticSubmissionHistoryHttpE2ETest' --no-daemon
```

La prueba `postPersistsAndBothHistoriesRecoverSameAttemptThenRejectDuplicate` usa Spring HTTP/MockMvc y H2 aislada. Verifica envío, lectura de respuesta cruda, lectura de observación seudónima, coincidencia del intento y tiempo, versión del instrumento y del algoritmo, procedencia sintética, rechazo de repetición y una única observación. La prueba complementaria rechaza el envío sin consentimiento activo antes de escribir. Hay componentes simulados (definición, identidad y motor de puntuación): aprobarla demuestra integridad del circuito técnico bajo esas condiciones, no validez del instrumento ni comportamiento de una base científica de producción.

### Secuencia longitudinal sintética (RQ1, condición necesaria)

La prueba `syntheticAttemptsAreOrderedBySubmissionTimeAndPartitionedByResearchSubject` envía primero el intento posterior y después el anterior para el mismo seudónimo, más un intento de otro seudónimo. Comprueba dos observaciones en orden descendente por `submittedAt`, límites temporales exactos y ausencia del tercer intento en la proyección del primer sujeto. La consulta del segundo sujeto muestra únicamente su intento. Los tres registros son ficticios; esta separación de resultados por clave de consulta **no demuestra autorización de acceso HTTP ni anonimización**. Antes de analizar trayectorias reales harán falta reglas explícitas para huso horario, frecuencia de medida, instrumentos/versiones comparables, observaciones faltantes y población autorizada. No se calcula cambio educativo ni tendencia con estos ejemplos.

### Artefacto analítico sintético v1

La misma prueba produce `build/research-synthetic-dataset/observations.csv` y `manifest.json` (ambos retenidos como artefacto del workflow `Research Traceability H2 E2E`). Una fila corresponde a una opción seleccionada por pregunta e intento, ordenada por tiempo ascendente. El CSV incluye seudónimo de investigación, identificador de intento, instrumento y versión, tiempo UTC, versión de puntuación, pregunta/opción, idioma, versión de traducción y procedencia. **No incluye `studentId`, UUID de identidad operativa ni información de consentimiento**. El manifiesto fija esquema, rango temporal, número de intentos y filas, clase `SYNTHETIC_ONLY` y SHA‑256 del CSV en UTF‑8 con saltos LF. El mismo estado genera los mismos bytes.

`SyntheticResearchDatasetBuilder` sólo es una pieza interna, sin controlador de exportación. Exige al menos dos intentos, igualdad de sujeto, versión de instrumento y algoritmo, tiempos concordantes con las respuestas crudas, respuestas no vacías y procedencia `SYNTHETIC_HTTP_E2E`/`TEST_ONLY` junto con identificadores operativos sintéticos. Rechaza el lote completo ante discrepancias; el manifiesto exitoso declara cero exclusiones. El rechazo se comunica con un código de calidad, sin publicar un dataset parcial. Estos metadatos declarados por el cliente no acreditan que un registro arbitrario sea inocuo; por ello la pieza no está disponible como API de datos reales.

La salida muestra que la cadena técnica ya puede generar una matriz de trabajo reproducible. No es una matriz de entrenamiento válida: faltan definición de constructos y desenlace, plan de muestreo, elegibilidad aprobada, consentimiento verificable, separación de identidades, controles de acceso, tratamiento de faltantes, equivalencia de versiones e idiomas y validación del protocolo.

**Puerta de campo:** cualquier recolección real depende por separado de aprobación ética e institucional, consentimiento, seguridad, control de acceso, gobierno del dato y validación metodológica. El indicador de disponibilidad técnica de una ruta no sustituye esas decisiones. Se mantienen separados la demostración offline y el circuito de recolección del piloto.

Marco de referencia interno: *Marco Rector de Investigación, Calidad y Desarrollo para la Tesis Doctoral basada en ILP*, apartados de trazabilidad, niveles de validación y doble definición de terminado.
