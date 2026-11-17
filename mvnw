#!/bin/sh
# Maven wrapper sederhana (tipe only-script): mengunduh Maven sesuai .mvn/wrapper/maven-wrapper.properties
set -e
BASE_DIR=$(cd "$(dirname "$0")" && pwd)
PROPS="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
URL=$(sed -n 's/^distributionUrl=//p' "$PROPS" | tr -d '\r')
NAME=$(basename "$URL" -bin.zip)
HOME_DIR="${MAVEN_USER_HOME:-$HOME/.m2}/wrapper/dists/$NAME"
if [ ! -x "$HOME_DIR/$NAME/bin/mvn" ]; then
  mkdir -p "$HOME_DIR"
  TMP="$HOME_DIR/$NAME-bin.zip"
  echo "Downloading $URL" >&2
  if command -v curl >/dev/null 2>&1; then curl -fsSL -o "$TMP" "$URL"; else wget -q -O "$TMP" "$URL"; fi
  if command -v unzip >/dev/null 2>&1; then unzip -q -o "$TMP" -d "$HOME_DIR"; else (cd "$HOME_DIR" && jar xf "$TMP"); fi
  rm -f "$TMP"
  chmod +x "$HOME_DIR/$NAME/bin/mvn"
fi
export MAVEN_PROJECTBASEDIR="$BASE_DIR"
exec "$HOME_DIR/$NAME/bin/mvn" "$@"
