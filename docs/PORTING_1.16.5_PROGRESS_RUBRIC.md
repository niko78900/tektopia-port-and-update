# TekTopia 1.16.5 Port Progress Rubric

This rubric measures progress against full TekTopia parity, not against a narrow demo.

## Bucket Weights

- Core bootstrap, registries, packaging: 10%
- Village runtime, persistence, structure discovery: 15%
- Professions and worker loops: 25%
- Structures, tokens, and economy/storage integration: 15%
- Combat, raids, and hostile events: 10%
- Networking, capabilities, and multiplayer sync: 10%
- Client readability and placeholder visuals: 5%
- QA, performance, and release hardening: 10%

## Current Target Bands

- 50% means a stable singleplayer alpha: core village runtime, persistent structures, guard/farmer/blacksmith plus several additional worker loops, manual tokens, and build verification.
- 70% means a broad gameplay alpha: most daily economy loops, necromancer threat path, multiplayer sanity, packet/capability validation, and performance checks.

## Current Estimate After 95% Candidate Sweep

Estimated implementation status: 82-88% player-facing parity.

Validated status remains lower until the strict runtime gates pass. The code now has several release-candidate systems, but the 30-minute singleplayer soak and two-player multiplayer sanity pass have not been completed in this documentation update.

- Core bootstrap, registries, packaging: 9/10
- Village runtime, persistence, structure discovery: 14/15
- Professions and worker loops: 22/25
- Structures, tokens, and economy/storage integration: 14/15
- Combat, raids, and hostile events: 9/10
- Networking, capabilities, and multiplayer sync: 7/10
- Client readability and placeholder visuals: 4/5
- QA, performance, and release hardening: 6/10

The lower bound reflects that multiplayer and 30-minute singleplayer QA have not been proven in this run. The upper bound reflects that the 95% candidate sweep added parity reporting, baseline food/happiness/death behavior, villager-item lineage, larger merchant sale coverage, stricter structures, missing item models, deeper social profession behavior, and improved necromancer support behavior.

## Explicit Deferrals

- Final CraftStudio replacement and production animation parity.
- Full GUI parity beyond status/inspection feedback.
- Full 1.16 natural worldgen parity.
- Final balance tuning for profession rates, raid scaling, prices, and social behavior.
