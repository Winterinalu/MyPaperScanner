# -------------------------------------------------------------------------
# MyPaperScanner Safety Rules (Conservative Pass)
# -------------------------------------------------------------------------

# Disable optimization to prevent R8 from over-aggressively merging Kotlin code
-dontoptimize

# Room Database Safety
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep @androidx.room.Entity class * { *; }
-keep class com.example.mypaperscanner.data.** { *; }

# Google Play Services & ML Kit (Essential for Document Scanner)
-keep class com.google.android.gms.** { *; }
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.google.mlkit.**

# Jetpack Compose Stability
# Ensure Composable functions and internal Compose metadata are preserved
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
    @androidx.compose.runtime.ReadOnlyComposable <methods>;
}
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# App UI & Logic (Broad Protection)
# We keep the entire app package to ensure ViewModels, Repositories, and Screens remain functional
-keep class com.example.mypaperscanner.** { *; }

# Image Loading (Coil)
-keep class coil3.** { *; }
-dontwarn coil3.**

# Kotlin Metadata Safety
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**
