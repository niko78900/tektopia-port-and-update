package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.common.TekItemMeta;
import net.tangotek.tektopia.entities.TekArchitectEntity;
import net.tangotek.tektopia.entities.TekBardEntity;
import net.tangotek.tektopia.entities.TekBlacksmithEntity;
import net.tangotek.tektopia.entities.TekButcherEntity;
import net.tangotek.tektopia.entities.TekCaptainAuraEntity;
import net.tangotek.tektopia.entities.TekChefEntity;
import net.tangotek.tektopia.entities.TekChildEntity;
import net.tangotek.tektopia.entities.TekClericEntity;
import net.tangotek.tektopia.entities.TekDeathCloudEntity;
import net.tangotek.tektopia.entities.TekDruidEntity;
import net.tangotek.tektopia.entities.TekEnchanterEntity;
import net.tangotek.tektopia.entities.TekFarmerEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekLumberjackEntity;
import net.tangotek.tektopia.entities.TekMerchantEntity;
import net.tangotek.tektopia.entities.TekMinerEntity;
import net.tangotek.tektopia.entities.TekNecromancerEntity;
import net.tangotek.tektopia.entities.TekNitwitEntity;
import net.tangotek.tektopia.entities.TekNomadEntity;
import net.tangotek.tektopia.entities.TekRancherEntity;
import net.tangotek.tektopia.entities.TekSpiritSkullEntity;
import net.tangotek.tektopia.entities.TekTeacherEntity;
import net.tangotek.tektopia.entities.TekTradesmanEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.common.TekGameRules;
import net.tangotek.tektopia.common.TekWorkerStatus;
import net.tangotek.tektopia.registry.TekEntities;
import net.tangotek.tektopia.structures.TekStructureStorage;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;

public class TekVillageManager {
    private static final double GUARD_RETREAT_HEALTH_RATIO = 0.35D;
    private static final String FARMER_TARGET_POS_TAG = "tek_farmer_work_target";
    private static final String FARMER_COOLDOWN_TAG = "tek_farmer_work_cooldown";
    private static final String FARMER_CARRY_TAG = "tek_farmer_carry";
    private static final String FARMER_STUCK_TICKS_TAG = "tek_farmer_stuck_ticks";
    private static final String FARMER_LAST_POS_TAG = "tek_farmer_last_pos";
    private static final String FARMER_MODE_TAG = "tek_farmer_mode";
    private static final int FARMER_MODE_HARVEST = 0;
    private static final int FARMER_MODE_DELIVER = 1;
    private static final String BLACKSMITH_COOLDOWN_TAG = "tek_blacksmith_work_cooldown";
    private static final String BLACKSMITH_DEMAND_TAG = "tek_blacksmith_demand";
    private static final String BLACKSMITH_MISSING_TAG = "tek_blacksmith_missing";
    private static final String BLACKSMITH_PLAN_TAG = "tek_blacksmith_plan";
    public static final String WORKER_COOLDOWN_TAG = "tek_worker_cooldown";
    public static final String WORKER_MODE_TAG = "tek_worker_mode";
    public static final String WORKER_TARGET_TAG = "tek_worker_target";
    public static final String WORKER_LAST_RESULT_TAG = "tek_worker_last_result";
    private static final int FARMER_SCAN_RADIUS = 12;
    private static final int WORKER_SCAN_RADIUS = 14;
    private static final long FARMER_RETRY_COOLDOWN = 40L;
    private static final long FARMER_WORK_COOLDOWN = 20L;
    private static final long FARMER_PATH_STEP_COOLDOWN = 10L;
    private static final int FARMER_STUCK_LIMIT = 80;
    private static final long BLACKSMITH_RETRY_COOLDOWN = 60L;
    private static final long BLACKSMITH_WORK_COOLDOWN = 100L;
    private static final long WORKER_RETRY_COOLDOWN = 80L;
    private static final long WORKER_PATH_STEP_COOLDOWN = 20L;
    private static final long WORKER_WORK_COOLDOWN = 120L;
    private static final long MERCHANT_WORK_COOLDOWN = 1200L;
    private static final long NOMAD_WORK_COOLDOWN = 2400L;
    private static final long SOCIAL_WORK_COOLDOWN = 600L;
    private static final long VENDOR_CHECK_INTERVAL = 600L;
    private static final long MERCHANT_VISIT_INTERVAL = 72000L;
    private static final long NOMAD_VISIT_INTERVAL = 36000L;
    private static final long RAID_CHECK_INTERVAL = 96000L;
    private static final long SCHOOL_START_TIME = 4000L;
    private static final long SCHOOL_END_TIME = 11000L;
    private static final long SOCIAL_START_TIME = 12000L;
    private static final long SOCIAL_END_TIME = 16000L;
    private static final int CHILD_ADULT_DAYS = 6;
    private static final long GUARD_ARMORY_TICK_INTERVAL = 40L;
    private static final long CAPTAIN_AURA_INTERVAL = 160L;
    private static final long ALERT_MEMORY_TICKS = 200L;
    private static final ArmorRecipe[] BLACKSMITH_ARMOR_RECIPES = new ArmorRecipe[] {
            new ArmorRecipe(Items.IRON_CHESTPLATE, Items.IRON_INGOT, 8),
            new ArmorRecipe(Items.IRON_LEGGINGS, Items.IRON_INGOT, 7),
            new ArmorRecipe(Items.IRON_HELMET, Items.IRON_INGOT, 5),
            new ArmorRecipe(Items.IRON_BOOTS, Items.IRON_INGOT, 4),
            new ArmorRecipe(Items.GOLDEN_CHESTPLATE, Items.GOLD_INGOT, 8),
            new ArmorRecipe(Items.GOLDEN_LEGGINGS, Items.GOLD_INGOT, 7),
            new ArmorRecipe(Items.GOLDEN_HELMET, Items.GOLD_INGOT, 5),
            new ArmorRecipe(Items.GOLDEN_BOOTS, Items.GOLD_INGOT, 4),
            new ArmorRecipe(Items.DIAMOND_CHESTPLATE, Items.DIAMOND, 8),
            new ArmorRecipe(Items.DIAMOND_LEGGINGS, Items.DIAMOND, 7),
            new ArmorRecipe(Items.DIAMOND_HELMET, Items.DIAMOND, 5),
            new ArmorRecipe(Items.DIAMOND_BOOTS, Items.DIAMOND, 4)
    };
    private static final Map<Item, TekVillageEconomy.ArmorClass> ARMOR_CLASSIFIER = buildArmorClassifier();

    private final Map<UUID, TekVillage> villages = new LinkedHashMap<>();

    public TekVillage createVillage(BlockPos center, int radius, long gameTime) {
        TekVillage village = new TekVillage(center, radius, gameTime);
        this.villages.put(village.getId(), village);
        return village;
    }

    public TekVillage upsertNearestVillage(BlockPos center, int radius, long gameTime) {
        TekVillage village = this.findNearestVillage(center).orElse(null);
        if (village == null || village.getCenter().distSqr(center) > (double) radius * (double) radius * 4.0D) {
            return this.createVillage(center, radius, gameTime);
        }
        village.updateGeometry(center, radius);
        return village;
    }

    public boolean removeVillage(UUID villageId) {
        return this.villages.remove(villageId) != null;
    }

    public void clear() {
        this.villages.clear();
    }

    public int size() {
        return this.villages.size();
    }

    public Collection<TekVillage> getVillages() {
        return Collections.unmodifiableCollection(this.villages.values());
    }

    public Optional<TekVillage> findNearestVillage(BlockPos pos) {
        return this.villages.values().stream()
                .min(Comparator.comparingDouble(v -> v.getCenter().distSqr(pos)));
    }

    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT villagesTag = new ListNBT();
        for (TekVillage village : this.villages.values()) {
            villagesTag.add(village.save());
        }
        nbt.put("villages", villagesTag);
        return nbt;
    }

    public void load(CompoundNBT nbt) {
        this.villages.clear();
        ListNBT villagesTag = nbt.getList("villages", 10);
        for (int i = 0; i < villagesTag.size(); i++) {
            CompoundNBT villageTag = villagesTag.getCompound(i);
            TekVillage village = TekVillage.load(villageTag);
            this.villages.put(village.getId(), village);
        }
    }

    public void tick(ServerWorld level, TekVillageStructureManager structureManager) {
        for (TekVillage village : this.villages.values()) {
            AxisAlignedBB engagementBounds = village.getBounds().inflate(12.0D, 4.0D, 12.0D);
            List<TekVillagerEntity> villagers = level.getEntitiesOfClass(
                    TekVillagerEntity.class,
                    engagementBounds,
                    villager -> villager != null && villager.isAlive()
            );
            this.syncVillageResidents(village, villagers);
            this.syncProfessionCounts(village, villagers);
            this.tickVillageDailySystems(level, village, structureManager, villagers);

            List<MonsterEntity> hostiles = level.getEntitiesOfClass(
                    MonsterEntity.class,
                    engagementBounds,
                    this::isVillageHostile
            );
            village.setLastKnownHostileCount(hostiles.size());
            if (hostiles.isEmpty() && village.isRaidActive()) {
                village.setRaidActive(false);
            }
            BlockPos retreatPos = this.resolveRetreatPos(structureManager).orElse(village.getCenter());
            BlockPos alertPos = village.getLastAlertPos() != null ? village.getLastAlertPos() : village.getCenter();

            List<TekGuardEntity> guards = level.getEntitiesOfClass(
                    TekGuardEntity.class,
                    engagementBounds,
                    guard -> guard != null && guard.isAlive()
            );
            this.ensureCaptainGuard(structureManager, guards);

            if (hostiles.isEmpty()) {
                boolean alertActive = village.hasActiveAlert(level.getGameTime(), ALERT_MEMORY_TICKS);
                if (alertActive) {
                    for (TekGuardEntity guard : guards) {
                        if (guard.distanceToSqr(
                                alertPos.getX() + 0.5D,
                                alertPos.getY(),
                                alertPos.getZ() + 0.5D
                        ) > 4.0D) {
                            guard.setWorkerStatus(TekWorkerStatus.MOVING);
                            guard.getNavigation().moveTo(
                                    alertPos.getX() + 0.5D,
                                    alertPos.getY(),
                                    alertPos.getZ() + 0.5D,
                                    1.15D
                            );
                        } else {
                            guard.setWorkerStatus(TekWorkerStatus.IDLE);
                        }
                    }
                    for (TekVillagerEntity villager : villagers) {
                        if (villager instanceof TekGuardEntity) {
                            continue;
                        }
                        villager.getNavigation().moveTo(
                                retreatPos.getX() + 0.5D,
                                retreatPos.getY(),
                                retreatPos.getZ() + 0.5D,
                                1.08D
                        );
                    }
                } else if (level.isDay()) {
                    village.clearAlert();
                    for (TekGuardEntity guard : guards) {
                        BlockPos guardIdlePos = this.resolveGuardIdlePos(structureManager).orElse(retreatPos);
                        if (guard.distanceToSqr(
                                guardIdlePos.getX() + 0.5D,
                                guardIdlePos.getY(),
                                guardIdlePos.getZ() + 0.5D
                        ) > 16.0D) {
                            guard.setWorkerStatus(TekWorkerStatus.MOVING);
                            guard.getNavigation().moveTo(
                                    guardIdlePos.getX() + 0.5D,
                                    guardIdlePos.getY(),
                                    guardIdlePos.getZ() + 0.5D,
                                    1.05D
                            );
                        } else {
                            guard.setWorkerStatus(TekWorkerStatus.IDLE);
                        }
                    }
                    if (level.getGameTime() % GUARD_ARMORY_TICK_INTERVAL == 0L) {
                        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
                        if (storage != null) {
                            TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
                            if (!economy.getChests().isEmpty()) {
                                this.tickGuardArmory(guards, economy);
                            }
                        }
                    }
                    this.tickCivilianWork(level, village, structureManager, villagers);
                } else {
                    village.clearAlert();
                    for (TekVillagerEntity villager : villagers) {
                        if (villager instanceof TekGuardEntity) {
                            continue;
                        }
                        if (villager.distanceToSqr(
                                retreatPos.getX() + 0.5D,
                                retreatPos.getY(),
                                retreatPos.getZ() + 0.5D
                        ) > 9.0D) {
                            villager.getNavigation().moveTo(
                                    retreatPos.getX() + 0.5D,
                                    retreatPos.getY(),
                                    retreatPos.getZ() + 0.5D,
                                    1.0D
                            );
                        }
                    }
                }
                continue;
            }
            MonsterEntity nearestToCenter = hostiles.stream()
                    .min(Comparator.comparingDouble(h -> h.distanceToSqr(village.getCenter().getX(), village.getCenter().getY(), village.getCenter().getZ())))
                    .orElse(hostiles.get(0));
            village.setAlert(nearestToCenter.blockPosition(), level.getGameTime());
            if (level.getGameTime() % GUARD_ARMORY_TICK_INTERVAL == 0L) {
                TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
                if (storage != null) {
                    TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
                    if (!economy.getChests().isEmpty()) {
                        this.tickGuardArmory(guards, economy);
                    }
                }
            }

            for (TekGuardEntity guard : guards) {
                if (this.isLowHealth(guard)) {
                    guard.setTarget(null);
                    guard.setWorkerStatus(TekWorkerStatus.RESTING);
                    guard.getNavigation().moveTo(
                            retreatPos.getX() + 0.5D,
                            retreatPos.getY(),
                            retreatPos.getZ() + 0.5D,
                            1.20D
                    );
                    continue;
                }
                LivingEntity nearestHostile = hostiles.stream()
                        .max(Comparator.comparingDouble(h -> this.scoreThreatForGuard(h, guard)))
                        .orElse(null);
                if (nearestHostile == null) {
                    continue;
                }
                guard.setTarget(nearestHostile);
                guard.setWorkerStatus(TekWorkerStatus.COMBAT);
                guard.getNavigation().moveTo(nearestHostile, 1.15D);
            }
            this.tickCaptainAura(level, guards);

            for (TekVillagerEntity villager : villagers) {
                if (villager instanceof TekGuardEntity) {
                    continue;
                }
                villager.setTarget(null);
                villager.getNavigation().moveTo(
                        retreatPos.getX() + 0.5D,
                        retreatPos.getY(),
                        retreatPos.getZ() + 0.5D,
                        1.08D
                );
            }
        }
    }

    private void tickCivilianWork(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            List<TekVillagerEntity> villagers
    ) {
        for (TekVillagerEntity villager : villagers) {
            if (villager instanceof TekGuardEntity) {
                continue;
            }
            if (villager instanceof TekFarmerEntity) {
                this.tickFarmerWork(level, village, structureManager, (TekFarmerEntity) villager);
                continue;
            }
            if (villager instanceof TekBlacksmithEntity) {
                this.tickBlacksmithWork(level, village, structureManager, (TekBlacksmithEntity) villager);
                continue;
            }
            if (villager instanceof TekMinerEntity) {
                this.tickBlockGatherer(level, village, structureManager, villager, TekStructureType.MINESHAFT, "miner", this::isMinerTarget);
                continue;
            }
            if (villager instanceof TekLumberjackEntity) {
                this.tickBlockGatherer(level, village, structureManager, villager, TekStructureType.LUMBER_AREA, "lumberjack", this::isLumberTarget);
                continue;
            }
            if (villager instanceof TekChefEntity) {
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.KITCHEN, "chef", WORKER_WORK_COOLDOWN, this::tryChefRecipe);
                continue;
            }
            if (villager instanceof TekRancherEntity) {
                this.tickRancherWork(level, village, structureManager, (TekRancherEntity) villager);
                continue;
            }
            if (villager instanceof TekButcherEntity) {
                this.tickButcherWork(level, village, structureManager, (TekButcherEntity) villager);
                continue;
            }
            if (villager instanceof TekMerchantEntity) {
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.MERCHANT_STALL, "merchant", MERCHANT_WORK_COOLDOWN, this::tryMerchantRecipe);
                continue;
            }
            if (villager instanceof TekNomadEntity) {
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.TOWNHALL, "nomad", NOMAD_WORK_COOLDOWN, this::tryNomadRecipe);
                continue;
            }
            if (villager instanceof TekTeacherEntity) {
                this.tickTeacherWork(level, village, structureManager, (TekTeacherEntity) villager);
                continue;
            }
            if (villager instanceof TekBardEntity) {
                this.tickBardWork(level, village, structureManager, (TekBardEntity) villager);
                continue;
            }
            if (villager instanceof TekClericEntity) {
                this.tickClericWork(level, village, (TekClericEntity) villager, villagers);
                continue;
            }
            if (villager instanceof TekDruidEntity) {
                this.tickDruidWork(level, village, structureManager, (TekDruidEntity) villager);
                continue;
            }
            if (villager instanceof TekEnchanterEntity) {
                this.tickEnchanterWork(level, village, structureManager, (TekEnchanterEntity) villager);
                continue;
            }
            if (villager instanceof TekNitwitEntity) {
                this.tickNitwitWork(level, village, (TekNitwitEntity) villager);
                continue;
            }
            if (villager instanceof TekArchitectEntity || villager instanceof TekTradesmanEntity) {
                this.tickTownHallVendor(level, structureManager, villager);
                continue;
            }
            if (villager instanceof TekChildEntity) {
                this.tickChildWork(level, village, structureManager, (TekChildEntity) villager);
            }
        }
    }

    private void tickVillageDailySystems(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            List<TekVillagerEntity> villagers
    ) {
        long gameTime = level.getGameTime();
        if (village.getNextMerchantTick() < 0L) {
            village.setNextMerchantTick(gameTime + MERCHANT_VISIT_INTERVAL / 2L);
        }
        if (village.getNextNomadTick() < 0L) {
            village.setNextNomadTick(gameTime + NOMAD_VISIT_INTERVAL / 2L);
        }
        if (village.getNextRaidTick() < 0L) {
            village.setNextRaidTick(gameTime + RAID_CHECK_INTERVAL);
        }
        if (gameTime - village.getLastDailyTick() >= 24000L) {
            village.setLastDailyTick(gameTime);
            if (village.getResidents().size() > 0) {
                village.setRaidLevel(Math.max(village.getRaidLevel(), village.getResidents().size() / 4));
            }
        }
        if (gameTime % VENDOR_CHECK_INTERVAL == 0L) {
            this.ensureTownHallVendors(level, structureManager);
        }
        if (gameTime >= village.getNextMerchantTick()) {
            if (this.spawnMerchantVisitor(level, village, structureManager)) {
                village.recordVisitorSpawn();
            }
            village.setNextMerchantTick(gameTime + MERCHANT_VISIT_INTERVAL);
        }
        if (gameTime >= village.getNextNomadTick()) {
            if (this.spawnNomadVisitor(level, village, structureManager, villagers)) {
                village.recordVisitorSpawn();
            }
            village.setNextNomadTick(gameTime + NOMAD_VISIT_INTERVAL);
        }
        if (gameTime >= village.getNextRaidTick()) {
            if (villagers.size() >= 3 && this.spawnNecromancerRaid(level, village, structureManager)) {
                village.setRaidActive(true);
                village.setRaidLevel(village.getRaidLevel() + 1);
            }
            village.setNextRaidTick(gameTime + RAID_CHECK_INTERVAL);
        }
    }

    private void ensureTownHallVendors(ServerWorld level, TekVillageStructureManager structureManager) {
        TekVillageStructure townHall = structureManager.getStructure(TekStructureType.TOWNHALL).orElse(null);
        if (townHall == null || !townHall.isValid()) {
            return;
        }
        AxisAlignedBB bounds = townHall.getBounds().inflate(4.0D, 3.0D, 4.0D);
        if (level.getEntitiesOfClass(TekArchitectEntity.class, bounds, vendor -> vendor != null && vendor.isAlive()).isEmpty()) {
            TekArchitectEntity architect = TekEntities.TEK_ARCHITECT.get().create(level);
            if (architect != null) {
                this.placeVillageEntity(level, architect, townHall.getSafeSpot());
            }
        }
        if (level.getEntitiesOfClass(TekTradesmanEntity.class, bounds, vendor -> vendor != null && vendor.isAlive()).isEmpty()) {
            TekTradesmanEntity tradesman = TekEntities.TEK_TRADESMAN.get().create(level);
            if (tradesman != null) {
                this.placeVillageEntity(level, tradesman, townHall.getSafeSpot());
            }
        }
    }

    private boolean spawnMerchantVisitor(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager) {
        TekVillageStructure stall = structureManager.getStructure(TekStructureType.MERCHANT_STALL).orElse(null);
        if (stall == null || !stall.isValid()) {
            return false;
        }
        AxisAlignedBB bounds = village.getBounds().inflate(40.0D, 8.0D, 40.0D);
        if (!level.getEntitiesOfClass(TekMerchantEntity.class, bounds, merchant -> merchant != null && merchant.isAlive()).isEmpty()) {
            return false;
        }
        TekMerchantEntity merchant = TekEntities.TEK_MERCHANT.get().create(level);
        if (merchant == null) {
            return false;
        }
        BlockPos arrival = this.resolveArrivalPoint(level, village, village.getVisitorSpawnCount());
        this.placeVillageEntity(level, merchant, arrival);
        merchant.setWorkerStatus(TekWorkerStatus.MOVING);
        merchant.getNavigation().moveTo(stall.getSafeSpot().getX() + 0.5D, stall.getSafeSpot().getY(), stall.getSafeSpot().getZ() + 0.5D, 1.0D);
        return true;
    }

    private boolean spawnNomadVisitor(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager, List<TekVillagerEntity> villagers) {
        TekVillageStructure townHall = structureManager.getStructure(TekStructureType.TOWNHALL).orElse(null);
        TekVillageStructure storage = structureManager.getStructure(TekStructureType.STORAGE).orElse(null);
        TekVillageStructure home = structureManager.getStructure(TekStructureType.HOME)
                .orElse(structureManager.getStructure(TekStructureType.HOME2)
                        .orElse(structureManager.getStructure(TekStructureType.HOME4)
                                .orElse(structureManager.getStructure(TekStructureType.HOME6).orElse(null))));
        if (townHall == null || storage == null || home == null || !townHall.isValid() || !storage.isValid() || !home.isValid()) {
            return false;
        }
        if (villagers.size() >= Math.max(4, home.getFloorTileCount() / 4)) {
            return false;
        }
        TekNomadEntity nomad = TekEntities.TEK_NOMAD.get().create(level);
        if (nomad == null) {
            return false;
        }
        BlockPos arrival = this.resolveArrivalPoint(level, village, village.getVisitorSpawnCount());
        this.placeVillageEntity(level, nomad, arrival);
        nomad.setWorkerStatus(TekWorkerStatus.MOVING);
        nomad.getNavigation().moveTo(townHall.getSafeSpot().getX() + 0.5D, townHall.getSafeSpot().getY(), townHall.getSafeSpot().getZ() + 0.5D, 1.0D);
        return true;
    }

    private boolean spawnNecromancerRaid(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager) {
        TekVillageStructure townHall = structureManager.getStructure(TekStructureType.TOWNHALL).orElse(null);
        if (townHall == null || !townHall.isValid()) {
            return false;
        }
        int count = Math.max(1, Math.min(4, 1 + village.getRaidLevel()));
        for (int i = 0; i < count; i++) {
            TekNecromancerEntity necromancer = TekEntities.TEK_NECROMANCER.get().create(level);
            if (necromancer == null) {
                continue;
            }
            BlockPos arrival = this.resolveArrivalPoint(level, village, village.getVisitorSpawnCount() + i);
            this.placeVillageEntity(level, necromancer, arrival);
            necromancer.getNavigation().moveTo(townHall.getSafeSpot().getX() + 0.5D, townHall.getSafeSpot().getY(), townHall.getSafeSpot().getZ() + 0.5D, 0.95D);
        }
        return true;
    }

    private BlockPos resolveArrivalPoint(ServerWorld level, TekVillage village, int cornerIndex) {
        BlockPos corner = village.getVisitorArrivalPoint(cornerIndex);
        int y = level.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, corner.getX(), corner.getZ());
        return new BlockPos(corner.getX(), y, corner.getZ());
    }

    private void placeVillageEntity(ServerWorld level, MobEntity entity, BlockPos pos) {
        entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(entity);
    }

    private void tickTeacherWork(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager, TekTeacherEntity teacher) {
        CompoundNBT data = teacher.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekVillageStructure school = structureManager.getStructure(TekStructureType.SCHOOL).orElse(null);
        if (school == null || !school.isValid()) {
            this.setWorkerResult(data, "teacher", "missing_school", gameTime + SOCIAL_WORK_COOLDOWN);
            teacher.setWorkerStatus(TekWorkerStatus.WAITING_FOR_INPUTS);
            return;
        }
        if (!this.isSchoolTime(level)) {
            teacher.setWorkerStatus(TekWorkerStatus.IDLE);
            this.setWorkerResult(data, "teacher", "school_closed", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        List<TekChildEntity> children = level.getEntitiesOfClass(TekChildEntity.class, school.getBounds().inflate(2.0D), child -> child != null && child.isAlive());
        if (children.isEmpty()) {
            this.setWorkerResult(data, "teacher", "no_students", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        if (!this.spendWorkerHunger(teacher, 2, "teacher")) {
            this.setWorkerResult(data, "teacher", "too_hungry", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        TekVillageEconomy economy = storage == null ? null : TekVillageEconomy.fromStorage(level, storage);
        boolean usedBook = economy != null && this.tryStorageRecipe(economy, "teacher_book", Items.BOOK, 1, ItemStack.EMPTY, gameTime);
        int lesson = 2 + teacher.getSkill(teacher.getProfessionType()) / 25 + (usedBook ? 3 : 0);
        for (TekChildEntity child : children) {
            child.setIntelligence(child.getIntelligence() + lesson);
            child.setThoughtKey(usedBook ? "book_lesson" : "school");
            child.setWorkerStatus(TekWorkerStatus.WORKING);
        }
        teacher.addSkill(teacher.getProfessionType(), 1);
        teacher.setThoughtKey(usedBook ? "teaching_books" : "teaching");
        teacher.setWorkerStatus(TekWorkerStatus.WORKING);
        this.setWorkerResult(data, "teacher", (usedBook ? "book_taught_" : "taught_") + children.size(), gameTime + SOCIAL_WORK_COOLDOWN);
    }

    private void tickBardWork(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager, TekBardEntity bard) {
        CompoundNBT data = bard.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekVillageStructure tavern = structureManager.getStructure(TekStructureType.TAVERN).orElse(null);
        if (tavern == null || !tavern.isValid()) {
            this.setWorkerResult(data, "bard", "missing_tavern", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        if (!this.isSocialTime(level)) {
            bard.setWorkerStatus(TekWorkerStatus.IDLE);
            this.setWorkerResult(data, "bard", "tavern_closed", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        if (!this.spendWorkerHunger(bard, 2, "bard")) {
            this.setWorkerResult(data, "bard", "too_hungry", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        List<TekVillagerEntity> audience = level.getEntitiesOfClass(TekVillagerEntity.class, tavern.getBounds().inflate(5.0D), villager -> villager != bard && villager.isAlive());
        for (TekVillagerEntity villager : audience) {
            villager.setHappy(villager.getHappy() + 3);
            villager.setThoughtKey("music");
        }
        bard.addSkill(bard.getProfessionType(), 1);
        bard.setHappy(bard.getHappy() + 2);
        bard.setWorkerStatus(TekWorkerStatus.SOCIALIZING);
        this.setWorkerResult(data, "bard", "performed_" + audience.size(), gameTime + SOCIAL_WORK_COOLDOWN);
    }

    private void tickClericWork(ServerWorld level, TekVillage village, TekClericEntity cleric, List<TekVillagerEntity> villagers) {
        CompoundNBT data = cleric.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekVillagerEntity target = villagers.stream()
                .filter(villager -> villager != cleric && villager.isAlive() && (villager.getHealth() < villager.getMaxHealth() || villager.getHappy() < 70))
                .min(Comparator.comparingDouble(villager -> villager.distanceToSqr(cleric)))
                .orElse(null);
        if (target == null) {
            this.setWorkerResult(data, "cleric", "no_bless_target", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        if (!this.spendWorkerHunger(cleric, 3, "cleric")) {
            this.setWorkerResult(data, "cleric", "too_hungry", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        target.heal(4.0F);
        target.setHappy(target.getHappy() + 5);
        target.setThoughtKey("blessed");
        cleric.addSkill(cleric.getProfessionType(), 1);
        cleric.setWorkerStatus(TekWorkerStatus.WORKING);
        this.setWorkerResult(data, "cleric", "blessed", gameTime + SOCIAL_WORK_COOLDOWN);
    }

    private void tickDruidWork(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager, TekDruidEntity druid) {
        CompoundNBT data = druid.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        BlockPos target = this.findNearestGrowableCrop(level, druid.blockPosition(), village);
        if (target == null) {
            this.setWorkerResult(data, "druid", "no_growth_target", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        if (!this.spendWorkerHunger(druid, 4, "druid")) {
            this.setWorkerResult(data, "druid", "too_hungry", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        BlockState state = level.getBlockState(target);
        if (state.getBlock() instanceof CropsBlock) {
            CropsBlock crop = (CropsBlock) state.getBlock();
            if (!crop.isMaxAge(state)) {
                level.setBlock(target, crop.getStateForAge(crop.getMaxAge()), 3);
                druid.addSkill(druid.getProfessionType(), 1);
                druid.setWorkerStatus(TekWorkerStatus.WORKING);
                this.setWorkerResult(data, "druid", "grew_crop", gameTime + SOCIAL_WORK_COOLDOWN);
                return;
            }
        }
        this.setWorkerResult(data, "druid", "blocked_growth", gameTime + SOCIAL_WORK_COOLDOWN);
    }

    private void tickEnchanterWork(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager, TekEnchanterEntity enchanter) {
        CompoundNBT data = enchanter.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekVillageStructure library = structureManager.getStructure(TekStructureType.LIBRARY).orElse(null);
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (library == null || !library.isValid() || storage == null) {
            this.setWorkerResult(data, "enchanter", "missing_library_or_storage", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (this.tryStorageRecipe(economy, "enchanter_paper", Items.SUGAR_CANE, 3, new ItemStack(Items.PAPER, 3), gameTime)) {
            if (this.spendWorkerHunger(enchanter, 1, "enchanter")) {
                enchanter.addSkill(enchanter.getProfessionType(), 1);
            }
            enchanter.setWorkerStatus(TekWorkerStatus.WORKING);
            this.setWorkerResult(data, "enchanter", "crafted_paper", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        Map<Item, Integer> bookInputs = new HashMap<>();
        bookInputs.put(Items.PAPER, 3);
        bookInputs.put(Items.LEATHER, 1);
        if (economy.craftWithReservedInputs("enchanter_book", bookInputs, new ItemStack(Items.BOOK), gameTime)) {
            if (this.spendWorkerHunger(enchanter, 2, "enchanter")) {
                enchanter.addSkill(enchanter.getProfessionType(), 1);
            }
            enchanter.setWorkerStatus(TekWorkerStatus.WORKING);
            this.setWorkerResult(data, "enchanter", "crafted_book", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        if (this.tryStorageRecipe(economy, "enchanter", Items.LAPIS_LAZULI, 1, new ItemStack(Items.EXPERIENCE_BOTTLE), gameTime)) {
            this.spendWorkerHunger(enchanter, 2, "enchanter");
            enchanter.addSkill(enchanter.getProfessionType(), 1);
            enchanter.setWorkerStatus(TekWorkerStatus.WORKING);
            this.setWorkerResult(data, "enchanter", "bottled_xp", gameTime + SOCIAL_WORK_COOLDOWN);
            return;
        }
        this.setWorkerResult(data, "enchanter", "missing_lapis", gameTime + SOCIAL_WORK_COOLDOWN);
    }

    private void tickNitwitWork(ServerWorld level, TekVillage village, TekNitwitEntity nitwit) {
        CompoundNBT data = nitwit.getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        nitwit.setHappy(nitwit.getHappy() + 1);
        nitwit.setThoughtKey("wandering");
        nitwit.setWorkerStatus(TekWorkerStatus.SOCIALIZING);
        this.setWorkerResult(data, "nitwit", "wandered", gameTime + SOCIAL_WORK_COOLDOWN);
    }

    private void tickChildWork(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager, TekChildEntity child) {
        if (child.getDaysAlive() >= CHILD_ADULT_DAYS) {
            this.promoteChildToNitwit(level, child);
            return;
        }
        TekVillageStructure school = structureManager.getStructure(TekStructureType.SCHOOL).orElse(null);
        if (school != null && school.isValid() && this.isSchoolTime(level)) {
            child.setWorkerStatus(TekWorkerStatus.MOVING);
            child.setThoughtKey("school");
            child.getNavigation().moveTo(school.getSafeSpot().getX() + 0.5D, school.getSafeSpot().getY(), school.getSafeSpot().getZ() + 0.5D, 1.0D);
        } else {
            child.setWorkerStatus(TekWorkerStatus.SOCIALIZING);
        }
    }

    private boolean isSchoolTime(ServerWorld level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime >= SCHOOL_START_TIME && dayTime <= SCHOOL_END_TIME;
    }

    private boolean isSocialTime(ServerWorld level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime >= SOCIAL_START_TIME && dayTime <= SOCIAL_END_TIME;
    }

    private boolean spendWorkerHunger(TekVillagerEntity villager, int amount, String thought) {
        if (amount <= 0) {
            return true;
        }
        if (villager.getHunger() <= amount) {
            villager.setThoughtKey("needs_food");
            villager.setWorkerStatus(TekWorkerStatus.WAITING_FOR_INPUTS);
            return false;
        }
        villager.setHunger(villager.getHunger() - amount);
        if (thought != null && !thought.isEmpty()) {
            villager.setThoughtKey(thought);
        }
        return true;
    }

    private void promoteChildToNitwit(ServerWorld level, TekChildEntity child) {
        TekNitwitEntity nitwit = TekEntities.TEK_NITWIT.get().create(level);
        if (nitwit == null) {
            return;
        }
        nitwit.moveTo(child.getX(), child.getY(), child.getZ(), child.yRot, child.xRot);
        nitwit.setHunger(child.getHunger());
        nitwit.setHappy(child.getHappy());
        nitwit.setIntelligence(child.getIntelligence());
        nitwit.setDaysAlive(child.getDaysAlive());
        nitwit.setHomePos(child.getHomePos());
        nitwit.setBedPos(child.getBedPos());
        nitwit.setThoughtKey("grown_up");
        level.addFreshEntity(nitwit);
        child.remove();
    }

    private void tickTownHallVendor(ServerWorld level, TekVillageStructureManager structureManager, TekVillagerEntity vendor) {
        TekVillageStructure townHall = structureManager.getStructure(TekStructureType.TOWNHALL).orElse(null);
        if (townHall == null || !townHall.isValid()) {
            vendor.setWorkerStatus(TekWorkerStatus.WAITING_FOR_INPUTS);
            return;
        }
        vendor.setWorkerStatus(TekWorkerStatus.VENDING);
        if (!townHall.getBounds().inflate(2.0D).contains(vendor.position())) {
            vendor.getNavigation().moveTo(townHall.getSafeSpot().getX() + 0.5D, townHall.getSafeSpot().getY(), townHall.getSafeSpot().getZ() + 0.5D, 1.0D);
        }
    }

    private void tickFarmerWork(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekFarmerEntity farmer
    ) {
        long gameTime = level.getGameTime();
        CompoundNBT data = farmer.getPersistentData();
        if (gameTime < data.getLong(FARMER_COOLDOWN_TAG)) {
            return;
        }
        if (this.hasFarmerCarry(data)) {
            this.tickFarmerDelivery(level, village, structureManager, farmer, data, gameTime);
            return;
        }

        data.putInt(FARMER_MODE_TAG, FARMER_MODE_HARVEST);

        BlockPos target = this.readFarmerTarget(data);
        if (target == null || !this.isHarvestableCrop(level, target, village)) {
            target = this.findNearestHarvestableCrop(level, farmer.blockPosition(), village);
        }
        if (target == null) {
            if (this.tryFarmerPlantOrTill(level, village, structureManager, farmer, data, gameTime)) {
                return;
            }
            this.setWorkerResult(data, "farmer", "no_crop_target", gameTime + FARMER_RETRY_COOLDOWN);
            TekWorkerNavigator.clearNavigation(data, FARMER_TARGET_POS_TAG);
            return;
        }

        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                farmer,
                data,
                FARMER_TARGET_POS_TAG,
                FARMER_COOLDOWN_TAG,
                target,
                1.0D,
                4.0D,
                gameTime,
                FARMER_PATH_STEP_COOLDOWN,
                FARMER_RETRY_COOLDOWN,
                "farmer_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }

        List<ItemStack> drops = this.harvestAndReplant(level, target);
        this.collectFarmerDrops(data, drops);
        data.remove(FARMER_TARGET_POS_TAG);
        data.putLong(FARMER_COOLDOWN_TAG, gameTime + (this.hasFarmerCarry(data) ? FARMER_PATH_STEP_COOLDOWN : FARMER_WORK_COOLDOWN));
    }

    private void tickFarmerDelivery(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekFarmerEntity farmer,
            CompoundNBT data,
            long gameTime
    ) {
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (storage == null) {
            data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_RETRY_COOLDOWN);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (economy.getChests().isEmpty()) {
            data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_RETRY_COOLDOWN);
            return;
        }

        data.putInt(FARMER_MODE_TAG, FARMER_MODE_DELIVER);
        BlockPos deliveryPos = storage.getSafeSpot() != null ? storage.getSafeSpot() : village.getCenter();
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                farmer,
                data,
                null,
                FARMER_COOLDOWN_TAG,
                deliveryPos,
                1.0D,
                9.0D,
                gameTime,
                FARMER_PATH_STEP_COOLDOWN,
                FARMER_RETRY_COOLDOWN,
                "farmer_delivery_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }

        this.deliverFarmerCarry(data, economy);
        data.putLong(FARMER_COOLDOWN_TAG, gameTime + (this.hasFarmerCarry(data) ? FARMER_RETRY_COOLDOWN : FARMER_WORK_COOLDOWN));
    }

    private void tickBlacksmithWork(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekBlacksmithEntity blacksmith
    ) {
        long gameTime = level.getGameTime();
        CompoundNBT data = blacksmith.getPersistentData();
        if (gameTime < data.getLong(BLACKSMITH_COOLDOWN_TAG)) {
            return;
        }

        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (storage == null || storage.getChestPositions().isEmpty()) {
            data.putLong(BLACKSMITH_COOLDOWN_TAG, gameTime + BLACKSMITH_RETRY_COOLDOWN);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (economy.getChests().isEmpty()) {
            data.putLong(BLACKSMITH_COOLDOWN_TAG, gameTime + BLACKSMITH_RETRY_COOLDOWN);
            return;
        }
        BlacksmithDemand demand = this.computeBlacksmithDemand(level, village, economy);
        this.writeBlacksmithDebugData(blacksmith, demand, economy);

        BlockPos workPos = storage.getSafeSpot() != null ? storage.getSafeSpot() : village.getCenter();
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                blacksmith,
                data,
                null,
                BLACKSMITH_COOLDOWN_TAG,
                workPos,
                1.0D,
                9.0D,
                gameTime,
                15L,
                BLACKSMITH_RETRY_COOLDOWN,
                "blacksmith_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }

        ArmorRecipe planned = this.selectBlacksmithRecipe(economy, demand, blacksmith);
        data.putString(BLACKSMITH_PLAN_TAG, planned == null ? "-" : planned.output.getRegistryName() != null ? planned.output.getRegistryName().toString() : planned.output.toString());
        boolean crafted = this.tryCraftArmorFromStorage(planned, economy);
        data.putLong(BLACKSMITH_COOLDOWN_TAG, gameTime + (crafted ? BLACKSMITH_WORK_COOLDOWN : BLACKSMITH_RETRY_COOLDOWN));
    }

    private void tickRancherWork(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekRancherEntity rancher
    ) {
        long gameTime = level.getGameTime();
        CompoundNBT data = rancher.getPersistentData();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (storage == null) {
            this.setWorkerResult(data, "rancher", "missing_storage", gameTime + WORKER_RETRY_COOLDOWN);
            rancher.setWorkerStatus(TekWorkerStatus.WAITING_FOR_STORAGE);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (economy.getChests().isEmpty()) {
            this.setWorkerResult(data, "rancher", "missing_chest", gameTime + WORKER_RETRY_COOLDOWN);
            rancher.setWorkerStatus(TekWorkerStatus.WAITING_FOR_STORAGE);
            return;
        }
        TekAnimalPens.PenSnapshot pen = TekAnimalPens.findRancherPen(level, structureManager, economy, gameTime);
        if (pen == null) {
            this.setWorkerResult(data, "rancher", "missing_pen", gameTime + WORKER_RETRY_COOLDOWN);
            rancher.setWorkerStatus(TekWorkerStatus.WAITING_FOR_INPUTS);
            return;
        }
        data.putString(WORKER_MODE_TAG, "rancher_pen");
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                rancher,
                data,
                null,
                WORKER_COOLDOWN_TAG,
                pen.workPos,
                1.0D,
                9.0D,
                gameTime,
                WORKER_PATH_STEP_COOLDOWN,
                WORKER_RETRY_COOLDOWN,
                "rancher_pen_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }
        rancher.setWorkerStatus(TekWorkerStatus.WORKING);
        String result = TekAnimalPens.runRancherAction(pen, economy, gameTime);
        rancher.setWorkerStatus(result.startsWith("missing") || result.startsWith("empty")
                ? TekWorkerStatus.WAITING_FOR_INPUTS
                : TekWorkerStatus.IDLE);
        this.setWorkerResult(data, "rancher", result, gameTime + WORKER_WORK_COOLDOWN);
    }

    private void tickButcherWork(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekButcherEntity butcher
    ) {
        long gameTime = level.getGameTime();
        CompoundNBT data = butcher.getPersistentData();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekVillageStructure butcherShop = structureManager.getStructure(TekStructureType.BUTCHER).orElse(null);
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (butcherShop == null || !butcherShop.isValid() || storage == null) {
            this.setWorkerResult(data, "butcher", "missing_butcher_or_storage", gameTime + WORKER_RETRY_COOLDOWN);
            butcher.setWorkerStatus(TekWorkerStatus.WAITING_FOR_INPUTS);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (economy.getChests().isEmpty()) {
            this.setWorkerResult(data, "butcher", "missing_chest", gameTime + WORKER_RETRY_COOLDOWN);
            butcher.setWorkerStatus(TekWorkerStatus.WAITING_FOR_STORAGE);
            return;
        }
        TekAnimalPens.ButcherTarget target = TekAnimalPens.findButcherTarget(level, structureManager, economy, butcher.blockPosition());
        if (target == null) {
            String cooked = this.tryButcherRecipe(economy, gameTime);
            this.setWorkerResult(data, "butcher", "-".equals(cooked) ? "no_surplus_animal" : cooked, gameTime + WORKER_RETRY_COOLDOWN);
            butcher.setWorkerStatus("-".equals(cooked) ? TekWorkerStatus.WAITING_FOR_INPUTS : TekWorkerStatus.WORKING);
            return;
        }
        data.putString(WORKER_MODE_TAG, "butcher_animal");
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                butcher,
                data,
                WORKER_TARGET_TAG,
                WORKER_COOLDOWN_TAG,
                target.getWorkPos(),
                1.0D,
                6.0D,
                gameTime,
                WORKER_PATH_STEP_COOLDOWN,
                WORKER_RETRY_COOLDOWN,
                "butcher_animal_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }
        butcher.setWorkerStatus(TekWorkerStatus.WORKING);
        String result = TekAnimalPens.processButcherTarget(target, economy);
        this.setWorkerResult(data, "butcher", result, gameTime + WORKER_WORK_COOLDOWN);
    }

    private BlockPos readFarmerTarget(CompoundNBT data) {
        if (!data.contains(FARMER_TARGET_POS_TAG, 4)) {
            return null;
        }
        return BlockPos.of(data.getLong(FARMER_TARGET_POS_TAG));
    }

    private boolean tryFarmerPlantOrTill(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekFarmerEntity farmer,
            CompoundNBT data,
            long gameTime
    ) {
        BlockPos origin = this.resolveWorkPos(structureManager, TekStructureType.FARM, village).orElse(farmer.blockPosition());
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        TekVillageEconomy economy = storage == null ? null : TekVillageEconomy.fromStorage(level, storage);
        SeedPlan seedPlan = economy == null ? null : this.selectSeedPlan(economy, gameTime);
        if (seedPlan != null) {
            BlockPos plantTarget = this.findNearestPlantableFarmland(level, origin, village);
            if (plantTarget != null) {
                TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                        level,
                        farmer,
                        data,
                        FARMER_TARGET_POS_TAG,
                        FARMER_COOLDOWN_TAG,
                        plantTarget,
                        1.0D,
                        4.0D,
                        gameTime,
                        FARMER_PATH_STEP_COOLDOWN,
                        FARMER_RETRY_COOLDOWN,
                        "farmer_plant_unreachable"
                );
                if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
                    return true;
                }
                Map<Item, Integer> inputs = new HashMap<>();
                inputs.put(seedPlan.seedItem, 1);
                if (economy.craftWithReservedInputs("farmer_seed", inputs, ItemStack.EMPTY, gameTime)) {
                    level.setBlock(plantTarget, seedPlan.cropBlock.defaultBlockState(), 3);
                    this.setWorkerResult(data, "farmer", "planted_" + seedPlan.seedItem.getRegistryName().getPath(), gameTime + FARMER_WORK_COOLDOWN);
                    farmer.setWorkerStatus(TekWorkerStatus.WORKING);
                    return true;
                }
                this.setWorkerResult(data, "farmer", "missing_seed", gameTime + FARMER_RETRY_COOLDOWN);
                return true;
            }
        }

        BlockPos tillTarget = this.findNearestTillableBlock(level, origin, village);
        if (tillTarget == null) {
            return false;
        }
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                farmer,
                data,
                FARMER_TARGET_POS_TAG,
                FARMER_COOLDOWN_TAG,
                tillTarget,
                1.0D,
                4.0D,
                gameTime,
                FARMER_PATH_STEP_COOLDOWN,
                FARMER_RETRY_COOLDOWN,
                "farmer_till_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return true;
        }
        level.setBlock(tillTarget, Blocks.FARMLAND.defaultBlockState(), 3);
        this.setWorkerResult(data, "farmer", "tilled_soil", gameTime + FARMER_WORK_COOLDOWN);
        farmer.setWorkerStatus(TekWorkerStatus.WORKING);
        return true;
    }

    private SeedPlan selectSeedPlan(TekVillageEconomy economy, long gameTime) {
        if (economy.countAvailableItem(Items.WHEAT_SEEDS, gameTime) > 0) {
            return new SeedPlan(Items.WHEAT_SEEDS, Blocks.WHEAT);
        }
        if (economy.countAvailableItem(Items.CARROT, gameTime) > 0) {
            return new SeedPlan(Items.CARROT, Blocks.CARROTS);
        }
        if (economy.countAvailableItem(Items.POTATO, gameTime) > 0) {
            return new SeedPlan(Items.POTATO, Blocks.POTATOES);
        }
        if (economy.countAvailableItem(Items.BEETROOT_SEEDS, gameTime) > 0) {
            return new SeedPlan(Items.BEETROOT_SEEDS, Blocks.BEETROOTS);
        }
        return null;
    }

    private BlockPos findNearestPlantableFarmland(ServerWorld level, BlockPos origin, TekVillage village) {
        int radius = Math.min(village.getRadius(), FARMER_SCAN_RADIUS);
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!village.contains(candidate) || !level.getBlockState(candidate).isAir() || !level.getBlockState(candidate.below()).is(Blocks.FARMLAND)) {
                        continue;
                    }
                    double dist = candidate.distSqr(origin);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestPos = candidate.immutable();
                    }
                }
            }
        }
        return bestPos;
    }

    private BlockPos findNearestTillableBlock(ServerWorld level, BlockPos origin, TekVillage village) {
        int radius = Math.min(village.getRadius(), FARMER_SCAN_RADIUS);
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos candidate = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(candidate);
                    if (!village.contains(candidate)
                            || !level.getBlockState(candidate.above()).isAir()
                            || state.getBlock() != Blocks.DIRT && state.getBlock() != Blocks.GRASS_BLOCK) {
                        continue;
                    }
                    double dist = candidate.distSqr(origin);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestPos = candidate.immutable();
                    }
                }
            }
        }
        return bestPos;
    }

    private BlockPos findNearestHarvestableCrop(ServerWorld level, BlockPos origin, TekVillage village) {
        int radius = Math.min(village.getRadius(), FARMER_SCAN_RADIUS);
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!this.isHarvestableCrop(level, candidate, village)) {
                        continue;
                    }
                    double dist = candidate.distSqr(origin);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestPos = candidate.immutable();
                    }
                }
            }
        }
        return bestPos;
    }

    private boolean isHarvestableCrop(ServerWorld level, BlockPos pos, TekVillage village) {
        if (!village.contains(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CropsBlock)) {
            return false;
        }
        CropsBlock crop = (CropsBlock) state.getBlock();
        return crop.isMaxAge(state);
    }

    private BlockPos findNearestGrowableCrop(ServerWorld level, BlockPos origin, TekVillage village) {
        int radius = Math.min(village.getRadius(), FARMER_SCAN_RADIUS);
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!village.contains(candidate)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(candidate);
                    if (!(state.getBlock() instanceof CropsBlock)) {
                        continue;
                    }
                    CropsBlock crop = (CropsBlock) state.getBlock();
                    if (crop.isMaxAge(state)) {
                        continue;
                    }
                    double dist = candidate.distSqr(origin);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestPos = candidate.immutable();
                    }
                }
            }
        }
        return bestPos;
    }

    private List<ItemStack> harvestAndReplant(ServerWorld level, BlockPos pos) {
        List<ItemStack> drops = new java.util.ArrayList<>();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CropsBlock)) {
            return drops;
        }
        CropsBlock crop = (CropsBlock) state.getBlock();
        if (!crop.isMaxAge(state)) {
            return drops;
        }
        drops.addAll(Block.getDrops(state, level, pos, null));
        level.destroyBlock(pos, false);
        if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).is(Blocks.FARMLAND)) {
            level.setBlock(pos, crop.defaultBlockState(), 3);
        }
        return drops;
    }

    private Optional<TekStructureStorage> resolveStorageStructure(TekVillageStructureManager structureManager) {
        if (structureManager == null) {
            return Optional.empty();
        }
        TekVillageStructure storage = structureManager.getStructure(TekStructureType.STORAGE).orElse(null);
        if (storage instanceof TekStructureStorage) {
            return Optional.of((TekStructureStorage) storage);
        }
        return Optional.empty();
    }

    private Optional<BlockPos> resolveWorkPos(TekVillageStructureManager structureManager, TekStructureType preferredType, TekVillage village) {
        if (structureManager != null) {
            TekVillageStructure preferred = structureManager.getStructure(preferredType).orElse(null);
            if (preferred != null && preferred.getSafeSpot() != null) {
                return Optional.of(preferred.getSafeSpot());
            }
            TekVillageStructure storage = structureManager.getStructure(TekStructureType.STORAGE).orElse(null);
            if (storage != null && storage.getSafeSpot() != null) {
                return Optional.of(storage.getSafeSpot());
            }
        }
        return Optional.of(village.getCenter());
    }

    private Optional<BlockPos> resolveGuardIdlePos(TekVillageStructureManager structureManager) {
        if (structureManager == null) {
            return Optional.empty();
        }
        TekVillageStructure guardPost = structureManager.getStructure(TekStructureType.GUARD_POST).orElse(null);
        if (guardPost != null && guardPost.getSafeSpot() != null) {
            return Optional.of(guardPost.getSafeSpot());
        }
        TekVillageStructure barracks = structureManager.getStructure(TekStructureType.BARRACKS).orElse(null);
        if (barracks != null && barracks.getSafeSpot() != null) {
            return Optional.of(barracks.getSafeSpot());
        }
        return Optional.empty();
    }

    private void tickBlockGatherer(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekVillagerEntity worker,
            TekStructureType workType,
            String mode,
            java.util.function.Predicate<BlockState> targetPredicate
    ) {
        long gameTime = level.getGameTime();
        CompoundNBT data = worker.getPersistentData();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (storage == null) {
            this.setWorkerResult(data, mode, "missing_storage", gameTime + WORKER_RETRY_COOLDOWN);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (economy.getChests().isEmpty()) {
            this.setWorkerResult(data, mode, "missing_chest", gameTime + WORKER_RETRY_COOLDOWN);
            return;
        }
        BlockPos origin = this.resolveWorkPos(structureManager, workType, village).orElse(worker.blockPosition());
        BlockPos target = this.readWorkerTarget(data);
        if (target == null || !village.contains(target) || !targetPredicate.test(level.getBlockState(target))) {
            target = this.findNearestBlock(level, origin, village, targetPredicate);
        }
        if (target == null) {
            this.setWorkerResult(data, mode, "no_target", gameTime + WORKER_RETRY_COOLDOWN);
            data.remove(WORKER_TARGET_TAG);
            return;
        }
        data.putLong(WORKER_TARGET_TAG, target.asLong());
        data.putString(WORKER_MODE_TAG, mode + "_gather");
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                worker,
                data,
                WORKER_TARGET_TAG,
                WORKER_COOLDOWN_TAG,
                target,
                1.0D,
                9.0D,
                gameTime,
                WORKER_PATH_STEP_COOLDOWN,
                WORKER_RETRY_COOLDOWN,
                mode + "_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }

        BlockState targetState = level.getBlockState(target);
        List<ItemStack> drops = "lumberjack".equals(mode)
                ? this.collectLumberDrops(level, target)
                : this.collectWorkerBlockDrops(level, targetState, target, mode);
        if (!this.canInsertAll(economy, drops)) {
            this.setWorkerResult(data, mode, "storage_full", gameTime + WORKER_RETRY_COOLDOWN);
            return;
        }
        if ("lumberjack".equals(mode)) {
            this.destroyLumberClusterAndReplant(level, target, drops);
        } else {
            level.destroyBlock(target, false);
        }
        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) {
                economy.insert(drop);
            }
        }
        data.remove(WORKER_TARGET_TAG);
        this.setWorkerResult(data, mode, "gathered_" + drops.size(), gameTime + WORKER_WORK_COOLDOWN);
    }

    private void tickStorageCrafter(
            ServerWorld level,
            TekVillage village,
            TekVillageStructureManager structureManager,
            TekVillagerEntity worker,
            TekStructureType workType,
            String mode,
            long successCooldown,
            java.util.function.BiFunction<TekVillageEconomy, Long, String> recipe
    ) {
        long gameTime = level.getGameTime();
        CompoundNBT data = worker.getPersistentData();
        if (gameTime < data.getLong(WORKER_COOLDOWN_TAG)) {
            return;
        }
        TekStructureStorage storage = this.resolveStorageStructure(structureManager).orElse(null);
        if (storage == null) {
            this.setWorkerResult(data, mode, "missing_storage", gameTime + WORKER_RETRY_COOLDOWN);
            return;
        }
        TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
        if (economy.getChests().isEmpty()) {
            this.setWorkerResult(data, mode, "missing_chest", gameTime + WORKER_RETRY_COOLDOWN);
            return;
        }
        BlockPos workPos = this.resolveWorkPos(structureManager, workType, village).orElse(village.getCenter());
        data.putString(WORKER_MODE_TAG, mode + "_work");
        TekWorkerNavigator.NavigationResult navigationResult = TekWorkerNavigator.moveTo(
                level,
                worker,
                data,
                null,
                WORKER_COOLDOWN_TAG,
                workPos,
                1.0D,
                9.0D,
                gameTime,
                WORKER_PATH_STEP_COOLDOWN,
                WORKER_RETRY_COOLDOWN,
                mode + "_worksite_unreachable"
        );
        if (navigationResult != TekWorkerNavigator.NavigationResult.REACHED) {
            return;
        }

        worker.setWorkerStatus(TekWorkerStatus.WORKING);
        String result = recipe.apply(economy, gameTime);
        if (worker instanceof TekMerchantEntity && !"-".equals(result)) {
            village.recordMerchantSale(result);
        }
        long nextCooldown = "-".equals(result) ? WORKER_RETRY_COOLDOWN : successCooldown;
        worker.setWorkerStatus("-".equals(result) ? TekWorkerStatus.WAITING_FOR_INPUTS : TekWorkerStatus.IDLE);
        this.setWorkerResult(data, mode, result, gameTime + nextCooldown);
    }

    private BlockPos readWorkerTarget(CompoundNBT data) {
        if (!data.contains(WORKER_TARGET_TAG, 4)) {
            return null;
        }
        return BlockPos.of(data.getLong(WORKER_TARGET_TAG));
    }

    private BlockPos findNearestBlock(
            ServerWorld level,
            BlockPos origin,
            TekVillage village,
            java.util.function.Predicate<BlockState> predicate
    ) {
        int radius = Math.min(village.getRadius(), WORKER_SCAN_RADIUS);
        double bestDistance = Double.MAX_VALUE;
        BlockPos bestPos = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!village.contains(candidate) || !predicate.test(level.getBlockState(candidate))) {
                        continue;
                    }
                    double dist = candidate.distSqr(origin);
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestPos = candidate.immutable();
                    }
                }
            }
        }
        return bestPos;
    }

    private boolean isMinerTarget(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.STONE
                || block == Blocks.COBBLESTONE
                || block == Blocks.COAL_ORE
                || block == Blocks.IRON_ORE
                || block == Blocks.GOLD_ORE
                || block == Blocks.REDSTONE_ORE
                || block == Blocks.LAPIS_ORE;
    }

    private boolean isLumberTarget(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.OAK_LOG
                || block == Blocks.SPRUCE_LOG
                || block == Blocks.BIRCH_LOG
                || block == Blocks.JUNGLE_LOG
                || block == Blocks.ACACIA_LOG
                || block == Blocks.DARK_OAK_LOG;
    }

    private List<ItemStack> collectWorkerBlockDrops(ServerWorld level, BlockState state, BlockPos target, String mode) {
        List<ItemStack> drops = Block.getDrops(state, level, target, null);
        if (!drops.isEmpty() || !"miner".equals(mode)) {
            return drops;
        }
        ItemStack fallback = this.minerFallbackDrop(state.getBlock());
        if (fallback.isEmpty()) {
            return drops;
        }
        drops.add(fallback);
        return drops;
    }

    private ItemStack minerFallbackDrop(Block block) {
        if (block == Blocks.STONE || block == Blocks.COBBLESTONE) {
            return new ItemStack(Items.COBBLESTONE);
        }
        if (block == Blocks.COAL_ORE) {
            return new ItemStack(Items.COAL);
        }
        if (block == Blocks.IRON_ORE) {
            return new ItemStack(Items.IRON_INGOT);
        }
        if (block == Blocks.GOLD_ORE) {
            return new ItemStack(Items.GOLD_INGOT);
        }
        if (block == Blocks.REDSTONE_ORE) {
            return new ItemStack(Items.REDSTONE, 4);
        }
        if (block == Blocks.LAPIS_ORE) {
            return new ItemStack(Items.LAPIS_LAZULI, 4);
        }
        return ItemStack.EMPTY;
    }

    private List<ItemStack> collectLumberDrops(ServerWorld level, BlockPos target) {
        List<ItemStack> drops = new java.util.ArrayList<>();
        for (BlockPos logPos : this.findConnectedLogs(level, target, 16)) {
            drops.addAll(Block.getDrops(level.getBlockState(logPos), level, logPos, null));
        }
        return drops;
    }

    private void destroyLumberClusterAndReplant(ServerWorld level, BlockPos target, List<ItemStack> drops) {
        Block originalLog = level.getBlockState(target).getBlock();
        List<BlockPos> logs = this.findConnectedLogs(level, target, 16);
        for (BlockPos logPos : logs) {
            level.destroyBlock(logPos, false);
        }
        Block sapling = this.saplingForLog(originalLog);
        if (sapling == Blocks.AIR || !level.getBlockState(target).isAir()) {
            return;
        }
        for (ItemStack drop : drops) {
            if (drop.getItem() != sapling.asItem() || drop.getCount() <= 0) {
                continue;
            }
            drop.shrink(1);
            level.setBlock(target, sapling.defaultBlockState(), 3);
            return;
        }
    }

    private List<BlockPos> findConnectedLogs(ServerWorld level, BlockPos start, int maxLogs) {
        List<BlockPos> logs = new java.util.ArrayList<>();
        java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
        java.util.HashSet<BlockPos> seen = new java.util.HashSet<>();
        queue.add(start.immutable());
        seen.add(start.immutable());
        while (!queue.isEmpty() && logs.size() < maxLogs) {
            BlockPos current = queue.pollFirst();
            if (!this.isLumberTarget(level.getBlockState(current))) {
                continue;
            }
            logs.add(current.immutable());
            for (BlockPos next : new BlockPos[] { current.above(), current.north(), current.south(), current.east(), current.west() }) {
                if (seen.add(next.immutable())) {
                    queue.addLast(next.immutable());
                }
            }
        }
        return logs;
    }

    private Block saplingForLog(Block log) {
        if (log == Blocks.OAK_LOG) {
            return Blocks.OAK_SAPLING;
        }
        if (log == Blocks.SPRUCE_LOG) {
            return Blocks.SPRUCE_SAPLING;
        }
        if (log == Blocks.BIRCH_LOG) {
            return Blocks.BIRCH_SAPLING;
        }
        if (log == Blocks.JUNGLE_LOG) {
            return Blocks.JUNGLE_SAPLING;
        }
        if (log == Blocks.ACACIA_LOG) {
            return Blocks.ACACIA_SAPLING;
        }
        if (log == Blocks.DARK_OAK_LOG) {
            return Blocks.DARK_OAK_SAPLING;
        }
        return Blocks.AIR;
    }

    private boolean canInsertAll(TekVillageEconomy economy, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && !economy.canInsert(stack)) {
                return false;
            }
        }
        return true;
    }

    private String tryChefRecipe(TekVillageEconomy economy, Long gameTime) {
        if (this.tryStorageRecipe(economy, "chef", Items.WHEAT, 3, new ItemStack(Items.BREAD), gameTime)) {
            return "bread";
        }
        if (this.tryStorageRecipe(economy, "chef", Items.POTATO, 1, new ItemStack(Items.BAKED_POTATO), gameTime)) {
            return "baked_potato";
        }
        return "-";
    }

    private String tryRancherRecipe(TekVillageEconomy economy, Long gameTime) {
        if (this.tryStorageRecipe(economy, "rancher", Items.WHEAT, 2, new ItemStack(Items.WHITE_WOOL), gameTime)) {
            return "wool";
        }
        if (this.tryStorageRecipe(economy, "rancher", Items.WHEAT_SEEDS, 3, new ItemStack(Items.EGG), gameTime)) {
            return "egg";
        }
        if (this.tryStorageRecipe(economy, "rancher", Items.CARROT, 2, new ItemStack(Items.LEATHER), gameTime)) {
            return "leather";
        }
        return "-";
    }

    private String tryButcherRecipe(TekVillageEconomy economy, Long gameTime) {
        if (this.tryStorageRecipe(economy, "butcher", Items.BEEF, 1, new ItemStack(Items.COOKED_BEEF), gameTime)) {
            return "cooked_beef";
        }
        if (this.tryStorageRecipe(economy, "butcher", Items.PORKCHOP, 1, new ItemStack(Items.COOKED_PORKCHOP), gameTime)) {
            return "cooked_porkchop";
        }
        if (this.tryStorageRecipe(economy, "butcher", Items.CHICKEN, 1, new ItemStack(Items.COOKED_CHICKEN), gameTime)) {
            return "cooked_chicken";
        }
        if (this.tryStorageRecipe(economy, "butcher", Items.MUTTON, 1, new ItemStack(Items.COOKED_MUTTON), gameTime)) {
            return "cooked_mutton";
        }
        return "-";
    }

    private String tryMerchantRecipe(TekVillageEconomy economy, Long gameTime) {
        if (this.tryStorageRecipe(economy, "merchant", Items.BOOK, 2, new ItemStack(Items.EMERALD, 3), gameTime)) {
            return "sold_books";
        }
        if (this.tryStorageRecipe(economy, "merchant", Items.PAPER, 16, new ItemStack(Items.EMERALD, 3), gameTime)) {
            return "sold_paper";
        }
        if (this.tryStorageRecipe(economy, "merchant", Items.COOKED_BEEF, 4, new ItemStack(Items.EMERALD, 2), gameTime)) {
            return "sold_cooked_beef";
        }
        if (this.tryStorageRecipe(economy, "merchant", Items.COOKED_PORKCHOP, 4, new ItemStack(Items.EMERALD, 2), gameTime)) {
            return "sold_cooked_pork";
        }
        if (this.tryStorageRecipe(economy, "merchant", Items.WHITE_WOOL, 8, new ItemStack(Items.EMERALD, 2), gameTime)) {
            return "sold_wool";
        }
        if (this.tryStorageRecipe(economy, "merchant", Items.EMERALD, 1, new ItemStack(Items.IRON_INGOT, 3), gameTime)) {
            return "traded_iron";
        }
        if (this.tryStorageRecipe(economy, "merchant", Items.BREAD, 4, new ItemStack(Items.EMERALD), gameTime)) {
            return "sold_bread";
        }
        return "-";
    }

    private String tryNomadRecipe(TekVillageEconomy economy, Long gameTime) {
        if (economy.countItem(Items.BREAD) < 8 && economy.insert(new ItemStack(Items.BREAD, 4))) {
            return "gift_bread";
        }
        if (economy.countItem(Items.OAK_LOG) < 8 && economy.insert(new ItemStack(Items.OAK_LOG, 4))) {
            return "gift_logs";
        }
        return "-";
    }

    private boolean tryStorageRecipe(TekVillageEconomy economy, String owner, Item input, int inputCount, ItemStack output, long gameTime) {
        Map<Item, Integer> inputs = new HashMap<>();
        inputs.put(input, inputCount);
        return economy.craftWithReservedInputs(owner, inputs, output, gameTime);
    }

    private void setWorkerResult(CompoundNBT data, String mode, String result, long nextGameTime) {
        data.putString(WORKER_MODE_TAG, mode);
        data.putString(WORKER_LAST_RESULT_TAG, result);
        data.putLong(WORKER_COOLDOWN_TAG, nextGameTime);
    }

    private boolean tryCraftArmorFromStorage(ArmorRecipe recipe, TekVillageEconomy economy) {
        if (recipe == null || !this.canCraftRecipe(economy, recipe)) {
            return false;
        }
        Map<Item, Integer> inputs = new HashMap<>();
        inputs.put(recipe.input, recipe.inputCount);
        return economy.craftWithReservedInputs("blacksmith", inputs, recipe.createOutputStack(), 0L);
    }

    private void collectFarmerDrops(CompoundNBT data, List<ItemStack> drops) {
        if (drops.isEmpty()) {
            return;
        }
        CompoundNBT carry = data.contains(FARMER_CARRY_TAG, 10) ? data.getCompound(FARMER_CARRY_TAG).copy() : new CompoundNBT();
        for (ItemStack stack : drops) {
            if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                continue;
            }
            String key = Integer.toString(Item.getId(stack.getItem()));
            int current = carry.getInt(key);
            carry.putInt(key, current + stack.getCount());
        }
        data.put(FARMER_CARRY_TAG, carry);
    }

    private void deliverFarmerCarry(CompoundNBT data, TekVillageEconomy economy) {
        if (!data.contains(FARMER_CARRY_TAG, 10)) {
            return;
        }
        CompoundNBT carry = data.getCompound(FARMER_CARRY_TAG).copy();
        List<String> keys = new java.util.ArrayList<>(carry.getAllKeys());
        java.util.Collections.sort(keys);
        for (String key : keys) {
            int count = carry.getInt(key);
            if (count <= 0) {
                carry.remove(key);
                continue;
            }
            int itemId;
            try {
                itemId = Integer.parseInt(key);
            } catch (NumberFormatException ignored) {
                carry.remove(key);
                continue;
            }
            Item item = Item.byId(itemId);
            if (item == null || item == Items.AIR) {
                carry.remove(key);
                continue;
            }
            int remaining = count;
            while (remaining > 0) {
                int move = Math.min(64, remaining);
                ItemStack stack = new ItemStack(item, move);
                TekItemMeta.markVillagerItem(stack);
                if (!economy.insert(stack)) {
                    break;
                }
                remaining -= move;
            }
            if (remaining <= 0) {
                carry.remove(key);
            } else {
                carry.putInt(key, remaining);
            }
        }
        if (carry.isEmpty()) {
            data.remove(FARMER_CARRY_TAG);
        } else {
            data.put(FARMER_CARRY_TAG, carry);
        }
    }

    private boolean hasFarmerCarry(CompoundNBT data) {
        if (!data.contains(FARMER_CARRY_TAG, 10)) {
            return false;
        }
        CompoundNBT carry = data.getCompound(FARMER_CARRY_TAG);
        for (String key : carry.getAllKeys()) {
            if (carry.getInt(key) > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean advanceFarmerStuckTracker(TekFarmerEntity farmer, CompoundNBT data) {
        long currentPos = farmer.blockPosition().asLong();
        long lastPos = data.getLong(FARMER_LAST_POS_TAG);
        if (lastPos == currentPos) {
            int stuckTicks = data.getInt(FARMER_STUCK_TICKS_TAG) + 1;
            data.putInt(FARMER_STUCK_TICKS_TAG, stuckTicks);
            return stuckTicks >= FARMER_STUCK_LIMIT;
        }
        data.putLong(FARMER_LAST_POS_TAG, currentPos);
        data.putInt(FARMER_STUCK_TICKS_TAG, 0);
        return false;
    }

    private void resetFarmerStuckTracker(CompoundNBT data) {
        data.remove(FARMER_LAST_POS_TAG);
        data.remove(FARMER_STUCK_TICKS_TAG);
    }

    private void syncVillageResidents(TekVillage village, List<TekVillagerEntity> villagers) {
        Set<UUID> seen = new HashSet<>();
        for (TekVillagerEntity villager : villagers) {
            if (!village.contains(villager.blockPosition())) {
                continue;
            }
            UUID id = villager.getUUID();
            seen.add(id);
            village.addResident(id);
        }
        Set<UUID> stale = new HashSet<>(village.getResidents());
        stale.removeAll(seen);
        for (UUID staleResident : stale) {
            village.removeResident(staleResident);
        }
    }

    private void syncProfessionCounts(TekVillage village, List<TekVillagerEntity> villagers) {
        Map<String, Integer> counts = new HashMap<>();
        for (TekVillagerEntity villager : villagers) {
            if (!village.contains(villager.blockPosition())) {
                continue;
            }
            counts.merge(villager.getProfessionType().getSerializedName(), 1, Integer::sum);
        }
        village.clearProfessionCounts();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            village.setProfessionCount(entry.getKey(), entry.getValue());
        }
    }

    private Optional<BlockPos> resolveRetreatPos(TekVillageStructureManager structureManager) {
        if (structureManager == null) {
            return Optional.empty();
        }
        TekVillageStructure townHall = structureManager.getStructure(TekStructureType.TOWNHALL).orElse(null);
        if (townHall == null || townHall.getSafeSpot() == null) {
            return Optional.empty();
        }
        return Optional.of(townHall.getSafeSpot());
    }

    private boolean isLowHealth(TekGuardEntity guard) {
        return guard.getHealth() <= guard.getMaxHealth() * GUARD_RETREAT_HEALTH_RATIO;
    }

    private void ensureCaptainGuard(TekVillageStructureManager structureManager, List<TekGuardEntity> guards) {
        if (guards.isEmpty()) {
            return;
        }
        TekVillageStructure barracks = structureManager == null ? null : structureManager.getStructure(TekStructureType.BARRACKS).orElse(null);
        if (barracks == null || !barracks.isValid()) {
            for (TekGuardEntity guard : guards) {
                if (guard.isCaptain()) {
                    guard.setCaptain(false);
                }
            }
            return;
        }
        TekGuardEntity currentCaptain = guards.stream()
                .filter(TekGuardEntity::isCaptain)
                .findFirst()
                .orElse(null);
        if (currentCaptain != null) {
            return;
        }
        TekGuardEntity best = guards.stream()
                .max(Comparator.comparingInt(guard -> guard.scoreWeapon(guard.getMainHandItem()) + guard.getSkill(guard.getProfessionType())))
                .orElse(guards.get(0));
        best.setCaptain(true);
        best.setHappy(best.getHappy() + 10);
    }

    private void tickCaptainAura(ServerWorld level, List<TekGuardEntity> guards) {
        if (level.getGameTime() % CAPTAIN_AURA_INTERVAL != 0L) {
            return;
        }
        for (TekGuardEntity guard : guards) {
            if (!guard.isCaptain() || !guard.isAlive()) {
                continue;
            }
            TekCaptainAuraEntity aura = TekEntities.TEK_CAPTAIN_AURA.get().create(level);
            if (aura == null) {
                return;
            }
            this.placeVillageEntity(level, aura, guard.blockPosition());
            return;
        }
    }

    private void tickGuardArmory(List<TekGuardEntity> guards, TekVillageEconomy economy) {
        List<ItemStack> snapshot = economy.snapshotStacks();
        for (TekGuardEntity guard : guards) {
            GuardUpgradeChoice best = this.selectBestGuardUpgrade(guard, snapshot);
            if (best == null) {
                continue;
            }
            ItemStack upgraded = best.stack.copy();
            upgraded.setCount(1);
            if (!economy.extractOne(upgraded)) {
                continue;
            }
            ItemStack old = guard.getItemBySlot(best.slot).copy();
            guard.setItemSlot(best.slot, upgraded);
            if (!old.isEmpty()) {
                economy.insert(old);
            }
            snapshot = economy.snapshotStacks();
        }
    }

    private GuardUpgradeChoice selectBestGuardUpgrade(TekGuardEntity guard, List<ItemStack> stacks) {
        GuardUpgradeChoice best = null;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(stack);
            int candidateScore;
            int currentScore;
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                candidateScore = guard.scoreArmor(stack, slot);
                currentScore = guard.scoreArmor(guard.getItemBySlot(slot), slot);
            } else if (slot == EquipmentSlotType.MAINHAND) {
                candidateScore = guard.scoreWeapon(stack);
                currentScore = guard.scoreWeapon(guard.getMainHandItem());
            } else {
                continue;
            }
            if (candidateScore < 0 || candidateScore <= currentScore) {
                continue;
            }
            GuardUpgradeChoice candidate = new GuardUpgradeChoice(slot, stack, candidateScore - currentScore, candidateScore);
            if (best == null || this.compareGuardUpgrades(candidate, best) > 0) {
                best = candidate;
            }
        }
        return best;
    }

    private int compareGuardUpgrades(GuardUpgradeChoice a, GuardUpgradeChoice b) {
        int byDelta = Integer.compare(a.deltaScore, b.deltaScore);
        if (byDelta != 0) {
            return byDelta;
        }
        int byCandidate = Integer.compare(a.candidateScore, b.candidateScore);
        if (byCandidate != 0) {
            return byCandidate;
        }
        String aName = a.stack.getItem().getRegistryName() == null ? a.stack.getItem().toString() : a.stack.getItem().getRegistryName().toString();
        String bName = b.stack.getItem().getRegistryName() == null ? b.stack.getItem().toString() : b.stack.getItem().getRegistryName().toString();
        int byName = bName.compareTo(aName);
        if (byName != 0) {
            return byName;
        }
        return Integer.compare(a.slot.ordinal(), b.slot.ordinal());
    }

    private BlacksmithDemand computeBlacksmithDemand(ServerWorld level, TekVillage village, TekVillageEconomy economy) {
        Map<TekVillageEconomy.ArmorClass, Integer> demand = new EnumMap<>(TekVillageEconomy.ArmorClass.class);
        for (TekVillageEconomy.ArmorClass armorClass : TekVillageEconomy.ArmorClass.values()) {
            demand.put(armorClass, 0);
        }

        List<TekGuardEntity> guards = level.getEntitiesOfClass(
                TekGuardEntity.class,
                village.getBounds().inflate(12.0D, 4.0D, 12.0D),
                guard -> guard != null && guard.isAlive()
        );
        for (TekGuardEntity guard : guards) {
            for (TekVillageEconomy.ArmorClass armorClass : TekVillageEconomy.ArmorClass.values()) {
                if (this.guardNeedsUpgradeForClass(guard, armorClass)) {
                    demand.put(armorClass, demand.get(armorClass) + 1);
                }
            }
        }

        Map<TekVillageEconomy.ArmorClass, Integer> stock = economy.countArmorByClass(ARMOR_CLASSIFIER);
        Map<TekVillageEconomy.ArmorClass, Integer> deficit = new EnumMap<>(TekVillageEconomy.ArmorClass.class);
        for (TekVillageEconomy.ArmorClass armorClass : TekVillageEconomy.ArmorClass.values()) {
            int needed = demand.get(armorClass);
            int available = stock.getOrDefault(armorClass, 0);
            deficit.put(armorClass, Math.max(0, needed - available));
        }

        return new BlacksmithDemand(demand, stock, deficit);
    }

    private boolean guardNeedsUpgradeForClass(TekGuardEntity guard, TekVillageEconomy.ArmorClass armorClass) {
        EquipmentSlotType slot = toEquipmentSlot(armorClass);
        int equippedScore = guard.scoreArmor(guard.getItemBySlot(slot), slot);
        int bestCandidate = -1;
        for (ArmorRecipe recipe : BLACKSMITH_ARMOR_RECIPES) {
            if (recipe.armorClass != armorClass) {
                continue;
            }
            int candidateScore = guard.scoreArmor(recipe.createOutputStack(), slot);
            if (candidateScore > bestCandidate) {
                bestCandidate = candidateScore;
            }
        }
        return bestCandidate > equippedScore;
    }

    private ArmorRecipe selectBlacksmithRecipe(TekVillageEconomy economy, BlacksmithDemand demand, TekBlacksmithEntity blacksmith) {
        List<TekVillageEconomy.ArmorClass> byDeficit = new java.util.ArrayList<>();
        byDeficit.add(TekVillageEconomy.ArmorClass.CHESTPLATE);
        byDeficit.add(TekVillageEconomy.ArmorClass.LEGGINGS);
        byDeficit.add(TekVillageEconomy.ArmorClass.HELMET);
        byDeficit.add(TekVillageEconomy.ArmorClass.BOOTS);
        byDeficit.sort((a, b) -> Integer.compare(
                demand.deficitByClass.getOrDefault(b, 0),
                demand.deficitByClass.getOrDefault(a, 0)
        ));

        for (TekVillageEconomy.ArmorClass armorClass : byDeficit) {
            if (demand.deficitByClass.getOrDefault(armorClass, 0) <= 0) {
                continue;
            }
            ArmorRecipe best = this.pickBestCraftableRecipeForClass(economy, armorClass);
            if (best != null) {
                return best;
            }
        }

        int baseIndex = Math.floorMod(blacksmith.getUUID().hashCode(), BLACKSMITH_ARMOR_RECIPES.length);
        for (int i = 0; i < BLACKSMITH_ARMOR_RECIPES.length; i++) {
            ArmorRecipe recipe = BLACKSMITH_ARMOR_RECIPES[(baseIndex + i) % BLACKSMITH_ARMOR_RECIPES.length];
            if (this.canCraftRecipe(economy, recipe)) {
                return recipe;
            }
        }
        return null;
    }

    private ArmorRecipe pickBestCraftableRecipeForClass(TekVillageEconomy economy, TekVillageEconomy.ArmorClass armorClass) {
        ArmorRecipe best = null;
        for (ArmorRecipe recipe : BLACKSMITH_ARMOR_RECIPES) {
            if (recipe.armorClass != armorClass || !this.canCraftRecipe(economy, recipe)) {
                continue;
            }
            if (best == null || recipe.priority > best.priority) {
                best = recipe;
            }
        }
        return best;
    }

    private boolean canCraftRecipe(TekVillageEconomy economy, ArmorRecipe recipe) {
        return economy.countAvailableItem(recipe.input, 0L) >= recipe.inputCount && economy.canInsert(recipe.createOutputStack());
    }

    private void writeBlacksmithDebugData(TekBlacksmithEntity blacksmith, BlacksmithDemand demand, TekVillageEconomy economy) {
        CompoundNBT data = blacksmith.getPersistentData();
        CompoundNBT demandTag = new CompoundNBT();
        for (TekVillageEconomy.ArmorClass armorClass : TekVillageEconomy.ArmorClass.values()) {
            String key = armorClass.name().toLowerCase();
            demandTag.putInt("need_" + key, demand.demandByClass.getOrDefault(armorClass, 0));
            demandTag.putInt("stock_" + key, demand.stockByClass.getOrDefault(armorClass, 0));
            demandTag.putInt("deficit_" + key, demand.deficitByClass.getOrDefault(armorClass, 0));
        }
        data.put(BLACKSMITH_DEMAND_TAG, demandTag);

        List<String> blocked = new java.util.ArrayList<>();
        for (TekVillageEconomy.ArmorClass armorClass : TekVillageEconomy.ArmorClass.values()) {
            if (demand.deficitByClass.getOrDefault(armorClass, 0) <= 0) {
                continue;
            }
            if (this.pickBestCraftableRecipeForClass(economy, armorClass) == null) {
                blocked.add(armorClass.name().toLowerCase());
            }
        }
        data.putString(BLACKSMITH_MISSING_TAG, blocked.isEmpty() ? "-" : String.join(",", blocked));
    }

    private static Map<Item, TekVillageEconomy.ArmorClass> buildArmorClassifier() {
        Map<Item, TekVillageEconomy.ArmorClass> map = new HashMap<>();
        for (ArmorRecipe recipe : BLACKSMITH_ARMOR_RECIPES) {
            map.put(recipe.output, recipe.armorClass);
        }
        return map;
    }

    private static EquipmentSlotType toEquipmentSlot(TekVillageEconomy.ArmorClass armorClass) {
        switch (armorClass) {
            case HELMET:
                return EquipmentSlotType.HEAD;
            case CHESTPLATE:
                return EquipmentSlotType.CHEST;
            case LEGGINGS:
                return EquipmentSlotType.LEGS;
            case BOOTS:
            default:
                return EquipmentSlotType.FEET;
        }
    }

    private boolean isVillageHostile(MonsterEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        boolean hybridRaids = entity.level instanceof ServerWorld && TekGameRules.hybridRaids((ServerWorld) entity.level);
        if (entity instanceof ZombieEntity && !(entity instanceof ZombifiedPiglinEntity)
                || entity instanceof WitherSkeletonEntity
                || entity instanceof EvokerEntity
                || entity instanceof VexEntity
                || entity instanceof VindicatorEntity
                || hybridRaids && (entity instanceof PillagerEntity
                        || entity instanceof RavagerEntity
                        || entity instanceof WitchEntity)) {
            return true;
        }
        if (entity.getType().getRegistryName() == null) {
            return false;
        }
        if (!"tektopia".equals(entity.getType().getRegistryName().getNamespace())) {
            return false;
        }
        String path = entity.getType().getRegistryName().getPath();
        return path.contains("necromancer") || path.contains("spirit_skull") || path.contains("death_cloud");
    }

    private double scoreThreatForGuard(MonsterEntity hostile, TekGuardEntity guard) {
        double score = 1000.0D - Math.min(900.0D, hostile.distanceToSqr(guard));
        if (hostile instanceof RavagerEntity) {
            score += 700.0D;
        } else if (hostile instanceof EvokerEntity || hostile instanceof WitchEntity) {
            score += 500.0D;
        } else if (hostile instanceof VexEntity) {
            score += 300.0D;
        } else if (hostile instanceof PillagerEntity || hostile instanceof VindicatorEntity) {
            score += 250.0D;
        }
        if (hostile.getType().getRegistryName() != null && "tektopia".equals(hostile.getType().getRegistryName().getNamespace())) {
            String path = hostile.getType().getRegistryName().getPath();
            if (path.contains("necromancer")) {
                score += 900.0D;
            } else if (path.contains("spirit_skull")) {
                score += 450.0D;
            } else if (path.contains("death_cloud")) {
                score += 350.0D;
            }
        }
        if (guard.isCaptain()) {
            score += 50.0D;
        }
        return score;
    }

    private static final class SeedPlan {
        private final Item seedItem;
        private final Block cropBlock;

        private SeedPlan(Item seedItem, Block cropBlock) {
            this.seedItem = seedItem;
            this.cropBlock = cropBlock;
        }
    }

    private static final class ArmorRecipe {
        private final Item output;
        private final Item input;
        private final int inputCount;
        private final TekVillageEconomy.ArmorClass armorClass;
        private final int priority;

        private ArmorRecipe(Item output, Item input, int inputCount) {
            this.output = output;
            this.input = input;
            this.inputCount = inputCount;
            this.armorClass = classifyArmor(output);
            this.priority = scoreArmorPriority(output);
        }

        private ItemStack createOutputStack() {
            return new ItemStack(this.output);
        }

        private static TekVillageEconomy.ArmorClass classifyArmor(Item item) {
            if (item == Items.IRON_HELMET || item == Items.GOLDEN_HELMET || item == Items.DIAMOND_HELMET) {
                return TekVillageEconomy.ArmorClass.HELMET;
            }
            if (item == Items.IRON_CHESTPLATE || item == Items.GOLDEN_CHESTPLATE || item == Items.DIAMOND_CHESTPLATE) {
                return TekVillageEconomy.ArmorClass.CHESTPLATE;
            }
            if (item == Items.IRON_LEGGINGS || item == Items.GOLDEN_LEGGINGS || item == Items.DIAMOND_LEGGINGS) {
                return TekVillageEconomy.ArmorClass.LEGGINGS;
            }
            return TekVillageEconomy.ArmorClass.BOOTS;
        }

        private static int scoreArmorPriority(Item item) {
            if (item == Items.DIAMOND_HELMET || item == Items.DIAMOND_CHESTPLATE || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_BOOTS) {
                return 300;
            }
            if (item == Items.IRON_HELMET || item == Items.IRON_CHESTPLATE || item == Items.IRON_LEGGINGS || item == Items.IRON_BOOTS) {
                return 200;
            }
            return 100;
        }
    }

    private static final class BlacksmithDemand {
        private final Map<TekVillageEconomy.ArmorClass, Integer> demandByClass;
        private final Map<TekVillageEconomy.ArmorClass, Integer> stockByClass;
        private final Map<TekVillageEconomy.ArmorClass, Integer> deficitByClass;

        private BlacksmithDemand(
                Map<TekVillageEconomy.ArmorClass, Integer> demandByClass,
                Map<TekVillageEconomy.ArmorClass, Integer> stockByClass,
                Map<TekVillageEconomy.ArmorClass, Integer> deficitByClass
        ) {
            this.demandByClass = demandByClass;
            this.stockByClass = stockByClass;
            this.deficitByClass = deficitByClass;
        }
    }

    private static final class GuardUpgradeChoice {
        private final EquipmentSlotType slot;
        private final ItemStack stack;
        private final int deltaScore;
        private final int candidateScore;

        private GuardUpgradeChoice(EquipmentSlotType slot, ItemStack stack, int deltaScore, int candidateScore) {
            this.slot = slot;
            this.stack = stack;
            this.deltaScore = deltaScore;
            this.candidateScore = candidateScore;
        }
    }
}
