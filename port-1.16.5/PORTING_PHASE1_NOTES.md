# Phase 1-5 Bootstrap Notes

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

## Sanity Build Command (local to this folder)

```powershell
$env:JAVA_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\tooling\jdk-17.0.18+8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\.gradle-home'
.\gradlew.bat classes
```
