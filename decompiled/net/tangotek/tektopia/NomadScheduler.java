/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.entities.EntityNomad;

public class NomadScheduler {
    protected final World world;
    protected final Village village;
    private boolean dayReset = false;

    public NomadScheduler(World w, Village v) {
        this.world = w;
        this.village = v;
    }

    private boolean spawnCheck(int villagerCount) {
        if (villagerCount <= 3) {
            return true;
        }
        int sqrtClamp = (int)Math.sqrt(villagerCount);
        return this.world.field_73012_v.nextInt(sqrtClamp) == 0;
    }

    public void update() {
        IVillageData vd = this.village.getTownData();
        if (vd != null && this.village.isValid() && !Village.isNightTime(this.world)) {
            this.dayReset = false;
            if (!vd.getNomadsCheckedToday()) {
                vd.setNomadsCheckedToday(true);
                int villagerCount = this.village.getResidentCount();
                if (this.spawnCheck(villagerCount)) {
                    BlockPos pos = this.village.getEdgeNode();
                    if (pos != null) {
                        this.spawnNomad(pos);
                        if (villagerCount <= 1) {
                            this.spawnNomad(pos);
                        }
                        for (int count = 0; this.world.field_73012_v.nextInt(10) < 5 && count < 5; ++count) {
                            this.spawnNomad(pos);
                        }
                    } else {
                        this.village.sendChatMessage("Could not find a spawn location for Nomad.");
                    }
                }
            }
        }
    }

    public void resetDay() {
        if (!this.dayReset) {
            IVillageData vd = this.village.getTownData();
            if (vd != null && this.village.isValid()) {
                vd.setNomadsCheckedToday(false);
            }
            this.dayReset = true;
        }
    }

    private void spawnNomad(BlockPos pos) {
        EntityNomad nomad = new EntityNomad(this.world);
        nomad.func_70012_b((double)pos.func_177958_n() + 0.5, pos.func_177956_o(), (double)pos.func_177952_p() + 0.5, 0.0f, 0.0f);
        nomad.func_180482_a(this.world.func_175649_E(pos), null);
        this.world.func_72838_d((Entity)nomad);
        this.village.debugOut("Spawning Nomad at " + pos);
    }
}

