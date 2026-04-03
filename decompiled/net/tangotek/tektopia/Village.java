/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.village.MerchantRecipe
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.CapabilityDispatcher
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent
 */
package net.tangotek.tektopia;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.world.BlockEvent;
import net.tangotek.tektopia.FarmFinder;
import net.tangotek.tektopia.MerchantScheduler;
import net.tangotek.tektopia.MineshaftFinder;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.NomadScheduler;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.RaidScheduler;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.blockfinder.BlockFinder;
import net.tangotek.tektopia.blockfinder.SaplingScanner;
import net.tangotek.tektopia.blockfinder.SugarCaneScanner;
import net.tangotek.tektopia.blockfinder.TreeScanner;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.caps.VillageData;
import net.tangotek.tektopia.entities.EntityMerchant;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.pathing.PathingGraph;
import net.tangotek.tektopia.storage.InventoryScanner;
import net.tangotek.tektopia.storage.VillagerInventory;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureMineshaft;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;
import net.tangotek.tektopia.tickjob.TickJobQueue;

public class Village {
    protected Map<VillageStructureType, List<VillageStructure>> structures = new HashMap<VillageStructureType, List<VillageStructure>>();
    protected BlockFinder blockFinder;
    protected FarmFinder farmFinder;
    protected MineshaftFinder mineFinder;
    protected PathingGraph pathingGraph;
    private ArrayDeque<BlockPos> villagerPositions = new ArrayDeque();
    private long lastVillagerPosTime = 0L;
    private boolean isDestroyed = false;
    private World world;
    private List<TileEntityChest> storage = new ArrayList<TileEntityChest>();
    private Map<BlockPos, InventoryScanner> storageScanners = new HashMap<BlockPos, InventoryScanner>();
    private BlockPos center;
    private BlockPos origin;
    private CapabilityDispatcher capabilities = null;
    private int cleanTick = 0;
    private int tickCounter = 0;
    private int guardSleepOffset = 1000 - EntityVillagerTek.SLEEP_START_TIME;
    private int clericSleepOffset = 1000 - EntityVillagerTek.SLEEP_START_TIME;
    private AxisAlignedBB aabb;
    private List<VillageEnemy> enemies = new ArrayList<VillageEnemy>();
    private BlockPos enemySighting = null;
    protected TickJobQueue jobs = new TickJobQueue();
    protected MerchantScheduler merchantScheduler;
    protected NomadScheduler nomadScheduler;
    protected RaidScheduler raidScheduler;
    public static final int VILLAGE_SIZE = 120;
    private IVillageData villageData;
    private static final VillageData emptyVillageData = new VillageData();
    private String villageName = "";
    private UUID villageUUID;
    private List<EntityVillagerTek> residents = new ArrayList<EntityVillagerTek>();
    private Set<EntityVillagerTek> activeDefenders = new HashSet<EntityVillagerTek>();

    public Village(World worldIn, BlockPos origin) {
        this.blockFinder = new BlockFinder();
        this.farmFinder = new FarmFinder(worldIn, this);
        this.mineFinder = new MineshaftFinder(worldIn, this);
        this.origin = origin;
        this.pathingGraph = new PathingGraph(worldIn, this);
        this.world = worldIn;
        this.merchantScheduler = new MerchantScheduler(worldIn, this);
        this.nomadScheduler = new NomadScheduler(worldIn, this);
        this.raidScheduler = new RaidScheduler(worldIn, this);
        this.cleanTick = this.world.field_73012_v.nextInt(40) + 40;
        this.blockFinder.registerBlockScanner(new TreeScanner(this, 30));
        this.blockFinder.registerBlockScanner(new SugarCaneScanner(this, 15));
        this.blockFinder.registerBlockScanner(new SaplingScanner(this, 15));
        this.setupServerJobs();
    }

    public void addJob(TickJob job) {
        this.jobs.addJob(job);
    }

    public void debugOut(String text) {
        System.out.println("[" + this.villageName + "] " + text);
    }

    protected void setupServerJobs() {
        this.addJob(new TickJob(100, 100, true, () -> this.merchantScheduler.update()));
        this.addJob(new TickJob(200, 100, true, () -> this.nomadScheduler.update()));
        this.addJob(new TickJob(10, 5, true, () -> {
            this.enemySighting = null;
        }));
        this.addJob(new TickJob(4000, 2000, true, () -> {
            IVillageData vd = this.getTownData();
            if (vd != null) {
                vd.getEconomy().refreshValues(this);
            }
        }));
    }

    public ItemStack getVillageToken() {
        EntityItemFrame frame;
        VillageStructure townHall = this.getNearestStructure(VillageStructureType.TOWNHALL, this.getOrigin());
        if (townHall != null && (frame = townHall.getItemFrame()) != null) {
            return frame.func_82335_i();
        }
        return ItemStack.field_190927_a;
    }

    public IVillageData getTownData() {
        VillageStructure townHall;
        if (this.origin != null && (townHall = this.getNearestStructure(VillageStructureType.TOWNHALL, this.origin)) != null) {
            this.villageData = townHall.getData();
            if (this.villageData != null) {
                return this.villageData;
            }
        }
        this.debugOut("Returning EMPTY village data");
        return emptyVillageData;
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public BlockPos getOrigin() {
        return this.origin;
    }

    public World getWorld() {
        return this.world;
    }

    public PathingGraph getPathingGraph() {
        return this.pathingGraph;
    }

    public boolean isLoaded() {
        if (this.getOrigin() != null) {
            return this.world.func_175667_e(this.getOrigin());
        }
        return false;
    }

    private boolean canAddStructure(VillageStructure struct) {
        List<VillageStructure> structs;
        return this.isStructureValid(struct) && ((structs = this.structures.get((Object)struct.type)) == null || struct.getMaxAllowed() == 0 || structs.size() < struct.getMaxAllowed());
    }

    public boolean addStructure(VillageStructure struct) {
        if (this.canAddStructure(struct)) {
            List<VillageStructure> structList = this.structures.get((Object)struct.type);
            if (structList == null) {
                structList = new ArrayList<VillageStructure>();
                this.structures.put(struct.type, structList);
            }
            if (!structList.contains(struct)) {
                if (structList.add(struct)) {
                    EntityItemFrame frame = struct.getItemFrame();
                    if (frame != null && frame.func_82335_i() != null) {
                        ModItems.bindItemToVillage(struct.getItemFrame().func_82335_i(), this);
                    }
                    if (struct.type == VillageStructureType.TOWNHALL && this.pathingGraph.nodeCount() <= 0) {
                        this.pathingGraph.seedVillage(struct.getDoorOutside());
                        this.origin = struct.getDoorOutside();
                        this.addVillagerPosition(struct.getDoorOutside());
                        ItemStack villageToken = this.getVillageToken();
                        this.villageName = !villageToken.func_190926_b() && villageToken.func_82837_s() && !villageToken.func_82833_r().equals("Town Hall") ? villageToken.func_82833_r() : Village.randomAlphaNumeric(3);
                        this.villageUUID = this.getTownData().getUUID();
                        this.debugOut("Village ADDED - [" + this.getName() + "]   " + this.villageUUID);
                    }
                    this.debugOut("Adding structure " + (Object)((Object)struct.type) + " [" + struct.getDoor() + "]");
                    if (struct.adjustsVillageCenter()) {
                        this.updateCenter();
                    }
                    return true;
                }
            } else {
                this.debugOut("Tried adding structure that already existed - " + struct.type.name());
            }
        }
        return false;
    }

    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random() * (double)"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length());
            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(character));
        }
        return builder.toString();
    }

    public void forceRaid(int raidPoints) {
        this.raidScheduler.forceRaid(raidPoints);
    }

    public boolean canSleepAt(BlockPos pos) {
        if (VillageStructureHome.isBed(this.world, pos)) {
            List<VillageStructure> homes = this.getHomes();
            for (VillageStructure struct : homes) {
                VillageStructureHome home = (VillageStructureHome)struct;
                if (home.canSleepAt(pos)) continue;
                return false;
            }
        }
        return true;
    }

    public int getNextGuardSleepOffset() {
        int result = this.guardSleepOffset;
        this.guardSleepOffset += 7677;
        if (this.guardSleepOffset > 24000) {
            this.guardSleepOffset -= 24000;
        }
        return result;
    }

    public int getNextClericSleepOffset() {
        int result = this.clericSleepOffset;
        this.clericSleepOffset += 7777;
        if (this.clericSleepOffset > 24000) {
            this.clericSleepOffset -= 24000;
        }
        return result;
    }

    public List<VillageStructure> getHomes() {
        return this.getStructures(VillageStructureType.BARRACKS, VillageStructureType.HOME2, VillageStructureType.HOME4, VillageStructureType.HOME6);
    }

    public List<VillageStructure> getStructures(VillageStructureType ... types) {
        ArrayList<VillageStructure> outList = new ArrayList<VillageStructure>();
        for (VillageStructureType arg : types) {
            outList.addAll(this.getStructures(arg));
        }
        return outList;
    }

    public List<VillageStructure> getStructures(VillageStructureType type) {
        List<VillageStructure> structList = this.structures.get((Object)type);
        if (structList != null && !structList.isEmpty()) {
            return new ArrayList<VillageStructure>(structList);
        }
        return new ArrayList<VillageStructure>();
    }

    public boolean hasStructure(VillageStructureType type) {
        List<VillageStructure> structList = this.structures.get((Object)type);
        return structList != null && !structList.isEmpty();
    }

    public VillageStructure getNearestStructure(VillageStructureType type, BlockPos pos) {
        List<VillageStructure> structList = this.getStructures(type);
        double min = Double.MAX_VALUE;
        VillageStructure closest = null;
        for (VillageStructure struct : structList) {
            double dist = pos.func_177951_i((Vec3i)struct.getDoor());
            if (!(dist < min)) continue;
            min = dist;
            closest = struct;
        }
        return closest;
    }

    public boolean isStructureValid(VillageStructure struct) {
        if (!struct.isValid()) {
            return false;
        }
        boolean count = false;
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure vs : lst) {
                if (vs == struct || !vs.isStructureOverlapped(struct)) continue;
                this.debugOut("Structures overlap | " + (Object)((Object)struct.type) + "  " + vs.getAABB());
                return false;
            }
        }
        return true;
    }

    public void addVillagerPosition(EntityVillagerTek villager) {
        this.addVillagerPosition(villager.func_180425_c());
    }

    public void addVillagerPosition(BlockPos pos) {
        if (this.world.func_82737_E() - this.lastVillagerPosTime > 20L && this.getStructure(pos) == null) {
            this.villagerPositions.addLast(pos);
            this.lastVillagerPosTime = this.world.func_82737_E();
            while (this.villagerPositions.size() > 20) {
                this.villagerPositions.removeFirst();
            }
        }
    }

    public BlockPos getLastVillagerPos() {
        if (this.villagerPositions.isEmpty()) {
            return this.getOrigin();
        }
        BlockPos pos = this.villagerPositions.removeFirst();
        this.villagerPositions.addLast(pos);
        return pos;
    }

    public AxisAlignedBB getAABB() {
        return this.aabb;
    }

    public boolean isInStructure(BlockPos bp) {
        return this.getStructure(bp) != null;
    }

    public VillageStructure getStructure(BlockPos bp) {
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure vs : lst) {
                if (!vs.isBlockInside(bp)) continue;
                return vs;
            }
        }
        return null;
    }

    public VillageStructure getStructureFromFrame(BlockPos bp) {
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure vs : lst) {
                if (!vs.getFramePos().equals((Object)bp)) continue;
                return vs;
            }
        }
        return null;
    }

    public void purchaseFromMerchant(MerchantRecipe recipe, EntityMerchant merchant, EntityPlayer player) {
        IVillageData vd;
        String skullOwner;
        if (recipe.func_77397_d().func_77973_b() == Items.field_151144_bL && !(skullOwner = ModItems.getSkullAnimal(recipe.func_77397_d())).isEmpty()) {
            this.merchantScheduler.addOrder(skullOwner);
            player.field_71071_by.func_70437_b(ItemStack.field_190927_a);
            player.field_71071_by.func_174925_a(Items.field_151144_bL, 3, 0, null);
            player.func_145747_a((ITextComponent)new TextComponentString("The merchant will return tomorrow with a " + skullOwner + "."));
        }
        if ((vd = this.getTownData()) != null) {
            this.debugOut("Player selling " + recipe.func_77394_a().func_77973_b().func_77658_a() + " [" + player.func_70005_c_() + "]");
            vd.getEconomy().sellItem(recipe, this);
        }
    }

    public void trackHappy(int delta) {
    }

    public void resetStorage() {
        this.storage.clear();
        this.storageScanners.clear();
    }

    public void addStorageChest(TileEntityChest chest) {
        this.storage.add(chest);
        this.storageScanners.put(chest.func_174877_v(), new InventoryScanner((IInventory)chest));
        this.debugOut("Storage Chest [" + chest.func_174877_v() + "]   Added    (" + this.storage.size() + " total)");
    }

    public void removeStorageChest(TileEntityChest chest) {
        this.storage.remove(chest);
        this.storageScanners.remove(chest.func_174877_v());
        this.debugOut("Storage Chest [" + chest.func_174877_v() + "]   Removed    (" + this.storage.size() + " total)");
    }

    public int getStorageCount(Predicate<ItemStack> predicate) {
        int count = 0;
        for (TileEntityChest chest : this.storage) {
            if (chest.func_145837_r() || chest.func_191420_l()) continue;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                ItemStack itemStack = chest.func_70301_a(i);
                if (!predicate.test(itemStack)) continue;
                count += itemStack.func_190916_E();
            }
        }
        return count;
    }

    public TileEntityChest getStorageChestWithItem(Function<ItemStack, Integer> function) {
        int bestResult = 0;
        TileEntityChest bestChest = null;
        for (TileEntityChest chest : this.storage) {
            if (chest.func_145837_r() || chest.func_191420_l()) continue;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                int result;
                ItemStack itemStack = chest.func_70301_a(i);
                if (itemStack.func_190926_b() || (result = function.apply(itemStack).intValue()) <= bestResult) continue;
                bestResult = result;
                bestChest = chest;
            }
        }
        return bestChest;
    }

    private int chestCompareTo(TileEntityChest chest1, TileEntityChest chest2, BlockPos origin) {
        double d1 = chest1.func_145835_a((double)origin.func_177958_n(), (double)origin.func_177956_o(), (double)origin.func_177952_p());
        double d2 = chest2.func_145835_a((double)origin.func_177958_n(), (double)origin.func_177956_o(), (double)origin.func_177952_p());
        return Double.compare(d1, d2);
    }

    public TileEntityChest getAvailableStorageChest(ItemStack testStack, BlockPos origin) {
        TileEntityChest bestChest = null;
        this.storage.sort((chest1, chest2) -> this.chestCompareTo((TileEntityChest)chest1, (TileEntityChest)chest2, origin));
        for (TileEntityChest chest : this.storage) {
            if (chest.func_145837_r()) continue;
            int openSlot = -1;
            boolean matchedThisChest = false;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                ItemStack itemStack = chest.func_70301_a(i);
                if (itemStack.func_190926_b() && openSlot < 0) {
                    openSlot = i;
                    if (!matchedThisChest) continue;
                    return chest;
                }
                if (!VillagerInventory.areItemsStackable(itemStack, testStack)) continue;
                matchedThisChest = true;
                if (itemStack.func_190916_E() >= itemStack.func_77976_d()) continue;
                return chest;
            }
            if (openSlot < 0) continue;
            if (matchedThisChest) {
                return chest;
            }
            if (bestChest != null) continue;
            bestChest = chest;
        }
        return bestChest;
    }

    public VillageStructureHome getAvailableHome(EntityVillagerTek villager) {
        List<VillageStructure> homes = this.getHomes();
        for (VillageStructure struct : homes) {
            VillageStructureHome home = (VillageStructureHome)struct;
            if (home.isFull() || !home.canVillagerSleep(villager)) continue;
            return home;
        }
        return null;
    }

    public int getSize() {
        return 120;
    }

    private void occupancySpam() {
        int beds = 0;
        int residents = 0;
        List<VillageStructure> homes = this.getHomes();
        for (VillageStructure struct : homes) {
            VillageStructureHome home = (VillageStructureHome)struct;
            beds += home.getMaxResidents();
            residents += home.getCurResidents();
        }
        this.debugOut("Residents:  " + residents + " / " + beds);
    }

    public int getResidentCount() {
        return this.residents.size();
    }

    public String getName() {
        return this.villageName;
    }

    public void villageReport(String reportType) {
        IVillageData vd = this.getTownData();
        if (vd != null) {
            StringBuilder reportOut = new StringBuilder();
            reportOut.append("\n");
            reportOut.append("----- Village Report --[" + this.villageName + "]---------\n");
            if (this.isReportType(reportType, "homes")) {
                this.homeReport(reportOut);
            }
            if (this.isReportType(reportType, "happy")) {
                this.happyReport(reportOut);
            }
            if (this.isReportType(reportType, "hunger")) {
                this.hungerReport(reportOut);
            }
            if (this.isReportType(reportType, "levels")) {
                this.levelReport(reportOut);
            }
            if (this.isReportType(reportType, "villagers")) {
                this.villagerReport(reportOut);
            }
            if (this.isReportType(reportType, "economy")) {
                vd.getEconomy().report(reportOut);
            }
            reportOut.append("------------------------------------\n");
            this.debugOut(reportOut.toString());
        } else {
            System.err.println("==== Village Without VillageData?? ====");
        }
    }

    private void homeReport(StringBuilder builder) {
        List<VillageStructure> homes = this.getHomes();
        builder.append("    " + homes.size() + " homes - " + this.residents.size() + " residents");
        for (VillageStructure struct : homes) {
            VillageStructureHome home = (VillageStructureHome)struct;
            home.villageReport(builder);
        }
    }

    private void happyReport(StringBuilder builder) {
        int HAPPY_BUCKETS = 5;
        int BUCKET_SIZE = 20;
        int[] happyBuckets = new int[5];
        for (EntityVillagerTek villager : this.residents) {
            int bucket;
            int n = bucket = MathHelper.func_76125_a((int)(villager.getHappy() / 20), (int)0, (int)4);
            happyBuckets[n] = happyBuckets[n] + 1;
        }
        for (int i = 0; i < 5; ++i) {
            int step = 20 * i;
            builder.append("Happy " + String.format("%-2s", step) + " - " + String.format("%-2s", step + 20 - 1) + ") " + String.format("%-3s", happyBuckets[i]));
            if (happyBuckets[i] > 0) {
                char[] chars = new char[happyBuckets[i]];
                Arrays.fill(chars, 'P');
                builder.append("  " + new String(chars));
            }
            builder.append("\n");
        }
    }

    private void hungerReport(StringBuilder builder) {
        int HUNGER_BUCKETS = 5;
        int BUCKET_SIZE = 20;
        int[] hungerBuckets = new int[5];
        for (EntityVillagerTek villager : this.residents) {
            int bucket;
            int n = bucket = MathHelper.func_76125_a((int)(villager.getHunger() / 20), (int)0, (int)4);
            hungerBuckets[n] = hungerBuckets[n] + 1;
        }
        for (int i = 0; i < 5; ++i) {
            int step = 20 * i;
            builder.append("Hunger " + String.format("%-2s", step) + " - " + String.format("%-2s", step + 20 - 1) + ") " + String.format("%-3s", hungerBuckets[i]));
            if (hungerBuckets[i] > 0) {
                char[] chars = new char[hungerBuckets[i]];
                Arrays.fill(chars, 'H');
                builder.append("  " + new String(chars));
            }
            builder.append("\n");
        }
    }

    private void levelReport(StringBuilder builder) {
        class ProfessionLevelTracker {
            public int ct = 0;
            public int sum = 0;
            public int days = 0;
            public ProfessionType pt;
            List<EntityVillagerTek> villagers = new ArrayList<EntityVillagerTek>();

            ProfessionLevelTracker(ProfessionType pt) {
                this.pt = pt;
            }

            int getAvg() {
                return this.sum / this.ct;
            }

            float getSkillsPerDay() {
                return (float)this.sum / (float)this.days;
            }

            public void report(StringBuilder builder) {
                builder.append(String.format("%-15s", this.pt.name) + " [" + String.format("%-2s", this.getAvg()) + "]   [" + String.format("%.2f", Float.valueOf(this.getSkillsPerDay())) + " per day] ");
                this.villagers.forEach(v -> builder.append(String.format("%-2s", v.getBaseSkill(this.pt)) + " "));
                builder.append("\n");
            }
        }
        HashMap<ProfessionType, ProfessionLevelTracker> levelMap = new HashMap<ProfessionType, ProfessionLevelTracker>();
        for (EntityVillagerTek v : this.residents) {
            ProfessionLevelTracker tracker = (ProfessionLevelTracker)levelMap.get((Object)v.getProfessionType());
            if (tracker == null) {
                tracker = new ProfessionLevelTracker(v.getProfessionType());
                levelMap.put(v.getProfessionType(), tracker);
            }
            int baseSkill = v.getBaseSkill(v.getProfessionType());
            ++tracker.ct;
            tracker.sum += baseSkill;
            tracker.days += v.getDaysAlive();
            tracker.villagers.add(v);
        }
        levelMap.entrySet().stream().sorted(Comparator.comparing(e -> ((ProfessionLevelTracker)e.getValue()).getAvg())).forEach(e -> ((ProfessionLevelTracker)e.getValue()).report(builder));
    }

    private void villagerReport(StringBuilder builder) {
        BlockPos lastStuckPos = VillageManager.get(this.world).getLastStuck();
        builder.append("Villagers: " + this.residents.size());
        if (lastStuckPos != null) {
            builder.append(" | LastStuck: " + lastStuckPos);
        }
        builder.append("\n");
        if (this.residents.isEmpty()) {
            builder.append("  (none)\n");
            return;
        }
        this.residents.stream().sorted(Comparator.comparing(v -> v.func_70005_c_())).forEach(v -> {
            String profession = v.getProfessionType() == null ? "none" : v.getProfessionType().name;
            VillageStructure struct = v.getCurrentStructure();
            String structName = struct == null ? "none" : struct.type.name();
            Path path = v.func_70661_as() == null ? null : v.func_70661_as().func_75505_d();
            String pathInfo = "none";
            if (path != null && !v.func_70661_as().func_75500_f()) {
                pathInfo = String.format("%s/%s", path.func_75873_e() + 1, path.func_75874_d());
            }
            String reason = v.getAIDebugReason();
            if (reason.isEmpty()) {
                if (v.isSleeping()) {
                    reason = "sleeping";
                } else if (v.shouldSleep()) {
                    reason = "needs_sleep";
                } else if (v.getIdle() > 200) {
                    reason = "idle_" + v.getIdle() / 20 + "s";
                } else if (v.isStoragePriority()) {
                    reason = "storage_priority";
                } else {
                    reason = "-";
                }
            }
            BlockPos debugTarget = v.getAIDebugTarget();
            String targetInfo = debugTarget == null ? "-" : debugTarget.toString();
            String modeInfo = v.getMovementMode() == null ? "none" : v.getMovementMode().name();
            String animInfo = v.getServerAnimationName();
            if (animInfo == null || animInfo.isEmpty()) {
                animInfo = "-";
            }
            builder.append("  - ");
            builder.append(String.format("%s [%s] hp %.1f/%.1f hunger %s happy %s idle %ss", v.func_70005_c_(), profession, Float.valueOf(v.func_110143_aJ()), Float.valueOf(v.func_110138_aP()), v.getHunger(), v.getHappy(), v.getIdle() / 20));
            builder.append(String.format(" | work %s sleep %s starving %s", v.isWorkTime(), v.isSleeping(), v.isStarving()));
            builder.append(String.format(" | pos %s struct %s", v.func_180425_c(), structName));
            builder.append(String.format(" | path %s mode %s anim %s", pathInfo, modeInfo, animInfo));
            builder.append(String.format(" | task %s reason %s target %s priority %s", v.getAIDebugTask(), reason, targetInfo, v.isStoragePriority()));
            builder.append("\n");
        });
    }

    private boolean isReportType(String reportType, String testType) {
        return reportType.equals("all") || reportType.equals(testType);
    }

    public BlockPos getEdgeNode() {
        BlockPos pos;
        BasePathingNode node = this.pathingGraph.getEdgeNode(this.getOrigin(), (double)this.getSize() * 0.9);
        if (node != null && this.getStructure(pos = node.getBlockPos()) == null) {
            return pos;
        }
        return null;
    }

    private static long fixTime(long time) {
        while (time > 24000L) {
            time -= 24000L;
        }
        while (time < 0L) {
            time += 24000L;
        }
        return time;
    }

    public static boolean isTimeOfDay(World world, long startTime, long endTime, long offset) {
        startTime = Village.fixTime(startTime + offset);
        endTime = Village.fixTime(endTime + offset);
        long time = Village.getTimeOfDay(world);
        if (endTime > startTime) {
            return time >= startTime && time <= endTime;
        }
        return time >= startTime || time <= endTime;
    }

    public static boolean isTimeOfDay(World world, long startTime, long endTime) {
        return Village.isTimeOfDay(world, startTime, endTime, 0L);
    }

    public static long getTimeOfDay(World world) {
        return world.func_72820_D() % 24000L;
    }

    public static boolean isNightTime(World world) {
        long timeOfDay = Village.getTimeOfDay(world);
        return timeOfDay > 11500L || timeOfDay < 500L || timeOfDay < 200L || world.func_72896_J();
    }

    private void updateCenter() {
        double farthestStructure = 0.0;
        this.center = this.getOrigin();
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure struct : lst) {
                if (!struct.adjustsVillageCenter()) continue;
                if (struct.type == VillageStructureType.STORAGE) {
                    this.center = struct.getDoorOutside();
                }
                if (this.aabb == null) {
                    this.aabb = struct.getAABB();
                    continue;
                }
                this.aabb = this.aabb.func_111270_a(struct.getAABB());
            }
        }
    }

    public void destroy() {
        this.debugOut("== Village being destroyed ==");
        this.isDestroyed = true;
    }

    private void cleanVillage() {
        for (List<VillageStructure> lst : this.structures.values()) {
            for (int i = lst.size() - 1; i >= 0; --i) {
                VillageStructure struct = lst.get(i);
                if (this.isStructureValid(struct)) continue;
                this.debugOut("Removing structure " + (Object)((Object)struct.type) + " [" + struct.getDoor() + "]");
                struct.onDestroy();
                lst.remove(i);
                if (!struct.adjustsVillageCenter()) continue;
                this.updateCenter();
            }
        }
        VillageManager vm = VillageManager.get(this.world);
        vm.addScanBox(new AxisAlignedBB(this.center).func_72314_b(64.0, 64.0, 64.0));
        this.structures.entrySet().removeIf(entry -> ((List)entry.getValue()).isEmpty());
        this.clearDeadEnemies();
        this.cleanDebugTurds();
        this.cleanTick = this.world.field_73012_v.nextInt(40) + 40;
    }

    private void tickInventoryScanners() {
        for (InventoryScanner scanner : this.storageScanners.values()) {
            int changedSlot = scanner.tickSlot();
            if (changedSlot < 0) continue;
            this.notifyResidentsStorageUpdate(scanner.getChangedItem());
        }
    }

    private void notifyResidentsStorageUpdate(ItemStack updatedItem) {
        if (!updatedItem.func_190926_b()) {
            this.residents.forEach(v -> v.onStorageChange(updatedItem));
        }
    }

    public void onStorageChange(TileEntityChest chest, int slot, ItemStack updatedItem) {
        InventoryScanner scanner = this.storageScanners.get(chest.func_174877_v());
        if (scanner != null) {
            scanner.updateSlotSilent(slot);
        }
        this.notifyResidentsStorageUpdate(updatedItem);
    }

    public void addResident(EntityVillagerTek v) {
        this.residents.add(v);
    }

    public void removeResident(EntityVillagerTek v) {
        this.residents.remove((Object)v);
    }

    public void update() {
        this.jobs.tick();
        this.blockFinder.update();
        this.farmFinder.update();
        this.mineFinder.update();
        this.pathingGraph.update();
        this.raidScheduler.update();
        this.tickInventoryScanners();
        for (List<VillageStructure> lst : this.structures.values()) {
            for (VillageStructure struct : lst) {
                struct.update();
            }
        }
        ++this.tickCounter;
        --this.cleanTick;
        if (this.cleanTick <= 0) {
            this.cleanVillage();
        }
        if (!this.world.func_72935_r()) {
            this.merchantScheduler.resetDay();
            this.nomadScheduler.resetDay();
        }
    }

    public boolean isValid() {
        if (this.isDestroyed) {
            return false;
        }
        ItemStack villageToken = this.getVillageToken();
        if (villageToken.func_190926_b()) {
            this.debugOut("Village has no Token");
            return false;
        }
        if (!this.getTownData().getUUID().equals(this.villageUUID)) {
            this.debugOut("Village UUID changed.  Token swap?");
            return false;
        }
        if (this.getOrigin() != null && !this.world.func_175667_e(this.getOrigin())) {
            this.debugOut("Village unloaded");
            return false;
        }
        if (this.center == null) {
            System.err.println("Village has no center");
            return false;
        }
        if (this.structures.size() <= 0) {
            this.debugOut("Village has no structures");
            return false;
        }
        return true;
    }

    public boolean isInVillage(BlockPos pos) {
        return Math.max(Math.abs(this.getOrigin().func_177952_p() - pos.func_177952_p()), Math.abs(this.getOrigin().func_177958_n() - pos.func_177958_n())) < 120;
    }

    public BlockPos getEnemySighting() {
        return this.enemySighting;
    }

    public boolean enemySeenRecently() {
        return !this.enemies.isEmpty();
    }

    private void clearDeadEnemies() {
        Iterator<VillageEnemy> iterator = this.enemies.iterator();
        while (iterator.hasNext()) {
            VillageEnemy enemy = iterator.next();
            if (enemy.enemy.func_70089_S() && Math.abs(this.tickCounter - enemy.aggressionTime) <= 400) continue;
            iterator.remove();
        }
    }

    private void cleanDebugTurds() {
        List stands = this.world.func_72872_a(EntityArmorStand.class, this.getAABB());
        for (EntityArmorStand st : stands) {
            if (!st.func_95999_t().equals("PathNode") || st.field_70173_aa <= 400) continue;
            st.func_70106_y();
        }
    }

    public void addOrRenewEnemy(EntityLivingBase enemy, int threat) {
        this.enemySighting = enemy.func_180425_c();
        for (VillageEnemy existingEnemy : this.enemies) {
            if (existingEnemy.enemy != enemy) continue;
            existingEnemy.threat += threat;
            existingEnemy.aggressionTime = this.tickCounter;
            return;
        }
        this.enemies.add(new VillageEnemy(enemy, this.tickCounter));
    }

    public EntityLivingBase getEnemyTarget(EntityVillagerTek villager) {
        double closest = Double.MAX_VALUE;
        boolean foundMinion = false;
        EntityLivingBase target = null;
        for (VillageEnemy existingEnemy : this.enemies) {
            if (!existingEnemy.enemy.func_70089_S()) continue;
            boolean isMinion = EntityNecromancer.isMinion(existingEnemy.enemy);
            if (isMinion && !foundMinion) {
                closest = Double.MAX_VALUE;
                target = null;
            }
            if (!isMinion && foundMinion) continue;
            double dist = existingEnemy.enemy.func_70068_e((Entity)villager);
            if (dist < closest) {
                closest = dist;
                target = existingEnemy.enemy;
            }
            foundMinion = isMinion;
        }
        return target;
    }

    private void cleanDefenders() {
        this.activeDefenders.removeIf(d -> d.func_70638_az() == null || !d.func_70638_az().func_70089_S() || !d.func_70089_S());
    }

    public void addActiveDefender(EntityVillagerTek villager) {
        this.activeDefenders.add(villager);
    }

    public EntityVillagerTek getActiveDefender(BlockPos pos) {
        this.cleanDefenders();
        EntityVillagerTek defender = this.activeDefenders.stream().min(Comparator.comparing(e -> Float.valueOf(e.func_110143_aJ()))).orElse(null);
        return defender;
    }

    public void onBlockUpdate(World w, BlockPos bp) {
        this.pathingGraph.onBlockUpdate(w, bp);
    }

    public void onCropGrowEvent(BlockEvent.CropGrowEvent event) {
        if (this.isInVillage(event.getPos())) {
            this.farmFinder.onCropGrowEvent(event);
        }
    }

    public void reportVillagerDamage(EntityVillagerTek villager, DamageSource damageSrc, float damageAmount) {
        villager.func_70690_d(new PotionEffect(MobEffects.field_188423_x, 200));
        String damageMessage = villager.func_145748_c_().func_150260_c() + " [" + (villager.getProfessionType() == null ? "" : villager.getProfessionType().name) + "] has taken " + String.format("%.2f", Float.valueOf(damageAmount)) + " damage";
        if (damageSrc.func_76346_g() != null) {
            damageMessage = damageMessage + " from " + damageSrc.func_76346_g().func_145748_c_().func_150260_c();
        }
        String finalDamageMessage = damageMessage;
        this.sendChatMessage(finalDamageMessage);
        this.debugOut("[Villager Damage] " + finalDamageMessage);
    }

    public void reportVillagerDeath(EntityVillagerTek villager, DamageSource damageSrc) {
        String damageMessage = villager.func_145748_c_().func_150260_c() + " [" + villager.getProfessionType().name + "] has been killed by " + damageSrc.func_76355_l() + " damage. [" + villager.func_180425_c().func_177958_n() + ", " + villager.func_180425_c().func_177956_o() + ", " + villager.func_180425_c().func_177952_p() + "]";
        this.playEvent(SoundEvents.field_187911_gk, (ITextComponent)new TextComponentString(damageMessage));
        List necros = this.world.func_72872_a(EntityNecromancer.class, new AxisAlignedBB(this.getOrigin()).func_186662_g(120.0));
        necros.forEach(n -> n.notifyVillagerDeath());
    }

    public void sendChatMessage(String msg) {
        this.sendChatMessage((ITextComponent)new TextComponentString(msg));
    }

    public void sendChatMessage(ITextComponent textComponent) {
        List nearbyPlayers = this.world.func_175661_b(EntityPlayerMP.class, p -> p.func_180425_c().func_177951_i((Vec3i)this.getOrigin()) < 40000.0);
        nearbyPlayers.stream().forEach(p -> p.func_145747_a(textComponent));
        this.debugOut(textComponent.func_150260_c());
    }

    public void playEvent(SoundEvent soundEvent, ITextComponent textComponent) {
        for (EntityPlayerMP entityPlayer : this.world.func_72872_a(EntityPlayerMP.class, this.getAABB().func_186662_g(50.0))) {
            entityPlayer.field_70170_p.func_184148_a(null, entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v, soundEvent, SoundCategory.MASTER, 1.2f, 1.0f);
            entityPlayer.func_145747_a(textComponent);
        }
    }

    public boolean hasBlock(Block b) {
        return this.blockFinder.hasBlock(b);
    }

    public BlockPos requestBlock(Block b) {
        return this.blockFinder.requestBlock(b);
    }

    public void releaseBlockClaim(Block block, BlockPos pos) {
        this.blockFinder.releaseClaim(this.world, block, pos);
    }

    public VillageStructureMineshaft requestMineshaft(EntityVillagerTek miner, Predicate<VillageStructureMineshaft> pred, BiPredicate<VillageStructureMineshaft, VillageStructureMineshaft> compare) {
        return this.mineFinder.requestMineshaft(miner, pred, compare);
    }

    public BlockPos requestMaxAgeCrop() {
        return this.farmFinder.getMaxAgeCrop();
    }

    public BlockPos requestFarmland(Predicate<BlockPos> pred) {
        return this.farmFinder.getFarmland(pred);
    }

    class VillageEnemy {
        public EntityLivingBase enemy;
        public int aggressionTime;
        public int threat;

        VillageEnemy(EntityLivingBase enemy, int agressionTimeIn) {
            this.enemy = enemy;
            this.aggressionTime = agressionTimeIn;
            this.threat = 1;
        }
    }
}
