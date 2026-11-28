# Google Play upload checklist — REVERENDER VHS Launcher (com.reverender.vhslauncher)

## Before the first upload
- [ ] Host `play/privacy.html` at https://reverender.com/vhs/privacy
- [ ] Make the source public (GPLv3: LibretroDroid and the cores). Put its address in `play/licenses.html` (SOURCE_URL),
      host that page at https://reverender.com/vhs/licenses (SETUP > LICENSES opens it). Add a LICENSE file (GPL-3.0) to the public repo.
- [ ] Deck songs bundled in the app: confirm you own the rights to every one (or remove them from the Play build).
- [ ] Use Play App Signing; upload `REVERENDER-VHS-PLAY.aab` (from the "latest" release) to Internal testing first.

## Store listing
- Name, short and full description: `play/listing.md`
- App icon 512x512, feature graphic 1024x500, 8 phone screenshots (1080x1920): from the store material
- Promo video: upload `promo-1920x1080.mp4` to YouTube (public or unlisted, ads off), paste the link
- Category: Personalization. Contact email required.

## Pricing
- Paid app, one-time price. No in-app purchases, no ads ("Contains ads: No").
- Paid apps need a payments profile (merchant account) in Play Console.

## App content forms
- Privacy policy: https://reverender.com/vhs/privacy
- Ads: No
- Data safety: no data collected, no data shared. (Notification content is processed only on the device;
  searches go to the services as part of the feature and are not collected by you.) Data is not encrypted in transit
  declaration: answer "yes" (HTTPS). Users can't request deletion: no data is kept.
- Content rating questionnaire: utility/personalization; unrestricted internet (web browser, online video) = Yes.
- Target audience: 13+ (not designed for children).
- News app: No (the RSS ticker is user-chosen).
- Foreground service declaration: FOREGROUND_SERVICE_MEDIA_PLAYBACK — "plays music and internet radio while the
  app is in the background, controlled from the notification". Add a short video link if asked.
- Notification listener: no Console form, but the in-app disclosure (SETUP > NOTIFS) is in place; mention it in the review notes.
- REQUEST_DELETE_PACKAGES: used for the drawer's UNINSTALL; it's a normal permission (no form).
- Home app (launcher): fine; no QUERY_ALL_PACKAGES (uses <queries> intents).

## Review notes (paste in "App access" / notes for reviewers)
"Home screen launcher with a VHS look. Notification access is optional and only used on-device for the
notification counter and the VHS CHAT screen; a disclosure is shown before the settings open. Videos from YouTube
play in YouTube's official embedded player. The game deck plays the user's own files and links to free homebrew
games on their makers' pages; no games are included or downloaded by the app. Emulator cores are bundled as native
libraries; no code is downloaded."

## Things kept out of the Play build
- YouTube stream extraction (NewPipe): replaced by YouTube's embedded player
- ROM search: replaced by FILE + free homebrew links
- archive.org: only Creative Commons / public-domain items
- Runtime download of emulator cores: bundled instead
- snes9x (non-commercial licence): replaced by bsnes
- "WhatsApp" / "Trinitron" wording
