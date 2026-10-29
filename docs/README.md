# Project documentation

**Status:** Mixed approvals. RULE-01–06, MVP-DEC-01–08, ARCH-DEC-01–08 and UX-DEC-01–08 were explicitly approved by the project owner on 2026-10-29. Final art licensing/branding, monetization, publishing and other remaining decisions are not yet approved.

This directory is intended to become the canonical home of the project specifications. Once a document or decision is approved, update its status in a reviewed commit. The `main` branch represents the accepted project record; proposed changes belong in pull requests.

| File | Purpose | Status |
| --- | --- | --- |
| [product-requirements.md](product-requirements.md) | Product vision, MVP scope, acceptance criteria | MVP-DEC-01–08 accepted; unrelated business/technical choices pending |
| [game-rules.md](game-rules.md) | Gameplay mechanics and rule decisions | RULE-01–06 accepted; remaining project scope pending |
| [architecture.md](architecture.md) | Android stack, system boundaries, persistence and testing | ARCH-DEC-01–08 accepted; implementation details pending |
| [ux-design.md](ux-design.md) | Interaction and responsive design principles | UX-DEC-01–08 accepted; final asset selection and validation pending |
| [market-research.md](market-research.md) | Competitors and differentiation hypotheses | Research draft |
| [google-play.md](google-play.md) | Publishing requirements and verification checkpoints | Research draft |
| [roadmap.md](roadmap.md) | Phases, dependencies, risks, quality gates | Proposed |
| [decisions.md](decisions.md) | Decisions, alternatives, approval status | Six rules, eight MVP, eight architecture and eight UX decisions accepted; remaining ADRs proposed |
| [sources.md](sources.md) | Source register and verification dates | Research draft |

## Working agreements

1. The repository is the source of truth. Do not treat conversation text, screenshots, or a task issue as superseding an approved repository specification.
2. Every substantive document declares its **status**, **last reviewed**, and relevant outstanding decisions.
3. Game logic is specified separately from UI effects. A presentation convention must not silently redefine the rules.
4. Link externally verifiable claims to [sources.md](sources.md) or direct primary references, and refresh volatile policies ahead of launch.
5. Record new decisions in [decisions.md](decisions.md), including explicit approval and rationale.
6. No implementation code until Phase 1 approval.

## Phase 1 exit requirements

The owner approves game rules (including reserve and completed-run conventions), MVP boundaries, technical direction, UX criteria, and publication approach, then explicitly authorizes Phase 2. **UX approval alone does not authorize coding.**
