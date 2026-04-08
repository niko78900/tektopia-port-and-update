package net.tangotek.tektopia.village;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.entities.TekBlacksmithEntity;
import net.tangotek.tektopia.entities.TekFarmerEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
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
    private static final int FARMER_SCAN_RADIUS = 12;
    private static final long FARMER_RETRY_COOLDOWN = 40L;
    private static final long FARMER_WORK_COOLDOWN = 20L;
    private static final long FARMER_PATH_STEP_COOLDOWN = 10L;
    private static final int FARMER_STUCK_LIMIT = 80;
    private static final long BLACKSMITH_RETRY_COOLDOWN = 60L;
    private static final long BLACKSMITH_WORK_COOLDOWN = 100L;
    private static final ArmorRecipe[] BLACKSMITH_ARMOR_RECIPES = new ArmorRecipe[] {
            new ArmorRecipe(Items.IRON_CHESTPLATE, Items.IRON_INGOT, 8),
            new ArmorRecipe(Items.IRON_LEGGINGS, Items.IRON_INGOT, 7),
            new ArmorRecipe(Items.IRON_HELMET, Items.IRON_INGOT, 5),
            new ArmorRecipe(Items.IRON_BOOTS, Items.IRON_INGOT, 4),
            new ArmorRecipe(Items.GOLDEN_CHESTPLATE, Items.GOLD_INGOT, 8),
            new ArmorRecipe(Items.GOLDEN_LEGGINGS, Items.GOLD_INGOT, 7),
            new ArmorRecipe(Items.GOLDEN_HELMET, Items.GOLD_INGOT, 5),
            new ArmorRecipe(Items.GOLDEN_BOOTS, Items.GOLD_INGOT, 4)
    };

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

            List<TekGuardEntity> guards = level.getEntitiesOfClass(
                    TekGuardEntity.class,
                    engagementBounds,
                    guard -> guard != null && guard.isAlive()
            );

            if (hostiles.isEmpty()) {
                if (level.isDay()) {
                    for (TekGuardEntity guard : guards) {
                        if (guard.distanceToSqr(
                                retreatPos.getX() + 0.5D,
                                retreatPos.getY(),
                                retreatPos.getZ() + 0.5D
                        ) > 16.0D) {
                            guard.getNavigation().moveTo(
                                    retreatPos.getX() + 0.5D,
                                    retreatPos.getY(),
                                    retreatPos.getZ() + 0.5D,
                                    1.05D
                            );
                        }
                    }
                    this.tickCivilianWork(level, village, structureManager, villagers);
                } else {
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

        boolean crafted = this.tryCraftArmorFromStorage(level, blacksmith, economy);
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

    private boolean tryCraftArmorFromStorage(ServerWorld level, TekBlacksmithEntity blacksmith, TekVillageEconomy economy) {
        int baseIndex = Math.floorMod((int) (level.getGameTime() / 20L) + blacksmith.getUUID().hashCode(), BLACKSMITH_ARMOR_RECIPES.length);
        for (int offset = 0; offset < BLACKSMITH_ARMOR_RECIPES.length; offset++) {
            ArmorRecipe recipe = BLACKSMITH_ARMOR_RECIPES[(baseIndex + offset) % BLACKSMITH_ARMOR_RECIPES.length];
            if (economy.countItem(recipe.input) < recipe.inputCount) {
                continue;
            }
            Map<Item, Integer> inputs = new HashMap<>();
            inputs.put(recipe.input, recipe.inputCount);
            if (economy.craftWithInputs(inputs, recipe.createOutputStack())) {
                return true;
            }
        }
        return false;
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

        private ArmorRecipe(Item output, Item input, int inputCount) {
            this.output = output;
            this.input = input;
            this.inputCount = inputCount;
        }

        private ItemStack createOutputStack() {
            return new ItemStack(this.output);
        }
    }
}
