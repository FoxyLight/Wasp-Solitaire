# Classic Wasp Solitaire: rules specification

**Status:** Proposed, awaiting product-owner approval  
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
- References describe dealing the reserve when the player is stuck; whether this is a strict legality condition or strategy advice needs a product decision (see below).

### Completion and win

A complete run is 13 face-up cards of one suit in order K-Q-J-10-9-8-7-6-5-4-3-2-A, with Ace at the exposed end. Four such runs win the game.

The rules references specify the objective as four tableau runs. Some digital versions automatically remove completed runs to a completed-suits display. **This removal is a presentation/game-state convention requiring approval**, not a universally established Wasp rule. The engine should make the chosen behavior deterministic and undoable.

## Pending decisions before the rules become authoritative

| ID | Question | Proposed behavior | Status |
| --- | --- | --- | --- |
| RULE-01 | When can the reserve be dealt? | Allow one deal at any time; explain that dealing when stuck is traditional guidance | Pending |
| RULE-02 | What happens to a complete exposed run? | Automatically transfer to completed-suits area; Undo restores it | Pending |
| RULE-03 | When does the clock run? | Active gameplay only; pause while app is backgrounded | Pending |
| RULE-04 | Are undo-assisted wins recorded normally? | Yes, with explicit stats definitions | Pending |
| RULE-05 | Is the first release Classic Wasp only? | Yes; Scorpion as a later separate variant | Pending |

## Implementation invariants

- Each card appears **exactly once** across tableau, reserve, and completed-runs storage (if enabled).
- Every completed run has exactly 13 distinct cards, same suit, descending K→A.
- The total number of physical cards remains 52 after every action and Undo.
- A group move keeps the entire attached tail intact and preserves its order.
- Only a face-up card can be selected.
- A reserve deal is atomic, happens at most once, and is reversible by Undo if the UI supports Undo for dealing.
- Invalid actions do not change state or history.
- A saved game and its restored version represent precisely the same logical board, score/timer metadata, and available actions.
- New games support deterministic seeding to reproduce reports of incorrect moves.

## Required rule test scenarios

1. Shuffle/deal produces 52 distinct cards and correct face-up masks.
2. Same-suit one-rank-down move accepted; different suit, same rank, or rank gap rejected.
3. Selecting a middle face-up card moves the entire group even when the group is unordered.
4. Moving any face-up group to an empty column succeeds.
5. Moving a face-down card fails.
6. Uncovering a face-down card reveals precisely that card.
7. Reserve deals to exactly columns 1–3, and cannot be repeated.
8. Undo after a reveal, reserve deal, or automatic completion restores the exact prior state.
9. A complete K→A run is recognized; incomplete or mixed-suit runs are not.
10. Property tests/random action sequences preserve card identity/count and legality.

## Notes on accuracy

The historical inventor and publication date of Wasp are not currently substantiated. Do not claim a specific origin in marketing. Do not advertise every random deal as solvable without a verified solver/generator.

See [sources.md](sources.md) for reference links.
