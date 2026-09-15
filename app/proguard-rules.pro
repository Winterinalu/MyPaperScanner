# Room ProGuard Rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep @androidx.room.Entity class * { *; }
-keep class com.example.mypaperscanner.data.** { *; }
