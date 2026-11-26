#!/usr/bin/env bash
# 1 GB RAM run: memory (PSS), frame timing (gfxinfo), low-memory kills and crashes.
set -u
OUT=lowram; mkdir -p "$OUT"
PKG=com.reverender.r95
adb install -r -g app/build/outputs/apk/standard/debug/app-standard-debug.apk > "$OUT/install.txt" 2>&1
# The emulator image keeps 2 GB; lock ~1 GB with incompressible data so the app really runs with ~1 GB.
adb root > /dev/null 2>&1; sleep 3
adb shell "mkdir -p /mnt/hog && mount -t tmpfs -o size=1100m tmpfs /mnt/hog && dd if=/dev/urandom of=/mnt/hog/fill bs=1048576 count=1000" > /dev/null 2>&1 || echo "hog failed"
adb shell swapoff -a > /dev/null 2>&1 || true
echo "== device"; adb shell cat /proc/meminfo | head -3; adb shell getprop ro.config.low_ram
adb logcat -c
shot() { adb exec-out screencap -p > "$OUT/$1.png" 2>/dev/null || true; }
mem() { echo "== meminfo after $1"; adb shell dumpsys meminfo $PKG | grep -E "TOTAL( PSS)?:|TOTAL  |Java Heap|Native Heap|Graphics" | head -6; }
alive() { adb shell pidof $PKG > /dev/null && echo "ALIVE after $1" || echo "!! PROCESS GONE after $1"; }

adb shell am start -n "$PKG/.MainActivity" > /dev/null
sleep 3; shot 00-boot
sleep 12; shot 01-desktop; mem desktop; alive desktop
adb shell dumpsys gfxinfo $PKG reset > /dev/null
sleep 20
echo "== gfxinfo desktop (20 s)"; adb shell dumpsys gfxinfo $PKG | grep -E "Total frames|Janky|50th|90th|95th|99th" | head -8

for app in vectordreams video plasma orbital customize emulator vaporpet; do
  adb shell am start -n "$PKG/.MainActivity" --es r95_open "$app" > /dev/null 2>&1
  sleep 9; shot "app-$app"; mem "$app"; alive "$app"
done
adb shell dumpsys gfxinfo $PKG reset > /dev/null
sleep 20
echo "== gfxinfo with windows open (20 s)"; adb shell dumpsys gfxinfo $PKG | grep -E "Total frames|Janky|50th|90th|95th|99th" | head -8
mem final; alive final
echo "== perf log"; adb logcat -d | grep -E "r95.*perf:" | head -20
echo "== low memory kills"; adb logcat -d | grep -iE "lowmemorykiller|lmkd.*kill|Killing .*$PKG" | head -20
echo "== crashes"; adb logcat -d | grep -A25 "FATAL EXCEPTION" | head -80
adb logcat -d > "$OUT/logcat.txt"
echo "done"
