#!/usr/bin/env bash
# =====================================================================
# File: free-port.sh
# Location: /c/temp/ilp-rebuild/inclusive-learning-platform/
# Author: Inclusive Learning Platform Automation
# Description:
#   Detects and frees network ports blocked by other processes
#   (use --force to kill them automatically, ideal for CI/CD pipelines)
# =====================================================================

# Default ports to check
PORTS=(8761 8888 8080 8081 8082 5432)

# Detect if --force is passed
FORCE=false
if [[ "$1" == "--force" ]]; then
  FORCE=true
  shift
fi

# If user passed custom ports, override defaults
if [[ $# -gt 0 ]]; then
  PORTS=("$@")
fi

echo "🔍 Checking ports: ${PORTS[*]}"
echo "-----------------------------------------------------"

for port in "${PORTS[@]}"; do
  # Find processes using the port
  PIDS=$(sudo lsof -t -i:"$port" 2>/dev/null)

  if [[ -n "$PIDS" ]]; then
    echo -e "\n⚠️  Port $port is in use by:"
    sudo lsof -i:"$port" | awk 'NR==1 || /LISTEN/'

    for pid in $PIDS; do
      PROC_NAME=$(ps -p "$pid" -o comm=)
      if [[ "$FORCE" == true ]]; then
        sudo kill -9 "$pid" && \
          echo "✅ Port $port freed (PID $pid: $PROC_NAME stopped)" || \
          echo "❌ Could not terminate process $pid"
      else
        read -rp "❓ Terminate process $pid ($PROC_NAME) to free port $port? [y/N]: " answer
        if [[ "$answer" =~ ^[Yy]$ ]]; then
          sudo kill -9 "$pid" && \
            echo "✅ Port $port freed (PID $pid stopped)"
        else
          echo "⏸️ Skipped port $port (still in use)"
        fi
      fi
    done
  else
    echo "✅ Port $port is free"
  fi
done

echo -e "\n✨ Port check and cleanup completed."
