# Research sources and verification register

**Status:** Research draft  
**Prepared:** 2026-10-29

This register links important external facts to their sources. Prefer official documentation and primary rule references. All claims about app listings, SDK versions, and Play Console policies can change and must be rechecked when development/release begins.

## Wasp and related game rules

- [Semicolon Software: Wasp](https://www.semicolon.com/Solitaire/Rules/Wasp.html) — standard seven-column deal, unrestricted fan movement to empty columns, reserve deal and goal.
- [BVS Solitaire: Wasp](https://www.bvssolitaire.com/rules/wasp.htm) — 52-card deck, 7x7 tableau, face-down pattern and Scorpion difference.
- [Semicolon Software: Scorpion](https://www.semicolon.com/Solitaire/Rules/Scorpion.html) — contrasting King-only rule for empty columns.

These sources are sufficient to ground the core movement rules but do not settle every digital presentation convention. The precise inventor/date of Wasp remains unverified.

## Android engineering references

- [Android architecture guidance](https://developer.android.com/topic/architecture)
- [Jetpack Compose](https://developer.android.com/compose)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Compose testing](https://developer.android.com/develop/ui/compose/testing)
- [Android adaptive layouts](https://developer.android.com/develop/ui/compose/layouts/adaptive)

## Google Play and policy, checked 2026-10-29

- [Google Play target API requirements](https://support.google.com/googleplay/android-developer/answer/11926878) — new standard Android submissions target API 36+ from Aug 31, 2026.
- [New personal-account closed testing](https://support.google.com/googleplay/android-developer/answer/14151465) — 12 opted-in testers continuously for at least 14 days for eligible new personal accounts.
- [Package-name registration and developer verification](https://support.google.com/googleplay/android-developer/answer/16984799).
- [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756).
- [Google Play data safety](https://support.google.com/googleplay/android-developer/answer/10787469).
- [Prepare app for review](https://support.google.com/googleplay/android-developer/answer/9859455).
- [Preview asset specifications](https://support.google.com/googleplay/android-developer/answer/9866151).
- [Store listing best practices](https://support.google.com/googleplay/android-developer/answer/13393723).

## Competitor listing leads

- [TigerStudio Wasp Solitaire](https://play.google.com/store/apps/details?id=com.tigerstudiobg.games.wasp).
- [AlgoTech Scorpion Wasp Solitaire](https://play.google.com/store/apps/details?id=com.algotgames.scorpion).
- [Maple Media Solitaire Super Pack](https://play.google.com/store/apps/details?id=com.tesseractmobile.solitairefreepack).
- [OutOfTheBit Scorpion Solitaire](https://play.google.com/store/apps/details?id=com.outofthebit.scorpion).
- [Microsoft Solitaire Collection](https://play.google.com/store/apps/details?id=com.microsoft.microsoftsolitairecollection).

Competitor listing links are research leads, not immutable evidence about current pricing, ratings or behavior.

## Candidate asset research

- [Kenney Playing Cards Pack](https://kenney.nl/assets/playing-cards-pack) — evaluate the current license and asset suitability before importing.

## Ongoing verification protocol

Record source URL, access date, observed fact, and whether the fact is a mandatory rule, product recommendation, or hypothesis. Recheck volatile Play policies before each submission. Keep asset licenses and snapshots of technical decisions in the repository rather than relying only on chat messages.
