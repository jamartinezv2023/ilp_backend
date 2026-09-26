# ILP: preparación offline del piloto (contrato de revisión)

Estado: **diseño para revisión; recolección de campo deshabilitada**. Este documento
delimita un incremento verificable. No habilita instrumentos, participantes,
sincronización ni persistencia científica.

## Separación de circuitos

| Circuito | Comportamiento actual | Criterio de avance |
| --- | --- | --- |
| Demostración sintética offline | Actividad ficticia de Física en el frontend, sin envío al backend | Debe funcionar tras recargar sin red; reiniciar elimina su historial local |
| Prueba HTTP sintética | Navegador, Spring y H2 aislada; comprueba un mismo identificador en respuesta e historial | Sólo en entorno de prueba, con identificadores `SYNTHETIC-` y consentimiento ficticio |
| Recolección del piloto | Cerrada: envío genérico no aprobado 403; escrituras legacy 410 | Requiere protocolo institucional, responsable de datos, instrumento y versión aprobados, autorización de uso y pruebas específicas antes de habilitarse |

Una respuesta guardada únicamente en un dispositivo **no** cuenta como respuesta
recibida, sincronizada ni integrante del conjunto de datos doctoral.

## Contrato propuesto para un incremento posterior

1. Registrar localmente un identificador de operación UUID, identificador y
   versión inmutables del instrumento, versión de traducción, idioma, instante
   local con zona horaria, identificador seudónimo y respuestas. La identidad
   de la persona y las autorizaciones se definirán en el protocolo aprobado.
2. Mantener estados `PENDING`, `SENDING`, `CONFIRMED`, `CONFLICT` y `FAILED`.
   Una interfaz debe distinguir claramente pendiente local de confirmado por
   el servidor, incluso tras recargar y perder la conexión.
3. El servidor debe validar autorización, esquema y versión antes de escribir;
   asociar de forma única el identificador de operación al resultado; repetir
   una operación idéntica debe devolver el mismo resultado. Una repetición con
   el mismo identificador y contenido distinto debe generar conflicto.
4. Confirmar sólo cuando la lectura posterior del historial devuelve el mismo
   identificador de operación, participante seudónimo, instrumento y versión.
   Un GET fallido no autoriza repetir automáticamente el POST con otro ID.
5. Definir con la revisión institucional cifrado local, control de acceso,
   caducidad, borrado, manejo de dispositivo compartido y recuperación. Hasta
   entonces no guardar respuestas reales ni datos identificables en el equipo.

El esquema y los endpoints del punto 3 son **propuestas**, no capacidades
actuales. Ninguna bandera de interfaz o definición activa sustituye la
aprobación de campo. El bloqueo del PR #125 permanece como condición inicial.

## Criterios E2E antes de activar el circuito

- Sin conexión: un intento sintético queda `PENDING` y reaparece tras recargar;
  no se presenta como sincronizado.
- Reconexión: una operación se transmite una vez y el historial confirma el
  mismo ID, instrumento y versión; una repetición idéntica no duplica filas.
- Fallo de red después del POST: reintento con el **mismo** ID; se comprueba
  respuesta e historial antes de declarar éxito.
- Conflicto: mismo ID con otro contenido recibe conflicto explícito, sin
  reemplazo silencioso de respuestas.
- Rechazo: falta de aprobación, consentimiento o versión aceptada no escribe
  datos; se comprueba en H2 aislada y luego en revisión desplegada y aislada.
- Aislamiento: la demostración offline no invoca endpoints de recolección;
  las pruebas no crean cuentas reales ni conectan la base científica.

## Secuencia de implementación

1. Aprobar el contrato técnico y el protocolo de datos con dirección y expertos.
2. Implementar la cola local **sólo sintética** y sus pruebas de navegador.
3. Implementar idempotencia y estado de sincronización en Spring con H2 de
   prueba; medir duplicados, conflictos y recuperación de fallos.
4. Revisar seguridad y accesibilidad en dispositivo compartido; verificar E2E
   en un despliegue de revisión con identidad de commit y base aislada.
5. Preparar por separado la autorización del trabajo de campo y la versión
   aprobada del instrumento. El paso 4 no habilita por sí solo el paso 5.
