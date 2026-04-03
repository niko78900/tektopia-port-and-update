/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package net.tangotek.tektopia.pathing;

import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.network.PacketPathingNode;
import net.tangotek.tektopia.pathing.PathingCell;
import net.tangotek.tektopia.pathing.PathingCellMap;
import net.tangotek.tektopia.pathing.PathingGraph;
import net.tangotek.tektopia.pathing.PathingNode;
import net.tangotek.tektopia.pathing.PathingNodeClient;
import net.tangotek.tektopia.structures.VillageStructure;

public class BasePathingNode
extends PathingNode {
    private final byte clearanceHeight;
    private long updateTick = 0L;

    public BasePathingNode(BlockPos bp, byte ch) {
        super(new PathingCell(bp, (byte)0));
        this.clearanceHeight = ch;
        this.updateTick = System.currentTimeMillis();
    }

    public byte getClearanceHeight() {
        return this.clearanceHeight;
    }

    public long getUpdateTick() {
        return this.updateTick;
    }

    @Override
    public int updateConnections(World world, PathingCellMap cellMap, PathingGraph graph) {
        this.updateTick = System.currentTimeMillis();
        this.checkConnection(world, cellMap, graph, 1, 0);
        this.checkConnection(world, cellMap, graph, -1, 0);
        this.checkConnection(world, cellMap, graph, 0, 1);
        this.checkConnection(world, cellMap, graph, 0, -1);
        if (this.parent == null) {
            this.parent = new PathingNode(this.getCell().up());
            this.parent.addChild(this);
            graph.addLastNode(this.parent);
        }
        return 0;
    }

    private boolean checkConnection(World world, PathingCellMap cellMap, PathingGraph graph, int x, int z) {
        if (!graph.isInRange(this.getBlockPos().func_177982_a(x, 0, z))) {
            return false;
        }
        PathingNode connected = this.getConnection(x, z);
        if (connected == null) {
            boolean newNode = false;
            BasePathingNode node = this.getExistingNeighbor(cellMap, x, z);
            if (node == null && (node = this.checkWalkableNeighbor(world, x, z)) != null) {
                newNode = true;
            }
            if (node != null && this.canWalkTo(node)) {
                BasePathingNode.connectNodes(this, node, graph);
                if (newNode) {
                    graph.addFirstNode(node);
                    cellMap.putNode(node, world);
                    return true;
                }
            }
        } else {
            this.checkParentLink(connected);
        }
        return false;
    }

    @Override
    protected void notifyListeners(World world, List<EntityPlayerMP> listeners) {
        listeners.forEach(p -> TekVillager.NETWORK.sendTo((IMessage)new PacketPathingNode(new PathingNodeClient(this)), p));
    }

    private BasePathingNode checkWalkableNeighbor(World world, int x, int z) {
        BlockPos bp = this.getBlockPos().func_177982_a(x, 0, z);
        if (!(BasePathingNode.canWalkOn(world, bp) || BasePathingNode.canWalkOn(world, bp = bp.func_177977_b()) || BasePathingNode.canWalkOn(world, bp = bp.func_177977_b()))) {
            bp = null;
        }
        if (bp != null) {
            bp = bp.func_177984_a();
            byte clearance = 0;
            if (BasePathingNode.isPassable(world, bp) && BasePathingNode.isPassable(world, bp.func_177981_b(1))) {
                clearance = 2;
                if (BasePathingNode.isPassable(world, bp.func_177981_b(2))) {
                    clearance = (byte)(clearance + 1);
                }
            }
            if (clearance >= 2) {
                return new BasePathingNode(bp, clearance);
            }
        }
        return null;
    }

    public static boolean isPassable(World world, BlockPos bp) {
        IBlockState blockState = world.func_180495_p(bp);
        if (blockState.func_185904_a().func_76224_d()) {
            return false;
        }
        if (blockState.func_177230_c().func_176205_b((IBlockAccess)world, bp)) {
            return true;
        }
        return BasePathingNode.isPortal(world, bp);
    }

    private static boolean isPortal(World world, BlockPos bp) {
        if (VillageStructure.isWoodDoor(world, bp)) {
            return true;
        }
        return VillageStructure.isGate(world, bp);
    }

    public static boolean canWalkOn(World world, BlockPos bp) {
        if (!BasePathingNode.isPassable(world, bp)) {
            IBlockState blockState = world.func_180495_p(bp);
            if (blockState.func_185904_a().func_76224_d()) {
                return false;
            }
            return !(blockState.func_177230_c() instanceof BlockFence) && !(blockState.func_177230_c() instanceof BlockWall);
        }
        return false;
    }

    private boolean canWalkTo(BasePathingNode node) {
        return node.getCell().y == this.getCell().y - 1 && node.getClearanceHeight() >= 3 || node.getCell().y == this.getCell().y || node.getCell().y == this.getCell().y + 1 && this.getClearanceHeight() >= 3;
    }

    private BasePathingNode getExistingNeighbor(PathingCellMap cellMap, int x, int z) {
        return cellMap.getNodeYRange(this.getCell().x + x, this.getCell().y - 1, this.getCell().y + 1, this.getCell().z + z);
    }
}
