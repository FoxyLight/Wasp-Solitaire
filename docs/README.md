# Project documentation

**Status:** Draft documentation set, prepared 2026-10-29. None of the recommendations is approved solely by appearing here.

This directory is intended to become the canonical home of the project specifications. Once a document or decision is approved, update its status in a reviewed commit. The `main` branch represents the accepted project record; proposed changes belong in pull requests.

| File | Purpose | Status |
| --- | --- | --- |
| [product-requirements.md](product-requirements.md) | Product vision, MVP scope, acceptance criteria | Proposed |
| [game-rules.md](game-rules.md) | Gameplay mechanics and rule ambiguities | Proposed |
| [architecture.md](architecture.md) | Android stack, system boundaries, persistence and testing | Proposed |
| [ux-design.md](ux-design.md) | Interaction and responsive design principles | Proposed |
| [market-research.md](market-research.md) | Competitors and differentiation hypotheses | Research draft |
| [google-play.md](google-play.md) | Publishing requirements and verification checkpoints | Research draft |
| [roadmap.md](roadmap.md) | Phases, dependencies, risks, quality gates | Proposed |
| [decisions.md](decisions.md) | Decisions, alternatives, approval status | Pending approval |
| [sources.md](sources.md) | Source register and verification dates | Research draft |

## Working agreements

1. The repository is the source of truth. Do not treat conversation text, screenshots, or a task issue as superseding an approved repository specification.
2. Every substantive document declares its **status**, **last reviewed**, and relevant outstanding decisions.
3. Game logic is specified separately from UI effects. A presentation convention must not silently redefine the rules.
4. Link externally verifiable claims to [sources.md](sources.md) or direct primary references, and refresh volatile policies ahead of launch.
5. Record new decisions in [decisions.md](decisions.md), including explicit approval and rationale.
6. No implementation code until Phase 1 approval.

## Phase 1 exit requirements

The owner approves game rules (including reserve and completed-run conventions), MVP boundaries, technical direction, UX criteria, and publication approach. Then Phase 2 may begin.
