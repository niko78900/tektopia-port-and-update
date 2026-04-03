/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAILeadAnimalToStructure
extends EntityAIMoveToBlock {
    private final VillageStructureType structureType;
    private VillageStructure destinationStructure;
    protected final EntityVillagerTek villager;
    private boolean active = false;
    private int pathTick = 0;
    private final EntityTagType entTagType;

    public EntityAILeadAnimalToStructure(EntityVillagerTek v, VillageStructureType structureType, EntityTagType tagType) {
        super(v);
        this.villager = v;
        this.structureType = structureType;
        this.destinationStructure = null;
        this.entTagType = tagType;
    }

    protected VillageStructure getDestinationStructure() {
        return this.villager.getVillage().getNearestStructure(this.structureType, this.villager.func_180425_c());
    }

    @Override
    protected BlockPos getDestinationBlock() {
        this.destinationPos = null;
        if (this.villager.getVillage() != null && this.villager.getLeadAnimal() != null && this.villager.isWorkTime()) {
            this.destinationStructure = this.getDestinationStructure();
            if (this.destinationStructure != null) {
                this.destinationPos = this.destinationStructure.getSafeSpot();
            }
        }
        return this.destinationPos;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick()) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.active = true;
        this.pathTick = 30;
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.active;
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    public void func_75246_d() {
        if (this.villager.getLeadAnimal() != null) {
            if (this.villager.getLeadAnimal().func_70068_e((Entity)this.villager) > 40.0) {
                this.teleportAnimal(this.villager.func_180425_c());
            }
            if (!this.villager.getLeadAnimal().func_70089_S()) {
                this.active = false;
            } else if (this.villager.getLeadAnimal().func_110166_bE() != this.villager) {
                this.active = false;
            }
        }
        --this.pathTick;
        if (this.pathTick <= 0) {
            this.updateAnimalPath();
            if (this.villager.func_70681_au().nextInt(8) == 0) {
                this.villager.modifyHunger(-1);
            }
            this.pathTick = 30;
        }
        super.func_75246_d();
    }

    private void updateAnimalPath() {
        this.villager.getLeadAnimal().func_70690_d(new PotionEffect(MobEffects.field_76424_c, 40));
        this.villager.getLeadAnimal().func_70661_as().func_75497_a((Entity)this.villager, (double)this.villager.getLeadAnimal().func_70689_ay() * 1.2);
    }

    private void teleportAnimal(BlockPos pos) {
        this.villager.getLeadAnimal().func_70634_a((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
    }

    @Override
    protected void onArrival() {
        this.active = false;
        super.onArrival();
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        this.active = false;
        super.onPathFailed(pos);
    }

    @Override
    public void func_75251_c() {
        if (this.villager.getLeadAnimal() != null) {
            if (!this.destinationStructure.isBlockInside(this.villager.getLeadAnimal().func_180425_c())) {
                this.teleportAnimal(this.destinationStructure.getSafeSpot());
            }
            if (this.entTagType != null) {
                ModEntities.makeTaggedEntity((Entity)this.villager.getLeadAnimal(), this.entTagType);
            }
            this.villager.getLeadAnimal().func_110160_i(true, false);
            this.villager.setLeadAnimal(null);
        }
        this.active = false;
        this.destinationStructure = null;
        super.func_75251_c();
    }
}

