# Phase 1-2 Bootstrap Notes

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

## Sanity Build Command (local to this folder)

```powershell
$env:JAVA_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\tooling\jdk-17.0.18+8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\.gradle-home'
.\gradlew.bat classes
```
