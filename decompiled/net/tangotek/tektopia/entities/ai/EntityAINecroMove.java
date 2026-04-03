/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAINecroMove
extends EntityAIMoveToBlock {
    protected final EntityNecromancer necro;
    private int failedPath = 0;

    public EntityAINecroMove(EntityNecromancer n) {
        super(n);
        this.necro = n;
    }

    @Override
    public boolean func_75250_a() {
        if (this.navigator.isAITick() && this.navigator.hasVillage() && !this.navigator.func_70781_l()) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        List<VillageStructure> animalePens;
        if (this.necro.isAngry() && this.necro.func_70681_au().nextInt(3) == 0 && !(animalePens = this.necro.getVillage().getStructures(VillageStructureType.COW_PEN, VillageStructureType.SHEEP_PEN, VillageStructureType.CHICKEN_COOP, VillageStructureType.PIG_PEN)).isEmpty()) {
            animalePens.removeIf(p -> p.getEntitiesInside(EntityAnimal.class).size() <= 0);
            VillageStructure closestPen = animalePens.stream().min(Comparator.comparing(p -> p.getDoor().func_177951_i((Vec3i)this.navigator.func_180425_c()))).orElse(null);
            if (closestPen != null) {
                return closestPen.getDoor();
            }
        }
        return this.navigator.getVillage().getLastVillagerPos();
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    @Override
    void updateMovementMode() {
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        ++this.failedPath;
        if (this.failedPath > 50) {
            this.necro.debugOut("Killed Necro. Too many failed pathfindings");
            this.necro.func_70106_y();
        }
        super.onPathFailed(pos);
    }

    @Override
    protected boolean isNearWalkPos() {
        return this.necro.func_180425_c().func_177951_i((Vec3i)this.destinationPos) < 4.0;
    }

    @Override
    protected void onArrival() {
        super.onArrival();
    }

    @Override
    public void func_75251_c() {
        super.func_75251_c();
    }
}

