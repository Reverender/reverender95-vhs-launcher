#!/usr/bin/env bash
# RETRO_EMU_95: search the Internet Archive, load the first result, catch Java and native crashes.
set -u
OUT=emu; mkdir -p "$OUT"
PKG=com.reverender.r95
adb install -r -g app/build/outputs/apk/standard/debug/app-standard-debug.apk > "$OUT/install.txt" 2>&1
shot() { adb exec-out screencap -p > "$OUT/$1.png" 2>/dev/null || true; }
for q in "tetris" "zelda gba" "donkey kong nes" "pokemon gbc" "mario n64" "street fighter snes"; do
  tag=$(echo "$q" | tr ' ' '_')
  adb logcat -c
  adb shell am start -S -n "$PKG/.MainActivity" --ez r95_skip_boot true --es r95_open none > /dev/null 2>&1
  sleep 8
  adb shell am start -n "$PKG/.MainActivity" --es r95_emu_search "'$q'" > /dev/null 2>&1
  result="TIMEOUT"
  for i in $(seq 1 80); do
    sleep 4
    L=$(adb logcat -d)
    if echo "$L" | grep -q "FATAL EXCEPTION\|Fatal signal"; then result="CRASH"; break; fi
    if ! adb shell pidof $PKG > /dev/null; then result="PROCESS DIED"; break; fi
    if echo "$L" | grep -q "emu: running"; then sleep 12; L=$(adb logcat -d); if echo "$L" | grep -q "FATAL EXCEPTION\|Fatal signal"; then result="CRASH AFTER START"; elif adb shell pidof $PKG > /dev/null; then result="RUNNING"; else result="DIED AFTER START"; fi; break; fi
    if echo "$L" | grep -q "emu: rejected\|emu: download failed\|search done, 0 results"; then result="REFUSED (handled)"; break; fi
  done
  shot "$tag"
  echo "== $q: $result"
  adb logcat -d | grep -E " r95 |r95  " | grep -i "emu" | head -20
  adb logcat -d | grep -A30 "FATAL EXCEPTION" | head -50
  adb logcat -d | grep -B2 -A25 "Fatal signal" | head -60
  adb logcat -d | grep -E "DEBUG|libretro|LibretroDroid" | grep -iE "backtrace|#0[0-9]|abort|signal|error" | head -30
  echo "$q: $result" >> "$OUT/summary.txt"
done
echo "== summary"; cat "$OUT/summary.txt"
