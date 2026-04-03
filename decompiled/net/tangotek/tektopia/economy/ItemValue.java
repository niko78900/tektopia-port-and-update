/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.MathHelper
 */
package net.tangotek.tektopia.economy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ProfessionType;

public class ItemValue {
    private final ItemStack itemStack;
    private final int baseValue;
    private final int appearanceWeight;
    private int purchaseCount;
    private float markDown;
    private float markUp;
    private final ProfessionType requiredProfession;
    private final float PURCHASE_MIN_REDUCTION = 0.1f;
    private final float PURCHASE_MAX_REDUCTION = 0.3f;

    public ItemValue(ItemStack itemStack, int baseValue, int appearanceWeight, ProfessionType reqProf) {
        this.itemStack = ModItems.makeTaggedItem(itemStack, ItemTagType.VILLAGER);
        this.baseValue = baseValue;
        this.appearanceWeight = appearanceWeight;
        this.requiredProfession = reqProf;
        this.reset();
    }

    public String getName() {
        return ItemValue.getName(this.itemStack);
    }

    public static String getName(ItemStack stack) {
        return stack.func_77973_b().func_77658_a() + "_" + stack.func_77960_j();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ProfessionType getRequiredProfession() {
        return this.requiredProfession;
    }

    public int getBaseValue() {
        return this.baseValue;
    }

    public int getPurchaseCount() {
        return this.purchaseCount;
    }

    public float getAppearanceWeight() {
        return this.appearanceWeight * this.getCurrentValue() / this.getBaseValue();
    }

    public int getCurrentValue() {
        return Math.max(1, (int)((float)this.baseValue - this.markDown + this.markUp));
    }

    public void reset() {
        this.purchaseCount = 0;
        this.markDown = 0.0f;
        this.markUp = 0.0f;
    }

    public float markDown(float ageLerp) {
        float reduction = (float)MathHelper.func_151238_b((double)0.1f, (double)0.3f, (double)ageLerp) * (float)this.getCurrentValue();
        this.markDown += reduction;
        ++this.purchaseCount;
        return reduction;
    }

    public boolean isForSale() {
        return this.purchaseCount < 8;
    }

    public void markUp(float value) {
        this.markUp += value;
    }

    public String toString() {
        String result = "[" + this.getName() + "]  pc: " + this.purchaseCount + "   markDown: " + this.markDown + "    markUp: " + this.markUp + "    price: " + this.getCurrentValue() + "(" + this.getBaseValue() + ")";
        return result;
    }
}

