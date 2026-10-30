# Technical architecture: approved baseline for Version 1.0

**Status:** ARCH-DEC-01 through ARCH-DEC-08 **Accepted** by the project owner on 2026-10-29. RULE-01–06 and MVP-DEC-01–08 are also accepted. **Phase 2 project setup was explicitly authorized on 2026-10-30; game implementation and release are separate milestones.**  
**Last reviewed:** 2026-10-29  
**Approvals:** See [decisions.md](decisions.md). Any detail labeled *proposed* below is not an additional approved product decision.

## Approved architecture decisions

| ID | Decision | Accepted approach |
| --- | --- | --- |
| **ARCH-DEC-01** | Platform and UI | Native Android application written in **Kotlin**, using **Jetpack Compose**. |
| **ARCH-DEC-02** | Project modules | Two Gradle modules: `:app` and independent pure Kotlin `:game-engine`. |
| **ARCH-DEC-03** | State management | **ViewModel + StateFlow** with unidirectional data flow: user intent → rules-validated transition → published state → UI. |
| **ARCH-DEC-04** | Persistence | **Room** for saved game state, Undo history and four MVP statistics; **DataStore** for small settings. |
| **ARCH-DEC-05** | Undo | Save durable **previous-state snapshots**, updated atomically with each successful action, with no arbitrary fixed history cap within a deal. |
| **ARCH-DEC-06** | Board rendering | **Custom adaptive Compose layout** with gesture handling and accessible tap-to-move alternative. |
| **ARCH-DEC-07** | SDK compatibility | **Minimum API 26**; **target API 36 or higher**, rechecked when development and release occur. |
| **ARCH-DEC-08** | Tooling and verification | **Gradle**, **JUnit**, **Compose UI tests**, representative device/lifecycle tests, and **GitHub Actions** for automated build/test checks. |

### Why these choices

Compose gives us Android-native navigation, accessibility, lifecycle integration and adaptable interfaces. A two-module layout allows the rules engine to run JVM unit tests without Android dependencies. Room is preferred over DataStore for durable action-history transactions and growing Undo history; DataStore remains appropriate for settings. This is an intentional refinement of the earlier single-module/DataStore-only proposal. A full game framework such as libGDX is not part of the accepted Version 1.0 architecture.

## Approved module boundaries (ARCH-DEC-02)

```text
:app                           depends on :game-engine
  Compose screens and board
  GameViewModel / StateFlow
  Room repository (game + Undo + MVP stats)
  DataStore repository (settings)
  Android lifecycle, navigation, audio/haptics, tests and app entry

:game-engine                   Android-independent, no dependency on :app
  Card, Suit, Rank, Tableau, Reserve, GameState
  DealGenerator and deterministic deal identity
  Move/deal action validation and state transitions
  Face-down reveal, completed tableau-run recognition, win detection
  Legal-move enumeration for Hint
  JVM unit tests
```

Do not add separate feature or layer Gradle modules without a documented reason. Exact package names and file structure can be decided at project initialization.

## State flow and responsibilities (ARCH-DEC-03)

```text
Touch / tap / accessibility action
          |
          v
  Compose UI (intent only)
          |
          v
  GameViewModel (coordinates)
          |
          v
:game-engine validates and returns the next state
          |
          v
Room repository persists action + Undo snapshot atomically
          |
          v
StateFlow publishes authoritative state
          |
          v
Compose rerenders; UI animations do not own the rules state
```

The diagram specifies logical ownership, not a mandated implementation sequence or API. The UI must not edit a tableau list or bypass rules validation. Invalid actions produce no committed state change or history entry. The ViewModel and repository coordinate effects and persistence, while the pure Kotlin engine owns game legality.

### Rules and MVP invariants carried forward

- **RULE-01:** The reserve can be dealt once at any time while playing, independent of available moves.
- **RULE-02:** Completed K→A suited runs stay on the tableau; do not store or animate them into a separate completed-card pile.
- **RULE-03:** Track **active** play time; pause while backgrounded and after a win.
- **RULE-04:** Undo-assisted wins are ordinary wins.
- **RULE-05:** Only Classic Wasp is included in Version 1.0.
- **RULE-06:** Zero currently legal tableau moves is a board observation, **not** an automatic loss or proof of unsolvability.
- **MVP-DEC-01:** Both drag/drop and explicit tap-select/tap-destination must execute every legal card-group movement.
- **MVP-DEC-03:** Basic Hint returns legal moves or the still-available reserve, with no solver guarantee.
- **MVP-DEC-04:** The only Version 1.0 displayed statistics are unique games started, games won, best active-play winning time and fewest successful actions in a winning game.
- **MVP-DEC-07:** Saving and exact resume work offline, including retained Undo history.

## Persistence boundaries (ARCH-DEC-04 and ARCH-DEC-05)

**Room** is the accepted authoritative local store for a versioned current-game state, saved Undo history and the four Version 1.0 statistics. The game state must carry the full card identity/order and face-up states, reserve status, deterministic deal identity/seed, move count and active-time metadata needed to resume correctly. Never rely solely on an assumed unchanged shuffle algorithm; restore the saved dealt card configuration.

**DataStore** stores small settings such as sound, haptics and reduced motion. Settings do not need to be part of a game transaction.

**Undo approach:** For each successful player action, preserve a durable snapshot of the complete **preceding logical game state**. Perform the new-state and Undo-history write as one atomic database operation. A reserve deal and any card flips it triggers count as a single reversible action. Failed moves and Hint requests create no history record. Unlimited Undo means no arbitrary fixed per-game maximum; it remains subject to finite device storage, and storage failures must be handled visibly/safely rather than silently discarding older undo entries.

**Restore behavior:** After process death, screen rotation or app backgrounding, load the saved board and history without inflating game-start/win statistics. Validate card-count and identity invariants after restoration. Use explicit schema versions and safe migration/recovery paths as implementation-level guidance, not as permission to erase prior states silently.

The specific Room entity schema, snapshot encoding, compression, serialization framework and transaction API are **implementation details to be documented in Phase 2**, not independently approved here.

## Board rendering (ARCH-DEC-06)

The board is a custom Compose layout with seven columns, position/overlap derived from available window dimensions, and hit-testing that selects the intended face-up card plus every attached card below it. The drag preview is drawn above the tableau, with the underlying rules state unchanged until a valid drop is committed. Invalid drops visually return cards without altering the state.

The supported tap path selects a card group, highlights valid destinations, and requires an explicit destination selection; gameplay must be possible without dragging. Use Android accessibility semantics, reduced-motion preference, and window-inset awareness. Portrait and landscape phone layouts plus an adaptive tablet layout are MVP requirements. **UX-DEC-01–08 now approve** a green-table/ivory-face/blue-back design direction, expanded column inspection, compact controls, accessible feedback and interruption-aware timer. Final art assets/licensing and tested dimensions remain pending; see [ux-design.md](ux-design.md).

## SDK and build baseline (ARCH-DEC-07 and ARCH-DEC-08)

- **minSdk:** Android 8.0 / API 26.
- **targetSdk:** API 36 **or a higher level mandated by current Google Play rules when submitting**. Recheck the policy and Android behavioral changes before initializing and publishing.
- **Compile SDK and dependency versions:** select current mutually compatible stable versions during implementation; not frozen by this review.
- **Build:** Gradle, preferably Kotlin DSL; the approved module layout is `:app` + `:game-engine`.
- **CI:** GitHub Actions runs at least a reproducible build and JVM unit tests on relevant pull requests. Android UI/instrumented tests run on a suitable emulator/device test environment.
- **No backend:** offline-first gameplay is an approved MVP property; a future online service would require a separate scope/architecture decision.

## Test plan and release evidence (ARCH-DEC-08)

| Layer | Required coverage |
| --- | --- |
| Game-engine JUnit tests | Deal identity, 52-card invariants, legal/illegal moves, unordered attached tails, empty columns, reserve, face-down reveals, nonremoving completed runs, win detection |
| Randomized/property-style engine checks | Valid state and card identity preserved over long sequences; reproducible seeds |
| Room persistence tests | Atomic current-state/Undo writes, unlimited-depth-in-principle history, Undo after reserve and flip, restart boundaries, load/restore consistency, nonduplicated statistics |
| Compose UI tests | Drag/drop, explicit tap destination, accessibility controls, legal highlighting, Hint without mutation, responsive layout |
| Device/lifecycle tests | Narrow portrait, landscape, tablet, rotation, background/process death, edge-to-edge/window insets and performance |

**No legal tableau moves**, with or without reserve, must never transition to an automatic loss. A won game requires four complete suited runs remaining on the tableau. The tests for these requirements are specified further in [game-rules.md](game-rules.md) and [product-requirements.md](product-requirements.md).

## Still open and explicitly not approved by ARCH-DEC-01–08

- The UX design direction is approved (UX-DEC-01–08). Exact final card assets/licensing, validated contrast, device-tested dimensions and detailed animation tuning remain to be finalized.
- App name/package ID, branding and assets/licenses.
- Pricing, advertisements, purchases, analytics SDKs and publication account details.
- Specific Android/Compose/Room/JUnit/Gradle versions, Room tables/serialization and dependency-injection frameworks.
- UX-DEC-08 specifies first-successful-action start, blocking-screen/dialog pauses and stop on victory. Lifecycle/monotonic-clock implementation details remain engineering work.
- Phase 2 project setup was authorized 2026-10-30; the actual game engine, UI, persistence and release require milestone-specific review and acceptance.

## Supporting references

- [Android architecture](https://developer.android.com/topic/architecture)
- [Jetpack Compose](https://developer.android.com/compose)
- [Room](https://developer.android.com/training/data-storage/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Compose testing](https://developer.android.com/develop/ui/compose/testing)
- [Google Play target API levels](https://support.google.com/googleplay/android-developer/answer/11926878)

Time-sensitive Google Play requirements must be revalidated ahead of release. The documentation repository remains the source of truth. The owner authorized beginning Phase 2 setup on 2026-10-30; further features and public release retain their separate review and authorization gates.
