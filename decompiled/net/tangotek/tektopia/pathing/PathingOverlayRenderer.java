/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package net.tangotek.tektopia.pathing;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tangotek.tektopia.pathing.PathingNodeClient;
import net.tangotek.tektopia.pathing.PathingOverlayChunk;

public class PathingOverlayRenderer {
    private Map<ChunkPos, PathingOverlayChunk> chunks = new HashMap<ChunkPos, PathingOverlayChunk>();
    private boolean enabled = false;

    public PathingOverlayRenderer() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void handleNodeUpdate(PathingNodeClient node) {
        boolean bl = this.enabled = node != null;
        if (this.enabled) {
            ChunkPos chunkPos = new ChunkPos(node.pos);
            PathingOverlayChunk overlayChunk = this.chunks.get(chunkPos);
            if (overlayChunk == null) {
                overlayChunk = new PathingOverlayChunk(chunkPos);
                this.chunks.put(chunkPos, overlayChunk);
            }
            overlayChunk.putNode(node);
        } else {
            this.chunks.clear();
        }
    }

    @SubscribeEvent
    public void renderOverlays(RenderWorldLastEvent event) {
        if (!this.enabled) {
            return;
        }
        Minecraft minecraft = Minecraft.func_71410_x();
        WorldClient world = minecraft.field_71441_e;
        EntityPlayerSP player = minecraft.field_71439_g;
        if (world == null || player == null) {
            return;
        }
        double partialTicks = event.getPartialTicks();
        double viewX = player.field_70142_S + (player.field_70165_t - player.field_70142_S) * partialTicks;
        double viewY = player.field_70137_T + (player.field_70163_u - player.field_70137_T) * partialTicks;
        double viewZ = player.field_70136_U + (player.field_70161_v - player.field_70136_U) * partialTicks;
        GlStateManager.func_179094_E();
        GlStateManager.func_179090_x();
        GlStateManager.func_179141_d();
        GlStateManager.func_179147_l();
        BufferBuilder vertexBuffer = Tessellator.func_178181_a().func_178180_c();
        vertexBuffer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        vertexBuffer.func_178969_c(-viewX, -viewY, -viewZ);
        this.renderOverlays(world, vertexBuffer, viewX, viewY, viewZ);
        vertexBuffer.func_178969_c(0.0, 0.0, 0.0);
        Tessellator.func_178181_a().func_78381_a();
        GlStateManager.func_179098_w();
        GlStateManager.func_179121_F();
    }

    private void renderOverlays(WorldClient world, BufferBuilder vertexBuffer, double viewX, double viewY, double viewZ) {
        this.chunks.values().forEach(c -> c.renderOverlays(world, vertexBuffer, viewX, viewY, viewZ));
    }
}

