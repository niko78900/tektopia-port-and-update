/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLiving
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.util.ResourceLocation
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.client.LayerVillagerHeldItem;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class RenderVillager<T extends EntityVillagerTek>
extends RenderLiving<T> {
    protected final String textureName;
    protected final ModelCraftStudio maleModel;
    protected final ModelCraftStudio femaleModel;
    protected ResourceLocation[] maleTextures;
    protected ResourceLocation[] femaleTextures;

    public RenderVillager(RenderManager manager, String modelName, boolean hasGenderModels, int texW, int texH, String textureName, float shadowSize) {
        super(manager, (ModelBase)new ModelCraftStudio("tektopia", modelName + "_m", texW, texH), shadowSize);
        this.func_177094_a(new LayerVillagerHeldItem((RenderLivingBase<?>)this));
        this.textureName = textureName;
        this.maleModel = (ModelCraftStudio)this.field_77045_g;
        this.femaleModel = hasGenderModels ? new ModelCraftStudio("tektopia", modelName + "_f", texW, texH) : null;
        this.setupTextures();
    }

    public RenderVillager(RenderManager manager, String modelName, boolean hasGenders, int texW, int texH, String textureName) {
        this(manager, modelName, hasGenders, texW, texH, textureName, 0.4f);
    }

    protected void setupTextures() {
        this.maleTextures = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "_m.png")};
        this.femaleTextures = new ResourceLocation[]{new ResourceLocation("tektopia", "textures/entity/" + this.textureName + "_f.png")};
    }

    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.field_77045_g = !((EntityVillagerTek)((Object)entity)).isMale() && this.femaleModel != null ? this.femaleModel : this.maleModel;
        super.func_76986_a(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(T entity) {
        return ((EntityVillagerTek)((Object)entity)).isMale() ? this.maleTextures[0] : this.femaleTextures[0];
    }

    protected void applyRotations(T entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        T villager = entityLiving;
        if (((EntityVillagerTek)((Object)villager)).getForceAxis() >= 0) {
            GlStateManager.func_179114_b((float)(((EntityVillagerTek)((Object)villager)).getForceAxis() * -90), (float)0.0f, (float)1.0f, (float)0.0f);
        } else {
            super.func_77043_a(entityLiving, p_77043_2_, rotationYaw, partialTicks);
        }
    }
}

