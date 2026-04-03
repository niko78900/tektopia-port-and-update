/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IWorldEventListener
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.gen.ChunkProviderServer
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package net.tangotek.tektopia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageClient;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.network.PacketVillage;
import net.tangotek.tektopia.pathing.PathingWorldListener;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureFactory;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;
import net.tangotek.tektopia.tickjob.TickJobQueue;

public class VillageManager
extends WorldSavedData {
    protected World world;
    private long tick = 0L;
    protected Set<Village> villages = new HashSet<Village>();
    protected Queue<AxisAlignedBB> scanBoxes = new LinkedList<AxisAlignedBB>();
    protected BlockPos lastVillagerPos;
    private static final String DATA_IDENTIFIER = "tektopia_VillageManager";
    private final VillageStructureFactory structureFactory = new VillageStructureFactory();
    private final PathingWorldListener pathWorldListener;
    private BlockPos lastStuck = null;
    private boolean debugOn = false;
    protected TickJobQueue jobs = new TickJobQueue();

    public VillageManager(World worldIn) {
        super(DATA_IDENTIFIER);
        this.world = worldIn;
        this.pathWorldListener = new PathingWorldListener(this);
        this.world.func_72954_a((IWorldEventListener)this.pathWorldListener);
        this.jobs.addJob(new TickJob(200, 0, true, () -> this.sendVillagesToClients()));
        this.jobs.addJob(new TickJob(100, 20, true, () -> this.processPlayerPositions(128, 32, 128)));
        this.jobs.addJob(new TickJob(15, 10, true, () -> this.processPlayerPositions(16, 6, 16)));
    }

    public static VillageManager get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        VillageManager instance = (VillageManager)storage.func_75742_a(VillageManager.class, DATA_IDENTIFIER);
        if (instance == null) {
            instance = new VillageManager(world);
            storage.func_75745_a(DATA_IDENTIFIER, (WorldSavedData)instance);
        }
        return instance;
    }

    public static int getItemValue(Item item) {
        return 1;
    }

    private boolean isChunkFullyLoaded(World world, BlockPos pos) {
        if (world.field_72995_K) {
            return true;
        }
        long i = ChunkPos.func_77272_a((int)(pos.func_177958_n() >> 4), (int)(pos.func_177952_p() >> 4));
        Chunk chunk = (Chunk)((ChunkProviderServer)world.func_72863_F()).field_73244_f.get(i);
        return chunk != null && !chunk.field_189550_d;
    }

    public void update() {
        ++this.tick;
        this.jobs.tick();
        Iterator<Village> itr = this.villages.iterator();
        while (itr.hasNext()) {
            Village v = itr.next();
            if (!this.isChunkFullyLoaded(this.world, v.getOrigin()) || !v.isValid()) {
                IVillageData vd = v.getTownData();
                if (vd != null) {
                    v.debugOut("Village REMOVED - [" + v.getName() + "]   " + vd.getUUID());
                }
                v.destroy();
                itr.remove();
                continue;
            }
            v.update();
        }
        this.procesScanBoxes();
    }

    private void sendVillagesToClients() {
        List players = this.world.func_175661_b(EntityPlayerMP.class, EntitySelectors.field_94557_a);
        for (EntityPlayerMP p : players) {
            Village v = this.getNearestVillage(p.func_180425_c(), 200);
            TekVillager.NETWORK.sendTo((IMessage)new PacketVillage(v == null ? null : new VillageClient(v)), p);
        }
    }

    private void processPlayerPositions(int x, int y, int z) {
        List players = this.world.func_175661_b(EntityPlayerMP.class, EntitySelectors.field_94557_a);
        for (EntityPlayerMP p : players) {
            AxisAlignedBB aabb = p.func_174813_aQ().func_72314_b((double)x, (double)y, (double)z);
            this.addScanBox(aabb);
        }
    }

    private void procesScanBoxes() {
        while (!this.scanBoxes.isEmpty()) {
            AxisAlignedBB aabb = this.scanBoxes.poll();
            ListIterator itr = this.world.func_72872_a(EntityItemFrame.class, aabb).listIterator();
            while (itr.hasNext()) {
                EntityItemFrame itemFrame = (EntityItemFrame)itr.next();
                VillageStructureType structType = VillageStructureFactory.getByItem(itemFrame.func_82335_i());
                if (structType == null) continue;
                boolean validStructure = false;
                BlockPos framePos = itemFrame.func_174857_n();
                Village village = this.getNearestVillage(framePos, 360);
                if (village == null && structType == VillageStructureType.TOWNHALL) {
                    village = new Village(this.world, framePos);
                    this.villages.add(village);
                } else {
                    village = this.getVillageAt(framePos);
                    if (village != null && !ModItems.isItemVillageBound(itemFrame.func_82335_i(), village)) {
                        if (!ModItems.isItemVillageBound(itemFrame.func_82335_i())) {
                            village.debugOut("Binding Structure Marker to village - " + framePos);
                            ModItems.bindItemToVillage(itemFrame.func_82335_i(), village);
                        } else {
                            village.debugOut("Structure Marker " + itemFrame.func_82335_i().func_77977_a() + " bound to INCORRECT village - " + framePos);
                            village = null;
                        }
                    }
                }
                if (village != null) {
                    VillageStructure struct = village.getStructureFromFrame(framePos);
                    if (struct == null) {
                        struct = VillageStructureFactory.create(structType, this.world, village, itemFrame);
                        if (village.addStructure(struct)) {
                            validStructure = true;
                        }
                    } else {
                        validStructure = true;
                    }
                }
                if (validStructure && !ModItems.isTaggedItem(itemFrame.func_82335_i(), ItemTagType.STRUCTURE)) {
                    ModItems.makeTaggedItem(itemFrame.func_82335_i(), ItemTagType.STRUCTURE);
                    itemFrame.func_82334_a(itemFrame.func_82335_i());
                    continue;
                }
                if (validStructure || !ModItems.isTaggedItem(itemFrame.func_82335_i(), ItemTagType.STRUCTURE)) continue;
                ModItems.untagItem(itemFrame.func_82335_i(), ItemTagType.STRUCTURE);
                itemFrame.func_82334_a(itemFrame.func_82335_i());
            }
        }
    }

    public void addScanBox(AxisAlignedBB aabb) {
        this.scanBoxes.add(aabb);
    }

    public Village getVillageAt(BlockPos blockPos) {
        for (Village v : this.villages) {
            if (!v.isLoaded() || !v.isInVillage(blockPos)) continue;
            return v;
        }
        return null;
    }

    public Village getNearestVillage(BlockPos blockPos, int maxDist) {
        Village result = null;
        double closest = Double.MAX_VALUE;
        int maxDistSq = maxDist * maxDist;
        for (Village v : this.villages) {
            double distSq;
            if (!v.isLoaded() || !((distSq = v.getOrigin().func_177951_i((Vec3i)blockPos)) < closest) || !(distSq < (double)maxDistSq)) continue;
            closest = distSq;
            result = v;
        }
        return result;
    }

    public List<Village> getVillagesNear(BlockPos blockPos, int maxDist) {
        ArrayList<Village> result = new ArrayList<Village>();
        int maxDistSq = maxDist * maxDist;
        for (Village v : this.villages) {
            double distSq;
            if (!v.isLoaded() || !((distSq = v.getOrigin().func_177951_i((Vec3i)blockPos)) < (double)maxDistSq)) continue;
            result.add(v);
        }
        return result;
    }

    public boolean canSleepAt(BlockPos pos) {
        for (Village v : this.villages) {
            if (v.canSleepAt(pos)) continue;
            return false;
        }
        return true;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public boolean isDebugOn() {
        return this.debugOn;
    }

    public void submitStuck(BlockPos bp) {
        this.lastStuck = bp;
    }

    public BlockPos getLastStuck() {
        return this.lastStuck;
    }

    public void onBlockUpdate(World w, BlockPos bp) {
        for (Village v : this.villages) {
            v.onBlockUpdate(w, bp);
        }
    }

    public void onCropGrowEvent(BlockEvent.CropGrowEvent event) {
        for (Village v : this.villages) {
            v.onCropGrowEvent(event);
        }
    }

    public void villageReport(String reportType) {
        System.out.println("VILLAGE REPORT - [" + this.villages.size() + " villages]");
        for (Village v : this.villages) {
            if (!v.isLoaded()) continue;
            v.villageReport(reportType);
        }
    }

    public void func_76184_a(NBTTagCompound nbt) {
    }

    public NBTTagCompound func_189551_b(NBTTagCompound compound) {
        return compound;
    }
}

