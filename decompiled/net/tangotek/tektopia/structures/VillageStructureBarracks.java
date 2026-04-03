/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructureHome;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureBarracks
extends VillageStructureHome {
    protected VillageStructureBarracks(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.BARRACKS, "Barracks", 10);
    }

    @Override
    protected void onFloorScanStart() {
        List<EntityArmorStand> stands = this.getEntitiesInside(EntityArmorStand.class);
        stands.forEach(s -> this.addSpecialBlock(Blocks.field_150418_aU, s.func_180425_c()));
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        super.scanSpecialBlock(pos, block);
    }

    @Override
    public boolean canVillagerSleep(EntityVillagerTek villager) {
        return villager instanceof EntityGuard;
    }

    @Override
    public void update() {
        super.update();
    }
}

