#!/usr/bin/env bash
# Screenshots for the in-app help tour: each step before and after its tap / hold / swipe, CRT effect off.
set -u
OUT=store; mkdir -p "$OUT"
P=com.reverender.vhslauncher
adb shell wm size 1080x1920; adb shell wm density 420; sleep 3
W=1080; H=1920
adb install -r -g app/build/outputs/apk/play/release/*.apk > "$OUT/install.txt" 2>&1
adb shell cmd notification allow_listener $P/com.reverender.r95.os.RetroNotificationService
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200 >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false >/dev/null
cap() { sleep 0.5; timeout 20 adb exec-out screencap -p > "$OUT/$1.png"; echo "shot $1"; }
start() { timeout 20 adb shell am force-stop $P; timeout 20 adb shell am start -n "$P/com.reverender.r95.MainActivity" --ei r95_vhs_tour 0 --ei r95_vhs_crt 0 --ei r95_vhs_wall 0 --ei r95_vhs_font 0 --ei r95_vhs_drawer 0 "$@" >/dev/null; sleep 10; }
tap() { adb shell input tap $(($1*W/1000)) $(($2*H/1000)); sleep ${3:-2.5}; }
hold() { adb shell input swipe $(($1*W/1000)) $(($2*H/1000)) $(($1*W/1000)) $(($2*H/1000)) 1500; sleep ${3:-2.5}; }
swipe() { adb shell input swipe $(($1*W/1000)) $(($2*H/1000)) $(($3*W/1000)) $(($4*H/1000)) 250; sleep ${5:-2.5}; }
back() { adb shell input keyevent KEYCODE_BACK; sleep 2; }
grid() { cp "$OUT/$1.png" "$OUT/$1.png" 2>/dev/null; }

# chat messages first (they show in the notification counter, the shade and VHS CHAT)
start --es r95_vhs_chat_test com.android.shell --es r95_vhs_swipe_left vhs:chat --es r95_vhs_swipe_right vhs:web --es r95_vhs_webhome https://en.m.wikipedia.org/wiki/Videocassette_recorder --es r95_vhs_titles "MY_VCR~"
adb shell cmd notification post -S messaging --conversation Sam --message Sam:Are_we_still_on_for_tonight --message Sam:I_got_the_tapes chat1 x >/dev/null; sleep 1
adb shell cmd notification post -S messaging --conversation Band --message Kim:Rehearsal_at_eight --message Joe:Bringing_the_synth chat2 y >/dev/null; sleep 4
cap t01-home
# app drawer, hold an app
swipe 500 700 500 300; cap t02-drawer
hold 200 370; cap t03-drawer-hold
back; back
# search: tap the APPS field
start; tap 150 180; cap t04-kb
back; back
# Android's notification shade
start; swipe 500 20 500 600; cap t05-shade
back
# SETUP from the help bar, and a tap on CRT
start; tap 500 950; cap t06-setup
tap 200 190; cap t07-setup-crt
back
# hold the clock
start; hold 760 580 4; cap t08-clock
# radio: pick a station, press PLAY
start; tap 600 210; cap t09-station
tap 90 580 9; cap t10-playing
# EQ and the playlist
tap 50 760; cap t11-eq
back
start; tap 120 820; cap t12-playlist
back
# LOAD: video, games, music decks
start; tap 880 180; cap t13-load
tap 440 150; cap t14-games
tap 680 150; cap t15-music
back
# sideways swipes: VHS CHAT one way, VHS WEB the other
start; swipe 850 400 150 400; cap t16-chat
tap 330 160; cap t17-chat-open
back
start; swipe 150 400 850 400 8; cap t18-web
back
echo "pid: $(adb shell pidof $P)"
adb logcat -d | grep -E "FATAL|AndroidRuntime" | tail -20
ls -la "$OUT"
