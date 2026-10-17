# APEX AI — Self-Driving Lab 🏎️

Native Android Q-learning driving simulator (Java Canvas, no WebView). The agent explores steering decisions and learns a circular circuit over repeated episodes.

## Get the APK on Android

1. Open [GitHub Actions](https://github.com/RyWethxss/Apex-ai-android/actions/workflows/android-apk.yml).
2. Open the latest successful **Build Android APK** run.
3. Under **Artifacts**, download **APEX-AI-Android-APK**.
4. Extract the ZIP on your phone, then open `app-debug.apk` and approve installation when Android asks.

The workflow builds a debug APK automatically on every push to `main`.

## Features

- Live top-down race track with steering AI
- Q-learning policy (77 states × 3 actions)
- Episodes, collisions, exploration and best progress
- Learning progress chart
- Pause/resume and training speed controls
- Long-press to reset training
- Persisted Q-table between launches

## Developer build

Java 17, Gradle 8.9, Android Gradle Plugin 8.7.3, Android SDK 35. Run `gradle assembleDebug`.

Note: Learning outcomes vary and successful full laps are not guaranteed. Debug APK is for testing, not Play Store distribution.
