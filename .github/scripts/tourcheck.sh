#!/usr/bin/env bash
# The help tour itself: a few pages, caught mid-animation.
set -u
OUT=store; mkdir -p "$OUT"
P=com.reverender.vhslauncher
adb shell wm size 1080x1920; adb shell wm density 420; sleep 3
W=1080; H=1920
adb install -r -g app/build/outputs/apk/play/release/*.apk > "$OUT/install.txt" 2>&1
adb shell settings put global animator_duration_scale 1
cap() { timeout 20 adb exec-out screencap -p > "$OUT/$1.png"; echo "shot $1"; }
adb shell am start -n "$P/com.reverender.r95.MainActivity" --ei r95_vhs_tour 1 --ei r95_vhs_crt 1 >/dev/null; sleep 12
cap c01
for i in 2 3 4 5 6; do
  adb shell input tap $((W*86/100)) $((H*92/100)); sleep 0.9; cap c0${i}a; sleep 1.3; cap c0${i}b; sleep 1.3; cap c0${i}c
done
adb shell screenrecord --time-limit 10 /sdcard/tour.mp4 & R=$!
sleep 10.5; wait $R 2>/dev/null; adb pull /sdcard/tour.mp4 "$OUT/tour.mp4" >/dev/null 2>&1
echo "pid: $(adb shell pidof $P)"
adb logcat -d | grep -E "FATAL|AndroidRuntime" | tail -20
ls -la "$OUT"
