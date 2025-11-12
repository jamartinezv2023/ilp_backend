@echo off
:: Auto Push Controlado - híbrido (seguro para CMD y Git Bash)
cd /d "%~dp0"
chcp 65001 >nul

echo ==========================================================
echo 🚀 Inclusive Platform - Auto Push Controlado
echo ==========================================================
echo.
git status
echo.
set /p CONTINUAR=¿Deseas subir los cambios al repositorio? (s/n): 
if /i "%CONTINUAR%" NEQ "s" (
    echo ❌ Operación cancelada por el usuario.
    echo [%date% %time%] Cancelado manualmente >> "%~dp0logs\%date:~6,4%-%date:~3,2%-%date:~0,2%_log.txt"
    goto :FIN
)
set /p MENSAJE=Escribe el mensaje del commit: 
if "%MENSAJE%"=="" set "MENSAJE=Actualización automática"

git add -A
git commit -m "%MENSAJE%" || echo No hubo cambios para commitear.

git push origin main
if errorlevel 1 (
    echo ❌ Error al subir los cambios.
    echo [%date% %time%] ❌ Error en push: "%MENSAJE%" >> "%~dp0logs\%date:~6,4%-%date:~3,2%-%date:~0,2%_log.txt"
    call :Notify "Error al subir los cambios"
    goto :FIN
)

for /f "tokens=1" %%H in ('git rev-parse HEAD') do set HASH=%%H

echo ✅ Cambios subidos correctamente.
echo [%date% %time%] ✅ Push exitoso: "%MENSAJE%" (commit %HASH%) >> "%~dp0logs\%date:~6,4%-%date:~3,2%-%date:~0,2%_log.txt"
call :Notify "Push exitoso: %MENSAJE%"

:FIN
echo.
echo ----------------------------------------------------------
echo 🕒 Registro guardado en: %~dp0logs\%date:~6,4%-%date:~3,2%-%date:~0,2%_log.txt
echo ----------------------------------------------------------
pause
exit /b

:Notify
where powershell >nul 2>nul
if %errorlevel%==0 (
    powershell -Command ^
    "[console]::beep(1000,250); Add-Type -AssemblyName PresentationFramework;[System.Windows.MessageBox]::Show('%~1','Inclusive Platform - Auto Push','OK','Information')"
    exit /b
)
echo 🔔 %~1
exit /b
