/*
 * Decompiled with CFR 0.152.
 */
package net.tangotek.tektopia.entities.ai;

import java.util.function.Predicate;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.entities.EntityVillageNavigator;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIWanderStructure;

public class EntityAITavernVisit
extends EntityAIWanderStructure {
    public EntityAITavernVisit(EntityVillagerTek v, Predicate<EntityVillagerTek> shouldPred) {
        super(v, EntityVillagerTek.findLocalTavern(), shouldPred, 0);
    }

    @Override
    public boolean func_75250_a() {
        return super.func_75250_a() && this.villager.isAIFilterEnabled("visit_tavern");
    }

    @Override
    public boolean func_75253_b() {
        if (this.villager.isWorkTime() || this.villager.shouldSleep() || this.villager.getHappy() >= this.villager.getMaxHappy()) {
            return false;
        }
        return super.func_75253_b();
    }

    @Override
    public void func_75246_d() {
        super.func_75246_d();
        if (this.hasArrived()) {
            if (this.villager.isSitting()) {
                if (this.villager.func_70681_au().nextInt(300) == 0) {
                    this.villager.modifyHappy(1);
                    if (this.villager.field_70170_p.func_72872_a(EntityVillageNavigator.class, this.villager.func_174813_aQ().func_186662_g(8.0)).size() > 1) {
                        this.villager.func_184185_a(ModSoundEvents.villagerSocialize, 0.4f + this.villager.func_70681_au().nextFloat() * 0.7f, this.villager.func_70681_au().nextFloat() * 0.4f + 0.8f);
                    }
                    if (this.villager.func_70681_au().nextInt(5) == 0) {
                        this.villager.cheerBeer(2);
                    }
                }
            } else if (this.villager.func_70681_au().nextInt(400) == 0) {
                this.villager.modifyHappy(1);
            }
        }
    }
}

