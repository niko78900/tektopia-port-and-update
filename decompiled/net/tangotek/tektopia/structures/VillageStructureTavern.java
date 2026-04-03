/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia.structures;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

public class VillageStructureTavern
extends VillageStructure {
    private EntityBard performingBard = null;

    protected VillageStructureTavern(World world, Village v, EntityItemFrame itemFrame) {
        super(world, v, itemFrame, VillageStructureType.TAVERN, "Tavern");
    }

    @Override
    protected void scanSpecialBlock(BlockPos pos, Block block) {
        if (block == Blocks.field_150323_B) {
            this.addSpecialBlock(Blocks.field_150323_B, pos);
        }
        super.scanSpecialBlock(pos, block);
    }

    @Override
    public void update() {
        super.update();
    }

    public void setPerformingBard(EntityBard bard) {
        this.performingBard = bard;
    }

    public EntityBard getPerformingBard() {
        return this.performingBard;
    }

    @Override
    public int getSitTime(EntityVillagerTek villager) {
        return 3000 + villager.func_70681_au().nextInt(2000);
    }

    @Override
    protected boolean shouldVillagerSit(EntityVillagerTek villager) {
        return true;
    }
}

