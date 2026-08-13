#!/usr/bin/env bash
# Проверка паритета ключей strings.xml во всех values* относительно values/.
set -euo pipefail

PROJ_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
EN="$PROJ_ROOT/app/src/main/res/values/strings.xml"

keys() {
  sed -n 's/.*name="\([^"]*\)".*/\1/p' "$1" | sort -u
}

en_file="$(mktemp)"
keys "$EN" >"$en_file"
fail=0

for f in "$PROJ_ROOT"/app/src/main/res/values*/strings.xml; do
  [[ "$f" == "$EN" ]] && continue
  loc_file="$(mktemp)"
  keys "$f" >"$loc_file"
  missing="$(comm -23 "$en_file" "$loc_file" || true)"
  extra="$(comm -13 "$en_file" "$loc_file" || true)"
  if [[ -n "$missing" || -n "$extra" ]]; then
    echo "check_strings_parity: расхождение в $f" >&2
    [[ -n "$missing" ]] && echo "  нет:" >&2 && echo "$missing" >&2
    [[ -n "$extra" ]] && echo "  лишние:" >&2 && echo "$extra" >&2
    fail=1
  fi
  rm -f "$loc_file"
done

count="$(wc -l <"$en_file" | tr -d ' ')"
rm -f "$en_file"
if [[ "$fail" -ne 0 ]]; then
  exit 1
fi
echo "OK: ${count} ключей во всех локалях"
