/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderLiving
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.tangotek.tektopia.entities.EntityNecromancer;

public class RenderNecromancer<T extends EntityLiving>
extends RenderLiving<T> {
    protected final String textureName;
    public static final Factory FACTORY = new Factory();

    public RenderNecromancer(RenderManager manager, String modelName, int texW, int texH, String textureName, float shadowSize) {
        super(manager, (ModelBase)new ModelCraftStudio("tektopia", modelName, texW, texH), shadowSize);
        this.textureName = textureName;
    }

    public RenderNecromancer(RenderManager manager, String modelName, int texW, int texH, String textureName) {
        this(manager, modelName, texW, texH, textureName, 0.4f);
    }

    protected ResourceLocation getEntityTexture(T entity) {
        return new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "_" + ((EntityNecromancer)((Object)entity)).getLevel() + ".png");
    }

    protected int getTeamColor(T entityIn) {
        return 0x1D1D21;
    }

    public static class Factory<T extends EntityNecromancer>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderNecromancer(manager, "necromancer", 128, 64, "necromancer", 0.4f);
        }
    }
}

