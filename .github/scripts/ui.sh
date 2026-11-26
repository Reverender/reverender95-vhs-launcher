#!/usr/bin/env bash
# UI check of the VHS launcher APK on an emulator.
set -u
OUT=ui; mkdir -p "$OUT"
SIZE=$(adb shell wm size | grep -o '[0-9]*x[0-9]*' | tail -1); W=${SIZE%x*}; H=${SIZE#*x}; echo "screen $W x $H"
: > "$OUT/install.txt"
# ---- VHS+ (notification area): grant access, post test notifications, check the title bar
NPKG=com.reverender.r95.vhs.notif
adb install -r -g app/build/outputs/apk/vhsnotif/release/*.apk >> "$OUT/install.txt" 2>&1
timeout 20 adb shell cmd notification allow_listener $NPKG/com.reverender.r95.os.RetroNotificationService
timeout 20 adb shell am start -n "$NPKG/com.reverender.r95.MainActivity" >/dev/null; sleep 15
timeout 20 adb exec-out screencap -p > "$OUT/N0-none.png"
timeout 20 adb shell cmd notification post -S bigtext -t Hello t1 First_message >/dev/null; sleep 0.8
timeout 20 adb exec-out screencap -p > "$OUT/N1-flash.png"; sleep 4
timeout 20 adb shell cmd notification post -S bigtext -t Again t2 Second_message >/dev/null; sleep 4
timeout 20 adb exec-out screencap -p > "$OUT/N2-count.png"
timeout 20 adb shell cmd notification post -S bigtext -t More t3 Third >/dev/null; sleep 4
timeout 20 adb shell cmd notification cancel t1 >/dev/null; sleep 2
timeout 20 adb exec-out screencap -p > "$OUT/N3-after-cancel.png"
# VHS CHAT: messaging notifications (from the shell, standing in for WhatsApp), the swipe-left target
timeout 20 adb shell am force-stop $NPKG
timeout 20 adb shell am start -n "$NPKG/com.reverender.r95.MainActivity" --es r95_vhs_chat_test com.android.shell --es r95_vhs_swipe_left vhs:chat --ei r95_vhs_switch_ms 4000 >/dev/null; sleep 12
timeout 20 adb shell cmd notification post -S messaging --conversation Mama --message Mama:Hoi_schat --message Mama:Kom_je_zondag_eten chat1 x >/dev/null; sleep 1
timeout 20 adb shell cmd notification post -S messaging --conversation Band_chat --message Kim:Repetitie_om_acht --message Joe:Ik_neem_de_synth_mee chat2 y >/dev/null; sleep 2
adb shell input swipe $((W*85/100)) $((H/2)) $((W*15/100)) $((H/2)) 250; sleep 3
timeout 20 adb exec-out screencap -p > "$OUT/N4-chats.png"
adb logcat -d | grep "r95 *: swipe" | tail -5
# swipe the first chat away
adb shell input swipe $((W*20/100)) $((H*155/1000)) $((W*90/100)) $((H*155/1000)) 250; sleep 2
timeout 20 adb exec-out screencap -p > "$OUT/N4b-chat-dismissed.png"
adb shell input tap $((W/3)) $((H*155/1000)); sleep 3
timeout 20 adb exec-out screencap -p > "$OUT/N5-chat.png"
# type a reply: the keyboard comes up and the reply line moves above it
adb shell input tap $((W/3)) $((H*88/100)); sleep 2
adb shell input text "Hallo%sdaar%sdit%sis%seen%slanger%sbericht"; sleep 2
timeout 20 adb exec-out screencap -p > "$OUT/N6-typing.png"
adb shell input keyevent KEYCODE_BACK; sleep 1
# menu switch effect: back to the list, then back to the home screen, caught mid-switch
# (the emulator runs with animations off, which would skip it)
adb shell settings put global animator_duration_scale 1; sleep 1
adb shell input keyevent KEYCODE_BACK; sleep 1.5
adb shell input keyevent KEYCODE_BACK; sleep 0.3
timeout 20 adb exec-out screencap -p > "$OUT/S1-switch.png"; sleep 5
adb shell input tap $((W/2)) $((H-22)); sleep 1.4
timeout 20 adb exec-out screencap -p > "$OUT/S2-switch.png"; sleep 5
timeout 20 adb exec-out screencap -p > "$OUT/S3-settled.png"
adb shell settings put global animator_duration_scale 0
# fonts and the VFD DECK visualizer, and the CRT styles' look (one screenshot each)
for k in 1 2 3 6 7; do
  timeout 20 adb shell am force-stop $NPKG
  timeout 20 adb shell am start -n "$NPKG/com.reverender.r95.MainActivity" --ei r95_vhs_font $k --ei r95_vhs_viz 6 >/dev/null; sleep 6
  timeout 20 adb exec-out screencap -p > "$OUT/F$k-font.png"
done
timeout 20 adb shell am start -n "$NPKG/com.reverender.r95.MainActivity" --ei r95_vhs_font 0 >/dev/null; sleep 2
echo "notif pid: $(adb shell pidof $NPKG)"; adb logcat -d | grep -E "FATAL|AndroidRuntime" | tail -10
timeout 20 adb shell am force-stop $NPKG; timeout 20 adb uninstall $NPKG >/dev/null
[ "$ONLY" = notif ] && exit 0
# ---- the VHS-only APK (R8-stripped release)
VHS_WALL_Y=${VHS_WALL_Y:-433}; VHS_DECKSW_Y=${VHS_DECKSW_Y:-108}
VPKG=com.reverender.r95.vhs
adb install -r -g app/build/outputs/apk/vhs/release/*.apk >> "$OUT/install.txt" 2>&1
adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" > /dev/null; sleep 15
# ---- lifecycle check: the visualizer and the CRT must keep running after another app and after lock / unlock
adb logcat -c
adb exec-out screencap -p > "$OUT/L1-start.png"; sleep 1
adb exec-out screencap -p > "$OUT/L1b-start.png"
timeout 20 adb shell am start -a android.settings.SETTINGS >/dev/null; sleep 4
timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" >/dev/null; sleep 5
adb exec-out screencap -p > "$OUT/L2-after-app.png"; sleep 1
adb exec-out screencap -p > "$OUT/L2b-after-app.png"
timeout 10 adb shell input keyevent KEYCODE_SLEEP; sleep 6
timeout 10 adb shell input keyevent KEYCODE_WAKEUP; sleep 1; timeout 10 adb shell input keyevent 82; sleep 1; timeout 10 adb shell wm dismiss-keyguard; sleep 5
adb exec-out screencap -p > "$OUT/L3-after-lock.png"; sleep 1
adb exec-out screencap -p > "$OUT/L3b-after-lock.png"
timeout 10 adb shell input keyevent KEYCODE_SLEEP; sleep 20
timeout 10 adb shell input keyevent KEYCODE_WAKEUP; sleep 1; timeout 10 adb shell input keyevent 82; sleep 1; timeout 10 adb shell wm dismiss-keyguard; sleep 5
adb exec-out screencap -p > "$OUT/L4-after-long-lock.png"
for wi in 0 1 2; do
  timeout 20 adb shell am force-stop $VPKG
  timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --ei r95_vhs_wall $wi --ei r95_vhs_viz $((wi % 9)) --ei r95_vhs_font $((wi % 7)) --ef r95_vhs_uiblur $([ $wi = 3 ] && echo 0.8 || echo 0) >/dev/null; sleep 6
  adb exec-out screencap -p > "$OUT/W$wi-wall.png"
done
# taps on the right side must stay in the launcher; holding the clock opens the clock app
timeout 20 adb shell am force-stop $VPKG; timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" >/dev/null; sleep 6
timeout 10 adb shell input tap $((W*90/100)) $((H*30/100)); sleep 2
echo "after right tap: $(timeout 10 adb shell dumpsys window | grep -m1 mCurrentFocus)"
timeout 10 adb shell input swipe $((W*78/100)) $((H*55/100)) $((W*78/100)) $((H*55/100)) 900; sleep 3
echo "after clock hold: $(timeout 10 adb shell dumpsys window | grep -m1 mCurrentFocus)"; adb logcat -d | grep "r95 *: clock" | tail -2
# wide screen: rotate to landscape — home menu + LOAD side by side, nothing cut off
timeout 10 adb shell settings put system accelerometer_rotation 0
timeout 10 adb shell settings put system user_rotation 1; sleep 5
adb exec-out screencap -p > "$OUT/R1-landscape.png"
timeout 10 adb shell input tap $((H*75/100)) $((W*95/100)); sleep 2   # (landscape) SETUP on the home side
adb exec-out screencap -p > "$OUT/R1b-landscape-setup.png"
timeout 10 adb shell input keyevent KEYCODE_BACK; sleep 1
timeout 10 adb shell settings put system user_rotation 0; sleep 4
adb exec-out screencap -p > "$OUT/R2-portrait-again.png"
echo "== lifecycle log"; adb logcat -d | grep -E -A8 "r95 *: (viz|life|power)|FATAL" | grep -v "viz:" | tail -60
adb exec-out screencap -p > "$OUT/4-vhsonly.png"
# swipe up: the apps drawer fades in; swipe it sideways: it fades out
adb shell input swipe $((W/2)) $((H*65/100)) $((W/2)) $((H*30/100)) 250; sleep 0.12
adb exec-out screencap -p > "$OUT/4a-drawer-fading.png"; sleep 2
adb exec-out screencap -p > "$OUT/4b-drawer.png"
adb shell input swipe $((W*30/100)) $((H*50/100)) $((W*85/100)) $((H*50/100)) 250; sleep 2
adb exec-out screencap -p > "$OUT/4c-drawer-swiped-away.png"
adb shell input swipe $((W/2)) $((H*65/100)) $((W/2)) $((H*30/100)) 250; sleep 2
adb shell input tap $((W*50/100)) $((H*96/100)); sleep 2   # outside the box
adb exec-out screencap -p > "$OUT/4d-drawer-tapped-out.png"
# SETUP
adb shell input tap $((W/2)) $((H-22)); sleep 3
adb exec-out screencap -p > "$OUT/5-vhsonly-setup.png"
# WALL is the 7th item: find it on the screenshot run; tap a few rows down from the header
for y in 0 1; do adb shell input tap $((W/3)) $((H*VHS_WALL_Y/1000)); sleep 1; done
adb exec-out screencap -p > "$OUT/5b-setup-wall.png"
adb shell input keyevent KEYCODE_BACK; sleep 3
adb exec-out screencap -p > "$OUT/5c-main-wall.png"
# LOAD deck (hold LOAD) → VIDEO / GAMES switch → GAMES
adb shell input swipe $((W*88/100)) $((H*162/1000)) $((W*88/100)) $((H*162/1000)) 1600; sleep 3
adb exec-out screencap -p > "$OUT/6-vhsonly-deck.png"
adb shell input tap $((W*30/100)) $((H*VHS_DECKSW_Y/1000)); sleep 3
adb exec-out screencap -p > "$OUT/6b-vhsonly-games.png"
adb shell input keyevent KEYCODE_BACK; sleep 2
adb exec-out screencap -p > "$OUT/6c-vhsonly-after-back.png"
# GAME DECK: load a tiny homemade NES ROM (an endless loop) through the test hook, check the core runs
python3 - <<'PY'
prg = bytearray([0xEA] * 0x4000)
prg[0:3] = bytes([0x4C, 0x00, 0xC0])           # $C000: JMP $C000
for v in (0x3FFA, 0x3FFC, 0x3FFE): prg[v:v+2] = bytes([0x00, 0xC0])  # NMI / RESET / IRQ → $C000
open('/tmp/test.nes', 'wb').write(b'NES\x1a' + bytes([1, 1, 0, 0]) + bytes(8) + bytes(prg) + bytes(0x2000))
PY
adb shell mkdir -p /sdcard/Android/data/$VPKG/files
adb push /tmp/test.nes /sdcard/Android/data/$VPKG/files/test.nes > /dev/null
adb logcat -c
adb shell am force-stop $VPKG; sleep 1
adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --es r95_vhs_rom test.nes > /dev/null; sleep 45
adb exec-out screencap -p > "$OUT/8-vhsonly-game.png"
echo "game pid: $(adb shell pidof $VPKG)"
# back (to the cartridge search) and EJECT must not crash
adb logcat -c
adb shell input keyevent KEYCODE_BACK; sleep 4
adb exec-out screencap -p > "$OUT/8b-game-back.png"
echo "after back pid: $(adb shell pidof $VPKG)"
adb shell input tap $((W*66/100)) $((H*156/1000)); sleep 4   # ◀GAME: back to the running game
adb exec-out screencap -p > "$OUT/8c-game-again.png"
adb shell input tap $((W*12/100)) $((H*975/1000)); sleep 4   # ▲EJECT
adb exec-out screencap -p > "$OUT/8d-game-eject.png"
echo "after eject pid: $(adb shell pidof $VPKG)"
echo "== eject log"; adb logcat -d | grep -E "FATAL|AndroidRuntime|Fatal signal|backtrace|#0[0-9] pc|Abort message|libretrodroid|DEBUG" | grep -v "attach of thread" | tail -40
echo "== after eject audio"; adb shell dumpsys audio | grep -E "state:started" | head -3
# Hardcore Radio (preset 8), then sleep: the stream must keep playing with the screen off
adb shell am force-stop $VPKG; sleep 1; adb logcat -c
adb shell am start -n $VPKG/com.reverender.r95.MainActivity --ei r95_vhs_radio 8 >/dev/null; sleep 25
adb exec-out screencap -p > "$OUT/9-vhs-hardcore.png"
echo "== radio playing?"; adb shell dumpsys media_session | grep -E "state=PlaybackState" | head -2
adb logcat -d | grep -iE "ExoPlayer|PlaybackException|radio|stream|r95 " | grep -v "attach of thread" | tail -15
timeout 10 adb shell input keyevent KEYCODE_SLEEP; sleep 60
echo "== after 60 s asleep"; adb shell dumpsys media_session | grep -E "state=PlaybackState" | head -2
adb shell dumpsys audio | grep -E "state:started" | head -3
timeout 20 adb shell dumpsys activity services $VPKG | grep -E "ServiceRecord|isForeground" | head -4
timeout 10 adb shell input keyevent KEYCODE_WAKEUP; sleep 2; timeout 10 adb shell input keyevent 82 || true; sleep 2
# UI motion blur on: main screen, drawer slide, then off again
adb shell am force-stop $VPKG; sleep 1
adb shell am start -n $VPKG/com.reverender.r95.MainActivity --ef r95_vhs_uiblur 0.7 >/dev/null; sleep 8
adb exec-out screencap -p > "$OUT/10-vhs-uiblur.png"
adb shell input swipe $((W/2)) $((H*65/100)) $((W/2)) $((H*30/100)) 250; sleep 0.15
adb exec-out screencap -p > "$OUT/10b-vhs-uiblur-drawer.png"; sleep 2
adb shell input keyevent KEYCODE_BACK; sleep 2
adb shell input swipe $((W/2)) $((H*70/100)) $((W/2)) $((H*30/100)) 200; sleep 0.1
adb exec-out screencap -p > "$OUT/10c-vhs-uiblur-scroll.png"
echo "uiblur pid: $(adb shell pidof $VPKG)"
# CRT switch-on after the lock screen
timeout 10 adb shell input keyevent KEYCODE_SLEEP; sleep 3
timeout 10 adb shell input keyevent KEYCODE_WAKEUP; timeout 10 adb shell input keyevent 82; sleep 0.12
timeout 25 adb exec-out screencap -p > "$OUT/11-vhs-poweron.png"; sleep 0.15
timeout 25 adb exec-out screencap -p > "$OUT/11b-vhs-poweron.png"; sleep 2
timeout 25 adb exec-out screencap -p > "$OUT/11c-vhs-home.png"
# BACK on the home screen opens the apps drawer, BACK again closes it
timeout 25 adb shell input keyevent KEYCODE_BACK; sleep 1.5
timeout 25 adb exec-out screencap -p > "$OUT/12-vhs-back-drawer.png"
timeout 25 adb shell input keyevent KEYCODE_BACK; sleep 1.5
timeout 25 adb exec-out screencap -p > "$OUT/12b-vhs-back-closed.png"
timeout 25 adb shell input keyevent KEYCODE_BACK; sleep 1.5   # (closes the drawer if it was open)
# hold EQ: the equalizer page
timeout 25 adb shell input swipe $((W*6/100)) $((H*74/100)) $((W*6/100)) $((H*74/100)) 900; sleep 2
timeout 25 adb exec-out screencap -p > "$OUT/13-vhs-eq.png"
timeout 25 adb shell input swipe $((W*30/100)) $((H*30/100)) $((W*30/100)) $((H*20/100)) 300; sleep 1
timeout 25 adb exec-out screencap -p > "$OUT/13b-vhs-eq-drag.png"
timeout 25 adb shell input keyevent KEYCODE_BACK; sleep 1.5
# setup: THEME twice
timeout 25 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" >/dev/null; sleep 3
timeout 25 adb shell input tap $((W/2)) $((H-22)); sleep 1.5
timeout 25 adb exec-out screencap -p > "$OUT/14a-setup.png"
timeout 25 adb shell input tap $((W*40/100)) $((H*12/100)); sleep 1
timeout 25 adb shell input tap $((W*40/100)) $((H*12/100)); sleep 2
timeout 25 adb exec-out screencap -p > "$OUT/14-vhs-theme.png"
timeout 25 adb shell input tap $((W*40/100)) $((H*12/100)); sleep 2
timeout 25 adb exec-out screencap -p > "$OUT/14b-vhs-theme.png"
timeout 25 adb shell input keyevent KEYCODE_BACK; sleep 2
# empty titles: no title bars on the home screen
timeout 20 adb shell am force-stop $VPKG
timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --es r95_vhs_titles "~" >/dev/null; sleep 5
timeout 25 adb exec-out screencap -p > "$OUT/T0-no-titles.png"
timeout 20 adb shell am force-stop $VPKG
timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --es r95_vhs_titles "MY_VCR~" >/dev/null; sleep 5
timeout 25 adb exec-out screencap -p > "$OUT/T1-one-title.png"
timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --es r95_vhs_titles "VHS/VCR_MENU~REVERENDER_95" >/dev/null; sleep 2
# vaporwave colours: each multicolour background, the last with PASTEL MIX text
for gi in 0 1 2 3 4 5; do
  timeout 20 adb shell am force-stop $VPKG
  timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --ei r95_vhs_wall -1 --ei r95_vhs_grad $gi --ez r95_vhs_mix $([ $gi = 2 ] && echo true || echo false) >/dev/null; sleep 5
  timeout 25 adb exec-out screencap -p > "$OUT/G$gi-grad.png"
done
timeout 20 adb shell am force-stop $VPKG
timeout 20 adb shell am start -n "$VPKG/com.reverender.r95.MainActivity" --ei r95_vhs_grad -1 --ez r95_vhs_mix false >/dev/null; sleep 4
echo "eq/theme pid: $(adb shell pidof $VPKG)"
# visualizer: music, then ■ STOP -> the standby shows must run
timeout 25 adb shell input tap $((W/2)) $((H*97/100)); sleep 1.5   # setup -> END back to the menu
timeout 25 adb logcat -c
timeout 25 adb shell input keyevent KEYCODE_MEDIA_PLAY; sleep 10
timeout 25 adb exec-out screencap -p > "$OUT/15-viz-playing.png"
timeout 25 adb shell input tap $((W*36/100)) $((H*55/100)); sleep 6   # ■
timeout 25 adb exec-out screencap -p > "$OUT/15b-viz-stopped.png"; sleep 1
timeout 25 adb exec-out screencap -p > "$OUT/15c-viz-stopped.png"
timeout 25 adb logcat -d | grep "viz:" | tail -12
timeout 25 adb shell am force-stop $VPKG; adb shell am start -n $VPKG/com.reverender.r95.MainActivity --ef r95_vhs_uiblur 0 >/dev/null; sleep 4
echo "== game log"; adb logcat -d | grep -E "FATAL|AndroidRuntime|r95|libretro|LibretroDroid" | grep -v "attach of thread" | tail -25
echo "vhs-only pid: $(adb shell pidof $VPKG)"
echo "== warnings"
timeout 25 adb logcat -d | grep -E "R95|FATAL|AndroidRuntime: [A-Z]" | grep -v "Calling main\|START com\|Shutting down\|boot image\|lock profiling" | head -40
timeout 25 adb shell pidof $PKG && echo "app alive"
echo done
