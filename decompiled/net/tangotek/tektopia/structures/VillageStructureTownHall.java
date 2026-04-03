/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityArchitect;
import net.tangotek.tektopia.entities.EntityTradesman;
import net.tangotek.tektopia.entities.EntityVendor;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public class VillageStructureTownHall
extends VillageStructure {
    protected VillageStructureTownHall(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.TOWNHALL, "Town Hall");
    }

    @Override
    protected void onFloorScanEnd() {
        super.onFloorScanEnd();
        if (this.village.isValid() && this.village.getResidentCount() < 1 && !this.village.getTownData().isEmpty() && !this.village.getTownData().completedStartingGifts()) {
            this.village.getTownData().executeStartingGifts(this.world, this.village, this.safeSpot);
        }
    }

    @Override
    protected void setupServerJobs() {
        this.addJob(new TickJob(200, 0, true, () -> {
            this.trySpawnVendor(EntityArchitect.class, w -> new EntityArchitect((World)w));
            this.trySpawnVendor(EntityTradesman.class, w -> new EntityTradesman((World)w));
        }));
        super.setupServerJobs();
    }

    private <T extends EntityVendor> void trySpawnVendor(Class<T> clazz, Function<World, T> createFunc) {
        BlockPos pos;
        List merchantList = this.world.func_72872_a(clazz, this.getAABB().func_72314_b(2.0, 3.0, 2.0));
        while (merchantList.size() > 1) {
            ((EntityVendor)((Object)merchantList.get(0))).func_70106_y();
            merchantList.remove(0);
        }
        if (merchantList.isEmpty() && (pos = this.getRandomFloorTile()) != null) {
            EntityVendor vendor = (EntityVendor)((Object)createFunc.apply(this.world));
            vendor.func_70012_b((double)pos.func_177958_n() + 0.5, pos.func_177956_o(), (double)pos.func_177952_p() + 0.5, 0.0f, 0.0f);
            vendor.func_180482_a(this.world.func_175649_E(pos), null);
            this.world.func_72838_d((Entity)vendor);
        }
    }

    @Override
    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return this.world.field_73012_v.nextInt(3) == 0;
    }

    @Override
    public int getSitTime(EntityVillagerTek villager) {
        return 100 + villager.func_70681_au().nextInt(300);
    }

    @Override
    public int getMaxAllowed() {
        return 1;
    }
}

