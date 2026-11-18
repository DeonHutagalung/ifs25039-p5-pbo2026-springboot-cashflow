#!/bin/sh
# Maven wrapper sederhana (only-script): mengunduh Maven sesuai .mvn/wrapper/maven-wrapper.properties
# Pengunduhan memakai curl, wget, atau Java (fallback) sehingga jalan di image Docker minimal.
set -e
BASE_DIR=$(cd "$(dirname "$0")" && pwd)
PROPS="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
URL=$(sed -n 's/^distributionUrl=//p' "$PROPS" | tr -d '\r')
NAME=$(basename "$URL" -bin.zip)
DIST_ROOT="${MAVEN_USER_HOME:-${HOME:-/tmp}/.m2}/wrapper/dists/$NAME"
MVN_BIN="$DIST_ROOT/$NAME/bin/mvn"

download() {
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$2" "$1"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$2" "$1"
  else
    java "$BASE_DIR/.mvn/wrapper/Download.java" "$1" "$2"
  fi
}

if [ ! -x "$MVN_BIN" ]; then
  mkdir -p "$DIST_ROOT"
  ZIP="$DIST_ROOT/$NAME-bin.zip"
  echo "Downloading $URL" >&2
  download "$URL" "$ZIP"
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$ZIP" -d "$DIST_ROOT"
  else
    (cd "$DIST_ROOT" && jar xf "$ZIP")
  fi
  rm -f "$ZIP"
  chmod +x "$MVN_BIN"
fi

export MAVEN_PROJECTBASEDIR="$BASE_DIR"
exec "$MVN_BIN" "$@"