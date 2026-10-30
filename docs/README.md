# Project documentation

**Status:** RULE-01–06 and MVP/ARCH/UX/REL-DEC-01–08 approved 2026-10-29; owner separately authorized beginning Phase 2 setup on 2026-10-30; initial scaffold PR #7 was merged but the build verification failed and stabilization is under review. Actual account, brand/package and asset verification, full implementation and release authorization remain pending.

This directory is intended to become the canonical home of the project specifications. Once a document or decision is approved, update its status in a reviewed commit. The `main` branch represents the accepted project record; proposed changes belong in pull requests.

| File | Purpose | Status |
| --- | --- | --- |
| [product-requirements.md](product-requirements.md) | Product vision, MVP scope, acceptance criteria | MVP-DEC-01–08 accepted; launch policy covered in REL-DEC-01–08 |
| [game-rules.md](game-rules.md) | Gameplay mechanics and rule decisions | RULE-01–06 accepted; remaining project scope pending |
| [architecture.md](architecture.md) | Android stack, system boundaries, persistence and testing | ARCH-DEC-01–08 accepted; implementation details pending |
| [ux-design.md](ux-design.md) | Interaction and responsive design principles | UX-DEC-01–08 accepted; final asset selection and validation pending |
| [market-research.md](market-research.md) | Competitors and differentiation hypotheses | Research draft |
| [google-play.md](google-play.md) | Publishing strategy, requirements and verification checkpoints | REL-DEC-01–08 accepted; account/package/assets and release prerequisites unverified |
| [roadmap.md](roadmap.md) | Phases, dependencies, risks, quality gates | Decision groups and Phase 2 setup authorization recorded; release prerequisites remain pending |
| [decisions.md](decisions.md) | Decisions, alternatives, approval status | Six rules plus eight MVP, eight architecture, eight UX and eight release decisions accepted; provisional qualifications tracked |
| [sources.md](sources.md) | Source register and verification dates | Research draft |
| [phase2-setup.md](phase2-setup.md) | Phase 2 structure, SDK/build validation and known setup limitations | Initial scaffold merged; stabilization checks pending |
| [repository-governance.md](repository-governance.md) | GitHub required-check and review protection instructions | Administrator action pending |

## Working agreements

1. The repository is the source of truth. Do not treat conversation text, screenshots, or a task issue as superseding an approved repository specification.
2. Every substantive document declares its **status**, **last reviewed**, and relevant outstanding decisions.
3. Game logic is specified separately from UI effects. A presentation convention must not silently redefine the rules.
4. Link externally verifiable claims to [sources.md](sources.md) or direct primary references, and refresh volatile policies ahead of launch.
5. Record new decisions in [decisions.md](decisions.md), including explicit approval and rationale.
6. The owner expressly authorized **beginning Phase 2 setup on 2026-10-30** after Phase 1 review. New phases/features remain subject to the approved roadmap, reviewed PRs and later release authorization.

## Phase 1 exit requirements

The owner approved all five decision groups and **explicitly authorized beginning Phase 2 setup on 2026-10-30**. Verification of account/package/brand/assets and other release prerequisites is still required. A **later, separate go/no-go** is mandatory before production publication.
