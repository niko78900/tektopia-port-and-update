/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.pathfinding.Path
 */
package net.tangotek.tektopia.entities.ai;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.pathfinding.Path;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.entities.EntityNecromancer;
import net.tangotek.tektopia.entities.EntitySpiritSkull;

public class EntityAISoulLink
extends EntityAIBase {
    private EntityNecromancer necro;
    private EntitySpiritSkull skull;
    private EntityCreature target;
    private int abilityTime = 0;
    private final int ABILITY_LENGTH = 22;

    public EntityAISoulLink(EntityNecromancer v) {
        this.necro = v;
        this.func_75248_a(1);
    }

    public boolean func_75250_a() {
        this.target = null;
        if (this.necro.isAITick() && this.necro.isReadyForSkull()) {
            Path path;
            List animals = this.necro.field_70170_p.func_72872_a(EntityAnimal.class, this.necro.func_174813_aQ().func_186662_g(20.0));
            this.target = animals.stream().filter(a -> this.isCreatureInRange((EntityCreature)a) && !this.necro.isCreatureSkulled((EntityCreature)a)).findAny().orElse(null);
            if (this.target == null) {
                List mobs = this.necro.field_70170_p.func_72872_a(EntityMob.class, this.necro.func_174813_aQ().func_186662_g(20.0));
                Collections.shuffle(mobs);
                this.target = mobs.stream().filter(m -> {
                    if (!this.isCreatureInRange((EntityCreature)m)) return false;
                    if (!m.func_70089_S()) return false;
                    if (!EntityNecromancer.isMinion((EntityLivingBase)m)) return false;
                    if (this.necro.isCreatureSkulled((EntityCreature)m)) return false;
                    return true;
                }).findAny().orElse(null);
            }
            if (this.target != null && ((path = this.necro.func_70661_as().func_179680_a(this.target.func_180425_c())) == null || path.func_75874_d() <= 1)) {
                this.target = null;
            }
        }
        return this.target != null;
    }

    private boolean isCreatureInRange(EntityCreature c) {
        return c.func_70068_e((Entity)this.necro) < 400.0;
    }

    public void func_75249_e() {
        this.startAbility();
    }

    public boolean func_75253_b() {
        return this.abilityTime > 0;
    }

    public boolean func_75252_g() {
        return false;
    }

    public void func_75246_d() {
        if (this.abilityTime > 0) {
            --this.abilityTime;
            if (this.abilityTime == 12) {
                this.spawnSkull();
            }
        }
        this.necro.func_70671_ap().func_75651_a((Entity)this.target, 30.0f, 30.0f);
        super.func_75246_d();
    }

    private void startAbility() {
        this.abilityTime = 22;
        this.necro.func_70661_as().func_75499_g();
        this.necro.playServerAnimation("necro_siphon");
        this.necro.func_184185_a(ModSoundEvents.deathSkullLeave, this.necro.field_70170_p.field_73012_v.nextFloat() * 0.4f + 1.2f, this.necro.field_70170_p.field_73012_v.nextFloat() * 0.4f + 0.8f);
        this.necro.setSpellTarget((Entity)this.target);
    }

    private void stopAbility() {
        this.necro.stopServerAnimation("necro_siphon");
        this.necro.setSpellTarget(null);
    }

    private void spawnSkull() {
        this.skull = new EntitySpiritSkull(this.necro.field_70170_p);
        this.skull.func_70012_b(this.target.field_70165_t, this.target.field_70163_u + (double)this.target.func_70047_e(), this.target.field_70161_v, this.target.field_70177_z, 0.0f);
        this.necro.field_70170_p.func_72838_d((Entity)this.skull);
        this.skull.setSkullCreature(this.target);
        this.necro.addSkull(this.skull);
        EntityNecromancer.makeMinion((EntityLivingBase)this.target, this.necro);
    }

    public void func_75251_c() {
        this.stopAbility();
        this.abilityTime = 0;
    }
}

