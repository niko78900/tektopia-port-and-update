/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.pathing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathingGraph;
import net.tangotek.tektopia.pathing.PathingNode;

public class PathingCellMap2 {
    private BlockPos origin;
    private int nodeCount = 0;
    private BasePathingNode firstNode = null;
    private static final int VILLAGE2 = 240;
    private Set[] baseNodes = new Set[57600];
    private LinkedList<BasePathingNode> edgeNodes = new LinkedList();
    private Random rnd = new Random();
    public static final int HALF_VILLAGE = 60;

    public PathingCellMap2(BlockPos orig) {
        this.origin = orig;
    }

    public void putNode(BasePathingNode node, World world) {
        Set<BasePathingNode> nodeSet;
        if (this.firstNode == null) {
            this.edgeNodes.add(node);
            this.firstNode = node;
        }
        if ((nodeSet = this.getXZSet(node.getCell().x, node.getCell().z, true)) != null) {
            if (this.rnd.nextInt(10) == 0) {
                double curDist = this.edgeNodes.getFirst().getBlockPos().func_177951_i((Vec3i)this.firstNode.getBlockPos());
                double thisDist = node.getBlockPos().func_177951_i((Vec3i)this.firstNode.getBlockPos());
                int edgeDist = this.getAxisDistance(this.firstNode.getBlockPos(), node.getBlockPos());
                if (thisDist > curDist && edgeDist < 115 && world.func_175678_i(node.getBlockPos()) && this.edgeNodes.getFirst().getBlockPos().func_177951_i((Vec3i)node.getBlockPos()) > 400.0) {
                    System.out.println("Edge Node: [" + thisDist + "] " + node.getBlockPos());
                    this.edgeNodes.addFirst(node);
                }
            }
            if (!nodeSet.add(node)) {
                throw new IllegalArgumentException("Duplicate BasePathingNode encountered");
            }
            ++this.nodeCount;
        }
    }

    private int getAxisDistance(BlockPos bp1, BlockPos bp2) {
        return Math.max(Math.abs(bp1.func_177958_n() - bp2.func_177958_n()), Math.abs(bp1.func_177952_p() - bp2.func_177952_p()));
    }

    public void removeNode(BasePathingNode node, PathingGraph graph) {
        Set<BasePathingNode> nodeSet = this.getXZSet(node.getCell().x, node.getCell().z, false);
        if (nodeSet != null && nodeSet.remove(node)) {
            node.destroy(graph);
            --this.nodeCount;
        }
    }

    public int nodeCount() {
        return this.nodeCount;
    }

    public BasePathingNode getEdgeNode(BlockPos origin, double minDist) {
        double minDistSq = minDist * minDist;
        while (this.edgeNodes.size() > 10) {
            Random rnd = new Random();
            ArrayList<BasePathingNode> arrayList = new ArrayList<BasePathingNode>(this.edgeNodes);
            this.edgeNodes.clear();
            while (this.edgeNodes.size() < 10 && !arrayList.isEmpty()) {
                BasePathingNode node = arrayList.remove(rnd.nextInt(arrayList.size()));
                if (!(origin.func_177951_i((Vec3i)node.getBlockPos()) >= minDistSq)) continue;
                this.edgeNodes.add(node);
            }
        }
        for (BasePathingNode node : this.edgeNodes) {
            if (!(origin.func_177951_i((Vec3i)node.getBlockPos()) >= minDistSq)) continue;
            this.edgeNodes.remove(node);
            this.edgeNodes.addLast(node);
            return node;
        }
        return null;
    }

    public void debugEdgeNodes(World world) {
        for (BasePathingNode node : this.edgeNodes) {
            System.out.println("Edge Node at " + node.getBlockPos());
            EntityArmorStand ent = new EntityArmorStand(world, (double)node.getBlockPos().func_177958_n(), (double)node.getBlockPos().func_177956_o(), (double)node.getBlockPos().func_177952_p());
            ent.func_70690_d(new PotionEffect(MobEffects.field_188423_x, 200));
            ent.func_70606_j(0.0f);
            ent.field_70725_aQ = -200;
            world.func_72838_d((Entity)ent);
        }
    }

    public BasePathingNode getNode(int x, int y, int z) {
        return this.getNodeYRange(x, y, y, z);
    }

    public BasePathingNode getNodeYRange(int x, int y1, int y2, int z) {
        Set<BasePathingNode> nodeSet = this.getXZSet(x, z, false);
        if (nodeSet != null) {
            for (BasePathingNode node : nodeSet) {
                if (node.getCell().y < y1 || node.getCell().y > y2) continue;
                return node;
            }
        }
        return null;
    }

    public void updateNodes(int x, int y1, int y2, int z, PathingGraph graph) {
        Set<BasePathingNode> nodeSet = this.getXZSet(x, z, false);
        if (nodeSet != null) {
            for (BasePathingNode node : nodeSet) {
                if (node.getCell().y < y1 || node.getCell().y > y2) continue;
                graph.addFirstNode(node);
            }
        }
    }

    private Set<BasePathingNode> getXZSet(int x, int z, boolean create) {
        int xx = x - this.origin.func_177958_n() + 120;
        int zz = z - this.origin.func_177952_p() + 120;
        if (xx < 0 || xx >= 240 || zz < 0 || zz >= 240) {
            return null;
        }
        HashSet result = this.baseNodes[xx * 240 + zz];
        if (create && result == null) {
            this.baseNodes[xx * 240 + zz] = result = new HashSet();
        }
        return result;
    }

    public Set<PathingNode> getTopNodes() {
        PathingNode topNode = this.firstNode.getTopParent();
        HashSet<PathingNode> outNodes = new HashSet<PathingNode>();
        this.fillConnections(topNode, outNodes);
        return outNodes;
    }

    private void fillConnections(PathingNode node, Set<PathingNode> outNodes) {
        if (!outNodes.contains(node)) {
            outNodes.add(node);
            for (PathingNode peer : node.connections) {
                this.fillConnections(peer, outNodes);
            }
        }
    }
}

