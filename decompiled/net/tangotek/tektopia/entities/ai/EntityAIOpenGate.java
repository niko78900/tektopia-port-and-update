/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.pathing.PathNavigateVillager2;

public class EntityAIOpenGate
extends EntityAIBase {
    private EntityVillagerTek villager;
    protected BlockPos gatePosition = BlockPos.field_177992_a;
    protected BlockFenceGate gateBlock;
    boolean hasStoppedDoorInteraction;
    float entityPositionX;
    float entityPositionZ;
    int closeDoorTemporisation;

    public EntityAIOpenGate(EntityVillagerTek v) {
        this.villager = v;
    }

    public boolean func_75250_a() {
        if (!this.villager.field_70123_F) {
            return false;
        }
        PathNavigateVillager2 pathNavigate = (PathNavigateVillager2)this.villager.func_70661_as();
        Path path = pathNavigate.func_75505_d();
        if (path != null && !path.func_75879_b() && pathNavigate.getEnterDoors()) {
            for (int i = 0; i < Math.min(path.func_75873_e() + 2, path.func_75874_d()); ++i) {
                PathPoint pathpoint = path.func_75877_a(i);
                this.gatePosition = new BlockPos(pathpoint.field_75839_a, pathpoint.field_75837_b + 1, pathpoint.field_75838_c);
                if (!(this.villager.func_70092_e(this.gatePosition.func_177958_n(), this.villager.field_70163_u, this.gatePosition.func_177952_p()) <= 2.25)) continue;
                this.gateBlock = this.getBlockGate(this.gatePosition);
                if (this.gateBlock == null) continue;
                return true;
            }
            this.gatePosition = new BlockPos((Entity)this.villager);
            this.gateBlock = this.getBlockGate(this.gatePosition);
            return this.gateBlock != null;
        }
        return false;
    }

    public boolean func_75253_b() {
        return this.closeDoorTemporisation > 0 && !this.hasStoppedDoorInteraction;
    }

    public void func_75249_e() {
        this.closeDoorTemporisation = 20;
        this.toggleGate(this.gatePosition, true);
        this.hasStoppedDoorInteraction = false;
        this.entityPositionX = (float)((double)((float)this.gatePosition.func_177958_n() + 0.5f) - this.villager.field_70165_t);
        this.entityPositionZ = (float)((double)((float)this.gatePosition.func_177952_p() + 0.5f) - this.villager.field_70161_v);
    }

    public void func_75246_d() {
        float f1;
        float f = (float)((double)((float)this.gatePosition.func_177958_n() + 0.5f) - this.villager.field_70165_t);
        float f2 = this.entityPositionX * f + this.entityPositionZ * (f1 = (float)((double)((float)this.gatePosition.func_177952_p() + 0.5f) - this.villager.field_70161_v));
        if (f2 < 0.0f) {
            this.hasStoppedDoorInteraction = true;
        }
        --this.closeDoorTemporisation;
        super.func_75246_d();
    }

    private BlockFenceGate getBlockGate(BlockPos pos) {
        IBlockState iblockstate = this.villager.field_70170_p.func_180495_p(pos);
        Block block = iblockstate.func_177230_c();
        return block instanceof BlockFenceGate ? (BlockFenceGate)block : null;
    }

    public void func_75251_c() {
        this.toggleGate(this.gatePosition, false);
    }

    private void toggleGate(BlockPos gatePosition, boolean open) {
        IBlockState state = this.villager.field_70170_p.func_180495_p(gatePosition);
        if (this.getBlockGate(gatePosition) != null && (Boolean)state.func_177229_b((IProperty)BlockFenceGate.field_176466_a) != open) {
            state = state.func_177226_a((IProperty)BlockFenceGate.field_176466_a, (Comparable)Boolean.valueOf(open));
            this.villager.field_70170_p.func_180501_a(gatePosition, state, 10);
            this.villager.field_70170_p.func_180498_a((EntityPlayer)null, (Boolean)state.func_177229_b((IProperty)BlockFenceGate.field_176466_a) != false ? 1008 : 1014, gatePosition, 0);
        }
    }
}

