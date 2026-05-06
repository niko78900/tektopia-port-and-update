# Tektopia Port and Update

Community-maintained patch workspace for a decompiled TekTopia (Minecraft 1.12.2) jar.

This repository is not a standard Gradle mod source tree. It is a practical patching workspace built around:
- decompiled Java sources under `decompiled/`
- targeted recompilation of selected classes
- repackaging those class files and selected assets into a base TekTopia jar

## Project Intent

The goal of this fork is to add quality-of-life updates and gameplay tweaks while keeping the original mod playable on Minecraft 1.12.2.

Long-term goal: port TekTopia functionality to newer Minecraft/Forge (or NeoForge/Fabric-equivalent) versions while keeping gameplay behavior as close as practical to the original design.

Current focus areas include:
- villager crafting/inventory QoL behavior
- guard equipment policy improvements (including gold armor)
- blacksmith crafting pool updates
- structure scanning improvements for multi-floor buildings
- vanilla golem behavior experiments integrated with village combat logic

## Current Status

- Minecraft target: `1.12.2`
- Mod id: `tektopia`
- Source annotation version in decompiled Java: `1.1.1` (patched at build time for release jars)
- Release jars produced in this workspace:
- `out/tektopia-1.1.1.jar`
- `out/tektopia-1.1.2.jar`
- `releases/1.1.3/tektopia-1.1.3.jar` (if present locally)
- `1.1.x` builds are treated as pre-release bridge milestones, not final production releases.

### 1.16.5 Port Alpha (In-Repo Workspace)

An isolated Forge 1.16.5 port workspace exists at `port-1.16.5/`.

Current target milestone is `1.16.5-alpha.2` focused on a stable singleplayer core loop:
- Town Hall + Storage discovery and persistence across reloads
- farmer harvest -> collect -> deliver loop
- blacksmith demand-driven armor crafting (iron/gold, optional diamond by material/policy)
- guard storage-based monotonic gear upgrades
- village alert memory, guard rally, civilian retreat/recovery

Dev build command:

```powershell
$env:JAVA_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\tooling\jdk-17.0.18+8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\.gradle-home'
cd .\port-1.16.5
.\gradlew.bat build
```

Release packaging command:

```powershell
$env:JAVA_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\tooling\jdk-17.0.18+8'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME='c:\Users\Niko\Desktop\Code\Tektopia Update\port-1.16.5\.gradle-home'
cd .\port-1.16.5
.\gradlew.bat releaseJar -Ptektopia_enable_reobf=true
```

Smoke-test command flow (in-game):

```text
/tektopia_port starter_kit
/tektopia_port discover_structures 64
/tektopia_port village status
/tektopia_port worker_status
/tektopia_port economy_status
/tektopia_port guard_status
/tektopia_port raid_test 6
```

Output jar:
- `port-1.16.5/build/libs/tektopia-1.16.5-alpha.2.jar`

## Porting Goal and Release Policy

- Primary objective: deliver a maintainable port to newer Minecraft versions.
- Current `1.1.x` line: stabilization and gameplay/QoL validation on 1.12.2 before full port migration.
- GitHub release policy: mark `1.1.x` artifacts as **Pre-release** until core AI/combat/structure behavior is validated and port planning is complete.
- Porting priorities:
  1. stabilize behavior in current branch
  2. reduce decompile/mapping fragility
  3. move toward a clean modern source layout and modern loader target

Port-prep documents:
- `docs/PORTING_TO_1.16.5_PLAN.md`
- `docs/PORTING_1.16.5_BACKLOG.md`
- `docs/PORTING_1.16.5_QA_CHECKLIST.md`

## Version History

### 1.1.0 (Base Upstream Jar)

- Serves as the baseline jar used by the patch build script.
- No fork QoL changes applied at this stage.

### 1.1.1 (Initial QoL Fork Release)

- Implemented initial 6-phase QoL scope:
repository cleanup,
license fail-open adjustment,
decompile stabilization,
crafting/inventory QoL behavior,
runtime toggles,
validation pass.
- Added guard gold armor support to AI filter/equipment policy.
- Added blacksmith gold armor recipes (boots/chestplate/leggings/helmet).
- Fixed release localization gaps for newly added gold-related filters.
- Hardened patch build pipeline for Java 8 compatibility.
- Release class: pre-release bridge build.

### 1.1.2 (Gold Armor Visual Pipeline)

- Added/updated guard model and texture pipeline for gold armor visuals.
- Iterated asset packaging flow in release build process.
- Marked bodyguard texture fidelity as work-in-progress during iteration.
- Release class: pre-release bridge build.

### 1.1.3 (Structure + Golem AI Iteration)

- Added multi-floor structure scanning support with slab/stair/ladder/vine traversal.
- Iterated vanilla golem behavior integration with village defense logic:
target selection,
retreat behavior,
sunrise return handling,
snow golem combat tuning.
- Continued fixes for snow golem combat edge cases
and reduced villager-friendly-fire risk.
- Known state from recent testing: iron golem behavior improved but still needs more validation;
snow golem behavior still has unresolved issues in some scenarios.
- Release class: pre-release bridge build.

## Completed Work Summary

### Core QoL phases (initial 6-phase plan)

1. Repository hygiene and import blocker cleanup.
2. Runtime license gating moved to fail-open behavior for QoL flow.
3. Decompiler type/cast stabilization in key logic paths.
4. Villager crafting/inventory fixes:
transactional item consumption,
stack-aware capacity checks,
success-only penalty application.
5. Runtime QoL toggles for crafting behavior.
6. Validation and cleanup pass with temp/sync-noise controls.

### Post-phase feature additions

- Guard AI filters now include gold armor equipment policy.
- Blacksmith recipes now include gold armor crafting (boots/chestplate/leggings/helmet).
- Localization support added for new guard/blacksmith gold filter keys.
- Multi-floor building scan support added, including vertical traversal via stairs/slabs/ladders/vines.
- Additional golem behavior logic introduced for village combat/retreat/return scenarios.
- Guard texture/model iteration support for gold armor visuals.

## Repository Layout

- `decompiled/`
Main decompiled source tree and asset overrides used for patch builds.
- `decompiled/net/tangotek/tektopia/`
Primary mod code package.
- `decompiled/assets/tektopia/`
Asset overrides currently tracked in source (guard model/texture files).
- `build-release-1.1.1.ps1`
Primary patch-build script used to compile selected classes and rebuild jar outputs.
- `tooling/`
Local build dependencies (Forge, Minecraft, CraftStudio jars, mappings).
- `out/`
Local build outputs.
- `releases/`
Local release packaging area by version (ignored from git by default).

## Build Model (Important)

This project uses an incremental patch build, not a full clean compile of all decompiled classes.

The script:
- starts from a base jar (`tektopia-1.1.0.jar` by default)
- compiles selected Java files against local dependency jars
- overlays patched `.class` files into extracted jar contents
- overlays selected assets from `decompiled/assets/`
- updates version markers in key class/metadata entries
- repacks a distributable jar

Because this is decompiled code, full-project recompilation is not assumed to be stable.

## Prerequisites

- Windows + PowerShell
- JDK with `javac` and `jar` available
- local dependency jars in `tooling/libs/`:
- `forge-1.12.2-14.23.5.2860-universal.jar`
- `minecraft-client-1.12.2.jar`
- `minecraft-server-1.12.2.jar`
- `minecraft-client-1.12.2-srg.jar`
- `minecraft-server-1.12.2-srg.jar`
- `CraftStudioAPI-universal-1.0.1.95-mc1.12-alpha.jar`
- base mod jar at repo root (default: `tektopia-1.1.0.jar`)

## Build Commands

Example: build 1.1.2

```powershell
powershell -ExecutionPolicy Bypass -File .\build-release-1.1.1.ps1 `
  -BaseJar .\tektopia-1.1.0.jar `
  -Version 1.1.2 `
  -OutJar .\out\tektopia-1.1.2.jar
```

Example: build 1.1.3

```powershell
powershell -ExecutionPolicy Bypass -File .\build-release-1.1.1.ps1 `
  -BaseJar .\tektopia-1.1.0.jar `
  -Version 1.1.3 `
  -OutJar .\out\tektopia-1.1.3.jar
```

## Release Packaging Convention

Local release bundle structure:

```text
releases/
  1.1.X/
    tektopia-1.1.X.jar
    source/   (optional snapshot copy)
```

Note: `releases/` is intentionally git-ignored to keep the repository source-focused.

## Key Files for Ongoing Development

- `decompiled/net/tangotek/tektopia/entities/EntityGuard.java`
Guard combat/equipment logic and guard-driven golem behavior hooks.
- `decompiled/net/tangotek/tektopia/entities/EntityBlacksmith.java`
Crafting recipe and AI filter policy for smithing.
- `decompiled/net/tangotek/tektopia/GolemProbe.java`
Event-driven vanilla golem behavior logic.
- `decompiled/net/tangotek/tektopia/structures/VillageStructure.java`
Structure scanning and floor tile discovery (including vertical traversal support).
- `decompiled/assets/tektopia/craftstudio/models/entity/guard_m.csjsmodel`
Guard model node definitions and texture offsets.
- `decompiled/assets/tektopia/textures/entity/guard_m.png`
- `decompiled/assets/tektopia/textures/entity/guard_f.png`
Guard texture atlases.

## Runtime QoL Toggle Properties

System properties currently used:

- `tektopia.qol.craft.prioritize_need` (default `true`)
- `tektopia.qol.craft.enforce_personal_limit` (default `true`)

Accepted true values: `1`, `true`, `yes`, `on`.

## Known Limitations and Risks

- Decompiled-source projects can contain mapping/signature artifacts that prevent clean full recompiles.
- Build success depends on keeping `build-release-1.1.1.ps1` compile target list aligned with edited classes.
- Golem behavior changes have been iterated heavily and may still need gameplay tuning in live worlds.
- Guard gold-armor texture/model alignment is asset-sensitive and may require additional texture-offset iteration per atlas revision.

## Recommended Workflow for New Changes

1. Edit only the required classes/assets in `decompiled/`.
2. If a newly edited Java file must be compiled, add it to the `$javaSources` list in `build-release-1.1.1.ps1`.
3. Build to `out/tektopia-<version>.jar`.
4. Smoke-test in a Forge 1.12.2 instance with CraftStudio API dependency.
5. Keep commits scoped and descriptive (feature/fix focused).

## Disclaimer

This repository is a reverse-engineered maintenance workspace intended for compatibility and QoL patching. Upstream TekTopia ownership and original asset/code rights remain with their respective owners.
