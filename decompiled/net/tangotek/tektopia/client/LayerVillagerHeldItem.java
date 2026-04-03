/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.model.CSModelRenderer
 *  com.leviathanstudio.craftstudio.client.model.ModelCraftStudio
 *  com.leviathanstudio.craftstudio.client.util.MathHelper
 *  javax.vecmath.Matrix4f
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.ModelBook
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.client;

import com.leviathanstudio.craftstudio.client.model.CSModelRenderer;
import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.vecmath.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.entities.EntityVillagerTek;

@SideOnly(value=Side.CLIENT)
public class LayerVillagerHeldItem
implements LayerRenderer<EntityLivingBase> {
    protected final RenderLivingBase<?> livingEntityRenderer;
    private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private final ModelBook modelBook = new ModelBook();

    public LayerVillagerHeldItem(RenderLivingBase<?> livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void func_177141_a(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean flag = entitylivingbaseIn.func_184591_cq() == EnumHandSide.RIGHT;
        ItemStack leftHandItem = flag ? entitylivingbaseIn.func_184592_cb() : entitylivingbaseIn.func_184614_ca();
        ItemStack rightHandItem = flag ? entitylivingbaseIn.func_184614_ca() : entitylivingbaseIn.func_184592_cb();
        boolean showLayer = true;
        if (entitylivingbaseIn instanceof EntityVillagerTek) {
            EntityVillagerTek villager = (EntityVillagerTek)entitylivingbaseIn;
            boolean bl = showLayer = !villager.isSleeping();
            if (!villager.getActionItem().func_190926_b() && (rightHandItem = villager.getActionItem()).func_77973_b() == ModItems.EMPTY_HAND_ITEM.func_77973_b()) {
                rightHandItem = ItemStack.field_190927_a;
            }
        }
        if (!(!showLayer || leftHandItem.func_190926_b() && rightHandItem.func_190926_b())) {
            GlStateManager.func_179094_E();
            if (this.livingEntityRenderer.func_177087_b().field_78091_s) {
                float f = 0.5f;
                GlStateManager.func_179109_b((float)0.0f, (float)0.75f, (float)0.0f);
                GlStateManager.func_179152_a((float)0.5f, (float)0.5f, (float)0.5f);
            }
            this.renderHeldItem(entitylivingbaseIn, rightHandItem, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, ageInTicks, scale);
            this.renderHeldItem(entitylivingbaseIn, leftHandItem, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, ageInTicks, scale);
            GlStateManager.func_179121_F();
        }
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, float ageInTicks, float scale) {
        if (!itemStack.func_190926_b()) {
            boolean flag;
            GlStateManager.func_179094_E();
            if (entity.func_70093_af()) {
                GlStateManager.func_179109_b((float)0.0f, (float)0.2f, (float)0.0f);
            }
            this.translateToHand(handSide, scale);
            GlStateManager.func_179114_b((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            GlStateManager.func_179114_b((float)180.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            boolean bl = flag = handSide == EnumHandSide.LEFT;
            if (itemStack.func_77973_b() == Items.field_151122_aG) {
                this.renderBook(ageInTicks);
            } else {
                Minecraft.func_71410_x().func_175597_ag().func_187462_a(entity, itemStack, transformType, flag);
            }
            GlStateManager.func_179121_F();
        }
    }

    protected void translateToHand(EnumHandSide handSide, float scale) {
        ModelCraftStudio model = (ModelCraftStudio)this.livingEntityRenderer.func_177087_b();
        ArrayDeque<CSModelRenderer> stack = new ArrayDeque<CSModelRenderer>();
        for (CSModelRenderer parent : model.getParentBlocks()) {
            if (!this.findChildChain("ArmRightWrist", parent, stack)) continue;
            stack.push(parent);
            while (!stack.isEmpty()) {
                CSModelRenderer modelRenderer = (CSModelRenderer)stack.pop();
                GlStateManager.func_179109_b((float)(modelRenderer.field_78800_c * scale), (float)(modelRenderer.field_78797_d * scale), (float)(modelRenderer.field_78798_e * scale));
                FloatBuffer buf = com.leviathanstudio.craftstudio.client.util.MathHelper.makeFloatBuffer((Matrix4f)modelRenderer.getRotationMatrix());
                GlStateManager.func_179110_a((FloatBuffer)buf);
                GlStateManager.func_179109_b((float)(modelRenderer.field_82906_o * scale), (float)(modelRenderer.field_82908_p * scale), (float)(modelRenderer.field_82907_q * scale));
            }
        }
    }

    private boolean findChildChain(String name, CSModelRenderer modelRenderer, Deque<CSModelRenderer> stack) {
        if (modelRenderer.field_78802_n.equals(name)) {
            return true;
        }
        if (modelRenderer.field_78805_m != null) {
            for (ModelRenderer child : modelRenderer.field_78805_m) {
                CSModelRenderer csModel = (CSModelRenderer)child;
                if (!this.findChildChain(name, csModel, stack)) continue;
                stack.push(csModel);
                return true;
            }
        }
        return false;
    }

    protected void renderBook(float ageInTicks) {
        GlStateManager.func_179094_E();
        float f = 25.5f;
        GlStateManager.func_179114_b((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        float rot = 80.0f;
        GlStateManager.func_179114_b((float)rot, (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)40.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.func_179137_b((double)0.0, (double)-0.2, (double)-0.3);
        this.livingEntityRenderer.func_110776_a(TEXTURE_BOOK);
        float f3 = f + 0.25f;
        float f4 = f + 0.75f;
        f3 = (f3 - (float)MathHelper.func_76140_b((double)f3)) * 1.6f - 0.3f;
        f4 = (f4 - (float)MathHelper.func_76140_b((double)f4)) * 1.6f - 0.3f;
        if (f3 < 0.0f) {
            f3 = 0.0f;
        }
        if (f4 < 0.0f) {
            f4 = 0.0f;
        }
        if (f3 > 1.0f) {
            f3 = 1.0f;
        }
        if (f4 > 1.0f) {
            f4 = 1.0f;
        }
        float f5 = 12.2f;
        GlStateManager.func_179089_o();
        this.modelBook.func_78088_a((Entity)null, 1500.0f, 0.17f, 0.9f, 1.0f, 0.0f, 0.0625f);
        GlStateManager.func_179121_F();
    }

    public boolean func_177142_b() {
        return false;
    }
}

