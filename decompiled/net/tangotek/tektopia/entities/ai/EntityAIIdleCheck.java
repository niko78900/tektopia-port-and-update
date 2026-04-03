/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 */
package net.tangotek.tektopia.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityAIIdleCheck
extends EntityAIBase {
    protected final EntityVillagerTek villager;
    private int idleTicks = 0;

    public EntityAIIdleCheck(EntityVillagerTek v) {
        this.villager = v;
        this.func_75248_a(7);
    }

    public boolean func_75250_a() {
        return this.villager.isAITick() && this.villager.hasVillage();
    }

    public void func_75249_e() {
        this.idleTicks = 0;
    }

    public boolean func_75253_b() {
        return true;
    }

    public void func_75246_d() {
        ++this.idleTicks;
        if (this.idleTicks % 80 == 0) {
            this.villager.setStoragePriority();
        }
        this.villager.setIdle(this.idleTicks);
        if (this.idleTicks % 1200 == 0) {
            this.villager.debugOut("Idle for " + this.idleTicks / 20 + " seconds");
        }
    }

    public void func_75251_c() {
        this.villager.setIdle(0);
        if (this.idleTicks >= 1200) {
            this.villager.debugOut(" was idle for " + this.idleTicks / 20 + " seconds.");
        }
    }
}

