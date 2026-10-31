# Phase 3: Classic Wasp engine

Owner-authorized 2026-10-31 after completing repository protection and fresh Phase 2 verification. Implementation merged into `main` through [PR #11](https://github.com/FoxyLight/Wasp-Solitaire/pull/11) on 2026-10-31 after required checks passed. Phase 4 implementation and publication are separate authorization gates.

## Approved requirements

Implements [Classic Wasp rules](game-rules.md), RULE-01/02/04/05/06, MVP-01 through MVP-06 (engine portions), MVP-08, MVP-15, MVP-DEC-02/03 and ARCH-DEC-02/03/05/08. [Product requirements](product-requirements.md), [architecture](architecture.md), [decisions](decisions.md), [roadmap](roadmap.md) and [UX](ux-design.md) were consulted before implementation. Existing accepted product decisions are unchanged.

## Capabilities and API

- `ClassicWasp.newGame(seed)` creates 52 unique cards, seven seven-card columns and a three-card reserve. Columns 0–3 have three hidden cards each; the others are entirely exposed.
- The shuffle uses explicit Fisher-Yates with Java's specified `Random(seed)` algorithm over suit declaration order, ranks Ace through King. Dealing is column-major, a documented deterministic implementation choice where the specification does not mandate a shuffle/deal algorithm. Never reconstruct a saved board solely from a seed: persist the actual state.
- Zero-based columns and card indices; card lists run from deepest to exposed. Selecting any face-up card moves its whole tail, without inspecting internal suit/rank order. Nonempty destinations require same suit and exactly one rank higher; empty columns accept any exposed group.
- `ClassicWasp.apply(session, action)` returns `Transition.Accepted` with a new session or `Rejected` with the **same original session** and an explicit reason. Moves automatically expose a newly uncovered card. Reserve dealing is one atomic action, to columns 0–2 regardless of empty columns or legal moves.
- `GameState.completedRuns` recognizes exposed face-up suited K-to-A suffixes in place. Four runs account for all 52 unique cards and derive `WON`; no cards are removed. Completed runs remain selectable before victory. Gameplay stops after victory, with Undo still allowed.
- `GameAction.Undo` restores the exact prior state, including reserve, reveals and net action count, without any fixed history cap. Undoing a win and winning again is allowed; this engine does not disqualify Undo-assisted wins.
- `legalMoves` enumerates all legal tableau actions in stable source/index/destination order; `hint` chooses the first, otherwise the available reserve, otherwise null. Neither changes state/history. There is no strategy ranking, solver, reshuffle or automatic loss state.
- `GameState.restore` validates seven columns, all 52 unique cards, reserve size, hidden-prefix/exposed-card structure and nonnegative action count. Collection inputs are deeply copied into unmodifiable lists. State has value equality without a public unchecked `copy` path.
- `GameSession.restore(state, history)` accepts ordered previous-state snapshots, checks deal seed and consecutive action counts, and defensively copies history. Persistence adapters must save the **complete** history and board atomically; this structural validation is not a proof that arbitrary externally supplied history is reachable by legal play. Treat persisted input as versioned and validate it before publishing.

The engine has no Android dependencies or new libraries. The ViewModel should validate a user intent, atomically persist an accepted state plus its previous snapshots through Room, then publish through StateFlow. Invalid actions and hints must not be persisted as moves. Snapshots retain seed and net successful action count. This milestone does not implement lifecycle timing: the future app layer must persist active time alongside the engine session, start on the first accepted gameplay action, pause on blocking UI/background and stop on victory (RULE-03, UX-DEC-08). Statistics must count each deal's win only once.

## Verification

The deterministic JUnit suite covers initial counts/masks/order, fixed seeded shuffle output independently checked with Java Collections.shuffle, accepted and rejected placements, unordered tails, empty columns, invalid indices/hidden cards, precise reveal/Undo, reserve targeting/repeat/Undo, in-place complete and malformed runs, victory/Undo-assisted re-win, no-move states with/without reserve, exhaustive enumeration versus validation, legal/nonmutating hints, collection alias/mutation resistance and invalid restoration.

Twenty seeded action sequences each exercise up to 160 moves/deals, checking card identities/count, rejected actions, exact single-step Undo and full-history unwind, including snapshot restoration. This tests rules and invariants, not guaranteed deal solvability. Run `./gradlew --no-daemon :game-engine:test`; Android build and instrumentation remain in the unchanged required CI workflow. Fresh Phase 2 baseline evidence: [run 41229459279](https://github.com/FoxyLight/Wasp-Solitaire/actions/runs/41229459279).

## Remaining work and next milestone

The proposed [Phase 4 plan](phase4-delivery-plan.md), subject to owner approval, should connect the engine to a GameViewModel/StateFlow and build the adaptive seven-column board with explicit tap destinations, whole-tail dragging, accessible column inspection, Undo/Hint/Deal 3 and in-place run indicators. Add integration tests proving UI actions use engine legality and preserve state on invalid input across portrait, landscape and tablet layouts. Room encoding/transactions, durable saves/history, lifecycle timer, four statistics and preferences follow in Phase 5. No accounts, production identity/signing changes or store publishing are included here.
