# TekTopia 1.16.5 100% Release-Candidate QA

This checklist is the hard gate for moving from the current 93-95% implementation candidate to a 100% full-parity release claim.

## Build Gate

- Run `.\gradlew.bat clean compileJava --rerun-tasks`.
- Run `.\gradlew.bat build`.
- Run `.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true`.
- Result on 2026-05-08 after the RC hardening pass:
  - `.\gradlew.bat clean compileJava --rerun-tasks`: passed.
  - `.\gradlew.bat build`: passed.
  - `.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true`: passed.

## Command Gate

- In a loaded singleplayer test world, run:
  - `/tektopia_port qa_start`
  - `/tektopia_port parity_report`
  - `/tektopia_port qa_status`
  - `/tektopia_port workforce_status`
  - `/tektopia_port economy_status`
  - `/tektopia_port reservations_status`
  - `/tektopia_port pens_status`
  - `/tektopia_port trade_status`
  - `/tektopia_port raid_status`
  - `/tektopia_port sync_status`
  - `/tektopia_port worldgen_status`
  - `/tektopia_port perf_status`
  - `/tektopia_port asset_inventory`
  - `/tektopia_port asset_inventory strict`
- `asset_inventory strict` must pass before 100% can be claimed.
- `qa_status`, `parity_report`, `perf_status`, and `reservations_status` should include actionable PASS/WARN/FAIL lines. Any FAIL blocks 100%.

## Client Visual Gate

- Boot a client with no missing renderer, model, or texture errors.
- Spawn every registered Tek entity and inspect the renderer path.
- Confirm Guard uses the recovered CraftStudio model and texture path.
- Confirm every required entity in `visual_coverage.json` is `recovered`, `recreated`, or `custom_renderer`.
- Confirm no required entity depends on normal-release Biped fallback.

## GUI Gate

- Right-click a villager while not crouching and confirm a player-facing GUI opens.
- Toggle AI filters from the villager GUI and verify server authority. The server must reject client packets when the target villager is gone, too far away, in another dimension, or not the villager currently open in the player's container.
- Inspect villager status, inventory slots, AI filters, and architect/tradesman trade actions through the villager GUI.
- Inspect Village, Storage, and Trade tabs in the villager GUI and confirm the server sends `PacketVillageGuiSnapshot`, `PacketStorageGuiSnapshot`, and `PacketTradeGuiSnapshot`.
- Confirm malformed or oversized GUI snapshot counts disconnect/fail decoding rather than producing clipped or inconsistent client state.
- Reconnect while near a village and confirm village, villager, thought, item-thought, GUI snapshot, and AI filter state resyncs.

## Token Ownership Gate

- Buy/generate a village-bound token in Village A and confirm its debug/status output shows binding metadata.
- Attempt to use the bound token in Village B by frame discovery and direct use. The server must reject the wrong-village use without creating or assigning a structure in Village B.
- Confirm unbound/manual tokens still work for migration-safe manual discovery.
- Test concurrent architect/tradesman GUI purchases from two clients and confirm emerald cost, item grant, and token purchase counters remain server-authoritative with no duplication.

## Singleplayer Soak Gate

- New world, starter generation, and manual frame/token discovery.
- 30-minute village loop with all registered professions present.
- Save/reload with active workers, active reservations, villagers carrying items, invalid structures, and active raid state.
- Confirm no worker deadlocks, reservation leaks, packet spam, or structure rescan failures.

## Multiplayer Gate

- Dedicated server, two clients, at least 30 minutes.
- Both clients must agree on village, villager, GUI, trade, AI filter, thought, item-thought, alert, raid, storage, and path/debug state.
- Client reconnect must receive fresh authoritative snapshots.
- Concurrent workers and concurrent trade/GUI actions must not duplicate items or purchases.

## Worldgen Gate

- Generate multiple new worlds.
- Natural starter structures appear at controlled rates.
- Generated structures scan, persist, and do not duplicate villages uncontrollably.
- Manual frame/token discovery remains supported.

## Performance Gate

- Run 1-, 3-, and 5-village tests.
- Confirm no sustained village tick overload, repeated structure scan spikes, packet spam, worldgen duplicate checks, or reservation leaks.
- Check `perf_status` for village tick timing, frame-scan candidate/accepted/rejected counts, and worldgen duplicate-prevention counters.

## Current Result

- The 100% gate is not passed yet.
- Implementation advanced substantially: strict asset coverage exists, all registered entity renderers point at CraftStudio asset paths, Guard renders through real CraftStudio cube parts, villager GUI is container-backed with Main/AI/Village/Storage/Trade tabs, AI filters/trade actions have server-side container/distance/entity validation, reconnect resends GUI state, village-bound token guardrails exist for generated/purchased tokens and frame discovery, starter worldgen status/perf commands exist, worldgen saved data records attempts/failures/duplicate guards, and saved data has an explicit migration gate.
- Static check on 2026-05-08 resolved all 23 `visual_coverage.json` entries to existing resource files. This is not a substitute for live client visual boot.
- Remaining blockers: live client visual boot with every entity spawned, visual quality pass on recreated non-Guard assets, natural worldgen validation, dedicated two-client multiplayer validation, Village A/Village B token runtime validation, full 30-minute soak/reload evidence, and 1/3/5-village performance evidence.
