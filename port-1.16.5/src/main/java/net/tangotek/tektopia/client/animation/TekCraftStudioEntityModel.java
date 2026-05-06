package net.tangotek.tektopia.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.TekTopiaPort;

import java.util.Optional;

public class TekCraftStudioEntityModel<T extends MobEntity> extends BipedModel<T> {
    private final ResourceLocation modelLocation;
    private Optional<TekCraftStudioModel> craftStudioModel = Optional.empty();
    private boolean attemptedLoad;

    public TekCraftStudioEntityModel(ResourceLocation modelLocation) {
        super(0.0F);
        this.modelLocation = modelLocation;
    }

    public boolean hasCraftStudioModel() {
        this.ensureLoaded();
        return this.craftStudioModel.isPresent();
    }

    public Optional<TekCraftStudioModel> craftStudioModel() {
        this.ensureLoaded();
        return this.craftStudioModel;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.ensureLoaded();
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void renderToBuffer(
            MatrixStack matrixStack,
            IVertexBuilder buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        this.ensureLoaded();
        super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void ensureLoaded() {
        if (this.attemptedLoad) {
            return;
        }
        this.attemptedLoad = true;
        this.craftStudioModel = TekCraftStudioModelLoader.load(this.modelLocation);
        this.craftStudioModel.ifPresent(model -> TekTopiaPort.LOGGER.info(
                "Loaded TekTopia CraftStudio model {} title={} cubes={} armorCubes={}",
                model.source(),
                model.title(),
                model.cubeCount(),
                model.armorCubeCount()
        ));
    }
}
