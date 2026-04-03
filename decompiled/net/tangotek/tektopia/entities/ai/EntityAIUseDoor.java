/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.pathing.PathNavigateVillager2;

public class EntityAIUseDoor
extends EntityAIBase {
    boolean closeDoor;
    int closeDoorTimer;
    protected EntityLiving entity;
    protected BlockPos doorPosition = BlockPos.field_177992_a;
    protected BlockDoor doorBlock;

    public EntityAIUseDoor(EntityLiving entitylivingIn, boolean shouldClose) {
        this.entity = entitylivingIn;
        this.closeDoor = shouldClose;
    }

    public boolean func_75250_a() {
        if (!this.entity.field_70123_F) {
            return false;
        }
        PathNavigateVillager2 pathNavigate = (PathNavigateVillager2)this.entity.func_70661_as();
        Path path = pathNavigate.func_75505_d();
        if (path != null && !path.func_75879_b() && pathNavigate.getEnterDoors()) {
            for (int i = 0; i < Math.min(path.func_75873_e() + 2, path.func_75874_d()); ++i) {
                PathPoint pathpoint = path.func_75877_a(i);
                this.doorPosition = new BlockPos(pathpoint.field_75839_a, pathpoint.field_75837_b + 1, pathpoint.field_75838_c);
                if (!(this.entity.func_70092_e((double)this.doorPosition.func_177958_n(), this.entity.field_70163_u, (double)this.doorPosition.func_177952_p()) <= 2.25)) continue;
                this.doorBlock = this.getBlockDoor(this.doorPosition);
                if (this.doorBlock == null) continue;
                return true;
            }
            this.doorPosition = new BlockPos((Entity)this.entity).func_177984_a();
            this.doorBlock = this.getBlockDoor(this.doorPosition);
            return this.doorBlock != null;
        }
        return false;
    }

    private BlockDoor getBlockDoor(BlockPos pos) {
        IBlockState iblockstate = this.entity.field_70170_p.func_180495_p(pos);
        Block block = iblockstate.func_177230_c();
        return block instanceof BlockDoor && iblockstate.func_185904_a() == Material.field_151575_d ? (BlockDoor)block : null;
    }

    public boolean func_75253_b() {
        return this.closeDoor && this.closeDoorTimer >= 0;
    }

    private boolean isDoorClear() {
        return this.entity.field_70170_p.func_72872_a(EntityVillagerTek.class, new AxisAlignedBB(this.doorPosition)).isEmpty();
    }

    public void func_75249_e() {
        this.closeDoorTimer = 25;
        this.openDoor(true);
    }

    private void openDoor(boolean open) {
        this.doorBlock.func_176512_a(this.entity.field_70170_p, this.doorPosition, open);
    }

    public void func_75251_c() {
        if (this.closeDoor) {
            this.openDoor(false);
        }
    }

    public void func_75246_d() {
        --this.closeDoorTimer;
        if (this.closeDoorTimer == 0 && !this.isDoorClear()) {
            this.openDoor(true);
            this.closeDoorTimer = 25;
        }
        super.func_75246_d();
    }
}

