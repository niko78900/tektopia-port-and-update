# TekTopia 1.16.5 95% Release-Candidate QA

This checklist is the validation gate for moving the port from 80% alpha candidate to 90-95% parity candidate.

## Quick Gate

- Run `.\gradlew.bat build` from `port-1.16.5`.
- Confirm `/tektopia_port parity_report` works in a loaded test world.
- Confirm only intended local commits are ahead of origin and `Textures/` remains reference-only.

## Singleplayer Soak

- Start a new world and run `/tektopia_port qa_start`.
- Discover or build Town Hall, Storage, Home, Farm, Kitchen, Blacksmith, Butcher, sheep/cow/pig/chicken pens, Guard Post/Barracks, Merchant Stall, School, Tavern, and Library.
- Every 5 minutes for 30 minutes run `parity_report`, `qa_status`, `workforce_status`, `economy_status`, `reservations_status`, `pens_status`, `trade_status`, and `raid_status`.
- During the loop force missing-input, full-storage, invalid-structure, blocked-path, active-reservation, visitor, food shortage, and raid cases.
- Save/reload once with active workers and once during or immediately after a raid.

## Multiplayer Sanity

- Run two players near one village for at least 10 minutes.
- Confirm both players receive village, structure, villager, thought, item-thought, alert, path/debug, trade, and AI-filter state.
- Change a guard AI filter from one player and confirm the other player sees the server-authoritative result.
- Run concurrent workers against the same Storage and confirm reservations do not duplicate items or remain stuck after expiry.

## Pass Criteria

- No compile/build errors.
- No crash during the soak.
- Villages, structures, residents, assignments, inventories, reservations, cooldowns, visitors, and raids survive reload.
- Workers recover from unreachable targets, missing inputs, full storage, invalid pens, vanished animals, and removed chests.
- Remaining gaps are limited to final animation/model polish, natural worldgen polish, and balance tuning.

## Current Validation Result

- `.\gradlew.bat classes`: passed after every implementation chunk in the 95% candidate sweep.
- `.\gradlew.bat build`: passed after the sweep.
- `/tektopia_port parity_report`: implemented, not runtime-tested in a loaded game session here.
- 30-minute singleplayer soak: not run in this coding session.
- Save/reload runtime validation: not run in this coding session.
- Two-player multiplayer sanity: not run in this coding session.

Current milestone label: 90-95% implementation candidate, with release-candidate validation still pending the live game-session gates above.
