/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityAICreateItem
extends EntityAIBase {
    protected final Predicate<EntityVillagerTek> shouldPred;
    protected final Supplier<ItemStack> supplier;
    protected final EntityVillagerTek villager;

    public EntityAICreateItem(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred, Supplier<ItemStack> supplier) {
        this.villager = v;
        this.shouldPred = shouldPred;
        this.supplier = supplier;
    }

    public boolean func_75250_a() {
        return this.villager.isAITick() && this.villager.hasVillage() && this.shouldPred.test(this.villager);
    }

    public boolean func_75253_b() {
        return false;
    }

    public void func_75249_e() {
        this.villager.getInventory().func_174894_a(this.supplier.get());
    }
}

