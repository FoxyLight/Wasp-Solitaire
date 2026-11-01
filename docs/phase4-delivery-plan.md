# Phase 4 delivery plan: from engine to accessible playable board

**Delivery reconciliation (2026-10-31):** 4A, 4B and 4C are merged through owner-approved PRs #18–20. Current main is `6c64b6a4899bb3e0fa7790ff1422470608810b83`; Issue #14 is closed. The owner authorized 4D audit/design Stages 1–3. The [modern classic design package](design/phase4d/README.md), decisions 4D-UX-01–08 and [acceptance matrix](design/phase4d/validation.md) are **proposed, not accepted**. Stop for explicit design approval before 4D application implementation, then retain a separate implementation PR/merge gate. Issue #15 remains open. Earlier authorization language below describes the plan's original gate, not the present status of merged milestones.

**Status:** Proposed execution refinement for review, 2026-10-31. No new product/rules decisions, code changes, implementation authorization, merge approval or release authorization are implied.
**Dependencies:** Phase 3's owner-approved [PR #11](https://github.com/FoxyLight/Wasp-Solitaire/pull/11) merged on 2026-10-31 with required CI green. Beginning Phase 4 application code requires separate owner authorization. The accepted [rules](game-rules.md), [MVP](product-requirements.md), [architecture](architecture.md) and [UX](ux-design.md) remain authoritative.

## Product contract (do not reinterpret)

- Classic Wasp only, 52 cards, seven columns, three-card reserve, initial 12 hidden cards.
- Only engine-validated actions change board state. Any exposed card may move with its entire attached tail (even unordered), same-suit one-rank lower to nonempty columns, or any group to empty columns.
- Reserve can be dealt once at any time during active play, including when legal moves exist.
- Four completed K→A suited runs remain in the tableau; no automatic removal or unwinnable/loss declaration.
- Unlimited in-deal Undo, hints that suggest only legal moves or the undealt reserve; no strategic solver promise.
- Tap-select/tap-destination and dragging both reach every legal move; explicit destination choice, non-color-only feedback and an accessible inspection alternative on cramped displays.
- Active-only time and the four approved statistics belong to Phase 5, not invented stopgap counters.

## Reviewable Phase 4 milestones

Each row is a **separate PR gate** that must pass required GitHub Actions `verify` and `emulator` checks and receive explicit owner merge approval. Later PRs start from the updated default branch after the preceding merge, unless an explicitly reviewed stacked-PR strategy is chosen.

| Milestone | Implement | Objective acceptance evidence | Tracking |
| --- | --- | --- | --- |
| **4A: session/control boundary** | GameViewModel + StateFlow, UI intents and transient selection state, engine-authoritative transitions; design a versioned persistence repository interface for Phase 5 | ViewModel tests show selection/no-op/hint never changes the game, accepted engine transitions publish once, rejected moves never add history, Undo and reserve remain reversible; code has no direct UI tableau mutation; lifecycle/rotation assumptions documented | [#12](https://github.com/FoxyLight/Wasp-Solitaire/issues/12) |
| **4B: board rendering** | Compose seven-column board, readable face-up/down cards, reserve/status, completed-run indicators and selected tail; original/approved-symbol placeholders | Compose/screenshot checks show seven columns, 52-card deal representation, correct 12 hidden cards and in-place completed runs, no clipping or lost controls on representative widths/heights | [#13](https://github.com/FoxyLight/Wasp-Solitaire/issues/13) |
| **4C: fully playable controls** | Explicit tap-to-move and whole-tail drag/drop, legal destination marks, cancel/switch selection; Undo, Hint, Deal 3, Restart, New Game, victory indication | UI tests execute legal/illegal moves using **both** input methods, show/undo reserve and reveal, preserve board/history on invalid drop, and confirm before destructive reset. No auto-defeat or implicit destination | [#14](https://github.com/FoxyLight/Wasp-Solitaire/issues/14) |
| **4D: accessibility and devices** | Expanded column inspection, TalkBack semantics and actions, selection for tightly overlapping cards, responsive window/inset handling | A no-drag user can reach every legal selected-card/destination pair including crowded columns; TalkBack announces rank, suit, column and group; matrix below verified and findings documented | [#15](https://github.com/FoxyLight/Wasp-Solitaire/issues/15) |

### 4A storage seam: avoid the Phase 5 rewrite

- Model accepted transitions as the only write source; keep UI highlighting/selection ephemeral. Publish authoritative session state through StateFlow after coordinated state handling.
- Specify a **versioned session snapshot contract** carrying full tableau order, face states, reserve, seed, successful action count and unbounded previous-state Undo history. The storage port and its error/cancellation contract must anticipate one atomic current-state+Undo write, with no seed-only reconstruction.
- Phase 4's in-memory implementation is **not** a durable autosave and must never claim process-death recovery. Keep persistence operations replaceable by the Room implementation in 5A without changing game rules or screen input APIs.
- Explicitly document when the ViewModel reads/dispatches/rejects intents and how it avoids stale concurrent actions. A selected card is an ephemeral UI choice; the engine remains the authority for move legality.
- Avoid a fake timer, game-start counter, or persistence shortcut that would later require reinterpretation of approved statistics.

## Provisional device and accessibility verification matrix

This matrix is a **test target, not a claim tests have been run or that a particular physical device exists**. Capture actual emulator/device model, OS/API, viewport, font scale, orientation, accessibility mode and pass/fail evidence in each milestone PR. Foldable/resizable windows supplement, not replace, representative phone/tablet testing.

| Test environment | Representative viewport / mode | Required observations |
| --- | --- | --- |
| Narrow portrait phone | ~320dp width (API 26+ representative emulator where supported) | Seven-column overview stays visible; tiny strip selection achievable through expanded inspection; toolbar remains usable |
| Typical portrait phone | ~360–400dp width, normal and large text | Card ranks/suits and legal destinations remain identifiable; drag and tap paths work; no important controls clipped |
| Short landscape phone | ~320–400dp available height, insets/navigation bars | Controls and exposed tail remain reachable; vertical inspection/scroll does not cause accidental moves |
| Large tablet / adaptive window | ~800dp+ width, resized window if available | Board adapts to dimensions; touch targets, empty columns and long tails remain reachable |
| Accessibility pass | TalkBack, 200% font scale (where supported), non-drag navigation, reduced-motion preference | Every legal action accessible without dragging; column/card/tail/destination labels unambiguous; feedback not color- or animation-only |
| Lifecycle smoke | Rotate/resize during a selected card and after a committed move | No board duplication/loss; selection resets safely or remains coherent; do **not** claim cross-process persistence before 5A |

Target at least ~48dp for standalone toolbar/control hit areas. Overlapped card strips may be smaller **only** with a dependable expanded inspection/non-drag selection path. Test contrast of final color pairings and asset rights before final acceptance.

## Phase 5 hand-off, deliberately split

### 5A: durable state and exact recovery

- Implement approved Room storage for **current state plus all Undo snapshots atomically**; no arbitrary Undo cap.
- Include explicit schema versioning, migration and corrupted-snapshot recovery/error strategy. A storage error must not silently discard prior Undo.
- Validate the actual full board, history, reserved cards, action counts and deal identity after process death, app restart, background/rotation and a reserve reveal + Undo.
- Demonstrate offline use and test interruption at write boundaries. Do not count a new game or record a win merely from loading a saved session.

### 5B: active-time, statistics and finishing features

- Implement timer: start only on first *accepted* tableau move or reserve deal, pause in background/blocking UI, stop after win, restore active duration exactly; no wall-clock inflation.
- Exactly **four** displayed statistics: unique deals started, games won (including Undo-assisted), best winning active time, fewest net successful actions in a win. No win-rate/loss/streak tracking.
- Persist stats in Room with exactly-once per-deal win counting; Restart is same deal, New Game starts new deal, repeated Undo/re-win cannot double count.
- DataStore settings (sound default off, haptics optional/system-aware, reduced motion), one green-table theme, win summary, concise feedback and How-to-Play controls, with accessibility tests.

A Phase 5 PR can be split further when test scope or atomicity risk warrants; the 5A/5B labels are delivery gates, not a license to combine unreviewable changes.

## Parallel release-readiness track (not publication)

Follow [issue #16](https://github.com/FoxyLight/Wasp-Solitaire/issues/16) during Phases 4–6 so Phase 7 is not blocked by preventable late work. Collect information about intended individual publisher account, package/title rights, card/logo/audio licenses, real device/tester availability, privacy hosting and current Google Play declarations. **Do not** create accounts, pay fees, register production package identity, sign/release, enroll external testers or publish without specific owner approval. Revalidate time-sensitive Play policies near submission.

## Governance and evidence required for each PR

1. Branch from latest main, name a narrowly defined milestone, link its issue and document the exact approved MVP/UX/ARCH/RULE criteria.
2. Provide repeatable seeded engine examples and positive/negative UI tests. Rejected actions must not mutate board or Undo history.
3. Require both GitHub checks, with actual link and commit SHA; an existing green historical run is not evidence for a new PR. Add targeted device/TalkBack evidence where CI alone cannot prove accessibility.
4. State which functionality is **not yet implemented** (especially durable saves/timing/statistics in Phase 4); record relevant screenshots, tested dimensions and limitations.
5. No automatic merge, rule/UX deviations, production package identity change, external account work or publication. Request explicit owner review/approval before merge.

**Success:** after separately accepted 4A–4D PRs, a player can actually complete a rules-correct Classic Wasp game by touch **or accessible non-drag controls** across representative viewports. Phase 5 remains required for exact offline resume, statistics, timer, full polish and release readiness.
