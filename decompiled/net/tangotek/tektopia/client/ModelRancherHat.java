/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ModelRancherHat
extends ModelBiped {
    private final ModelCraftStudio modelHat = new ModelCraftStudio("tektopia", "rancher_hat", 64, 64);

    public ModelRancherHat() {
        super(0.0f, 0.0f, 64, 64);
    }

    public void func_78088_a(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.func_179094_E();
        float rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        float rotateAngleX = headPitch * ((float)Math.PI / 180);
        if (entityIn.func_70093_af()) {
            GlStateManager.func_179109_b((float)0.0f, (float)0.25f, (float)0.0f);
        }
        GlStateManager.func_179114_b((float)(rotateAngleY * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)(rotateAngleX * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        this.modelHat.render();
        GlStateManager.func_179121_F();
    }

    public void func_78087_a(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (entityIn instanceof EntityArmorStand) {
            this.field_78116_c.field_78795_f = headPitch * ((float)Math.PI / 180);
            this.field_78116_c.field_78796_g = netHeadYaw * ((float)Math.PI / 180);
            this.field_78116_c.func_78793_a(0.0f, 1.0f, 0.0f);
            ModelRancherHat.func_178685_a((ModelRenderer)this.field_78116_c, (ModelRenderer)this.field_178720_f);
        }
    }
}

