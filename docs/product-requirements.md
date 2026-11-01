# Product requirements and MVP scope

**Phase 4D owner amendment (2026-11-01):** [Final approved design and implementation record](phase4d-interface.md) supersedes earlier Hint/expanded-inspection/control-layout proposals. Hint is removed (MVP-08 / MVP-DEC-03); no separate Inspect screen, title or completion display. Two-tap moves use whole-tail selection and explicit destination; illegal destination taps clear selection without a game action. Primary controls are Undo and Deal 3. Full non-drag access remains through direct board accessibility actions. Owner design approval authorizes implementation; merge and publication remain separate gates. Phases 4A–4C are merged through PRs #18–20. Historical text below must be read with this amendment.

**Status:** RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08, UX-DEC-01–08 and REL-DEC-01–08 **approved** by project owner on 2026-10-29. Business/release strategy is settled; actual publisher, branding/package eligibility and assets/licensing remain unverified; beginning Phase 2 setup was separately authorized on 2026-10-30  
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
6. No advertising, analytics/tracking or monetization SDKs in Version 1.0 (REL-DEC-01, 04). Perform a real dependency/data audit before preparing privacy declarations.

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
| MVP-13 | Offline | All core gameplay, saves, statistics, and settings work without network access; no account required for play (MVP-DEC-07). REL-DEC-01/04 also approve a free, ad-free, no-tracking launch policy |
| MVP-14 | Preferences | One green-table visual direction with ivory faces/blue card backs and optional sound/haptic/reduced-motion controls (MVP-DEC-06, UX-DEC-01, 02, 07); exact art asset/license remains unselected |
| MVP-15 | No automatic defeat | Zero currently available tableau moves does not auto-declare a loss; player can deal remaining reserve or use Undo/Restart/New Game (RULE-06) |

## Deferred from v1.0 (MVP-DEC-08)

- Extra card backs and table themes, advanced statistics (including win rate, abandoned games and streaks), customizable animation speed.
- Deal-number entry/sharing, replay, localization, advanced or strategic hints.
- Scorpion mode and other related patience games (also excluded by RULE-05).
- Daily challenges, achievements, leaderboards, cloud synchronization, solver-certified solvable deals.
- Ads, in-app purchases and subscriptions are **excluded by REL-DEC-01** for Version 1.0; any future monetization requires a separate business/compliance decision. Google Play's free-to-paid-download limitation should be considered before future changes.

## Approved MVP decisions and implementation-level acceptance criteria

**Approval date:** 2026-10-29. The project owner explicitly accepted **MVP-DEC-01 through MVP-DEC-08** as proposed for Version 1.0. These feature decisions complement [RULE-01 through RULE-06](game-rules.md). They do **not** authorize implementation or establish a verified/registered application ID. The Android stack/minimum API, UX direction, free/ad-free business model and release strategy were subsequently approved as **ARCH-DEC-01–08**, **UX-DEC-01–08** and **REL-DEC-01–08**; see [architecture.md](architecture.md), [ux-design.md](ux-design.md) and [google-play.md](google-play.md).

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
- **Best time:** shortest active gameplay duration among won games (RULE-03). Under UX-DEC-08, start on first successfully committed tableau move/reserve deal, pause while backgrounded or blocked by menus/dialogs/screens and stop on victory.
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
- Visual direction and interaction model are approved by UX-DEC-01–08; the final original/third-party artwork choice, provenance and licenses remain pending.
- REL-DEC-01–08 now approve the free/ad-free, no tracking SDK launch policy, English (US)/United States initial release, and publication process. Actual Play Console account, privacy page, final branding/package, approved assets, license audit and store preparations **remain incomplete**.
- Detailed performance/contrast verification, asset-specific licensing and final device testing remain outstanding; first-action and blocking-screen timer semantics are approved under UX-DEC-08.

## Quality/release gates

1. All engine unit tests and randomized invariant checks pass.
2. No known reproducible lost/duplicated-card, rule, save corruption, or release-blocking crash defect.
3. New game, Undo, reserve, win, rotation, and process restoration pass on devices/emulators.
4. The full game is usable without drag gestures.
5. Performance acceptable on chosen representative midrange Android hardware; measure before setting an FPS target.
6. Google Play declarations, testing and visual assets are ready and verified.
7. Product owner approves rules and scope before code work.

## Open questions

Primary player persona and exact device test matrix remain open. Launch language/region (English US / United States) and no-monetization Version 1.0 strategy are approved under REL-DEC-01–08; final card assets/licenses, branding rights and actual package registration remain unverified. Wasp Solitaire, FoxyLight and `com.foxylight.waspsolitaire` are **provisional candidates**, not verified store identities. The first committed action and blocking-screen timer behavior are settled by UX-DEC-08. Approved technology: Kotlin, Compose, `:app` + `:game-engine`, ViewModel/StateFlow, Room game persistence/Undo/history/stats, DataStore settings (ARCH-DEC-01–08). The Version 1.0 statistics set, unlimited Undo policy, and same-deal restart semantics have been accepted as above. Unresolved decisions belong in [decisions.md](decisions.md).
