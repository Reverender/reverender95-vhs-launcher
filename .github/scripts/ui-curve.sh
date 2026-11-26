#!/bin/bash
# CURVE on Android 13+ (the GPU shader path): screenshots at each level, and any shader errors
OUT=ui; mkdir -p $OUT
VPKG=com.reverender.r95.vhs
adb install -r -g app/build/outputs/apk/vhs/release/*.apk > /dev/null 2>&1
for c in 0 1 2; do
  timeout 20 adb shell am force-stop $VPKG
  timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --ei r95_vhs_curve $c >/dev/null; sleep 12
  timeout 20 adb exec-out screencap -p > "$OUT/C$c-curve-api34.png"
done
echo "pid: $(adb shell pidof $VPKG)"
adb logcat -d | grep -iE "curve shader|FATAL|RuntimeShader" | tail -20
