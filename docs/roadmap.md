# Development roadmap and risk register

**Status:** Phase 1 decision groups approved; owner explicitly authorized beginning Phase 2 on 2026-10-30. **Setup is in a reviewable PR; operational release prerequisites and final publisher identity remain pending.**  
**Last reviewed:** 2026-10-29

## Overall approach

Build a reliable engine first, then an accessible card board, then polish and publication. These are rough **planning ranges**, not deadlines. A solo experienced developer might require about **8–14 weeks** from approved Phase 1 to a release candidate, with additional external review/testing time.

| Phase | Goal and deliverable | Dependencies | Principal risk | Exit criterion |
| --- | --- | --- | --- | --- |
| 1. Research and approval | Approved rules, MVP, architecture, UX and release strategy; verified readiness prerequisites | None | Unverified publisher/brand/assets or ambiguous scope | Owner records all decisions, closes readiness gaps and separately authorizes Phase 2 |
| 2. Environment/setup | Kotlin/Compose `:app`, pure Kotlin `:game-engine`, Gradle and GitHub Actions setup | Entire Phase 1 and explicit implementation authorization | Toolchain/SDK compatibility | Clean builds, JVM tests and CI; minimum API 26, target API 36+ verified at setup |
| 3. Pure game engine | Deterministic deals, move validation, reserve, completed tableau runs, legal Hint enumeration and snapshot-compatible state | Phase 2 + approved rules/MVP/architecture | Invalid moves/card duplication | JUnit/invariant suites pass, including reserve Undo and zero-move states |
| 4. Board/input | Seven-column overview, expanded column inspection, explicit tap/drag interactions and accessible reserve controls | Phase 3 interface + approved UX-DEC-01–08 | Tiny hit targets/gesture errors | Every legal move accessible across portrait/landscape phones and tablets, including TalkBack inspection |
| 5. Polish/state | Room-backed atomic autosave/Undo, four stats, DataStore settings, single green-table visual theme, feedback preferences, win summary and interruption-aware timer | Phase 4 + UX-DEC-01–08 | Lifecycle/animation state drift | Offline resume and timer tests pass, with accessible reduced-motion/sound-off behavior |
| 6. QA/optimization | Regression tests, performance, TalkBack, device coverage | Phase 5 | Device-specific bugs | No known release blockers |
| 7. Store preparation | Confirm publisher/package and licenses, sign App Bundle, publish privacy policy, finish declarations/listing and required tests | Phase 6 and verified owner-controlled Play account | Verification, account testing and review delay | Owner-reviewed release candidate and required Play production access |
| 8. Launch/maintenance | Owner-approved staged production rollout, feedback-led fixes and release maintenance | Phase 7 and separate owner production go/no-go | Retention/compatibility and policy changes | Post-release issue and rollout monitoring process in place |

Six rules decisions and **all four sets of eight** MVP, architecture, UX and release decisions are approved (REL-DEC-01–08 accepted 2026-10-29). The owner separately authorized **beginning Phase 2 setup on 2026-10-30**. Brand/account/package/artwork verification and actual device/tester matrix remain pending. **No production release is authorized**; setup changes still require PR review/merge.

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
- [ ] Verify actual Play Console existence, personal/organization category, account owner and support contact. This is not established by approving the policy.
- [x] Approve provisional working identity: Wasp Solitaire, candidate FoxyLight branding and candidate `com.foxylight.waspsolitaire` (REL-DEC-02; owner-approved 2026-10-29).
- [ ] Verify actual brand/trademark rights, package registration/availability and select the final application ID; **candidate is not registered**.
- [x] Approve release policy for English (US), initial US distribution, no tracking SDKs, asset provenance, internal/account-appropriate closed testing, and separate owner-controlled staged rollout (REL-DEC-04–08; owner-approved 2026-10-29).
- [ ] Confirm public privacy-policy hosting and contact, final asset licenses, representative device/tester plan and current Play declarations.
- [ ] Recheck time-sensitive SDK target and Play requirements before development and store submission.
- [x] Receive owner's **explicit authorization to begin Phase 2 setup** (2026-10-30); implementation changes require reviewed PRs, and public release requires a separate go/no-go.

## Milestone quality gates

Every implementation milestone must have repeatable acceptance tests, reproducible bugs with deal seeds, and a reviewed code/document update. Keep release blockers separate from enhancements. Use GitHub issues for execution tasks and refer to specs in `docs/`.
