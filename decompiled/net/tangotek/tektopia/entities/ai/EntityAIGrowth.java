/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIGrowth
extends EntityAIMoveToBlock {
    private boolean arrived = false;
    private int castTime = 0;
    private int castIterations = 0;
    private EntityDruid druid;
    private final int CAST_TIME = 90;
    private final double range;
    private GrowthType growthType;
    private BlockPos growthPos;

    public EntityAIGrowth(EntityVillagerTek entityIn, double range) {
        super(entityIn);
        this.druid = (EntityDruid)entityIn;
        this.range = range;
    }

    @Override
    public boolean func_75250_a() {
        this.growthPos = null;
        if (this.druid.isAITick() && this.druid.func_70681_au().nextInt(10) == 0 && this.druid.hasVillage() && this.druid.isWorkTime() && this.druid.isGrowTime()) {
            boolean trySapling = this.druid.func_70681_au().nextBoolean();
            if (trySapling && this.druid.isAIFilterEnabled("cast_growth_trees")) {
                this.growthType = GrowthType.SAPLING;
                this.growthPos = this.druid.getVillage().requestBlock(Blocks.field_150345_g);
            } else if (this.druid.isAIFilterEnabled("cast_growth_crops")) {
                this.growthType = GrowthType.FARM;
                BlockPos farmPos = this.druid.getVillage().requestFarmland(bp -> this.canBlockGrow((BlockPos)bp));
                if (farmPos != null) {
                    this.growthPos = farmPos.func_177984_a();
                }
            }
            if (this.growthPos != null) {
                return super.func_75250_a();
            }
        }
        return false;
    }

    private boolean canBlockGrow(BlockPos bp) {
        IGrowable growable;
        BlockPos cropPos = bp.func_177984_a();
        IBlockState cropState = this.druid.field_70170_p.func_180495_p(cropPos);
        return cropState.func_177230_c() instanceof IGrowable && (growable = (IGrowable)cropState.func_177230_c()).func_176473_a(this.druid.field_70170_p, cropPos, cropState, false);
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
        this.castIterations = MathHelper.func_76125_a((int)(this.druid.getSkill(ProfessionType.DRUID) / 20), (int)1, (int)5);
    }

    @Override
    protected BlockPos getDestinationBlock() {
        return this.growthPos;
    }

    @Override
    protected void onArrival() {
        if (!this.arrived) {
            this.tryCast();
        }
        this.arrived = true;
        super.onArrival();
    }

    protected boolean inRange() {
        return this.growthPos.func_177951_i((Vec3i)this.druid.func_180425_c()) < this.range * this.range;
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.inRange();
    }

    public boolean func_75252_g() {
        return this.castTime <= 0;
    }

    protected void tryCast() {
        if (this.druid.func_70089_S() && this.inRange()) {
            this.castTime = 90;
            this.druid.playServerAnimation("villager_cast_grow");
            this.druid.modifyHunger(-2);
            this.druid.func_70661_as().func_75499_g();
            this.druid.setSpellBlock(this.growthPos);
            this.setArrived();
            this.druid.playSound(ModSoundEvents.healingSource);
        }
    }

    @Override
    public void func_75246_d() {
        --this.castTime;
        if (this.castTime == 48) {
            this.druid.playSound(ModSoundEvents.villagerEnchant);
            this.druid.setSpellBlock(this.growthPos);
            if (this.growthType == GrowthType.SAPLING) {
                int growthCount = this.druid.getSkillLerp(ProfessionType.DRUID, 6, 20);
                for (int i = 0; i < growthCount; ++i) {
                    this.growBlock(this.growthPos);
                }
            } else if (this.growthType == GrowthType.FARM) {
                int range = this.druid.getGrowthRange();
                for (int x = -range; x <= range; ++x) {
                    for (int z = -range; z <= range; ++z) {
                        BlockPos localPos = this.growthPos.func_177965_g(x).func_177964_d(z);
                        this.growBlock(localPos);
                    }
                }
            }
            if (this.castIterations == 0) {
                this.druid.tryAddSkill(ProfessionType.DRUID, 12);
            }
        }
        if (this.castTime >= 0) {
            this.druid.faceLocation(this.growthPos.func_177958_n(), this.growthPos.func_177952_p(), 30.0f);
        }
        if (this.castTime <= 0 && this.castIterations > 0) {
            --this.castIterations;
            this.druid.stopServerAnimation("villager_cast_grow");
            this.druid.setSpellBlock(null);
            this.tryCast();
        }
        super.func_75246_d();
    }

    private void growBlock(BlockPos pos) {
        IGrowable growable;
        IBlockState growthState;
        Block growthBlock;
        if (this.druid.hasVillage() && (growthBlock = (growthState = this.druid.getVillage().getWorld().func_180495_p(pos)).func_177230_c()) instanceof IGrowable && (growable = (IGrowable)growthBlock).func_176473_a(this.druid.field_70170_p, pos, growthState, false) && growable.func_180670_a(this.druid.field_70170_p, this.druid.func_70681_au(), pos, growthState)) {
            growable.func_176474_b(this.druid.field_70170_p, this.druid.func_70681_au(), pos, growthState);
        }
    }

    @Override
    public void func_75251_c() {
        if (this.druid.hasVillage()) {
            this.druid.getVillage().releaseBlockClaim(Blocks.field_150345_g, this.growthPos);
        }
        this.druid.setSpellBlock(null);
        this.druid.stopServerAnimation("villager_cast_grow");
        this.arrived = false;
        this.castTime = 0;
        super.func_75251_c();
    }

    @Override
    public boolean func_75253_b() {
        if (this.castTime > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    @Override
    void updateMovementMode() {
        this.druid.setMovementMode(this.druid.getDefaultMovement());
    }

    private static enum GrowthType {
        FARM,
        SAPLING;

    }
}

