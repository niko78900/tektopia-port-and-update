# TekTopia 1.16.5 70% Validation And 80% Alpha QA

This checklist is the gate for calling the current port 70% validated and the next feature sweep 80% alpha.

## Build Gate

- Run `.\gradlew.bat clean build` from `port-1.16.5`.
- Build must pass without ignored/conflict Java files under `src/main/java`.

## 70% Validation Gate

- Start a new singleplayer world and run `/tektopia_port qa_start`.
- Build or discover Town Hall, Storage, Home, Farm, Kitchen, Blacksmith, Butcher, sheep/cow/pig/chicken pens, Guard Post or Barracks, Merchant Stall, School, Tavern, and Library.
- Use `/tektopia_port qa_status`, `workforce_status`, `economy_status`, `reservations_status`, `pens_status`, and `raid_status` every 5 minutes during a 30-minute loop.
- During the loop, force missing-input, full-storage, invalid-structure, blocked-path, active-reservation, visitor, and raid cases.
- Save/reload once with active workers and once during or immediately after a raid.
- Pass criteria: no crash, no duplicated reserved items, no permanently stuck worker loops, no lost village/structure/reservation/raid state after reload.

## Multiplayer Sanity Gate

- Run two players near one village.
- Confirm both players see synced village state, structure validity, villager thought/status, item thoughts, alerts, and path/debug status.
- Change a guard AI filter from one player and confirm the other player receives the authoritative result.
- Run concurrent worker loops against shared Storage and confirm `reservations_status` does not show duplicated or stuck reservations.

## 80% Alpha Acceptance

- The build gate and 70% validation gate pass first.
- Functional client feedback exists for villager, village, trade, AI filter, alert, and blocked-worker state.
- Merchant/Architect/Tradesman prices and stock are visible.
- Teacher/Child, Bard, Cleric, Druid, Enchanter, Merchant, Guard, and Raid loops expose useful status and recover from missing inputs or unreachable targets.
- Remaining known gaps are limited to final visuals/animation, full GUI polish, natural worldgen polish, and balance tuning.
