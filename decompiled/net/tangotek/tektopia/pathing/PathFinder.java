/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.init.Blocks
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathFinder
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 */
package net.tangotek.tektopia.pathing;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathStep;
import net.tangotek.tektopia.pathing.PathingCell;
import net.tangotek.tektopia.pathing.PathingGraph;
import net.tangotek.tektopia.pathing.PathingNode;

public class PathFinder
extends net.minecraft.pathfinding.PathFinder {
    protected EntityVillageNavigator entity;
    protected IBlockAccess blockAccess;
    protected PriorityQueue<PathStep> openSteps = new PriorityQueue<PathStep>(Comparator.comparingInt(a -> a.getTotalPathDistance()));
    protected Set<PathingNode> closedNodes = new HashSet<PathingNode>();

    public PathFinder(EntityVillageNavigator entityNav) {
        super(null);
        this.entity = entityNav;
        this.blockAccess = entityNav.field_70170_p;
    }

    public PathingGraph getGraph() {
        if (this.entity.hasVillage()) {
            return this.entity.getVillage().getPathingGraph();
        }
        return null;
    }

    @Nullable
    public Path func_186333_a(IBlockAccess worldIn, EntityLiving entitylivingIn, Entity targetEntity, float maxDistance) {
        return this.findPath(worldIn, entitylivingIn, targetEntity.field_70165_t, targetEntity.func_174813_aQ().field_72338_b, targetEntity.field_70161_v);
    }

    @Nullable
    public Path func_186336_a(IBlockAccess worldIn, EntityLiving entitylivingIn, BlockPos targetPos, float maxDistance) {
        return this.findPath(worldIn, entitylivingIn, (float)targetPos.func_177958_n() + 0.5f, (float)targetPos.func_177956_o() + 0.5f, (float)targetPos.func_177952_p() + 0.5f);
    }

    private Path findPath(IBlockAccess worldIn, EntityLiving entityLivingIn, double x, double y, double z) {
        this.blockAccess = worldIn;
        PathingGraph graph = this.getGraph();
        if (graph != null) {
            BasePathingNode endNode = graph.getBaseNode(MathHelper.func_76128_c((double)x), MathHelper.func_76128_c((double)y), MathHelper.func_76128_c((double)z));
            PathingNode startNode = this.getStart(graph);
            return this.findPath(worldIn, startNode, endNode);
        }
        return null;
    }

    public Path findPath(IBlockAccess worldIn, PathingNode startNode, PathingNode endNode) {
        int maxLevel;
        if (endNode == null || startNode == null) {
            return null;
        }
        PathStep firstStep = null;
        for (int level = maxLevel = endNode.getTopParent().getCell().level; level >= 0; --level) {
            PathingNode localStart = startNode.getParent(level);
            PathingNode localEnd = endNode.getParent(level);
            firstStep = this.findLevelPath(new PathStep(localStart, null, localEnd, firstStep), localEnd);
        }
        return this.finalizePath(firstStep);
    }

    private PathStep findLevelPath(PathStep startPoint, PathingNode endNode) {
        PathStep current;
        this.openSteps.clear();
        this.closedNodes.clear();
        this.openSteps.add(startPoint);
        while ((current = this.openSteps.poll()) != null) {
            this.closedNodes.add(current.getNode());
            if (current.getNode() == endNode) {
                return current.reverseSteps();
            }
            for (PathingNode connectedNode : current.getNode().connections) {
                if (this.closedNodes.contains(connectedNode)) continue;
                boolean isOpen = false;
                PathStep stepToAdd = null;
                Iterator<PathStep> itr = this.openSteps.iterator();
                while (itr.hasNext()) {
                    PathStep step = itr.next();
                    if (!step.getNode().equals(connectedNode)) continue;
                    if (step.updateDistance(current)) {
                        itr.remove();
                        stepToAdd = step;
                    }
                    isOpen = true;
                    break;
                }
                if (!isOpen) {
                    if (current.getParentStep() == null) {
                        stepToAdd = new PathStep(connectedNode, current, endNode, null);
                    } else if (connectedNode.getParent() == current.getParentStep().getNode()) {
                        stepToAdd = new PathStep(connectedNode, current, endNode, current.getParentStep());
                    } else if (current.getParentStep().getNextStep() != null && connectedNode.getParent() == current.getParentStep().getNextStep().getNode()) {
                        stepToAdd = new PathStep(connectedNode, current, endNode, current.getParentStep().getNextStep());
                    }
                }
                if (stepToAdd == null) continue;
                this.openSteps.add(stepToAdd);
            }
        }
        return null;
    }

    private Path finalizePath(PathStep firstStep) {
        ArrayList<PathPoint> list = new ArrayList<PathPoint>();
        for (PathStep step = firstStep; step != null; step = step.getNextStep()) {
            PathingCell cell = step.getNode().getCell();
            list.add(new PathPoint(cell.x, cell.y, cell.z));
        }
        return new Path(list.toArray(new PathPoint[0]));
    }

    private PathingNode getStart(PathingGraph graph) {
        int i;
        if (this.entity.func_70090_H()) {
            i = (int)this.entity.func_174813_aQ().field_72338_b;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.func_76128_c((double)this.entity.field_70165_t), i, MathHelper.func_76128_c((double)this.entity.field_70161_v));
            Block block = this.blockAccess.func_180495_p((BlockPos)blockpos$mutableblockpos).func_177230_c();
            while (block == Blocks.field_150358_i || block == Blocks.field_150355_j) {
                blockpos$mutableblockpos.func_181079_c(MathHelper.func_76128_c((double)this.entity.field_70165_t), ++i, MathHelper.func_76128_c((double)this.entity.field_70161_v));
                block = this.blockAccess.func_180495_p((BlockPos)blockpos$mutableblockpos).func_177230_c();
            }
        } else if (this.entity.field_70122_E) {
            i = MathHelper.func_76128_c((double)(this.entity.func_174813_aQ().field_72338_b + 0.5));
        } else {
            BlockPos blockpos = new BlockPos((Entity)this.entity);
            while ((this.blockAccess.func_180495_p(blockpos).func_185904_a() == Material.field_151579_a || this.blockAccess.func_180495_p(blockpos).func_177230_c().func_176205_b(this.blockAccess, blockpos)) && blockpos.func_177956_o() > 0) {
                blockpos = blockpos.func_177977_b();
            }
            i = blockpos.func_177984_a().func_177956_o();
        }
        BlockPos blockpos2 = new BlockPos((Entity)this.entity);
        BasePathingNode node = graph.getBaseNode(blockpos2.func_177958_n(), i, blockpos2.func_177952_p());
        if (node == null) {
            node = graph.getBaseNode(blockpos2.func_177958_n(), i + 1, blockpos2.func_177952_p());
        }
        if (node == null) {
            HashSet set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.func_174813_aQ().field_72340_a, (double)i, this.entity.func_174813_aQ().field_72339_c));
            set.add(new BlockPos(this.entity.func_174813_aQ().field_72340_a, (double)i, this.entity.func_174813_aQ().field_72334_f));
            set.add(new BlockPos(this.entity.func_174813_aQ().field_72336_d, (double)i, this.entity.func_174813_aQ().field_72339_c));
            set.add(new BlockPos(this.entity.func_174813_aQ().field_72336_d, (double)i, this.entity.func_174813_aQ().field_72334_f));
            for (BlockPos blockpos1 : set) {
                node = graph.getNodeYRange(blockpos1.func_177958_n(), blockpos1.func_177956_o() - 1, blockpos1.func_177956_o(), blockpos1.func_177952_p());
                if (node == null) continue;
                return node;
            }
        }
        return node;
    }
}

