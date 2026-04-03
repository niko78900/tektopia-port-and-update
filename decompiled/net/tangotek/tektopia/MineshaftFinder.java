/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureMineshaft;
import net.tangotek.tektopia.structures.VillageStructureType;

public class MineshaftFinder {
    protected World world;
    private Village village;
    private int debugTick = 100;

    public MineshaftFinder(World w, Village v) {
        this.world = w;
        this.village = v;
    }

    public VillageStructureMineshaft requestMineshaft(EntityVillagerTek miner, Predicate<VillageStructureMineshaft> pred, BiPredicate<VillageStructureMineshaft, VillageStructureMineshaft> compare) {
        VillageStructureMineshaft bestTunnel = null;
        List<VillageStructure> mineshafts = this.village.getStructures(VillageStructureType.MINESHAFT);
        for (VillageStructure struct : mineshafts) {
            VillageStructureMineshaft mine = (VillageStructureMineshaft)struct;
            if (!pred.test(mine)) continue;
            if (mine.getTunnelMiner() == miner) {
                return mine;
            }
            if (mine.getTunnelMiner() != null || bestTunnel != null && !compare.test(mine, bestTunnel)) continue;
            bestTunnel = mine;
        }
        return bestTunnel;
    }

    public void update() {
    }

    private void debugInfo() {
        List<VillageStructure> mineshafts = this.village.getStructures(VillageStructureType.MINESHAFT);
        this.village.debugOut("Mineshafts: " + mineshafts.size());
        int i = 1;
        for (VillageStructure struct : mineshafts) {
            VillageStructureMineshaft mine = (VillageStructureMineshaft)struct;
            this.village.debugOut("       #" + i + "  Door=" + mine.getDoor() + "     Len=" + mine.getTunnelLength());
        }
        this.debugTick = 140;
    }
}

