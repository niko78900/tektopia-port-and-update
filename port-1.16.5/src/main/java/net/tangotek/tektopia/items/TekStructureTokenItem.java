package net.tangotek.tektopia.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.common.TekItemMeta;

public class TekStructureTokenItem extends Item {
    public TekStructureTokenItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || TekItemMeta.isStructureTokenValidated(stack);
    }
}
