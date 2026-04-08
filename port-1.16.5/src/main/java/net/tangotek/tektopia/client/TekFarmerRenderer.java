package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.entities.TekFarmerEntity;

public class TekFarmerRenderer extends BipedRenderer<TekFarmerEntity, BipedModel<TekFarmerEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/villager/farmer.png");

    public TekFarmerRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BipedModel<>(0.0F), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TekFarmerEntity entity) {
        return TEXTURE;
    }
}
