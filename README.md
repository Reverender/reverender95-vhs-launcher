# REVERENDER_95 VHS launcher

An Android home screen (launcher) in the style of a VHS / VCR on-screen menu: CRT scanlines, tape glitches,
3D neon wallpapers, a music deck with radio and visualizer, a video deck, a game deck (NES / SNES / GB / GBA / N64),
an equalizer, and an apps menu.

Split off from [reverender95-android-Native](https://github.com/Reverender/reverender95-android-Native)
(the REVERENDER_95 desktop app). It shares that app's source; only the `vhs` build is made here.

* Download: the `REVERENDER_95-VHS.apk` in the **latest** release (built on every push to `main`).
* App id: `com.reverender.r95.vhs` — installs next to REVERENDER_95.
* UI check on an emulator: Actions → *UI check* → Run workflow (results on the `ui-results` branch).

Release signing: add the repository secrets used by `build-apk.yml` (the same keystore as before), otherwise
the APK is signed with a debug key and an older install has to be removed first.
