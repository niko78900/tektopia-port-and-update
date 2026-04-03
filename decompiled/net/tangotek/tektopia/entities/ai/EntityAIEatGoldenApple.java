/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityAIEatGoldenApple
extends EntityAIBase {
    private EntityVillagerTek villager;
    private ItemStack foodItem;
    private int eatTime = 0;

    public EntityAIEatGoldenApple(EntityVillagerTek v) {
        this.villager = v;
        this.func_75248_a(1);
    }

    public boolean func_75252_g() {
        return false;
    }

    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.villager.func_110143_aJ() < this.villager.func_110138_aP() / 2.0f && this.villager.isAIFilterEnabled("eat_golden_apple")) {
            this.foodItem = this.villager.getInventory().getItem(p -> p.func_77973_b() == Items.field_151153_ao && ModItems.isTaggedItem(p, ItemTagType.VILLAGER) ? 1 : 0);
            if (!this.foodItem.func_190926_b()) {
                return true;
            }
        }
        return false;
    }

    public void func_75249_e() {
        this.startEat();
        super.func_75249_e();
    }

    public boolean func_75253_b() {
        return this.eatTime >= 0;
    }

    public void func_75246_d() {
        --this.eatTime;
        if (this.eatTime == 0 && !this.villager.getInventory().removeItems(p -> ItemStack.func_77989_b((ItemStack)p, (ItemStack)this.foodItem), 1).isEmpty()) {
            this.foodItem.func_190918_g(1);
            this.villager.func_70690_d(new PotionEffect(MobEffects.field_76428_l, 200, 1));
            this.villager.func_70690_d(new PotionEffect(MobEffects.field_76444_x, 2400, 0));
        }
        super.func_75246_d();
    }

    private void startEat() {
        this.eatTime = 80;
        this.villager.func_70661_as().func_75499_g();
        this.villager.equipActionItem(this.foodItem);
        this.villager.playServerAnimation("villager_eat");
    }

    private void stopEat() {
        this.villager.unequipActionItem(this.foodItem);
        this.villager.stopServerAnimation("villager_eat");
    }

    public void func_75251_c() {
        super.func_75251_c();
        this.stopEat();
        this.foodItem = null;
        this.eatTime = 0;
    }
}

