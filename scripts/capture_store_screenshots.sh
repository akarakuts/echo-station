#!/usr/bin/env bash
# Обёртка: скриншоты витрины через scripts/capture_store_screenshots.py
set -euo pipefail
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
exec python3 "$(cd "$(dirname "$0")" && pwd)/capture_store_screenshots.py"
