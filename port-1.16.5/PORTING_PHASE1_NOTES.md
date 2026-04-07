# Phase 1 Bootstrap Notes

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

## Sanity Build Command (local to this folder)

```powershell
$env:JAVA_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\tooling\jdk-17.0.18+8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\.gradle-home'
.\gradlew.bat classes
```

