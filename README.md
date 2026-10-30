# Wasp Solitaire for Android

This repository is the source of truth for **Wasp Solitaire Android**.

## Current status

The project owner approved the Classic Wasp rules, Version 1.0 MVP, Android architecture, UI/UX and release strategy in Phase 1. On **2026-10-30**, the owner explicitly authorized **beginning Phase 2**. The Android setup is proposed in a reviewable PR and is **not yet merged**.

**No playable game exists yet.** The Phase 2 setup provides a minimal Compose placeholder screen and a pure Kotlin JVM engine scaffold, deliberately without implementing card rules or gameplay.

## Project structure

- `app/`: native Android Jetpack Compose app. **Temporary** app ID `org.example.waspsolitaire`, not for Google Play publication.
- `game-engine/`: pure Kotlin/JVM module. Contains a **scaffold only** until Phase 3.
- `.github/workflows/android-ci.yml`: pinned Gradle Android build and engine test workflow.
- `docs/`: authoritative approved specifications, milestone tracking and release constraints.

## Build the setup

Use JDK 17, Android SDK platform 37 and Gradle 9.5.0:

```sh
gradle --no-daemon :game-engine:test :app:assembleDebug :app:testDebugUnitTest
```

The official Gradle wrapper binary has not been retrieved/committed in this environment; see [Phase 2 setup notes](docs/phase2-setup.md). CI pins Gradle 9.5.0 through `gradle/actions/setup-gradle`.

Do not mistake the scaffold tests for game-engine validation. No app code may be published to Google Play without verified developer identity, final package ID, asset rights, compliance checks and **separate owner release approval**.

## Documentation and governance

- [Rules](docs/game-rules.md)
- [MVP scope](docs/product-requirements.md)
- [Architecture](docs/architecture.md)
- [UX design](docs/ux-design.md)
- [Release strategy](docs/google-play.md)
- [Decision log](docs/decisions.md)
- [Roadmap](docs/roadmap.md)
- [Phase 2 setup and build caveats](docs/phase2-setup.md)

All substantive changes go through reviewable pull requests. Never auto-merge or publish.
