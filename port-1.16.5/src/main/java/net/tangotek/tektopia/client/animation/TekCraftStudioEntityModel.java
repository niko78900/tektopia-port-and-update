package net.tangotek.tektopia.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.tangotek.tektopia.TekTopiaPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TekCraftStudioEntityModel<T extends MobEntity> extends BipedModel<T> {
    private final ResourceLocation modelLocation;
    private Optional<TekCraftStudioModel> craftStudioModel = Optional.empty();
    private List<CraftPart> roots = Collections.emptyList();
    private List<CraftPart> parts = Collections.emptyList();
    private Map<String, CraftPart> partsByName = Collections.emptyMap();
    private boolean attemptedLoad;

    public TekCraftStudioEntityModel(ResourceLocation modelLocation) {
        super(0.0F);
        this.modelLocation = modelLocation;
        this.texWidth = 64;
        this.texHeight = 64;
    }

    public boolean hasCraftStudioModel() {
        this.ensureLoaded();
        return this.craftStudioModel.isPresent();
    }

    public Optional<TekCraftStudioModel> craftStudioModel() {
        this.ensureLoaded();
        return this.craftStudioModel;
    }

    public boolean hasRenderableCraftStudioModel() {
        this.ensureLoaded();
        return this.craftStudioModel.isPresent() && !this.roots.isEmpty();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.ensureLoaded();
        if (!this.hasRenderableCraftStudioModel()) {
            super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            return;
        }
        Optional<String> animationKey = TekCraftStudioAnimationLibrary.resolve(entity, limbSwingAmount);
        if (animationKey.isPresent()
                && TekCraftStudioAnimationLibrary.load(animationKey.get())
                        .map(animation -> this.applyAnimation(animation, animationKey.get(), limbSwing, ageInTicks, netHeadYaw, headPitch))
                        .orElse(false)) {
            return;
        }
        this.applyInternalClip(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
        if (!this.hasRenderableCraftStudioModel()) {
            super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }
        matrixStack.pushPose();
        for (CraftPart root : this.roots) {
            root.part.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
        matrixStack.popPose();
    }

    private void ensureLoaded() {
        if (this.attemptedLoad) {
            return;
        }
        this.attemptedLoad = true;
        this.craftStudioModel = TekCraftStudioModelLoader.load(this.modelLocation);
        this.craftStudioModel.ifPresent(model -> {
            this.buildRenderableParts(model);
            TekTopiaPort.LOGGER.info(
                    "Loaded TekTopia CraftStudio model {} title={} cubes={} armorCubes={} renderRoots={}",
                    model.source(),
                    model.title(),
                    model.cubeCount(),
                    model.armorCubeCount(),
                    this.roots.size()
            );
        });
    }

    private void buildRenderableParts(TekCraftStudioModel model) {
        List<CraftPart> builtRoots = new ArrayList<>();
        List<CraftPart> builtParts = new ArrayList<>();
        Map<String, CraftPart> byName = new LinkedHashMap<>();
        for (TekCraftStudioModel.Cube cube : model.roots()) {
            builtRoots.add(this.buildPart(cube, builtParts, byName));
        }
        this.roots = Collections.unmodifiableList(builtRoots);
        this.parts = Collections.unmodifiableList(builtParts);
        this.partsByName = Collections.unmodifiableMap(byName);
    }

    private CraftPart buildPart(TekCraftStudioModel.Cube cube, List<CraftPart> builtParts, Map<String, CraftPart> byName) {
        ModelRenderer part = new ModelRenderer(this);
        int[] tex = cube.texOffset();
        float[] size = cube.size();
        float[] offset = cube.offsetFromPivot();
        float[] position = cube.position();
        float[] rotation = cube.rotation();
        float width = Math.max(0.0F, size[0]);
        float height = Math.max(0.0F, size[1]);
        float depth = Math.max(0.0F, size[2]);
        part.texOffs(tex[0], tex[1]).addBox(
                offset[0] - width / 2.0F,
                -offset[1] - height / 2.0F,
                offset[2] - depth / 2.0F,
                width,
                height,
                depth
        );
        float baseX = position[0];
        float baseY = 24.0F - position[1];
        float baseZ = position[2];
        part.setPos(baseX, baseY, baseZ);
        part.xRot = rotation[0];
        part.yRot = rotation[1];
        part.zRot = rotation[2];
        CraftPart craftPart = new CraftPart(cube.name(), part, baseX, baseY, baseZ, rotation[0], rotation[1], rotation[2]);
        builtParts.add(craftPart);
        byName.put(craftPart.name, craftPart);
        for (TekCraftStudioModel.Cube child : cube.children()) {
            CraftPart childPart = this.buildPart(child, builtParts, byName);
            part.addChild(childPart.part);
        }
        return craftPart;
    }

    private boolean applyAnimation(
            TekCraftStudioAnimation animation,
            String animationKey,
            float limbSwing,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (this.partsByName.isEmpty()) {
            return false;
        }
        for (CraftPart craftPart : this.parts) {
            craftPart.reset();
        }
        float frame = this.animationFrame(animation, animationKey, limbSwing, ageInTicks);
        boolean applied = false;
        for (CraftPart craftPart : this.parts) {
            TekCraftStudioAnimation.NodeAnimation node = animation.node(craftPart.name);
            if (node == null) {
                continue;
            }
            float[] position = node.samplePosition(frame, animation.duration(), animation.holdLastKeyframe());
            if (position != null) {
                craftPart.applyPosition(position);
                applied = true;
            }
            float[] rotation = node.sampleRotation(frame, animation.duration(), animation.holdLastKeyframe());
            if (rotation != null) {
                craftPart.applyRotationDegrees(rotation);
                applied = true;
            }
        }
        this.applyHeadLook(netHeadYaw, headPitch);
        return applied;
    }

    private float animationFrame(TekCraftStudioAnimation animation, String animationKey, float limbSwing, float ageInTicks) {
        if (animationKey.contains("walk") || animationKey.contains("run")) {
            return (limbSwing * 12.0F) % animation.duration();
        }
        return ageInTicks % animation.duration();
    }

    private void applyInternalClip(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float walk = Math.min(1.0F, limbSwingAmount);
        float armSwing = MathHelper.cos(limbSwing * 0.6662F) * 0.85F * walk;
        float legSwing = MathHelper.cos(limbSwing * 0.6662F) * 1.05F * walk;
        float idle = MathHelper.sin(ageInTicks * 0.06F) * 0.025F;
        for (CraftPart craftPart : this.parts) {
            craftPart.reset();
            String name = craftPart.name;
            if (name.contains("head")) {
                craftPart.applyHeadLook(netHeadYaw, headPitch);
            } else if (name.contains("armleft")) {
                craftPart.part.xRot += -armSwing;
            } else if (name.contains("armright")) {
                craftPart.part.xRot += armSwing;
            } else if (name.contains("legleft")) {
                craftPart.part.xRot += legSwing;
            } else if (name.contains("legright")) {
                craftPart.part.xRot += -legSwing;
            } else if (name.contains("body")) {
                craftPart.part.xRot += idle;
            }
        }
    }

    private void applyHeadLook(float netHeadYaw, float headPitch) {
        for (CraftPart craftPart : this.parts) {
            if (craftPart.name.contains("head")) {
                craftPart.applyHeadLook(netHeadYaw, headPitch);
            }
        }
    }

    private static final class CraftPart {
        private final String name;
        private final ModelRenderer part;
        private final float baseX;
        private final float baseY;
        private final float baseZ;
        private final float baseXRot;
        private final float baseYRot;
        private final float baseZRot;

        private CraftPart(
                String name,
                ModelRenderer part,
                float baseX,
                float baseY,
                float baseZ,
                float baseXRot,
                float baseYRot,
                float baseZRot
        ) {
            this.name = name == null ? "" : name.toLowerCase(java.util.Locale.ROOT);
            this.part = part;
            this.baseX = baseX;
            this.baseY = baseY;
            this.baseZ = baseZ;
            this.baseXRot = baseXRot;
            this.baseYRot = baseYRot;
            this.baseZRot = baseZRot;
        }

        private void reset() {
            this.part.setPos(this.baseX, this.baseY, this.baseZ);
            this.part.xRot = this.baseXRot;
            this.part.yRot = this.baseYRot;
            this.part.zRot = this.baseZRot;
        }

        private void applyPosition(float[] position) {
            this.part.setPos(this.baseX + position[0], this.baseY - position[1], this.baseZ + position[2]);
        }

        private void applyRotationDegrees(float[] rotation) {
            this.part.xRot = this.baseXRot + degreesToRadians(rotation[0]);
            this.part.yRot = this.baseYRot + degreesToRadians(rotation[1]);
            this.part.zRot = this.baseZRot + degreesToRadians(rotation[2]);
        }

        private void applyHeadLook(float netHeadYaw, float headPitch) {
            this.part.yRot += degreesToRadians(netHeadYaw);
            this.part.xRot += degreesToRadians(headPitch);
        }

        private static float degreesToRadians(float degrees) {
            return degrees * ((float) Math.PI / 180.0F);
        }
    }
}
