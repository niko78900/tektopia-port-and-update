/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.EnchantmentData
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemEnchantedBook
 *  net.minecraft.item.ItemStack
 */
package net.tangotek.tektopia.entities.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.entities.EntityEnchanter;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAICraftItems;
import net.tangotek.tektopia.entities.crafting.Recipe;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityAIEnchant
extends EntityAICraftItems {
    private EntityItem floatingItem;

    public EntityAIEnchant(EntityVillagerTek v, List<Recipe> recipes, String anim, ItemStack heldItem, int craftTime, VillageStructureType structureType, Block machineBlock, Predicate<EntityVillagerTek> shouldPred, int skillChance) {
        super(v, recipes, anim, heldItem, craftTime, structureType, machineBlock, shouldPred);
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        if (this.floatingItem != null) {
            this.floatingItem.field_70177_z += 1.0f;
            this.floatingItem.field_70126_B = this.floatingItem.field_70177_z;
            this.floatingItem.field_70181_x = 0.0;
        }
    }

    @Override
    protected ItemStack craftItem(EntityVillagerTek villager) {
        ItemStack enchantedItem = super.craftItem(villager);
        if (enchantedItem != null && enchantedItem.func_77948_v()) {
            boolean wasVillagerItem = ModItems.isTaggedItem(enchantedItem, ItemTagType.VILLAGER);
            int enchantLevel = 5 + villager.getSkillLerp(ProfessionType.ENCHANTER, 1, 30);
            if (!wasVillagerItem) {
                enchantLevel = Math.max(1, enchantLevel / 5);
            }
            boolean isBook = false;
            List<EnchantmentData> list = new ArrayList();
            enchantedItem = new ItemStack(enchantedItem.func_77973_b(), 1, enchantedItem.func_77960_j());
            for (int i = 0; i < 10; ++i) {
                if (enchantedItem.func_77973_b() == Items.field_151134_bR) {
                    list = EnchantmentHelper.func_77513_b((Random)villager.func_70681_au(), (ItemStack)new ItemStack(Items.field_151122_aG, 1, enchantedItem.func_77960_j()), (int)enchantLevel, (boolean)true);
                    isBook = true;
                } else {
                    list = EnchantmentHelper.func_77513_b((Random)villager.func_70681_au(), (ItemStack)enchantedItem, (int)enchantLevel, (boolean)true);
                }
                list.removeIf(e -> e.field_76302_b.func_190936_d());
                if (!list.isEmpty()) break;
            }
            for (EnchantmentData enchantmentdata : list) {
                if (isBook) {
                    ItemEnchantedBook.func_92115_a((ItemStack)enchantedItem, (EnchantmentData)enchantmentdata);
                    continue;
                }
                enchantedItem.func_77966_a(enchantmentdata.field_76302_b, enchantmentdata.field_76303_c);
            }
            if (wasVillagerItem) {
                ModItems.makeTaggedItem(enchantedItem, ItemTagType.VILLAGER);
            }
        }
        return enchantedItem;
    }

    @Override
    public boolean func_75250_a() {
        return super.func_75250_a();
    }

    @Override
    protected void onArrival() {
        super.onArrival();
        this.clearFloater();
        this.floatingItem = new EntityItem(this.villager.field_70170_p, (double)this.destinationPos.func_177958_n() + 0.5, (double)this.destinationPos.func_177956_o() + 1.6, (double)this.destinationPos.func_177952_p() + 0.5, this.activeRecipe.getProduct().func_77946_l());
        this.floatingItem.func_189654_d(true);
        this.floatingItem.lifespan = 6000;
        this.floatingItem.func_174871_r();
        this.floatingItem.field_70159_w = 0.0;
        this.floatingItem.field_70181_x = 0.0;
        this.floatingItem.field_70179_y = 0.0;
        this.villager.addJob(new TickJob(16, 0, false, () -> {
            if (this.floatingItem != null) {
                this.villager.field_70170_p.func_72838_d((Entity)this.floatingItem);
            }
        }));
    }

    private void clearFloater() {
        if (this.floatingItem != null) {
            this.floatingItem.func_70106_y();
            this.floatingItem = null;
        }
    }

    @Override
    protected void startCraft() {
        super.startCraft();
        if (this.villager instanceof EntityEnchanter) {
            EntityEnchanter enchanter = (EntityEnchanter)this.villager;
            enchanter.setCasting(this.destinationPos);
        }
    }

    @Override
    protected void stopCraft() {
        super.stopCraft();
        if (this.villager instanceof EntityEnchanter) {
            EntityEnchanter enchanter = (EntityEnchanter)this.villager;
            enchanter.setCasting(null);
        }
    }

    @Override
    public void func_75251_c() {
        this.clearFloater();
        super.func_75251_c();
    }
}

