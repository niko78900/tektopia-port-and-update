/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.pathing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathingGraph;
import net.tangotek.tektopia.pathing.PathingNode;

public class PathingCellMap {
    private final int defaultCapacity;
    private int nodeCount = 0;
    private BasePathingNode firstNode = null;
    private Map<Integer, Map<Integer, Set<BasePathingNode>>> baseNodes;
    private NavigableSet<BasePathingNode> edgeNodes = new TreeSet<BasePathingNode>(Comparator.comparingInt(a -> (int)a.getBlockPos().func_177951_i((Vec3i)this.firstNode.getBlockPos())));
    private Random rnd = new Random();

    public PathingCellMap(int defaultMapCapacity) {
        this.defaultCapacity = defaultMapCapacity;
        this.baseNodes = new HashMap<Integer, Map<Integer, Set<BasePathingNode>>>(this.defaultCapacity);
    }

    public void putNode(BasePathingNode node, World world) {
        int edgeDist;
        Set<BasePathingNode> nodeSet;
        Map<Integer, Set<BasePathingNode>> zMap;
        if (this.firstNode == null) {
            this.firstNode = node;
            this.edgeNodes.add(node);
        }
        if ((zMap = this.baseNodes.get(node.getCell().x)) == null) {
            zMap = new HashMap<Integer, Set<BasePathingNode>>(this.defaultCapacity);
            this.baseNodes.put(node.getCell().x, zMap);
        }
        if ((nodeSet = zMap.get(node.getCell().z)) == null) {
            nodeSet = new HashSet<BasePathingNode>();
            zMap.put(node.getCell().z, nodeSet);
        }
        if (this.rnd.nextInt(30) == 0 && (edgeDist = this.getAxisDistance(this.firstNode.getBlockPos(), node.getBlockPos())) < 115 && world.func_175678_i(node.getBlockPos())) {
            this.edgeNodes.add(node);
            if (this.edgeNodes.size() > 10) {
                this.edgeNodes.pollFirst();
            }
        }
        if (!nodeSet.add(node)) {
            throw new IllegalArgumentException("Duplicate BasePathingNode encountered");
        }
        ++this.nodeCount;
    }

    private int getAxisDistance(BlockPos bp1, BlockPos bp2) {
        return Math.max(Math.abs(bp1.func_177958_n() - bp2.func_177958_n()), Math.abs(bp1.func_177952_p() - bp2.func_177952_p()));
    }

    public void removeNode(BasePathingNode node, PathingGraph graph) {
        Set<BasePathingNode> nodeSet = this.getXZSet(node.getCell().x, node.getCell().z);
        if (nodeSet != null && nodeSet.remove(node)) {
            node.destroy(graph);
            --this.nodeCount;
        }
    }

    public int nodeCount() {
        return this.nodeCount;
    }

    public BasePathingNode getEdgeNode(BlockPos origin, double minDist) {
        if (!this.edgeNodes.isEmpty()) {
            int index = this.rnd.nextInt(this.edgeNodes.size());
            int i = 0;
            for (BasePathingNode edgeNode : this.edgeNodes) {
                if (i == index) {
                    return edgeNode;
                }
                ++i;
            }
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
        Set<BasePathingNode> nodeSet = this.getXZSet(x, z);
        if (nodeSet != null) {
            for (BasePathingNode node : nodeSet) {
                if (node.getCell().y < y1 || node.getCell().y > y2) continue;
                return node;
            }
        }
        return null;
    }

    public void updateNodes(int x, int y1, int y2, int z, PathingGraph graph) {
        Set<BasePathingNode> nodeSet = this.getXZSet(x, z);
        if (nodeSet != null) {
            for (BasePathingNode node : nodeSet) {
                if (node.getCell().y < y1 || node.getCell().y > y2) continue;
                graph.addFirstNode(node);
            }
        }
    }

    private Set<BasePathingNode> getXZSet(int x, int z) {
        Map<Integer, Set<BasePathingNode>> zMap = this.baseNodes.get(x);
        if (zMap != null) {
            return zMap.get(z);
        }
        return null;
    }

    public Set<PathingNode> getTopNodes() {
        PathingNode topNode = this.firstNode.getTopParent();
        HashSet<PathingNode> outNodes = new HashSet<PathingNode>();
        this.fillConnections(topNode, outNodes);
        return outNodes;
    }

    public void notifyListenerInitial(World world, EntityPlayerMP player) {
        ArrayList<EntityPlayerMP> listeners = new ArrayList<EntityPlayerMP>(1);
        listeners.add(player);
        for (Map<Integer, Set<BasePathingNode>> zMap : this.baseNodes.values()) {
            for (Set<BasePathingNode> nodeSet : zMap.values()) {
                for (BasePathingNode node : nodeSet) {
                    node.notifyListeners(world, listeners);
                }
            }
        }
    }

    private void fillConnections(PathingNode node, Set<PathingNode> outNodes) {
        if (!outNodes.contains(node)) {
            outNodes.add(node);
            for (PathingNode peer : node.connections) {
                this.fillConnections(peer, outNodes);
            }
        }
    }

    public BasePathingNode randomNode() {
        int numX = (int)(Math.random() * (double)this.baseNodes.size());
        for (Map<Integer, Set<BasePathingNode>> xMap : this.baseNodes.values()) {
            if (--numX >= 0) continue;
            int numZ = (int)(Math.random() * (double)xMap.size());
            for (Set<BasePathingNode> zSet : xMap.values()) {
                if (--numZ >= 0) continue;
                int numY = (int)(Math.random() * (double)zSet.size());
                for (BasePathingNode node : zSet) {
                    if (--numY >= 0) continue;
                    return node;
                }
            }
        }
        throw new AssertionError();
    }
}

