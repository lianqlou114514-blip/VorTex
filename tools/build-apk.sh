#!/usr/bin/env bash
# language: Bash, file: tools/build-apk.sh
# VorTex 1.0 — reproducible APK build without Gradle or network dependencies.
# Requires: JDK 17, Android SDK platform-35 and build-tools 35.0.0.
# Usage: ANDROID_HOME=/path/to/android-sdk bash tools/build-apk.sh
set -uo pipefail
: "${ANDROID_HOME:?set ANDROID_HOME to your Android SDK}"
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
BT="$ANDROID_HOME/build-tools/35.0.0"
AJAR="$ANDROID_HOME/platforms/android-35/android.jar"
OUT="$ROOT/build"
APK="$ROOT/releases/VorTex-1.0.apk"
JDK=${JAVA_HOME:-$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")}
for f in "$BT/aapt2" "$BT/d8" "$BT/zipalign" "$BT/apksigner" "$AJAR"; do
  [ -f "$f" ] || { echo "missing: $f"; exit 1; }
done
rm -rf "$OUT"; mkdir -p "$OUT/flat" "$OUT/gen" "$OUT/classes" "$OUT/dex" "$(dirname "$APK")"
"$BT/aapt2" compile --dir "$ROOT/app/src/main/res" -o "$OUT/flat/res.zip" || exit 1
"$BT/aapt2" link -o "$OUT/base.apk" -I "$AJAR" \
  --manifest "$ROOT/app/src/main/AndroidManifest.xml" --java "$OUT/gen" \
  --min-sdk-version 26 --target-sdk-version 34 --version-code 1 --version-name 1.0 \
  --auto-add-overlay "$OUT/flat/res.zip" || exit 1
find "$ROOT/app/src/main/java" -name '*.java' > "$OUT/sources.txt"
find "$OUT/gen" -name 'R.java' >> "$OUT/sources.txt"
"$JDK/bin/javac" -source 17 -target 17 -encoding UTF-8 -nowarn -Xlint:-options \
  -classpath "$AJAR" -d "$OUT/classes" @"$OUT/sources.txt" || exit 1
CLASSES=$(find "$OUT/classes" -name '*.class' | tr '\n' ' ')
"$BT/d8" --lib "$AJAR" --min-api 26 --output "$OUT/dex" $CLASSES || exit 1
cp "$OUT/base.apk" "$OUT/unsigned.apk"
(cd "$OUT/dex" && zip -q "$OUT/unsigned.apk" classes.dex)
"$BT/zipalign" -f -p 4 "$OUT/unsigned.apk" "$OUT/aligned.apk" || exit 1
"$BT/apksigner" sign --ks "$ROOT/app/app-release.jks" --ks-key-alias 'AIDE+' \
  --ks-pass pass:123789456 --key-pass pass:123789456 --min-sdk-version 26 \
  --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true \
  --out "$APK" "$OUT/aligned.apk" || exit 1
"$BT/apksigner" verify --print-certs "$APK"
echo "APK -> $APK"
