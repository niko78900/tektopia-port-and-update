/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.LicenseTracker;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;
import net.tangotek.tektopia.client.ModelRancherHat;

public class ItemRancherHat
extends ItemArmor {
    private String name;
    private ModelRancherHat hatModel;

    public ItemRancherHat(String name) {
        super(Items.field_151161_ac.func_82812_d(), Items.field_151161_ac.field_77880_c, EntityEquipmentSlot.HEAD);
        this.func_77637_a(CreativeTabs.field_78037_j);
        this.setRegistryName(name);
        this.func_77655_b(name);
        this.name = name;
    }

    public void registerItemModel() {
        TekVillager.proxy.registerItemRenderer((Item)this, 0, this.name);
    }

    @SideOnly(value=Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
        if (!stack.func_190926_b() && this.hasLicense((Entity)living, LicenseTracker.Feature.HATS)) {
            if (this.hatModel == null) {
                this.hatModel = new ModelRancherHat();
            }
            return this.hatModel;
        }
        return null;
    }

    private boolean hasLicense(Entity ent, LicenseTracker.Feature feature) {
        if (!(ent instanceof AbstractClientPlayer)) {
            return false;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)ent;
        IPlayerLicense license = (IPlayerLicense)player.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY, null);
        if (license == null) {
            return true;
        }
        return license.hasFeature(feature);
    }

    @Nullable
    @SideOnly(value=Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (this.hasLicense(entity, LicenseTracker.Feature.HATS)) {
            return "tektopia:textures/entity/rancher_hat.png";
        }
        return null;
    }

    @SideOnly(value=Side.CLIENT)
    public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.YELLOW + "" + TextFormatting.BOLD + I18n.func_74838_a((String)"item.rancher_hat.text1"));
        tooltip.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + I18n.func_74838_a((String)"item.rancher_hat.text2"));
        tooltip.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + I18n.func_74838_a((String)"item.rancher_hat.text3"));
    }
}
