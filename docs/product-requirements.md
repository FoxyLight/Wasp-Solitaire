# Product requirements and MVP scope

**Status:** RULE-01–06, Version 1.0 MVP scope (MVP-DEC-01–08) and technical architecture (ARCH-DEC-01–08) **approved** by project owner on 2026-10-29. Branding, monetization, specific UX design and publishing decisions remain proposed  
**Last reviewed:** 2026-10-29  
**Working product name:** Wasp Solitaire for Android

## Vision

A polished, trustworthy, offline-first implementation of Classic Wasp Solitaire for Android phones and tablets. Focus on readable cards, intuitive interactions, accurate mechanics, and reliable resume rather than a large collection of loosely related games.

## Users and positioning

- Intended initial audience: people who enjoy traditional patience games and want an uncomplicated dedicated Wasp experience.
- Positioning hypothesis: clear, distraction-free play is a meaningful improvement over ad-heavy or broad catalog alternatives.
- Demand and monetization are **not validated**. Avoid unsupported claims about market size or revenue potential.

## Initial product principles

1. Correct rules are nonnegotiable.
2. Players should understand why a move succeeds or fails.
3. No internet, account, or external service is needed for core gameplay.
4. Preserve in-progress games across process death and device rotation.
5. Accessibility includes non-drag play; reduced motion is supported.
6. Avoid collecting personal data or adding monetization SDKs without a separate product and policy decision.

## MVP: required for v1.0

| ID | Feature | Acceptance criterion |
| --- | --- | --- |
| MVP-01 | Classic Wasp deal/rules | Complies with approved [game-rules.md](game-rules.md); reproducible deterministic seed |
| MVP-02 | Group moves | Both drag/drop and tap-select/tap-destination support every legal move; no ambiguous automatic target selection; illegal moves have no game-state effect (MVP-DEC-01) |
| MVP-03 | Move guidance | Selected card/group has clearly highlighted legal destinations; tap-to-move lets the player explicitly choose the destination (MVP-DEC-01) |
| MVP-04 | Reserve | One three-card deal **at any time during play** (RULE-01), correct target columns, visible remaining status |
| MVP-05 | Undo, restart, new game | **Unlimited Undo within the current deal**, including undoing reserve dealing and automatic reveals; Undo restores the full preceding logical state. Restart repeats the initial deal; New Game uses a new seed, with discard confirmation when applicable (MVP-DEC-02) |
| MVP-06 | Win detection | Four suited King-to-Ace tableau runs recognized **without removing them** (RULE-02) |
| MVP-07 | Autosave and resume | Offline automatic save after every committed action and safe lifecycle restoration; exact tableau, seed, reserve, Undo history and active-play clock resume after process death/rotation. Completed runs remain on tableau (MVP-DEC-07) |
| MVP-08 | Hint | Highlight one currently legal tableau move; if none and reserve remains, suggest dealing it. No best-move/solvability guarantee or game-state mutation; zero moves must not trigger defeat (MVP-DEC-03, RULE-06) |
| MVP-09 | Basic statistics | Exactly four displayed records at launch: unique games started, games won (including Undo), best winning active-play time, and fewest successful actions in a win. No abandonment/loss rate or streaks in v1.0 (MVP-DEC-04) |
| MVP-10 | Layout | Support portrait and landscape phone layouts plus an adaptive tablet layout; verify on representative narrow and large screens (MVP-DEC-05) |
| MVP-11 | Accessibility | All essential actions possible without dragging; readable cards, labeled controls, accessible alternatives to small overlapping card targets, contrast and reduced-motion checks (MVP-DEC-01, 05, 06) |
| MVP-12 | Tutorial and controls | Clear How to Play, New Game, Restart, Undo, Hint, Settings, and completed-game flow |
| MVP-13 | Offline | All core gameplay, saves, statistics, and settings work without network access; no account required for play (MVP-DEC-07). This does not settle advertising or monetization strategy |
| MVP-14 | Preferences | One visual theme at launch, with optional sound, haptic feedback, and reduced-motion controls (MVP-DEC-06). Specific colors/art assets remain subject to design approval |
| MVP-15 | No automatic defeat | Zero currently available tableau moves does not auto-declare a loss; player can deal remaining reserve or use Undo/Restart/New Game (RULE-06) |

## Deferred from v1.0 (MVP-DEC-08)

- Extra card backs and table themes, advanced statistics (including win rate, abandoned games and streaks), customizable animation speed.
- Deal-number entry/sharing, replay, localization, advanced or strategic hints.
- Scorpion mode and other related patience games (also excluded by RULE-05).
- Daily challenges, achievements, leaderboards, cloud synchronization, solver-certified solvable deals.
- Ads and in-app purchases are **not MVP functionality**; any future monetization is a separate business and compliance decision, not implicitly approved or permanently prohibited.

## Approved MVP decisions and implementation-level acceptance criteria

**Approval date:** 2026-10-29. The project owner explicitly accepted **MVP-DEC-01 through MVP-DEC-08** as proposed for Version 1.0. These feature decisions complement [RULE-01 through RULE-06](game-rules.md). They do **not** authorize implementation or approve art direction, monetization, publishing or repository package ID. The Android technology stack and minimum API were subsequently approved as **ARCH-DEC-01–08**; see [architecture.md](architecture.md).

| ID | Approved decision | Acceptance boundary |
| --- | --- | --- |
| MVP-DEC-01 | Support both drag-and-drop and tap-to-move | Every legal group move works with either input. Tap selection exposes valid destinations and destination choice is explicit; invalid moves never mutate the state. |
| MVP-DEC-02 | Unlimited Undo, including reserve actions | Undo history is unbounded by an arbitrary move-count limit **within the current deal**; each action restores exact prior board, reserve, flips and state. Restart/New Game start a new history. |
| MVP-DEC-03 | Basic legal-move Hint | Display at least one legal tableau move when available, or suggest the undealt reserve when appropriate. No strategic ranking, win guarantee or automatic game loss. |
| MVP-DEC-04 | Four simple statistics only | Unique games started, games won, best winning active-play time and fewest successful actions in a winning game; exclude win rate, abandon counts and streaks from Version 1.0. |
| MVP-DEC-05 | Portrait/landscape phones and adaptive tablets | Essential game controls and card selection remain usable in each layout, including smaller phone windows. |
| MVP-DEC-06 | One theme, optional feedback controls | Ship one usable appearance with sound, haptics and reduced-motion preferences; defer additional themes. |
| MVP-DEC-07 | Fully offline automatic saving and resuming | Offline gameplay/save/resume; save each committed game action and restore exact in-progress game including Undo after app/process interruption. |
| MVP-DEC-08 | Defer advanced features beyond Version 1.0 | No Scorpion, daily challenges, solver guarantees, accounts/sync, achievements/leaderboards, advanced hints, or customization catalogs at launch. |

### Statistics semantics within approved MVP-DEC-04

- **Games started:** increment once for each newly generated deal; reopening, Undo or restarting the **same** deal does not increment it.
- **Games won:** increment once per completed deal; Undo-assisted wins count normally (RULE-04). A replay or restart must not double-count a previously recorded win for the same deal session.
- **Best time:** shortest active gameplay duration among won games; background time excluded (RULE-03). Specific timing for modal UI remains a UX detail to be decided.
- **Fewest moves:** minimum count of successfully committed tableau moves and reserve-deal actions in a win; Undo removes undone actions from the current attempt's net move count. Hints, taps that merely select a card, and invalid moves do not count.
- **No v1.0 loss/abandon metric:** no automatic defeat or loss increment when tableau moves disappear (RULE-06). Win rate, abandonment categorization, streaks and other aggregates are deferred.

### Cross-cutting MVP acceptance cases

1. Randomized, seeded deals respect all 52-card identity and face-state invariants in [game-rules.md](game-rules.md).
2. Arbitrary face-up attached tails can be moved to valid same-suit destinations or empty columns with **both** touch methods.
3. Undo restores board and reserve state after any sequence of moves, the three-card reserve deal, and automatic card reveal; no fixed Undo depth, including after saved-game resume.
4. A Hint is legal in the current state, does not apply a move, and suggests the undealt reserve where no tableau moves exist.
5. Completing four suited King→Ace tableau runs records a win without removing cards; Undo-assisted wins count normally.
6. No tableau moves, whether reserve remains or not, never automatically marks a defeat.
7. Autosave and restore preserve the exact active board, seed, reserve status, Undo history and gameplay clock across process death/rotation.
8. All essential gameplay is possible without dragging, offline, on portrait/landscape phone and tablet layouts.
9. Statistics contain the four agreed measures; repeated relaunch or restart does not inflate the games-started count.

### Still pending outside MVP approval

- Android technology/architecture and minimum API are approved under ARCH-DEC-01–08; exact dependency versions, persistence schemas, and implementation details remain to be selected during development.
- Visual design and asset/licensing choices beyond the approval to provide one theme.
- Pricing, ads, analytics SDKs, future monetization, Play Console account and release preparations.
- Fine UX details such as first-action timing and which dialogs pause the clock.

## Quality/release gates

1. All engine unit tests and randomized invariant checks pass.
2. No known reproducible lost/duplicated-card, rule, save corruption, or release-blocking crash defect.
3. New game, Undo, reserve, win, rotation, and process restoration pass on devices/emulators.
4. The full game is usable without drag gestures.
5. Performance acceptable on chosen representative midrange Android hardware; measure before setting an FPS target.
6. Google Play declarations, testing and visual assets are ready and verified.
7. Product owner approves rules and scope before code work.

## Open questions

Primary player persona, initial language, exact device test matrix, eventual monetization, application/package ID, and menu-specific timer details remain open. Approved technology: Kotlin, Compose, `:app` + `:game-engine`, ViewModel/StateFlow, Room game persistence/Undo/history/stats, DataStore settings (ARCH-DEC-01–08). The Version 1.0 statistics set, unlimited Undo policy, and same-deal restart semantics have been accepted as above. Unresolved decisions belong in [decisions.md](decisions.md).
