/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolPoint;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureMerchantStall;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIVisitMerchantStall
extends EntityAIPatrolPoint {
    protected final EntityMerchant merchant;
    protected VillageStructureMerchantStall merchantStall;

    public EntityAIVisitMerchantStall(EntityMerchant v, Predicate<EntityVillagerTek> shouldPred, int distanceFromPoint, int waitTime) {
        super(v, shouldPred, distanceFromPoint, waitTime);
        this.merchant = v;
    }

    @Override
    protected BlockPos getPatrolPoint() {
        List<VillageStructure> stalls = this.villager.getVillage().getStructures(VillageStructureType.MERCHANT_STALL);
        Collections.shuffle(stalls);
        if (!stalls.isEmpty()) {
            this.merchantStall = (VillageStructureMerchantStall)stalls.get(0);
        }
        if (this.merchantStall != null) {
            return this.merchantStall.getDoor();
        }
        return null;
    }

    @Override
    public void func_75249_e() {
        this.merchant.setStall(1);
        super.func_75249_e();
    }
}

