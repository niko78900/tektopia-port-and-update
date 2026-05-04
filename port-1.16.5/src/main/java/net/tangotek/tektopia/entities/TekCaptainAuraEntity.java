package net.tangotek.tektopia.entities;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class TekCaptainAuraEntity extends MonsterEntity {
    private static final String CREATED_TICK_TAG = "tek_created_tick";
    private static final long MAX_AGE_TICKS = 240L;

    public TekCaptainAuraEntity(EntityType<? extends TekCaptainAuraEntity> type, World level) {
        super(type, level);
        this.setNoAi(true);
        this.noPhysics = true;
        this.xpReward = 0;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
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
        if (this.tickCount % 40 == 0) {
            AxisAlignedBB bounds = new AxisAlignedBB(this.blockPosition()).inflate(12.0D, 4.0D, 12.0D);
            List<TekGuardEntity> guards = this.level.getEntitiesOfClass(TekGuardEntity.class, bounds, guard -> guard != null && guard.isAlive());
            for (TekGuardEntity guard : guards) {
                guard.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 120, 0, true, true));
                guard.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 120, 0, true, true));
            }
        }
        if (this.level.getGameTime() - data.getLong(CREATED_TICK_TAG) > MAX_AGE_TICKS) {
            this.remove();
        }
    }
}
