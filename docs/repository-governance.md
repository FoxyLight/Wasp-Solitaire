# Repository merge protection and CI policy

**Status:** Applied and independently verified on 2026-10-31; Issue #9 closed.
**Last reviewed:** 2026-10-31

## Verified configuration and enforcement

Ruleset **Protect main - PR and CI required** (26274758) is active, targeting the default branch `main`. It requires PRs with **zero mandatory approving reviews** for the solo developer, blocks force pushes and deletion, and has **no bypass actors**. Required checks `verify` and `emulator` are bound to GitHub Actions (integration 15368), with strict up-to-date checks enabled. The effective rules endpoint `/repos/FoxyLight/Wasp-Solitaire/rules/branches/main` confirms both checks apply.

[Temporary PR #10](https://github.com/FoxyLight/Wasp-Solitaire/pull/10) independently exercised both gates:

- [JVM failure probe](https://github.com/FoxyLight/Wasp-Solitaire/actions/runs/41229480447): `verify` failed deliberately; GitHub reported `mergeStateStatus: BLOCKED`.
- [Instrumentation failure probe](https://github.com/FoxyLight/Wasp-Solitaire/actions/runs/41229576590): `verify` passed and `emulator` failed deliberately; GitHub again reported `BLOCKED`.

The probe PR was closed without merging. No failing tests were added to `main`, no merge was attempted, and no protection was bypassed. [Issue #9](https://github.com/FoxyLight/Wasp-Solitaire/issues/9#issuecomment-6547493295) records the verification. Existing connector tools could not write rulesets; the authenticated GitHub CLI saved the setting and a fresh browser view confirmed it.

## Historical issue

The `main` branch was not protected and had no rulesets or required checks when Phase 2 PR #7 was merged despite failed CI. GitHub workflow files alone do **not** create branch protection. The current GitHub integration can read these settings but does not expose a write operation for rulesets or branch protection.

## Configure in GitHub (repository owner)

1. Open **Settings → Rules → Rulesets** in [FoxyLight/Wasp-Solitaire](https://github.com/FoxyLight/Wasp-Solitaire).
2. Create a **branch ruleset** targeting the default branch `main`, and set it to **Active**. If rulesets are unavailable on the account plan, use **Settings → Branches → Branch protection rules** for `main` instead.
3. Require a pull request before merging and require at least one approving review if your account setup supports meaningful independent review. Avoid enforcing an impossible requirement for a solo account without collaborators; retain manual owner approval as a project governance gate.
4. Require passing GitHub Actions status checks **verify** and **emulator** from the updated Android setup workflow, after both jobs have completed successfully at least once. Require branches to be up to date if appropriate.
5. Disallow force pushes and branch deletion. Restrict bypasses to emergency maintenance, and never bypass a failed CI check for routine development.
6. Save, then verify the rule actually applies to `main` and that an intentionally failing test branch is blocked from merging.

If you do not have access to rulesets or required checks on your current plan, record that constraint explicitly and continue manual PR review until an enforceable gate becomes available.

Do not claim that `main` is protected until GitHub confirms enforcement. Never automatically merge a pull request just because checks pass.
