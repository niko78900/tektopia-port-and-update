# TekTopia 1.16.5 95% Release-Candidate QA

This checklist is the validation gate for moving the port from 80% alpha candidate to 90-95% parity candidate.

## Quick Gate

- Run `.\gradlew.bat build` from `port-1.16.5`.
- Confirm `/tektopia_port parity_report` works in a loaded test world.
- Confirm `/tektopia_port asset_inventory` reports recovered Guard assets and explicit gaps for missing models/textures.
- Confirm `/tektopia_port sync_status` reports protocol `2`, initialized network state, and nearest-village packet radius.
- Confirm `/tektopia_port gui_snapshot` sends the nearest villager snapshot to chat and the client status cache.
- Confirm `/tektopia_port worldgen_test <townhall|storage|home|farm|mineshaft>` creates scanner-compatible starter structures.
- Confirm only intended local commits are ahead of origin and `Textures/` remains reference-only.

## Singleplayer Soak

- Start a new world and run `/tektopia_port qa_start`.
- Use `/tektopia_port worldgen_test townhall`, `storage`, `home`, `farm`, and `mineshaft` once to validate starter generation and scanner caching before manual structure expansion.
- Discover or build Town Hall, Storage, Home, Farm, Kitchen, Blacksmith, Butcher, sheep/cow/pig/chicken pens, Guard Post/Barracks, Merchant Stall, School, Tavern, and Library.
- Every 5 minutes for 30 minutes run `parity_report`, `qa_status`, `workforce_status`, `economy_status`, `reservations_status`, `pens_status`, `trade_status`, and `raid_status`.
- During the loop force missing-input, full-storage, invalid-structure, blocked-path, active-reservation, visitor, food shortage, and raid cases.
- Save/reload once with active workers and once during or immediately after a raid.

## Multiplayer Sanity

- Run two players near one village for at least 10 minutes.
- Run `sync_status` after join and after reconnect to confirm the server-side snapshot authority and protocol version.
- Confirm both players receive village, structure, villager, thought, item-thought, alert, path/debug, trade, and AI-filter state.
- Right-click inspect one villager from each client and confirm the `PacketVillagerGuiSnapshot` data appears on the status screen.
- Change a guard AI filter from one player and confirm the other player sees the server-authoritative result.
- Run concurrent workers against the same Storage and confirm reservations do not duplicate items or remain stuck after expiry.

## Client Visual Gate

- Run `/tektopia_port asset_inventory`.
- Confirm Guard reports recovered `guard_m.csjsmodel`, `guard_m.png`, and `guard_f.png`.
- Boot a client and confirm the visual coverage log reports explicit missing-asset gaps for non-Guard entities instead of silent fallback.
- Spawn Guard and verify the recovered guard texture renders through the CraftStudio-aware renderer with Biped/armor fallback.
- Spawn every Tek entity and confirm no missing model/texture crash.

## Pass Criteria

- No compile/build errors.
- No crash during the soak.
- Villages, structures, residents, assignments, inventories, reservations, cooldowns, visitors, and raids survive reload.
- Workers recover from unreachable targets, missing inputs, full storage, invalid pens, vanished animals, and removed chests.
- Remaining gaps are limited to full CraftStudio pose rendering coverage, natural worldgen polish, and balance tuning.

## Current Validation Result

- `.\gradlew.bat clean compileJava --rerun-tasks`: passed after the full-parity push.
- `.\gradlew.bat build`: passed after the full-parity push.
- `.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true`: passed after the full-parity push.
- `.\gradlew.bat compileJava --rerun-tasks`: passed after every implementation chunk in the full-parity push.
- `/tektopia_port parity_report`: implemented, not runtime-tested in a loaded game session here.
- `/tektopia_port asset_inventory`, `sync_status`, `gui_snapshot`, and `worldgen_test`: implemented, not runtime-tested in a loaded game session here.
- 30-minute singleplayer soak: not run in this coding session.
- Save/reload runtime validation: not run in this coding session.
- Two-player multiplayer sanity: not run in this coding session.

Current milestone label: 85-90% implementation candidate with the internal animation track started. Do not mark 90-95% release-candidate status until the live game-session gates above pass.
