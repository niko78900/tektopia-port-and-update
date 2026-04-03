/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.event.world.BlockEvent$HarvestDropsEvent
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package net.tangotek.tektopia;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.blocks.BlockChair;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class ModBlocks {
    public static BlockPos villagerBlockBreak;
    public static EntityVillagerTek villagerBreaker;
    public static BlockChair blockChair;

    public static void register(IForgeRegistry<Block> registry) {
        System.out.println("Registering Blocks");
        registry.registerAll((IForgeRegistryEntry[])new Block[]{blockChair});
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll((IForgeRegistryEntry[])new Item[]{blockChair.createItemBlock()});
    }

    public static void registerModels() {
        blockChair.registerItemModel(Item.func_150898_a((Block)blockChair));
    }

    public static void villagerDestroyBlock(BlockPos pos, EntityVillagerTek villager, boolean dropBlock, boolean makeVillagerItem) {
        if (dropBlock && makeVillagerItem) {
            villagerBlockBreak = pos;
            villagerBreaker = villager;
        }
        villager.field_70170_p.func_175655_b(pos, dropBlock);
    }

    public static void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() == null && event.getPos() == villagerBlockBreak) {
            for (ItemStack itemStack : event.getDrops()) {
                ModItems.makeTaggedItem(itemStack, ItemTagType.VILLAGER);
            }
            villagerBlockBreak = null;
        }
    }

    static {
        blockChair = new BlockChair("chair");
    }
}

