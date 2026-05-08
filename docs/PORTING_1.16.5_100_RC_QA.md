# TekTopia 1.16.5 100% Release-Candidate QA

This checklist is the hard gate for moving from the current 85-90% implementation candidate to a 100% full-parity release claim.

## Build Gate

- Run `.\gradlew.bat clean compileJava --rerun-tasks`.
- Run `.\gradlew.bat build`.
- Run `.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true`.
- Result in this implementation chunk: not yet rerun after all 100% work.

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
  - `/tektopia_port asset_inventory`
  - `/tektopia_port asset_inventory strict`
- `asset_inventory strict` must pass before 100% can be claimed.

## Client Visual Gate

- Boot a client with no missing renderer, model, or texture errors.
- Spawn every registered Tek entity and inspect the renderer path.
- Confirm Guard uses the recovered CraftStudio model and texture path.
- Confirm every required entity in `visual_coverage.json` is `recovered`, `recreated`, or `custom_renderer`.
- Confirm no required entity depends on normal-release Biped fallback.

## GUI Gate

- Right-click a villager while not crouching and confirm a player-facing GUI opens.
- Toggle AI filters from the GUI and verify server authority.
- Inspect village status, structures, storage/economy, reservations, and trade offers through GUI screens.
- Reconnect while near a village and confirm GUI state resyncs.

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

## Current Result

- The 100% gate is not passed yet.
- Current known blockers: strict visual asset coverage, full container-backed GUI, natural worldgen validation, two-client multiplayer validation, and full soak/performance evidence.
