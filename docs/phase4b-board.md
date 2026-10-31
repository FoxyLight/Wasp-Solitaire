# Phase 4B: adaptive read-only Compose board

Owner-authorized 2026-10-31; proposed in [PR #19](https://github.com/FoxyLight/Wasp-Solitaire/pull/19), tracking [Issue #13](https://github.com/FoxyLight/Wasp-Solitaire/issues/13). The PR requires owner review and separate merge approval. Issue #13 remains open until approved and merged. Phase 4C/4D, Phase 5 and publication remain separately gated.

## Baseline and approved requirements

Started from main `3d6db934a66a430a5afcaae5ac0b9b6067eabf30`, after Phase 3 PR #11 and Phase 4A PR #18 merged. The main [verification run](https://github.com/FoxyLight/Wasp-Solitaire/actions/runs/41235299366) passed verify and emulator before implementation. Read game-rules, product-requirements, architecture, ux-design, roadmap, phase4-delivery-plan, phase4a-state and phase3-engine on main. This implements rendering portions of UX-DEC-01/02/03/06, MVP-01/06/10/14, ARCH-DEC-01/02/03/06/08 and preserves all approved rules/exclusions.

## Components and state ownership

- MainActivity opens the existing repository asynchronously in lifecycleScope and obtains GameViewModel through GameViewModelFactory and the Activity ViewModelStore. Suspending load stays outside Factory.create; a future Room implementation can replace the port.
- WaspApplication owns GameSessionOwner, whose lazy InMemorySessionRepository generates one initial seeded deal per process. Production initialization uses SecureRandom.nextLong; JVM/Compose fixtures inject a fixed seed (123). Rotation and composition do not generate a new deal. A prepared controller can be discarded when ViewModelProvider finds the retained ViewModel; it reads the same repository and cannot reset it.
- GameBoardHost uses collectAsStateWithLifecycle to observe the existing immutable GameUiState. GameBoard, TableauColumn, PlayingCard, SuitMark and Reserve are read-only renderers. There is no second board model, move validator or mutation path in Compose. Completed suit recognition is supplied by the engine; unique exposed Aces locate the corresponding column indicator.
- GameUiState selection, legalDestinations and hintedAction drive outlines, descriptive text and semantic state labels. Every selected attached card is outlined. Legal/hint destination columns are outlined; text lists destinations and source/target. There are no click, tap or drag actions, and no interactive Undo/Hint/Deal 3/New Game/Restart controls in this milestone.
- The application repository and Activity-owned ViewModel are volatile. In-memory session/history/selection survive supported configuration recreation; closing the process loses the deal. No durable autosave or process-death recovery is provided.

## Responsive geometry and presentation

BoardGeometry computes seven equal columns from actual available width minus six 4dp gutters; card width caps at 120dp on large windows. The board uses safe drawing insets, 8dp side padding and centered columns. Card aspect starts at 1:1.44, with extra height for enlarged rank headers. Face-up overlap reserves the scaled rank line plus suit mark; face-down overlap is shallower. All seven columns remain in the overview and keep engine list order, deepest to exposed. A single vertical scroll region makes long columns, reserve and status reachable in short landscape windows and with enlarged fonts. No physical screen size or rotation-specific deal is hardcoded.

Green #165443, ivory #F7F4EB, blue #263B62, red #B4232C and dark #192327 follow approved UX direction. Original Compose Canvas paths draw clubs, diamonds, hearts and spades, and diagonal weave backs. Rank glyphs use the platform serif font. No external images, fonts, audio, artwork packs or third-party artwork licenses were imported. Suit shapes, rank text, explicit selection/hint/destination labels and in-place check marks supplement color. Hidden card identities are excluded from both visible content and semantics. Reserve cards are rendered only as backs.

The small-column overview is a rendering foundation; it is not a claim of complete TalkBack support or accessible individual selection. Interactive expanded inspection and comprehensive accessibility validation remain Phase 4D. A static preview notice communicates the current milestone without presenting nonworking gameplay buttons.

## Verification and evidence

Automated tests are executed by the required [PR #19 checks](https://github.com/FoxyLight/Wasp-Solitaire/pull/19/checks); the PR description records the actual final head, commands, results and CI run links. Do not infer test execution from this test inventory alone.

- Existing 17 engine JUnit tests and Phase 4A controller tests remain intact. New JVM tests cover seven-column width containment at 320/360/400/800/1200dp, enlarged-header/long-tail sizing and one-time seeded initialization/factory reopening of the same repository session.
- BoardRenderingTest uses deterministic engine deals and actual controller-produced selection/legal/hint states: seven ordered columns, 49 tableau plus three reserve cards, 12 hidden/37 exposed, exposed rank/suit descriptions and text, hidden-identity exclusion, accurate reserve status, immutable rendering and four in-place completed runs.
- DeviceConfigurationOverride renders 320x640, 390x780, 640x320 and 800x1000dp plus 320x640dp at 200% font scale. The same composition/session is resized through these configurations; assertions check horizontal containment/order, reachable exposed column ends and scroll-to-reserve. These are localized Compose configuration overrides on the configured Android 35 x86_64 emulator, not five physical devices or five independently booted AVDs. See the [official testing pattern](https://developer.android.com/develop/ui/compose/testing/common-patterns).
- The entry-screen test obtains the real Activity ViewModel, commits a reserve deal and selects a tail through the existing controller, recreates the Activity, requests real landscape rotation, and checks the identical ViewModel/session, retained selection, displayed reserve and absence of gameplay click handlers. Programmatic setup intents are test fixtures, not user-facing Phase 4C interactions.
- PNG captures include top/reserve views for each size/text override, selected/hint state and completed runs. CI uploads these plus instrumentation reports as board-rendering-evidence. Screenshots support visual review; assertions do not establish final contrast, TalkBack usability or physical-device coverage.

Run `./gradlew --no-daemon :game-engine:test :app:testDebugUnitTest :app:assembleDebug` and `./gradlew --no-daemon :app:connectedDebugAndroidTest`. The existing verify/emulator jobs and protections remain required. The emulator workflow additionally verifies Gradle collected the generated test screenshots and uploads evidence; its validation commands and Android API remain unchanged.

## Remaining work

Phase 4C connects explicit tap/drag intents and gameplay workflows; Phase 4D adds expanded column inspection, accessible actions and comprehensive TalkBack/device testing. Room/atomic durable saves, DataStore, lifecycle timer and four statistics belong to Phase 5. There is no new variant, solver, loss rule, account, production identity/signing change or publication. No new owner product decision is requested by this milestone.
Local combined validation was attempted with JDK 17 but stopped because no Android SDK is configured on the workstation. CI provides the SDK and emulator; consult the final PR results for executed Android/JVM/instrumentation checks.
