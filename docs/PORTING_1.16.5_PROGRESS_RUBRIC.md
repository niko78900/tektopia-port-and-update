# TekTopia 1.16.5 Port Progress Rubric

This rubric measures progress against full TekTopia parity, not against a narrow demo.

## Bucket Weights

- Core bootstrap, registries, packaging: 10%
- Village runtime, persistence, structure discovery: 15%
- Professions and worker loops: 25%
- Structures, tokens, and economy/storage integration: 15%
- Combat, raids, and hostile events: 10%
- Networking, capabilities, and multiplayer sync: 10%
- Client readability and animation foundation: 5%
- QA, performance, and release hardening: 10%

## Current Target Bands

- 50% means a stable singleplayer alpha: core village runtime, persistent structures, guard/farmer/blacksmith plus several additional worker loops, manual tokens, and build verification.
- 70% means a broad gameplay alpha: most daily economy loops, necromancer threat path, multiplayer sanity, packet/capability validation, and performance checks.

## Current Estimate After Full-Parity Push

Estimated implementation status: 85-90% player-facing parity.

Validated status remains lower until the strict runtime gates pass. The code now starts the internal CraftStudio-compatible track and adds stronger GUI/sync/worldgen test surfaces, but the 30-minute singleplayer soak, client visual boot, and two-player multiplayer sanity pass have not been completed in this documentation update.

- Core bootstrap, registries, packaging: 9/10
- Village runtime, persistence, structure discovery: 14/15
- Professions and worker loops: 22/25
- Structures, tokens, and economy/storage integration: 14/15
- Combat, raids, and hostile events: 9/10
- Networking, capabilities, and multiplayer sync: 8/10
- Client readability and animation foundation: 4/5
- QA, performance, and release hardening: 6.5/10

The lower bound reflects that multiplayer, client visual boot, and 30-minute singleplayer QA have not been proven in this run. The upper bound reflects that the full-parity push added recovered Guard CraftStudio assets, a visual-gap manifest, a CraftStudio model loader/renderer foundation, villager GUI snapshot packet/cache support, right-click snapshot inspection, starter structure generation commands, sync diagnostics, and visual asset reporting.

## Explicit Deferrals

- Full CraftStudio pose rendering for every entity and complete required asset coverage.
- Full container-backed GUI parity beyond snapshot/status feedback.
- Full 1.16 natural biome worldgen parity beyond controlled starter generation.
- Final balance tuning for profession rates, raid scaling, prices, and social behavior.
