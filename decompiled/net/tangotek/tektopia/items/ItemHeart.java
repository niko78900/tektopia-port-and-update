/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.items;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ItemHeart
extends Item {
    private String name;

    public ItemHeart(String name) {
        this.func_77637_a(CreativeTabs.field_78026_f);
        this.setRegistryName(name);
        this.func_77655_b(name);
        this.name = name;
    }

    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer(this, 0, this.name);
    }

    public int getEntityLifespan(ItemStack itemStack, World world) {
        return Integer.MAX_VALUE;
    }

    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (entityItem.field_70170_p.field_72995_K) {
            if (entityItem.field_70170_p.field_73012_v.nextInt(17) == 0) {
                entityItem.field_70170_p.func_184134_a((double)((float)entityItem.field_70165_t + 0.5f), (double)((float)entityItem.field_70163_u + 0.5f), (double)((float)entityItem.field_70161_v + 0.5f), ModSoundEvents.twinkle, SoundCategory.BLOCKS, MathHelper.func_151240_a((Random)entityItem.field_70170_p.field_73012_v, (float)0.8f, (float)1.1f), MathHelper.func_151240_a((Random)entityItem.field_70170_p.field_73012_v, (float)0.7f, (float)1.3f), false);
            }
            if (entityItem.field_70170_p.field_73012_v.nextInt(4) == 0) {
                double d0 = (double)((float)entityItem.field_70165_t) + MathHelper.func_82716_a((Random)field_77697_d, (double)-3.0, (double)3.0);
                double d1 = (double)((float)entityItem.field_70163_u) + MathHelper.func_82716_a((Random)field_77697_d, (double)0.5, (double)2.0);
                double d2 = (double)((float)entityItem.field_70161_v) + MathHelper.func_82716_a((Random)field_77697_d, (double)-3.0, (double)3.0);
                entityItem.field_70170_p.func_175688_a(EnumParticleTypes.HEART, d0, d1, d2, 0.0, field_77697_d.nextDouble() * 0.2, 0.0, new int[0]);
            }
        }
        return super.onEntityItemUpdate(entityItem);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState iblockstate = world.func_180495_p(pos);
        ItemStack itemstack = player.func_184586_b(hand);
        if (player.func_175151_a(pos.func_177972_a(side), side, itemstack) && iblockstate.func_177230_c() == Blocks.field_150324_C) {
            if (world.field_72995_K) {
                for (int i = 0; i < 10; ++i) {
                    double d0 = (double)pos.func_177958_n() + MathHelper.func_82716_a((Random)field_77697_d, (double)-2.0, (double)2.0);
                    double d1 = (double)pos.func_177956_o() + MathHelper.func_82716_a((Random)field_77697_d, (double)0.5, (double)1.5);
                    double d2 = (double)pos.func_177952_p() + MathHelper.func_82716_a((Random)field_77697_d, (double)-2.0, (double)2.0);
                    world.func_175688_a(EnumParticleTypes.HEART, d0, d1, d2, 0.0, field_77697_d.nextDouble() * 0.2, 0.0, new int[0]);
                    d0 = (double)pos.func_177958_n() + MathHelper.func_82716_a((Random)field_77697_d, (double)-1.0, (double)1.0);
                    d1 = (double)pos.func_177956_o() + MathHelper.func_82716_a((Random)field_77697_d, (double)0.5, (double)1.5);
                    d2 = (double)pos.func_177952_p() + MathHelper.func_82716_a((Random)field_77697_d, (double)-1.0, (double)1.0);
                    world.func_175688_a(EnumParticleTypes.VILLAGER_HAPPY, d0, d1, d2, 0.0, field_77697_d.nextDouble() * 0.2, 0.0, new int[0]);
                    d0 = (double)pos.func_177958_n() + MathHelper.func_82716_a((Random)field_77697_d, (double)-1.0, (double)1.0);
                    d1 = (double)pos.func_177956_o() + MathHelper.func_82716_a((Random)field_77697_d, (double)0.5, (double)1.5);
                    d2 = (double)pos.func_177952_p() + MathHelper.func_82716_a((Random)field_77697_d, (double)-1.0, (double)1.0);
                    world.func_175688_a(EnumParticleTypes.VILLAGER_HAPPY, d0, d1, d2, 0.0, field_77697_d.nextDouble() * 0.2, 0.0, new int[0]);
                }
                return EnumActionResult.SUCCESS;
            }
            String parentLastName = "";
            if (itemstack.func_179543_a("village").func_186855_b("parent")) {
                UUID parentUUID = itemstack.func_179543_a("village").func_186857_a("parent");
                List parents = world.func_175647_a(EntityVillagerTek.class, new AxisAlignedBB(pos).func_72314_b(200.0, 255.0, 200.0), p -> p.func_110124_au() == parentUUID);
                if (!parents.isEmpty()) {
                    EntityVillagerTek parent = (EntityVillagerTek)((Object)parents.get(0));
                    parentLastName = parent.getLastName();
                }
            }
            itemstack.func_190918_g(1);
            EntityChild child = new EntityChild(world);
            child.func_70012_b((double)pos.func_177958_n() + 0.5, pos.func_177956_o() + 1, (double)pos.func_177952_p() + 0.5, 0.0f, 0.0f);
            child.func_180482_a(world.func_175649_E(pos), null);
            String childFirstName = child.getFirstName();
            if (!parentLastName.isEmpty() && !childFirstName.isEmpty()) {
                child.func_96094_a(childFirstName + " " + parentLastName);
            }
            world.func_72838_d((Entity)child);
            world.func_184133_a((EntityPlayer)null, pos, ModSoundEvents.villagerHeartMagic, SoundCategory.BLOCKS, 1.5f, 1.0f);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}

