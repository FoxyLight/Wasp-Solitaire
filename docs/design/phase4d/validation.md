# Phase 4D acceptance matrix and implementation risks

**Proposed, not executed against a redesign.** Approval of layouts precedes application work. Baseline CI/device evidence and its limits are recorded in [the design review](README.md#verified-baseline-and-evidence-limits).

| ID | Required acceptance | Evidence required after implementation |
| --- | --- | --- |
| T01 | Every engine-enumerated legal move reachable by direct tap, inspector selection + board tap, and inspector + destination chooser. Include multiple seeded deals and intermediate crowded states. | Compare exact resulting game/revision/Undo snapshots for each path against the engine/controller; no UI rule copy. |
| T02 | Whole unordered tail retained, including mixed suits and nonconsecutive ranks; any face-up source selectable. Hidden cards unavailable. | Engine fixtures plus actual touch and inspection actions; assert identity/order, attached count and reveal. |
| T03 | Empty-column destinations reachable and named; existing in-place completed runs remain intact. | Actual UI path to empty target and retained-run fixture; no foundation transfer. |
| T04 | Illegal destination, stale source/destination, canceled selection/drag and failed repository write make no unintended game/revision/Undo change. | Snapshot assertions and visible error; stale chooser recreated from updated authoritative state. |
| T05 | Reserve available while other legal moves exist and with empty columns; single deal only; Hint fallback correct. | Tap and accessible actions; exact reserve/face/revision/Undo assertions. |
| T06 | Hint does not commit state; Undo reverses moves, reveals and reserve exactly; no arbitrary cap introduced. | Existing tests retained plus new-route equivalence and focus restoration. |
| T07 | Restart/New Game confirm under existing predicates; cancellation and write failure preserve session. | Existing reset tests retained; native dialog title, explicit destructive label, cancel/Back and announcement checks. |
| T08 | Completed suited suffixes stay on tableau; four runs win; victory Undo available. | Exact engine-confirmed fixture, retained cards and no premature win; screenshot and screen-reader announcement. |
| T09 | Long-column inspection exposes every face-up card with readable full row; scroll/footer and selection survive Back. | Longest legal practical fixtures, beginning/middle/end rows, all source indices selected without tiny strips. |
| T10 | Rotation, Activity recreation and resizing preserve in-memory game/history, route, coherent selection and scroll anchor; cancel interrupted drag. | Actual Activity lifecycle tests plus Pixel rotation and tablet/pane-to-sheet transitions; no process-death recovery claim. |
| T11 | 320×640, 360×720, 390×780 portrait and 640×320 / 700×360 short landscape; primary controls stay reachable. | Configuration tests and reviewed actual screenshots with real content; both navigation modes/cutout insets where available. |
| T12 | Tablet widths 800 and 1000dp, resizable breakpoint crossings and short-height large windows. | Pane eligibility uses available board width; no clipped footer or shrunken labels; focus retained when pane becomes modal. |
| T13 | 200% text on narrow portrait and short landscape; all essential labels complete; no font-scale suppression. | Actual rendered screenshots, row measurement and full legal move via inspection; test “10”, long rank/suit and dialog labels. |
| T14 | TalkBack completes legal selection/move, empty target, Hint, reserve, Undo, cancel and confirmed reset. | Hands-on Pixel TalkBack session with recorded exact labels/actions/focus order, no duplicate symbols/hidden identities, correct start/attached count. This cannot be certified solely by semantics tests. |
| T15 | Errors/selection/success and confirmations announced once at useful times; return focus points to stable source/destination. | Native semantics assertions plus screen-reader listening; no live-region spam while dragging or scrolling. |
| T16 | Keyboard and switch-style scanning can traverse/action the same full-row path where supported; no modal trap. | Tab/Shift+Tab, Enter/Space, Back/Escape and physical/emulator input observations, with unsupported hardware explicitly disclosed. |
| T17 | Standalone targets ≥48dp; no overlapping invisible targets; essential meaning independent of color. | Measured layout assertions and visual review of selected, legal, hinted, empty, spent/disabled and focus states. |
| T18 | Contrast and reduced motion validated on final components. | Rendered text ≥4.5:1, meaningful boundaries ≥3:1 as applicable; animation scale disabled/reduced, no disappearing information or blocked input. |
| T19 | Exact final implementation head has required `verify` and `emulator` green. | CI URLs, commit SHA, actual counts/errors/skips, APK/config identity and screenshots in implementation report. No inferred passes or weakened protection. |
| T20 | Owner's Pixel 7a usability check. | Observe identify source → inspect → select unordered group → choose target → move → Undo; repeat at long column, rotation and large text. Record mis-taps, confusion and defects concretely; owner acceptance separately required. |

## Evidence and reporting gates

Retain existing JVM/Compose tests and add behavior tests for the new routes rather than tests that merely repeat layout constants. Export and visually inspect actual screenshots from the tested head; label seeded fixtures, configuration overrides and real physical rotations distinctly. An implemented screenshot must not be replaced by this proposal illustration. Report unresolved limitations and deferred Phase 5 functions. Keep the implementation PR unmerged; close Issue #15 only after owner-approved merge.

## Risks and mitigations

| Risk | Proposed mitigation / acceptance gate |
| --- | --- |
| Dense seven-column overview cannot provide 48dp targets at 320dp | Visible Inspect columns and full-width column/card/destination rows. Never claim all strip targets are 48dp. T01/T09/T14/T17. |
| Readable ranks increase vertical scrolling | Limit permanent chrome; stable dock/rail; full-card list route. Review actual task completion on Pixel. T11/T13/T20. |
| Inspector becomes a second game state | Render immutable StateFlow; stable identity/revision at intent; controller reconciliation only. T01/T04/T10. |
| Changing chrome breaks drag coordinates/drop zones | Compute board-local bounds after insets/scroll; cancel active drag on configuration changes. T02/T04/T10/T11. |
| New accessible grouping hides required actions | One complete column → card → legal-destination route plus visible controls; listen with TalkBack. T14/T15/T16. |
| Large text invalidates fixed height mockup measurements | Intrinsic growing text rows, scrolling surface and measured breakpoint selection. Illustrations are targets, not hard-coded bounds. T13. |
| Android system font/suit rendering differs from SVG preview | Verify actual Compose glyphs, “10” width, line metrics, suit red/black contrast and tails on Pixel and CI. No imported artwork required. T13/T18. |
| Scope too broad for safe review | Separate layout/adaptive changes from inspection/accessibility if needed, retaining a usable tested app in each PR. All gates still apply. |
| Owner mistakes design approval for implementation merge | Record design decision approval explicitly; future implementation starts from current main, gets separate PR review/merge approval. |

## Design-stage checks

The illustrations are original vector compositions using a seed-123 engine export. Check seven columns, 49 tableau entries, 12 hidden masks, three reserve cards and the legal 8♦ → 9♦ example. Validate SVG/XML and unique IDs, inspect all seven rendered views and ensure the proposal has no application, engine, test or workflow changes. Nominal contrast calculations are reproducible with `python docs/design/phase4d/render_mockups.py`; that authoring utility does not run or alter the Android app.
