/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 */
package net.tangotek.tektopia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class TekTopiaGlobalData
extends WorldSavedData {
    private static final String DATA_NAME = "tektopia_GlobalData";
    private Map<PatreonSupportTier, Set<UUID>> patreonTiers = new HashMap<PatreonSupportTier, Set<UUID>>();
    private Map<UUID, String> licenseMap = new HashMap<UUID, String>();

    public TekTopiaGlobalData() {
        super(DATA_NAME);
    }

    public TekTopiaGlobalData(String s) {
        super(s);
    }

    public static TekTopiaGlobalData get(World world) {
        MapStorage storage = world.func_175693_T();
        TekTopiaGlobalData instance = (TekTopiaGlobalData)storage.func_75742_a(TekTopiaGlobalData.class, DATA_NAME);
        if (instance == null) {
            instance = new TekTopiaGlobalData();
            storage.func_75745_a(DATA_NAME, (WorldSavedData)instance);
        }
        return instance;
    }

    public void func_76184_a(NBTTagCompound nbt) {
        for (PatreonSupportTier tier : PatreonSupportTier.values()) {
            NBTTagList tagList = nbt.func_150295_c(tier.name(), 8);
            if (tagList.func_82582_d()) continue;
            HashSet<UUID> tierSet = new HashSet<UUID>();
            for (int i = 0; i < tagList.func_74745_c(); ++i) {
                tierSet.add(UUID.fromString(tagList.func_150307_f(i)));
            }
            this.patreonTiers.put(tier, tierSet);
        }
    }

    public NBTTagCompound func_189551_b(NBTTagCompound compound) {
        this.patreonTiers.forEach((key, value) -> {
            NBTTagList tagList = new NBTTagList();
            value.forEach(uuid -> tagList.func_74742_a((NBTBase)new NBTTagString(uuid.toString())));
            compound.func_74782_a(key.name(), (NBTBase)tagList);
        });
        return compound;
    }

    public String getPatreonLicense(UUID uuid) {
        return this.licenseMap.get(uuid);
    }

    public void submitPatreonLicense(UUID uuid, String licData) {
        this.licenseMap.put(uuid, licData);
    }

    public static enum PatreonSupportTier {
        Level1,
        Level2,
        Level3,
        Level4;

    }
}

