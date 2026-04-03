/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAITarget
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.world.IBlockAccess
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.world.IBlockAccess;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityAIProtectVillage
extends EntityAITarget {
    EntityVillagerTek villager;
    EntityLivingBase villageEnemy;
    protected final Predicate<EntityVillagerTek> shouldPred;

    public EntityAIProtectVillage(EntityVillagerTek villager, Predicate<EntityVillagerTek> shouldPred) {
        super((EntityCreature)villager, false, false);
        this.villager = villager;
        this.func_75248_a(1);
        this.shouldPred = shouldPred;
    }

    public boolean func_75250_a() {
        if (this.villager.hasVillage() && this.shouldPred.test(this.villager)) {
            Path path;
            this.villageEnemy = this.villager.getVillage().getEnemyTarget(this.villager);
            if (this.villageEnemy != null && this.func_75296_a(this.villageEnemy, false) && (path = this.villager.getPathFinder().func_186333_a((IBlockAccess)this.villager.field_70170_p, (EntityLiving)this.villager, (Entity)this.villageEnemy, 240.0f)) != null) {
                return true;
            }
        }
        return false;
    }

    protected double func_111175_f() {
        return 240.0;
    }

    public void func_75249_e() {
        this.villager.func_70624_b(this.villageEnemy);
        super.func_75249_e();
    }

    public boolean func_75253_b() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.villageEnemy != this.villager.getVillage().getEnemyTarget(this.villager)) {
            return false;
        }
        return super.func_75253_b();
    }
}

