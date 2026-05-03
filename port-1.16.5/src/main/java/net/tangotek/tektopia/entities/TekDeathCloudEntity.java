package net.tangotek.tektopia.entities;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class TekDeathCloudEntity extends MonsterEntity {
    private static final String CREATED_TICK_TAG = "tek_created_tick";
    private static final long MAX_AGE_TICKS = 220L;

    public TekDeathCloudEntity(EntityType<? extends TekDeathCloudEntity> type, World level) {
        super(type, level);
        this.setNoAi(true);
        this.noPhysics = true;
        this.xpReward = 0;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D);
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
        if (this.tickCount % 20 == 0) {
            AxisAlignedBB bounds = new AxisAlignedBB(this.blockPosition()).inflate(3.5D, 1.5D, 3.5D);
            List<LivingEntity> targets = this.level.getEntitiesOfClass(
                    LivingEntity.class,
                    bounds,
                    entity -> entity instanceof TekVillagerEntity || entity instanceof PlayerEntity
            );
            for (LivingEntity target : targets) {
                target.hurt(DamageSource.MAGIC, 2.0F);
            }
        }
        if (this.level.getGameTime() - data.getLong(CREATED_TICK_TAG) > MAX_AGE_TICKS) {
            this.remove();
        }
    }
}
