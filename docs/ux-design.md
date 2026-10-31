# UI/UX specification: approved Version 1.0 baseline

**Status:** **UX-DEC-01–08 Accepted** on 2026-10-29, alongside RULE-01–06, MVP-DEC-01–08 and ARCH-DEC-01–08. REL-DEC-01–08 now approve the free/ad-free release strategy and provisional identity-validation process. Final branding/package/assets, account verification and permission for later implementation milestones remain pending. **Phase 2 setup was authorized on 2026-10-30 and the initial scaffold merged in PR #7; this does not approve later game/board milestones or publication.**  
**Last reviewed:** 2026-10-29  
**Approval record:** [decisions.md](decisions.md)

## Experience goals

Wasp Solitaire should feel like a clear, traditional patience game with restrained native Android polish. Prioritize accurate card information, easy movement, reliable control feedback and low-friction recovery. On small screens, do not make every card illegible just to avoid providing an inspection aid.

## Approved UX decisions

| ID | Area | Accepted Version 1.0 direction |
| --- | --- | --- |
| **UX-DEC-01** | Visual direction | Classic green felt-inspired table, warm ivory faces, restrained blue backs and accessible suit/selection accents. Specific first-pass palette tokens are design starting points, subject to contrast testing. |
| **UX-DEC-02** | Card artwork | Readable traditional rank and suit symbols, prominent corner indices and correct card proportions. Create original scalable assets or use explicitly licensed alternatives; verify rights before incorporating artwork. |
| **UX-DEC-03** | Responsive board | Seven-column overview on phones, plus accessible expanded column inspection when card strips are too narrow. Portrait/landscape phones and adaptive tablets/foldables. |
| **UX-DEC-04** | Movement interactions | Explicit tap selection and destination highlighting; complete attached-group dragging; obvious cancel/switch-selection behavior and unchanged state after invalid drops. |
| **UX-DEC-05** | Navigation and controls | Discoverable compact primary controls and six main screen types; confirm destructive restart/new-game actions while a deal is in progress. |
| **UX-DEC-06** | Accessibility | TalkBack-readable cards, non-drag group movement, adequate toolbar targets, high contrast, non-color cues, card inspection and reduced-motion support. |
| **UX-DEC-07** | Motion and feedback | Restrained, short animations; optional sound/haptics; respect system accessibility and haptics preferences. |
| **UX-DEC-08** | Timer experience | Start on the first successfully committed gameplay action; pause on app background and while blocking screens/dialogs interrupt play; stop after victory. |

These UX choices specify presentation and interaction, **not new game rules**. Engine-owned legality and approved stats remain unchanged.

## Visual system (UX-DEC-01 and UX-DEC-02)

- **Table:** Felt-inspired deep green with uncluttered, subtle texture (if any). Use a clear visual separation between cards and table.
- **Card faces:** Warm ivory/light neutral, high-contrast traditional black/red suit symbols, clear ranks in prominent corners, and consistent geometry.
- **Backs:** Restrained medium/dark blue pattern; recognizably different from faces.
- **Selection, legal destinations and hints:** Distinct but understated outlines or elevation, accompanied by shape/position/labels so feedback is never color-only.
- **Completed run:** Mark a complete suited K→A run **in place** without moving cards to a completed pile (RULE-02).
- **One Version 1.0 theme** per MVP-DEC-06; avoid additional customizations until later scope approval.

### Initial palette reference (not immutable hex requirements)

| Role | First-pass reference |
| --- | --- |
| Table green | `#165443` |
| Light card face | `#F7F4EB` |
| Card-back blue | `#263B62` |
| Red suit | `#B4232C` |

These are approved as a **visual direction/reference**, not prevalidated WCAG color pairings. The final palette may be tuned to satisfy actual contrast and accessibility results, keeping the same visual language. No specific third-party card pack is licensed or approved merely by accepting UX-DEC-02.

## Responsive board (UX-DEC-03)

| Window / device | Accepted behavior |
| --- | --- |
| Narrow portrait phone | Present a seven-column overview with compact gutters and readable indices as far as practical. If overlapping strips become too narrow for reliable individual selection, offer **expanded column inspection/selection**, rather than making card selection impossible. |
| Wide portrait phone | Enlarge cards and exposed card portions with available width. |
| Landscape phone | Use wider cards and a compact toolbar; if height is constrained, support safe vertical inspection/scrolling without obscuring selectable cards. |
| Tablet / foldable / resized app window | Adapt to available window dimensions, expanding columns/cards appropriately and supporting resize/orientation transitions. |

The overview must communicate the whole board, and expanded inspection must let users read/select otherwise tiny card strips. Inspecting a column is only a **second way to select from the same underlying game state**, never an independent rules variant. Use Compose adaptive layout, window insets and size-aware card overlap under ARCH-DEC-06; no hardcoded screen dimensions.

## Movement contract (UX-DEC-04)

### Tap-to-move

1. Tapping any **face-up** tableau card selects that card **plus all attached cards beneath it** (even when the internal tail is unordered).
2. Raise/outline the selected tail and show **all currently legal destination columns**. Empty columns are legal for any face-up group per [game-rules.md](game-rules.md).
3. Tap an explicitly indicated destination to commit. Do **not** silently choose between several valid targets.
4. Tap a different face-up card to switch selection; tap the already-selected card again, or use a cancel/back action, to clear selection without a move.
5. Invalid actions and selection changes do not mutate the logical game state or Undo history.

### Drag/drop

- Visually lift the complete selected tail into a top rendering layer; move the group as one unit.
- Validate the candidate drop via the game engine; accept only legal destinations, then snap/settle into place.
- An invalid drop returns to its original position with brief nonessential feedback and **no saved board change**.
- Avoid dependence on pixel-perfect card-strip precision: column inspection and accessible actions remain supported.

### Reserve, completed runs and no available moves

- Show the three-card reserve as a clear **Deal 3** action. It is available **at any time during active play before being dealt**, even when legal moves or empty columns exist (RULE-01).
- Completed K→A suited runs stay visible and normally selectable until the game is won (RULE-02).
- When no tableau moves are currently enumerated, show at most a neutral informational message (e.g. “No available tableau moves”). **Never label it Lost, Game Over, or Unwinnable** based on move enumeration alone (RULE-06). Keep available reserve and recovery controls usable.
- Hint may indicate one legal tableau move or suggest an undealt reserve when none exists, but it never claims a winning strategy or auto-declares defeat (MVP-DEC-03).

## Navigation and control hierarchy (UX-DEC-05)

The game board gets priority over surrounding menus. Keep primary **Undo**, **Hint**, and **Deal 3** controls easy to find on compact phones. New Game, Restart, Statistics, How to Play and Settings may be reached via an obvious overflow/navigation menu without crowding the tableau.

| Screen | Purpose |
| --- | --- |
| Home / Resume | Continue current saved game, New Game, How to Play; reach Settings. |
| Game board | Seven tableau columns, reserve, primary controls, elapsed active time, move count and suited-run progress **indicator only** (no card storage). |
| How to Play | Clear illustrated examples of selected tails, same-suit one-rank moves, empty columns, reserve, Undo, hints and winning. |
| Statistics | Exactly the four approved MVP statistics: unique games started, games won, best winning active time, fewest successful actions in a win. |
| Settings | Sound, haptics, reduced motion and accessible privacy information. One default theme, not a theme catalog. |
| Win summary | Recognize completed four-run win, show elapsed active time/moves and a New Game action; don't disqualify Undo-assisted wins (RULE-04). |

Confirm **Restart** and **New Game** when they would discard meaningful in-progress play; Restart preserves the original seeded deal, while New Game creates a new deal. Undo remains quickly discoverable and unlimited within the current deal (MVP-DEC-02). No screen or navigation action should cause automatic loss or an unrequested game reset.

## Accessibility contract (UX-DEC-06)

- All legal gameplay must be fully achievable **without dragging**, including selection of otherwise cramped/hidden card strips through expanded inspection and accessible actions.
- Expose each selectable card's rank, suit, column, face state, selection state and attached-tail context through accessibility semantics. Example announcement: “Column 3, eight of hearts, face up; select this card and four cards below it.”
- Expose legal destinations and selected-group movement as accessible actions; do not require a complex touch gesture or only rely on highlighted color.
- Aim for **at least 48dp touch targets** for toolbar controls and stand-alone actions. Overlapped card strips may be smaller in the overview, so provide a reliable larger inspection/selection alternative instead of claiming every strip is a 48dp target.
- Maintain text/icon contrast, distinguish suits by symbols as well as red/black ink, support system font scaling and TalkBack, and respect reduced-motion settings.
- Ensure cards, reserve, Undo, Hint, Settings and the win flow remain usable at representative phone/tablet sizes and under rotation.

These are acceptance requirements. Exact semantics labels, magnification treatment and measurements should be validated with actual devices and accessibility testing.

## Motion, audio and haptics (UX-DEC-07)

| Event | Approved feedback direction |
| --- | --- |
| Select card/tail | Subtle elevation and outline |
| Successful move | Short glide/snap |
| Invalid drop | Gentle return without board mutation |
| Newly revealed face-down card | Brief face flip, if motion is enabled |
| Completed same-suit run | Brief in-place highlight; no removal animation |
| Hint | Brief highlight/pulse of legal source and destination, or reserve |
| Win | Restrained celebration and accessible win summary |

Use an **initial movement-animation target around 120–220 ms**, subject to later device testing, frame performance and reduced-motion policy. This timing is a tunable usability target, not an exact inviolable number.

**Default preferences:** Sound **off**; haptics optional and governed by system preferences; default reduced motion follows the system preference, with an additional in-app reduction option. No sound, vibration, or animation may be required to understand rules or complete the game.

## Active-play timer UX (UX-DEC-08)

- **Start** timing on the **first successfully committed gameplay action**, whether a legal tableau move or the one-time reserve deal. Opening a game, selecting a card, requesting a Hint or entering a menu must not start the clock.
- **Pause** whenever the app is backgrounded or a blocking screen/dialog prevents active play, including How to Play, Settings, Statistics, a blocking menu or a confirmation dialog.
- **Resume** only when the in-progress game board is available for active play again.
- **Stop** once the game enters its won state. Elapsed time should restore exactly after process death/rotation.
- Measure **active play time**, not wall-clock time, under RULE-03. This preserves the approved best winning time statistic (MVP-DEC-04). Exact lifecycle implementation and monotonic-clock bookkeeping are engineering details.

## Asset provenance and licensing

Use original scalable card symbols/art, or alternatives with an explicitly verified license appropriate for the intended distribution. Before importing any third-party pack, record creator, URL, license, attribution requirement and modifications in an asset manifest. **No specific external card pack, app logo, trade name, license or payment for assets is approved by UX-DEC-02.**

A previous research candidate was the [Kenney Playing Cards Pack](https://kenney.nl/assets/playing-cards-pack); it remains a reference only until its actual license and suitability are verified.

## UX acceptance scenarios

1. All seven tableau columns are visible as an overview on a narrow portrait phone; expanded inspection makes tiny selectable cards accessible without changing their game-state meaning.
2. A player can move a mixed-order attached card group by either drag/drop or explicit tap-to-move, including to an empty column.
3. Selecting another card or canceling selection makes no game move; an illegal drop returns without changing state/Undo history.
4. The reserve stays available regardless of legal tableau moves until the one allowed deal. A no-moves state is informational, never an automatic defeat.
5. A completed K→A suit remains on the board; a win produces a summary with the correct active time and moves, and Undo use never disqualifies it.
6. Restart/New Game confirmation prevents accidental loss of in-progress play; Undo and Hint remain easy to reach.
7. TalkBack/large-text/non-drag users can select every legal source/destination, including small overlapped strips, on portrait, landscape and adaptive tablet layouts.
8. Sound-off default, system-aware haptics and reduced-motion preferences are honored; all meaning remains available without motion/sound.
9. The timer starts on the first committed action, pauses on background/blocking screens, resumes on gameplay return, stops on win, and restores after app interruption.
10. Settings and basic statistics operate offline; only the four approved MVP statistical measures are shown.

## Still pending after UX approval

- REL-DEC-02 proposes the **working title Wasp Solitaire**, candidate **FoxyLight** branding and candidate package `com.foxylight.waspsolitaire` subject to legal/account/registration checks; final icon, brand ownership, card art acquisition/license selection and attributions remain unverified.
- REL-DEC-01, 04, 06, 07 and 08 approve free/ad-free/no-tracking Version 1.0, English (US), initial United States market, and account-appropriate testing/rollout strategy. Actual Play Console account, testing eligibility, privacy declarations, contact/policy URLs, store account configuration and publication remain unfinished.
- Representative physical/emulator device matrix, accessibility validation results and final art/contrast/performance tuning.
- Candidate package ID must be verified and formally registered; signing ownership, release assets and app-store configuration remain pending.
- Explicit authorization for Phase 2 implementation, which is **not granted by approving these UX decisions**.

See [game-rules.md](game-rules.md), [product-requirements.md](product-requirements.md), [architecture.md](architecture.md), [decisions.md](decisions.md) and [roadmap.md](roadmap.md).
