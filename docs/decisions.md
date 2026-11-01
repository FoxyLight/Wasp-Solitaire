# Architecture and product decision log

**Phase 4D owner amendment (2026-11-01):** [Final approved design and implementation record](phase4d-interface.md) supersedes earlier Hint/expanded-inspection/control-layout proposals. Hint is removed (MVP-08 / MVP-DEC-03); no separate Inspect screen, title or completion display. Two-tap moves use whole-tail selection and explicit destination; illegal destination taps clear selection without a game action. Primary controls are Undo and Deal 3. Subsequent owner refinements remove visible column headings, enlarge rank/suit 20%, place rank left and suit right, and tighten overlap just below their row; accessible column identifiers and the narrow-card 200% text stacked fallback remain. The owner approved the revised mockup before implementation. The latest owner-approved mockup supersedes dotted destination outlines with RGB inversion of only the bottom exposed legal destination card, tighter reference-based spacing, optically centered bold sans ranks and distinct trefoil clubs. Empty slots stay unoutlined and playable. Full non-drag access remains through direct board accessibility actions. Owner design approval authorizes implementation; merge and publication remain separate gates. Phases 4A–4C are merged through PRs #18–20. Historical text below must be read with this amendment.

**Status:** RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08, UX-DEC-01–08 and REL-DEC-01–08 **Accepted** by project owner on 2026-10-29. Provisional app identity and real-world account, trademark, package, asset and release verification remain outstanding.  
**Created:** 2026-10-29  
**Last updated:** 2026-10-29  
**Approval record:** Project owner explicitly approved RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08, UX-DEC-01–08 and then REL-DEC-01–08 on 2026-10-29 for Version 1.0, including a provisional identity and account process subject to verification. These Phase 1 decisions did **not themselves** authorize development, account actions or publication. The owner subsequently authorized **beginning Phase 2 setup on 2026-10-30**; no account or publication action has been authorized.

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
| ADR-009 | Launch business model / REL-DEC-01, 04 | Free and ad-free, no IAP/subscriptions, tracking or required player account; offline gameplay and accurate privacy disclosures | **Accepted 2026-10-29** |
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
| MVP-DEC-03 | Hint | Removed by explicit owner instruction and final Phase 4D design approval; destination highlighting remains | **Superseded 2026-11-01** |
| MVP-DEC-04 | Statistics | Four only: unique games started, games won, best time, fewest moves | **Accepted** |
| MVP-DEC-05 | Devices | Portrait and landscape phones; adaptive tablet layout | **Accepted** |
| MVP-DEC-06 | Customization | One initial visual theme; optional sound, haptic feedback, reduced motion | **Accepted** |
| MVP-DEC-07 | Saving | Fully offline autosave and precise resume | **Accepted** |
| MVP-DEC-08 | Exclusions | Defer advanced modes, challenges, solver, cloud, large theme catalog and advanced statistics | **Accepted** |

**Boundary:** Offline gameplay and one theme are approved as MVP features. Architecture is approved by ARCH-DEC-01–08, UI/UX by UX-DEC-01–08, and the **free/ad-free launch and publication strategy** by REL-DEC-01–08. Actual publishing account, brand rights, package ID, assets/licenses, signed releases and owner authorization remain unverified/unexecuted.

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

**Boundary:** The owner subsequently approved UX-DEC-01–08 and REL-DEC-01–08, including the free/ad-free launch plan and a **provisional identity-validation process**. Final asset ownership/licenses, developer account, application ID registration, library versions and authorization to code remain unverified or unapproved.

## Approved Version 1.0 UI/UX decisions

The project owner accepted **UX-DEC-01 through UX-DEC-08** on **2026-10-29**, after requesting a review opportunity and then explicitly approving the original recommendations. The complete interaction specification and acceptance scenarios are in [ux-design.md](ux-design.md).

| ID | Topic | Accepted direction | Status |
| --- | --- | --- | --- |
| UX-DEC-01 | Visual direction | Classic green felt-inspired table, warm ivory faces and blue card backs; final contrast-adjusted colors may vary | **Accepted 2026-10-29** |
| UX-DEC-02 | Artwork | Traditional readable rank/suit graphics; original scalable or explicitly licensed assets, license verification required | **Accepted 2026-10-29** |
| UX-DEC-03 | Layout | Seven-column overview, readable strips/shared scrolling and direct board accessibility actions; no separate inspection screen | **Accepted 2026-10-29; revised 2026-11-01** |
| UX-DEC-04 | Interaction | Whole-tail selection then legal destination tap; illegal tap clears only selection; engine-validated drag remains | **Accepted 2026-10-29; revised 2026-11-01** |
| UX-DEC-05 | Navigation | Persistent Undo/Deal 3; Menu for Restart/New Game/Help with confirmations. No Hint, title or completion display | **Accepted 2026-10-29; revised 2026-11-01** |
| UX-DEC-06 | Accessibility | TalkBack source/destination actions on the board, complete non-drag play, high contrast, large standalone controls and keyboard support | **Accepted 2026-10-29; revised 2026-11-01** |
| UX-DEC-07 | Feedback | Brief unobtrusive animations, optional sound/haptics and system-aware reduced motion | **Accepted 2026-10-29** |
| UX-DEC-08 | Timer UX | First committed move/reserve starts timer; pause in background or blocking screen/dialog; stop on win | **Accepted 2026-10-29** |

**Scope boundary:** The visual direction is accepted; REL-DEC-01–08 now approve the **publishing strategy**, including a provisional app-name/brand/package candidate. No specific third-party artwork/license, trademark rights, verified account or package registration is established. Timing/palette details require accessibility/device validation. No implementation is authorized.

## Approved Version 1.0 publishing and release decisions

The project owner explicitly approved **REL-DEC-01 through REL-DEC-08** on **2026-10-29**, including provisional identity/account details **subject to verification**. Requirements and release gates are defined in [google-play.md](google-play.md).

| ID | Area | Accepted policy or process | Status |
| --- | --- | --- | --- |
| REL-DEC-01 | Business model | Free, ad-free, without IAP/subscriptions or monetization SDKs in v1.0; future changes need new approval | **Accepted 2026-10-29** |
| REL-DEC-02 | Product identity | Working title **Wasp Solitaire**; **FoxyLight** branding and `com.foxylight.waspsolitaire` as **candidates only**, pending rights, account and registration checks | **Accepted provisionally 2026-10-29** |
| REL-DEC-03 | Publisher/account | Owner-controlled, verified Play Console account; verify whether personal/organization and who owns it before setup | **Accepted process; account not verified** |
| REL-DEC-04 | Privacy | Local offline-first storage, no tracking SDKs, accurate privacy/Data Safety disclosures and public app-specific privacy policy | **Accepted 2026-10-29** |
| REL-DEC-05 | Assets | Original or appropriately licensed assets, recorded provenance, genuine compliant store assets and screenshots | **Accepted process; assets/licenses not selected** |
| REL-DEC-06 | Distribution | English (US), initial United States release; expansion only through later decision | **Accepted 2026-10-29** |
| REL-DEC-07 | Testing | Internal and account-dependent closed testing, representative device/accessibility QA, release-candidate verification and staged production rollout | **Accepted process; no tests completed** |
| REL-DEC-08 | Governance | Recheck live Play rules, use formal release checklist and obtain **separate owner go/no-go for publication**; **no Phase 2 authorization** | **Accepted 2026-10-29** |

**Scope boundary:** Acceptance records the **strategy and validation gates**, not actual developer-account creation, verified account category/ownership, permission to use FoxyLight, availability/registration of `com.foxylight.waspsolitaire`, asset licenses, privacy-policy publication, working Android software or store submission. **The owner later authorized beginning Phase 2 setup on 2026-10-30**; no Play Console registration, publication or unreviewed merge is authorized.

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

## Outstanding prerequisites (release strategy approved, checks incomplete)

- Confirm actual Play Console publisher/account, account category, identity verification, signing/control and support contact. **An account has not been verified here.**
- Verify ownership and legal suitability of the FoxyLight brand and Wasp Solitaire title; select/register the final package ID. `com.foxylight.waspsolitaire` is only a candidate.
- Select or create card/art/icon/font/audio assets, verify distribution licenses, and maintain the asset provenance manifest.
- Identify public privacy-policy URL and contact information, then validate data practices and Google Play declarations against an actual app build.
- Finalize representative devices/testers and confirm any account-dependent Play Console test duration or production-access requirements.
- Recheck target API and all time-sensitive Play policies before implementation and again before submission.
- **Phase 2 setup authorization granted 2026-10-30**; review/merge the setup changes separately. Later require a release-candidate go/no-go before production publication.

RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08, UX-DEC-01–08 and REL-DEC-01–08 are **accepted Phase 1 decision groups**, with the provisional qualifiers above. The Phase 1 readiness review was completed and the owner explicitly authorized **beginning Phase 2 setup on 2026-10-30**. Other phases, release activity and account/package registration remain independently gated. All operational prerequisites and authorization gates remain in force.
