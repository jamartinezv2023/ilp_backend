# Descarga sintética desde navegador

Laboratorio separado de las rutas React, la demostración offline y el piloto.
El adaptador HTTP sólo existe en src/test, escucha en loopback con puerto aleatorio,
y se inicia exclusivamente con ILP_DATASET_READY. H2 se configura en la clase base
SyntheticSubmissionHistoryHttpE2ETest; definición, identidad, mapeo de envío y
puntuación son fixtures. Servicios de persistencia, historial y generador son reales.
La preparación usa el E2E existente para persistir dos intentos del mismo sujeto
y un tercero ajeno; la descarga incluye únicamente los dos primeros.

El navegador recibe CSV y manifiesto en una respuesta JSON indivisible, verifica
SHA-256 con Web Crypto y habilita dos enlaces de descarga. Se eliminan enlaces y
URLs anteriores al preparar otra instantánea. Nunca se descarga parcialmente ante
un 422 del generador, HTML inesperado o checksum inválido.

Pruebas: correlación completa de filas con respuestas e historial persistidos,
SHA-256 de bytes descargados, rechazo real de versión discrepante sin enlaces
remanentes, mensajes y cambio de idioma es/en, checksum manipulado.

Windows PowerShell, desde un checkout limpio de esta rama:

```powershell
& {
    $ErrorActionPreference = 'Stop'
    Push-Location 'e2e/dataset-browser'
    try {
        npm.cmd ci
        if ($LASTEXITCODE -ne 0) { throw 'npm ci failed' }
        npx.cmd playwright install chromium
        if ($LASTEXITCODE -ne 0) { throw 'Browser installation failed' }
    } finally { Pop-Location }
    node.exe e2e/dataset-browser/run.mjs
    if ($LASTEXITCODE -ne 0) { throw 'Preserve browser and JUnit reports' }
}
```

Requiere Java 17 y Node 22. CI conserva Playwright y JUnit. No despliega la interfaz,
no añade controladores de producción, no conecta una BD científica ni autoriza
recolección escolar. Los dos archivos corresponden a una instantánea sintética;
no acreditan validez psicométrica ni un mecanismo de autorización de exportación.

## Inspección manual Windows

Ejecute `node.exe e2e/dataset-browser/run.mjs --manual` desde un worktree aislado.
Requiere Java 17 y Node 22; no requiere npm ni instalar Chromium para este modo.
El ejecutor abre el navegador predeterminado de Windows y muestra `LAB_URL` como
alternativa. Seleccione es/en, prepare y descargue CSV y manifiesto. Pulse ENTER
en PowerShell cuando termine. El coordinador cierra a los 9 minutos y el backend
limita la sesión a 10. La BD H2 temporal se cierra al finalizar Gradle.
`MANUAL_SESSION_EXIT_CODE=0` sólo confirma cierre normal, no aprobación E2E.
Los archivos descargados permanecen en su carpeta de Descargas.
