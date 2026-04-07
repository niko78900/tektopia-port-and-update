# Phase 1-6 Bootstrap Notes

This folder is an isolated Forge 1.16.5 scaffold for the TekTopia port effort.

## Isolation Guarantee

- Existing 1.12.2 source tree under `decompiled/` is untouched.
- Existing release/build scripts in repository root are untouched.
- All Phase 1 work is contained under `port-1.16.5/`.

## Current State

- Forge MDK baseline: `1.16.5-36.2.42`
- Mod id: `tektopia`
- Bootstrap class: `net.tangotek.tektopia.TekTopiaPort`
- Placeholder package layout created for:
  - `client`
  - `common`
  - `registry`
  - `network`
  - `caps`
  - `entities`
  - `entities.ai`
  - `structures`
  - `worldgen`
  - `village`

## Phase 2 Additions

- Deferred register shells added:
  - `registry/TekBlocks.java`
  - `registry/TekItems.java`
  - `registry/TekEntities.java`
  - `registry/TekPotions.java`
  - `registry/TekSounds.java`
- Network bootstrap shell added:
  - `network/TekNetwork.java`
- Command registration scaffold added:
  - `/tektopia_port ping`
  - `common/TekCommandEvents.java`
- Gamerule migration checkpoint class added (registration still deferred to later gameplay phase):
  - `common/TekGameRules.java`

## Phase 3 Additions

- `SimpleChannel` now registers scaffolds for the legacy packet set:
  - `PacketAIFilter`
  - `PacketLicense`
  - `PacketPathingNode`
  - `PacketVillage`
  - `PacketVillagerItemThought`
  - `PacketVillagerThought`
- Packet payload encode/decode/handle stubs are in:
  - `network/message/*`

## Phase 4 Additions

- Capability interfaces and providers ported to 1.16 style:
  - `caps/IPlayerLicense.java`
  - `caps/PlayerLicense.java`
  - `caps/PlayerLicenseProvider.java`
  - `caps/IVillageData.java`
  - `caps/VillageData.java`
  - `caps/VillageDataProvider.java`
  - `caps/TekCapabilities.java`
- Capability event wiring added:
  - `common/TekCapabilityEvents.java`
  - player attach/clone/tracking sync scaffolds
  - item capability attach scaffold for future town hall token path
- `PacketLicense` now applies server-submitted license data through capability events.
- Command validation path for capability state:
  - `/tektopia_port license get`
  - `/tektopia_port license set <data>`

## Phase 5 Additions

- First 1.16.5 custom-entity vertical slice added:
  - `entities/TekGuardEntity.java`
  - synced data flag (`equipGoldArmor`) + NBT save/load scaffold
  - baseline goals/attributes for compile-time AI migration anchor
- Entity registration and runtime wiring added:
  - `registry/TekEntities.java` (`tek_guard`)
  - `registry/TekItems.java` (`tek_guard_spawn_egg`)
  - `entities/TekEntityEvents.java` (attribute creation event)
- Dev command path extended:
  - `/tektopia_port spawn_test_guard`
  - gated to permission level 2 for controlled testing
- Guard/AI filter continuation pass:
  - `entities/TekVillagerEntity.java` provides shared AI filter registration/state/persistence scaffold.
  - `network/message/PacketAIFilter.java` now applies filter toggles server-side to port entities.
  - `entities/TekGuardEntity.java` now extends `TekVillagerEntity` and includes:
    - legacy-aligned guard filter keys (`equip_*_armor`, `equip_*_sword`)
    - hostile target goals + basic weapon/armor acceptance scoring against filters
  - Command surface expanded for live testing:
    - `/tektopia_port guard_filters`
    - `/tektopia_port guard_filter <filter> <enabled>`

## Sanity Build Command (local to this folder)

```powershell
$env:JAVA_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\tooling\jdk-17.0.18+8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\.gradle-home'
.\gradlew.bat classes
```

## Phase 6 Additions

- Structure runtime scaffolds added under `structures/`:
  - `TekVillageStructure.java`:
    - flood-fill floor scan with `MAX_FLOOR` guard rails
    - vertical traversal checks for stairs/slabs and ladder/vine climbables
    - room-height scan and per-structure special-block hooks
  - `TekStructureType.java` (`TOWNHALL`, `STORAGE`)
  - `TekStructureTownHall.java`
  - `TekStructureStorage.java` (tracks chest positions + key workstation blocks)
- Lightweight structure manager scaffold:
  - `village/TekVillageStructureManager.java`
- Dev command surface expanded for structure validation:
  - `/tektopia_port scan_structure <townhall|storage>`
  - `/tektopia_port scan_structure_status`
  - `/tektopia_port discover_structures <radius>`
  - `/tektopia_port clear_structure_cache`
- Frame/token discovery scaffold:
  - `village/TekStructureDiscovery.java`
  - scans nearby `ItemFrameEntity` markers and resolves structure type from:
    - tektopia item registry path (`townhall`/`storage`)
    - frame item display name fallback (`Town Hall`/`Storage`)
- Shared runtime integration:
  - `village/TekVillageRuntime.java` (per-dimension structure manager cache)
  - `common/TekStructureEvents.java` (periodic server auto-discovery + unload cache cleanup)
  - `common/TekCommandEvents.java` now reads/writes runtime cache instead of command-local state
  - added `/tektopia_port nearest_structure <townhall|storage>`
- Playability additions:
  - `client/TekClientEvents.java` + `client/TekGuardRenderer.java` for client-safe guard rendering
  - `registry/TekItems.java` now includes:
    - `structure_townhall_token`
    - `structure_storage_token`
  - item/lang assets added under `src/main/resources/assets/tektopia/`
  - command:
    - `/tektopia_port starter_kit` (spawn egg + structure tokens + item frames)
