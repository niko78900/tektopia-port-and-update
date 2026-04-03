/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.leviathanstudio.craftstudio.client.model.CSModelRenderer
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package net.tangotek.tektopia.client;

import com.google.common.collect.Maps;
import com.leviathanstudio.craftstudio.client.model.CSModelRenderer;
import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import java.util.Map;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class LayerVillagerArmor
implements LayerRenderer<EntityLivingBase> {
    private final RenderLivingBase<?> renderer;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private float alpha = 1.0f;
    private float colorR = 1.0f;
    private float colorG = 1.0f;
    private float colorB = 1.0f;

    public LayerVillagerArmor(RenderLivingBase<?> rendererIn) {
        this.renderer = rendererIn;
    }

    public void func_177141_a(EntityLivingBase entityVillager, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean showArmor = true;
        if (entityVillager instanceof EntityVillagerTek) {
            boolean bl = showArmor = !((EntityVillagerTek)entityVillager).isSleeping();
        }
        if (showArmor) {
            ModelCraftStudio model = (ModelCraftStudio)this.renderer.func_177087_b();
            for (CSModelRenderer parent : model.getParentBlocks()) {
                this.renderArmorModel(parent, entityVillager, scale);
            }
        }
    }

    protected void renderArmorModel(CSModelRenderer modelRenderer, EntityLivingBase entityVillager, float scale) {
        this.renderArmorSlot(modelRenderer, entityVillager, "ArmorChest", EntityEquipmentSlot.CHEST, scale);
        this.renderArmorSlot(modelRenderer, entityVillager, "ArmorLeg", EntityEquipmentSlot.LEGS, scale);
        this.renderArmorSlot(modelRenderer, entityVillager, "ArmorHead", EntityEquipmentSlot.HEAD, scale);
        this.renderArmorSlot(modelRenderer, entityVillager, "ArmorFeet", EntityEquipmentSlot.FEET, scale);
        if (modelRenderer.field_78805_m != null) {
            for (ModelRenderer child : modelRenderer.field_78805_m) {
                this.renderArmorModel((CSModelRenderer)child, entityVillager, scale);
            }
        }
        GlStateManager.func_179121_F();
    }

    private void renderArmorSlot(CSModelRenderer modelRenderer, EntityLivingBase entityVillager, String modelPrefix, EntityEquipmentSlot slot, float scale) {
        ItemArmor itemArmor;
        ItemStack itemStack;
        if (modelRenderer.field_78802_n.startsWith(modelPrefix) && (itemStack = entityVillager.func_184582_a(slot)).func_77973_b() instanceof ItemArmor && (itemArmor = (ItemArmor)itemStack.func_77973_b()).func_185083_B_() == slot) {
            int i = itemArmor.func_82814_b(itemStack);
            float r = (float)(i >> 16 & 0xFF) / 255.0f;
            float g = (float)(i >> 8 & 0xFF) / 255.0f;
            float b = (float)(i & 0xFF) / 255.0f;
            GlStateManager.func_179131_c((float)(this.colorR * r), (float)(this.colorG * g), (float)(this.colorB * b), (float)this.alpha);
            modelRenderer.field_78806_j = true;
            modelRenderer.func_78785_a(scale);
            modelRenderer.field_78806_j = false;
        }
    }

    public boolean func_177142_b() {
        return false;
    }

    private ResourceLocation getArmorResource(String domain) {
        String s = String.format("%s:textures/models/armor/armor.png", domain);
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s);
        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s);
            ARMOR_TEXTURE_RES_MAP.put(s, resourcelocation);
        }
        return resourcelocation;
    }
}

