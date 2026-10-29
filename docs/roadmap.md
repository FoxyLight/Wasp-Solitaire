# Development roadmap and risk register

**Status:** Proposed, pending scope approval  
**Last reviewed:** 2026-10-29

## Overall approach

Build a reliable engine first, then an accessible card board, then polish and publication. These are rough **planning ranges**, not deadlines. A solo experienced developer might require about **8–14 weeks** from approved Phase 1 to a release candidate, with additional external review/testing time.

| Phase | Goal and deliverable | Dependencies | Principal risk | Exit criterion |
| --- | --- | --- | --- | --- |
| 1. Research and approval | Approved rules, requirements, architecture and UX criteria | None | Ambiguous conventions | Owner records explicit approvals |
| 2. Environment/setup | Kotlin/Compose `:app`, pure Kotlin `:game-engine`, Gradle and GitHub Actions setup | Entire Phase 1 and explicit implementation authorization | Toolchain/SDK compatibility | Clean builds, JVM tests and CI; minimum API 26, target API 36+ verified at setup |
| 3. Pure game engine | Deterministic deals, move validation, reserve, completed tableau runs, legal Hint enumeration and snapshot-compatible state | Phase 2 + approved rules/MVP/architecture | Invalid moves/card duplication | JUnit/invariant suites pass, including reserve Undo and zero-move states |
| 4. Board/input | Seven-column overview, expanded column inspection, explicit tap/drag interactions and accessible reserve controls | Phase 3 interface + approved UX-DEC-01–08 | Tiny hit targets/gesture errors | Every legal move accessible across portrait/landscape phones and tablets, including TalkBack inspection |
| 5. Polish/state | Room-backed atomic autosave/Undo, four stats, DataStore settings, single green-table visual theme, feedback preferences, win summary and interruption-aware timer | Phase 4 + UX-DEC-01–08 | Lifecycle/animation state drift | Offline resume and timer tests pass, with accessible reduced-motion/sound-off behavior |
| 6. QA/optimization | Regression tests, performance, TalkBack, device coverage | Phase 5 | Device-specific bugs | No known release blockers |
| 7. Store preparation | Package, signing, policy declarations, listing, tester rollout | Phase 6, Play account | Account/test/review delay | Submission approved for appropriate release track |
| 8. Launch/maintenance | Monitoring, bug fixes, feedback-led backlog | Phase 7 | Retention/compatibility | Post-release issue process in place |

Six rules decisions, eight MVP decisions, eight architecture decisions and eight UI/UX decisions are approved. **Development cannot commence until the remaining Phase 1 publishing/business, asset/branding and release decisions are approved and the project owner explicitly authorizes Phase 2**.

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
- [ ] Complete broader Phase 1 approvals before authorizing implementation.
- [x] Approve Version 1.0 MVP and explicit exclusions (MVP-DEC-01–08 accepted 2026-10-29).
- [x] Approve ARCH-DEC-01–08: Kotlin/Compose; two modules; ViewModel/StateFlow; Room + DataStore; durable snapshot Undo; adaptive board; API 26 minimum/36+ target; testing and GitHub Actions (owner-approved 2026-10-29).
- [x] Confirm minimum Android version: API 26 (ARCH-DEC-07).
- [ ] Confirm physical/emulator test-device lineup for small and large layouts; target SDK policy to be reverified at development/release.
- [x] Approve UX-DEC-01–08: green-table visual direction, readable cards/original or licensed art, responsive inspection, move interactions, navigation, accessibility, optional feedback and timer UX (owner-approved 2026-10-29).
- [ ] Confirm the final card artwork/license, brand/logo and asset provenance before implementation/release.
- [ ] Decide revenue model and Play account type.
- [ ] Choose application ID/package name and confirm ownership.
- [ ] Review sources for freshness and record initial technical decisions.

## Milestone quality gates

Every implementation milestone must have repeatable acceptance tests, reproducible bugs with deal seeds, and a reviewed code/document update. Keep release blockers separate from enhancements. Use GitHub issues for execution tasks and refer to specs in `docs/`.
