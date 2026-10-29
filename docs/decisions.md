# Architecture and product decision log

**Status:** Mixed: RULE-01–05 and their corresponding decisions **Accepted** by project owner; other ADRs remain **Proposed**  
**Created:** 2026-10-29  
**Last updated:** 2026-10-29  
**Approval record:** Project owner explicitly approved RULE-01 through RULE-05 in the project conversation on 2026-10-29. This documentation PR records the approval without authorizing app development.

This log is the canonical location for major decisions once reviewed on the default branch. A recommendation is **not** an approved requirement unless its status is explicitly updated to **Accepted** with a date and rationale. When a decision changes, mark the old entry **Superseded** and link to the replacement.

| ID | Decision | Recommendation | Status |
| --- | --- | --- | --- |
| ADR-001 | Project document governance | GitHub `FoxyLight/Wasp-Solitaire`, versioned `docs/` and reviewable changes | Adopted operational convention via repository README; owner requested GitHub location |
| ADR-002 | Initial game mode / RULE-05 | Classic Wasp only; defer Scorpion | **Accepted 2026-10-29** |
| ADR-003 | Reserve timing / RULE-01 | Allow one-time deal at any time during play, even with legal moves or empty columns | **Accepted 2026-10-29** |
| ADR-004 | Completed runs / RULE-02 | Keep complete K→A suited runs on the tableau; do not automatically remove cards | **Accepted 2026-10-29** |
| ADR-005 | Android stack | Kotlin + Jetpack Compose + pure Kotlin game engine | Proposed |
| ADR-006 | Minimum/target API | Provisional min API 26; target at least API 36 under current Play requirement | Proposed / target subject to policy |
| ADR-007 | Persistence | Local-first versioned saves via DataStore; Room only if later requirements justify it | Proposed |
| ADR-008 | Interaction | Drag/drop plus explicit tap-select/tap-destination mode | Proposed |
| ADR-009 | Launch business model | Free, ad-free; no account or backend | Proposed |
| ADR-010 | Initial appearance | Deep green table, high-contrast cards, accessible text/icons | Proposed |
| ADR-011 | Statistics convention | Exclude active games from win-rate denominator; treat restart/abandon distinctly | Proposed |
| ADR-012 | Documentation approval gate | No app code until rules, MVP and technical plan approved | Proposed process, aligned with Phase 1 instructions |
| ADR-013 | Timer / RULE-03 | Count active gameplay time only; pause in background and once won | **Accepted 2026-10-29** |
| ADR-014 | Undo-assisted wins / RULE-04 | Count Undo-assisted victories as normal wins | **Accepted 2026-10-29** |

## Approval template

```text
Decision ID:
Status: Accepted | Rejected | Superseded
Approval date:
Approver:
Choice:
Rationale:
Alternatives considered:
Consequences:
Link to issue / PR:
```

## Outstanding questions

- What exact Android minimum version and test-device lineup are required?
- Will the Play account be personal or organization, and does it already exist?
- Is the no-ads/free policy acceptable for initial release?
- Which card graphic license and application/package name should be used?
- What additional statistics conventions (move counts, abandoned/restarted games, win-rate denominators) and timer UI edge cases should apply? RULE-03 and RULE-04 are accepted; related details are not all decided.

The accepted rules decisions are now fixed for the proposed Classic Wasp ruleset. **All other Proposed entries remain unapproved.** Do not start app development until Phase 1's wider product, architecture, UX, and release decisions have been approved.
