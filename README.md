# MotoGallery

> A fast, focused Android gallery experience designed for Motorola devices.

[![Android](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/) [![Jetpack%20Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)

MotoGallery is an offline-first photo gallery prototype with a high-contrast visual system, gesture-driven viewing, albums, discovery, people, settings, and an optional private vault. It is built as a native Android application with Kotlin and Jetpack Compose.

## Highlights

- Clean, high-contrast gallery dashboard
- Fast local photo browsing with Room-backed data access
- Albums, people, discovery, and photo detail flows
- Gesture photo viewer and voice-command entry point
- Optional vault and onboarding experience
- Firebase/App Check integration points for future connected features
- Android API 24+ baseline with Compose Material 3

## Tech stack

- **Kotlin + Jetpack Compose** for the native UI
- **Room** for local persistence
- **Coil** for image loading
- **Firebase AI / App Check** integration points
- **Gradle version catalog** for dependency management

## Getting started

### Requirements

- Android Studio Ladybug or newer
- JDK 11
- Android SDK 36
- An Android 7.0+ device or emulator

### Configure

1. Clone the repository.
2. Copy `.env.example` to `.env` and add only the credentials needed for your local build.
3. If using Firebase features, place the appropriate `google-services.json` in `app/`.
4. Open the project in Android Studio and let Gradle sync.

### Build and test

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedCheck
```

## Project structure

```text
app/src/main/java/com/example/
├── data/       # Room models, DAOs, and repositories
├── ui/         # Screens, view models, components, and theme
└── MainActivity.kt
```

## Security notes

- Never commit `.env`, service-account files, keystores, or production credentials.
- Release signing is intentionally environment-driven through `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD`.
- Any connected or AI-assisted feature should be reviewed for privacy and data minimization before release.

## Status

This is an actively evolving prototype. The local gallery experience is the current focus; cloud sync, recognition, and other connected capabilities should be treated as integration work rather than release guarantees.

## Contributing

Small, focused pull requests are welcome. Please describe the user-facing impact, include verification steps, and keep secrets out of commits. See the repository issues for current priorities.
