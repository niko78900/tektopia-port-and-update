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

## Current Estimate After Final 91% -> 100% Implementation Push

Estimated implementation status: 93-95% player-facing parity.

Validated status remains lower until the strict runtime gates pass. The code now has a container-backed villager GUI with Main, AI Filter, Village, Storage, and Trade tabs; server-authoritative GUI trade/filter actions; direct reconnect resync; real CraftStudio cube rendering for loaded models; profession-readable recreated asset coverage for every registered entity; starter worldgen status/perf surfaces with persisted attempt/failure/duplicate-prevention counters; and explicit saved-data migration. The 30-minute singleplayer soak, client visual boot, natural worldgen validation, and two-player multiplayer sanity pass have not been completed in this documentation update.

- Core bootstrap, registries, packaging: 9.8/10
- Village runtime, persistence, structure discovery: 14.7/15
- Professions and worker loops: 23.5/25
- Structures, tokens, and economy/storage integration: 14.4/15
- Combat, raids, and hostile events: 9.3/10
- Networking, capabilities, and multiplayer sync: 9.4/10
- Client readability and animation foundation: 4.8/5
- QA, performance, and release hardening: 7.5/10

The lower bound reflects that multiplayer, client visual boot, and 30-minute singleplayer QA have not been proven in this run. The upper bound reflects that the full-parity push now covers all registered visual asset paths, real CraftStudio cube rendering, container-backed villager inspection with village/storage/trade tabs, server-authoritative GUI filter/trade actions, reconnect resync, starter worldgen duplicate/failure diagnostics, build/release validation, and saved-data migration.

## Explicit Deferrals

- Visual quality parity for recreated non-Guard assets; resource coverage is complete, but non-Guard models/textures are recreated from the recovered humanoid base until better assets are supplied or remade.
- Separate standalone village/storage/trade containers beyond the container-backed villager GUI tabs and status/command surfaces.
- Full 1.16 natural biome worldgen parity beyond controlled starter generation plus starter worldgen status hooks.
- Final balance tuning for profession rates, raid scaling, prices, and social behavior.
