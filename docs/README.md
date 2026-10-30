# Project documentation

**Status:** RULE-01–06 and MVP-DEC-01–08, ARCH-DEC-01–08, UX-DEC-01–08, REL-DEC-01–08 were explicitly approved by the project owner on 2026-10-29. The release strategy is approved, while actual account, brand/package and asset verification, release execution and development authorization remain pending.

This directory is intended to become the canonical home of the project specifications. Once a document or decision is approved, update its status in a reviewed commit. The `main` branch represents the accepted project record; proposed changes belong in pull requests.

| File | Purpose | Status |
| --- | --- | --- |
| [product-requirements.md](product-requirements.md) | Product vision, MVP scope, acceptance criteria | MVP-DEC-01–08 accepted; launch policy covered in REL-DEC-01–08 |
| [game-rules.md](game-rules.md) | Gameplay mechanics and rule decisions | RULE-01–06 accepted; remaining project scope pending |
| [architecture.md](architecture.md) | Android stack, system boundaries, persistence and testing | ARCH-DEC-01–08 accepted; implementation details pending |
| [ux-design.md](ux-design.md) | Interaction and responsive design principles | UX-DEC-01–08 accepted; final asset selection and validation pending |
| [market-research.md](market-research.md) | Competitors and differentiation hypotheses | Research draft |
| [google-play.md](google-play.md) | Publishing strategy, requirements and verification checkpoints | REL-DEC-01–08 accepted; account/package/assets and release prerequisites unverified |
| [roadmap.md](roadmap.md) | Phases, dependencies, risks, quality gates | Decision groups accepted; readiness and authorization remain pending |
| [decisions.md](decisions.md) | Decisions, alternatives, approval status | Six rules plus eight MVP, eight architecture, eight UX and eight release decisions accepted; provisional qualifications tracked |
| [sources.md](sources.md) | Source register and verification dates | Research draft |

## Working agreements

1. The repository is the source of truth. Do not treat conversation text, screenshots, or a task issue as superseding an approved repository specification.
2. Every substantive document declares its **status**, **last reviewed**, and relevant outstanding decisions.
3. Game logic is specified separately from UI effects. A presentation convention must not silently redefine the rules.
4. Link externally verifiable claims to [sources.md](sources.md) or direct primary references, and refresh volatile policies ahead of launch.
5. Record new decisions in [decisions.md](decisions.md), including explicit approval and rationale.
6. No implementation code until Phase 1 readiness review and **separate explicit owner authorization**. Agreement on all decision groups alone does not authorize building.

## Phase 1 exit requirements

The owner has approved the five decision groups (rules, MVP, architecture, UX, release strategy), with account/package/brand/assets and other operational checks still outstanding. Conduct a **Phase 1 readiness and implementation authorization review** before initiating Phase 2. Owner permission to start coding must be explicit. A later, separate go/no-go is required before any production store release.
