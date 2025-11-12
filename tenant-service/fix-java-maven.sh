#!/usr/bin/env bash
# ------------------------------------------------------------------
# Archivo: fix-java-maven.sh
# Propósito: Configurar JAVA_HOME y compilar el proyecto Spring Boot en Git Bash
# Autor: José Alfredo Martínez Valdés
# Fecha: 2025-11-03
# ------------------------------------------------------------------

# 🌍 Detectar ruta real de Java (buscar en el PATH)
JAVA_BIN_PATH=$(which java | sed 's#/bin/java##')
if [ -z "$JAVA_BIN_PATH" ]; then
  echo "❌ No se encontró Java en el PATH. Verifica tu instalación."
  exit 1
fi

export JAVA_HOME="$JAVA_BIN_PATH"
export PATH="$JAVA_HOME/bin:$PATH"

# Mostrar versión detectada
echo "-----------------------------------------"
echo "🧠 JAVA_HOME detectado en:"
echo "   $JAVA_HOME"
echo "-----------------------------------------"
java -version

# 🚀 Detectar Maven
MAVEN_PATH="/c/Program Files/apache-maven-3.9.11"
if [ -d "$MAVEN_PATH" ]; then
  export PATH="${MAVEN_PATH// /\\ }/bin:$PATH"
else
  echo "⚠️ No se encontró Maven en la ruta esperada: $MAVEN_PATH"
  echo "   Si Maven está instalado en otro lugar, modifica esta variable."
fi

echo "-----------------------------------------"
echo "🧱 Versión de Maven:"
mvn -v
echo "-----------------------------------------"

# 📦 Ir al módulo tenant-service
cd ~/OneDrive/Documentos/TEAC2025-26/Reconstruccion_19102025/inclusive-learning-platform-backend/tenant-service || exit

# 🔧 Compilar el proyecto
echo "🔨 Ejecutando 'mvn clean package -DskipTests'..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
  echo "✅ Compilación completada correctamente."
else
  echo "❌ Error durante la compilación. Revisa el log anterior."
fi
