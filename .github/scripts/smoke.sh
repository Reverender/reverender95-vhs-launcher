#!/usr/bin/env bash
# Installs the debug APK, opens each app window and screenshots it. Crashes are logged.
set -u
API="$1"
OUT=smoke
mkdir -p "$OUT"
APK=app/build/outputs/apk/standard/debug/app-standard-debug.apk
PKG=com.reverender.r95

if [ "$API" -ge 23 ]; then adb install -r -g "$APK" > "$OUT/install.txt" 2>&1; else adb install -r "$APK" > "$OUT/install.txt" 2>&1; fi
if ! adb shell pm list packages | grep -q "$PKG"; then echo "INSTALL FAILED on API $API" > "$OUT/crashes.txt"; cat "$OUT/install.txt" >> "$OUT/crashes.txt"; exit 0; fi
adb logcat -c

shot() { adb exec-out screencap -p > "$OUT/$1.png" 2>/dev/null || true; }

# 1) normal start with the boot screen, then the desktop
adb shell am start -S -n "$PKG/.MainActivity" > /dev/null
sleep 4; shot 00-boot
sleep 6; shot 01-desktop

APPS="player eq fx cassette video revy plasma lissajous matrix vumeter cybergrid tunnel hypercube waterfall ribbon orbital fractal prism sonar magnetic kaleido starforge drum909 synth95 emulator vectordreams vaporpet calc notepad sticky planner todo focus clock browser customize settings"
for app in $APPS; do
  adb logcat -c
  adb shell am start -S -n "$PKG/.MainActivity" --es r95_open "$app" --ez r95_skip_boot true > /dev/null
  for w in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18; do
    sleep 1
    adb logcat -d | grep -q "rendered window $app" && break
  done
  sleep 3
  shot "app-$app"
  adb logcat -d | grep -q "rendered window $app" && echo "OK $app" >> "$OUT/rendered.txt" || echo "NOT RENDERED $app" >> "$OUT/rendered.txt"
  if adb logcat -d | grep -q "FATAL EXCEPTION"; then
    echo "CRASH while opening $app" >> "$OUT/crashes.txt"
    adb logcat -d | grep -A40 "FATAL EXCEPTION" >> "$OUT/crashes.txt"
    adb logcat -c
  fi
done

# 2) every website wallpaper on the bare desktop
for wp in 0 1 2 3 4 5 6 7 8 24 25 26 27 28 13 14 15 16 17 18 19 20 21 22 23 9 10 11 12; do
  adb logcat -c
  adb shell am start -S -n "$PKG/.MainActivity" --es r95_open none --ez r95_skip_boot true --ei r95_wallpaper $wp > /dev/null
  sleep 6
  shot "wall-$wp"
  if adb logcat -d | grep -q "FATAL EXCEPTION\|frame failed"; then
    echo "WALLPAPER $wp problem" >> "$OUT/crashes.txt"; adb logcat -d | grep -A30 "FATAL EXCEPTION\|frame failed" | head -60 >> "$OUT/crashes.txt"
  fi
done
adb shell am start -S -n "$PKG/.MainActivity" --ez r95_skip_boot true --ei r95_wallpaper 29 > /dev/null; sleep 2

# 3) play music from the deck and look at a visualizer with sound running
adb shell am start -S -n "$PKG/.MainActivity" --es r95_open "player" --ez r95_skip_boot true > /dev/null
sleep 6
adb logcat -d > "$OUT/logcat-full.txt"
adb logcat -d | grep -E "AndroidRuntime|FATAL|r95|LibretroDroid" > "$OUT/logcat-filtered.txt" || true
cat "$OUT/rendered.txt" >> "$OUT/crashes.txt" 2>/dev/null
[ -f "$OUT/crashes.txt" ] || echo "No crashes on API $API" > "$OUT/crashes.txt"
echo "done"
