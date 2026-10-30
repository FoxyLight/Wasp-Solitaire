# Phase 2: Android project environment and setup

**Status:** Initial scaffold merged through [PR #7](https://github.com/FoxyLight/Wasp-Solitaire/pull/7) on 2026-10-30 under the owner's Phase 2 authorization. Its CI checks failed due to Compose 1.12 dependencies requiring API 37 while `compileSdk` was 36. The Phase 2 stabilization PR addresses that failure. Phase 2 setup is **not verified complete** until the build and tests pass. No game-engine implementation, public release or unreviewed merge is authorized.
**Last reviewed:** 2026-10-30

## Scope of the Phase 2 setup

- Two approved Gradle modules: `:app` (native Android/Compose) and `:game-engine` (**pure Kotlin JVM**, no Android dependency).
- Minimal display-only Compose activity. **No real board, game rules, new deals, persistence or timer logic yet.**
- Pinned Android Gradle Plugin **9.3.1**, Gradle **9.5.0** (wrapper and CI), Kotlin/Compose compiler **2.4.21** and stable Compose BOM **2026.09.00**.
- Android minSdk **26**, targetSdk **36**, compileSdk **37**. The next setup/release review must recheck current Play requirements.
- A JUnit module-wiring smoke test and a device-only Compose UI smoke test. **These do not establish game correctness**.
- GitHub Actions runs the JVM smoke test, builds a debug APK, and has a separate Android emulator job for the existing Compose screen test. These are setup tests, not Wasp gameplay tests.
- No analytics, ads, network permissions, account system or in-app purchase integration.

## Important placeholder identity

The Android namespace and application ID are temporarily set to `org.example.waspsolitaire` for local/debug use only. **They are not the final name, not Play-registered and MUST NOT be released.** The proposed `com.foxylight.waspsolitaire` and FoxyLight identity remain subject to ownership and account verification (REL-DEC-02).

## Build instructions

Prerequisites: JDK **17**, Android SDK **platforms;android-37** (and Android build tools **36.0.0**), and the committed Gradle **9.5.0** wrapper.

```sh
./gradlew --no-daemon :game-engine:test
./gradlew --no-daemon :app:assembleDebug
./gradlew --no-daemon :app:testDebugUnitTest
```

**Verified Gradle wrapper:** The stabilization PR generates official Gradle 9.5.0 wrapper files using Gradle itself. The wrapper JAR must match the [Gradle-published SHA-256](https://gradle.org/release-checksums/) `497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`; the distribution is pinned with `distributionSha256Sum=553c78f50dafcd54d65b9a444649057857469edf836431389695608536d6b746`. The wrapper is validated by GitHub Actions. This is a stabilization-PR change until merged.

**Why API 37 compilation:** [Compose 1.12](https://android-developers.googleblog.com/2026/08/jetpack-compose-august-2026-release.html) requires compiling with API 37. Android Gradle Plugin 9.3 supports API 37. `compileSdk 37` does **not** change `minSdk 26` or `targetSdk 36`.

## Validation status

- Structure and source/static consistency can be checked from this PR.
- Initial PR #7 CI failed at `:app:checkDebugAarMetadata` because Compose dependencies required API 37. The stabilization PR updates the compile SDK and tests the setup in GitHub Actions.
- CI success must be checked on the stabilization PR; a passing build, JVM smoke test and emulator test are required before declaring Phase 2 verified.
- The Compose setup smoke test runs in CI on an Android emulator. Broader device coverage and accessible gameplay tests remain later milestones.

## Next milestone, not included here

Only after successful setup review/merge: pure Kotlin rules engine with deterministic deals, legal moves (including unordered tails), reserve deal, Undo-compatible state, Hint and win detection, and comprehensive real engine JUnit tests. The actual Android board and Room persistence are later phases.
