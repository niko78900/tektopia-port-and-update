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
- 85% means a playable parity candidate: functional villager GUI/filter control, most profession loops, guarded persistence/reload behavior, Biped-first readable visuals, starter worldgen path, multiplayer validation, and release packaging evidence.

## Current Alpha.3 Code State

- Build/package state: clean `compileJava` and `build` are passing.
- Added Biped-first villager GUI snapshot screen, `PacketVillagerGuiSnapshot`, server right-click open path, and AI filter toggle packet re-sync.
- Added lightweight registered roles for cleric, teacher, enchanter, druid, bard, architect, child, and nitwit with spawn eggs, attributes, renderers, filters, and basic daily loops.
- Added bounded necromancer minion management, guard-target priority, stale minion cleanup, performance counters, tick/scan staggering, and alpha QA commands.
- Added starter structure/worldgen test builder for Town Hall, Storage, Home, Farm, and Mineshaft.
- Runtime singleplayer, multiplayer, and long-play QA are still required before this can honestly be called an 85% parity candidate.

## Explicit Deferrals

- Final CraftStudio replacement and production animation parity.
- Pixel-perfect legacy GUI parity.
- Full 1.16 natural worldgen parity.
- Low-priority social polish until the gameplay loop is stable.
