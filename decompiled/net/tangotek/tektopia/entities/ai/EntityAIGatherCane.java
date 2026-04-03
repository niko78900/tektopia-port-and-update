/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.blockfinder.SugarCaneScanner;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIGatherCane
extends EntityAIMoveToBlock {
    private final EntityVillagerTek villager;
    private BlockPos targetPos;
    private boolean active = false;
    private int gatherTime = 0;
    private final int maxStorage;

    public EntityAIGatherCane(EntityVillagerTek entityIn, int maxStorage) {
        super(entityIn);
        this.villager = entityIn;
        this.maxStorage = maxStorage;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        if (this.villager.getVillage() != null) {
            this.releaseBlockClaim();
            BlockPos bp = this.villager.getVillage().requestBlock((Block)Blocks.field_150436_aH);
            if (bp != null) {
                this.villager.debugOut("GatherCane start " + bp);
                return bp;
            }
        }
        return null;
    }

    @Override
    protected BlockPos findWalkPos() {
        BlockPos diff = this.villager.func_180425_c().func_177973_b((Vec3i)this.destinationPos);
        EnumFacing facing = EnumFacing.func_176737_a((float)diff.func_177958_n(), (float)0.0f, (float)diff.func_177952_p());
        BlockPos focusPos = this.destinationPos.func_177972_a(facing);
        if (this.isWalkable(focusPos, this.villager)) {
            return focusPos;
        }
        BlockPos closest = null;
        double minDist = Double.MAX_VALUE;
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                double thisDist;
                BlockPos testPos = focusPos.func_177982_a(x, 0, z);
                if (!this.isWalkable(testPos, this.villager) || !((thisDist = testPos.func_177951_i((Vec3i)focusPos)) < minDist)) continue;
                minDist = thisDist;
                closest = testPos;
            }
        }
        return closest;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.getVillage() != null && this.villager.isWorkTime() && this.villager.func_70681_au().nextInt(15) == 0 && this.villager.isAIFilterEnabled("gather_cane")) {
            int storageCount = this.villager.getVillage().getStorageCount(p -> p.func_77973_b() == Items.field_151120_aE);
            if (this.maxStorage > 0 && storageCount >= this.maxStorage) {
                return false;
            }
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.targetPos = this.destinationPos;
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
        if (this.gatherTime > 0) {
            this.villager.func_70671_ap().func_75650_a((double)this.destinationPos.func_177958_n(), (double)this.destinationPos.func_177956_o(), (double)this.destinationPos.func_177952_p(), 30.0f, 30.0f);
            --this.gatherTime;
            if (this.gatherTime == 10) {
                this.gatherBlock();
            } else if (this.gatherTime == 0) {
                this.villager.pickupItems(4);
                this.active = false;
            }
        } else {
            super.func_75246_d();
        }
    }

    public boolean func_75252_g() {
        return this.gatherTime <= 0;
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onArrival() {
        if (this.isNearDestination(2.5)) {
            this.villager.debugOut("GatherCane [ " + this.targetPos.func_177958_n() + ", " + this.targetPos.func_177952_p() + "] Arrived at Destination: " + this.getWalkPos());
            if (SugarCaneScanner.getCaneStalk(this.villager.field_70170_p, this.targetPos) != null) {
                this.startGathering();
                super.onArrival();
            } else {
                this.villager.debugOut("GatherCane [ " + this.targetPos.func_177958_n() + ", " + this.targetPos.func_177952_p() + "] Uhhh.  No cane here?");
                this.active = false;
            }
        }
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        this.active = false;
        super.onPathFailed(pos);
    }

    private void startGathering() {
        this.villager.debugOut("GatherCane [ " + this.targetPos.func_177958_n() + ", " + this.targetPos.func_177952_p() + "] Starting");
        this.gatherTime = 36;
        this.villager.func_70661_as().func_75499_g();
        this.villager.playServerAnimation("villager_take");
    }

    private void stopChopping() {
        this.villager.stopServerAnimation("villager_take");
    }

    private void gatherBlock() {
        int count = 0;
        if (this.isNearDestination(3.0)) {
            BlockPos reedPos = this.targetPos;
            while (SugarCaneScanner.isCane(this.villager.field_70170_p.func_180495_p(reedPos.func_177984_a()))) {
                reedPos = reedPos.func_177984_a();
            }
            while (SugarCaneScanner.isCane(this.villager.field_70170_p.func_180495_p(reedPos.func_177977_b()))) {
                ModBlocks.villagerDestroyBlock(reedPos, this.villager, true, true);
                reedPos = reedPos.func_177977_b();
                ++count;
            }
            this.villager.tryAddSkill(ProfessionType.FARMER, 9);
            this.villager.throttledSadness(-2);
            this.villager.modifyHunger(-2);
        }
        this.villager.debugOut("GatherCane [ " + this.targetPos.func_177958_n() + ", " + this.targetPos.func_177952_p() + "] Chopped: " + count);
    }

    private void releaseBlockClaim() {
        if (this.targetPos != null && this.villager.getVillage() != null) {
            this.villager.getVillage().releaseBlockClaim((Block)Blocks.field_150436_aH, this.targetPos);
            this.targetPos = null;
        }
    }

    @Override
    public void func_75251_c() {
        this.stopChopping();
        this.releaseBlockClaim();
        super.func_75251_c();
    }
}

