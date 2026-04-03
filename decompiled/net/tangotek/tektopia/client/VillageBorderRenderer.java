/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tangotek.tektopia.VillageClient;

public class VillageBorderRenderer {
    private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");
    private VillageClient villageClient = null;

    public VillageBorderRenderer() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        if (this.villageClient == null) {
            return;
        }
        Minecraft minecraft = Minecraft.func_71410_x();
        WorldClient world = minecraft.field_71441_e;
        EntityPlayerSP player = minecraft.field_71439_g;
        if (world == null || player == null) {
            return;
        }
        this.renderBorder((Entity)player, event.getPartialTicks());
    }

    public void updateVillage(VillageClient vc) {
        this.villageClient = vc;
    }

    private int getBorderDistance(BlockPos pos) {
        int dist = Integer.MAX_VALUE;
        dist = Math.min(dist, Math.abs(pos.func_177958_n() - this.villageClient.getMaxX()));
        dist = Math.min(dist, Math.abs(pos.func_177958_n() - this.villageClient.getMinX()));
        dist = Math.min(dist, Math.abs(pos.func_177952_p() - this.villageClient.getMaxZ()));
        dist = Math.min(dist, Math.abs(pos.func_177952_p() - this.villageClient.getMinZ()));
        return dist;
    }

    public void renderBorder(Entity entityIn, float partialTicks) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        double RENDER_DISTANCE = 8.0;
        double RENDER_Y_DELTA = 2.0;
        double RENDER_WALL_WIDTH = 10.0;
        int borderDistance = this.getBorderDistance(entityIn.func_180425_c());
        if ((double)borderDistance < 8.0) {
            double colorAlpha = 1.0 - (double)borderDistance / 8.0;
            colorAlpha = Math.pow(colorAlpha, 2.0);
            double entityX = entityIn.field_70142_S + (entityIn.field_70165_t - entityIn.field_70142_S) * (double)partialTicks;
            double entityY = entityIn.field_70137_T + (entityIn.field_70163_u - entityIn.field_70137_T) * (double)partialTicks;
            double entityZ = entityIn.field_70136_U + (entityIn.field_70161_v - entityIn.field_70136_U) * (double)partialTicks;
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            Minecraft.func_71410_x().func_110434_K().func_110577_a(FORCEFIELD_TEXTURES);
            GlStateManager.func_179132_a((boolean)false);
            GlStateManager.func_179094_E();
            int color = 2138367;
            float colorR = (float)(color >> 16 & 0xFF) / 255.0f;
            float colorG = (float)(color >> 8 & 0xFF) / 255.0f;
            float colorB = (float)(color & 0xFF) / 255.0f;
            GlStateManager.func_179131_c((float)colorR, (float)colorG, (float)colorB, (float)((float)colorAlpha));
            GlStateManager.func_179136_a((float)-3.0f, (float)-3.0f);
            GlStateManager.func_179088_q();
            GlStateManager.func_179092_a((int)516, (float)0.1f);
            GlStateManager.func_179141_d();
            GlStateManager.func_179129_p();
            float f3 = (float)(Minecraft.func_71386_F() % 3000L) / 3000.0f;
            float f4 = 0.0f;
            float f5 = 0.0f;
            float f6 = 128.0f;
            bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            bufferbuilder.func_178969_c(-entityX, -entityY, -entityZ);
            double d8 = Math.max((double)MathHelper.func_76128_c((double)(entityZ - 10.0)), (double)this.villageClient.getMinZ());
            double d9 = Math.min((double)MathHelper.func_76143_f((double)(entityZ + 10.0)), (double)this.villageClient.getMaxZ());
            if (Math.abs(entityX - (double)this.villageClient.getMaxX()) < 8.0) {
                float f7 = 0.0f;
                double d10 = d8;
                while (d10 < d9) {
                    double d11 = Math.min(1.0, d9 - d10);
                    float f8 = (float)d11 * 0.5f;
                    bufferbuilder.func_181662_b((double)this.villageClient.getMaxX(), entityY + 2.0, d10).func_187315_a((double)(f3 + f7), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b((double)this.villageClient.getMaxX(), entityY + 2.0, d10 + d11).func_187315_a((double)(f3 + f8 + f7), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b((double)this.villageClient.getMaxX(), entityY - 2.0, d10 + d11).func_187315_a((double)(f3 + f8 + f7), (double)(f3 + 128.0f)).func_181675_d();
                    bufferbuilder.func_181662_b((double)this.villageClient.getMaxX(), entityY - 2.0, d10).func_187315_a((double)(f3 + f7), (double)(f3 + 128.0f)).func_181675_d();
                    d10 += 1.0;
                    f7 += 0.5f;
                }
            }
            if (Math.abs(entityX - (double)this.villageClient.getMinX()) < 8.0) {
                float f9 = 0.0f;
                double d12 = d8;
                while (d12 < d9) {
                    double d15 = Math.min(1.0, d9 - d12);
                    float f12 = (float)d15 * 0.5f;
                    bufferbuilder.func_181662_b((double)this.villageClient.getMinX(), entityY + 2.0, d12).func_187315_a((double)(f3 + f9), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b((double)this.villageClient.getMinX(), entityY + 2.0, d12 + d15).func_187315_a((double)(f3 + f12 + f9), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b((double)this.villageClient.getMinX(), entityY - 2.0, d12 + d15).func_187315_a((double)(f3 + f12 + f9), (double)(f3 + 128.0f)).func_181675_d();
                    bufferbuilder.func_181662_b((double)this.villageClient.getMinX(), entityY - 2.0, d12).func_187315_a((double)(f3 + f9), (double)(f3 + 128.0f)).func_181675_d();
                    d12 += 1.0;
                    f9 += 0.5f;
                }
            }
            d8 = Math.max((double)MathHelper.func_76128_c((double)(entityX - 10.0)), (double)this.villageClient.getMinX());
            d9 = Math.min((double)MathHelper.func_76143_f((double)(entityX + 10.0)), (double)this.villageClient.getMaxX());
            if (Math.abs(entityZ - (double)this.villageClient.getMaxZ()) < 8.0) {
                float f10 = 0.0f;
                double d13 = d8;
                while (d13 < d9) {
                    double d16 = Math.min(1.0, d9 - d13);
                    float f13 = (float)d16 * 0.5f;
                    bufferbuilder.func_181662_b(d13, entityY + 2.0, (double)this.villageClient.getMaxZ()).func_187315_a((double)(f3 + f10), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b(d13 + d16, entityY + 2.0, (double)this.villageClient.getMaxZ()).func_187315_a((double)(f3 + f13 + f10), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b(d13 + d16, entityY - 2.0, (double)this.villageClient.getMaxZ()).func_187315_a((double)(f3 + f13 + f10), (double)(f3 + 128.0f)).func_181675_d();
                    bufferbuilder.func_181662_b(d13, entityY - 2.0, (double)this.villageClient.getMaxZ()).func_187315_a((double)(f3 + f10), (double)(f3 + 128.0f)).func_181675_d();
                    d13 += 1.0;
                    f10 += 0.5f;
                }
            }
            if (Math.abs(entityZ - (double)this.villageClient.getMinZ()) < 8.0) {
                float f11 = 0.0f;
                double d14 = d8;
                while (d14 < d9) {
                    double d17 = Math.min(1.0, d9 - d14);
                    float f14 = (float)d17 * 0.5f;
                    bufferbuilder.func_181662_b(d14, entityY + 2.0, (double)this.villageClient.getMinZ()).func_187315_a((double)(f3 + f11), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b(d14 + d17, entityY + 2.0, (double)this.villageClient.getMinZ()).func_187315_a((double)(f3 + f14 + f11), (double)(f3 + 0.0f)).func_181675_d();
                    bufferbuilder.func_181662_b(d14 + d17, entityY - 2.0, (double)this.villageClient.getMinZ()).func_187315_a((double)(f3 + f14 + f11), (double)(f3 + 128.0f)).func_181675_d();
                    bufferbuilder.func_181662_b(d14, entityY - 2.0, (double)this.villageClient.getMinZ()).func_187315_a((double)(f3 + f11), (double)(f3 + 128.0f)).func_181675_d();
                    d14 += 1.0;
                    f11 += 0.5f;
                }
            }
            tessellator.func_78381_a();
            bufferbuilder.func_178969_c(0.0, 0.0, 0.0);
            GlStateManager.func_179089_o();
            GlStateManager.func_179118_c();
            GlStateManager.func_179136_a((float)0.0f, (float)0.0f);
            GlStateManager.func_179113_r();
            GlStateManager.func_179141_d();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
            GlStateManager.func_179132_a((boolean)true);
        }
    }
}

