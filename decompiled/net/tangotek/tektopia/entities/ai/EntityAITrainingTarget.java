/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAITarget
 *  net.minecraft.entity.item.EntityArmorStand
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.item.EntityArmorStand;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAITrainingTarget
extends EntityAITarget {
    EntityGuard guard;
    EntityLivingBase targetEntity;
    VillageStructure structure;
    protected final Predicate<EntityGuard> shouldPred;

    public EntityAITrainingTarget(EntityGuard guard, Predicate<EntityGuard> shouldPred) {
        super((EntityCreature)guard, false, false);
        this.guard = guard;
        this.func_75248_a(1);
        this.shouldPred = shouldPred;
    }

    public boolean func_75250_a() {
        if (this.guard.isAIFilterEnabled("practice_melee") && this.guard.hasVillage() && this.shouldPred.test(this.guard)) {
            List<VillageStructure> barracks = this.guard.getVillage().getStructures(VillageStructureType.BARRACKS);
            Collections.shuffle(barracks);
            for (VillageStructure str : barracks) {
                List<EntityArmorStand> armorStands = str.getEntitiesInside(EntityArmorStand.class);
                this.targetEntity = armorStands.stream().filter(a -> !str.isSpecialBlockOccupied(a.func_180425_c())).findAny().orElse(null);
                if (this.targetEntity == null) continue;
                this.structure = str;
                return true;
            }
        }
        return false;
    }

    public boolean func_75253_b() {
        if (this.guard.wantsPractice() <= 0) {
            return false;
        }
        return super.func_75253_b();
    }

    protected double func_111175_f() {
        return 240.0;
    }

    public void func_75249_e() {
        this.guard.func_70624_b(this.targetEntity);
        this.structure.occupySpecialBlock(this.targetEntity.func_180425_c());
        super.func_75249_e();
    }

    public void func_75251_c() {
        super.func_75251_c();
        this.structure.vacateSpecialBlock(this.targetEntity.func_180425_c());
        this.targetEntity = null;
        this.structure = null;
    }
}

