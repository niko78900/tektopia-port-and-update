/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 */
package net.tangotek.tektopia.pathing;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.tangotek.tektopia.pathing.PathingNodeClient;

public class PathingOverlayChunk {
    private final ChunkPos chunkPos;
    private Map<BlockPos, PathingNodeClient> nodes = new HashMap<BlockPos, PathingNodeClient>();
    private final int RENDER_RADIUS = 30;
    private final int RENDER_RADIUS_SQ = 900;

    public PathingOverlayChunk(ChunkPos cp) {
        this.chunkPos = cp;
    }

    public void putNode(PathingNodeClient node) {
        if (node.isDestroyed) {
            this.nodes.remove(node.pos);
        } else if (this.nodes.put(node.pos, node) != null) {
            node.setAge(400);
        }
    }

    public void renderOverlays(WorldClient world, BufferBuilder vertexBuffer, double viewX, double viewY, double viewZ) {
        if ((double)this.chunkPos.func_180334_c() - viewX > 30.0 || viewX - (double)this.chunkPos.func_180332_e() > 30.0 || (double)this.chunkPos.func_180333_d() - viewZ > 30.0 || viewZ - (double)this.chunkPos.func_180330_f() > 30.0) {
            return;
        }
        for (PathingNodeClient node : this.nodes.values()) {
            if (!(node.pos.func_177954_c(viewX, viewY, viewZ) < 900.0)) continue;
            this.renderNode(world, vertexBuffer, node);
        }
    }

    private void renderNode(WorldClient world, BufferBuilder vertexBuffer, PathingNodeClient node) {
        for (int index = 0; index < 4; ++index) {
            vertexBuffer.func_181662_b(node.getX(index), node.getY(index), node.getZ(index)).func_181669_b(170, 170, 170, 140).func_181675_d();
        }
        node.connections.forEach(conn -> {
            double innerEdge = 0.25;
            double outerEdge = 0.75;
            double width = 0.15;
            double gap = 0.03;
            double x = (double)node.pos.func_177958_n() + 0.5;
            double z = (double)node.pos.func_177952_p() + 0.5;
            double y = (double)node.pos.func_177956_o() + 0.05;
            for (int index = 0; index < 4; ++index) {
                if (conn.xOffset == 0) {
                    if (conn.zOffset == 1) {
                        vertexBuffer.func_181662_b(x - 0.03, y, z + 0.25).func_181669_b(98, 209, 83, 160).func_181675_d();
                        vertexBuffer.func_181662_b(x - 0.03 - 0.15, y, z + 0.25).func_181669_b(98, 209, 83, 160).func_181675_d();
                        vertexBuffer.func_181662_b(x - 0.03 - 0.15, y + (double)conn.yOffset, z + 0.75).func_181669_b(230, 230, 110, 160).func_181675_d();
                        vertexBuffer.func_181662_b(x - 0.03, y + (double)conn.yOffset, z + 0.75).func_181669_b(230, 230, 110, 160).func_181675_d();
                        continue;
                    }
                    vertexBuffer.func_181662_b(x + 0.03, y, z - 0.25).func_181669_b(98, 209, 83, 160).func_181675_d();
                    vertexBuffer.func_181662_b(x + 0.03 + 0.15, y, z - 0.25).func_181669_b(98, 209, 83, 160).func_181675_d();
                    vertexBuffer.func_181662_b(x + 0.03 + 0.15, y + (double)conn.yOffset, z - 0.75).func_181669_b(230, 230, 110, 160).func_181675_d();
                    vertexBuffer.func_181662_b(x + 0.03, y + (double)conn.yOffset, z - 0.75).func_181669_b(230, 230, 110, 160).func_181675_d();
                    continue;
                }
                if (conn.xOffset == 1) {
                    vertexBuffer.func_181662_b(x + 0.25, y, z + 0.03).func_181669_b(98, 209, 83, 160).func_181675_d();
                    vertexBuffer.func_181662_b(x + 0.25, y, z + 0.03 + 0.15).func_181669_b(98, 209, 83, 160).func_181675_d();
                    vertexBuffer.func_181662_b(x + 0.75, y + (double)conn.yOffset, z + 0.03 + 0.15).func_181669_b(230, 230, 110, 160).func_181675_d();
                    vertexBuffer.func_181662_b(x + 0.75, y + (double)conn.yOffset, z + 0.03).func_181669_b(230, 230, 110, 160).func_181675_d();
                    continue;
                }
                vertexBuffer.func_181662_b(x - 0.25, y, z - 0.03).func_181669_b(98, 209, 83, 160).func_181675_d();
                vertexBuffer.func_181662_b(x - 0.25, y, z - 0.03 - 0.15).func_181669_b(98, 209, 83, 160).func_181675_d();
                vertexBuffer.func_181662_b(x - 0.75, y + (double)conn.yOffset, z - 0.03 - 0.15).func_181669_b(230, 230, 110, 160).func_181675_d();
                vertexBuffer.func_181662_b(x - 0.75, y + (double)conn.yOffset, z - 0.03).func_181669_b(230, 230, 110, 160).func_181675_d();
            }
        });
    }
}

