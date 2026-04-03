/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;
import net.tangotek.tektopia.tickjob.TickJobQueue;

public class RaidScheduler {
    protected final World world;
    protected final Village village;
    private boolean forceFail = false;
    private int forceRaid = -1;
    protected TickJobQueue jobs = new TickJobQueue();

    public RaidScheduler(World w, Village v) {
        this.world = w;
        this.village = v;
        this.setupJobs();
    }

    private void setupJobs() {
        this.jobs.addJob(new TickJob(3000, 1200, true, () -> this.checkForRaid()));
    }

    public void forceRaid(int raidPoints) {
        this.forceRaid = raidPoints;
    }

    private void checkForRaid() {
        if (this.village.isValid() && this.world.func_175659_aa().func_151525_a() > 0) {
            int residentCount = this.village.getResidentCount();
            if (residentCount < 8 || this.forceFail) {
                return;
            }
            int chance = Math.min(residentCount / 10, 5);
            if (Village.isNightTime(this.world)) {
                chance += 3;
            }
            if (this.world.field_73012_v.nextInt(180) < chance) {
                EntityItemFrame frame;
                VillageStructure townHall = this.village.getNearestStructure(VillageStructureType.TOWNHALL, this.village.getOrigin());
                if (townHall != null && (frame = townHall.getItemFrame()) != null && frame.func_174866_q() != 1) {
                    this.village.debugOut("Necromancer raid blocked by town hall marker rotation.");
                    return;
                }
                if (this.world.func_72872_a(EntityNecromancer.class, new AxisAlignedBB(this.village.getOrigin()).func_186662_g(240.0)).isEmpty()) {
                    this.village.debugOut("========RAID SPAWN ======== Village( " + this.village.getOrigin() + " )");
                    int raidPoints = this.calcRaidPoints();
                    this.spawnRaid(raidPoints);
                } else {
                    this.village.debugOut("========RAID Failed. Necro already in Village ======== Village( " + this.village.getOrigin() + " )");
                }
            }
        }
    }

    private int calcRaidPoints() {
        int residentCount = this.village.getResidentCount();
        int difficultyFactor = (int)((double)residentCount * 0.15 * (double)this.world.func_175659_aa().func_151525_a());
        int guassianFactor = (int)(this.world.field_73012_v.nextGaussian() * (double)(residentCount / 5));
        int rp = residentCount + difficultyFactor + guassianFactor;
        this.village.debugOut("================================================================");
        this.village.debugOut("================================================================");
        this.village.debugOut("================================================================");
        this.village.debugOut("================================================================");
        this.village.debugOut("Village Raid Points [" + rp + "] (Population: " + residentCount + ") + (Difficulty: " + difficultyFactor + ") + (Random: " + guassianFactor + ")");
        this.village.debugOut("================================================================");
        this.village.debugOut("================================================================");
        this.village.debugOut("================================================================");
        this.village.debugOut("================================================================");
        return rp;
    }

    private void spawnRaid(int raidPoints) {
        BlockPos spawnPos = this.village.getEdgeNode();
        if (spawnPos != null) {
            EntityNecromancer necro = new EntityNecromancer(this.world);
            necro.func_70012_b(spawnPos.func_177958_n(), spawnPos.func_177956_o(), spawnPos.func_177952_p(), 0.0f, 0.0f);
            necro.func_180482_a(this.world.func_175649_E(spawnPos), null);
            necro.func_110163_bv();
            necro.setLevel(raidPoints / 10);
            this.village.playEvent(ModSoundEvents.deathSummon, (ITextComponent)new TextComponentString("A Necromancer approaches the village!"));
            int extraZombies = raidPoints / 10;
            int witherSkel = Math.min((raidPoints - 50) / 10, 12);
            for (int w = 0; w < witherSkel; ++w) {
                this.world.func_72838_d((Entity)necro.createWitherSkeleton(necro.func_180425_c()));
                extraZombies -= 2;
            }
            this.village.debugOut("    Spawned " + witherSkel + " extra wither skeletons");
            extraZombies = Math.min(extraZombies, 10);
            for (int i = 0; i < extraZombies; ++i) {
                EntityZombie zombie = necro.createZombie(necro.func_180425_c());
                this.world.func_72838_d((Entity)zombie);
            }
            this.world.func_72838_d((Entity)necro);
            this.village.debugOut("    Spawned " + extraZombies + " extra zombies");
            this.village.debugOut("    Spawned Necromancer level: " + necro.getLevel());
        } else {
            this.village.debugOut("Could not find a spawn location for raid.   Village( " + this.village.getOrigin() + " )");
        }
        this.forceFail = true;
        this.jobs.addJob(new TickJob(12000, 0, false, () -> {
            this.forceFail = false;
        }));
    }

    public void update() {
        this.jobs.tick();
        if (this.forceRaid >= 0) {
            int rp = this.forceRaid;
            if (this.forceRaid == 0) {
                rp = this.calcRaidPoints();
            }
            this.spawnRaid(rp);
            this.forceRaid = -1;
        }
    }
}

