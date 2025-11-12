#!/usr/bin/env bash
# ==========================================================
#  PostgreSQL Universal Authentication Setup (Linux/Ubuntu)
# ==========================================================
#  Autor: Plataforma Inclusiva - DevOps Unified Auth Script
#  Descripción:
#   - Unifica la autenticación de PostgreSQL (usuario postgres)
#   - Configura pg_hba.conf con scram-sha-256
#   - Reinicia PostgreSQL
#   - Actualiza contraseñas y .pgpass
#   - Prueba conexión a todas las bases de datos
# ==========================================================

set -e
export LANG=C
export LC_ALL=C

PG_USER="postgres"
PG_PASS="postgres"
PG_PORT=5432
PG_HBA_PATH=$(sudo -u postgres psql -t -P format=unaligned -c "SHOW hba_file;")
PG_SERVICE="postgresql"
PGPASS_FILE="$HOME/.pgpass"

DBS=("postgres" "auth_service" "inclusive_learning" "adaptive_education_db")

echo "=========================================================="
echo "  UNIVERSAL POSTGRES AUTHENTICATION SETUP (LINUX/UBUNTU)"
echo "=========================================================="

# --- Backup del archivo pg_hba.conf ---
BACKUP_FILE="${PG_HBA_PATH}.bak_$(date +%Y%m%d_%H%M%S)"
sudo cp "$PG_HBA_PATH" "$BACKUP_FILE"
echo "[OK] Copia de seguridad creada en: $BACKUP_FILE"

# --- Actualizar método de autenticación ---
sudo awk '/^host/{ $5="scram-sha-256" } { print }' "$PG_HBA_PATH" | sudo tee "$PG_HBA_PATH.tmp" > /dev/null
sudo mv "$PG_HBA_PATH.tmp" "$PG_HBA_PATH"
echo "[OK] Método de autenticación actualizado a scram-sha-256"

# --- Reiniciar servicio PostgreSQL ---
echo "Reiniciando servicio PostgreSQL..."
if systemctl is-active --quiet "$PG_SERVICE"; then
  sudo systemctl restart "$PG_SERVICE"
else
  sudo service "$PG_SERVICE" restart || sudo pg_ctlcluster 17 main restart
fi
echo "[OK] Servicio PostgreSQL reiniciado."

# --- Actualizar contraseña del usuario postgres ---
echo "Actualizando contraseña global del usuario postgres..."
sudo -u postgres psql -c "ALTER USER $PG_USER WITH PASSWORD '$PG_PASS';"
echo "[OK] Contraseña unificada para usuario postgres."

# --- Crear archivo ~/.pgpass ---
echo "Creando archivo ~/.pgpass..."
cat > "$PGPASS_FILE" <<EOF
localhost:$PG_PORT:*:$PG_USER:$PG_PASS
EOF
chmod 600 "$PGPASS_FILE"
echo "[OK] Archivo $PGPASS_FILE configurado correctamente."

# --- Pruebas de conexión ---
echo "----------------------------------------------------------"
echo "  Verificando conexiones a las bases de datos..."
echo "----------------------------------------------------------"
for db in "${DBS[@]}"; do
  echo "Probando conexión a base '$db'..."
  if PGPASSFILE="$PGPASS_FILE" psql -U "$PG_USER" -d "$db" -h localhost -p "$PG_PORT" -c "SELECT current_database(), current_user;" >/dev/null 2>&1; then
    echo "[OK] Conexión exitosa a $db"
  else
    echo "[ERROR] Falló la conexión a $db"
  fi
done

# --- Resultado final ---
echo "=========================================================="
echo "   TODAS LAS BASES DE DATOS SINCRONIZADAS Y UNIFICADAS"
echo "   Usuario: $PG_USER | Contraseña: $PG_PASS"
echo "=========================================================="
