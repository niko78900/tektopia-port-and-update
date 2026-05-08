# TekTopia 1.16.5 RC Release Notes Draft

## Current Candidate State

This is a build-green implementation candidate, not a full 100% release claim. The 2026-05-08 final 91% -> 100% implementation pass build gates passed:

- `.\gradlew.bat clean compileJava --rerun-tasks`
- `.\gradlew.bat build`
- `.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true`

## Added In The 100% Push

- Container-backed villager GUI with status, inventory snapshot, AI filter tab, and right-click open path.
- Village, Storage, and Trade tabs in the villager GUI backed by `PacketVillageGuiSnapshot`, `PacketStorageGuiSnapshot`, and `PacketTradeGuiSnapshot`.
- Server-authoritative AI filter and architect/tradesman GUI trade actions.
- Reconnect/dimension-change resync for village, villager, thought, item-thought, GUI, and AI filter state.
- Real CraftStudio cube-tree rendering for loaded `.csjsmodel` assets.
- Profession-readable recreated visual asset coverage for every registered entity, tracked by `visual_coverage.json`.
- Strict `/tektopia_port asset_inventory strict` gate.
- Starter worldgen status/perf commands and persisted starter attempt, failure, backoff, and duplicate-prevention state.
- Explicit village saved-data migration gate.

## RC Hardening After Candidate

- Hardened AI-filter and GUI trade packets with server-side villager/container/distance/dimension validation.
- Tightened architect/tradesman GUI purchases so token creation, ownership, cost, and player inventory mutation remain server-authoritative.
- Added village UUID/center binding metadata to generated and purchased tokens, plus frame-discovery rejection for wrong-village bound tokens.
- Bound controlled starter side-structure markers to the generated starter village when available.
- Rejected malformed oversized GUI snapshot counts instead of clipping them into partial client state.
- Added `captain_aura` support to `spawn_test_worker` for spawn-every-registered-entity QA.
- Expanded `qa_status`, `parity_report`, `perf_status`, and `reservations_status` with PASS/WARN/FAIL diagnostics for runtime QA.
- Static `visual_coverage.json` resource resolution passed for all 23 entries on 2026-05-08.
- The same three build gates passed again after this hardening pass.

## Still Required Before 100%

- Live client visual boot and spawn-every-entity validation.
- 30-minute singleplayer soak with reload and raid cases.
- Dedicated two-client multiplayer validation.
- Natural starter worldgen validation across multiple new worlds.
- Village A versus Village B token ownership runtime validation.
- 1-, 3-, and 5-village performance runs.
- Final-quality replacement pass for recreated non-Guard visual assets.
