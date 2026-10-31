# Phase 4A: GameViewModel, StateFlow and session boundary

**Status:** Owner authorized Phase 4A on 2026-10-31. Merged through owner-approved PR #18 at main 3d6db934a66a430a5afcaae5ac0b9b6067eabf30. Phase 4B board rendering, Phase 5A durable saves, timer/statistics, and Google Play actions are **not** authorized by this milestone.

**Requirements:** [roadmap](roadmap.md), [Phase 4 delivery plan](phase4-delivery-plan.md), [rules](game-rules.md), [MVP](product-requirements.md), [architecture](architecture.md), [UX](ux-design.md); tracking [issue #12](https://github.com/FoxyLight/Wasp-Solitaire/issues/12).

## Architecture and ownership

```text
Future Compose screen (Phase 4B/C) -> GameViewModel.submit(GameIntent)
                                          |
                                          v
                                       GameController
                                        |  (Mutex)
                                authoritative ClassicWasp.apply
                                        |
                         ACCEPTED: repository.commit(expectedRevision, next)
                                        |
                                only Stored: publish GameUiState
                                        |
                             StateFlow -> eventual Compose board
```

- The approved pure Kotlin `:game-engine` remains the only rules authority. There are no engine changes.
- `GameUiState.session` holds the immutable full session, including the board and unlimited prior-state Undo history. `board` exposes the engine state. `CardSelection`, `selectedTail`, `legalDestinations` and `hintedAction` are **ephemeral presentation state**, never saved as a move.
- Intent types: `Select`, `CancelSelection`, `MoveTo` (explicit selected-card destination), `Move` (drag-compatible explicit source/index/destination), `DealReserve`, `Undo`, `Hint`, `Refresh`. No implicit destination is chosen by the controller.
- Selecting any exposed card highlights **all** legal destination columns, including empty ones; toggling/canceling a selection never enters history. A selected tail retains its original order and can be unordered. Invalid indices/hidden cards yield `InvalidInput`.
- `Move`, `MoveTo`, `DealReserve` and `Undo` call the engine. Rejected engine actions and failed writes do not change the authoritative UI state or Undo history. Hints only expose the engine's first legal action or reserve suggestion; no solver or automatic defeat is introduced.
- The controller serializes incoming intents with a coroutine `Mutex`. Suspending repository operations are executed in order. Each accepted engine transition is recorded with a strictly increasing `revision` before a new session is emitted. After Undo, net `actionCount` can decrease, while `revision` still increases.
- `GameViewModel` owns a read-only `StateFlow<GameUiState>` and a separate read-only `lastResult` flow. `dispatch` is suspending; `submit` launches within `viewModelScope`. A ViewModelProvider factory can inject a pre-opened controller when the board UI is added in Phase 4B. The existing app screen remains unchanged.

## Persistence-ready contract (no persistence implementation yet)

`SessionRecord(schemaVersion = 1, revision, session)` is the versioned **logical** snapshot. Its `GameSession` must carry the **actual full tableau card identities/order/face states, reserve, seed, net action count and all previous-state Undo snapshots**. The version does not define a serialized on-disk format; Phase 5A will specify and test the Room schema, encoding, migrations and recovery.

`SessionRepository.load()` and `commit(expectedRevision, next)` are suspending APIs. The commit outcome is `Stored`, `Conflict` or `Failure`; only `Stored` allows publication of the next session. The **future Room implementation must atomically persist current state and all Undo snapshots**, without arbitrary history truncation, seed-only reconstruction, partial writes or silently replacing a corrupt session with a new deal. Confirm migrations and corruption recovery before claiming durability.

The Phase 4A `InMemorySessionRepository` is intentionally **volatile**; it makes no disk, autosave or process-death guarantees. It uses a Mutex for revision compare-and-swap. If two controllers share a repository and one commits first, the stale controller gets `StaleRevision` and leaves its visible state untouched; `Refresh` explicitly reloads the latest revision and clears ephemeral selection. Storage failures also retain the original state. These semantics prevent stale intents from overwriting successfully committed sessions.

## Lifecycle and boundaries

A `GameViewModel` retained in the same activity's ViewModelStore across **configuration change** retains its controller and in-memory state. The **application screen does not yet construct a ViewModel or display a board**; Phase 4B will create the actual lifecycle-aware factory/host and verify rotation. If the process dies or the in-memory repository/controller is recreated, the current deal is lost. Saving the complete session, elapsed active time and statistics across process death is reserved for Phase 5A/5B. Do not advertise offline autosave yet.

Do not introduce Room, DataStore, app identity/signing, onboarding/accounts, timer/statistics, completed-run removal or new game rules in Phase 4A.

## Validation

- `./gradlew --no-daemon :game-engine:test :app:testDebugUnitTest :app:assembleDebug`: JVM rule tests plus controller/ViewModel tests and Android compilation.
- Existing Android 35 setup-screen instrumentation via required GitHub Actions `emulator`; this **does not** validate a gameplay board that does not exist yet.
- Dedicated controller tests: immutable engine projection, selected-tail and legal-destination matching, toggling/canceling without actions, invalid/hidden selection rejection, accepted tap and drag intents, rejected drops, reserve/Undo exact restoration, hints without commits, injected store write failure, stale compare-and-swap and refresh, reopening with Undo history, and ViewModel flow delegation.
- CI results must be attributed to the actual PR head SHA. No test success is claimed by this document before a completed run.

## Remaining work

[4B](phase4-delivery-plan.md) builds the adaptive seven-column Compose board and creates a lifecycle-aware ViewModel host/factory; 4C completes touch controls and 4D validates accessibility. Phase 5A replaces volatile storage with a Room-backed atomic repository; 5B handles active-only time, exactly four statistics, settings and polish.

**Review gate:** Passing `verify` and `emulator` plus separate explicit owner merge approval are mandatory. This milestone creates no publication authorization.
