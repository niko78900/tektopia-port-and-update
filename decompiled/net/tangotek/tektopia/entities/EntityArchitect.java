/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.village.MerchantRecipe
 *  net.minecraft.village.MerchantRecipeList
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.entities.EntityVendor;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.items.ItemStructureToken;

public class EntityArchitect
extends EntityVendor {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityArchitect.class);

    public EntityArchitect(World worldIn) {
        super(worldIn, 0);
    }

    @Override
    protected void populateBuyingList() {
        if (this.buyingList == null && this.hasVillage()) {
            this.buyingList = new MerchantRecipeList();
            for (ItemStructureToken token : ModItems.structureTokens) {
                if (token.getCost(this.getVillage()) <= 0) continue;
                ItemStack tokenItem = ModItems.createTaggedItem(token, ItemTagType.VILLAGER);
                ModItems.bindItemToVillage(tokenItem, this.village);
                this.buyingList.add((Object)this.createMerchantRecipe(tokenItem, token.getCost(this.village)));
            }
        }
    }

    @Override
    protected String getTranslationKey() {
        return "entity.villager.architect";
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    public void func_70933_a(MerchantRecipe recipe) {
        super.func_70933_a(recipe);
        if (this.hasVillage() && recipe.func_77397_d().func_77973_b() instanceof ItemStructureToken) {
            ModItems.bindItemToVillage(recipe.func_77397_d(), this.getVillage());
        }
    }

    static {
        EntityVillagerTek.setupAnimations(animHandler, "architect_m");
    }
}

