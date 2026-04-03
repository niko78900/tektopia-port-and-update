/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.pathfinding.PathNavigateGround
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIFollow;

public abstract class EntityAIAttachLeadToAnimal
extends EntityAIFollow {
    protected EntityAnimal targetAnimal = null;
    private boolean active = false;
    private ItemStack leadItem = new ItemStack(Items.field_151058_ca);
    private int leashTime = 0;
    private Predicate<EntityVillagerTek> shouldPred;
    protected final EntityVillagerTek villager;

    public EntityAIAttachLeadToAnimal(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred) {
        super(v);
        this.villager = v;
        this.shouldPred = shouldPred;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.hasVillage() && this.shouldPred != null && this.shouldPred.test(this.villager)) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    public void func_75249_e() {
        this.active = true;
        this.villager.equipActionItem(this.leadItem);
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        return this.targetAnimal != null && this.active;
    }

    @Override
    public void func_75246_d() {
        if (this.leashTime > 0) {
            this.villager.func_70671_ap().func_75650_a((double)this.destinationPos.func_177958_n(), (double)this.destinationPos.func_177956_o(), (double)this.destinationPos.func_177952_p(), 30.0f, 30.0f);
            --this.leashTime;
            if (this.leashTime == 0) {
                this.attachLeash();
                this.active = false;
            }
        } else {
            super.func_75246_d();
        }
    }

    public boolean func_75252_g() {
        return this.leashTime <= 0;
    }

    @Override
    protected void onArrival() {
        if (this.targetAnimal.func_110166_bE() instanceof EntityMerchant) {
            this.targetAnimal.func_110160_i(true, false);
        }
        if (this.targetAnimal.func_110166_bE() != null) {
            this.active = false;
        } else {
            this.startLeash();
        }
        super.onArrival();
    }

    @Override
    protected void onStuck() {
        this.active = false;
        super.onStuck();
    }

    @Override
    protected void onPathFailed(BlockPos pos) {
        this.active = false;
        super.onPathFailed(pos);
    }

    protected void attachLeash() {
        this.targetAnimal.func_110162_b((Entity)this.villager, true);
        this.villager.setLeadAnimal(this.targetAnimal);
        this.villager.throttledSadness(-3);
        if (this.targetAnimal.func_70661_as() instanceof PathNavigateGround) {
            PathNavigateGround navGround = (PathNavigateGround)this.targetAnimal.func_70661_as();
            navGround.func_189566_q().func_186317_a(true);
            navGround.func_189566_q().func_186321_b(true);
        }
        this.active = false;
    }

    private void startLeash() {
        this.villager.debugOut("Attaching lead to animal [ " + this.targetAnimal.func_174791_d() + "]");
        this.leashTime = 36;
        this.villager.func_70661_as().func_75499_g();
        this.villager.playServerAnimation("villager_take");
    }

    private void stopLeash() {
        this.villager.stopServerAnimation("villager_take");
    }

    @Override
    public void func_75251_c() {
        this.leashTime = 0;
        this.active = false;
        this.targetAnimal = null;
        this.villager.unequipActionItem(this.leadItem);
        this.stopLeash();
        super.func_75251_c();
    }
}

