# Technical architecture proposal

**Status:** Architecture proposed, no app code authorized; RULE-01–06 accepted in rules specification  
**Last reviewed:** 2026-10-29

## Stack recommendation

- **Language:** Kotlin.
- **UI:** Jetpack Compose + Material 3 for menus/controls; custom Compose layout/gestures for the overlapping tableau.
- **Architecture:** MVVM / unidirectional action and state flow.
- **Game logic:** Pure Kotlin model and rules engine, with no Android UI dependencies.
- **Reactive UI:** StateFlow exposed by a view model.
- **Local persistence:** DataStore for compact state/settings, with schema versioning. Consider Room only if replay history or relational queries warrant it.
- **Testing:** Unit tests for engine; Android instrumentation and Compose UI tests for interactions, responsiveness and lifecycle.
- **Build:** Gradle Kotlin DSL, Android Studio, Git.
- **SDK:** minimum API 26 is provisional; target API at least 36 for standard new Play apps as of 2026-10-29. Recheck at publication.

## Why Compose over libGDX?

Compose integrates native menus, accessibility APIs, app lifecycle, responsive layouts, and Android-specific behaviors. A specialized rendering engine is not necessary for a turn-based card game. Compose's custom board layout and overlapping-card input handling are real engineering risks, so prototype them early. libGDX remains a fallback if measured performance or gesture requirements are not achievable.

## Logical architecture

```text
Player action -> Compose UI -> GameViewModel -> RulesEngine
                       ^             |              |
                       |         StateFlow       New immutable state
                       +-------------+--------------+
                                     |
                               Save repository
                                     |
                              Local persistence
```

- **Model:** immutable `Card`, `TableauColumn`, `GameState`, `GameAction`, `GameResult`. Use stable card IDs and explicit ordering.
- **Rules engine:** validate a requested move; transition state atomically; detect exposures and completed **tableau** runs without extracting them; allow the single reserve deal at any time during play; enumerate legal moves for hints. **Never interpret zero available tableau moves as an automatic loss** (RULE-06).
- **Deal generator:** deterministic shuffle from a seed for replay and debugging.
- **ViewModel:** receives user intents, exposes display state, coordinates animation requests and saves. It does **not** decide move legality.
- **UI:** renders state, performs drag hit-testing, shows errors/feedback, and honors accessibility preferences.
- **Repository:** saves current game, resume state, settings and compact stats with migration/version handling.

## Proposed packages

```text
app/
game/model/
game/engine/
game/analysis/
ui/board/
ui/components/
ui/screens/
ui/theme/
data/
test/
```

Start as one Gradle application module with separate packages; extract an engine module when it adds measurable test or reuse value.

## State and persistence

- Do not let animation state be the source of truth for cards.
- Represent a move, reserve deal and automatic reveal as reversible command transactions. **Do not implement automatic completed-run transfer or a completed-suits card pile** (RULE-02).
- Save a versioned snapshot with the shuffle seed and all tableau/reserve cards, face states, move history and elapsed active gameplay time. Derive completed-suit progress from the tableau rather than storing/removing completed cards.
- Restore logical state before rendering. On an unsupported save schema, recover safely without claiming success.
- Validate card count/identity after load and in engine tests.
- Count active gameplay time only (RULE-03); pause during backgrounding and on win. Details of pauses for modal menus and clock-start timing remain UX-level implementation choices.
- Wins after Undo count normally (RULE-04), without a separate win classification.
- Only Classic Wasp is included in v1.0 (RULE-05); do not introduce a parallel Scorpion ruleset as part of core engine scope.
- **RULE-06:** Model the absence of tableau moves as an observable board condition, **not** an automatic terminal/defeat state. If reserve cards remain, dealing the reserve is still a possible action; after reserve exhaustion, Undo/Restart/New Game must remain possible per UX scope. Only the four-run win condition produces a terminal game outcome in the approved rules. Do not infer deal unsolvability from legal-move enumeration alone.

## Touch and layout

- Seven columns must remain usable on narrow portrait screens.
- Derive geometry from actual available window area and avoid hardcoded pixel assumptions.
- Support both drag/drop and two-step tap selection for legal destinations.
- During dragging, render moving group in a top layer with the original state unchanged until a validated drop.
- Expose meaningful semantics for cards/columns and alternative non-drag accessibility controls.
- Test edge-to-edge system inset behavior and Android 16 navigation.

## Testing strategy

1. Deterministic example-based rules tests.
2. Randomized action sequence invariants and saved-state roundtrips; include zero-tableau-move states with reserve available and exhausted, without an auto-loss transition.
3. Compose gesture and semantics tests.
4. Rotation/process death/background-resume tests.
5. Performance profiling on smaller/midrange phones and at least one large-screen layout.

## Dependencies and governance

Choose exact, compatible stable dependencies at project initialization; do not freeze versions during research. Maintain a dependency and license register. No analytics, ad SDK, cloud, or sign-in libraries in MVP without explicit approval.

## Reference documents

- [Official Android architecture](https://developer.android.com/topic/architecture)
- [Jetpack Compose](https://developer.android.com/compose)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Compose testing](https://developer.android.com/develop/ui/compose/testing)
- [Google Play target API](https://support.google.com/googleplay/android-developer/answer/11926878)
