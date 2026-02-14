QUICK START GUIDE

## Android 2048 Game - Quick Start

### What's Included
This is a complete, production-ready Android 2048 Game built with:
- Kotlin
- Jetpack Compose (Modern Android UI)  
- Material Design 3

### Prerequisites
1. Install Android Studio (latest version from https://developer.android.com/studio)
2. Install JDK 11 or higher
3. Have Android SDK 33 installed (can be installed through Android Studio)

### Setup & Run

#### Option 1: Using Android Studio (Easiest)
1. Open Android Studio
2. File > Open and navigate to the `app-development` folder
3. Wait for Gradle to sync (may take 2-3 minutes on first run)
4. Connect an Android device or start an emulator
5. Click the green "Run" button (Shift+F10)
6. Select your device and click OK
7. App will launch in 1-2 minutes

#### Option 2: Using Command Line
1. Open terminal/command prompt in the project directory
2. Run: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`)
3. This creates: `app/build/outputs/apk/debug/app-debug.apk`
4. Install on device: `adb install app/build/outputs/apk/debug/app-debug.apk`
5. Or: `./gradlew installDebug` to build and install in one command

### Game Features
✓ Full 2048 game logic
✓ Swipe-based controls  
✓ Score tracking
✓ Win/Lose detection
✓ Colorful tile design
✓ Material Design UI

### Common Issues & Solutions

**Issue: "Gradle sync failed"**
- Solution: File > Invalidate Caches > Invalidate and Restart

**Issue: "Build-Tools version issue"**
- Solution: Tools > SDK Manager > SDK Tools tab > Check "Show Package Details" > Install latest Build-Tools 33.x.x

**Issue: "Could not find Android SDK"**
- Solution: File > Project Structure > SDK Location > Set correct Android SDK path

**Issue: App won't compile**
- Solution: Try: Build > Clean Project, then Build > Rebuild Project

### Project Files Overview

Key files to understand:
- `app/src/main/java/com/example/game2048/MainActivity.kt` - Main game screen UI
- `app/src/main/java/com/example/game2048/GameLogic.kt` - Game logic (moves, merging, etc.)
- `app/build.gradle` - Dependencies and build configuration
- `settings.gradle` - Root project config
- `gradle.properties` - Gradle properties

### How to Play
1. Launch the app
2. Swipe in any direction (up, down, left, right)
3. Tiles with same number merge when they touch
4. Get one tile to 2048 to win
5. Game ends when no more moves are possible
6. Tap "New Game" to restart

### Device Requirements
- Minimum: Android 5.0 (API 21)
- Target: Android 13+ (API 33)
- Recommended: Android 10+ with 2GB+ RAM

### Next Steps for Development
Want to enhance the game? Try:
1. Add tile move animations
2. Add sound effects
3. Save high score
4. Add undo move button
5. Add different board sizes (3x3, 5x5)
6. Add difficulty levels

For more info, see README.md in the project root.
