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
- [~] Migrate all call sites from `SimpleNetworkWrapper` API
- [ ] Validate client/server packet sync for villager thought/AI filter paths

## Phase 4: Capability + Data Persistence

- [ ] Port capability definitions in `caps/*` to 1.16 capability APIs
- [ ] Migrate `LicenseTracker` attach/copy behavior
- [ ] Port structure token + village data capability interactions
- [ ] Verify save/load parity for key village/player capability data

## Phase 5: Entities + AI Foundations

- [ ] Port `EntityVillagerTek` base and shared navigator behaviors first
- [ ] Port top-priority professions (Guard, Blacksmith, Farmer) as vertical slices
- [ ] Port necromancer/minion combat path after guard stack is stable
- [ ] Revalidate custom AI filter toggles and recipe behaviors

## Phase 6: Village Structures + Generation

- [ ] Port village structure detection/runtime (`structures/*`, `VillageStructure.java`)
- [ ] Rebuild generation hooks from `generation/*` for 1.16 structure APIs
- [ ] Rewire Town Hall / Storage integration
- [ ] Regression-test multi-floor scan behavior (including slab/stair/ladder traversal)

## Phase 7: Client Rendering and Models

- [ ] Implement chosen replacement for CraftStudio-driven rendering/animation
- [ ] Port renderer layers and armor overlays (`client/*`)
- [ ] Revalidate guard armor visuals (iron/gold/diamond)
- [ ] Revalidate villager thought particles and UI overlays

## Phase 8: QA + Release Hardening

- [ ] Dedicated test matrix (new world, old world migration expectations, raid/combat, profession loops)
- [ ] Multiplayer sanity tests (packet sync, capability sync, entity AI consistency)
- [ ] Performance pass (pathing, village ticks, raids)
- [ ] First public 1.16.x alpha pre-release

## Immediate Next Sprint (recommended)

1. Lock Forge version to **36.2.42** and scaffold `port-1.16.5`.
2. Port only bootstrap + registries + networking shell (no AI yet).
3. Bring up one minimal custom entity end-to-end.
4. Make the CraftStudio replacement decision before broad client migration.
