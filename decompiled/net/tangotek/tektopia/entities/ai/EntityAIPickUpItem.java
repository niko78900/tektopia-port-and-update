/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia.entities.ai;

import com.google.common.base.Predicate;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIMoveToBlock;

public class EntityAIPickUpItem
extends EntityAIMoveToBlock {
    protected final EntityVillagerTek villager;
    private List<PickUpData> pickUps;
    protected EntityItem pickUpItem;
    private final int chance;

    public EntityAIPickUpItem(EntityVillagerTek v, List<PickUpData> pickUpCounts, int chance) {
        super(v);
        this.villager = v;
        this.pickUps = pickUpCounts;
        this.chance = chance;
    }

    @Override
    public boolean func_75250_a() {
        if (this.villager.isAITick() && this.navigator.hasVillage() && this.villager.isWorkTime() && this.villager.func_70681_au().nextInt(this.chance) == 0) {
            return super.func_75250_a();
        }
        return false;
    }

    @Override
    protected BlockPos getDestinationBlock() {
        List entityItems = this.villager.field_70170_p.func_175647_a(EntityItem.class, this.villager.func_174813_aQ().func_72314_b(20.0, 10.0, 20.0), this.shouldPickUp(this.villager));
        this.pickUpItem = entityItems.stream().min(Comparator.comparing(e -> e.func_70068_e((Entity)this.villager))).orElse(null);
        if (this.pickUpItem != null) {
            return this.pickUpItem.func_180425_c();
        }
        return null;
    }

    private Predicate<EntityItem> shouldPickUp(EntityVillagerTek villager) {
        return e -> {
            if (this.villager.getVillage().getPathingGraph().isInGraph(e.func_180425_c())) {
                for (PickUpData pickUp : this.pickUps) {
                    int itemCount;
                    if (!villager.isAIFilterEnabled(pickUp.aiFilter) || !e.func_92059_d().func_77969_a(pickUp.itemStack) || (itemCount = villager.getInventory().getItemCount(i -> e.func_92059_d().func_77969_a(i))) >= pickUp.quantity) continue;
                    return true;
                }
            }
            return false;
        };
    }

    @Override
    protected BlockPos findWalkPos() {
        return this.destinationPos;
    }

    @Override
    public void func_75249_e() {
        super.func_75249_e();
    }

    @Override
    public boolean func_75253_b() {
        if (this.pickUpItem.func_70089_S()) {
            return super.func_75253_b();
        }
        return false;
    }

    @Override
    void updateMovementMode() {
        this.villager.setMovementMode(this.villager.getDefaultMovement());
    }

    @Override
    protected void onArrival() {
        this.villager.pickupItems(4);
        super.onArrival();
    }

    @Override
    public void func_75251_c() {
        this.pickUpItem = null;
        super.func_75251_c();
    }

    public static class PickUpData {
        public final int quantity;
        public final ItemStack itemStack;
        public final String aiFilter;

        public PickUpData(ItemStack itemStack, int quantity, String aiFilter) {
            this.itemStack = itemStack;
            this.quantity = quantity;
            this.aiFilter = aiFilter;
        }
    }
}

