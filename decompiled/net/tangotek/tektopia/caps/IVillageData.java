/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.caps;

import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.economy.ItemEconomy;

public interface IVillageData {
    public UUID getUUID();

    public boolean isChildReady(long var1);

    public void childSpawned(World var1);

    public ItemEconomy getEconomy();

    public void initEconomy();

    public void setNomadsCheckedToday(boolean var1);

    public boolean getNomadsCheckedToday();

    public void setMerchantCheckedToday(boolean var1);

    public boolean getMerchantCheckedToday();

    public void incrementProfessionSales();

    public int getProfessionSales();

    public boolean completedStartingGifts();

    public void skipStartingGifts();

    public void executeStartingGifts(World var1, Village var2, BlockPos var3);

    public void writeNBT(NBTTagCompound var1);

    public void readNBT(NBTTagCompound var1);

    public boolean isEmpty();
}

