/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.CSModelRenderer
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.CSModelRenderer;
import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.tangotek.tektopia.client.RenderVillager;
import net.tangotek.tektopia.entities.EntityBard;

public class RenderBard<T extends EntityBard>
extends RenderVillager<T> {
    public static final Factory FACTORY = new Factory();

    public RenderBard(RenderManager manager) {
        super(manager, "bard", false, 128, 64, "bard");
    }

    private void updateInstrument(CSModelRenderer modelRenderer, EntityBard entityBard) {
        if (modelRenderer.field_78802_n.startsWith("Flute")) {
            modelRenderer.field_78806_j = entityBard.isPerforming();
        }
        if (modelRenderer.field_78805_m != null) {
            for (ModelRenderer child : modelRenderer.field_78805_m) {
                this.updateInstrument((CSModelRenderer)child, entityBard);
            }
        }
    }

    protected void preRenderCallback(EntityBard entityBard, float partialTickTime) {
        ModelCraftStudio model = (ModelCraftStudio)this.func_177087_b();
        for (CSModelRenderer parent : model.getParentBlocks()) {
            this.updateInstrument(parent, entityBard);
        }
    }

    public static class Factory<T extends EntityBard>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderBard(manager);
        }
    }
}

