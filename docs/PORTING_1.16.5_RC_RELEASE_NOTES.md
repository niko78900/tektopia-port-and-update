# TekTopia 1.16.5 RC Release Notes Draft

## Current Candidate State

This is a build-green implementation candidate, not a full 100% release claim. The 2026-05-08 build gates passed:

- `.\gradlew.bat clean compileJava --rerun-tasks`
- `.\gradlew.bat build`
- `.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true`

## Added In The 100% Push

- Container-backed villager GUI with status, inventory snapshot, AI filter tab, and right-click open path.
- Server-authoritative AI filter and architect/tradesman GUI trade actions.
- Reconnect/dimension-change resync for village, villager, thought, item-thought, GUI, and AI filter state.
- Real CraftStudio cube-tree rendering for loaded `.csjsmodel` assets.
- Recreated visual asset coverage for every registered entity, tracked by `visual_coverage.json`.
- Strict `/tektopia_port asset_inventory strict` gate.
- Starter worldgen status/perf commands and persisted starter duplicate-prevention state.
- Explicit village saved-data migration gate.

## Still Required Before 100%

- Live client visual boot and spawn-every-entity validation.
- 30-minute singleplayer soak with reload and raid cases.
- Dedicated two-client multiplayer validation.
- Natural starter worldgen validation across multiple new worlds.
- 1-, 3-, and 5-village performance runs.
- Final-quality replacement pass for recreated non-Guard visual assets.
