# Architecture and product decision log

**Status:** Mixed: RULE-01–06, MVP-DEC-01–08 and ARCH-DEC-01–08 **Accepted** by project owner; commercial, visual-design and publication decisions remain **Proposed**  
**Created:** 2026-10-29  
**Last updated:** 2026-10-29  
**Approval record:** Project owner explicitly approved RULE-01–06, MVP-DEC-01–08 and, in a subsequent message on 2026-10-29, ARCH-DEC-01–08 for Version 1.0. These records do **not** authorize app development.

This log is the canonical location for major decisions once reviewed on the default branch. A recommendation is **not** an approved requirement unless its status is explicitly updated to **Accepted** with a date and rationale. When a decision changes, mark the old entry **Superseded** and link to the replacement.

| ID | Decision | Recommendation | Status |
| --- | --- | --- | --- |
| ADR-001 | Project document governance | GitHub `FoxyLight/Wasp-Solitaire`, versioned `docs/` and reviewable changes | Adopted operational convention via repository README; owner requested GitHub location |
| ADR-002 | Initial game mode / RULE-05 | Classic Wasp only; defer Scorpion | **Accepted 2026-10-29** |
| ADR-003 | Reserve timing / RULE-01 | Allow one-time deal at any time during play, even with legal moves or empty columns | **Accepted 2026-10-29** |
| ADR-004 | Completed runs / RULE-02 | Keep complete K→A suited runs on the tableau; do not automatically remove cards | **Accepted 2026-10-29** |
| ADR-005 | Android stack / ARCH-DEC-01, 02 | Kotlin + Jetpack Compose with separate pure Kotlin `:game-engine` and Android `:app` modules | **Accepted 2026-10-29** |
| ADR-006 | Android SDK / ARCH-DEC-07 | Minimum API 26; target API 36 or higher, reverified against Play policy before implementation/release | **Accepted 2026-10-29** |
| ADR-007 | Persistence / ARCH-DEC-04, 05 | Room for current game, atomic durable previous-state Undo snapshots and four MVP statistics; DataStore for settings | **Accepted 2026-10-29; supersedes earlier DataStore-only game-save proposal** |
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

## Approved technical architecture decisions

The project owner accepted **ARCH-DEC-01–08** on **2026-10-29**. The accepted technical boundaries and their tradeoffs are documented in [architecture.md](architecture.md).

| ID | Topic | Accepted direction | Status |
| --- | --- | --- | --- |
| ARCH-DEC-01 | Platform and UI | Native Android with Kotlin and Jetpack Compose | **Accepted** |
| ARCH-DEC-02 | Gradle modules | Two modules, `:app` and independent pure Kotlin `:game-engine` | **Accepted** |
| ARCH-DEC-03 | State management | ViewModel, StateFlow and unidirectional data flow | **Accepted** |
| ARCH-DEC-04 | Persistence | Room for saves/Undo/four statistics; DataStore for settings | **Accepted** |
| ARCH-DEC-05 | Undo technique | Durable prior-state snapshots in atomic transactions, no arbitrary fixed Undo-history cap | **Accepted** |
| ARCH-DEC-06 | Board rendering | Adaptive custom Compose layout, gestures and accessible non-drag input | **Accepted** |
| ARCH-DEC-07 | SDK | Minimum API 26; target API 36+ with current-policy recheck | **Accepted** |
| ARCH-DEC-08 | Testing and tools | Gradle, JUnit, Compose UI tests, device/lifecycle tests and GitHub Actions | **Accepted** |

**Boundary:** The owner has **not** approved the specific UI color palette/assets, monetization model, publication/account configuration, application ID, library version pins, or permission to start coding. Implementation details not explicitly covered by an ARCH-DEC remain subject to engineering review.

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

- The minimum Android version is settled (API 26, ARCH-DEC-07), but the specific device test matrix is still to be finalized during UX/release planning.
- Will the Play account be personal or organization, and does it already exist?
- Is the no-ads/free policy acceptable for initial release?
- Which card graphic license and application/package name should be used?
- First-valid-action timer start and pause behavior for modal screens remain UX details. Advanced win-rate/abandoned/streak definitions are deferred beyond v1.0 under MVP-DEC-04.
- Architecture is approved (ARCH-DEC-01–08). Package ID, art assets, representative test-device lineup, and publishing/monetization still require separate approvals.

RULE-01–06, MVP-DEC-01–08 and ARCH-DEC-01–08 are accepted for Version 1.0. **All remaining Proposed decisions remain unapproved.** Do not start application development until Phase 1's UX design specifics, publication/business decisions and explicit authorization to proceed have been approved.
