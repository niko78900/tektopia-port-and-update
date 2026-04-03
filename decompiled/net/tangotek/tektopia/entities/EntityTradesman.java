/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.util.text.event.ClickEvent
 *  net.minecraft.util.text.event.ClickEvent$Action
 *  net.minecraft.village.MerchantRecipe
 *  net.minecraft.village.MerchantRecipeList
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.entities;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.entities.EntityVendor;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.items.ItemProfessionToken;

public class EntityTradesman
extends EntityVendor {
    protected static AnimationHandler animHandler = TekVillager.getNewAnimationHandler(EntityTradesman.class);

    public EntityTradesman(World worldIn) {
        super(worldIn, 0);
    }

    @Override
    protected void populateBuyingList() {
        if (this.buyingList == null && this.hasVillage()) {
            this.buyingList = new MerchantRecipeList();
            for (ItemProfessionToken token : ModItems.professionTokens.values()) {
                if (token.getCost(this.village) <= 0) continue;
                ItemStack tokenItem = ModItems.createTaggedItem(token, ItemTagType.VILLAGER);
                ModItems.bindItemToVillage(tokenItem, this.village);
                this.buyingList.add((Object)this.createMerchantRecipe(tokenItem, token.getCost(this.village)));
            }
        }
    }

    @Override
    protected String getTranslationKey() {
        return "entity.villager.tradesman";
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return animHandler;
    }

    @Override
    public void func_70933_a(MerchantRecipe recipe) {
        super.func_70933_a(recipe);
        if (this.hasVillage() && recipe.func_77397_d().func_77973_b() instanceof ItemProfessionToken) {
            this.getVillage().getTownData().incrementProfessionSales();
            ModItems.bindItemToVillage(recipe.func_77397_d(), this.getVillage());
            if (this.getVillage().getTownData().getProfessionSales() == 10) {
                TextComponentTranslation textComponentA1 = new TextComponentTranslation("advertisement.patreonA1", new Object[0]);
                TextComponentString textComponentSpace = new TextComponentString(" ");
                TextComponentString linkComponent = new TextComponentString("patreon.com");
                ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.patreon.com/tangotek");
                linkComponent.func_150256_b().func_150241_a(click);
                linkComponent.func_150256_b().func_150228_d(Boolean.valueOf(true));
                linkComponent.func_150256_b().func_150238_a(TextFormatting.BLUE);
                TextComponentTranslation textComponentA2 = new TextComponentTranslation("advertisement.patreonA2", new Object[0]);
                this.getVillage().playEvent(SoundEvents.field_187802_ec, textComponentA1.func_150257_a((ITextComponent)textComponentSpace).func_150257_a((ITextComponent)linkComponent).func_150257_a((ITextComponent)textComponentSpace).func_150257_a((ITextComponent)textComponentA2));
                TextComponentTranslation textComponentB1 = new TextComponentTranslation("advertisement.patreonB1", new Object[0]);
                TextComponentTranslation linkComponentB2 = new TextComponentTranslation("advertisement.patreonB2", new Object[0]);
                ClickEvent clickB = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://pbs.twimg.com/media/EBABNv8UEAAjceo?format=jpg&name=large");
                linkComponentB2.func_150256_b().func_150241_a(clickB);
                linkComponentB2.func_150256_b().func_150228_d(Boolean.valueOf(true));
                linkComponentB2.func_150256_b().func_150238_a(TextFormatting.BLUE);
                TextComponentTranslation textComponentB3 = new TextComponentTranslation("advertisement.patreonB3", new Object[0]);
                this.getVillage().sendChatMessage(textComponentB1.func_150257_a((ITextComponent)textComponentSpace).func_150257_a((ITextComponent)linkComponentB2).func_150257_a((ITextComponent)textComponentSpace).func_150257_a((ITextComponent)textComponentB3));
            }
        }
    }

    static {
        EntityVillagerTek.setupAnimations(animHandler, "tradesman_m");
    }
}

