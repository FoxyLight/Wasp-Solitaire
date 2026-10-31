# Wasp Solitaire for Android

This repository is the source of truth for **Wasp Solitaire Android**.

## Current status

The project owner approved the Phase 1 specifications and authorized Phase 2 setup. Stabilization merged in [PR #8](https://github.com/FoxyLight/Wasp-Solitaire/pull/8); a fresh [baseline run](https://github.com/FoxyLight/Wasp-Solitaire/actions/runs/41229459279) passed Android compilation, JVM tests and Android 35 instrumentation on 2026-10-31. Required PR/CI protection is now configured and independently tested; see [governance](docs/repository-governance.md).

The Classic Wasp engine merged in PR #11, Phase 4A GameViewModel/StateFlow in PR #18, and the owner-approved Phase 4B board in PR #19. This feature branch proposes separately owner-authorized Phase 4C gameplay in [PR #20](https://github.com/FoxyLight/Wasp-Solitaire/pull/20): explicit tap and whole-tail drag, Undo, Hint, Deal 3, confirmed Restart/New Game and in-place victory. See [implementation, verification and limitations](docs/phase4c-gameplay.md).

Phase 4C is not merged. Expanded inspection/accessibility (4D), durable saves and timer/statistics (5) remain separately gated. There is no process-death recovery or publication authorization.

## Project structure

- `app/`: native Android Jetpack Compose app. **Temporary** app ID `org.example.waspsolitaire`, not for Google Play publication.
- `game-engine/`: pure Kotlin/JVM rules engine, immutable snapshots, Undo and legal hints.
- `.github/workflows/android-ci.yml`: pinned Gradle Android build and engine test workflow.
- `docs/`: authoritative approved specifications, milestone tracking and release constraints.

## Build the setup

Use JDK 17, Android SDK platform 37.0 for compilation, and the Gradle 9.5.0 wrapper:

```sh
./gradlew --no-daemon :game-engine:test :app:assembleDebug :app:testDebugUnitTest
```

The official Gradle wrapper and its SHA-256-pinned Gradle 9.5.0 distribution are committed. See [Phase 2 setup notes](docs/phase2-setup.md) for prerequisites and verification evidence.

The engine has rule tests; Android instrumentation currently covers only the setup screen. No app code may be published to Google Play without verified developer identity, final package ID, asset rights, compliance checks and **separate owner release approval**.

## Documentation and governance

- [Rules](docs/game-rules.md)
- [MVP scope](docs/product-requirements.md)
- [Architecture](docs/architecture.md)
- [UX design](docs/ux-design.md)
- [Release strategy](docs/google-play.md)
- [Decision log](docs/decisions.md)
- [Roadmap](docs/roadmap.md)
- [Phase 2 setup and build caveats](docs/phase2-setup.md)
- [Repository merge-gate administration](docs/repository-governance.md)

All substantive changes go through reviewable pull requests. Never auto-merge or publish.
