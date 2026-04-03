/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.caps.VillageDataProvider;

public class ItemStructureToken
extends Item {
    private int cost;
    private String name;

    public ItemStructureToken(String name, int cost) {
        this.func_77637_a(CreativeTabs.field_78026_f);
        this.func_77625_d(1);
        this.setRegistryName(name);
        this.func_77655_b(name);
        this.name = name;
        this.cost = cost;
    }

    public int getCost(Village v) {
        float mult = Math.min((float)(v.getTownData().getProfessionSales() / 5) * 0.2f, 10.0f);
        return (int)((float)this.cost * (1.0f + mult));
    }

    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer(this, 0, this.name);
    }

    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        if (((Object)((Object)this)).equals((Object)ModItems.structureTownHall)) {
            return new VillageDataProvider();
        }
        return super.initCapabilities(stack, nbt);
    }

    @SideOnly(value=Side.CLIENT)
    public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IVillageData vd;
        if (((Object)((Object)this)).equals((Object)ModItems.structureTownHall) && (vd = (IVillageData)stack.getCapability(VillageDataProvider.VILLAGE_DATA_CAPABILITY, null)) != null && vd.getEconomy().getSalesHistorySize() > 0) {
            tooltip.add(TextFormatting.RED + I18n.func_74838_a((String)"tooltips.saleshistory") + " " + vd.getEconomy().getSalesHistorySize());
        }
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_77636_d(ItemStack stack) {
        return stack.func_82839_y() && ModItems.isTaggedItem(stack, ItemTagType.STRUCTURE);
    }
}

