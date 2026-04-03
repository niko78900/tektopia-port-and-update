/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAreaEffectCloud
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.tangotek.tektopia.entities;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityCaptainAura
extends EntityAreaEffectCloud {
    private float radius = 3.0f;
    private final float RADIUS_PER_TICK = 0.5f;

    public EntityCaptainAura(World worldIn) {
        super(worldIn);
    }

    public EntityCaptainAura(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.func_184491_a(EnumParticleTypes.FIREWORKS_SPARK);
        this.func_184483_a(this.radius);
        this.func_184487_c(0.5f);
    }

    public void func_70071_h_() {
        this.radius += 0.5f;
        if (this.field_70170_p.field_72995_K) {
            for (int i = 0; i < 10; ++i) {
                this.perimeterParticle();
            }
        } else {
            if (this.field_70173_aa >= this.func_184489_o()) {
                this.func_70106_y();
                return;
            }
            double radiusSq = this.radius * this.radius;
            List villagerList = this.field_70170_p.func_72872_a(EntityVillagerTek.class, this.func_174813_aQ());
            villagerList.stream().filter(g -> g.func_70068_e((Entity)this) < radiusSq).forEach(g -> g.func_70690_d(new PotionEffect(MobEffects.field_76429_m, 100)));
        }
    }

    @SideOnly(value=Side.CLIENT)
    private void perimeterParticle() {
        double motionY = Math.random() * 0.01 + 0.01;
        float f1 = this.field_70170_p.field_73012_v.nextFloat() * ((float)Math.PI * 2);
        float xOffset = MathHelper.func_76134_b((float)f1) * this.radius;
        float zOffset = MathHelper.func_76126_a((float)f1) * this.radius;
        Vec3d pos = new Vec3d(this.field_70165_t + (double)xOffset, this.field_70163_u, this.field_70161_v + (double)zOffset);
        this.field_70170_p.func_175688_a(EnumParticleTypes.FIREWORKS_SPARK, pos.field_72450_a, pos.field_72448_b, pos.field_72449_c, 0.0, motionY, 0.0, new int[0]);
    }
}

