# missed

**A location-based reminder app for Android, built with Kotlin and Jetpack Compose.**

Missed is an in-progress Android application for creating reminders that are tied to real-world destinations. The current prototype includes onboarding, authentication foundations, a polished map picker, location search, and a layered architecture prepared for alarms and offline-first persistence.

> Project status: early-stage prototype. The map and onboarding flows are implemented; the complete alarm lifecycle, background geofencing, and synchronization are still on the roadmap.

## Current features

- Multi-screen onboarding flow
- Google sign-in and Firebase Authentication foundation
- Google Maps destination picker
- Nearby location search and reverse geocoding
- Permission-aware current-location handling
- Material 3 design with light and dark themes
- Type-safe Compose navigation
- Hilt dependency injection
- DataStore preferences

## Planned product flow

1. Choose a destination on the map.
2. Define a reminder and trigger radius.
3. Let the app monitor the destination in the background.
4. Receive the reminder when approaching the selected place.

## Architecture

The project follows a layered, feature-oriented structure:

```text
presentation/    Jetpack Compose screens, navigation, state
domain/          Models, repository contracts, use cases
data/            Local/remote sources and repository implementations
di/              Hilt modules and dependency bindings
```

The intended data flow keeps the domain layer independent of Android and treats local storage as the source of truth.

## Technology

- Kotlin
- Jetpack Compose and Material 3
- Hilt
- Room
- DataStore
- Firebase Authentication and Firestore
- Google Maps and Play Services Location
- Kotlin Coroutines and Serialization

## Getting started

### Requirements

- Android Studio with JDK 11+
- Android SDK 36
- A Firebase Android project
- A Google Maps API key

Clone the repository and open it in Android Studio:

```bash
git clone https://github.com/thuliovs/missed.git
cd missed
```

Add the Firebase `google-services.json` file to `app/`, configure your Maps key locally, and run the `app` configuration on an emulator or Android device with Google Play Services.

Do not commit private API keys or Firebase credentials.

## Roadmap

- Complete alarm creation and management
- Add Room entities and repositories
- Implement background geofencing
- Deliver foreground/background notifications
- Add offline-first synchronization
- Expand unit and UI test coverage

## Author

Built by [Thúlio Vicente](https://github.com/thuliovs) to explore modern Android architecture, location-aware experiences, and reliable background workflows.
