# Site port

The wallpapers are the website's own canvas code, translated to Kotlin.

1. Prettify the site's `rr-app-core-*.js` and `rr-wallpaper-renderer-*.js` (prettier --parser babel).
2. `node extract.js <scene functions...> freqData,timeData,mouseX,mouseY > core_part.js`
3. `node wp_extract.js > wp_part.js`; `cat core_part.js wp_part.js glue.js > all.js`
4. `node tx.js all.js ../../app/src/main/java/com/reverender/r95/ui/viz/site/SiteWallpapers.kt com.reverender.r95.ui.viz.site SiteWallpapers "$(cat seed.json)"`

`glue.js` is the frame driver (the background half of the site's `animate()` and the post renderer's `yl()`).
The Kotlin runtime it targets lives in `ui/viz/js` (JS value helpers + a Canvas 2D context on android.graphics).
