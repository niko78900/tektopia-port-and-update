/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.ResourceLocation
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class LayerRancherHat
implements LayerRenderer<EntityLivingBase> {
    private final RenderLivingBase<?> renderer;
    private final ModelCraftStudio model;
    private final ResourceLocation texture;

    public LayerRancherHat(RenderLivingBase<?> rendererIn) {
        this.renderer = rendererIn;
        this.model = new ModelCraftStudio("tektopia", "rancher_hat", 128, 64);
        this.texture = new ResourceLocation("tektopia", "textures/entity/rancher_m.png");
    }

    public void func_177141_a(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.func_179094_E();
        this.renderer.func_110776_a(this.texture);
        float rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        float rotateAngleX = headPitch * ((float)Math.PI / 180);
        if (entityIn.func_70093_af()) {
            GlStateManager.func_179109_b((float)0.0f, (float)1.6f, (float)0.0f);
        }
        GlStateManager.func_179114_b((float)(rotateAngleY * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)(rotateAngleX * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        this.model.render();
        GlStateManager.func_179121_F();
    }

    public boolean func_177142_b() {
        return false;
    }
}

