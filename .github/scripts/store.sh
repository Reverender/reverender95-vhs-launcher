#!/usr/bin/env bash
# Google Play store material: clean screenshots and short screen recordings of the Play edition.
set -u
OUT=store; mkdir -p "$OUT"
[ "${ONLY:-}" = tour ] && exec bash .github/scripts/tour.sh
P=com.reverender.vhslauncher
adb shell wm size 1080x1920; adb shell wm density 420; sleep 3
W=1080; H=1920
adb install -r -g app/build/outputs/apk/play/release/*.apk > "$OUT/install.txt" 2>&1
adb shell cmd notification allow_listener $P/com.reverender.r95.os.RetroNotificationService
# a clean status bar for the shots (demo mode: full battery, 12:00, no notification icons)
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200 >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 >/dev/null
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false >/dev/null
adb shell settings put global animator_duration_scale 1
adb shell settings put global transition_animation_scale 1
cap() { timeout 20 adb exec-out screencap -p > "$OUT/$1.png"; echo "shot $1"; }
start() { timeout 20 adb shell am force-stop $P; timeout 20 adb shell am start -n "$P/com.reverender.r95.MainActivity" "$@" >/dev/null; }
rec() { adb shell rm -f /sdcard/$1.mp4; adb shell screenrecord --size 1080x1920 --bit-rate 12000000 --time-limit $2 /sdcard/$1.mp4 & RECPID=$!; }
recdone() { wait $RECPID 2>/dev/null; sleep 1; adb pull /sdcard/$1.mp4 "$OUT/$1.mp4" >/dev/null 2>&1; echo "video $1"; }

# ONLY=tail: just the VHS WEB and LOAD parts
if [ "${ONLY:-}" != "tail" ]; then
# 0) first boot: the help tour
start; sleep 14
cap 00-tour-1
rec v0-tour 12
adb shell input tap $((W*86/100)) $((H*90/100)); sleep 2.5
cap 00-tour-2
adb shell input tap $((W*86/100)) $((H*90/100)); sleep 2.5
# SKIP has to be held: try a few heights along the bottom row
for y in 90 88 92; do adb shell input swipe $((W*12/100)) $((H*y/100)) $((W*12/100)) $((H*y/100)) 2000; sleep 1.5; done
recdone v0-tour
cap 00-tour-after

# 1) home screen, radio on, news ticker, tape tower
start --ei r95_vhs_tour 0 --ei r95_vhs_wall 0 --ei r95_vhs_viz 2 --ei r95_vhs_font 0 --es r95_vhs_titles "REVERENDER_VHS" --ei r95_vhs_crt 1 --ei r95_vhs_radio 0 --es r95_vhs_swipe_left vhs:setup --es r95_vhs_swipe_right vhs:web
sleep 22
cap 01-home
rec v1-home 8; sleep 8; recdone v1-home
# drawer
rec v2-drawer 7
adb shell input swipe $((W/2)) $((H*70/100)) $((W/2)) $((H*30/100)) 250; sleep 3
cap 02-drawer
adb shell input keyevent KEYCODE_BACK; sleep 3
recdone v2-drawer
# setup, through the channel-change switch
rec v3-setup 9
adb shell input swipe $((W*85/100)) $((H/2)) $((W*15/100)) $((H/2)) 250; sleep 3
cap 03-setup
adb shell input swipe $((W/2)) $((H*70/100)) $((W/2)) $((H*40/100)) 400; sleep 2
cap 03b-setup-scrolled
adb shell input keyevent KEYCODE_BACK; sleep 3
recdone v3-setup

# 2) other looks: wallpapers, fonts, CRT styles, drawer styles
i=0
for cfg in "1 4 2 2" "2 6 3 3" "3 1 4 5" "4 7 1 6" "5 3 5 4"; do
  set -- $cfg
  start --ei r95_vhs_tour 0 --ei r95_vhs_wall $1 --ei r95_vhs_viz $2 --ei r95_vhs_font $3 --ei r95_vhs_crt $4 --ei r95_vhs_drawer $((i % 4))
  sleep 12; cap 1$i-look-w$1
  rec v4-look$i 4; sleep 4; recdone v4-look$i
  i=$((i+1))
done
# a drawer in another style
start --ei r95_vhs_tour 0 --ei r95_vhs_wall 2 --ei r95_vhs_drawer 2; sleep 10
adb shell input swipe $((W/2)) $((H*70/100)) $((W/2)) $((H*30/100)) 250; sleep 3; cap 20-drawer-teletext
start --ei r95_vhs_tour 0 --ei r95_vhs_wall 1 --ei r95_vhs_drawer 3; sleep 10
adb shell input swipe $((W/2)) $((H*70/100)) $((W/2)) $((H*30/100)) 250; sleep 3; cap 21-drawer-grid

# 3) VHS CHAT (test chat notifications from the shell)
start --ei r95_vhs_tour 0 --ei r95_vhs_wall 0 --es r95_vhs_chat_test com.android.shell --es r95_vhs_swipe_left vhs:chat
sleep 12
adb shell cmd notification post -S messaging --conversation Sam --message Sam:Are_we_still_on_for_tonight --message Sam:I_got_the_tapes chat1 x >/dev/null; sleep 1
adb shell cmd notification post -S messaging --conversation Band --message Kim:Rehearsal_at_eight --message Joe:Bringing_the_synth chat2 y >/dev/null; sleep 3
cap 30-home-notifs
rec v5-chat 8
adb shell input swipe $((W*85/100)) $((H/2)) $((W*15/100)) $((H/2)) 250; sleep 3
cap 31-chat
adb shell input tap $((W/3)) $((H*155/1000)); sleep 3
cap 32-chat-open
recdone v5-chat

fi
# 4) VHS WEB
start --ei r95_vhs_tour 0 --ei r95_vhs_wall 3 --es r95_vhs_swipe_right vhs:web --es r95_vhs_webhome https://en.m.wikipedia.org/wiki/Videocassette_recorder; sleep 10
rec v6-web 9
adb shell input swipe $((W*15/100)) $((H*35/100)) $((W*85/100)) $((H*35/100)) 250; sleep 7
cap 40-web
recdone v6-web

# 5) LOAD (tape / game / music decks)
start --ei r95_vhs_tour 0 --ei r95_vhs_wall 2 --es r95_vhs_swipe_left vhs:load; sleep 10
rec v7-load 8
adb shell input swipe $((W*85/100)) $((H*35/100)) $((W*15/100)) $((H*35/100)) 250; sleep 3
cap 50-load
adb shell input tap $((W/2)) $((H*20/100)); sleep 3
cap 51-load-deck
recdone v7-load
echo "pid: $(adb shell pidof $P)"
adb logcat -d | grep -E "FATAL|AndroidRuntime" | tail -20
ls -la "$OUT"
