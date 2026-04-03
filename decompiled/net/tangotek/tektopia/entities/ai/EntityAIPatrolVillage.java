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

public class EntityAIPatrolVillage
extends EntityAIMoveToBlock {
    protected final EntityVillagerTek villager;

    public EntityAIPatrolVillage(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred) {
        super(v);
        this.villager = v;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villager.isWorkTime() && this.villager.func_70681_au().nextInt(10) == 0) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos dest = this.villager.getVillage().getLastVillagerPos();
        return dest;
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.villager.func_180425_c().func_177951_i((Vec3i)this.destinationPos) < 4.0;
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    protected void onArrival() {
        this.villager.throttledSadness(-2);
        super.onArrival();
    }
}

