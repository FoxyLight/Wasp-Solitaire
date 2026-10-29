# Architecture and product decision log

**Status:** All entries below are **Proposed / Pending approval**  
**Created:** 2026-10-29  
**Owner:** Project owner (approval not yet recorded)

This log is the canonical location for major decisions once reviewed on the default branch. A recommendation is **not** an approved requirement unless its status is explicitly updated to **Accepted** with a date and rationale. When a decision changes, mark the old entry **Superseded** and link to the replacement.

| ID | Decision | Recommendation | Status |
| --- | --- | --- | --- |
| ADR-001 | Project document governance | GitHub `FoxyLight/Wasp-Solitaire`, versioned `docs/` and reviewable changes | Adopted operational convention via repository README; owner requested GitHub location |
| ADR-002 | Initial game mode | Classic Wasp only; defer Scorpion | Proposed |
| ADR-003 | Reserve timing | Allow one-time deal at any time, advise players it is commonly used when stuck | Proposed, needs rules approval |
| ADR-004 | Completed runs | Auto-transfer exposed K→A same-suit runs to a completed-suits area, with atomic Undo | Proposed, needs rules approval |
| ADR-005 | Android stack | Kotlin + Jetpack Compose + pure Kotlin game engine | Proposed |
| ADR-006 | Minimum/target API | Provisional min API 26; target at least API 36 under current Play requirement | Proposed / target subject to policy |
| ADR-007 | Persistence | Local-first versioned saves via DataStore; Room only if later requirements justify it | Proposed |
| ADR-008 | Interaction | Drag/drop plus explicit tap-select/tap-destination mode | Proposed |
| ADR-009 | Launch business model | Free, ad-free; no account or backend | Proposed |
| ADR-010 | Initial appearance | Deep green table, high-contrast cards, accessible text/icons | Proposed |
| ADR-011 | Statistics convention | Exclude active games from win-rate denominator; treat restart/abandon distinctly | Proposed |
| ADR-012 | Documentation approval gate | No app code until rules, MVP and technical plan approved | Proposed process, aligned with Phase 1 instructions |

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

- Are we shipping traditional tableau-completion presentation, or auto-completed suited runs?
- Is reserve dealing restricted to a stuck state, or merely encouraged then?
- What exact Android minimum version and test-device lineup are required?
- Will the Play account be personal or organization, and does it already exist?
- Is the no-ads/free policy acceptable for initial release?
- Which card graphic license and application/package name should be used?
- What precise stats/clock rules should apply?

Do not translate these proposals into irreversible implementation choices until approved.
