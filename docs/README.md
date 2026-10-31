# Project documentation

**Status:** Approved Phase 1 decisions and the separately authorized Phase 2 setup are documented. Phase 2 stabilization was merged via PR #8 with passing Android/JVM/emulator checks; GitHub branch protection is active. Phase 3 engine is implemented in PR #11, **open and pending owner merge approval**. Phase 4 delivery refinements are proposed for review; account, package, art and production-release prerequisites remain pending.

This directory is intended to become the canonical home of the project specifications. Once a document or decision is approved, update its status in a reviewed commit. The `main` branch represents the accepted project record; proposed changes belong in pull requests.

| File | Purpose | Status |
| --- | --- | --- |
| [product-requirements.md](product-requirements.md) | Product vision, MVP scope, acceptance criteria | MVP-DEC-01–08 accepted; launch policy covered in REL-DEC-01–08 |
| [game-rules.md](game-rules.md) | Gameplay mechanics and rule decisions | RULE-01–06 accepted; remaining project scope pending |
| [architecture.md](architecture.md) | Android stack, system boundaries, persistence and testing | ARCH-DEC-01–08 accepted; implementation details pending |
| [ux-design.md](ux-design.md) | Interaction and responsive design principles | UX-DEC-01–08 accepted; final asset selection and validation pending |
| [market-research.md](market-research.md) | Competitors and differentiation hypotheses | Research draft |
| [google-play.md](google-play.md) | Publishing strategy, requirements and verification checkpoints | REL-DEC-01–08 accepted; account/package/assets and release prerequisites unverified |
| [roadmap.md](roadmap.md) | Phases, dependencies, risks, quality gates | Phase 4/5 delivery gates clarified; Phase 3 merge and release prerequisites pending |
| [decisions.md](decisions.md) | Decisions, alternatives, approval status | Six rules plus eight MVP, eight architecture, eight UX and eight release decisions accepted; provisional qualifications tracked |
| [sources.md](sources.md) | Source register and verification dates | Research draft |
| [phase2-setup.md](phase2-setup.md) | Phase 2 structure, SDK/build validation and known setup limitations | Stabilized in PR #8, with fresh passing CI |
| [repository-governance.md](repository-governance.md) | GitHub required-check and review protection instructions | Active required checks and PR protection, Issue #9 closed |
| [phase4-delivery-plan.md](phase4-delivery-plan.md) | Reviewable 4A–4D board milestones, 5A/5B persistence and quality gates | Proposed delivery sequencing; no Phase 4 implementation authorized |

## Working agreements

1. The repository is the source of truth. Do not treat conversation text, screenshots, or a task issue as superseding an approved repository specification.
2. Every substantive document declares its **status**, **last reviewed**, and relevant outstanding decisions.
3. Game logic is specified separately from UI effects. A presentation convention must not silently redefine the rules.
4. Link externally verifiable claims to [sources.md](sources.md) or direct primary references, and refresh volatile policies ahead of launch.
5. Record new decisions in [decisions.md](decisions.md), including explicit approval and rationale.
6. The owner expressly authorized **beginning Phase 2 setup on 2026-10-30** after Phase 1 review. New phases/features remain subject to the approved roadmap, reviewed PRs and later release authorization.

## Phase 1 exit requirements

The owner approved all five decision groups and **explicitly authorized beginning Phase 2 setup on 2026-10-30**. Verification of account/package/brand/assets and other release prerequisites is still required. A **later, separate go/no-go** is mandatory before production publication.
