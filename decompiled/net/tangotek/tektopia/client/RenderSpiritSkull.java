/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelSkeletonHead
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.entities.EntitySpiritSkull;

@SideOnly(value=Side.CLIENT)
public class RenderSpiritSkull
extends Render<EntitySpiritSkull> {
    private static final ResourceLocation SKULL_TEXTURE = new ResourceLocation("tektopia", "textures/entity/spirit_skull.png");
    public static final Factory FACTORY = new Factory();
    private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

    public RenderSpiritSkull(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    private float getRenderYaw(float p_82400_1_, float p_82400_2_, float p_82400_3_) {
        float f;
        for (f = p_82400_2_ - p_82400_1_; f < -180.0f; f += 360.0f) {
        }
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return p_82400_1_ + p_82400_3_ * f;
    }

    public void doRender(EntitySpiritSkull entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.func_82150_aj()) {
            return;
        }
        GlStateManager.func_179094_E();
        GlStateManager.func_179129_p();
        float f = this.getRenderYaw(entity.field_70126_B, entity.field_70177_z, partialTicks);
        float f1 = entity.field_70127_C + (entity.field_70125_A - entity.field_70127_C) * partialTicks;
        GlStateManager.func_179109_b((float)((float)x), (float)((float)y), (float)((float)z));
        if (entity.getSkullMode() == EntitySpiritSkull.SkullMode.PROTECTING) {
            float radius = entity.getSpinRadius();
            float speed = entity.getSpinSpeed();
            float xAxis = entity.getSpinAxis();
            GlStateManager.func_179114_b((float)(((float)entity.field_70173_aa + partialTicks) * speed), (float)xAxis, (float)1.0f, (float)0.0f);
            GlStateManager.func_179109_b((float)0.0f, (float)0.0f, (float)radius);
        }
        float f2 = 0.0625f;
        GlStateManager.func_179091_B();
        float scale = 0.6f;
        GlStateManager.func_179152_a((float)(-scale), (float)(-scale), (float)scale);
        GlStateManager.func_179141_d();
        this.func_180548_c(entity);
        if (this.field_188301_f) {
            GlStateManager.func_179142_g();
            GlStateManager.func_187431_e((int)this.func_188298_c(entity));
        }
        this.skeletonHeadModel.func_78088_a((Entity)entity, 0.0f, 0.0f, 0.0f, f, f1, 0.0625f);
        if (this.field_188301_f) {
            GlStateManager.func_187417_n();
            GlStateManager.func_179119_h();
        }
        GlStateManager.func_179121_F();
        super.func_76986_a((Entity)entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntitySpiritSkull entity) {
        return SKULL_TEXTURE;
    }

    public static class Factory<T extends EntitySpiritSkull>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderSpiritSkull(manager);
        }
    }
}

