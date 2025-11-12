#!/bin/bash
# 📄 fix-java-path.sh
# Corrige JAVA_HOME para Git Bash en Windows

# Detecta instalación de Java
JDK_DIR=$(ls -d /c/Program*/Eclipse*/jdk-* 2>/dev/null | head -n 1)

if [ -z "$JDK_DIR" ]; then
  echo "❌ No se encontró el JDK. Verifica la ruta de instalación."
  exit 1
fi

# Convierte ruta con espacios a formato corto compatible (8.3)
JDK_SHORT=$(cygpath -u "$(cmd.exe /c "for %A in (\"$JDK_DIR\") do @echo %~sA" 2>/dev/null | tr -d '\r')")

export JAVA_HOME="$JDK_SHORT"
export PATH="$JAVA_HOME/bin:/c/Program Files/apache-maven-3.9.11/bin:$PATH"

echo "✅ JAVA_HOME corregido:"
echo "$JAVA_HOME"
echo

# Verifica
java -version
mvn -v
