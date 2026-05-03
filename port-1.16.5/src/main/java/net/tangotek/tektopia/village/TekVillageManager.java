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
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
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
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.entities.TekBlacksmithEntity;
import net.tangotek.tektopia.entities.TekButcherEntity;
import net.tangotek.tektopia.entities.TekChefEntity;
import net.tangotek.tektopia.entities.TekFarmerEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekLumberjackEntity;
import net.tangotek.tektopia.entities.TekMerchantEntity;
import net.tangotek.tektopia.entities.TekMinerEntity;
import net.tangotek.tektopia.entities.TekNomadEntity;
import net.tangotek.tektopia.entities.TekRancherEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.common.TekWorkerStatus;
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
    private static final long GUARD_ARMORY_TICK_INTERVAL = 40L;
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

            List<MonsterEntity> hostiles = level.getEntitiesOfClass(
                    MonsterEntity.class,
                    engagementBounds,
                    this::isVillageHostile
            );
            village.setLastKnownHostileCount(hostiles.size());
            BlockPos retreatPos = this.resolveRetreatPos(structureManager).orElse(village.getCenter());
            BlockPos alertPos = village.getLastAlertPos() != null ? village.getLastAlertPos() : village.getCenter();

            List<TekGuardEntity> guards = level.getEntitiesOfClass(
                    TekGuardEntity.class,
                    engagementBounds,
                    guard -> guard != null && guard.isAlive()
            );

            if (hostiles.isEmpty()) {
                boolean alertActive = village.hasActiveAlert(level.getGameTime(), ALERT_MEMORY_TICKS);
                if (alertActive) {
                    for (TekGuardEntity guard : guards) {
                        if (guard.distanceToSqr(
                                alertPos.getX() + 0.5D,
                                alertPos.getY(),
                                alertPos.getZ() + 0.5D
                        ) > 4.0D) {
                            guard.getNavigation().moveTo(
                                    alertPos.getX() + 0.5D,
                                    alertPos.getY(),
                                    alertPos.getZ() + 0.5D,
                                    1.15D
                            );
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
                            guard.getNavigation().moveTo(
                                    guardIdlePos.getX() + 0.5D,
                                    guardIdlePos.getY(),
                                    guardIdlePos.getZ() + 0.5D,
                                    1.05D
                            );
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

            for (TekGuardEntity guard : guards) {
                if (this.isLowHealth(guard)) {
                    guard.setTarget(null);
                    guard.getNavigation().moveTo(
                            retreatPos.getX() + 0.5D,
                            retreatPos.getY(),
                            retreatPos.getZ() + 0.5D,
                            1.20D
                    );
                    continue;
                }
                LivingEntity nearestHostile = hostiles.stream()
                        .min(Comparator.comparingDouble(h -> h.distanceToSqr(guard)))
                        .orElse(null);
                if (nearestHostile == null) {
                    continue;
                }
                guard.setTarget(nearestHostile);
                guard.getNavigation().moveTo(nearestHostile, 1.15D);
            }

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
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.RANCH_PEN, "rancher", WORKER_WORK_COOLDOWN, this::tryRancherRecipe);
                continue;
            }
            if (villager instanceof TekButcherEntity) {
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.BUTCHER, "butcher", WORKER_WORK_COOLDOWN, this::tryButcherRecipe);
                continue;
            }
            if (villager instanceof TekMerchantEntity) {
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.MERCHANT_STALL, "merchant", MERCHANT_WORK_COOLDOWN, this::tryMerchantRecipe);
                continue;
            }
            if (villager instanceof TekNomadEntity) {
                this.tickStorageCrafter(level, village, structureManager, villager, TekStructureType.TOWNHALL, "nomad", NOMAD_WORK_COOLDOWN, this::tryNomadRecipe);
            }
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
            data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_RETRY_COOLDOWN);
            data.remove(FARMER_TARGET_POS_TAG);
            return;
        }

        data.putLong(FARMER_TARGET_POS_TAG, target.asLong());
        farmer.getNavigation().moveTo(
                target.getX() + 0.5D,
                target.getY(),
                target.getZ() + 0.5D,
                1.0D
        );

        if (farmer.distanceToSqr(
                target.getX() + 0.5D,
                target.getY(),
                target.getZ() + 0.5D
        ) > 4.0D) {
            if (this.advanceFarmerStuckTracker(farmer, data)) {
                data.remove(FARMER_TARGET_POS_TAG);
                data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_RETRY_COOLDOWN);
                return;
            }
            data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_PATH_STEP_COOLDOWN);
            return;
        }

        this.resetFarmerStuckTracker(data);
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
        farmer.getNavigation().moveTo(
                deliveryPos.getX() + 0.5D,
                deliveryPos.getY(),
                deliveryPos.getZ() + 0.5D,
                1.0D
        );

        if (farmer.distanceToSqr(
                deliveryPos.getX() + 0.5D,
                deliveryPos.getY(),
                deliveryPos.getZ() + 0.5D
        ) > 9.0D) {
            if (this.advanceFarmerStuckTracker(farmer, data)) {
                data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_RETRY_COOLDOWN);
                return;
            }
            data.putLong(FARMER_COOLDOWN_TAG, gameTime + FARMER_PATH_STEP_COOLDOWN);
            return;
        }

        this.resetFarmerStuckTracker(data);
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
        if (blacksmith.distanceToSqr(
                workPos.getX() + 0.5D,
                workPos.getY(),
                workPos.getZ() + 0.5D
        ) > 9.0D) {
            blacksmith.getNavigation().moveTo(
                    workPos.getX() + 0.5D,
                    workPos.getY(),
                    workPos.getZ() + 0.5D,
                    1.0D
            );
            data.putLong(BLACKSMITH_COOLDOWN_TAG, gameTime + 15L);
            return;
        }

        ArmorRecipe planned = this.selectBlacksmithRecipe(economy, demand, blacksmith);
        data.putString(BLACKSMITH_PLAN_TAG, planned == null ? "-" : planned.output.getRegistryName() != null ? planned.output.getRegistryName().toString() : planned.output.toString());
        boolean crafted = this.tryCraftArmorFromStorage(planned, economy);
        data.putLong(BLACKSMITH_COOLDOWN_TAG, gameTime + (crafted ? BLACKSMITH_WORK_COOLDOWN : BLACKSMITH_RETRY_COOLDOWN));
    }

    private BlockPos readFarmerTarget(CompoundNBT data) {
        if (!data.contains(FARMER_TARGET_POS_TAG, 4)) {
            return null;
        }
        return BlockPos.of(data.getLong(FARMER_TARGET_POS_TAG));
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
        worker.getNavigation().moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, 1.0D);
        if (worker.distanceToSqr(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D) > 9.0D) {
            data.putLong(WORKER_COOLDOWN_TAG, gameTime + WORKER_PATH_STEP_COOLDOWN);
            return;
        }

        List<ItemStack> drops = Block.getDrops(level.getBlockState(target), level, target, null);
        if (!this.canInsertAll(economy, drops)) {
            this.setWorkerResult(data, mode, "storage_full", gameTime + WORKER_RETRY_COOLDOWN);
            return;
        }
        level.destroyBlock(target, false);
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
        worker.setWorkerStatus(TekWorkerStatus.MOVING);
        worker.getNavigation().moveTo(workPos.getX() + 0.5D, workPos.getY(), workPos.getZ() + 0.5D, 1.0D);
        if (worker.distanceToSqr(workPos.getX() + 0.5D, workPos.getY(), workPos.getZ() + 0.5D) > 9.0D) {
            data.putLong(WORKER_COOLDOWN_TAG, gameTime + WORKER_PATH_STEP_COOLDOWN);
            return;
        }

        worker.setWorkerStatus(TekWorkerStatus.WORKING);
        String result = recipe.apply(economy, gameTime);
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
        if (entity instanceof ZombieEntity && !(entity instanceof ZombifiedPiglinEntity)
                || entity instanceof WitherSkeletonEntity
                || entity instanceof EvokerEntity
                || entity instanceof VexEntity
                || entity instanceof VindicatorEntity) {
            return true;
        }
        if (entity.getType().getRegistryName() == null) {
            return false;
        }
        return "tektopia".equals(entity.getType().getRegistryName().getNamespace())
                && entity.getType().getRegistryName().getPath().contains("necromancer");
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
