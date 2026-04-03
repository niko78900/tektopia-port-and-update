/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.FMLCommonHandler
 */
package net.tangotek.tektopia.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.tangotek.tektopia.EntityTagType;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.pathing.BasePathingNode;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureFencedArea;
import net.tangotek.tektopia.structures.VillageStructureType;
import net.tangotek.tektopia.tickjob.TickJob;

public abstract class VillageStructureRancherPen
extends VillageStructureFencedArea {
    private int glowCheck = 0;
    private int gateOpen = 0;
    protected int animalCount = 0;
    protected final int animalSize;
    private List<Map<EntityAnimal, Integer>> animalSchedule = new ArrayList<Map<EntityAnimal, Integer>>();

    protected VillageStructureRancherPen(World world, Village v, EntityItemFrame itemFrame, VillageStructureType structType, int animalSize, String name) {
        super(world, v, itemFrame, structType, name);
        this.animalSize = animalSize;
        for (int i = 0; i < AnimalScheduleType.values().length; ++i) {
            this.animalSchedule.add(new HashMap());
        }
    }

    @Override
    protected void setupServerJobs() {
        this.addJob(new TickJob(80, 120, true, () -> this.updateAnimals()));
        this.addJob(new TickJob(15, 15, true, () -> this.closeGate()));
        super.setupServerJobs();
    }

    public abstract EntityAnimal spawnAnimal(BlockPos var1);

    public abstract Class getAnimalClass();

    public abstract EntityVillagerTek.VillagerThought getNoFoodThought();

    public EntityVillagerTek.VillagerThought getNoHarvestThought() {
        return EntityVillagerTek.VillagerThought.CHICKEN_FOOD;
    }

    public BlockPos getSpawnLocation(World world) {
        BlockPos spawnPos = this.door.func_177967_a(this.signFacing, -2);
        if (BasePathingNode.isPassable(world, spawnPos)) {
            return spawnPos;
        }
        spawnPos = this.door.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), 1);
        if (BasePathingNode.isPassable(world, spawnPos)) {
            return spawnPos;
        }
        spawnPos = this.door.func_177967_a(this.signFacing, -1).func_177967_a(this.signFacing.func_176746_e(), -1);
        if (BasePathingNode.isPassable(world, spawnPos)) {
            return spawnPos;
        }
        return null;
    }

    @Override
    public void update() {
        super.update();
        --this.glowCheck;
        if (this.glowCheck < 0) {
            this.glowAnimals();
            this.glowCheck = 40;
        }
    }

    private void glowAnimals() {
        if (!this.world.func_72872_a(EntityPlayer.class, this.getAABB()).isEmpty()) {
            List<EntityAnimal> animals = this.getEntitiesInside(this.getAnimalClass());
            animals.forEach(a -> {
                if (ModEntities.isTaggedEntity((Entity)a, EntityTagType.VILLAGER)) {
                    a.func_70690_d(new PotionEffect(MobEffects.field_188423_x, 60));
                }
            });
        }
    }

    protected void updateAnimals() {
        List<EntityAnimal> animals = this.getEntitiesInside(this.getAnimalClass());
        this.animalCount = animals.size();
        animals.forEach(o -> this.updateAnimal((EntityAnimal)o));
        this.updateAnimalSchedule();
    }

    protected void closeGate() {
        if (VillageStructure.isGate(this.world, this.door)) {
            IBlockState state = this.world.func_180495_p(this.door);
            if (((Boolean)state.func_177229_b((IProperty)BlockFenceGate.field_176466_a)).booleanValue()) {
                ++this.gateOpen;
                if (this.gateOpen > 4) {
                    state = state.func_177226_a((IProperty)BlockFenceGate.field_176466_a, (Comparable)Boolean.valueOf(false));
                    this.world.func_180501_a(this.door, state, 10);
                    this.world.func_180498_a((EntityPlayer)null, (Boolean)state.func_177229_b((IProperty)BlockFenceGate.field_176466_a) != false ? 1008 : 1014, this.door, 0);
                    this.gateOpen = 0;
                }
            } else {
                this.gateOpen = 0;
            }
        }
    }

    protected void updateAnimal(EntityAnimal animal) {
        if (!Village.isNightTime(this.world)) {
            int hungerChance = 20;
            if (animal.func_70681_au().nextInt(hungerChance) == 0) {
                int newHunger = ModEntities.modifyAnimalHunger(animal, -3 - this.world.field_73012_v.nextInt(4));
                if (newHunger < 40) {
                    this.getVillage().debugOut(this.getAnimalClass().getSimpleName() + " hunger -> " + newHunger);
                }
                if (ModEntities.isAnimalStarving(animal)) {
                    animal.func_70097_a(DamageSource.field_76366_f, 1.0f);
                    if (!animal.func_70089_S()) {
                        this.village.sendChatMessage("A " + animal.func_145748_c_().func_150260_c() + " has died of starvation.");
                    }
                }
            }
        }
    }

    private void updateAnimalSchedule() {
        for (Map<EntityAnimal, Integer> map : this.animalSchedule) {
            Iterator<Map.Entry<EntityAnimal, Integer>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<EntityAnimal, Integer> entry = itr.next();
                if (!entry.getKey().func_70089_S()) {
                    itr.remove();
                    continue;
                }
                if (entry.getValue() >= 3) {
                    itr.remove();
                    continue;
                }
                entry.setValue(entry.getValue() + 1);
            }
        }
    }

    public void scheduleAnimal(EntityAnimal animal, AnimalScheduleType scheduleType) {
        if (!this.isAnimalScheduled(animal, scheduleType)) {
            this.animalSchedule.get(scheduleType.index()).put(animal, 0);
        }
    }

    public void clearAnimalSchedule(EntityAnimal animal, AnimalScheduleType scheduleType) {
        this.animalSchedule.get(scheduleType.index()).remove(animal);
    }

    public boolean isAnimalScheduled(EntityAnimal animal, AnimalScheduleType scheduleType) {
        return this.animalSchedule.get(scheduleType.index()).containsKey(animal);
    }

    public boolean isAnimalHome(EntityAnimal animal) {
        return this.getAnimalClass().isInstance(animal);
    }

    public boolean isPenFull(float percent) {
        int pct = FMLCommonHandler.instance().getMinecraftServerInstance().func_71218_a(0).func_82736_K().func_180263_c("villagerPenPercent");
        int goalCount = (int)((float)(this.floorTiles.size() * pct) * percent / (float)(this.getAnimalSize() * 100));
        return this.animalCount > 2 && this.animalCount >= goalCount;
    }

    public int getAnimalSize() {
        return this.animalSize;
    }

    public int getFeedCost(EntityVillagerTek villager) {
        return 3;
    }

    public static enum AnimalScheduleType {
        FEED(0),
        BUTCHER(1),
        HARVEST(2);

        private final int index;

        private AnimalScheduleType(int idx) {
            this.index = idx;
        }

        public int index() {
            return this.index;
        }
    }
}

