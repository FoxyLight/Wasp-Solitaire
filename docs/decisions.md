# Architecture and product decision log

**Status:** Mixed: RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08 and UX-DEC-01–08 **Accepted** by project owner; commercial, branding/asset-licensing and publication decisions remain **Proposed**  
**Created:** 2026-10-29  
**Last updated:** 2026-10-29  
**Approval record:** Project owner explicitly approved RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08 and then UX-DEC-01–08 on 2026-10-29 for Version 1.0. These records do **not** authorize app development.

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
| ADR-010 | Initial appearance / UX-DEC-01, 02 | Classic green table, ivory readable faces, restrained blue backs, traditional rank/suit symbols; specific card assets/brand licenses pending | **Accepted design direction 2026-10-29** |
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

**Boundary:** Offline gameplay and one theme are approved as MVP features. The Android stack is approved by ARCH-DEC-01–08, and the visual/interaction direction is approved by UX-DEC-01–08. Exact branded assets and licenses, pricing/ad policy, Play Console account and release plan remain pending. No development is authorized by these approvals.

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

**Boundary:** The owner has subsequently approved UX-DEC-01–08, including the classic palette direction and initial reference colors, but **not** a finalized asset pack or branded artwork. Monetization, publishing/account configuration, application ID, library version pins and permission to start coding remain unapproved.

## Approved Version 1.0 UI/UX decisions

The project owner accepted **UX-DEC-01 through UX-DEC-08** on **2026-10-29**, after requesting a review opportunity and then explicitly approving the original recommendations. The complete interaction specification and acceptance scenarios are in [ux-design.md](ux-design.md).

| ID | Topic | Accepted direction | Status |
| --- | --- | --- | --- |
| UX-DEC-01 | Visual direction | Classic green felt-inspired table, warm ivory faces and blue card backs; final contrast-adjusted colors may vary | **Accepted 2026-10-29** |
| UX-DEC-02 | Artwork | Traditional readable rank/suit graphics; original scalable or explicitly licensed assets, license verification required | **Accepted 2026-10-29** |
| UX-DEC-03 | Layout | Seven-column overview, expanded accessible column inspection, portrait/landscape phones and adaptive tablet layout | **Accepted 2026-10-29** |
| UX-DEC-04 | Interaction | Explicit card/tail selection and legal destination highlight; drag/drop; cancel/switch selection; invalid drops leave state unchanged | **Accepted 2026-10-29** |
| UX-DEC-05 | Navigation | Compact Undo/Hint/Deal 3, six core screens, confirmations when Restart/New Game discard in-progress work | **Accepted 2026-10-29** |
| UX-DEC-06 | Accessibility | TalkBack semantics, complete non-drag play, accessible inspection, high contrast and large toolbar touch targets | **Accepted 2026-10-29** |
| UX-DEC-07 | Feedback | Brief unobtrusive animations, optional sound/haptics and system-aware reduced motion | **Accepted 2026-10-29** |
| UX-DEC-08 | Timer UX | First committed move/reserve starts timer; pause in background or blocking screen/dialog; stop on win | **Accepted 2026-10-29** |

**Scope boundary:** The visual direction is accepted, but no individual third-party artwork/license, brand name/icon or publication plan has been approved. Timing targets and palette tokens are initial design references to be tuned through accessibility and device testing. No implementation is authorized.

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
- Which specific card artwork/license, app branding, and application/package ID should be used? UX-DEC-01/02 approve a direction and licensing standard, not an individual supplier or graphic.
- Timer starts on the first successfully committed move or reserve deal and pauses on blocking screens/dialogs (UX-DEC-08); exact monotonic clock implementation is an engineering detail. Advanced win-rate/abandoned/streak definitions are deferred beyond v1.0 under MVP-DEC-04.
- Architecture and UX direction are approved (ARCH-DEC-01–08, UX-DEC-01–08). Package ID, exact art assets/licenses, representative test-device lineup and publishing/monetization still require separate decisions.

RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08 and UX-DEC-01–08 are accepted for Version 1.0. **All remaining Proposed decisions stay unapproved.** Do not start application development until the remaining Phase 1 publishing/business, branding/asset and release prerequisites are approved and the owner explicitly authorizes implementation.
