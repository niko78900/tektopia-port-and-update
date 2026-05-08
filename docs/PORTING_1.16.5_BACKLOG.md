# Porting Backlog (Execution): 1.12.2 -> 1.16.5

Status legend:
- `[ ]` not started
- `[~]` in progress
- `[x]` done

## Phase 0: Baseline Decisions

- [x] Select target: Forge 1.16.5 (36.2.x)
- [x] Confirm release policy: current 1.1.x builds are pre-release bridge builds
- [~] Decide CraftStudio replacement strategy (internal compatibility runtime selected and started; full pose parity still pending)

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
- [~] Migrate all call sites from `SimpleNetworkWrapper` API (core village, villager thought/status, item thought, AI filter, and path debug send helpers now added)
- [~] Validate client/server packet sync for villager thought/AI filter paths (server-authoritative cache exists; multiplayer playtest still needed)

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
- [~] Port necromancer/minion combat path after guard stack is stable (scheduler, summons, skulls, clouds, guard scoring, and raid commands exist; balance still needs QA)
- [~] Revalidate custom AI filter toggles and recipe behaviors

## Phase 6: Village Structures + Generation

- [x] Port village structure detection/runtime foundation (`TekVillageStructure` floor scan + vertical traversal over stairs/slabs/ladders/vines)
- [x] Add first concrete structure scaffolds (`Town Hall`, `Storage`) with debug scan command paths
- [x] Add frame/token discovery path from nearby item frames to structure scans (`discover_structures`)
- [x] Rewire Town Hall / Storage integration through shared runtime cache + periodic server discovery ticks
- [~] Rebuild generation hooks from `generation/*` for 1.16 structure APIs (controlled starter generator and starter worldgen status/duplicate guards exist; true biome feature placement still pending)
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
- [x] Server-side village sync now sends village state, structure validity, villager thought/status, item thoughts, AI filters, alerts, and path debug data into a lightweight client cache
- [x] Storage reservations persist through `TekVillageSavedData` with migration-safe defaults
- [x] Shared worker navigation recovery is used by farmer, blacksmith, miner, lumberjack, chef, rancher, butcher, merchant, and nomad paths
- [x] Animal pen loops manage sheep/cow/pig/chicken pens with capacity checks, villager-owned animal tags, breeding feed consumption, wool/egg/milk collection, and butcher surplus protection
- [x] Production workers have deeper loops: farmers till/plant/harvest, lumberjacks harvest connected logs and replant when saplings drop, miners generate useful ore outputs, and cooks/smiths/enchanters use reserved inputs
- [x] Raid QA commands and guard reliability pass are in place: raid status/clear, reservation status, pen status, QA status/start, combat re-equipping, captain aura, and threat-priority scoring
- [~] Replace temporary renderer/model placeholders with final client assets/animation pipeline

## 70% Alpha Sweep Additions

- Added persisted logistics state for active storage reservations.
- Added server-authoritative packet payloads and a client-side cache for village/structure/villager/path state.
- Added shared worker path recovery with retry, stuck detection, cooldown, and debug path sync.
- Added animal pen management for sheep, cows, pigs, and chickens plus rancher/butcher behavior tied to real animals.
- Deepened farmer, lumberjack, miner, rancher, butcher, blacksmith, chef, and enchanter loops with more complete item flow and blocked-state reporting.
- Improved raids with active raid debug commands, clear/status tools, necromancer summon caps/scaling, combat re-equipping, and guard threat scoring.
- Current honest status after this sweep: approximately 68-72% of player-facing gameplay parity, assuming compile/build validation passes. Remaining uncertainty is mostly multiplayer QA, GUI polish, worldgen, and final visuals.

## 80% Alpha Candidate Additions

- Added a strict 70% validation checklist covering clean build, 30-minute singleplayer soak, save/reload with active state, and two-player multiplayer sanity.
- Deepened Architect/Tradesman/Merchant behavior with village-aware token prices, price tier escalation, visible purchase feedback, merchant sale history, and additional village-goods sales.
- Deepened Teacher/Child and social utility loops with school/social schedules, book-assisted teaching, hunger costs, persistent thoughts/status, and child-to-nitwit aging.
- Added structure comfort penalties for missing homes/beds, invalid structures, home-capacity shortages, and overcrowded homes.
- Added a functional client status screen and HUD overlay backed by the packet cache for village, trade, structures, thoughts, AI filters, alerts, and blocked path/debug state.
- Added village tick timing counters exposed through `qa_status` and global reservation expiry/debug output for stuck-reservation QA.
- Current honest status after this sweep: approximately 74-80% implementation parity. It is an 80% alpha candidate, not a runtime-certified 80%, until the 30-minute soak and multiplayer sanity gates pass.

## 95% Candidate Sweep Additions

- Added `/tektopia_port parity_report` plus a 95% RC QA checklist for the singleplayer soak and multiplayer sanity gates.
- Deepened villager core parity with baseline food values, recent-food happiness variety, villager-food preference, starvation damage, death sadness, village death counts, and Nitwit intelligence-to-skill conversion.
- Hardened economy lineage so villager-item outputs require villager-made inputs, and expanded Merchant sales across more baseline village goods.
- Tightened structure validation for Home 2/4/6 bed counts, school chairs, pen gates, and mineshaft openings; added missing item models for newer tokens and spawn eggs.
- Deepened social/special behavior for Bard, Cleric, Druid, Enchanter, Child, and Nitwit loops.
- Improved Necromancer parity with spirit-skull damage shielding, minion targeting/tagging, support cleanup on death, emerald drops, and villager-death ability suppression.
- Current honest status after this sweep: approximately 82-88% implementation parity. It is not a validated 95% release candidate until the 30-minute soak, reload, and two-player multiplayer gates pass.

## Full-Parity Push Additions

- Recovered legacy Guard visual assets into the 1.16.5 resource tree: `guard_m.csjsmodel`, `guard_m.png`, and `guard_f.png`.
- Added `assets/tektopia/visual_coverage.json` as the tracked visual-gap manifest for every registered Tek entity.
- Added a client-only CraftStudio-style model loader, cube hierarchy representation, Biped-backed renderer foundation, visual coverage boot log, and Guard renderer wiring with armor-layer fallback.
- Added `/tektopia_port asset_inventory` for server-safe visual coverage reporting.
- Added `/tektopia_port sync_status` and bumped the network protocol to `2` when the GUI snapshot packet was introduced.
- Added `/tektopia_port gui_snapshot`, `PacketVillagerGuiSnapshot`, client cache support, status-screen display, and right-click villager snapshot inspection.
- Added `/tektopia_port worldgen_test <townhall|storage|home|farm|mineshaft>` plus controlled starter structure generation and scanner-cache validation.
- Current honest status after this push: approximately 85-90% implementation parity. It is not a validated 90-95% release candidate until client visual boot, 30-minute soak/reload, and two-client multiplayer gates pass.

## 100% Implementation Push Additions

- Added `docs/PORTING_1.16.5_100_RC_QA.md` as the hard gate for any future 100% release claim.
- Added `/tektopia_port asset_inventory strict`; strict mode fails when any required visual model/texture is missing.
- Added `/tektopia_port perf_status` and `/tektopia_port worldgen_status`.
- Added controlled starter generation coverage for School, Tavern, and Library in addition to Town Hall, Storage, Home, Farm, and Mineshaft.
- Added `TekWorldgenSavedData` and starter duplicate-prevention status.
- Added `TekContainers`, a container-backed villager GUI, main/inventory tab, AI filter tab, and right-click GUI opening.
- Extended `PacketVillagerGuiSnapshot` to carry villager inventory stacks and AI filter state; bumped the network protocol to `4` after GUI/trade packet changes.
- Added `PacketTradeAction` and server-authoritative architect/tradesman token purchases from the GUI.
- Added direct reconnect/dimension-change resync for village snapshots, villager state, GUI snapshots, and AI filters.
- Upgraded the CraftStudio model path from metadata-only to renderable cube parts with deterministic internal walk/idle/head pose clips.
- Recreated model/texture asset coverage for every registered entity using the recovered humanoid skeleton as the in-port replacement baseline; strict coverage now has no required missing model entries.
- Added an explicit `TekVillageSavedData.DATA_VERSION` migration gate for pre-100 saved runtime data.
- Build gates passed after this push: `clean compileJava --rerun-tasks`, `build`, and `releaseJar -Ptektopia_enable_reobf=true`.
- Current honest status after this push: approximately 90-93% implementation parity. It is not a validated 100% release until live client visual boot, natural worldgen validation, 30-minute singleplayer soak/reload, two-client multiplayer, and 1/3/5-village performance gates pass.

## Final 91% -> 100% Push Additions

- Promoted the villager GUI into the current player-facing inspection hub with Main, AI Filters, Village, Storage, and Trade tabs.
- Added `PacketVillageGuiSnapshot`, `PacketStorageGuiSnapshot`, and `PacketTradeGuiSnapshot`; bumped `TekNetwork` protocol to `5`.
- Re-sends village, storage, and trade GUI snapshots with villager GUI snapshots and nearest-village reconnect sync.
- Polished recreated entity textures with profession-readable tint/detail variants so strict visual coverage is more usable during spawn-every-entity QA.
- Bumped `TekWorldgenSavedData` to version `2` and persisted starter-generation attempts, failure/backoff state, duplicate-prevention hits, last attempt position, dimension, and timing.
- Expanded `/tektopia_port worldgen_status` so QA can inspect generation defaults, failure retry, duplicate guards, and saved starter state.
- Current honest status after this final implementation pass: approximately 93-95% implementation parity. It is still not a validated 100% release until live client boot, spawn-every-entity visual QA, natural starter-generation validation in multiple worlds, 30-minute soak/reload, two-client multiplayer, and 1/3/5-village performance gates pass.

## 50% Foundation Additions

- Added a measurable progress rubric in `docs/PORTING_1.16.5_PROGRESS_RUBRIC.md`.
- Added placeholder-readable entity registrations, spawn eggs, language keys, and renderers for the next core profession set.
- Added frame/token structure types for Home, Farm, Mineshaft, Lumber Area, Kitchen, Butcher, Ranch Pen, Guard Post, Barracks, and Merchant Stall.
- Added first-pass runtime slices for mining, lumber, cooking, ranch goods, butchering, merchant trades, nomad gifts, guard idle posts, and necromancer/minion threats.
- Added playtest commands for generic worker spawning, workforce status, expanded starter kits, and necromancer raid tests.

## Phase 7: Client Rendering and Models

- [~] Implement chosen replacement for CraftStudio-driven rendering/animation
- [~] Port renderer layers and armor overlays (`client/*`)
- [~] Revalidate guard armor visuals (iron/gold/diamond)
- [~] Revalidate villager thought particles and UI overlays

## Phase 8: QA + Release Hardening

- [~] Dedicated test matrix (new world, old world migration expectations, raid/combat, profession loops)
- [ ] Multiplayer sanity tests (packet sync, capability sync, entity AI consistency)
- [ ] Performance pass (pathing, village ticks, raids)
- [~] First public 1.16.x alpha pre-release (`1.16.5-alpha.2` target)

## Immediate Next Sprint (recommended)

1. Run the updated 95% RC checklist in `docs/PORTING_1.16.5_95_RC_QA.md`.
2. Run the 100% hard gate in `docs/PORTING_1.16.5_100_RC_QA.md`.
3. Boot a client and validate `/tektopia_port asset_inventory strict`, CraftStudio renderer loading, every spawn egg/renderer, Guard armor/captain visuals, and no missing-model crash.
4. Fix soak-test and two-player multiplayer issues found by `parity_report`, `sync_status`, GUI snapshots, packet sync, worker reservations, AI filter authority, GUI trade actions, and raid alerts.
5. Run the villager GUI Village/Storage/Trade tabs against a real village, then decide whether separate standalone village/storage/trade containers are worth adding before release.
6. Replace recreated non-Guard visuals with final-quality assets where visual QA shows readability or quality issues.
