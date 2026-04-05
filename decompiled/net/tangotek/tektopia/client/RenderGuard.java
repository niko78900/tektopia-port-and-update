/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.CSModelRenderer
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.CSModelRenderer;
import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.tangotek.tektopia.client.RenderVillager;
import net.tangotek.tektopia.entities.EntityGuard;

public class RenderGuard<T extends EntityGuard>
extends RenderVillager<T> {
    public static final Factory FACTORY = new Factory();

    public RenderGuard(RenderManager manager) {
        super(manager, "guard", false, 128, 128, "guard");
    }

    private void updateArmorSlot(CSModelRenderer modelRenderer, EntityGuard entityGuard, String modelPrefix, EntityEquipmentSlot slot) {
        ItemArmor itemArmor;
        ItemStack itemStack;
        if (modelRenderer.field_78802_n.startsWith(modelPrefix) && (itemStack = entityGuard.func_184582_a(slot)).func_77973_b() instanceof ItemArmor && (itemArmor = (ItemArmor)itemStack.func_77973_b()).func_185083_B_() == slot) {
            String matName = itemArmor.func_82812_d().func_179242_c();
            String modelMat = modelRenderer.field_78802_n.substring(modelPrefix.length());
            modelRenderer.field_78806_j = modelMat.toLowerCase().startsWith(matName);
            if (modelRenderer.field_78806_j && modelRenderer.field_78802_n.substring(modelPrefix.length() + matName.length()).startsWith("Capt")) {
                modelRenderer.field_78806_j = entityGuard.isCaptain();
            }
        }
    }

    private void updateArmor(CSModelRenderer modelRenderer, EntityGuard entityGuard) {
        boolean bl = modelRenderer.field_78806_j = !modelRenderer.field_78802_n.startsWith("Armor");
        if (!entityGuard.isSleeping()) {
            this.updateArmorSlot(modelRenderer, entityGuard, "ArmorChest", EntityEquipmentSlot.CHEST);
            if (!modelRenderer.field_78806_j) {
                this.updateArmorSlot(modelRenderer, entityGuard, "ArmorLeg", EntityEquipmentSlot.LEGS);
            }
            if (!modelRenderer.field_78806_j) {
                this.updateArmorSlot(modelRenderer, entityGuard, "ArmorHead", EntityEquipmentSlot.HEAD);
            }
            if (!modelRenderer.field_78806_j) {
                this.updateArmorSlot(modelRenderer, entityGuard, "ArmorFeet", EntityEquipmentSlot.FEET);
            }
        }
        if (modelRenderer.field_78805_m != null) {
            for (ModelRenderer child : modelRenderer.field_78805_m) {
                this.updateArmor((CSModelRenderer)child, entityGuard);
            }
        }
    }

    protected void preRenderCallback(EntityGuard entityGuard, float partialTickTime) {
        ModelCraftStudio model = (ModelCraftStudio)this.func_177087_b();
        for (CSModelRenderer parent : model.getParentBlocks()) {
            this.updateArmor(parent, entityGuard);
        }
    }

    public static class Factory<T extends EntityGuard>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderGuard(manager);
        }
    }
}
