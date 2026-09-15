# Room ProGuard Rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep @androidx.room.Entity class * { *; }
-keep class com.example.mypaperscanner.data.** { *; }

# Google Play Services & ML Kit Document Scanner Rules
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# Jetpack Compose & Navigation Rules
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
    @androidx.compose.runtime.ReadOnlyComposable <methods>;
}

# Keep the entire UI and ViewModel layer to prevent navigation and dialog crashes
-keep class com.example.mypaperscanner.ui.** { *; }
-keep class com.example.mypaperscanner.MainActivity { *; }
-keep class com.example.mypaperscanner.util.** { *; }

# Coil Image Loading
-keep class coil3.** { *; }
-dontwarn coil3.**


