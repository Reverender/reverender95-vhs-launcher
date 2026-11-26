# Media3 and Compose ship their own consumer rules; nothing extra needed yet.

# Revy brain bridge (QuickJS calls the host interface by reflection)
-keep class app.cash.quickjs.** { *; }
-keep interface com.reverender.r95.net.RevyHost { *; }
-keepclassmembers class * implements com.reverender.r95.net.RevyHost { *; }

# NewPipeExtractor (VIDEO_STREAM_95 YouTube) and its Rhino JS engine
-keep class org.schabi.newpipe.extractor.** { *; }
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn org.mozilla.javascript.**
-dontwarn org.schabi.newpipe.extractor.**
-dontwarn javax.script.**
-dontwarn java.beans.**
-dontwarn jdk.dynalink.**
-dontwarn com.google.re2j.**

# LibretroDroid: its native engine calls back into these classes by name (JNI) — keep them as they are,
# or loading a ROM crashes in release builds.
-keep class com.swordfish.libretrodroid.** { *; }
-keepclassmembers class com.swordfish.libretrodroid.** { native <methods>; *; }
