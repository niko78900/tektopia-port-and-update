/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent
 */
package net.tangotek.tektopia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageFarm;

public class FarmFinder {
    private List<VillageFarm> farms = new ArrayList<VillageFarm>();
    private Random rng = new Random();
    protected World world;
    private Village village;
    private double totalFarmWeight;
    private int debugTick;

    public FarmFinder(World w, Village v) {
        this.world = w;
        this.village = v;
    }

    public BlockPos getFarmland(Predicate<BlockPos> pred) {
        double roll = this.world.field_73012_v.nextDouble() * this.totalFarmWeight;
        for (VillageFarm farm : this.farms) {
            BlockPos dirtPos;
            if (farm.getWeightScore() >= roll && (dirtPos = farm.getFarmland(pred)) != null) {
                return dirtPos;
            }
            roll -= farm.getWeightScore();
        }
        return null;
    }

    public BlockPos getMaxAgeCrop() {
        double roll = this.world.field_73012_v.nextDouble() * this.totalFarmWeight;
        for (VillageFarm farm : this.farms) {
            BlockPos cropPos;
            if (farm.getWeightScore() >= roll && (cropPos = farm.getMaxAgeCrop()) != null) {
                return cropPos;
            }
            roll -= farm.getWeightScore();
        }
        return null;
    }

    public void update() {
        for (VillageFarm curFarm : this.farms) {
            curFarm.update(this.village.getCenter());
        }
        for (int i = 0; !(i >= 15 || this.findRandomFarm(0.3f) || this.findRandomFarm(0.6f) || this.findRandomFarm(1.0f)); ++i) {
        }
    }

    private boolean findRandomFarm(float distanceScale) {
        int distance = (int)((float)this.village.getSize() * distanceScale);
        int height = (int)(20.0f * distanceScale);
        BlockPos testPos = new BlockPos((double)(this.village.getCenter().func_177958_n() + distance - this.rng.nextInt(distance * 2)), this.village.getAABB().field_72338_b - (double)height + (double)this.rng.nextInt((int)this.village.getAABB().field_72337_e - (int)this.village.getAABB().field_72338_b + height * 2), (double)(this.village.getCenter().func_177952_p() + distance - this.rng.nextInt(distance * 2)));
        if (VillageFarm.isFarmLand(this.world, testPos)) {
            for (VillageFarm farm : this.farms) {
                if (!farm.isBlockInside(testPos)) continue;
                return false;
            }
            VillageFarm newFarm = new VillageFarm(this.world, testPos, this.village);
            this.farms.add(newFarm);
            this.farms.removeIf(f -> this.isFullyContained((VillageFarm)f));
            this.totalFarmWeight = this.farms.stream().mapToDouble(v -> v.getWeightScore()).sum();
            Collections.sort(this.farms, Comparator.comparingDouble(VillageFarm::getWeightScore));
            return true;
        }
        return false;
    }

    private boolean isFullyContained(VillageFarm farm) {
        for (VillageFarm f : this.farms) {
            if (f == farm || !this.boxContainsBox(f.getAABB(), farm.getAABB())) continue;
            return true;
        }
        return false;
    }

    private boolean boxContainsBox(AxisAlignedBB b1, AxisAlignedBB b2) {
        return b1.field_72340_a <= b2.field_72340_a && b1.field_72336_d >= b2.field_72336_d && b1.field_72339_c <= b2.field_72339_c && b1.field_72334_f >= b2.field_72334_f && b1.field_72338_b <= b2.field_72338_b && b1.field_72337_e >= b2.field_72337_e;
    }

    public void onCropGrowEvent(BlockEvent.CropGrowEvent event) {
        for (VillageFarm farm : this.farms) {
            farm.onCropGrowEvent(event);
        }
    }
}

