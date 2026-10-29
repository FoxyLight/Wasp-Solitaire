# Product requirements and MVP scope

**Status:** MVP and architecture proposed; RULE-01–06 approved by owner on 2026-10-29  
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
| MVP-02 | Group moves | Tap selection and drag/drop both permit all legal moves; illegal moves are rejected without state change |
| MVP-03 | Move guidance | Selection exposes valid destinations; ambiguous tap-to-move is resolved explicitly |
| MVP-04 | Reserve | One three-card deal **at any time during play** (RULE-01), correct target columns, visible remaining status |
| MVP-05 | Undo, restart, new game | Complete atomic Undo; restart repeats same initial deal; new game generates a new seed with confirmation where needed |
| MVP-06 | Win detection | Four suited King-to-Ace tableau runs recognized **without removing them** (RULE-02) |
| MVP-07 | Autosave and resume | Exact tableau, seed, move history, reserve state, and statistics restored; completed-run status is derived from cards **remaining on tableau** |
| MVP-08 | Hint | Highlights at least one **legal** move; does not promise strategic optimality |
| MVP-09 | Basic statistics | Counts wins, starts, abandons, timing, and moves; **Undo-assisted wins count normally** (RULE-04); timer counts active play only (RULE-03); other definitions pending |
| MVP-10 | Layout | Playable in portrait and landscape across representative phone sizes; tablets supported where practical |
| MVP-11 | Accessibility | Essential interaction possible without dragging; labeled controls, readable card indices, contrast and reduced-motion checks |
| MVP-12 | Tutorial and controls | Clear How to Play, New Game, Restart, Undo, Hint, Settings, and completed-game flow |
| MVP-13 | Offline | All game and settings flows work in airplane mode; no required account |
| MVP-14 | Preferences | Simple visual theme with sound and haptics toggles; sensible defaults |
| MVP-15 | No automatic defeat | Zero currently available tableau moves does not auto-declare a loss; player can deal remaining reserve or use Undo/Restart/New Game (RULE-06) |

## Deferred until user feedback or a new scope approval

- More card backs and table themes, advanced statistics, customizable animation speed.
- Deal-number entry/sharing, replay, localization, advanced hints.
- Scorpion mode and other related patience games.
- Daily challenges, achievements, leaderboards, sync, optional purchases, and solver-certified solvable deals.

## Approved rules decisions and remaining product proposals

**Accepted rules scope (2026-10-29):** Classic Wasp only for v1.0 (RULE-05); reserve available once at any time (RULE-01); completed sequences stay on the tableau (RULE-02); active gameplay time only (RULE-03); Undo-assisted wins count normally (RULE-04); never automatically declare defeat solely on no available tableau moves (RULE-06). See [game-rules.md](game-rules.md) and [decisions.md](decisions.md).

**Still proposed, not approved:**

- Free, ad-free, offline-first launch; no backend or sign-in.
- Kotlin / Compose Android-native implementation.
- Minimum API 26 as provisional compatibility target; Google Play target API must be verified for submission.
- Default table is classic green with high-readability cards.
- The remaining detailed MVP features and the full release plan.

## Analytics and statistics definitions (proposed)

- **Games started:** count a newly dealt board, not each app launch.
- **Games won:** completed boards, **including games in which Undo was used** (RULE-04).
- **Abandoned:** newly starting/restarting without completing previous board; separately count restarts if desired.
- **Win rate:** games won / (games won + abandoned), excluding active in-progress games.
- **Best time:** active gameplay duration for a completed board (RULE-03); exact menu/first-action edge-case handling remains for UX specification.
- **Fewest moves:** proposed: player actions for a completed board, with dealing the reserve counted as an action; Undo should not inflate the count. This move-count convention is **not yet approved**.
- Do not imply in-progress deals are losses. **RULE-06:** absence of immediately available tableau moves does not justify marking a game as lost, even after the reserve is spent. The player can choose another action according to normal controls.

## Quality/release gates

1. All engine unit tests and randomized invariant checks pass.
2. No known reproducible lost/duplicated-card, rule, save corruption, or release-blocking crash defect.
3. New game, Undo, reserve, win, rotation, and process restoration pass on devices/emulators.
4. The full game is usable without drag gestures.
5. Performance acceptable on chosen representative midrange Android hardware; measure before setting an FPS target.
6. Google Play declarations, testing and visual assets are ready and verified.
7. Product owner approves rules and scope before code work.

## Open questions

Who is the primary player persona? Which languages and device classes are mandatory? What is the eventual monetization policy? What is the approved package/application ID? What is the preferred statistics model for restart and Undo? Answers should be captured in [decisions.md](decisions.md).
