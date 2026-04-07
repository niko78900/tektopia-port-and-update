# Porting Plan: TekTopia 1.12.2 -> 1.16.5 (Forge 36.2.x)

## Target Selection

Recommended target: **Minecraft 1.16.5 + Forge 36.2.x**.

Why this is the easiest practical 1.16 option:
- Last 1.16 patch, with mature ecosystem/docs and many migration examples.
- Still Java 8 era for mod development, reducing toolchain churn from current setup.
- Less migration shock than jumping directly from 1.12.2 to 1.18+ (worldgen/data systems are much larger rewrites there).

## Important External Constraint

`CraftStudio API` appears to have published support only up to Minecraft **1.12.2**.  
That means model/animation integration used by this codebase must be replaced or reimplemented for 1.16.5.

## Codebase Audit Snapshot (this repository)

- Java files in `decompiled/`: **269**
- Java files in `decompiled/net/tangotek/tektopia`: **257**
- Legacy obfuscated symbol usage (`func_*/field_*`) hits: **2392**
- Legacy Forge/FML API hotspot hits (networking, lifecycle events, etc.): **96**
- Client-only side annotation hits (`@SideOnly` / `Side.CLIENT`): **55**

Highest obfuscated/hotspot classes (top migration risk):
- `decompiled/net/tangotek/tektopia/entities/EntityVillagerTek.java`
- `decompiled/net/tangotek/tektopia/entities/EntityGuard.java`
- `decompiled/net/tangotek/tektopia/entities/EntityEnchanter.java`
- `decompiled/net/tangotek/tektopia/entities/EntityBlacksmith.java`
- `decompiled/net/tangotek/tektopia/entities/EntityNecromancer.java`
- `decompiled/net/tangotek/tektopia/structures/VillageStructure.java`
- `decompiled/net/tangotek/tektopia/generation/*`

## Major Migration Buckets

1. Mod bootstrap/lifecycle migration
- Replace 1.12 lifecycle (`@Mod.EventHandler`, preInit/init/postInit) with modern 1.16 setup events and mod bus registration.
- Current anchor file: `decompiled/net/tangotek/tektopia/TekVillager.java`

2. Registry migration
- Move block/item/entity/potion/sound registration to `DeferredRegister` pattern.
- Anchors: `ModBlocks`, `ModItems`, `ModEntities`, `ModPotions`, `ModSoundEvents`.

3. Networking migration
- Replace `SimpleNetworkWrapper`, `IMessage`, `IMessageHandler` with Forge 1.16 `SimpleChannel`.
- Anchors: `network/*`, `TekVillager.NETWORK`, `TekNetworkHelper`, `LicenseTracker`, `VillageManager`.

4. Capability migration
- Port old capability provider/storage style to 1.16 `LazyOptional` + modern capability access.
- Anchors: `caps/*`, `LicenseTracker`, `ItemStructureToken`, `Village`.

5. Entity/AI migration
- Rebase villager profession entities and custom AI tasks to 1.16 entity/goal APIs and updated attribute registration patterns.
- Highest-risk anchors: `entities/*`, `entities/ai/*`.

6. Worldgen/structure migration
- 1.12 `StructureVillagePieces` flow is not drop-in for 1.16.
- Requires redesign for 1.16 structure registration and village integration.
- Anchors: `generation/*`, hooks in `TekVillager`, structure classes in `structures/*`.

7. Rendering/model pipeline migration (critical blocker)
- Current client rendering relies on CraftStudio + custom animation plumbing.
- Since CraftStudio API is 1.12-bound, choose one:
  - reimplement runtime model/animation stack,
  - convert assets to another supported pipeline,
  - or temporarily ship fallback renderers while porting gameplay first.
- Anchors: `client/*`, `proxy/TekClientAnimationHandler.java`, `proxy/TekServerAnimationHandler.java`, `assets/tektopia/craftstudio/*`.

## Porting Strategy (recommended)

Use a two-track plan:
- **Track A (compile-first):** establish a clean Forge 1.16.5 project skeleton and port infrastructure (mod entry, registries, networking, capabilities) until it compiles and runs.
- **Track B (behavior-first):** reintroduce gameplay systems in risk order: village core -> professions -> combat/raids -> rendering polish.

This avoids getting blocked by rendering while gameplay architecture is still migrating.

## Definition of "Prepared"

This repository is considered "ready to start implementation" when:
- target version and loader are fixed (1.16.5 / Forge 36.2.x),
- migration backlog is broken into executable tasks with ownership,
- API risk zones are enumerated (done),
- external dependency blocker (CraftStudio) has a selected replacement path.

