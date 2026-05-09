package net.tangotek.tektopia.common;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.village.TekVillage;

public final class TekItemMeta {
    private static final String ROOT_TAG = "village";
    private static final String VILLAGER_ITEM_TAG = "villager";
    private static final String STRUCTURE_TOKEN_TAG = "struct";
    private static final String VILLAGE_ID_TAG = "villageId";
    private static final String VILLAGE_CENTER_TAG = "villageCenter";

    private TekItemMeta() {
    }

    public static ItemStack markVillagerItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.getOrCreateTagElement(ROOT_TAG).putBoolean(VILLAGER_ITEM_TAG, true);
        }
        return stack;
    }

    public static boolean isVillagerItem(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(ROOT_TAG);
        return tag != null && tag.getBoolean(VILLAGER_ITEM_TAG);
    }

    public static ItemStack markStructureTokenValidated(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.getOrCreateTagElement(ROOT_TAG).putBoolean(STRUCTURE_TOKEN_TAG, true);
        }
        return stack;
    }

    public static ItemStack clearStructureTokenValidated(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(ROOT_TAG);
        if (tag != null) {
            tag.remove(STRUCTURE_TOKEN_TAG);
        }
        return stack;
    }

    public static boolean isStructureTokenValidated(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(ROOT_TAG);
        return tag != null && tag.getBoolean(STRUCTURE_TOKEN_TAG);
    }

    public static ItemStack bindToVillage(ItemStack stack, TekVillage village) {
        if (!stack.isEmpty() && village != null) {
            CompoundNBT tag = stack.getOrCreateTagElement(ROOT_TAG);
            tag.putUUID(VILLAGE_ID_TAG, village.getId());
            tag.putLong(VILLAGE_CENTER_TAG, village.getCenter().asLong());
        }
        return stack;
    }

    public static boolean isBoundToVillage(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(ROOT_TAG);
        return tag != null && tag.hasUUID(VILLAGE_ID_TAG);
    }

    @Nullable
    public static UUID getBoundVillageId(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement(ROOT_TAG);
        if (tag == null || !tag.hasUUID(VILLAGE_ID_TAG)) {
            return null;
        }
        return tag.getUUID(VILLAGE_ID_TAG);
    }

    public static boolean canUseInVillage(ItemStack stack, TekVillage village) {
        UUID boundVillage = getBoundVillageId(stack);
        return boundVillage == null || village != null && boundVillage.equals(village.getId());
    }

    public static String describeVillageBinding(ItemStack stack) {
        UUID boundVillage = getBoundVillageId(stack);
        if (boundVillage == null) {
            return "unbound";
        }
        CompoundNBT tag = stack.getTagElement(ROOT_TAG);
        String center = tag != null && tag.contains(VILLAGE_CENTER_TAG, 4)
                ? BlockPos.of(tag.getLong(VILLAGE_CENTER_TAG)).toShortString()
                : "unknown";
        return boundVillage + " center=" + center;
    }
}
