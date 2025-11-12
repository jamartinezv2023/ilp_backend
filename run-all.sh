#!/bin/bash
# ============================================================
# 📁 inclusive-learning-platform-backend/run-all.sh
# 🚀 Arranca todos los microservicios Spring Boot del monorepo
# ============================================================

# Colores para mensajes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # Sin color

# Ruta base del monorepo
BASE_DIR="$(pwd)"

# Puertos esperados (ajústalos si cambian)
declare -A SERVICES=(
  ["commons-service"]=8090
  ["tenant-service"]=8082
  ["auth-service"]=8081
  ["gateway-service"]=8080
  ["report-service"]=8085
  ["notification-service"]=8086
  ["monitoring-service"]=8087
)

# Función para verificar si un puerto está en uso
is_port_in_use() {
  local port=$1
  (netstat -ano 2>/dev/null | grep -q ":$port") && return 0 || return 1
}

# Limpieza de procesos al salir
cleanup() {
  echo -e "\n${RED}🧹 Deteniendo todos los servicios...${NC}"
  pkill -f "java -jar"
  echo -e "${GREEN}✅ Todos los servicios detenidos.${NC}"
  exit 0
}
trap cleanup SIGINT

# Verificación del entorno
echo -e "${CYAN}🔍 Verificando entorno Java y Maven...${NC}"
java -version || { echo -e "${RED}❌ Java no encontrado. Instálalo antes de continuar.${NC}"; exit 1; }
mvn -v || { echo -e "${RED}❌ Maven no encontrado. Instálalo antes de continuar.${NC}"; exit 1; }

# Compilación global
echo -e "${YELLOW}⚙️  Compilando todos los servicios (sin ejecutar tests)...${NC}"
mvn clean package -DskipTests > /dev/null
echo -e "${GREEN}✅ Compilación completa.${NC}\n"

# Arranque secuencial
for SERVICE in "${!SERVICES[@]}"; do
  PORT=${SERVICES[$SERVICE]}
  SERVICE_PATH="$BASE_DIR/$SERVICE"

  if [ -d "$SERVICE_PATH" ]; then
    echo -e "${CYAN}🚀 Iniciando $SERVICE en puerto $PORT...${NC}"
    cd "$SERVICE_PATH"
    JAR_FILE=$(find target -name "*.jar" | grep -v "original" | head -n 1)

    if [ -f "$JAR_FILE" ]; then
      if is_port_in_use "$PORT"; then
        echo -e "${YELLOW}⚠️  El puerto $PORT ya está en uso. Saltando $SERVICE.${NC}"
      else
        nohup java -jar "$JAR_FILE" > "$SERVICE.log" 2>&1 &
        sleep 5
        echo -e "${GREEN}✅ $SERVICE iniciado correctamente.${NC}"
        echo -e "🌐 Swagger UI: ${CYAN}http://localhost:$PORT/swagger-ui/index.html${NC}\n"
      fi
    else
      echo -e "${RED}❌ No se encontró JAR para $SERVICE.${NC}"
    fi
  else
    echo -e "${YELLOW}⚠️  Directorio $SERVICE no encontrado. Saltando.${NC}"
  fi
done

echo -e "${GREEN}🎉 Todos los servicios han sido iniciados.${NC}"
echo -e "🛑 Usa Ctrl + C para detener todos los procesos.\n"

# Mantener el script activo
while true; do sleep 60; done
