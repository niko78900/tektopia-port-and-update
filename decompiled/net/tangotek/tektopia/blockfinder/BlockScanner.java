/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.blockfinder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;

public abstract class BlockScanner {
    protected final Block scanBlock;
    private Random rng = new Random();
    private long tickCount = 0L;
    private long releaseTick = 0L;
    private List<BlockPos> recentBlocks = new LinkedList<BlockPos>();
    private Map<BlockPos, Long> claimedBlocks = new HashMap<BlockPos, Long>();
    protected Village village;
    private final int scansPerTick;
    protected Queue<BlockPos> scannedBlocks = new PriorityQueue<BlockPos>(50, Comparator.comparingInt(a -> (int)a.func_177951_i((Vec3i)this.village.getCenter())));

    public BlockScanner(Block scanBlock, Village village, int scansPerTick) {
        this.scanBlock = scanBlock;
        this.village = village;
        this.scansPerTick = scansPerTick;
    }

    public Block getScanBlock() {
        return this.scanBlock;
    }

    public void update() {
        ++this.tickCount;
        if (!this.recentBlocks.isEmpty()) {
            BlockPos recent = this.recentBlocks.remove(0);
            this.scanNearby(recent);
        }
        for (int i = 0; i < this.scansPerTick; ++i) {
            this.scanRandomBlock(this.rng.nextFloat());
        }
        if (this.releaseTick-- < 0L) {
            this.releaseTick = 100L;
            this.releaseClaimedBlocks();
        }
    }

    public boolean hasBlocks() {
        return !this.scannedBlocks.isEmpty();
    }

    public int getBlockCount() {
        return this.scannedBlocks.size();
    }

    public BlockPos requestBlock() {
        while (!this.scannedBlocks.isEmpty()) {
            BlockPos bp = this.scannedBlocks.poll();
            if (!this.village.getWorld().func_180495_p(bp).func_177230_c().equals(this.scanBlock)) continue;
            this.claimedBlocks.put(bp, this.tickCount);
            return bp;
        }
        return null;
    }

    public void releaseClaim(BlockPos bp) {
        this.claimedBlocks.remove(bp);
    }

    public abstract BlockPos testBlock(World var1, BlockPos var2);

    protected abstract void scanNearby(BlockPos var1);

    protected void scanRandomBlock(float mod) {
        int radius = Math.max((int)((float)this.village.getSize() * mod), 20);
        int vertOffset = (int)(20.0f * mod) + 5;
        int X = this.village.getCenter().func_177958_n() + radius - this.rng.nextInt(radius * 2);
        int Y = MathHelper.func_76136_a((Random)this.rng, (int)((int)this.village.getAABB().field_72338_b - vertOffset), (int)((int)this.village.getAABB().field_72337_e + vertOffset));
        int Z = this.village.getCenter().func_177952_p() + radius - this.rng.nextInt(radius * 2);
        this.scanBlock(new BlockPos(X, Y, Z));
    }

    protected void scanBlock(BlockPos testPos) {
        BlockPos targetPos;
        if (this.village.isInVillage(testPos) && (targetPos = this.testBlock(this.village.getWorld(), testPos)) != null && !this.scannedBlocks.contains(targetPos) && !this.claimedBlocks.containsKey(targetPos)) {
            this.scannedBlocks.add(targetPos);
            this.recentBlocks.add(targetPos);
        }
    }

    protected void releaseClaimedBlocks() {
        Iterator<Map.Entry<BlockPos, Long>> itr = this.claimedBlocks.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<BlockPos, Long> entry = itr.next();
            long timeClaimed = this.tickCount - entry.getValue();
            if (timeClaimed > 2400L) {
                itr.remove();
                continue;
            }
            if (timeClaimed <= 600L) continue;
        }
    }
}

