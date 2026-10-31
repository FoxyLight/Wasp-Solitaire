# Google Play publishing and release plan

**Status:** **REL-DEC-01 through REL-DEC-08 accepted** by the project owner on 2026-10-29. These approve the Version 1.0 **release plan and its verification gates**, not evidence that a Play Console account, package ID, brand, asset license, store listing or release has been created or verified. **Phase 2 project setup was authorized on 2026-10-30; app publication remains unauthorized.**  
**Last reviewed:** 2026-10-29  
**Platform:** Standard Android phones/tablets (no Wear, Auto, TV or XR commitment)  
**Source-of-truth decision log:** [decisions.md](decisions.md)

## Approved publishing and release decisions

| ID | Topic | Accepted Version 1.0 approach | Important condition |
| --- | --- | --- | --- |
| **REL-DEC-01** | Business model | Release as **free, ad-free**, with no purchases, subscriptions or monetization SDKs in Version 1.0. | Google Play's free-to-paid conversion limitation means the initial pricing choice is significant. Future monetization requires separate approval. |
| **REL-DEC-02** | App identity | **Wasp Solitaire** is the working title; **FoxyLight** is candidate developer branding; `com.foxylight.waspsolitaire` is a **candidate**, not a registered Android application ID. | Verify trademark/branding rights, publisher identity, package-name eligibility/registration and final ownership **before fixing an app identity**. |
| **REL-DEC-03** | Publishing account | Use an **owner-controlled, properly verified Google Play Console account**. | Actual account owner, personal/organization classification, verification status, access controls, support contact and signing custody are still unknown. Do not assume an account exists. |
| **REL-DEC-04** | Privacy and data handling | Offline-first, no tracking/analytics SDKs or unnecessary permissions, accurate Data Safety forms and an accessible public app-specific privacy policy. | Audit final app and dependency behavior before claiming no collected/shared data. Host and review the real policy before publication. |
| **REL-DEC-05** | Assets and listing | Use original or properly licensed art/assets, documented provenance and genuine compliant store artwork/screenshots. | Specific card images, icon/brand, fonts/audio, licenses, rights and asset manifest must be verified; no particular third-party pack is approved. |
| **REL-DEC-06** | Initial locale and geography | **English (United States)** user-facing release, distributed initially in the **United States**. | Verify target audience/country settings and available listing localization before launch. Other locales/regions require a later distribution decision. |
| **REL-DEC-07** | Testing and rollout | Internal tests → account-appropriate closed testing → device/accessibility QA and release candidate checks → conservative staged production rollout. | Test requirements depend on the actual account and current Play policies. Production access and rollout remain subject to review/authorization. |
| **REL-DEC-08** | Release governance | Formal publication checklist, owner approval **before public release**, and current-policy revalidation at implementation and submission. | The REL decision itself did not authorize development. The owner later expressly authorized beginning **Phase 2 setup on 2026-10-30**; store submission remains unauthorized. |

## Identity, account and publication prerequisites

| Item | Planned direction | Status and required validation |
| --- | --- | --- |
| Product name | Wasp Solitaire | **Working title only**; verify legal/brand/store suitability |
| Developer label | FoxyLight | **Provisional**, based on existing repository branding; verify ownership and right to use it |
| Application ID | `com.foxylight.waspsolitaire` | **Candidate only**; not registered or availability-checked; verify before Android project initialization |
| Play Console | Owner personally publishing as an individual | **Owner confirmed intended personal account and that no account exists yet**. Account creation, verification and support contacts remain pending. |
| Support email and public developer contact | Owner-controlled contact | **Not provided or validated**; no address is invented |
| Privacy-policy page | Public app-specific webpage (GitHub Pages is a **possible** host) | **Not published**; URL, content and ownership remain to be confirmed |
| App icon and brand assets | Original graphics aligned with approved green/ivory/blue UX direction | **Not designed or licensed**; approval is only of the approach |
| Store language and territory | English (US), initial US availability | **Approved launch scope**, but no store configuration exists |

### Business-model consequence

Free and ad-free means **no display ads, paid download, in-app purchases, subscriptions or monetization SDKs in Version 1.0**. Future monetization is not authorized by this decision. Google Play generally does not allow converting an app already offered free into a paid-download app under the same package name; confirm the current policy before any future change. Do not treat the approved launch plan as a promise of revenue or validated market demand.

## Privacy and compliance approach (REL-DEC-04)

- Store deals, Undo history, game statistics and preferences locally through the approved Room/DataStore architecture.
- Include **no advertising, analytics or tracking SDKs** and avoid requesting unnecessary permissions in the initial implementation.
- Keep all gameplay and settings usable without internet or sign-in. A privacy-information link may lead to an external webpage without making network access a gameplay requirement.
- Before release, audit every included SDK, permission and any outbound traffic. Complete **Data Safety** and all applicable Play Console declarations according to **actual runtime behavior**, not a presumed "no collection" claim.
- Publish a human-readable, app-specific privacy policy at a stable, publicly accessible URL and link it in the app and Play listing as required. Hosting via GitHub Pages is only one possible option, not an approved or completed deployment.
- Verify applicable target audience, ads, content rating, Families and regional regulatory declarations. No age-targeting or specific content rating is asserted here.

## Artwork, listing and provenance (REL-DEC-05)

- Prepare original or appropriately licensed card faces, backs, app icon, illustrations, fonts and any sounds; record their source, author, license, distribution rights, attribution obligations and modifications in an asset manifest.
- Store icon: **512 × 512 PNG**; feature graphic: **1024 × 500 JPEG/PNG**, according to the publishing guidance checked on 2026-10-29. Recheck exact size/format rules before submission.
- Produce **genuine gameplay screenshots** from a real build once implementation is authorized; do not substitute mockups and do not misrepresent gameplay or unimplemented features.
- Draft accurate store title, short and long descriptions, contact details and privacy link, all subject to owner review.
- The [Kenney Playing Cards Pack](https://kenney.nl/assets/playing-cards-pack) remains a **research candidate only**. Nothing in REL-DEC-05 establishes a license, adopts its artwork or permits reuse of competitors' graphics.

## Testing and release process (REL-DEC-07 and REL-DEC-08)

1. **Preproduction identity check:** confirm account classification, verification, developer contacts, publisher/brand rights, package-name eligibility, distribution countries and asset provenance.
2. **Internal testing:** after separate development authorization, verify seeded rules, card identity, reserve, Undo, no auto-defeat, exact offline resume, the four statistics, and accessible phone/tablet UI.
3. **Closed testing, if needed:** determine real account requirements. Under the policy listed below, affected new personal accounts need **at least 12 opted-in testers continuously for at least 14 days** before applying for production access; the actual requirement must be checked for the real account.
4. **Accessibility and device review:** include a minimum API 26 device/emulator, intermediate Android devices, current target/API 36+, narrow phone portrait/landscape and tablet-width layout. Verify TalkBack, non-drag movement, small card inspection, reduced motion and process restoration. Confirm exact matrix during implementation planning.
5. **Release candidate:** signed Android App Bundle, Play App Signing and securely held upload key, dependency/privacy review, correct listing/declarations, final asset manifest, and no known critical gameplay, save or accessibility regressions.
6. **Owner release approval:** require an **additional explicit go/no-go** before public production publication; no unattended release or automatic submission.
7. **Gradual production rollout:** only when approved and eligible; monitor feedback, stability and policy notices as practical without adding unapproved tracking software.
8. **At submission:** revalidate Play policies, availability/registration, country/locale and any account-specific testing requirement. Current published policies may change.

This approved **process** is not evidence that any tests have been run, an account has been created, an app bundle has been built, or the game has been released.

## Time-sensitive Google Play policy reference table

**Research snapshot:** 2026-10-29. Use official documentation as the authority and revalidate at development and publication.

| Topic | Research baseline / question to verify | Official source |
| --- | --- | --- |
| Android target API | As recorded on 2026-10-29, new standard Android submissions from Aug 31, 2026 target **API 36+**; check for newer deadlines, exceptions and extensions before release. | [Target API levels](https://support.google.com/googleplay/android-developer/answer/11926878) |
| Account and package registration | Verify legal owner and account category; register/verify chosen package name as required. | [Package name registration](https://support.google.com/googleplay/android-developer/answer/16984799) |
| New personal-account testing | Conditional closed-test requirement for affected personal accounts created after Nov 13, 2023: 12 opted-in testers for 14 continuous days before applying for production access. | [Testing new personal accounts](https://support.google.com/googleplay/android-developer/answer/14151465) |
| Signing | Signed Android App Bundle, Play App Signing and protected signing/upload credentials. | [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756) |
| Privacy | Accurate public privacy policy and declarations based on real data practices and dependencies. | [Prepare for review](https://support.google.com/googleplay/android-developer/answer/9859455), [Data safety](https://support.google.com/googleplay/android-developer/answer/10787469) |
| Store assets | Icon, feature graphic and real gameplay screenshots compliant with current listing requirements. | [Preview asset specifications](https://support.google.com/googleplay/android-developer/answer/9866151) |
| Store text | Accurate, non-misleading product description and images. | [Store listing guidelines](https://support.google.com/googleplay/android-developer/answer/13393723) |

## Readiness checklist (not evidence of completion)

- [x] Approve Version 1.0 publishing **strategy** and governance (REL-DEC-01–08; owner-approved 2026-10-29).
- [ ] Create an owner-controlled personal/individual Play Console account (owner selected personal and reported no existing account); then verify identity, contacts and ownership.
- [ ] Verify FoxyLight branding rights, final app title and candidate package eligibility; formally select and register actual application ID.
- [ ] Confirm owner-controlled developer/support contacts, privacy-policy URL and account/signing ownership.
- [ ] Confirm final card artwork/icon/font/audio sources and redistribution licenses; create provenance manifest.
- [ ] Confirm representative device test matrix and identify tester/review participants.
- [ ] Revalidate SDK target, account testing, content, audience, pricing, signing and privacy policies at the relevant time.
- [ ] Prepare and review a public privacy policy, real Data Safety answers, ads declaration and any other Play Console forms.
- [ ] After explicit Phase 2 authorization, create and test the actual app, produce real screenshots, sign a release AAB and validate listing assets.
- [ ] Run internal and required closed tests; address critical defects and accessibility gaps.
- [ ] Obtain **separate owner approval** for production publication and staged rollout.

## Scope and authorization boundary

All major Phase 1 **decision groups** may be accepted while some **real-world verification and execution prerequisites** remain incomplete. A merged documentation PR records the owner's planning choices, not package ownership, accepted artwork licenses, verified Play Console credentials, a validated release candidate or authorization to begin building software. A Phase 1 readiness review was conducted and the owner expressly authorized beginning **Phase 2 setup on 2026-10-30**. Neither that instruction nor this plan authorizes a public release or bypasses publisher/package/asset verification.

See [roadmap.md](roadmap.md), [product-requirements.md](product-requirements.md), [ux-design.md](ux-design.md), [architecture.md](architecture.md) and [sources.md](sources.md).
