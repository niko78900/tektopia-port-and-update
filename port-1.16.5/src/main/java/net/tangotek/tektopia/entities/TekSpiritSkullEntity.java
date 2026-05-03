package net.tangotek.tektopia.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class TekSpiritSkullEntity extends MonsterEntity {
    private static final String CREATED_TICK_TAG = "tek_created_tick";
    private static final long MAX_AGE_TICKS = 400L;

    public TekSpiritSkullEntity(EntityType<? extends TekSpiritSkullEntity> type, World level) {
        super(type, level);
        this.xpReward = 4;
        this.setNoGravity(true);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.25D, false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TekGuardEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TekVillagerEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            return;
        }
        CompoundNBT data = this.getPersistentData();
        if (!data.contains(CREATED_TICK_TAG)) {
            data.putLong(CREATED_TICK_TAG, this.level.getGameTime());
        }
        if (this.level.getGameTime() - data.getLong(CREATED_TICK_TAG) > MAX_AGE_TICKS) {
            this.remove();
        }
    }
}
