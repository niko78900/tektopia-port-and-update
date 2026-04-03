/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.Capability$IStorage
 */
package net.tangotek.tektopia.caps;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.economy.ItemEconomy;
import net.tangotek.tektopia.economy.ItemValue;

public class VillageData
implements IVillageData,
Capability.IStorage<IVillageData> {
    private long childSpawnTime = 0L;
    private int totalProfessionSales = 0;
    private boolean checkedMerchants = false;
    private boolean checkedNomads = false;
    private boolean startingGifts = false;
    private boolean isEmpty = true;
    protected ItemEconomy economy = new ItemEconomy();
    private UUID uuid = UUID.randomUUID();

    @Override
    public void initEconomy() {
        if (this.economy.hasItems()) {
            return;
        }
        this.economy.addItem(new ItemValue(new ItemStack(Blocks.field_150364_r, 64, BlockPlanks.EnumType.JUNGLE.func_176839_a()), 4, 22, ProfessionType.LUMBERJACK));
        this.economy.addItem(new ItemValue(new ItemStack(Blocks.field_150364_r, 64, BlockPlanks.EnumType.BIRCH.func_176839_a()), 4, 22, ProfessionType.LUMBERJACK));
        this.economy.addItem(new ItemValue(new ItemStack(Blocks.field_150364_r, 64, BlockPlanks.EnumType.OAK.func_176839_a()), 4, 22, ProfessionType.LUMBERJACK));
        this.economy.addItem(new ItemValue(new ItemStack(Blocks.field_150364_r, 64, BlockPlanks.EnumType.SPRUCE.func_176839_a()), 4, 22, ProfessionType.LUMBERJACK));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151015_O, 64), 4, 20, ProfessionType.FARMER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151174_bG, 64), 4, 20, ProfessionType.FARMER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_185164_cV, 64), 4, 20, ProfessionType.FARMER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151172_bF, 64), 4, 20, ProfessionType.FARMER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151025_P, 16), 8, 10, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151105_aU, 1), 4, 5, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151157_am, 32), 10, 15, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151083_be, 32), 10, 15, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151077_bg, 32), 10, 15, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_179557_bn, 32), 10, 15, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151168_bH, 32), 4, 15, ProfessionType.CHEF));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151024_Q, 1), 2, 10, ProfessionType.BUTCHER));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151027_R, 1), 3, 10, ProfessionType.BUTCHER));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151026_S, 1), 3, 10, ProfessionType.BUTCHER));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151021_T, 1), 2, 10, ProfessionType.BUTCHER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151110_aK, 16), 2, 20, ProfessionType.RANCHER));
        this.economy.addItem(new ItemValue(new ItemStack(Item.func_150898_a((Block)Blocks.field_150325_L), 64), 7, 20, ProfessionType.RANCHER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151137_ax, 64), 5, 20, ProfessionType.MINER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151045_i, 4), 12, 10, ProfessionType.MINER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151100_aR, 16, EnumDyeColor.BLUE.func_176767_b()), 5, 15, ProfessionType.MINER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151043_k, 16), 12, 10, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151035_b, 1), 5, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151036_c, 1), 5, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151040_l, 1), 4, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151030_Z, 1), 10, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151165_aa, 1), 9, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151028_Y, 1), 7, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151167_ab, 1), 6, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151046_w, 1), 10, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151056_x, 1), 10, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151048_u, 1), 8, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151163_ad, 1), 20, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151173_ae, 1), 18, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151161_ac, 1), 14, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack((Item)Items.field_151175_af, 1), 12, 5, ProfessionType.BLACKSMITH));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151121_aF, 16), 3, 20, ProfessionType.ENCHANTER));
        this.economy.addItem(new ItemValue(new ItemStack(Items.field_151122_aG, 8), 8, 20, ProfessionType.ENCHANTER));
    }

    @Override
    public ItemEconomy getEconomy() {
        return this.economy;
    }

    @Override
    public void setNomadsCheckedToday(boolean checked) {
        this.checkedNomads = checked;
    }

    @Override
    public boolean getNomadsCheckedToday() {
        return this.checkedNomads;
    }

    @Override
    public void setMerchantCheckedToday(boolean checked) {
        this.checkedMerchants = checked;
    }

    @Override
    public boolean getMerchantCheckedToday() {
        return this.checkedMerchants;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public boolean isChildReady(long totalWorldTime) {
        return totalWorldTime > this.childSpawnTime;
    }

    @Override
    public void childSpawned(World w) {
        this.childSpawnTime = w.func_82737_E() + (long)MathHelper.func_76136_a((Random)w.field_73012_v, (int)20000, (int)30000);
    }

    @Override
    public void incrementProfessionSales() {
        ++this.totalProfessionSales;
    }

    @Override
    public int getProfessionSales() {
        return this.totalProfessionSales;
    }

    @Override
    public boolean completedStartingGifts() {
        return this.startingGifts;
    }

    @Override
    public void skipStartingGifts() {
        this.startingGifts = true;
    }

    @Override
    public void executeStartingGifts(World world, Village village, BlockPos pos) {
        this.startingGifts = true;
        world.func_175656_a(pos, Blocks.field_150486_ae.func_176223_P());
        TileEntity tileEntity = world.func_175625_s(pos);
        if (tileEntity instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest)tileEntity;
            this.addGiftItem(chest, village, ModItems.structureStorage, 12);
            this.addGiftItem(chest, village, ModItems.structureHome2, 13);
            this.addGiftItem(chest, village, ModItems.itemFarmer, 14);
            this.addGiftItem(chest, village, ModItems.itemLumberjack, 15);
        }
    }

    private void addGiftItem(TileEntityChest chest, Village village, Item item, int slot) {
        ItemStack itemStack = ModItems.createTaggedItem(item, ItemTagType.VILLAGER);
        ModItems.bindItemToVillage(itemStack, village);
        chest.func_70299_a(slot, itemStack);
    }

    @Override
    public void writeNBT(NBTTagCompound compound) {
        compound.func_186854_a("uuid", this.uuid);
        compound.func_74772_a("childTime", this.childSpawnTime);
        compound.func_74757_a("checkedMerchants", this.checkedMerchants);
        compound.func_74757_a("checkedNomads", this.checkedNomads);
        compound.func_74768_a("totalProfessionSales", this.totalProfessionSales);
        compound.func_74757_a("startingGifts", this.startingGifts);
        if (this.economy != null) {
            this.economy.writeNBT(compound);
        }
    }

    @Override
    public void readNBT(NBTTagCompound compound) {
        this.uuid = compound.func_186857_a("uuid");
        if (this.uuid.getLeastSignificantBits() == 0L && this.uuid.getMostSignificantBits() == 0L) {
            this.uuid = UUID.randomUUID();
        }
        this.childSpawnTime = compound.func_74763_f("childTime");
        this.checkedMerchants = compound.func_74767_n("checkedMerchants");
        this.checkedNomads = compound.func_74767_n("checkedNomads");
        this.totalProfessionSales = compound.func_74762_e("totalProfessionSales");
        this.startingGifts = compound.func_74767_n("startingGifts");
        if (compound.func_74764_b("SalesHistory")) {
            this.initEconomy();
            this.getEconomy().readNBT(compound);
        }
        this.isEmpty = false;
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty;
    }

    @Nullable
    public NBTBase writeNBT(Capability<IVillageData> capability, IVillageData instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        instance.writeNBT(compound);
        return compound;
    }

    public void readNBT(Capability<IVillageData> capability, IVillageData instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound)nbt;
            instance.readNBT(compound);
        }
    }
}

