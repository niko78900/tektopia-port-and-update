/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIGenericMove
extends EntityAIMoveToBlock {
    protected final Function<EntityVillagerTek, BlockPos> whereFunc;
    protected final Predicate<EntityVillagerTek> shouldPred;
    protected final Runnable resetRunner;
    protected final Runnable startRunner;
    protected final EntityVillagerTek villager;
    protected final EntityVillagerTek.MovementMode moveMode;

    public EntityAIGenericMove(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred, Function<EntityVillagerTek, BlockPos> whereFunc, EntityVillagerTek.MovementMode moveMode, Runnable startRunner, Runnable resetRunner) {
        super(v);
        this.villager = v;
        this.shouldPred = shouldPred;
        this.whereFunc = whereFunc;
        this.resetRunner = resetRunner;
        this.startRunner = startRunner;
        this.moveMode = moveMode;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.navigator.hasVillage() && this.shouldPred.test(this.villager)) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        BlockPos dest = this.whereFunc.apply(this.villager);
        return dest;
    }

    @Override
    public void func_75249_e() {
        if (this.startRunner != null) {
            this.startRunner.run();
        }
        super.func_75249_e();
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.moveMode);
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.navigator.func_180425_c().func_177951_i((Vec3i)this.destinationPos) < 4.0;
    }

    @Override
    protected void onArrival() {
        super.onArrival();
    }

    @Override
    public void func_75251_c() {
        if (this.resetRunner != null) {
            this.resetRunner.run();
        }
        super.func_75251_c();
    }
}

