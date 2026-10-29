# Architecture and product decision log

**Status:** Mixed: RULE-01–06 and MVP-DEC-01–08 **Accepted** by project owner; architecture, commercial, visual-design and publication ADRs remain **Proposed**  
**Created:** 2026-10-29  
**Last updated:** 2026-10-29  
**Approval record:** Project owner explicitly approved RULE-01–06 and, in a subsequent message on 2026-10-29, MVP-DEC-01–08 for Version 1.0. This document records both approval groups without authorizing app development.

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
| ADR-008 | Interaction / MVP-DEC-01 | Drag/drop plus explicit tap-select/tap-destination for every legal move | **Accepted 2026-10-29** |
| ADR-009 | Launch business model | Free, ad-free; no account or backend | Proposed |
| ADR-010 | Initial appearance | Deep green table, high-contrast cards, accessible text/icons | Proposed |
| ADR-011 | Future analytics | Definitions of win rate, abandoned games and streaks are deferred; these metrics are not in v1.0 | **Superseded by MVP-DEC-04 (2026-10-29)** |
| ADR-012 | Documentation approval gate | No app code until rules, MVP and technical plan approved | Proposed process, aligned with Phase 1 instructions |
| ADR-013 | Timer / RULE-03 | Count active gameplay time only; pause in background and once won | **Accepted 2026-10-29** |
| ADR-014 | Undo-assisted wins / RULE-04 | Count Undo-assisted victories as normal wins | **Accepted 2026-10-29** |
| ADR-015 | No automatic defeat / RULE-06 | No legal tableau moves alone must not auto-declare defeat; undealt reserve and non-proof of unwinnability are explicitly recognized | **Accepted 2026-10-29** |

## Approved Version 1.0 MVP decisions

The project owner accepted the following on **2026-10-29**. The scope and testable acceptance criteria are specified in [product-requirements.md](product-requirements.md).

| ID | Decision | Accepted choice | Status |
| --- | --- | --- | --- |
| MVP-DEC-01 | Input methods | Drag/drop plus explicit tap-to-move, both supporting all legal moves | **Accepted** |
| MVP-DEC-02 | Undo | Unlimited within a deal, including reserve actions and automatic card reveals | **Accepted** |
| MVP-DEC-03 | Hint | Highlight one legal move; suggest reserve when applicable; no solver guarantee | **Accepted** |
| MVP-DEC-04 | Statistics | Four only: unique games started, games won, best time, fewest moves | **Accepted** |
| MVP-DEC-05 | Devices | Portrait and landscape phones; adaptive tablet layout | **Accepted** |
| MVP-DEC-06 | Customization | One initial visual theme; optional sound, haptic feedback, reduced motion | **Accepted** |
| MVP-DEC-07 | Saving | Fully offline autosave and precise resume | **Accepted** |
| MVP-DEC-08 | Exclusions | Defer advanced modes, challenges, solver, cloud, large theme catalog and advanced statistics | **Accepted** |

**Boundary:** Offline gameplay and one theme are approved as MVP features; this does not approve the Android technology stack, exact visual identity, pricing/ad policy, Play Console account, or release plan. No development may start without additional approvals.

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
- First-valid-action timer start and pause behavior for modal screens remain UX details. Advanced win-rate/abandoned/streak definitions are deferred beyond v1.0 under MVP-DEC-04.
- Native Android architecture, package ID, art assets, target testing devices and publishing/monetization still require separate approvals.

RULE-01–06 and MVP-DEC-01–08 are accepted for Version 1.0. **All remaining Proposed entries stay unapproved.** Do not start application development until Phase 1's architecture, UX design specifics, and release decisions have been approved.
