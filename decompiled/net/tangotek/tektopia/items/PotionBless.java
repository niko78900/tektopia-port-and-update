/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.potion.Potion
 */
package net.tangotek.tektopia.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class PotionBless
extends Potion {
    public PotionBless(int color) {
        super(false, color);
        this.setRegistryName("tektopia", "village_bless");
        this.func_76390_b("effect.village_bless.name");
    }

    public boolean func_76397_a(int duration, int amplifier) {
        return duration % 400 == 0;
    }

    public void func_76394_a(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof EntityVillagerTek) {
            EntityVillagerTek villager = (EntityVillagerTek)entityLivingBaseIn;
            villager.modifyHappy(1);
        }
    }
}

