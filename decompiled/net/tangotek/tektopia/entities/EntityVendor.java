/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IMerchant
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.village.MerchantRecipe
 *  net.minecraft.village.MerchantRecipeList
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.VillagerRole;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIWanderStructure;
import net.tangotek.tektopia.tickjob.TickJob;

public abstract class EntityVendor
extends EntityVillagerTek
implements IMerchant {
    @Nullable
    protected EntityPlayer buyingPlayer;
    @Nullable
    protected MerchantRecipeList buyingList;

    public EntityVendor(World worldIn, int roleMask) {
        super(worldIn, null, roleMask | VillagerRole.VENDOR.value);
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();
        this.addJob(new TickJob(200, 200, true, () -> {
            if (this.getCurrentStructure() == null) {
                this.func_70106_y();
            }
        }));
    }

    @Override
    public boolean isMale() {
        return true;
    }

    @Override
    protected void initEntityAIBase() {
    }

    @Override
    protected void func_184651_r() {
        super.func_184651_r();
        this.addTask(50, new EntityAIWanderStructure(this, p -> p.getCurrentStructure(), p -> p.func_70681_au().nextInt(5) == 0, 0));
    }

    protected ItemStack createEmerald(int qty) {
        return new ItemStack(Items.field_151166_bC, qty);
    }

    protected MerchantRecipe createMerchantRecipe(ItemStack item, int emeraldCost) {
        if (emeraldCost <= 64) {
            return new MerchantRecipe(this.createEmerald(emeraldCost), ItemStack.field_190927_a, item, 0, 99999);
        }
        if (emeraldCost % 9 == 0) {
            return new MerchantRecipe(new ItemStack(Item.func_150898_a((Block)Blocks.field_150475_bE), emeraldCost / 9), ItemStack.field_190927_a, item, 0, 99999);
        }
        return new MerchantRecipe(new ItemStack(Item.func_150898_a((Block)Blocks.field_150475_bE), emeraldCost / 9), this.createEmerald(emeraldCost % 9), item, 0, 99999);
    }

    @Override
    public boolean canConvertProfession(ProfessionType pt) {
        return false;
    }

    @Override
    public void addVillagerPosition() {
    }

    public void func_70932_a_(@Nullable EntityPlayer player) {
        this.buyingPlayer = player;
        this.buyingList = null;
    }

    @Nullable
    public EntityPlayer func_70931_l_() {
        return this.buyingPlayer;
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    @Override
    public boolean isSleepingTime() {
        return false;
    }

    @Override
    protected void bedCheck() {
    }

    @Override
    public int getHunger() {
        return this.getMaxHunger();
    }

    @Nullable
    public MerchantRecipeList func_70934_b(EntityPlayer player) {
        if (this.buyingList == null) {
            this.populateBuyingList();
        }
        return this.buyingList;
    }

    @Override
    public boolean func_184645_a(EntityPlayer player, EnumHand hand) {
        boolean flag;
        ItemStack itemstack = player.func_184586_b(hand);
        boolean bl = flag = itemstack.func_77973_b() == Items.field_151057_cb;
        if (flag) {
            itemstack.func_111282_a(player, (EntityLivingBase)this, hand);
        } else if (this.func_70089_S() && !this.isTrading() && !this.func_70631_g_() && !player.func_70093_af()) {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }
            if (this.buyingList != null) {
                if (!this.field_70170_p.field_72995_K && !this.buyingList.isEmpty()) {
                    this.func_70932_a_(player);
                    this.func_70661_as().func_75499_g();
                    player.func_180472_a((IMerchant)this);
                } else if (this.buyingList.isEmpty()) {
                    return super.func_184645_a(player, hand);
                }
            }
        }
        return true;
    }

    protected abstract void populateBuyingList();

    @SideOnly(value=Side.CLIENT)
    public void func_70930_a(@Nullable MerchantRecipeList recipeList) {
    }

    public World func_190670_t_() {
        return this.field_70170_p;
    }

    public void func_70933_a(MerchantRecipe recipe) {
        recipe.func_77399_f();
        this.field_70757_a = -this.func_70627_aG();
        this.func_184185_a(SoundEvents.field_187915_go, this.func_70599_aP(), this.func_70647_i());
        this.setHappy(this.getMaxHappy());
    }

    public void func_110297_a_(ItemStack stack) {
        if (!this.field_70170_p.field_72995_K && this.field_70757_a > -this.func_70627_aG() + 20) {
            this.field_70757_a = -this.func_70627_aG();
            this.func_184185_a(stack.func_190926_b() ? SoundEvents.field_187913_gm : SoundEvents.field_187915_go, this.func_70599_aP(), this.func_70647_i());
        }
    }

    protected abstract String getTranslationKey();

    public ITextComponent func_145748_c_() {
        TextComponentTranslation itextcomponent = new TextComponentTranslation(this.getTranslationKey(), new Object[0]);
        itextcomponent.func_150256_b().func_150209_a(this.func_174823_aP());
        itextcomponent.func_150256_b().func_179989_a(this.func_189512_bd());
        return itextcomponent;
    }

    public BlockPos func_190671_u_() {
        return new BlockPos((Entity)this);
    }
}

