/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package net.tangotek.tektopia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tangotek.tektopia.ItemTagType;
import net.tangotek.tektopia.ProfessionType;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.caps.IVillageData;
import net.tangotek.tektopia.entities.EntityBard;
import net.tangotek.tektopia.entities.EntityBlacksmith;
import net.tangotek.tektopia.entities.EntityButcher;
import net.tangotek.tektopia.entities.EntityChef;
import net.tangotek.tektopia.entities.EntityChild;
import net.tangotek.tektopia.entities.EntityCleric;
import net.tangotek.tektopia.entities.EntityDruid;
import net.tangotek.tektopia.entities.EntityEnchanter;
import net.tangotek.tektopia.entities.EntityFarmer;
import net.tangotek.tektopia.entities.EntityGuard;
import net.tangotek.tektopia.entities.EntityLumberjack;
import net.tangotek.tektopia.entities.EntityMiner;
import net.tangotek.tektopia.entities.EntityNitwit;
import net.tangotek.tektopia.entities.EntityNomad;
import net.tangotek.tektopia.entities.EntityRancher;
import net.tangotek.tektopia.entities.EntityTeacher;
import net.tangotek.tektopia.items.ItemBeer;
import net.tangotek.tektopia.items.ItemHammer;
import net.tangotek.tektopia.items.ItemHeart;
import net.tangotek.tektopia.items.ItemProfessionToken;
import net.tangotek.tektopia.items.ItemRancherHat;
import net.tangotek.tektopia.items.ItemStructureToken;
import net.tangotek.tektopia.structures.VillageStructureType;

public class ModItems {
    public static ItemHammer ironHammer = new ItemHammer(Item.ToolMaterial.IRON, "iron_hammer");
    public static ItemBeer beer = new ItemBeer("beer");
    public static ItemHeart heart = new ItemHeart("heart");
    public static ItemRancherHat rancherHat = new ItemRancherHat("rancher_hat");
    public static ItemStructureToken structureTavern = new ItemStructureToken("structure_tavern", 45);
    public static ItemStructureToken structureTownHall = new ItemStructureToken("structure_townhall", 0);
    public static ItemStructureToken structureSchool = new ItemStructureToken("structure_school", 35);
    public static ItemStructureToken structureStorage = new ItemStructureToken("structure_storage", 0);
    public static ItemStructureToken structureSheepPen = new ItemStructureToken("structure_sheeppen", 18);
    public static ItemStructureToken structureMineshaft = new ItemStructureToken("structure_mineshaft", 3);
    public static ItemStructureToken structurePigPen = new ItemStructureToken("structure_pigpen", 18);
    public static ItemStructureToken structureHome2 = new ItemStructureToken("structure_home2", 6);
    public static ItemStructureToken structureHome4 = new ItemStructureToken("structure_home4", 13);
    public static ItemStructureToken structureHome6 = new ItemStructureToken("structure_home6", 20);
    public static ItemStructureToken structureCowPen = new ItemStructureToken("structure_cowpen", 20);
    public static ItemStructureToken structureLibrary = new ItemStructureToken("structure_library", 30);
    public static ItemStructureToken structureChickenCoop = new ItemStructureToken("structure_chickencoop", 16);
    public static ItemStructureToken structureButcher = new ItemStructureToken("structure_butcher", 25);
    public static ItemStructureToken structureBlacksmith = new ItemStructureToken("structure_blacksmith", 30);
    public static ItemStructureToken structureGuardPost = new ItemStructureToken("structure_guard_post", 2);
    public static ItemStructureToken structureMerchantStall = new ItemStructureToken("structure_merchant_stall", 20);
    public static ItemStructureToken structureKitchen = new ItemStructureToken("structure_kitchen", 18);
    public static ItemStructureToken structureBarracks = new ItemStructureToken("structure_barracks", 35);
    public static ItemProfessionToken itemBard = new ItemProfessionToken("prof_bard", ProfessionType.BARD, (w, v) -> new EntityBard((World)w), 15);
    public static ItemProfessionToken itemBlacksmith = new ItemProfessionToken("prof_blacksmith", ProfessionType.BLACKSMITH, (w, v) -> new EntityBlacksmith((World)w), 5);
    public static ItemProfessionToken itemButcher = new ItemProfessionToken("prof_butcher", ProfessionType.BUTCHER, (w, v) -> new EntityButcher((World)w), 4);
    public static ItemProfessionToken itemChef = new ItemProfessionToken("prof_chef", ProfessionType.CHEF, (w, v) -> new EntityChef((World)w), 7);
    public static ItemProfessionToken itemCleric = new ItemProfessionToken("prof_cleric", ProfessionType.CLERIC, (w, v) -> new EntityCleric((World)w), 12);
    public static ItemProfessionToken itemDruid = new ItemProfessionToken("prof_druid", ProfessionType.DRUID, (w, v) -> new EntityDruid((World)w), 12);
    public static ItemProfessionToken itemEnchanter = new ItemProfessionToken("prof_enchanter", ProfessionType.ENCHANTER, (w, v) -> new EntityEnchanter((World)w), 15);
    public static ItemProfessionToken itemFarmer = new ItemProfessionToken("prof_farmer", ProfessionType.FARMER, (w, v) -> new EntityFarmer((World)w), 3);
    public static ItemProfessionToken itemGuard = new ItemProfessionToken("prof_guard", ProfessionType.GUARD, (w, v) -> new EntityGuard((World)w), 5);
    public static ItemProfessionToken itemLumberjack = new ItemProfessionToken("prof_lumberjack", ProfessionType.LUMBERJACK, (w, v) -> new EntityLumberjack((World)w), 3);
    public static ItemProfessionToken itemMiner = new ItemProfessionToken("prof_miner", ProfessionType.MINER, (w, v) -> new EntityMiner((World)w), 4);
    public static ItemProfessionToken itemRancher = new ItemProfessionToken("prof_rancher", ProfessionType.RANCHER, (w, v) -> new EntityRancher((World)w), 4);
    public static ItemProfessionToken itemTeacher = new ItemProfessionToken("prof_teacher", ProfessionType.TEACHER, (w, v) -> new EntityTeacher((World)w), 8);
    public static ItemProfessionToken itemChild = new ItemProfessionToken("prof_child", ProfessionType.CHILD, (w, v) -> new EntityChild((World)w), 0);
    public static ItemProfessionToken itemNitWit = new ItemProfessionToken("prof_nitwit", ProfessionType.NITWIT, (w, v) -> new EntityNitwit((World)w), 0);
    public static ItemProfessionToken itemNomad = new ItemProfessionToken("prof_nomad", ProfessionType.NOMAD, (w, v) -> new EntityNomad((World)w), 0);
    public static ItemProfessionToken itemCaptain = new ItemProfessionToken("prof_captain", ProfessionType.CAPTAIN, (w, v) -> {
        EntityGuard guard;
        if (v instanceof EntityGuard && v.hasVillage() && !(guard = (EntityGuard)((Object)v)).isCaptain()) {
            List otherGuards = w.func_72872_a(EntityGuard.class, v.getVillage().getAABB().func_186662_g(50.0));
            otherGuards.stream().filter(g -> g.isCaptain() && g.getVillage() == v.getVillage()).forEach(g -> g.setCaptain(false));
            guard.setCaptain(true);
            guard.modifyHappy(100);
            guard.func_184185_a(SoundEvents.field_193784_dd, 1.0f, 1.0f);
        }
        return null;
    }, 3){

        @Override
        public int getCost(Village v) {
            if (v.getStructures(VillageStructureType.BARRACKS).isEmpty()) {
                return 0;
            }
            return super.getCost(v);
        }
    };
    public static ItemStructureToken[] structureTokens = new ItemStructureToken[]{structureTavern, structureTownHall, structureSchool, structureStorage, structureSheepPen, structureMineshaft, structurePigPen, structureHome2, structureHome4, structureHome6, structureCowPen, structureLibrary, structureChickenCoop, structureButcher, structureBlacksmith, structureGuardPost, structureMerchantStall, structureKitchen, structureBarracks};
    public static Map<ProfessionType, ItemProfessionToken> professionTokens = new HashMap<ProfessionType, ItemProfessionToken>();
    public static ItemStack EMPTY_HAND_ITEM;

    private static void addProfessionToken(ItemProfessionToken token) {
        professionTokens.put(token.getProfessionType(), token);
    }

    public static void register(IForgeRegistry<Item> registry) {
        System.out.println("Registering Items");
        registry.registerAll((IForgeRegistryEntry[])new Item[]{ironHammer, beer, heart, rancherHat});
        professionTokens.values().forEach(pt -> registry.register((IForgeRegistryEntry)pt));
        registry.registerAll((IForgeRegistryEntry[])structureTokens);
    }

    public static void registerModels() {
        ironHammer.registerItemModel();
        beer.registerItemModel();
        heart.registerItemModel();
        rancherHat.registerItemModel();
        for (ItemStructureToken token : structureTokens) {
            token.registerItemModel();
        }
        professionTokens.values().forEach(pt -> pt.registerItemModel());
    }

    public static ItemProfessionToken getProfessionToken(ProfessionType pt) {
        return professionTokens.get((Object)pt);
    }

    public static String getSkullAnimal(ItemStack skull) {
        if (skull.func_77942_o()) {
            return skull.func_77978_p().func_74779_i("SkullName");
        }
        return "";
    }

    private static boolean isAllVillager(IInventory inv) {
        for (int i = 0; i < inv.func_70302_i_(); ++i) {
            ItemStack itemStack = inv.func_70301_a(i);
            if (itemStack.func_190926_b() || ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER)) continue;
            return false;
        }
        return true;
    }

    public static boolean isCompressionCraft(PlayerEvent.ItemCraftedEvent event) {
        Item item = event.crafting.func_77973_b();
        return item == Items.field_151042_j || item == Items.field_151137_ax || item == Items.field_151166_bC || item == Items.field_151015_O || item == Items.field_151043_k || item == Item.func_150898_a((Block)Blocks.field_150475_bE) || item == Item.func_150898_a((Block)Blocks.field_150339_S) || item == Item.func_150898_a((Block)Blocks.field_150451_bX) || item == Item.func_150898_a((Block)Blocks.field_150340_R) || item == Item.func_150898_a((Block)Blocks.field_150407_cf);
    }

    public static void onPlayerCraftedEvent(PlayerEvent.ItemCraftedEvent event) {
        if (ModItems.isAllVillager(event.craftMatrix) && ModItems.isCompressionCraft(event)) {
            ModItems.makeTaggedItem(event.crafting, ItemTagType.VILLAGER);
        }
    }

    public static ItemStack makeTaggedItem(ItemStack itemStack, ItemTagType tag, String displayName) {
        return ModItems.makeTaggedItemInternal(itemStack, tag, displayName);
    }

    public static ItemStack makeTaggedItem(ItemStack itemStack, ItemTagType tag) {
        return ModItems.makeTaggedItemInternal(itemStack, tag, itemStack.func_82833_r());
    }

    public static ItemStack createTaggedItem(Item item, int qty, ItemTagType tag) {
        ItemStack itemStack = new ItemStack(item, qty);
        return ModItems.makeTaggedItemInternal(itemStack, tag, itemStack.func_82833_r());
    }

    public static ItemStack createTaggedItem(Item item, ItemTagType tag) {
        return ModItems.createTaggedItem(item, 1, tag);
    }

    public static ItemStack untagItem(ItemStack itemStack, ItemTagType tag) {
        if (ModItems.isTaggedItem(itemStack, tag)) {
            itemStack.func_190925_c("village").func_74757_a(tag.tag, false);
        }
        return itemStack;
    }

    private static ItemStack makeTaggedItemInternal(ItemStack itemStack, ItemTagType tag, String displayName) {
        if (!ModItems.isTaggedItem(itemStack, tag)) {
            itemStack.func_190925_c("village").func_74757_a(tag.tag, true);
        }
        return itemStack;
    }

    public static boolean isVillagerItemsEnabled() {
        return false;
    }

    public static boolean isTaggedItem(ItemStack itemStack, ItemTagType tag) {
        NBTTagCompound tagCompound = itemStack.func_179543_a("village");
        if (tagCompound != null) {
            return tagCompound.func_74767_n(tag.tag);
        }
        return false;
    }

    public static boolean canVillagerSee(ItemStack itemStack) {
        return !ModItems.isVillagerItemsEnabled() || ModItems.isTaggedItem(itemStack, ItemTagType.VILLAGER);
    }

    public static void bindItemToVillage(ItemStack itemStack, Village v) {
        itemStack.func_190925_c("village").func_186854_a("villageId", v.getTownData().getUUID());
    }

    public static boolean isItemVillageBound(ItemStack itemStack, Village v) {
        IVillageData townData = v.getTownData();
        if (itemStack.func_190925_c("village").func_186855_b("villageId") && townData != null) {
            return itemStack.func_190925_c("village").func_186857_a("villageId").equals(townData.getUUID());
        }
        return false;
    }

    public static boolean isItemVillageBound(ItemStack itemStack) {
        return itemStack.func_190925_c("village").func_186855_b("villageId");
    }

    public static void onItemToolTip(ItemTooltipEvent event) {
        if (ModItems.isTaggedItem(event.getItemStack(), ItemTagType.VILLAGER) && !event.getToolTip().isEmpty()) {
            event.getToolTip().set(0, TextFormatting.GREEN + (String)event.getToolTip().get(0));
        }
    }

    static {
        ModItems.addProfessionToken(itemBard);
        ModItems.addProfessionToken(itemBlacksmith);
        ModItems.addProfessionToken(itemButcher);
        ModItems.addProfessionToken(itemChef);
        ModItems.addProfessionToken(itemCleric);
        ModItems.addProfessionToken(itemDruid);
        ModItems.addProfessionToken(itemEnchanter);
        ModItems.addProfessionToken(itemFarmer);
        ModItems.addProfessionToken(itemGuard);
        ModItems.addProfessionToken(itemLumberjack);
        ModItems.addProfessionToken(itemMiner);
        ModItems.addProfessionToken(itemRancher);
        ModItems.addProfessionToken(itemTeacher);
        ModItems.addProfessionToken(itemChild);
        ModItems.addProfessionToken(itemNitWit);
        ModItems.addProfessionToken(itemNomad);
        ModItems.addProfessionToken(itemCaptain);
        EMPTY_HAND_ITEM = new ItemStack(Items.field_151086_cn);
    }
}

