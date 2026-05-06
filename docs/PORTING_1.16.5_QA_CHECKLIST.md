# TekTopia 1.16.5 Alpha QA Checklist

Use this checklist before tagging the next 1.16.5 alpha.

## Build Gates

- `gradlew.bat clean compileJava --rerun-tasks`
- `gradlew.bat build`
- `gradlew.bat releaseJar -Ptektopia_enable_reobf=true`
- Confirm `verifySourceHygiene` runs and no `*sync-conflict*.java` files exist under `src/main/java`.

## Singleplayer Smoke Test

Run in a new Forge 1.16.5 world:

```text
/tektopia_port starter_kit
/tektopia_port discover_structures 64
/tektopia_port village status
/tektopia_port worker_status
/tektopia_port workforce_status
/tektopia_port economy_status
/tektopia_port guard_status
/tektopia_port necromancer_raid 2
```

Acceptance:
- Town Hall and Storage are rediscovered from frame tokens.
- Village persists after save, quit, reload, and command status still resolves the same village.
- Farmer harvests/replants/delivers crops.
- Miner and lumberjack create deterministic storage outputs instead of destroying blocks with no drops.
- Lumberjack replants matching saplings when the harvested log position is valid soil.
- Blacksmith crafts armor from storage demand.
- Guards equip monotonic upgrades and react to raids.
- Thought and item-thought packets create client-visible particles for tracked villagers.

## Multiplayer Smoke Test

Run a dedicated server and connect two clients:

- Client A creates/discovers the village and spawns workers.
- Client B observes village status, villager thought particles, guard equipment, and raid alert response.
- Disconnect/reconnect Client B and confirm license capability sync and village snapshots resume.
- Trigger `/tektopia_port guard_filter equip_gold_armor false`, then verify guards stop taking gold armor on both clients.

## Long-Run Checks

- Run at least 20 minutes with one village, then repeat with three villages.
- Watch server log for missing renderer/model warnings, packet decode errors, and tick spikes.
- Repeatedly reload while villagers are carrying farmer drops, storage reservations are active, and a raid alert is active.
