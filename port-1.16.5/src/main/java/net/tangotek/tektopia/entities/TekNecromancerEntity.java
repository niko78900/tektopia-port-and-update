package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageRuntime;

public class TekNecromancerEntity extends MonsterEntity {
    private static final String NEXT_SUMMON_TAG = "tek_next_summon";
    private static final String MINION_OWNER_TAG = "tek_necromancer_owner";
    private static final String MINION_SPAWNED_TAG = "tek_necromancer_spawned";
    private static final long SUMMON_COOLDOWN = 240L;
    private static final long MINION_CLEANUP_TICKS = 2400L;
    private static final int MAX_OWNED_MINIONS = 5;

    public TekNecromancerEntity(EntityType<? extends TekNecromancerEntity> type, World level) {
        super(type, level);
        this.xpReward = 18;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 0.85D));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TekGuardEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide() || !(this.level instanceof ServerWorld) || !this.isAlive()) {
            return;
        }
        long gameTime = this.level.getGameTime();
        ServerWorld serverWorld = (ServerWorld) this.level;
        if (gameTime % 80L == 0L) {
            this.moveTowardNearestVillage(serverWorld);
            this.cleanupOwnedMinions(serverWorld, gameTime);
        }
        CompoundNBT data = this.getPersistentData();
        if (gameTime < data.getLong(NEXT_SUMMON_TAG)) {
            return;
        }
        data.putLong(NEXT_SUMMON_TAG, gameTime + SUMMON_COOLDOWN);
        this.summonMinion(serverWorld, gameTime);
    }

    private void summonMinion(ServerWorld level, long gameTime) {
        int nearbyMinions = level.getEntitiesOfClass(
                ZombieEntity.class,
                new AxisAlignedBB(this.blockPosition()).inflate(16.0D),
                this::isOwnedMinion
        ).size();
        if (nearbyMinions >= MAX_OWNED_MINIONS) {
            return;
        }
        ZombieEntity zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) {
            return;
        }
        BlockPos pos = this.blockPosition().offset(
                this.random.nextInt(7) - 3,
                0,
                this.random.nextInt(7) - 3
        );
        zombie.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.random.nextFloat() * 360.0F, 0.0F);
        zombie.getPersistentData().putUUID(MINION_OWNER_TAG, this.getUUID());
        zombie.getPersistentData().putLong(MINION_SPAWNED_TAG, gameTime);
        TekGuardEntity targetGuard = level.getEntitiesOfClass(
                TekGuardEntity.class,
                new AxisAlignedBB(this.blockPosition()).inflate(32.0D, 8.0D, 32.0D),
                guard -> guard != null && guard.isAlive()
        ).stream().min(java.util.Comparator.comparingDouble(guard -> guard.distanceToSqr(this))).orElse(null);
        if (targetGuard != null) {
            zombie.setTarget(targetGuard);
        }
        level.addFreshEntity(zombie);
    }

    private void moveTowardNearestVillage(ServerWorld level) {
        TekVillage village = TekVillageRuntime.get().villageManagerFor(level).findNearestVillage(this.blockPosition()).orElse(null);
        if (village == null || this.distanceToSqr(village.getCenter().getX(), village.getCenter().getY(), village.getCenter().getZ()) < 64.0D) {
            return;
        }
        BlockPos center = village.getCenter();
        this.getNavigation().moveTo(center.getX() + 0.5D, center.getY(), center.getZ() + 0.5D, 0.9D);
    }

    private void cleanupOwnedMinions(ServerWorld level, long gameTime) {
        for (ZombieEntity zombie : level.getEntitiesOfClass(
                ZombieEntity.class,
                new AxisAlignedBB(this.blockPosition()).inflate(48.0D, 16.0D, 48.0D),
                this::isOwnedMinion
        )) {
            long spawnedAt = zombie.getPersistentData().getLong(MINION_SPAWNED_TAG);
            if (spawnedAt > 0L && gameTime - spawnedAt > MINION_CLEANUP_TICKS) {
                zombie.remove();
            }
        }
    }

    private boolean isOwnedMinion(ZombieEntity zombie) {
        if (zombie == null || !zombie.isAlive() || !zombie.getPersistentData().hasUUID(MINION_OWNER_TAG)) {
            return false;
        }
        return this.getUUID().equals(zombie.getPersistentData().getUUID(MINION_OWNER_TAG));
    }
}
