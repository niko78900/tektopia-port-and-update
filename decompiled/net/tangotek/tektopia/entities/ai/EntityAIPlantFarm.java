/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityFarmer;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIPlantFarm
extends EntityAIMoveToBlock {
    private boolean active = false;
    private int plantTime = 0;
    private IBlockState plantState = null;
    protected final EntityVillagerTek villager;

    public EntityAIPlantFarm(EntityVillagerTek entityIn) {
        super(entityIn);
        this.villager = entityIn;
    }

    private Predicate<BlockPos> isPlantable() {
        return bp -> this.villager.field_70170_p.func_180495_p(bp).func_177230_c() == Blocks.field_150458_ak && this.villager.field_70170_p.func_175623_d(bp.func_177984_a());
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos farmPos = this.villager.getVillage().requestFarmland(this.isPlantable());
        if (farmPos != null) {
            BlockPos cropPos = farmPos.func_177984_a();
            this.plantState = this.checkNearbyCrops(cropPos);
            if (this.plantState != null && this.villager.isAIFilterEnabled("plant_" + this.plantState.func_177230_c().func_149739_a())) {
                Item seedItem = EntityFarmer.getSeed(this.plantState);
                Predicate<ItemStack> seedPred = i -> i.func_77973_b() == seedItem;
                if (this.villager.getInventory().getItemCount(seedPred) >= 1) {
                    return cropPos;
                }
                this.villager.setItemThought(seedItem);
            }
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
        if (this.plantTime > 0) {
            --this.plantTime;
            if (this.plantTime == 10) {
                this.stopPlanting();
                this.plantCrop();
            } else if (this.plantTime <= 0) {
                this.active = false;
            }
        }
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onArrival() {
        this.startPlanting();
        super.onArrival();
    }

    private void startPlanting() {
        float ANIMATION_TIME = 50.0f;
        if (this.isPlantable().test(this.destinationPos.func_177977_b())) {
            int animationCycles = this.villager.getSkillLerp(ProfessionType.FARMER, 6, 1);
            this.plantTime = (int)(50.0f * (float)animationCycles / 30.0f * 20.0f) + 10;
            this.villager.func_70661_as().func_75499_g();
            this.villager.playServerAnimation("villager_take");
        } else {
            this.active = false;
        }
    }

    private void stopPlanting() {
        this.villager.stopServerAnimation("villager_take");
    }

    private void plantCrop() {
        if (this.isNearDestination(4.0) && this.isPlantable().test(this.destinationPos.func_177977_b())) {
            this.villager.field_70170_p.func_180501_a(this.destinationPos, this.plantState, 2);
            this.villager.modifyHunger(-1);
            Item seedItem = EntityFarmer.getSeed(this.plantState);
            this.villager.getInventory().removeItems(is -> is.func_77973_b() == seedItem, 1);
            this.villager.tryAddSkill(ProfessionType.FARMER, 16);
        }
    }

    private IBlockState checkNearbyCrops(BlockPos pos) {
        Block[] cropBlocks;
        IBlockState bestState = null;
        for (Block b : cropBlocks = new Block[]{Blocks.field_150464_aj, Blocks.field_150469_bN, Blocks.field_150459_bM, Blocks.field_185773_cZ}) {
            int count = this.countNearbyCrop(pos, b);
            if (count >= 2) {
                return b.func_176223_P();
            }
            if (count != 1 || bestState != null) continue;
            bestState = b.func_176223_P();
        }
        return bestState;
    }

    private int countNearbyCrop(BlockPos pos, Block block) {
        int count = 0;
        if (this.villager.field_70170_p.func_180495_p(pos.func_177976_e()).func_177230_c() == block) {
            ++count;
        }
        if (this.villager.field_70170_p.func_180495_p(pos.func_177974_f()).func_177230_c() == block) {
            ++count;
        }
        if (this.villager.field_70170_p.func_180495_p(pos.func_177978_c()).func_177230_c() == block) {
            ++count;
        }
        if (this.villager.field_70170_p.func_180495_p(pos.func_177968_d()).func_177230_c() == block) {
            ++count;
        }
        return count;
    }

    @Override
    public void func_75251_c() {
        this.active = false;
        this.stopPlanting();
        super.func_75251_c();
    }
}

