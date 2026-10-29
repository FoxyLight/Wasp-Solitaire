# Classic Wasp Solitaire: rules specification

**Status:** RULE-01 through RULE-06 accepted by project owner on 2026-10-29; broader Phase 1 approvals remain pending  
**Last reviewed:** 2026-10-29  
**Primary references:** [Semicolon Wasp](https://www.semicolon.com/Solitaire/Rules/Wasp.html), [BVS Wasp](https://www.bvssolitaire.com/rules/wasp.htm), [Semicolon Scorpion](https://www.semicolon.com/Solitaire/Rules/Scorpion.html).

## Rules baseline

Wasp is a single-deck patience game related to Scorpion and Yukon. It uses 52 unique cards, 13 ranks in each of four suits, no Jokers, seven tableau columns, and a three-card reserve. The objective is to arrange the deck into four complete same-suit runs descending **King through Ace**.

### Initial deal

- Shuffle all 52 cards.
- Deal seven columns of seven cards each (49 tableau cards).
- In columns 1–4, three cards are face-down and four are face-up. In columns 5–7, all seven are face-up.
- Keep the final three cards as an undealt reserve.
- Initial totals: **12 face-down tableau + 37 face-up tableau + 3 reserve = 52**.
- Define column and card indexing unambiguously in the implementation: columns 1–7 from left to right; list order from the deepest (topmost illustrated) card to the exposed (bottommost illustrated) card.

### Legal moves

1. Select any **face-up** card in a tableau column.
2. That selected card **and every card after it toward the exposed end of its column** form an indivisible moving group. The group's internal cards **need not** be suit-ordered.
3. To place the group on a nonempty destination, its selected/leading card must have the **same suit** as the exposed destination card and be **exactly one rank lower**. Example: 8♥ may move onto 9♥.
4. To place the group on an **empty** tableau column, **any face-up selected card and attached group** is allowed. This is the key difference from Scorpion, whose empty columns require a King-led group.
5. No movement of face-down cards and no move from a column onto itself.
6. After a group moves away, if a face-down card has become exposed, turn that newly exposed card face-up.
7. Illegal moves leave the state unchanged.

### Reserve

- Three undealt cards are kept face-down until the one allowed reserve deal.
- A reserve deal distributes these cards **face-up** onto the exposed ends of columns 1, 2, and 3, respectively.
- No reshuffle or redeal.
- **RULE-01 (accepted):** The player may deal the reserve **at any time while the game is in progress**, including while legal tableau moves exist or columns are empty. "Deal when stuck" is strategic advice, not a legality condition.

### Completion and win

A complete run is 13 face-up cards of one suit in order K-Q-J-10-9-8-7-6-5-4-3-2-A, with Ace at the exposed end. Four such runs win the game.

- **RULE-02 (accepted):** Completed suited runs **remain in their tableau columns**. Do not automatically extract cards to foundations or a completed-suits pile, and do not free a column just because a run is complete.
- A completed run does not create a new movement restriction: until the game is won, ordinary legal moves (including selecting a face-up card within a completed run) remain available.
- The game ends in victory when the tableau contains all four complete 13-card same-suit descending runs, accounting for all 52 cards. With no cards removed, three of the seven columns will be empty. The UI may show a completed-suit **indicator**, not a separate card-storage area.

### No legal tableau moves and loss detection

- **RULE-06 (accepted):** A game **must not automatically declare defeat** merely because no legal tableau move is currently available. A lack of immediately available moves is not sufficient proof that the deal is unwinnable.
- When the reserve is still undealt, the player can still use the one-time three-card reserve deal (RULE-01), even if there are no legal tableau moves.
- Even when the reserve has already been dealt and no tableau move is available, **do not automatically mark the game as lost**, record a loss, or display a definitive "unwinnable" verdict based on that condition alone. Undo, Restart and New Game remain available under their separately specified behaviors.
- A UI may indicate **"No available tableau moves"** or a similar strictly factual state. Such a message is **not** a defeat declaration or a solver-certified judgment.
- No generalized automatic loss/unsolvability detection is part of the accepted Classic Wasp rules. A future solver would require separate specification and validation.

### Timer and statistics conventions

- **RULE-03 (accepted):** Count **active gameplay time only**. Pause while the game is backgrounded and after the game is won. **UX-DEC-08 (accepted 2026-10-29)** specifies the UI semantics: start timing on the first successfully committed tableau move or reserve deal, pause while blocking menus/screens/dialogs interrupt play, and stop at victory. These UX details preserve the approved active-time rule.
- **RULE-04 (accepted):** A win after using Undo counts **normally** as a victory, including win totals, win rate, and streaks. Undo does not disqualify a result. Other statistics definitions (denominators, abandonment, and move-count conventions) remain separate proposed product decisions.

## Approved rules decisions

The project owner explicitly approved these six decisions on **2026-10-29**. They are recorded on the default branch through merged PR #2; their approval does **not** approve the entire Phase 1 plan or authorize app development.

| ID | Question | Accepted behavior | Status |
| --- | --- | --- | --- |
| RULE-01 | When can the reserve be dealt? | One reserve deal at any time during play | Accepted |
| RULE-02 | What happens to a complete exposed run? | Keep it in its tableau column; no automatic removal | Accepted |
| RULE-03 | When does the clock run? | Active gameplay time only | Accepted |
| RULE-04 | Are Undo-assisted wins recorded normally? | Yes, count them as ordinary wins | Accepted |
| RULE-05 | Is the first release Classic Wasp only? | Yes; other variants such as Scorpion are out of initial scope | Accepted |
| RULE-06 | Should no immediately available tableau move automatically trigger defeat? | No; reserve may remain, and no-move detection alone is not proof of unwinnability | Accepted |

### Initial release scope

**RULE-05 (accepted):** Version 1.0 has **Classic Wasp only**. Scorpion and other variants may be considered later, but are not part of the approved rules for the initial mode.

## Implementation invariants

- Each card appears **exactly once** across the tableau and undealt reserve; there is no completed-runs storage.
- A recognized completed run has exactly 13 distinct face-up cards, same suit, descending K→A, and remains in its tableau column.
- The total number of physical cards remains 52 after every action and Undo.
- A group move keeps the entire attached tail intact and preserves its order.
- Only a face-up card can be selected.
- A reserve deal is atomic, happens at most once regardless of tableau moves/empty columns, and is reversible by **unlimited Undo within the current deal (MVP-DEC-02, accepted 2026-10-29)**.
- Invalid actions do not change state or history.
- A saved game and its restored version represent precisely the same logical board, score/timer metadata, and available actions.
- New games support deterministic seeding to reproduce reports of incorrect moves.
- Zero legal tableau moves is **not** a loss condition. No automatic state transition to defeat occurs solely from a zero-move enumeration, regardless of reserve state.

## Required rule test scenarios

1. Shuffle/deal produces 52 distinct cards and correct face-up masks.
2. Same-suit one-rank-down move accepted; different suit, same rank, or rank gap rejected.
3. Selecting a middle face-up card moves the entire group even when the group is unordered.
4. Moving any face-up group to an empty column succeeds.
5. Moving a face-down card fails.
6. Uncovering a face-down card reveals precisely that card.
7. Reserve deals to exactly columns 1–3 even when legal tableau moves exist or a column is empty, and cannot be repeated.
8. Undo after a reveal or reserve deal restores the exact prior state.
9. A complete K→A run is recognized in-place; no cards are removed, and incomplete or mixed-suit runs are not counted.
10. When four completed runs exist, all 52 cards remain on the tableau and the game enters the won state.
11. An Undo-assisted victory is counted in normal win statistics.
12. The active-time clock starts on the first successfully committed gameplay action, pauses during backgrounding or blocking screens/dialogs, resumes when gameplay becomes available, and stops after victory (RULE-03; UX-DEC-08).
13. Property tests/random action sequences preserve card identity/count and legality.
14. When no legal tableau moves exist but the reserve is undealt, the game remains in progress and allows the reserve deal.
15. When no legal tableau moves exist and the reserve has been dealt, the game does not automatically declare defeat or increment a loss statistic; recovery controls remain available.
16. A no-moves notification never states that the game is provably unwinnable, and a won state is determined only by the completed four tableau runs.

## Notes on accuracy

The historical inventor and publication date of Wasp are not currently substantiated. Do not claim a specific origin in marketing. Do not advertise every random deal as solvable without a verified solver/generator.

See [sources.md](sources.md) for reference links.
