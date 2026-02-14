# 2048 Game - Android Application

A fully functional 2048 game implementation for Android using Kotlin and Jetpack Compose.

## Features

- **Full 2048 Game Logic**: Complete implementation of the 2048 game mechanics
- **Smooth UI**: Built with Jetpack Compose for a modern Material Design interface
- **Gesture Controls**: Swipe gestures to move tiles in all directions (up, down, left, right)
- **Score Tracking**: Real-time score updates as you play
- **Game Status**: Displays when you win or when the game is over
- **Color-coded Tiles**: Each number has its own color scheme for better visuals
- **New Game**: Reset the game anytime with the "New Game" button

## Game Rules

1. Tiles with the same number merge when they touch
2. Move tiles by swiping up, down, left, or right
3. Each move adds a new random tile (2 or 4)
4. Reach 2048 to win (you can continue playing)
5. Game ends when there are no valid moves left

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/game2048/
│   │   │   ├── MainActivity.kt          # Main UI and game screen
│   │   │   ├── GameLogic.kt             # Core game logic
│   │   │   └── ui/theme/
│   │   │       ├── Theme.kt             # Material Design theme
│   │   │       ├── Color.kt             # Color palette
│   │   │       └── Type.kt              # Typography
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml          # App strings
│   │   │   │   └── themes.xml           # Theme resources
│   │   │   └── xml/
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   └── AndroidManifest.xml
│   └── ...
├── build.gradle                         # App-level build configuration
└── proguard-rules.pro
```

## Build Instructions

### Prerequisites
- Android Studio (latest version)
- Android SDK 33+
- Kotlin 1.7.20+

### Building from Android Studio
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device (Android 5.0+)

### Building from Command Line
```bash
./gradlew assembleDebug      # Build debug APK
./gradlew installDebug       # Install on connected device
./gradlew build              # Build all variants
```

## Game Controls

- **Up**: Swipe up or use up arrow
- **Down**: Swipe down or use down arrow  
- **Left**: Swipe left or use left arrow
- **Right**: Swipe right or use right arrow
- **New Game**: Tap the "New Game" button to restart

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: 21
- **Target SDK**: 33
- **Compilable SDK**: 33

## Dependencies

- androidx.core:core-ktx
- androidx.lifecycle:lifecycle-runtime-ktx
- androidx.activity:activity-compose
- androidx.compose.ui:ui
- androidx.compose.material3:material3

## Game Colors

The game uses a sophisticated color scheme:
- **Empty tiles**: Light gray
- **2**: Light background with dark text
- **4**: Slightly darker background
- **8-64**: Orange and red shades
- **128-512**: Yellow shades
- **1024**: Red shade
- **2048**: Golden yellow (winning tile)
- **4096+**: Dark gray with light text

## Author Notes

This is a modern Android implementation of the classic 2048 game, featuring:
- Responsive gesture-based controls
- Smooth tile animations (structure in place for expansion)
- Clean, maintainable code architecture
- Material Design 3 compliance

Enjoy the game!
