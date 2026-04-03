/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityFarmer;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAITillDirt
extends EntityAIMoveToBlock {
    private boolean active = false;
    private int hoeTime = 0;
    private ItemStack bestTool;
    protected final EntityVillagerTek villager;

    public EntityAITillDirt(EntityVillagerTek entityIn, Predicate<EntityVillagerTek> shouldPred) {
        super(entityIn);
        this.villager = entityIn;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos cropPos;
        if (this.villager.getVillage() != null && (cropPos = this.villager.getVillage().requestFarmland(block -> this.canTill((BlockPos)block))) != null) {
            return cropPos.func_177984_a();
        }
        return null;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isWorkTime() && this.villager.isAIFilterEnabled("till_dirt")) {
            List<ItemStack> toolList = this.villager.getInventory().getItems(EntityFarmer.getBestHoe(), 1);
            if (!toolList.isEmpty()) {
                this.bestTool = toolList.get(0);
                return super.func_75250_a();
            }
            this.villager.setThought(EntityVillagerTek.VillagerThought.HOE);
            this.bestTool = null;
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
        if (this.hoeTime > 0) {
            --this.hoeTime;
            if (this.hoeTime == 10) {
                this.stopHoeing();
                if (this.canTill(this.destinationPos.func_177977_b())) {
                    this.villager.field_70170_p.func_180501_a(this.destinationPos.func_177977_b(), Blocks.field_150458_ak.func_176223_P(), 3);
                    this.villager.field_70170_p.func_175698_g(this.destinationPos);
                }
                this.villager.tryAddSkill(ProfessionType.FARMER, 14);
                this.villager.damageItem(this.bestTool, 3);
                this.villager.modifyHunger(-2);
            } else if (this.hoeTime <= 0) {
                this.active = false;
                this.villager.throttledSadness(-3);
            }
        }
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onArrival() {
        this.startHoeing();
        super.onArrival();
    }

    private boolean canTill(BlockPos bp) {
        Block block = this.villager.field_70170_p.func_180495_p(bp).func_177230_c();
        if (block == Blocks.field_150346_d || block == Blocks.field_150349_c) {
            return this.villager.field_70170_p.func_175623_d(bp.func_177984_a());
        }
        return false;
    }

    private void startHoeing() {
        if (this.canTill(this.destinationPos.func_177977_b())) {
            int reductionTime = 40 * this.villager.getSkillLerp(ProfessionType.FARMER, 0, 3);
            this.hoeTime = 250 - reductionTime;
            if (this.bestTool.func_77973_b() == Items.field_151017_I) {
                this.hoeTime += 80;
            }
            this.villager.func_70661_as().func_75499_g();
            this.villager.playServerAnimation("villager_hoe");
            this.villager.equipActionItem(this.bestTool);
        } else {
            this.active = false;
        }
    }

    private void stopHoeing() {
        this.villager.stopServerAnimation("villager_hoe");
        this.villager.unequipActionItem();
    }

    @Override
    public void func_75251_c() {
        super.func_75251_c();
        this.active = false;
        this.hoeTime = 0;
        this.stopHoeing();
    }
}

