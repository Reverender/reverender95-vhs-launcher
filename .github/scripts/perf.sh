#!/usr/bin/env bash
# Low-end profile: frame times (gfxinfo), the app's per-job breakdown (r95 perf:) and GC counts per scenario.
set -u
OUT=perf; mkdir -p "$OUT"
PKG=com.reverender.r95
adb install -r -g app/build/outputs/apk/standard/debug/app-standard-debug.apk > "$OUT/install.txt" 2>&1
SIZE=$(adb shell wm size | grep -o '[0-9]*x[0-9]*' | tail -1); W=${SIZE%x*}; H=${SIZE#*x}; echo "screen $W x $H"
phase() { # name
  adb shell dumpsys gfxinfo $PKG reset > /dev/null; adb logcat -c
}
report() { # name
  echo "== $1"
  adb shell dumpsys gfxinfo $PKG | grep -E "Total frames|Janky|50th|90th|99th|Number Slow UI|Number Slow bitmap|Number Slow issue" | head -9
  # per-phase medians (ms) from framestats: input, animation (Compose recomposition + frame callbacks), layout, draw recording, sync, GPU
  adb shell dumpsys gfxinfo $PKG framestats | awk -F, '
    /^Flags,/ { for (i = 1; i <= NF; i++) col[$i] = i; hdr = 1; next }
    hdr && /^0,/ {
      n++
      inp[n] = ($col["AnimationStart"] - $col["HandleInputStart"]) / 1e6
      ani[n] = ($col["PerformTraversalsStart"] - $col["AnimationStart"]) / 1e6
      lay[n] = ($col["DrawStart"] - $col["PerformTraversalsStart"]) / 1e6
      drw[n] = ($col["SyncQueued"] - $col["DrawStart"]) / 1e6
      syn[n] = ($col["IssueDrawCommandsStart"] - $col["SyncStart"]) / 1e6
      gpu[n] = ($col["FrameCompleted"] - $col["IssueDrawCommandsStart"]) / 1e6
    }
    /---PROFILEDATA---/ && n > 0 && hdr { hdr = 0 }
    function med(a,  k, i, j, t, b) { k = 0; for (i in a) b[++k] = a[i]; for (i = 2; i <= k; i++) { t = b[i]; j = i - 1; while (j > 0 && b[j] > t) { b[j + 1] = b[j]; j-- } b[j + 1] = t } return k ? b[int((k + 1) / 2)] : 0 }
    END { if (n) printf "  phases(median ms, %d frames): input=%.1f anim=%.1f layout=%.1f draw=%.1f sync=%.1f gpu=%.1f\n", n, med(inp), med(ani), med(lay), med(drw), med(syn), med(gpu) }'

  adb logcat -d | grep "r95.*perf: fps" | tail -2 | sed 's/.*perf:/  perf:/'
  echo "  GC lines: $(adb logcat -d | grep -cE 'GC freed|concurrent copying GC')"
  adb exec-out screencap -p > "$OUT/$1.png" 2>/dev/null || true
}
adb shell am start -n "$PKG/.MainActivity" --ez r95_skip_boot true > /dev/null; sleep 20
phase idle; sleep 15; report idle-desktop-with-player
# drag the player by its title bar (centred window, title bar just below its top edge)
phase drag
for i in 1 2 3 4 5; do
  adb shell input swipe $((W/2)) $((H*24/100)) $((W/2)) $((H*45/100)) 900
  adb shell input swipe $((W/2)) $((H*45/100)) $((W/2)) $((H*24/100)) 900
done
report drag-window
# scroll inside the player
phase scroll
for i in 1 2 3 4 5; do adb shell input swipe $((W/2)) $((H*70/100)) $((W/2)) $((H*45/100)) 400; adb shell input swipe $((W/2)) $((H*45/100)) $((W/2)) $((H*70/100)) 400; done
report scroll-player
adb shell am start -n "$PKG/.MainActivity" --es r95_open "plasma,orbital,vectordreams" > /dev/null 2>&1; sleep 10
phase heavy; sleep 15; report heavy-3-windows
phase heavydrag
for i in 1 2 3 4 5; do adb shell input swipe $((W/2)) $((H*30/100)) $((W/2)) $((H*50/100)) 900; adb shell input swipe $((W/2)) $((H*50/100)) $((W/2)) $((H*30/100)) 900; done
report drag-with-3-windows
# new native windows + widgets/dream/wallpaper classes load without crashing
adb logcat -c
adb shell am start -n "$PKG/.MainActivity" --es r95_open "settings,fx,eq,cassette,music,monitor,help" > /dev/null 2>&1; sleep 15
adb exec-out screencap -p > "$OUT/new-windows.png" 2>/dev/null || true
adb shell am broadcast -a android.appwidget.action.APPWIDGET_UPDATE -n "$PKG/.os.VuWidget" > /dev/null 2>&1; sleep 2
echo "== crashes"
adb logcat -d | grep -E "FATAL EXCEPTION|AndroidRuntime: |Process: com.reverender" | head -40 || true
adb shell pidof $PKG && echo "app alive"
echo done
