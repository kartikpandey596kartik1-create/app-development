# Debug Configuration
-verbose

# Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Android X
-keep class androidx.** { *; }
-keepclassmembers class androidx.** { *; }

# Game logic
-keep class com.example.game2048.** { *; }
-keepclassmembers class com.example.game2048.** { *; }
