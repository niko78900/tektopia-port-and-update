package net.tangotek.tektopia.entities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.tangotek.tektopia.TekTopiaPort;

public class TekVillagerEntity extends CreatureEntity {
    private static final DataParameter<CompoundNBT> AI_FILTERS_DATA =
            EntityDataManager.defineId(TekVillagerEntity.class, DataSerializers.COMPOUND_TAG);

    private final Map<String, Boolean> filterDefaults = new LinkedHashMap<>();

    protected TekVillagerEntity(EntityType<? extends CreatureEntity> type, World level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AI_FILTERS_DATA, new CompoundNBT());
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
                    || target instanceof EvokerEntity
                    || target instanceof VexEntity
                    || target instanceof VindicatorEntity) {
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
        CompoundNBT filterData = new CompoundNBT();
        for (String filterName : this.filterDefaults.keySet()) {
            filterData.putBoolean(filterName, this.isAIFilterEnabled(filterName));
        }
        nbt.put("aiFilters", filterData);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (!nbt.contains("aiFilters", 10)) {
            return;
        }
        CompoundNBT savedFilters = nbt.getCompound("aiFilters");
        CompoundNBT merged = this.entityData.get(AI_FILTERS_DATA).copy();
        for (String filterName : this.filterDefaults.keySet()) {
            boolean enabled = savedFilters.contains(filterName) ? savedFilters.getBoolean(filterName) : this.filterDefaults.get(filterName);
            merged.putBoolean(filterName, enabled);
        }
        this.entityData.set(AI_FILTERS_DATA, merged);
    }
}
