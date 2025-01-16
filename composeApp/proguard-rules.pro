-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }

-dontwarn androidx.compose.material.**
-dontwarn okio.AsyncTimeout$Watchdog
