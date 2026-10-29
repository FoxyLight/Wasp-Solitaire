# Google Play publishing readiness

**Status:** Time-sensitive research draft; revalidate before submission  
**Verified:** 2026-10-29  
**Platform:** Standard Android phone/tablet app (not Wear, Auto, TV, or XR)

## Current requirements, with official references

| Topic | Requirement or planning consideration | Official reference |
| --- | --- | --- |
| API target | New standard Android apps/updates submitted from Aug 31, 2026 must target Android 16 / API 36 or higher, subject to applicable exceptions/extensions | [Target API levels](https://support.google.com/googleplay/android-developer/answer/11926878) |
| Account | Create/verify Play Console developer account; verify identity and package name as applicable | [Package name registration](https://support.google.com/googleplay/android-developer/answer/16984799) |
| Testing | Personal accounts created after Nov 13, 2023: closed test with >=12 opted-in testers continuously for >=14 days before applying for production access | [Testing new personal accounts](https://support.google.com/googleplay/android-developer/answer/14151465) |
| Signing/release | Signed Android App Bundle; Play App Signing setup for new apps, protected upload key | [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756) |
| Privacy | App-specific public privacy-policy URL, accessible in Play and within the app, even if no sensitive data collected | [App review](https://support.google.com/googleplay/android-developer/answer/9859455) |
| Data safety | Accurate disclosures including collection/sharing attributable to integrated SDKs | [Data safety](https://support.google.com/googleplay/android-developer/answer/10787469) |
| Declarations | Complete content rating, target audience and ads declarations and all relevant Play Console app-content forms | [App review](https://support.google.com/googleplay/android-developer/answer/9859455) |
| Store assets | Icon 512x512 PNG; feature graphic 1024x500 JPEG/PNG; relevant genuine gameplay screenshots | [Preview assets](https://support.google.com/googleplay/android-developer/answer/9866151) |
| Store wording | Accurate descriptions and nonmisleading screenshots | [Store listing best practices](https://support.google.com/googleplay/android-developer/answer/13393723) |

## Launch policy decisions awaiting approval

- Free/ad-free v1.0 is the current recommendation; no ads/IAP SDKs should be included without explicit change approval.
- No account, cloud sync or analytics tracker in MVP; confirm actual runtime behavior and dependency collection before claiming no user-data collection.
- Decide Play Console account type, owner, support email and public privacy-policy hosting.
- Choose package name carefully and verify registration/availability.
- Confirm intended target audience and whether any Families requirements apply.

## Preparation checklist

- [ ] Verify Play Console account and identity requirements.
- [ ] Confirm unique application ID/package name and package-name registration.
- [ ] Verify stable Android SDK and current target policy on submission date.
- [ ] Prepare signed release AAB and secure upload credentials.
- [ ] Prepare store title, short/long description, screenshots, icon, feature graphic and contact details.
- [ ] Publish accurate app-specific privacy policy as a standard publicly accessible webpage.
- [ ] Audit dependencies for data collection and fill Data Safety truthfully.
- [ ] Complete ads, content rating, target audience and any applicable declarations.
- [ ] Plan closed testers if account needs production-access approval; retain evidence of participation and feedback.
- [ ] Check license manifest for cards, icons, fonts and audio.
- [ ] Run prerelease testing on required device configurations.
- [ ] Verify the live policy again immediately before submission.

## Avoid premature promises

This document is not legal advice, and policies can change. An offline app still needs required Play disclosures. Approval of closed testing criteria does not automatically guarantee production access.
