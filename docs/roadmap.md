# Development roadmap and risk register

**Phase 4D owner amendment (2026-11-01):** [Final approved design and implementation record](phase4d-interface.md) supersedes earlier Hint/expanded-inspection/control-layout proposals. Hint is removed (MVP-08 / MVP-DEC-03); no separate Inspect screen, title or completion display. Two-tap moves use whole-tail selection and explicit destination; illegal destination taps clear selection without a game action. Primary controls are Undo and Deal 3. Full non-drag access remains through direct board accessibility actions. Owner design approval authorizes implementation; merge and publication remain separate gates. Phases 4A–4C are merged through PRs #18–20. Historical text below must be read with this amendment.

**Status:** Phases 1–3 and 4A–4C are merged (PRs #11, #18–20). Phase 4C merge: `6c64b6a4899bb3e0fa7790ff1422470608810b83`; Issue #14 closed. Owner approved the final Phase 4D design on 2026-11-01; [implementation PR #22](https://github.com/FoxyLight/Wasp-Solitaire/pull/22) remains unmerged pending separate review. Issue #15 stays open. Phase 5 and release remain gated.
**Last reviewed:** 2026-10-31

Phase 4A/4B/4C are merged through owner-approved PRs #18–20. See [Phase 4C evidence](phase4c-gameplay.md) and [approved Phase 4D design and validation](phase4d-interface.md).

## Overall approach

Build a reliable engine first, then an accessible card board, then polish and publication. These are rough **planning ranges**, not deadlines. A solo experienced developer might require about **8–14 weeks** from approved Phase 1 to a release candidate, with additional external review/testing time.

| Phase | Goal and deliverable | Dependencies | Principal risk | Exit criterion |
| --- | --- | --- | --- | --- |
| 1. Research and approval | Approved rules, MVP, architecture, UX and release strategy; verified readiness prerequisites | None | Unverified publisher/brand/assets or ambiguous scope | Owner records all decisions, closes readiness gaps and separately authorizes Phase 2 |
| 2. Environment/setup | Kotlin/Compose `:app`, pure Kotlin `:game-engine`, Gradle and GitHub Actions setup | Entire Phase 1 and explicit implementation authorization | Toolchain/SDK compatibility | Clean builds, JVM tests and CI; minimum API 26, target API 36+ verified at setup |
| 3. Pure game engine | Deterministic deals, move validation, reserve, completed tableau runs, legal Hint enumeration and snapshot-compatible state | Phase 2 + approved rules/MVP/architecture | Invalid moves/card duplication | JUnit/invariant suites pass, including reserve Undo and zero-move states |
| 4. Board/input (4A–4D) | [GameViewModel and state contract → adaptive board → playable controls → accessible inspection](phase4-delivery-plan.md) | Approved Phase 3 engine merged through a separate owner decision; UX-DEC-01–08 | State divergence, tiny hit targets, gesture errors | Four small reviewed PR gates; all legal actions work by tap and drag and on a representative phone/tablet matrix |
| 5. Persistence/polish (5A–5B) | [5A Room atomic saves/Undo and recovery; 5B clock, exactly four stats, DataStore preferences, win summary and polish](phase4-delivery-plan.md) | Phase 4 plus approved ARCH-DEC-04/05 and UX-DEC-08 | Save corruption, duplicate win counts, lifecycle drift | Process-death, rotation, transaction/Undo, active-time and statistics tests pass; offline recovery demonstrated |
| 6. QA/optimization | Regression tests, performance, TalkBack, device coverage | Phase 5 | Device-specific bugs | No known release blockers |
| 7. Store preparation | Confirm publisher/package and licenses, sign App Bundle, publish privacy policy, finish declarations/listing and required tests | Phase 6 and verified owner-controlled Play account | Verification, account testing and review delay | Owner-reviewed release candidate and required Play production access |
| 8. Launch/maintenance | Owner-approved staged production rollout, feedback-led fixes and release maintenance | Phase 7 and separate owner production go/no-go | Retention/compatibility and policy changes | Post-release issue and rollout monitoring process in place |

## Next delivery gates and owner approvals

Phase 3 was merged through the owner's explicit approval in [PR #11](https://github.com/FoxyLight/Wasp-Solitaire/pull/11) on 2026-10-31 after both required checks passed. The pure Kotlin engine is on `main`. **Phase 4 application implementation is a separate milestone and requires its own authorization**; approval of this documentation refinement alone does not grant it.

The [Phase 4 delivery plan](phase4-delivery-plan.md) is the execution checklist for four independently reviewable implementation increments:

- **4A:** GameViewModel + StateFlow, single-source-of-truth session/selection/intents, and a persistence-ready boundary ([issue #12](https://github.com/FoxyLight/Wasp-Solitaire/issues/12)).
- **4B:** Responsive seven-column Compose rendering using the engine state and original or verified-licensed provisional card symbols ([issue #13](https://github.com/FoxyLight/Wasp-Solitaire/issues/13)).
- **4C:** Explicit tap/drag moves and controls including Undo, Hint, Deal 3, Restart/New Game and win indication ([issue #14](https://github.com/FoxyLight/Wasp-Solitaire/issues/14)).
- **4D:** [Approved modern classic interface](phase4d-interface.md), direct non-drag board actions, TalkBack and representative device validation ([issue #15](https://github.com/FoxyLight/Wasp-Solitaire/issues/15)).

**Architecture-forward constraint:** 4A defines the repository interface and snapshot/version/Undo contract, but **does not claim durable saves**. Phase 5A must implement Room atomic state+history writes, migrations/recovery and interruption tests before the app claims offline autosave or exact resume. Phase 5B implements the approved active-only timer, exactly four statistics, DataStore settings and remaining feedback/polish.

**Parallel readiness track:** Identify representative devices/testers, verify final card-asset provenance, candidate brand and package, and plan Play Console registration/privacy-policy needs early ([issue #16](https://github.com/FoxyLight/Wasp-Solitaire/issues/16)). Account creation, spending, production identity/signing changes, external enrollment and publishing require separate owner authorization. These are administrative prerequisites, **not** dependencies that should block authorized board prototypes.

Each implementation slice needs its own branch/PR, recorded acceptance evidence, both required GitHub Actions checks, manual owner review and an explicit merge decision. The roadmap refinement **does not authorize Phase 4 code, Phase 5 code, account actions or release**.

Six rules decisions and **all four sets of eight** MVP, architecture, UX and release decisions are approved (REL-DEC-01–08 accepted 2026-10-29). The owner separately authorized **beginning Phase 2 setup on 2026-10-30**. Brand/account/package/artwork verification and actual device/tester matrix remain pending. **No production release is authorized**; Phase 2 and Phase 3 code merges are complete and their required verification passed. Phase 4 remains separately gated.

## Top risks

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Wasp rule misunderstanding | High | Formally approve [game-rules.md](game-rules.md); deterministic example and property tests, including no-move states (RULE-06) |
| Unordered group-movement bug | High | Unit tests for attached tails of mixed cards |
| Card input on small screens | High | Prototype early; explicit tap selection and accessible alternative |
| Save corruption after interruptions | High | Room transactions coupling saved states and prior-state Undo snapshots, integrity invariants and restore/Undo tests |
| Play Console account/testing issues | High | Confirm account category early; recruit testers during development |
| IP/license uncertainty | Medium | Document each asset's origin/license and keep inventory |
| Slow animation/frame drops | Medium | Profile on midrange devices; separate animations from engine |
| Unbounded features | High | Enforce v1.0 MVP and defer solver/daily challenges/cloud features |
| Weak market traction | Medium | Distinct positioning, dated competitor audit, user testing before expanded investment |

## Phase 1 readiness checklist

- [x] Approve RULE-01–06: reserve timing, completed runs, timer, Undo wins, Classic Wasp v1.0 scope, and no automatic defeat on zero tableau moves (owner-approved 2026-10-29).
- [x] Complete five Phase 1 **decision-group** approvals: RULE-01–06; MVP-DEC-01–08; ARCH-DEC-01–08; UX-DEC-01–08; REL-DEC-01–08 (2026-10-29).
- [x] Complete Phase 1 readiness review and separately authorize **beginning Phase 2 setup** (owner instruction 2026-10-30). Operational publisher/identity/art/test verification remains open.
- [x] Approve Version 1.0 MVP and explicit exclusions (MVP-DEC-01–08 accepted 2026-10-29).
- [x] Approve ARCH-DEC-01–08: Kotlin/Compose; two modules; ViewModel/StateFlow; Room + DataStore; durable snapshot Undo; adaptive board; API 26 minimum/36+ target; testing and GitHub Actions (owner-approved 2026-10-29).
- [x] Confirm minimum Android version: API 26 (ARCH-DEC-07).
- [ ] Confirm physical/emulator test-device lineup for small and large layouts; target SDK policy to be reverified at development/release.
- [x] Approve UX-DEC-01–08: green-table visual direction, readable cards/original or licensed art, responsive inspection, move interactions, navigation, accessibility, optional feedback and timer UX (owner-approved 2026-10-29).
- [ ] Confirm the final card artwork/license, brand/logo and asset provenance before implementation/release.
- [x] Approve free/ad-free Version 1.0 monetization strategy and verified-owner-account **policy** (REL-DEC-01, REL-DEC-03; owner-approved 2026-10-29).
- [ ] Create and verify the owner's intended **personal/individual** Play Console account (the owner has confirmed they do not have an account yet); confirm account owner and support contact. An intended category is not an existing account.
- [x] Approve provisional working identity: Wasp Solitaire, candidate FoxyLight branding and candidate `com.foxylight.waspsolitaire` (REL-DEC-02; owner-approved 2026-10-29).
- [ ] Verify actual brand/trademark rights, package registration/availability and select the final application ID; **candidate is not registered**.
- [x] Approve release policy for English (US), initial US distribution, no tracking SDKs, asset provenance, internal/account-appropriate closed testing, and separate owner-controlled staged rollout (REL-DEC-04–08; owner-approved 2026-10-29).
- [ ] Confirm public privacy-policy hosting and contact, final asset licenses, representative device/tester plan and current Play declarations.
- [ ] Recheck time-sensitive SDK target and Play requirements before development and store submission.
- [x] Receive owner's **explicit authorization to begin Phase 2 setup** (2026-10-30); implementation changes require reviewed PRs, and public release requires a separate go/no-go.

## Phase 2 stabilization gate (verified 2026-10-31)

- [x] Fresh Android debug build and pure JVM tests pass with `compileSdk 37`: [baseline run](https://github.com/FoxyLight/Wasp-Solitaire/actions/runs/41229459279).
- [x] Official Gradle 9.5.0 wrapper committed, with matching wrapper-JAR checksum and pinned distribution SHA-256.
- [x] Setup-screen Compose instrumentation test passes on Android 35.
- [x] Active PR and required CI checks on `main`, zero mandatory external approvals and no bypasses: see [repository governance](repository-governance.md).
- [x] Reconcile setup documentation with merged files and fresh CI evidence.

Phase 3 engine scope, API choices, verification and the next milestone are tracked in [phase3-engine.md](phase3-engine.md). The Android board, persistence, clock and statistics integration remain future work.

Phase 3 and Phase 4A–4C are merged. Final Phase 4D design approval authorizes the implementation in PR #22; its merge requires separate owner approval. No external account or publication authorization is implied.

## Milestone quality gates

Every implementation milestone must have repeatable acceptance tests, reproducible bugs with deal seeds, and a reviewed code/document update. Keep release blockers separate from enhancements. Use GitHub issues for execution tasks and refer to specs in `docs/`.
