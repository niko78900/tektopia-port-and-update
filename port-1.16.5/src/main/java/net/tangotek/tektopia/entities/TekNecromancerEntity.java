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

public class TekNecromancerEntity extends MonsterEntity {
    private static final String NEXT_SUMMON_TAG = "tek_next_summon";
    private static final long SUMMON_COOLDOWN = 240L;

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
        CompoundNBT data = this.getPersistentData();
        if (gameTime < data.getLong(NEXT_SUMMON_TAG)) {
            return;
        }
        data.putLong(NEXT_SUMMON_TAG, gameTime + SUMMON_COOLDOWN);
        this.summonMinion((ServerWorld) this.level);
    }

    private void summonMinion(ServerWorld level) {
        int nearbyMinions = level.getEntitiesOfClass(
                ZombieEntity.class,
                new AxisAlignedBB(this.blockPosition()).inflate(16.0D),
                zombie -> zombie != null && zombie.isAlive()
        ).size();
        if (nearbyMinions >= 3) {
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
        level.addFreshEntity(zombie);
    }
}
