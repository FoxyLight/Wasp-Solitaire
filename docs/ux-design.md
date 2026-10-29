# UX and visual design direction

**Status:** Proposed, not approved  
**Last reviewed:** 2026-10-29

## Experience goals

The player should understand the current board at a glance, move cards confidently, and recover from mistakes without friction. Keep the play field uncluttered and reserve prominent screen space for cards rather than decorative UI.

## Starting visual language

- Classic deep green table, light/high-contrast card faces, restrained blue card backs.
- Large corner rank and suit; preserve enough of each overlapped card to identify it.
- Simple native controls for Undo, Hint, Reserve, New Game, and Settings.
- Minimal, purposeful animation: lift on drag, snap to valid target, rebound on invalid drop, flip on reveal, celebration on win.
- Sound and haptics default to subtle and optional.
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
6. For reserve dealing, show remaining count and clear that there is only one three-card deal.

## Initial screen inventory

- **Home/resume:** Continue, New Game, How to Play, Settings.
- **Game board:** Reserve, tableau columns, completed-suits area/indicator, moves and time, controls.
- **How to Play:** Short illustrations and examples of group movement, same-suit builds, empty columns, reserve, and winning.
- **Settings:** Theme (if options are available), sound, haptics, reduced motion, privacy link.
- **Statistics:** Explicitly defined game counts and records.
- **Win summary:** Win feedback, completed time/moves, another game.

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
