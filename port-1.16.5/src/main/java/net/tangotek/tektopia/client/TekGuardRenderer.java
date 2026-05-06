package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.entities.TekGuardEntity;

public class TekGuardRenderer extends BipedRenderer<TekGuardEntity, BipedModel<TekGuardEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/illager/pillager.png");

    public TekGuardRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BipedModel<>(0.0F), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TekGuardEntity entity) {
        return TEXTURE;
    }
}
