/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.pathing.BasePathingNode;

public abstract class EntityAIPatrolPoint
extends EntityAIMoveToBlock {
    protected final Predicate<EntityVillagerTek> shouldPred;
    protected final EntityVillagerTek villager;
    protected final int distanceFromPoint;
    protected final int waitTime;
    private int waitRemaining;

    public EntityAIPatrolPoint(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred, int distanceFromPoint, int waitTime) {
        super(v);
        this.villager = v;
        this.distanceFromPoint = distanceFromPoint;
        this.shouldPred = shouldPred;
        this.waitTime = 200;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.navigator.hasVillage() && this.shouldPred.test(this.villager)) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public boolean func_75253_b() {
        if (this.waitRemaining > 0) {
            return true;
        }
        return super.func_75253_b();
    }

    protected abstract BlockPos getPatrolPoint();

    private BlockPos randomNearbyBlock(BlockPos pos, int xz) {
        return pos.func_177982_a(this.villager.func_70681_au().nextInt(xz * 2) - xz, 0, this.villager.func_70681_au().nextInt(xz * 2) - xz);
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos point = this.getPatrolPoint();
        if (point != null && this.villager.func_70681_au().nextInt(4) > 0) {
            for (int i = 0; i < 20; ++i) {
                BlockPos testBlock = this.randomNearbyBlock(point, this.distanceFromPoint);
                BasePathingNode baseNode = this.villager.getVillage().getPathingGraph().getNodeYRange(testBlock.func_177958_n(), testBlock.func_177956_o() - 5, testBlock.func_177956_o() + 5, testBlock.func_177952_p());
                if (baseNode == null) continue;
                return baseNode.getBlockPos();
            }
        }
        return point;
    }

    @Override
    protected void onArrival() {
        this.waitRemaining = this.villager.func_70681_au().nextInt(this.waitTime) + this.waitTime;
        super.onArrival();
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        if (this.hasArrived()) {
            --this.waitRemaining;
        }
    }

    @Override
    public void func_75249_e() {
        this.waitRemaining = 0;
        super.func_75249_e();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.navigator.func_180425_c().func_177951_i((Vec3i)this.destinationPos) < 4.0;
    }
}

