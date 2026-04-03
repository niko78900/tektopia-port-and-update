/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.VillageFarm;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIHarvestFarm
extends EntityAIMoveToBlock {
    private final boolean replant;
    private boolean active = false;
    private int harvestTime = 0;
    private IBlockState replantState = null;
    protected final EntityVillagerTek villager;

    public EntityAIHarvestFarm(EntityVillagerTek entityIn, boolean replant) {
        super(entityIn);
        this.replant = replant;
        this.villager = entityIn;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos cropPos;
        if (this.villager.getVillage() != null && (cropPos = this.villager.getVillage().requestMaxAgeCrop()) != null && this.villager.isAIFilterEnabled("harvest_" + this.villager.field_70170_p.func_180495_p(cropPos).func_177230_c().func_149739_a())) {
            return cropPos;
        }
        return null;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isWorkTime()) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    @Override
    public void func_75249_e() {
        this.active = true;
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
        super.func_75246_d();
        if (this.harvestTime > 0) {
            --this.harvestTime;
            if (this.harvestTime == 30) {
                this.stopHarvesting();
                this.gatherResources();
            } else if (this.harvestTime == 0) {
                this.villager.pickupItems(4);
                this.active = false;
                if (this.replant && this.replantState != null) {
                    this.villager.field_70170_p.func_180501_a(this.destinationPos, this.replantState, 2);
                }
            }
        }
    }

    public boolean func_75252_g() {
        return this.harvestTime > 30 || this.harvestTime <= 0;
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onArrival() {
        this.startHarvesting();
        super.onArrival();
    }

    private void startHarvesting() {
        float ANIMATION_TIME = 60.0f;
        if (VillageFarm.isMaxAgeCrop(this.villager.field_70170_p, this.destinationPos)) {
            int animationCycles = this.villager.getSkillLerp(ProfessionType.FARMER, 7, 2);
            this.harvestTime = (int)(60.0f * (float)animationCycles / 30.0f * 20.0f) + 30;
            this.villager.func_70661_as().func_75499_g();
            this.villager.playServerAnimation("villager_pickup");
        } else {
            this.active = false;
        }
    }

    private void stopHarvesting() {
        this.villager.stopServerAnimation("villager_pickup");
    }

    private void gatherResources() {
        if (this.isNearDestination(4.0)) {
            Block block = this.villager.field_70170_p.func_180495_p(this.destinationPos).func_177230_c();
            this.replantState = this.getReplantFromBlock(block);
            ModBlocks.villagerDestroyBlock(this.destinationPos, this.villager, true, true);
            int ruinerChance = this.villager.getSkillLerp(ProfessionType.FARMER, 2, 10);
            if (this.villager.field_70170_p.field_73012_v.nextInt(ruinerChance) == 0) {
                this.villager.field_70170_p.func_175656_a(this.destinationPos.func_177977_b(), Blocks.field_150346_d.func_176223_P());
                this.replantState = null;
            }
            this.villager.tryAddSkill(ProfessionType.FARMER, 12);
            this.villager.throttledSadness(-2);
            this.villager.modifyHunger(-1);
        }
    }

    protected IBlockState getReplantFromBlock(Block block) {
        if (block == Blocks.field_150464_aj) {
            return Blocks.field_150464_aj.func_176223_P();
        }
        if (block == Blocks.field_150469_bN) {
            return Blocks.field_150469_bN.func_176223_P();
        }
        if (block == Blocks.field_150459_bM) {
            return Blocks.field_150459_bM.func_176223_P();
        }
        if (block == Blocks.field_185773_cZ) {
            return Blocks.field_185773_cZ.func_176223_P();
        }
        return null;
    }

    @Override
    public void func_75251_c() {
        this.stopHarvesting();
        super.func_75251_c();
    }
}

