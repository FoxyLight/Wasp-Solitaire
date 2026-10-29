# UX and visual design direction

**Status:** Specific UI/visual design proposed; RULE-01–06 and MVP-DEC-01–08 accepted as requirements. No final visual direction or layout measurements approved.  
**Last reviewed:** 2026-10-29

## Experience goals

The player should understand the current board at a glance, move cards confidently, and recover from mistakes without friction. Keep the play field uncluttered and reserve prominent screen space for cards rather than decorative UI.

## Starting visual language

- **Proposed, not yet approved:** classic deep green table, light/high-contrast card faces, restrained blue card backs. **Approved MVP-DEC-06:** ship one readable initial theme; palette and exact assets are pending.
- Large corner rank and suit; preserve enough of each overlapped card to identify it.
- Simple native controls for Undo, Hint, Reserve, New Game, and Settings.
- Minimal, purposeful animation: lift on drag, snap to valid target, rebound on invalid drop, flip on reveal, celebration on win.
- **Approved MVP-DEC-06:** expose optional sound and haptics toggles and a reduced-motion preference; default intensities remain to be specified.
- Keep a reduce-motion setting; respect system accessibility preferences when feasible.

## Layout by available space

| Environment | Proposed handling |
| --- | --- |
| Narrow portrait phone | Seven fitted columns; readable top indices; constrained horizontal gutters; additional magnification/selection assistance |
| Wide portrait phone | Larger cards and more visible fanned cards |
| Landscape phone | Compress toolbar while using wider tableau |
| Tablet and foldable | Adapt to window dimensions, not hardcoded device names; roomier card layout |

Allow scrolling or inspection if the screen genuinely cannot display all cards readably; never conceal interactable cards without a discovery mechanism. Handle system bars and edge-to-edge insets for target API 36+.

## Interaction contract

1. Tap a face-up card to select the entire attached tail; show valid destinations.
2. Tap a destination to execute. Never silently choose between multiple viable columns without informing the player.
3. Drag the selected group with a visible elevated preview; release on a valid destination to commit; invalid drops restore without changing game state.
4. For tiny overlapping card regions, provide inspection/selection controls and accessibility actions rather than relying exclusively on precise gestures.
5. Keep Undo and New Game discoverable. Confirm actions that discard in-progress work.
6. For reserve dealing, show remaining count and clarify that the one three-card deal is available **at any time during active play**, even if other moves exist (RULE-01).
7. Indicate completed same-suit K→A runs **in place on the tableau**. Do not animate their removal to a separate pile (RULE-02).
8. Show elapsed **active gameplay** time (RULE-03) and do not mark a win as assisted or disqualified because Undo was used (RULE-04).
9. **RULE-06:** When no tableau moves are currently available, do not display a loss/game-over verdict. If the reserve is undealt, keep its deal action accessible. If exhausted, keep Undo/Restart/New Game accessible as applicable. A proposed informational message is "No available tableau moves," without any claim that the deal is unwinnable.

## Initial screen inventory

- **Home/resume:** Continue, New Game, How to Play, Settings.
- **Game board:** Reserve, tableau columns, completed-suits **progress indicator only** (no card-storage area), moves and active-play time, controls.
- **How to Play:** Short illustrations and examples of group movement, same-suit builds, empty columns, reserve, and winning.
- **Settings:** Theme (if options are available), sound, haptics, reduced motion, privacy link.
- **Statistics:** Four approved Version 1.0 records: unique games started, games won, best winning active-play time and fewest successful actions in a win (MVP-DEC-04). No win-rate, abandon counter, or streak dashboard.
- **Win summary:** Win feedback, completed time/moves, another game.

## MVP-approved interaction and accessibility boundaries

- **MVP-DEC-01:** All legal group moves work with both drag/drop and tap-select/tap-destination. Tap destinations must be explicit rather than silently guessed.
- **MVP-DEC-02:** Unlimited Undo within the current deal, including reserve-deal actions and flips; Undo must remain usable after saving/resuming.
- **MVP-DEC-03:** Hint identifies at least one legal tableau move where possible or suggests undealt reserve when no tableau move is available; it does not assert that a deal is lost or solvable.
- **MVP-DEC-05:** Portrait/landscape phones and adaptive tablet layouts are required, not optional stretch goals.
- **MVP-DEC-06:** One visual theme; optional sound/haptics and reduce-motion controls; additional themes deferred.
- **MVP-DEC-07:** Resume an offline in-progress game after process death/rotation with Undo history intact.
- **MVP-DEC-08:** No advanced strategic hint system, variants, daily challenges, cloud/account flows or theme catalogs for initial release.

## Accessibility requirements

- An alternative to drag-and-drop must support complete gameplay.
- Clear semantics for card rank/suit/face state, selected group and valid destinations.
- Large enough toolbar tap targets; accessible alternatives for overlapped card regions.
- Contrast, large-text preferences, color-independent suit information, reduced motion and screen reader testing.
- Do not rely on color alone to communicate legal/illegal moves.

## Asset strategy

Prefer original assets or assets with an explicit, commercially usable license. Candidate reference: [Kenney Playing Cards Pack](https://kenney.nl/assets/playing-cards-pack) (confirm exact license and attribution requirements before incorporation). Maintain an asset manifest with author, source URL, license, edits, and release suitability. Do not reuse competitor screenshots or copyrighted designs.

## Validation tasks

- Wireframe 7-column layout in narrow portrait and landscape.
- Prototype dragging unordered groups with collision/hit testing.
- Test tap selection and TalkBack access on a real device.
- Verify card readability under actual overlapping/scrolling conditions.
- Agree an animation budget on a representative midrange phone.
