# Phase 2: Android project environment and setup

**Status:** Implementation authorized by the project owner on 2026-10-30; **setup PR remains under review** until merged. The owner said "Begin phase 2." This authorization covers starting the approved Android project setup milestone, not production release, new features, or unreviewed merges.
**Last reviewed:** 2026-10-30

## Scope in this PR

- Two approved Gradle modules: `:app` (native Android/Compose) and `:game-engine` (**pure Kotlin JVM**, no Android dependency).
- Minimal display-only Compose activity. **No real board, game rules, new deals, persistence or timer logic yet.**
- Pinned Android Gradle Plugin **9.3.1**, Gradle **9.5.0** (installed by CI), Kotlin/Compose compiler **2.4.21** and stable Compose BOM **2026.09.00**.
- Android minSdk **26**, targetSdk **36**, compileSdk **36**. The next setup/release review must recheck current Play requirements.
- A JUnit module-wiring smoke test and a device-only Compose UI smoke test. **These do not establish game correctness**.
- GitHub Actions executes engine JUnit tests and assembles a debug APK; device instrumentation tests need a separately provisioned emulator.
- No analytics, ads, network permissions, account system or in-app purchase integration.

## Important placeholder identity

The Android namespace and application ID are temporarily set to `org.example.waspsolitaire` for local/debug use only. **They are not the final name, not Play-registered and MUST NOT be released.** The proposed `com.foxylight.waspsolitaire` and FoxyLight identity remain subject to ownership and account verification (REL-DEC-02).

## Build instructions

Prerequisites: JDK **17**, Android SDK **platforms;android-36** (and Android build tools **36.0.0**), Gradle **9.5.0**.

```sh
gradle --no-daemon :game-engine:test
gradle --no-daemon :app:assembleDebug
gradle --no-daemon :app:testDebugUnitTest
```

**Wrapper limitation:** A trusted, official `gradle-wrapper.jar` is not yet committed because the authoring environment could not retrieve the binary. This PR uses a pinned Gradle version via `gradle/actions/setup-gradle@v4` in CI, rather than shipping an incomplete/broken wrapper. A follow-up setup PR should generate and verify the standard wrapper from a trusted Gradle 9.5.0 installation, then commit `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties` and the official wrapper JAR. Until then, local builds need installed Gradle 9.5.0.

## Validation status

- Structure and source/static consistency can be checked from this PR.
- **A local Android compile was not executed**: the authoring environment lacks an Android SDK, installed Gradle and external dependency resolution.
- GitHub Actions is configured to run the real build and JVM smoke tests once the PR event starts. CI success must be checked in GitHub; do not mark it as passed without a run.
- A device/emulator UI test and SDK compatibility across supported devices are later acceptance gates.

## Next milestone, not included here

Only after successful setup review/merge: pure Kotlin rules engine with deterministic deals, legal moves (including unordered tails), reserve deal, Undo-compatible state, Hint and win detection, and comprehensive real engine JUnit tests. The actual Android board and Room persistence are later phases.
