package net.tangotek.tektopia.client.animation;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.TekTopiaPort;

public class TekCraftStudioRenderer<T extends MobEntity> extends BipedRenderer<T, TekCraftStudioEntityModel<T>> {
    private final TekCraftStudioEntityModel<T> model;
    private final ResourceLocation texture;
    private final ResourceLocation fallbackTexture;
    private Boolean textureAvailable;
    private boolean fallbackLogged;

    public TekCraftStudioRenderer(
            EntityRendererManager renderManager,
            ResourceLocation modelLocation,
            ResourceLocation texture,
            ResourceLocation fallbackTexture
    ) {
        this(renderManager, new TekCraftStudioEntityModel<>(modelLocation), texture, fallbackTexture);
    }

    private TekCraftStudioRenderer(
            EntityRendererManager renderManager,
            TekCraftStudioEntityModel<T> model,
            ResourceLocation texture,
            ResourceLocation fallbackTexture
    ) {
        super(renderManager, model, 0.5F);
        this.model = model;
        this.texture = texture;
        this.fallbackTexture = fallbackTexture;
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (this.model.hasRenderableCraftStudioModel() && this.hasTexture()) {
            return this.texture;
        }

        if (!this.fallbackLogged) {
            this.fallbackLogged = true;
            TekTopiaPort.LOGGER.warn(
                    "Using Biped fallback texture {} for {} because CraftStudio assets are incomplete",
                    this.fallbackTexture,
                    entity.getType().getRegistryName()
            );
        }
        return this.fallbackTexture;
    }

    private boolean hasTexture() {
        if (this.textureAvailable == null) {
            this.textureAvailable = TekCraftStudioModelLoader.resourceExists(this.texture);
            if (!this.textureAvailable) {
                TekTopiaPort.LOGGER.warn("Missing TekTopia CraftStudio texture {}", this.texture);
            }
        }
        return this.textureAvailable;
    }
}
