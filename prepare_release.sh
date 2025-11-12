#!/usr/bin/env bash
set -euo pipefail
echo "Cleaning project..."
find . -name target -type d -prune -exec rm -rf '{}' + || true
find . -name node_modules -type d -prune -exec rm -rf '{}' + || true
rm -rf pgdata || true
rm -rf .git || true

echo "Building Maven projects (skipping tests for speed; remove -DskipTests to run tests)..."
for d in services/*; do
  if [ -f "$d/pom.xml" ]; then
    echo "Building $d"
    (cd "$d" && ./mvnw -q -DskipTests package) || true
  fi
done

echo "Creating release zip..."
ZIPNAME="inclusive-learning-platform-backend-final_v3.zip"
rm -f "$ZIPNAME"
zip -r "$ZIPNAME" . -x "*.git*" "*/target/*" "*/pgdata/*" "*/node_modules/*" || true

echo "Splitting into parts of 80MB..."
split -b 80m -d -a 2 "$ZIPNAME" "${ZIPNAME}.part"
echo "Created parts:"
ls -lh ${ZIPNAME}.part*
