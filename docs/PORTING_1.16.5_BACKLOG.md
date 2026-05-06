# Porting Backlog (Execution): 1.12.2 -> 1.16.5

Status legend:
- `[ ]` not started
- `[~]` in progress
- `[x]` done

## Phase 0: Baseline Decisions

- [x] Select target: Forge 1.16.5 (36.2.x)
- [x] Confirm release policy: current 1.1.x builds are pre-release bridge builds
- [ ] Decide CraftStudio replacement strategy (hard blocker for final client parity)

## Phase 1: New 1.16.5 Project Skeleton

- [x] Create clean Forge MDK 1.16.5 project in a separate branch/folder (`port-1.16.5`)
- [x] Set mod id/name/version in modern `mods.toml`
- [x] Wire minimal mod entrypoint and verify client boots with empty mod
- [x] Establish package layout for migrated sources

## Phase 2: Core Infrastructure Port

- [x] Port mod bootstrap from `TekVillager` to modern event-bus setup
- [x] Port registries (`ModBlocks`, `ModItems`, `ModEntities`, `ModPotions`, `ModSoundEvents`) to `DeferredRegister`
- [x] Replace old proxies/common init flow with sided-safe 1.16 patterns
- [~] Port gamerule and server-start command registration flow

## Phase 3: Networking Port

- [x] Introduce `SimpleChannel` with protocol versioning
- [x] Port packet registrations and handlers from `network/*`
- [~] Migrate all call sites from `SimpleNetworkWrapper` API (core send helpers now added to `TekNetwork`)
- [~] Validate client/server packet sync for villager thought/AI filter paths (client thought/item particles and server village snapshots now wired; multiplayer QA still required)

## Phase 4: Capability + Data Persistence

- [x] Port capability definitions in `caps/*` to 1.16 capability APIs
- [x] Migrate `LicenseTracker` attach/copy behavior
- [x] Port structure token + village data capability interactions
- [x] Verify save/load parity for key village/player capability data (village + structure runtime + alert state)

## Phase 5: Entities + AI Foundations

- [x] Establish first custom-entity vertical slice on 1.16.5 (`tek_guard` registry + attributes + spawn command)
- [~] Port `EntityVillagerTek` base and shared behavior surfaces (AI filters, hostile targeting predicates, persistence)
- [x] Port top-priority profession vertical slice: Guard equipment filter policy + target goals + command controls
- [x] Port Blacksmith and Farmer behavior slices
- [~] Port necromancer/minion combat path after guard stack is stable (bounded minions, village targeting, stale cleanup now in alpha.3; runtime combat QA still required)
- [~] Revalidate custom AI filter toggles and recipe behaviors (GUI and packet sync path now exists; multiplayer QA still required)

## Phase 6: Village Structures + Generation

- [x] Port village structure detection/runtime foundation (`TekVillageStructure` floor scan + vertical traversal over stairs/slabs/ladders/vines)
- [x] Add first concrete structure scaffolds (`Town Hall`, `Storage`) with debug scan command paths
- [x] Add frame/token discovery path from nearby item frames to structure scans (`discover_structures`)
- [x] Rewire Town Hall / Storage integration through shared runtime cache + periodic server discovery ticks
- [~] Rebuild generation hooks from `generation/*` for 1.16 structure APIs (starter `worldgen_test` builder creates scan-compatible Town Hall, Storage, Home, Farm, and Mineshaft; natural biome placement still pending)
- [~] Regression-test multi-floor scan behavior (including slab/stair/ladder traversal)

## Playable Alpha Checkpoint (Current)

- [x] Mod loads and compiles on Forge `1.16.5-36.2.42` in isolated port workspace
- [x] Guard entity has registry, attributes, client renderer, and spawn egg path
- [x] Structure token items (`Town Hall`/`Storage`) exist for frame-based discovery tests
- [x] Runtime command kit exists for practical playtesting (`starter_kit`, `discover_structures`, `nearest_structure`, `worker_status`, `economy_status`, `guard_status`)
- [x] Farmer core loop is functional (harvest/replant/deliver with retry/backoff)
- [x] Blacksmith demand-driven armor crafting is functional (iron/gold, optional diamond by stock/policy)
- [x] Guard storage armory upgrades are functional and deterministic
- [x] Village alert memory + guard rally + civilian retreat/recovery are functional
- [x] Runtime persistence captures villages, structure assignments, and alert memory across reload
- [x] Expanded 50% gameplay foundation is in place: Miner, Lumberjack, Chef, Rancher, Butcher, Merchant, Nomad, and Necromancer entity/command/runtime slices now compile
- [~] Expanded 70% alpha surface is in place: Biped-first GUI snapshot, AI filter packet round trip, performance counters, staggered worker scans, starter worldgen test structures, and lightweight cleric/teacher/enchanter/druid/bard/architect/child/nitwit roles now compile
- [~] Replace temporary renderer/model placeholders with Biped-first readable client assets/armor layers; final CraftStudio-equivalent animation remains deferred

## 50% Foundation Additions

- Added a measurable progress rubric in `docs/PORTING_1.16.5_PROGRESS_RUBRIC.md`.
- Added placeholder-readable entity registrations, spawn eggs, language keys, and renderers for the next core profession set.
- Added frame/token structure types for Home, Farm, Mineshaft, Lumber Area, Kitchen, Butcher, Ranch Pen, Guard Post, Barracks, and Merchant Stall.
- Added first-pass runtime slices for mining, lumber, cooking, ranch goods, butchering, merchant trades, nomad gifts, guard idle posts, and necromancer/minion threats.
- Added playtest commands for generic worker spawning, workforce status, expanded starter kits, and necromancer raid tests.
- Added source hygiene build guard, real client-side thought/item/village/pathing packet state, periodic village snapshots, deterministic miner/lumberjack storage drops, and alpha QA checklist.

## Phase 7: Client Rendering and Models

- [ ] Implement chosen replacement for CraftStudio-driven rendering/animation
- [x] Port renderer layers and armor overlays (`client/*`) for Biped-first alpha path
- [~] Revalidate guard armor visuals (iron/gold/diamond)
- [~] Revalidate villager thought particles and UI overlays

## Phase 8: QA + Release Hardening

- [~] Dedicated test matrix (new world, old world migration expectations, raid/combat, profession loops)
- [ ] Multiplayer sanity tests (packet sync, capability sync, entity AI consistency)
- [~] Performance pass (pathing, village ticks, raids)
- [~] First public 1.16.x alpha pre-release (`1.16.5-alpha.3` target)

## Immediate Next Sprint (recommended)

1. Run structured gameplay QA on `1.16.5-alpha.3` (20+ minute village loops plus repeated reload tests).
2. Finish packet/cap sync validation for GUI filter toggles, villager snapshots, and village snapshots in multiplayer.
3. Validate starter structure/worldgen-test placement and convert it into natural biome placement hooks.
4. Decide and execute the CraftStudio replacement path for production-ready visuals.
