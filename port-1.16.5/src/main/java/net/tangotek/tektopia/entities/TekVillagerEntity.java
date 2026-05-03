package net.tangotek.tektopia.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.common.ProfessionType;
import net.tangotek.tektopia.common.TekWorkerStatus;

public class TekVillagerEntity extends CreatureEntity {
    private static final DataParameter<CompoundNBT> AI_FILTERS_DATA =
            EntityDataManager.defineId(TekVillagerEntity.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<CompoundNBT> CORE_DATA =
            EntityDataManager.defineId(TekVillagerEntity.class, DataSerializers.COMPOUND_TAG);

    private static final String HUNGER_TAG = "hunger";
    private static final String HAPPY_TAG = "happy";
    private static final String INTELLIGENCE_TAG = "intelligence";
    private static final String DAYS_ALIVE_TAG = "daysAlive";
    private static final String PROFESSION_TAG = "profession";
    private static final String SKILLS_TAG = "skills";
    private static final String SLEEPING_TAG = "sleeping";
    private static final String SITTING_TAG = "sitting";
    private static final String HOME_TAG = "home";
    private static final String BED_TAG = "bed";
    private static final String THOUGHT_TAG = "thought";
    private static final String ITEM_THOUGHT_TAG = "itemThought";
    private static final String WORK_STATUS_TAG = "workStatus";
    private static final String LAST_DAY_TICK_TAG = "tek_last_day_tick";
    private static final String LAST_HUNGER_TICK_TAG = "tek_last_hunger_tick";
    private static final String INVENTORY_TAG = "villagerInventory";
    private static final int INVENTORY_SIZE = 27;
    private static final int MAX_STAT = 100;
    private static final int SLEEP_START_TIME = 16000;
    private static final int SLEEP_END_TIME = 24000;
    private static final int WORK_START_TIME = 500;
    private static final int WORK_END_TIME = 12000;

    private final Map<String, Boolean> filterDefaults = new LinkedHashMap<>();
    private final NonNullList<ItemStack> villagerInventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    protected TekVillagerEntity(EntityType<? extends CreatureEntity> type, World level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AI_FILTERS_DATA, new CompoundNBT());
        this.entityData.define(CORE_DATA, this.createDefaultCoreData());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            return;
        }
        this.tickCoreVillageState();
    }

    protected void setInitialProfession(ProfessionType professionType) {
        if (this.getProfessionType() == ProfessionType.UNKNOWN) {
            this.setProfessionType(professionType);
            if (professionType != ProfessionType.UNKNOWN && this.getSkill(professionType) < 1) {
                this.setSkill(professionType, 1);
            }
        }
    }

    public ProfessionType getProfessionType() {
        return ProfessionType.fromSerializedName(this.coreData().getString(PROFESSION_TAG));
    }

    public void setProfessionType(ProfessionType professionType) {
        this.updateCoreData(data -> data.putString(PROFESSION_TAG, professionType.getSerializedName()));
    }

    public int getHunger() {
        return this.coreData().getInt(HUNGER_TAG);
    }

    public void setHunger(int hunger) {
        this.updateCoreData(data -> data.putInt(HUNGER_TAG, clampStat(hunger)));
    }

    public int getHappy() {
        return this.coreData().getInt(HAPPY_TAG);
    }

    public void setHappy(int happy) {
        this.updateCoreData(data -> data.putInt(HAPPY_TAG, clampStat(happy)));
    }

    public int getIntelligence() {
        return this.coreData().getInt(INTELLIGENCE_TAG);
    }

    public void setIntelligence(int intelligence) {
        this.updateCoreData(data -> data.putInt(INTELLIGENCE_TAG, clampStat(intelligence)));
    }

    public int getDaysAlive() {
        return Math.max(0, this.coreData().getInt(DAYS_ALIVE_TAG));
    }

    public void setDaysAlive(int daysAlive) {
        this.updateCoreData(data -> data.putInt(DAYS_ALIVE_TAG, Math.max(0, daysAlive)));
    }

    public int getSkill(ProfessionType professionType) {
        CompoundNBT skills = this.coreData().getCompound(SKILLS_TAG);
        return Math.max(0, skills.getInt(professionType.getSerializedName()));
    }

    public void setSkill(ProfessionType professionType, int value) {
        this.updateCoreData(data -> {
            CompoundNBT skills = data.getCompound(SKILLS_TAG);
            skills.putInt(professionType.getSerializedName(), Math.max(0, Math.min(100, value)));
            data.put(SKILLS_TAG, skills);
        });
    }

    public void addSkill(ProfessionType professionType, int amount) {
        if (amount <= 0) {
            return;
        }
        this.setSkill(professionType, this.getSkill(professionType) + amount);
    }

    public boolean isSleepingState() {
        return this.coreData().getBoolean(SLEEPING_TAG);
    }

    public void setSleepingState(boolean sleeping) {
        this.updateCoreData(data -> data.putBoolean(SLEEPING_TAG, sleeping));
    }

    public boolean isSittingState() {
        return this.coreData().getBoolean(SITTING_TAG);
    }

    public void setSittingState(boolean sitting) {
        this.updateCoreData(data -> data.putBoolean(SITTING_TAG, sitting));
    }

    public boolean isWorkTime() {
        long dayTime = this.level.getDayTime() % 24000L;
        return dayTime >= WORK_START_TIME && dayTime < WORK_END_TIME;
    }

    public boolean shouldSleep() {
        long dayTime = this.level.getDayTime() % 24000L;
        return dayTime >= SLEEP_START_TIME && dayTime < SLEEP_END_TIME;
    }

    public TekWorkerStatus getWorkerStatus() {
        return TekWorkerStatus.fromSerializedName(this.coreData().getString(WORK_STATUS_TAG));
    }

    public void setWorkerStatus(TekWorkerStatus status) {
        this.updateCoreData(data -> data.putString(WORK_STATUS_TAG, status.getSerializedName()));
    }

    public BlockPos getHomePos() {
        return readBlockPos(this.coreData(), HOME_TAG);
    }

    public void setHomePos(BlockPos pos) {
        this.updateCoreData(data -> writeBlockPos(data, HOME_TAG, pos));
    }

    public BlockPos getBedPos() {
        return readBlockPos(this.coreData(), BED_TAG);
    }

    public void setBedPos(BlockPos pos) {
        this.updateCoreData(data -> writeBlockPos(data, BED_TAG, pos));
    }

    public String getThoughtKey() {
        return this.coreData().getString(THOUGHT_TAG);
    }

    public void setThoughtKey(String thoughtKey) {
        this.updateCoreData(data -> data.putString(THOUGHT_TAG, thoughtKey == null ? "" : thoughtKey));
    }

    public String getItemThoughtId() {
        return this.coreData().getString(ITEM_THOUGHT_TAG);
    }

    public void setItemThought(Item item) {
        ResourceLocation id = item == null ? null : item.getRegistryName();
        this.updateCoreData(data -> data.putString(ITEM_THOUGHT_TAG, id == null ? "" : id.toString()));
    }

    public List<ItemStack> getVillagerInventorySnapshot() {
        List<ItemStack> snapshot = new ArrayList<>();
        for (ItemStack stack : this.villagerInventory) {
            snapshot.add(stack.copy());
        }
        return Collections.unmodifiableList(snapshot);
    }

    public ItemStack addToVillagerInventory(ItemStack incoming) {
        if (incoming.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack remaining = incoming.copy();
        for (int i = 0; i < this.villagerInventory.size(); i++) {
            ItemStack stack = this.villagerInventory.get(i);
            if (stack.isEmpty() || !ItemStack.isSame(stack, remaining) || !ItemStack.tagMatches(stack, remaining)) {
                continue;
            }
            int move = Math.min(remaining.getCount(), stack.getMaxStackSize() - stack.getCount());
            if (move <= 0) {
                continue;
            }
            stack.grow(move);
            remaining.shrink(move);
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        for (int i = 0; i < this.villagerInventory.size(); i++) {
            if (!this.villagerInventory.get(i).isEmpty()) {
                continue;
            }
            ItemStack inserted = remaining.copy();
            inserted.setCount(Math.min(remaining.getCount(), remaining.getMaxStackSize()));
            this.villagerInventory.set(i, inserted);
            remaining.shrink(inserted.getCount());
            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return remaining;
    }

    public int removeFromVillagerInventory(Item item, int amount) {
        if (item == null || amount <= 0) {
            return 0;
        }
        int remaining = amount;
        for (int i = 0; i < this.villagerInventory.size(); i++) {
            ItemStack stack = this.villagerInventory.get(i);
            if (stack.isEmpty() || stack.getItem() != item) {
                continue;
            }
            int take = Math.min(remaining, stack.getCount());
            stack.shrink(take);
            remaining -= take;
            if (stack.isEmpty()) {
                this.villagerInventory.set(i, ItemStack.EMPTY);
            }
            if (remaining <= 0) {
                break;
            }
        }
        return amount - remaining;
    }

    public int countVillagerInventoryItem(Item item) {
        if (item == null) {
            return 0;
        }
        int count = 0;
        for (ItemStack stack : this.villagerInventory) {
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public void clearVillagerInventory() {
        for (int i = 0; i < this.villagerInventory.size(); i++) {
            this.villagerInventory.set(i, ItemStack.EMPTY);
        }
    }

    public String formatCoreDebug() {
        return "profession=" + this.getProfessionType().getSerializedName()
                + " hunger=" + this.getHunger()
                + " happy=" + this.getHappy()
                + " intelligence=" + this.getIntelligence()
                + " daysAlive=" + this.getDaysAlive()
                + " status=" + this.getWorkerStatus().getSerializedName()
                + " workTime=" + this.isWorkTime()
                + " shouldSleep=" + this.shouldSleep()
                + " sleeping=" + this.isSleepingState()
                + " sitting=" + this.isSittingState()
                + " home=" + formatBlockPos(this.getHomePos())
                + " bed=" + formatBlockPos(this.getBedPos())
                + " thought=" + blankAsDash(this.getThoughtKey())
                + " itemThought=" + blankAsDash(this.getItemThoughtId());
    }

    protected void registerAIFilter(String filterName, boolean defaultEnabled) {
        if (this.filterDefaults.putIfAbsent(filterName, defaultEnabled) != null) {
            TekTopiaPort.LOGGER.warn("Duplicate AI filter registration on {}: {}", this.getName().getString(), filterName);
            return;
        }
        CompoundNBT current = this.entityData.get(AI_FILTERS_DATA);
        if (!current.contains(filterName)) {
            CompoundNBT updated = current.copy();
            updated.putBoolean(filterName, defaultEnabled);
            this.entityData.set(AI_FILTERS_DATA, updated);
        }
    }

    public List<String> getAIFilters() {
        return new ArrayList<>(this.filterDefaults.keySet());
    }

    public boolean isAIFilterEnabled(String filterName) {
        CompoundNBT data = this.entityData.get(AI_FILTERS_DATA);
        if (data.contains(filterName)) {
            return data.getBoolean(filterName);
        }
        return this.filterDefaults.getOrDefault(filterName, true);
    }

    public boolean setAIFilter(String filterName, boolean enabled) {
        if (!this.filterDefaults.containsKey(filterName)) {
            return false;
        }
        CompoundNBT updated = this.entityData.get(AI_FILTERS_DATA).copy();
        updated.putBoolean(filterName, enabled);
        this.entityData.set(AI_FILTERS_DATA, updated);
        return true;
    }

    protected Predicate<LivingEntity> hostileSelector() {
        return target -> {
            if (target instanceof ZombieEntity && !(target instanceof ZombifiedPiglinEntity)
                    || target instanceof WitherSkeletonEntity
                    || target instanceof PillagerEntity
                    || target instanceof VindicatorEntity
                    || target instanceof RavagerEntity
                    || target instanceof WitchEntity
                    || target instanceof EvokerEntity
                    || target instanceof VexEntity) {
                return true;
            }
            if (!(target instanceof MonsterEntity) || target.getType().getRegistryName() == null) {
                return false;
            }
            return "tektopia".equals(target.getType().getRegistryName().getNamespace())
                    && target.getType().getRegistryName().getPath().contains("necromancer");
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("coreData", this.coreData());
        nbt.put(INVENTORY_TAG, this.writeInventory());
        CompoundNBT filterData = new CompoundNBT();
        for (String filterName : this.filterDefaults.keySet()) {
            filterData.putBoolean(filterName, this.isAIFilterEnabled(filterName));
        }
        nbt.put("aiFilters", filterData);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("coreData", 10)) {
            CompoundNBT merged = this.createDefaultCoreData();
            merged.merge(nbt.getCompound("coreData"));
            this.entityData.set(CORE_DATA, merged);
        }
        if (nbt.contains(INVENTORY_TAG, 9)) {
            this.readInventory(nbt.getList(INVENTORY_TAG, 10));
        }
        if (nbt.contains("aiFilters", 10)) {
            CompoundNBT savedFilters = nbt.getCompound("aiFilters");
            CompoundNBT merged = this.entityData.get(AI_FILTERS_DATA).copy();
            for (String filterName : this.filterDefaults.keySet()) {
                boolean enabled = savedFilters.contains(filterName) ? savedFilters.getBoolean(filterName) : this.filterDefaults.get(filterName);
                merged.putBoolean(filterName, enabled);
            }
            this.entityData.set(AI_FILTERS_DATA, merged);
        }
    }

    private void tickCoreVillageState() {
        long gameTime = this.level.getGameTime();
        CompoundNBT persistent = this.getPersistentData();
        long lastHungerTick = persistent.getLong(LAST_HUNGER_TICK_TAG);
        if (lastHungerTick <= 0L) {
            persistent.putLong(LAST_HUNGER_TICK_TAG, gameTime);
        } else if (gameTime - lastHungerTick >= 1200L) {
            persistent.putLong(LAST_HUNGER_TICK_TAG, gameTime);
            this.setHunger(this.getHunger() - 1);
            if (this.getHunger() <= 0) {
                this.setHappy(this.getHappy() - 1);
                this.setThoughtKey("hungry");
            }
        }

        long day = this.level.getDayTime() / 24000L;
        long lastDay = persistent.getLong(LAST_DAY_TICK_TAG);
        if (lastDay <= 0L) {
            persistent.putLong(LAST_DAY_TICK_TAG, day);
        } else if (day > lastDay) {
            persistent.putLong(LAST_DAY_TICK_TAG, day);
            this.setDaysAlive(this.getDaysAlive() + 1);
        }
    }

    private CompoundNBT createDefaultCoreData() {
        CompoundNBT data = new CompoundNBT();
        data.putInt(HUNGER_TAG, MAX_STAT);
        data.putInt(HAPPY_TAG, MAX_STAT);
        data.putInt(INTELLIGENCE_TAG, 0);
        data.putInt(DAYS_ALIVE_TAG, 0);
        data.putString(PROFESSION_TAG, ProfessionType.UNKNOWN.getSerializedName());
        data.put(SKILLS_TAG, new CompoundNBT());
        data.putBoolean(SLEEPING_TAG, false);
        data.putBoolean(SITTING_TAG, false);
        data.putString(THOUGHT_TAG, "");
        data.putString(ITEM_THOUGHT_TAG, "");
        data.putString(WORK_STATUS_TAG, TekWorkerStatus.IDLE.getSerializedName());
        return data;
    }

    private CompoundNBT coreData() {
        return this.entityData.get(CORE_DATA);
    }

    private void updateCoreData(CoreDataMutator mutator) {
        CompoundNBT updated = this.coreData().copy();
        mutator.mutate(updated);
        this.entityData.set(CORE_DATA, updated);
    }

    private ListNBT writeInventory() {
        ListNBT list = new ListNBT();
        for (int i = 0; i < this.villagerInventory.size(); i++) {
            ItemStack stack = this.villagerInventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.putByte("Slot", (byte) i);
            stack.save(itemTag);
            list.add(itemTag);
        }
        return list;
    }

    private void readInventory(ListNBT list) {
        this.clearVillagerInventory();
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT itemTag = list.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < 0 || slot >= this.villagerInventory.size()) {
                continue;
            }
            ItemStack stack = ItemStack.of(itemTag);
            if (!stack.isEmpty()) {
                this.villagerInventory.set(slot, stack);
            }
        }
    }

    private static int clampStat(int value) {
        return Math.max(0, Math.min(MAX_STAT, value));
    }

    private static void writeBlockPos(CompoundNBT data, String tag, BlockPos pos) {
        if (pos == null) {
            data.remove(tag);
            return;
        }
        CompoundNBT posTag = new CompoundNBT();
        posTag.putInt("x", pos.getX());
        posTag.putInt("y", pos.getY());
        posTag.putInt("z", pos.getZ());
        data.put(tag, posTag);
    }

    private static BlockPos readBlockPos(CompoundNBT data, String tag) {
        if (!data.contains(tag, 10)) {
            return null;
        }
        CompoundNBT posTag = data.getCompound(tag);
        return new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
    }

    private static String formatBlockPos(BlockPos pos) {
        return pos == null ? "-" : pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static String blankAsDash(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private interface CoreDataMutator {
        void mutate(CompoundNBT data);
    }
}
