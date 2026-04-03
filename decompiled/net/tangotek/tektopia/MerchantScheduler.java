/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityChicken
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.tangotek.tektopia;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.entities.EntityMerchant;

public class MerchantScheduler {
    protected final World world;
    protected final Village village;
    private boolean dayReset = false;
    private List<String> pendingMerchantOrders = new ArrayList<String>();

    public MerchantScheduler(World w, Village v) {
        this.world = w;
        this.village = v;
    }

    private boolean spawnCheck() {
        return true;
    }

    public void addOrder(String order) {
        this.pendingMerchantOrders.add(order);
    }

    public void update() {
        IVillageData vd = this.village.getTownData();
        if (vd != null && this.village.isValid() && !Village.isNightTime(this.world)) {
            this.dayReset = false;
            if (!vd.getMerchantCheckedToday()) {
                vd.setMerchantCheckedToday(true);
                if (this.spawnCheck()) {
                    BlockPos pos = this.village.getEdgeNode();
                    if (pos != null) {
                        List merchants = this.world.func_72872_a(EntityMerchant.class, this.village.getAABB().func_186662_g(120.0));
                        merchants.forEach(m -> m.func_70106_y());
                        EntityMerchant merchant = new EntityMerchant(this.world);
                        merchant.func_70012_b((double)pos.func_177958_n() + 0.5, pos.func_177956_o(), (double)pos.func_177952_p() + 0.5, 0.0f, 0.0f);
                        merchant.func_180482_a(this.world.func_175649_E(pos), null);
                        this.world.func_72838_d((Entity)merchant);
                        this.village.debugOut("Spawning merchant at " + pos);
                        for (String order : this.pendingMerchantOrders) {
                            Object animal = null;
                            switch (order) {
                                case "Cow": {
                                    merchant.addAnimalDelivery((EntityAnimal)new EntityCow(this.world));
                                    break;
                                }
                                case "Sheep": {
                                    merchant.addAnimalDelivery((EntityAnimal)new EntitySheep(this.world));
                                    break;
                                }
                                case "Pig": {
                                    merchant.addAnimalDelivery((EntityAnimal)new EntityPig(this.world));
                                    break;
                                }
                                case "Chicken": {
                                    merchant.addAnimalDelivery((EntityAnimal)new EntityChicken(this.world));
                                }
                            }
                        }
                    } else {
                        this.village.sendChatMessage("Could not find a spawn location for travelling Merchant.");
                    }
                }
            }
        }
    }

    public void resetDay() {
        if (!this.dayReset) {
            IVillageData vd = this.village.getTownData();
            if (vd != null && this.village.isValid()) {
                vd.setMerchantCheckedToday(false);
            }
            this.dayReset = true;
        }
    }
}

