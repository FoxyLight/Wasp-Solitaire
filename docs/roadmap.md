# Development roadmap and risk register

**Status:** Proposed, pending scope approval  
**Last reviewed:** 2026-10-29

## Overall approach

Build a reliable engine first, then an accessible card board, then polish and publication. These are rough **planning ranges**, not deadlines. A solo experienced developer might require about **8–14 weeks** from approved Phase 1 to a release candidate, with additional external review/testing time.

| Phase | Goal and deliverable | Dependencies | Principal risk | Exit criterion |
| --- | --- | --- | --- | --- |
| 1. Research and approval | Approved rules, requirements, architecture and UX criteria | None | Ambiguous conventions | Owner records explicit approvals |
| 2. Environment/setup | Buildable native Android app shell, Git/CI and unit test setup | Phase 1 | Toolchain/SDK incompatibility | Clean local+CI build on device/emulator |
| 3. Pure game engine | Deterministic deals, rule validation, reserve, Undo, win detection | Phase 2 + approved rules | Invalid moves/card duplication | Unit/invariant suites pass |
| 4. Board/input | Adaptive tableau, tap and drag group moves, reserve controls | Phase 3 interface | Tiny hit targets/gesture errors | Entire game playable on representative screens |
| 5. Polish/state | Autosave, hints, statistics, menus, animations and audio toggles | Phase 4 | Lifecycle/animation state drift | End-to-end MVP acceptance scenarios pass |
| 6. QA/optimization | Regression tests, performance, TalkBack, device coverage | Phase 5 | Device-specific bugs | No known release blockers |
| 7. Store preparation | Package, signing, policy declarations, listing, tester rollout | Phase 6, Play account | Account/test/review delay | Submission approved for appropriate release track |
| 8. Launch/maintenance | Monitoring, bug fixes, feedback-led backlog | Phase 7 | Retention/compatibility | Post-release issue process in place |

Development cannot commence without documented Phase 1 approval.

## Top risks

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Wasp rule misunderstanding | High | Formally approve [game-rules.md](game-rules.md); deterministic example and property tests |
| Unordered group-movement bug | High | Unit tests for attached tails of mixed cards |
| Card input on small screens | High | Prototype early; explicit tap selection and accessible alternative |
| Save corruption after interruptions | High | Versioned snapshots, integrity invariants, restore/Undo tests |
| Play Console account/testing issues | High | Confirm account category early; recruit testers during development |
| IP/license uncertainty | Medium | Document each asset's origin/license and keep inventory |
| Slow animation/frame drops | Medium | Profile on midrange devices; separate animations from engine |
| Unbounded features | High | Enforce v1.0 MVP and defer solver/daily challenges/cloud features |
| Weak market traction | Medium | Distinct positioning, dated competitor audit, user testing before expanded investment |

## Phase 1 readiness checklist

- [ ] Approve the precise Wasp rules and digital-game conventions.
- [ ] Approve v1.0 MVP and explicit exclusions.
- [ ] Approve native Kotlin + Compose and engine separation.
- [ ] Confirm minimum Android version and physical test-device plan.
- [ ] Approve initial visual direction, cards, accessibility goals.
- [ ] Decide revenue model and Play account type.
- [ ] Choose application ID/package name and confirm ownership.
- [ ] Review sources for freshness and record initial technical decisions.

## Milestone quality gates

Every implementation milestone must have repeatable acceptance tests, reproducible bugs with deal seeds, and a reviewed code/document update. Keep release blockers separate from enhancements. Use GitHub issues for execution tasks and refer to specs in `docs/`.
