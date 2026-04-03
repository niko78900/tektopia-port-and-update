/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import org.lwjgl.opengl.GL11;

public class LayerVillagerDestination
implements LayerRenderer<EntityLivingBase> {
    protected final RenderLivingBase<?> livingEntityRenderer;

    public LayerVillagerDestination(RenderLivingBase<?> livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void func_177141_a(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn instanceof EntityVillagerTek) {
            EntityVillagerTek villager = (EntityVillagerTek)entitylivingbaseIn;
            GL11.glPushMatrix();
            GL11.glPushAttrib((int)8192);
            Vec3d pos = villager.func_174791_d();
            GL11.glTranslated((double)(-pos.field_72450_a), (double)(-pos.field_72448_b), (double)(-pos.field_72449_c));
            GL11.glDisable((int)2896);
            GL11.glDisable((int)3553);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    public boolean func_177142_b() {
        return false;
    }
}

