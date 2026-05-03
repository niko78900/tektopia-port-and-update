package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class TekBipedRenderer<T extends MobEntity> extends BipedRenderer<T, BipedModel<T>> {
    private final ResourceLocation texture;

    public TekBipedRenderer(EntityRendererManager renderManager, ResourceLocation texture) {
        super(renderManager, new BipedModel<>(0.0F), 0.5F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }
}
