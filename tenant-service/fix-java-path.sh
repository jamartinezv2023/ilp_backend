#!/usr/bin/env bash
# ---------------------------------------------------------------
# Archivo: fix-java-path.sh
# Propósito: Configurar JAVA_HOME y Maven correctamente en Git Bash
# Autor: José Alfredo Martínez Valdés
# Fecha: 2025-11-03
# ---------------------------------------------------------------

echo "🔍 Detectando instalación de Java..."

# Detectar java.exe y obtener su ruta
JAVA_BIN=$(cygpath -w "$(which java)")
JAVA_DIR=$(dirname "$(dirname "$JAVA_BIN")")

if [ -z "$JAVA_DIR" ]; then
  echo "❌ No se encontró Java. Asegúrate de tener JDK 17 instalado."
  exit 1
fi

# Establecer JAVA_HOME sin espacios problemáticos (versión segura)
JAVA_HOME=$(cygpath -u "$JAVA_DIR")
export JAVA_HOME

# Añadir Java y Maven al PATH
if [ -d "/c/Program Files/apache-maven-3.9.11" ]; then
  MAVEN_HOME="/c/Program Files/apache-maven-3.9.11"
elif [ -d "/c/Program Files (x86)/apache-maven-3.9.11" ]; then
  MAVEN_HOME="/c/Program Files (x86)/apache-maven-3.9.11"
else
  echo "⚠️ Maven no se encontró en las rutas estándar."
  MAVEN_HOME=""
fi

if [ -n "$MAVEN_HOME" ]; then
  export PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH"
else
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "---------------------------------------------"
echo "✅ JAVA_HOME configurado en:"
echo "   $JAVA_HOME"
echo "---------------------------------------------"
java -version
echo "---------------------------------------------"
mvn -v
echo "---------------------------------------------"

# Ir al módulo tenant-service
PROJECT_DIR="$HOME/OneDrive/Documentos/TEAC2025-26/Reconstruccion_19102025/inclusive-learning-platform-backend/tenant-service"
if [ -d "$PROJECT_DIR" ]; then
  cd "$PROJECT_DIR" || exit
  echo "🚀 Ejecutando compilación (mvn clean package -DskipTests)..."
  mvn clean package -DskipTests
else
  echo "⚠️ No se encontró el directorio tenant-service, omitiendo compilación."
fi
