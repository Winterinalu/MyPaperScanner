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

# Keep everything in our app packages from being aggressively optimized away if reflection is assumed
-keep class com.example.mypaperscanner.** { *; }

