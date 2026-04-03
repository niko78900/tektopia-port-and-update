/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.tangotek.tektopia.client.RenderVillager;
import net.tangotek.tektopia.entities.EntityNomad;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class RenderNomad<T extends EntityNomad>
extends RenderVillager<T> {
    public static final Factory FACTORY = new Factory();
    public static final ResourceLocation[] MALE_TEXTURES = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/nomad0M.png"), new ResourceLocation("tektopia", "textures/entity/nomad1M.png")};
    public static final ResourceLocation FEMALE_TEXTURE = new ResourceLocation("tektopia", "textures/entity/nomad0F.png");

    public RenderNomad(RenderManager manager) {
        super(manager, "nomad", false, 64, 64, "nomad");
    }

    @Override
    protected void setupTextures() {
        this.maleTextures = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "0_m.png"), new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "1_m.png")};
        this.femaleTextures = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "0_f.png")};
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        if (!((EntityVillagerTek)((Object)entity)).isMale()) {
            return this.femaleTextures[0];
        }
        return this.maleTextures[(int)(Math.abs(entity.func_110124_au().getLeastSignificantBits()) % 2L)];
    }

    public static class Factory<T extends EntityNomad>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderNomad(manager);
        }
    }
}

