/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.tangotek.tektopia.client.RenderVillager;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class RenderChild<T extends EntityChild>
extends RenderVillager<T> {
    public static final Factory FACTORY = new Factory();

    public RenderChild(RenderManager manager) {
        super(manager, "child", true, 64, 64, "child", 0.15f);
    }

    @Override
    protected void setupTextures() {
        this.maleTextures = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "0_m.png"), new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "1_m.png")};
        this.femaleTextures = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "0_f.png")};
    }

    protected void preRenderCallback(EntityChild entitylivingbaseIn, float partialTickTime) {
        GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
    }

    @Override
    protected ResourceLocation getEntityTexture(T child) {
        if (!((EntityVillagerTek)((Object)child)).isMale()) {
            return this.femaleTextures[0];
        }
        return this.maleTextures[((EntityChild)((Object)child)).getVariation()];
    }

    public static class Factory<T extends EntityChild>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderChild(manager);
        }
    }
}

