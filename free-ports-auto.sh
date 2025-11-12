#!/usr/bin/env bash
# =====================================================================
# File: free-ports-auto
# Location: inclusive-learning-platform/
# Author: Inclusive Learning Platform Automation
# Description:
#   Cross-platform DevOps script to free occupied ports.
#   Detects Windows / Linux / WSL and Docker containers.
#   Supports interactive and force modes (--force).
# =====================================================================

# Detect OS
OS=$(uname | tr '[:upper:]' '[:lower:]')
ARGS=()
FORCE_MODE=false
PORTS=()

# Parse arguments
for arg in "$@"; do
  if [[ "$arg" == "--force" || "$arg" == "-f" ]]; then
    FORCE_MODE=true
  else
    PORTS+=("$arg")
  fi
done

# Function: stop Docker containers using specified ports
check_docker_ports() {
  if command -v docker &>/dev/null; then
    echo "🐳 Checking Docker containers using specified ports..."
    for port in "${PORTS[@]}"; do
      CONTAINERS=$(docker ps --format "{{.ID}} {{.Ports}}" | grep -E ":${port}->" | awk '{print $1}')
      if [[ -n "$CONTAINERS" ]]; then
        echo "⚠️  Port $port is in use by Docker container(s):"
        echo "$CONTAINERS"
        for cid in $CONTAINERS; do
          if $FORCE_MODE; then
            echo "🛑 Stopping container $cid..."
            docker stop "$cid" >/dev/null && echo "✅ Container $cid stopped."
          else
            read -p "❓ Stop container $cid using port $port? (y/n): " choice
            [[ "$choice" == "y" || "$choice" == "Y" ]] && docker stop "$cid" >/dev/null && echo "✅ Container $cid stopped."
          fi
        done
      else
        echo "✅ Port $port not used by any Docker container."
      fi
    done
  else
    echo "⚠️ Docker not detected on this system."
  fi
}

# Function: free ports in Windows or Linux
free_ports_system() {
  echo "🧠 Detecting and freeing system ports..."
  for port in "${PORTS[@]}"; do
    if [[ "$OS" == *"mingw"* || "$OS" == *"cygwin"* || "$OS" == *"msys"* ]]; then
      # Windows
      PID=$(netstat -ano | findstr ":$port" | findstr "LISTENING" | awk '{print $5}' | sort -u)
      if [[ -n "$PID" ]]; then
        echo "⚠️ Port $port in use by PID(s): $PID"
        for p in $PID; do
          if $FORCE_MODE; then
            taskkill /PID "$p" /F >/nul 2>&1
            echo "✅ Killed process PID $p on port $port"
          else
            read -p "❓ Kill process PID $p on port $port? (y/n): " choice
            [[ "$choice" == "y" || "$choice" == "Y" ]] && taskkill /PID "$p" /F >/nul 2>&1 && echo "✅ Killed process PID $p"
          fi
        done
      else
        echo "✅ Port $port appears free on system."
      fi
    else
      # Linux / macOS
      PID=$(lsof -ti:"$port")
      if [[ -n "$PID" ]]; then
        echo "⚠️ Port $port in use by PID(s): $PID"
        for p in $PID; do
          if $FORCE_MODE; then
            kill -9 "$p" >/dev/null 2>&1
            echo "✅ Killed process PID $p on port $port"
          else
            read -p "❓ Kill process PID $p on port $port? (y/n): " choice
            [[ "$choice" == "y" || "$choice" == "Y" ]] && kill -9 "$p" >/dev/null 2>&1 && echo "✅ Killed process PID $p"
          fi
        done
      else
        echo "✅ Port $port appears free on system."
      fi
    fi
  done
}

# --- Main execution ---
echo "🌍 Environment: $OS"
echo "🎯 Ports to check: ${PORTS[*]:-None}"
echo "⚙️ Force mode: $FORCE_MODE"
echo "-----------------------------------------------------"

if [[ ${#PORTS[@]} -eq 0 ]]; then
  echo "❌ No ports specified."
  echo "Usage: ./free-ports-auto [--force|-f] <port1> <port2> ..."
  exit 1
fi

check_docker_ports
free_ports_system

echo "✅ Operation complete."
