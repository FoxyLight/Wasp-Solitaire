# Repository merge protection and CI policy

**Status:** Owner/administrator configuration required; not yet applied.
**Last reviewed:** 2026-10-30

## Observed issue

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
