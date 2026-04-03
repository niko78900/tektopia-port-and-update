/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.init.Blocks
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathFinder
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.pathfinding.WalkNodeProcessor
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.pathing;

import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathFinder;

public class PathNavigateVillager2
extends PathNavigate {
    private int currentPointIndex = -1;
    private long pathUpdate = 0L;
    private int directPathThrottle = 0;
    private Queue<EntityArmorStand> lastDebugs = new LinkedList<EntityArmorStand>();

    public PathNavigateVillager2(EntityLiving entitylivingIn, World worldIn, boolean doors) {
        super(entitylivingIn, worldIn);
        this.field_179695_a.func_186321_b(doors);
        this.field_179695_a.func_186317_a(doors);
    }

    protected boolean func_75485_k() {
        return this.field_75515_a.field_70122_E || this.getCanSwim() && this.func_75506_l() || this.field_75515_a.func_184218_aH();
    }

    protected Vec3d func_75502_i() {
        return new Vec3d(this.field_75515_a.field_70165_t, (double)this.getPathablePosY(), this.field_75515_a.field_70161_v);
    }

    public Path func_179680_a(BlockPos pos) {
        return super.func_179680_a(pos);
    }

    public Path func_75494_a(Entity entityIn) {
        return this.func_179680_a(new BlockPos(entityIn));
    }

    private int getPathablePosY() {
        if (this.field_75515_a.func_70090_H() && this.getCanSwim()) {
            int i = (int)this.field_75515_a.func_174813_aQ().field_72338_b;
            Block block = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c((double)this.field_75515_a.field_70165_t), i, MathHelper.func_76128_c((double)this.field_75515_a.field_70161_v))).func_177230_c();
            int j = 0;
            while (block == Blocks.field_150358_i || block == Blocks.field_150355_j) {
                block = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c((double)this.field_75515_a.field_70165_t), ++i, MathHelper.func_76128_c((double)this.field_75515_a.field_70161_v))).func_177230_c();
                if (++j <= 16) continue;
                return (int)this.field_75515_a.func_174813_aQ().field_72338_b;
            }
            return i;
        }
        return (int)(this.field_75515_a.func_174813_aQ().field_72338_b + 0.5);
    }

    public void setBreakDoors(boolean canBreakDoors) {
        this.field_179695_a.func_186321_b(canBreakDoors);
    }

    public void setEnterDoors(boolean enterDoors) {
        this.field_179695_a.func_186317_a(enterDoors);
    }

    public boolean getEnterDoors() {
        return this.field_179695_a.func_186323_c();
    }

    public void setCanSwim(boolean canSwim) {
        this.field_179695_a.func_186316_c(canSwim);
    }

    public boolean getCanSwim() {
        return this.field_179695_a.func_186322_e();
    }

    protected net.minecraft.pathfinding.PathFinder func_179679_a() {
        this.field_179695_a = new WalkNodeProcessor();
        this.field_179695_a.func_186317_a(true);
        this.field_179695_a.func_186321_b(true);
        this.field_179695_a.func_186316_c(true);
        EntityVillageNavigator villageNav = (EntityVillageNavigator)this.field_75515_a;
        return new PathFinder(villageNav);
    }

    public PathFinder getVillagerPathFinder() {
        return (PathFinder)this.func_179679_a();
    }

    protected void func_179677_a(Vec3d positionVec3) {
    }

    public boolean func_75484_a(@Nullable Path pathentityIn, double speedIn) {
        this.pathUpdate = System.currentTimeMillis();
        return super.func_75484_a(pathentityIn, speedIn);
    }

    protected void func_75508_h() {
        super.func_75508_h();
        if (this.field_75514_c.func_75873_e() != this.currentPointIndex && !this.field_75514_c.func_75879_b()) {
            this.currentPointIndex = this.field_75514_c.func_75873_e();
            BasePathingNode pathNode = null;
            if (this.currentPointIndex >= 0 && this.currentPointIndex < this.field_75514_c.func_75874_d()) {
                PathPoint pathPoint = this.field_75514_c.func_75877_a(this.currentPointIndex);
                Village v = ((EntityVillageNavigator)this.field_75515_a).getVillage();
                if (pathPoint != null && v != null) {
                    pathNode = v.getPathingGraph().getBaseNode(pathPoint.field_75839_a, pathPoint.field_75837_b, pathPoint.field_75838_c);
                }
            }
            if (pathNode == null || pathNode.getUpdateTick() > this.pathUpdate) {
                this.func_75484_a(null, 1.0);
            }
        }
    }

    protected boolean func_75493_a(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        --this.directPathThrottle;
        if (this.directPathThrottle <= 0) {
            this.directPathThrottle = 30 + this.field_75513_b.field_73012_v.nextInt(15);
            Vec3d dir = posVec32.func_178788_d(posVec31).func_72432_b();
            double div = Math.max(Math.abs(dir.field_72450_a), Math.abs(dir.field_72449_c));
            double widthX = (double)sizeX / div;
            double widthZ = (double)sizeZ / div;
            dir = dir.func_186678_a(0.5);
            Vec3d cur = posVec31;
            while (cur.func_72436_e(posVec32) > 1.0) {
                if (!this.isSafeToStandAt(cur, widthX, sizeY, widthZ)) {
                    return false;
                }
                cur = cur.func_178787_e(dir);
            }
            return true;
        }
        return false;
    }

    private boolean isSafeToStandAt(Vec3d pos, double widthX, double height, double widthZ) {
        double halfX = widthX / 2.0;
        double halfZ = widthZ / 2.0;
        BlockPos corner1 = new BlockPos(pos.field_72450_a - halfX, pos.field_72448_b, pos.field_72449_c - halfZ);
        BlockPos corner2 = new BlockPos(pos.field_72450_a + halfX, pos.field_72448_b + height, pos.field_72449_c + halfZ);
        for (BlockPos blockPos : BlockPos.func_177980_a((BlockPos)corner1, (BlockPos)corner2)) {
            IBlockState blockState = this.field_75513_b.func_180495_p(blockPos);
            Block block = blockState.func_177230_c();
            if (!block.func_176205_b((IBlockAccess)this.field_75513_b, blockPos)) {
                return false;
            }
            if (blockPos.func_177956_o() != corner1.func_177956_o()) continue;
            PathNodeType pathnodetype = this.field_179695_a.func_186319_a((IBlockAccess)this.field_75513_b, blockPos.func_177958_n(), blockPos.func_177956_o() - 1, blockPos.func_177952_p(), this.field_75515_a, 1, 2, 1, true, true);
            if (pathnodetype == PathNodeType.WATER) {
                return false;
            }
            if (pathnodetype == PathNodeType.LAVA) {
                return false;
            }
            if (pathnodetype == PathNodeType.OPEN) {
                return false;
            }
            pathnodetype = this.field_179695_a.func_186319_a((IBlockAccess)this.field_75513_b, blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), this.field_75515_a, 1, 2, 1, true, true);
            float f = this.field_75515_a.func_184643_a(pathnodetype);
            if (f < 0.0f || f >= 8.0f) {
                return false;
            }
            if (pathnodetype != PathNodeType.DAMAGE_FIRE && pathnodetype != PathNodeType.DANGER_FIRE && pathnodetype != PathNodeType.DAMAGE_OTHER) continue;
            return false;
        }
        return true;
    }
}

