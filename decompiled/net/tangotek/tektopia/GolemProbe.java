package net.tangotek.tektopia;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureType;

@Mod.EventBusSubscriber(modid="tektopia")
public class GolemProbe {
    private static final double IRON_VIEW_RANGE = 72.0;
    private static final double SNOW_VIEW_RANGE = 64.0;
    private static final float SNOW_DAMAGE = 3.0f;
    private static final float IRON_RETREAT_PCT = 0.35f;
    private static final float SNOW_RETREAT_PCT = 0.45f;
    private static Field villageEnemiesField = null;
    private static Field villageEnemyEntityField = null;
    private static boolean villageEnemyReflectionInit = false;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Object worldObj = event.world;
        if (!(worldObj instanceof World)) {
            return;
        }
        World world = (World)worldObj;
        if (world.field_72995_K || (world.func_82737_E() & 1L) != 0L) {
            return;
        }
        VillageManager vm = VillageManager.get(world);
        ArrayList<Village> villages = new ArrayList<Village>(vm.villages);
        for (Village village : villages) {
            AxisAlignedBB scanArea;
            if (village == null || !village.isLoaded() || village.getOrigin() == null || (scanArea = GolemProbe.getScanArea(village)) == null) continue;
            List<EntityIronGolem> ironGolems = world.func_72872_a(EntityIronGolem.class, scanArea);
            for (EntityIronGolem golem : ironGolems) {
                GolemProbe.controlIronGolem(world, village, golem);
            }
            List<EntitySnowman> snowGolems = world.func_72872_a(EntitySnowman.class, scanArea);
            for (EntitySnowman golem : snowGolems) {
                GolemProbe.controlSnowGolem(world, village, golem);
            }
            GolemProbe.applySnowballDamage(world, scanArea);
        }
    }

    private static AxisAlignedBB getScanArea(Village village) {
        if (village.getAABB() != null) {
            return village.getAABB().func_186662_g(24.0);
        }
        if (village.getOrigin() != null) {
            return new AxisAlignedBB(village.getOrigin()).func_186662_g(140.0);
        }
        return null;
    }

    private static void controlIronGolem(World world, Village village, EntityIronGolem golem) {
        if (golem == null || !golem.func_70089_S()) {
            return;
        }
        GolemProbe.ensureViewRange((EntityCreature)golem, IRON_VIEW_RANGE);
        EntityLivingBase target = GolemProbe.findVillageTarget(village, (EntityCreature)golem);
        if (target != null) {
            golem.func_70624_b(target);
            golem.func_70661_as().func_75497_a((Entity)target, 1.05);
            return;
        }
        GolemProbe.clearInvalidTarget((EntityCreature)golem);
        if (GolemProbe.shouldRetreat((EntityLivingBase)golem, IRON_RETREAT_PCT) || GolemProbe.shouldReturnToTownHall(world, (EntityCreature)golem)) {
            golem.func_70624_b((EntityLivingBase)null);
            GolemProbe.moveToTownHall((EntityCreature)golem, village, 1.15);
        }
    }

    private static void controlSnowGolem(World world, Village village, EntitySnowman golem) {
        if (golem == null || !golem.func_70089_S()) {
            return;
        }
        GolemProbe.ensureViewRange((EntityCreature)golem, SNOW_VIEW_RANGE);
        GolemProbe.applyRainImmunity(world, golem);
        GolemProbe.clearSnowTrail(world, golem);
        EntityLivingBase target = GolemProbe.findVillageTarget(village, (EntityCreature)golem);
        if (target != null) {
            golem.func_70624_b(target);
            if (golem.func_70068_e((Entity)target) > 196.0) {
                golem.func_70661_as().func_75497_a((Entity)target, 1.0);
            } else {
                golem.func_70661_as().func_75499_g();
            }
            return;
        }
        GolemProbe.clearInvalidTarget((EntityCreature)golem);
        if (GolemProbe.shouldRetreat((EntityLivingBase)golem, SNOW_RETREAT_PCT) || GolemProbe.shouldReturnToTownHall(world, (EntityCreature)golem)) {
            golem.func_70624_b((EntityLivingBase)null);
            GolemProbe.moveToTownHall((EntityCreature)golem, village, 1.2);
        }
    }

    private static void ensureViewRange(EntityCreature golem, double viewRange) {
        IAttributeInstance followRange = golem.func_110148_a(SharedMonsterAttributes.field_111265_b);
        if (followRange != null && followRange.func_111126_e() < viewRange) {
            followRange.func_111128_a(viewRange);
        }
    }

    private static EntityLivingBase findVillageTarget(Village village, EntityCreature golem) {
        List<?> entries = GolemProbe.getVillageEnemyEntries(village);
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        double closest = Double.MAX_VALUE;
        boolean foundMinion = false;
        EntityLivingBase target = null;
        for (Object enemyEntry : entries) {
            EntityLivingBase enemy = GolemProbe.getVillageEnemyFromEntry(enemyEntry);
            if (enemy == null || !enemy.func_70089_S()) continue;
            boolean isMinion = EntityNecromancer.isMinion(enemy);
            if (isMinion && !foundMinion) {
                closest = Double.MAX_VALUE;
                target = null;
            }
            if (!isMinion && foundMinion) continue;
            double dist = enemy.func_70068_e((Entity)golem);
            if (dist < closest) {
                closest = dist;
                target = enemy;
            }
            foundMinion = isMinion;
        }
        if (target == null || !GolemProbe.isHostile(target)) {
            return null;
        }
        Path path = golem.func_70661_as().func_179680_a(target.func_180425_c());
        if (path != null || golem.func_70068_e((Entity)target) < 64.0) {
            return target;
        }
        return null;
    }

    private static List<?> getVillageEnemyEntries(Village village) {
        GolemProbe.initVillageEnemyReflection();
        if (villageEnemiesField == null) {
            return null;
        }
        try {
            return (List)villageEnemiesField.get(village);
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    private static EntityLivingBase getVillageEnemyFromEntry(Object enemyEntry) {
        GolemProbe.initVillageEnemyReflection();
        if (enemyEntry == null || villageEnemyEntityField == null) {
            return null;
        }
        try {
            Object value = villageEnemyEntityField.get(enemyEntry);
            if (value instanceof EntityLivingBase) {
                return (EntityLivingBase)value;
            }
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    private static void initVillageEnemyReflection() {
        if (villageEnemyReflectionInit) {
            return;
        }
        villageEnemyReflectionInit = true;
        try {
            villageEnemiesField = Village.class.getDeclaredField("enemies");
            villageEnemiesField.setAccessible(true);
            Class<?>[] nested = Village.class.getDeclaredClasses();
            for (Class<?> c : nested) {
                if (!"VillageEnemy".equals(c.getSimpleName())) continue;
                villageEnemyEntityField = c.getDeclaredField("enemy");
                villageEnemyEntityField.setAccessible(true);
                break;
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            villageEnemiesField = null;
            villageEnemyEntityField = null;
        }
    }

    private static boolean isHostile(EntityLivingBase target) {
        return target != null && (target instanceof EntityZombie && !(target instanceof EntityPigZombie) || target instanceof EntityWitherSkeleton || target instanceof EntityEvoker || target instanceof EntityVex || target instanceof EntityVindicator || target instanceof EntityNecromancer || EntityNecromancer.isMinion(target));
    }

    private static void clearInvalidTarget(EntityCreature golem) {
        EntityLivingBase currentTarget = golem.func_70638_az();
        if (currentTarget != null && !GolemProbe.isHostile(currentTarget)) {
            golem.func_70624_b((EntityLivingBase)null);
        }
    }

    private static boolean shouldRetreat(EntityLivingBase golem, float healthPct) {
        float maxHealth = golem.func_110138_aP();
        if (maxHealth <= 0.0f) {
            return false;
        }
        return golem.func_110143_aJ() / maxHealth <= healthPct;
    }

    private static boolean shouldReturnToTownHall(World world, EntityCreature golem) {
        return !Village.isNightTime(world) && golem.func_70638_az() == null;
    }

    private static void moveToTownHall(EntityCreature golem, Village village, double speed) {
        BlockPos townHallPos = GolemProbe.getTownHallPos(village);
        if (townHallPos == null || golem.func_174818_b(townHallPos) <= 100.0) {
            return;
        }
        golem.func_70661_as().func_75492_a((double)townHallPos.func_177958_n() + 0.5, (double)townHallPos.func_177956_o(), (double)townHallPos.func_177952_p() + 0.5, speed);
    }

    private static BlockPos getTownHallPos(Village village) {
        VillageStructure townHall;
        if (village == null || village.getOrigin() == null) {
            return null;
        }
        if ((townHall = village.getNearestStructure(VillageStructureType.TOWNHALL, village.getOrigin())) != null) {
            return townHall.getDoorOutside(2);
        }
        return village.getOrigin();
    }

    private static void applyRainImmunity(World world, EntitySnowman golem) {
        float health;
        float maxHealth;
        BlockPos rainPos;
        if (!world.func_72896_J() || golem.func_70090_H() || !world.func_175678_i(rainPos = new BlockPos(golem.field_70165_t, golem.field_70163_u + 1.0, golem.field_70161_v)) || (health = golem.func_110143_aJ()) >= (maxHealth = golem.func_110138_aP()) || maxHealth <= 0.0f) {
            return;
        }
        golem.func_70606_j(Math.min(maxHealth, health + 2.2f));
    }

    private static void clearSnowTrail(World world, EntitySnowman golem) {
        for (int i = 0; i < 4; ++i) {
            int x = MathHelper.func_76128_c((double)(golem.field_70165_t + (double)(((float)(i % 2 * 2 - 1)) * 0.25f)));
            int z = MathHelper.func_76128_c((double)(golem.field_70161_v + (double)(((float)(i / 2 % 2 * 2 - 1)) * 0.25f)));
            int y = MathHelper.func_76128_c((double)golem.field_70163_u);
            GolemProbe.clearSnowAt(world, new BlockPos(x, y, z));
            GolemProbe.clearSnowAt(world, new BlockPos(x, y - 1, z));
        }
    }

    private static void clearSnowAt(World world, BlockPos pos) {
        if (world.func_180495_p(pos).func_177230_c() == Blocks.field_150431_aC) {
            world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
        }
    }

    private static void applySnowballDamage(World world, AxisAlignedBB scanArea) {
        List<EntitySnowball> snowballs = world.func_72872_a(EntitySnowball.class, scanArea);
        for (EntitySnowball snowball : snowballs) {
            if (!snowball.func_70089_S()) continue;
            Entity thrower = snowball.func_85052_h();
            if (!(thrower instanceof EntitySnowman) || !thrower.func_70089_S()) continue;
            List<EntityLivingBase> impacted = world.func_175647_a(EntityLivingBase.class, snowball.func_174813_aQ().func_72314_b(0.65, 0.65, 0.65), e -> GolemProbe.canSnowballDamage((EntityLivingBase)e));
            if (impacted.isEmpty()) continue;
            EntityLivingBase target = impacted.stream().min((a, b) -> Double.compare(a.func_70068_e((Entity)snowball), b.func_70068_e((Entity)snowball))).orElse(null);
            if (target == null) continue;
            target.func_70097_a(DamageSource.func_76356_a((Entity)snowball, thrower), SNOW_DAMAGE);
            world.func_72960_a((Entity)snowball, (byte)3);
            snowball.func_70106_y();
        }
    }

    private static boolean canSnowballDamage(EntityLivingBase target) {
        if (target == null || target instanceof EntityVillagerTek || target instanceof EntityVillager) {
            return false;
        }
        return GolemProbe.isHostile(target);
    }
}
