/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.leviathanstudio.craftstudio.client.registry.CraftStudioLoader
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCrops
 *  net.minecraft.block.BlockDirt
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockStone
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.command.ICommand
 *  net.minecraft.entity.player.EntityPlayer$SleepResult
 *  net.minecraft.item.Item
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionType
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.GameRules$ValueType
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.structure.StructureBoundingBox
 *  net.minecraft.world.gen.structure.StructureComponent
 *  net.minecraft.world.storage.loot.LootTableList
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.event.RegistryEvent$Register
 *  net.minecraftforge.event.entity.living.BabyEntitySpawnEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.event.entity.player.PlayerSleepInBedEvent
 *  net.minecraftforge.event.terraingen.PopulateChunkEvent$Post
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent$Post
 *  net.minecraftforge.event.world.BlockEvent$HarvestDropsEvent
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.SidedProxy
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLServerStartingEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 *  net.minecraftforge.fml.common.network.IGuiHandler
 *  net.minecraftforge.fml.common.network.NetworkRegistry
 *  net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.registries.IForgeRegistry
 */
package net.tangotek.tektopia;

import com.leviathanstudio.craftstudio.client.registry.CraftStudioLoader;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.tangotek.tektopia.ModBlocks;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.ModGuiHandler;
import net.tangotek.tektopia.ModItems;
import net.tangotek.tektopia.ModPotions;
import net.tangotek.tektopia.ModSoundEvents;
import net.tangotek.tektopia.TekDataSerializers;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.commands.VillageCommands;
import net.tangotek.tektopia.generation.TekStructureVillagePieces;
import net.tangotek.tektopia.proxy.CommonProxy;
import net.tangotek.tektopia.structures.VillageStructureType;

@Mod(modid="tektopia", name="TekTopia", version="1.1.0", useMetadata=true, updateJSON="https://raw.githubusercontent.com/TangoTek/tektopia-public/master/update.json", acceptedMinecraftVersions="[1.12.2]")
public class TekVillager {
    public static final String MODID = "tektopia";
    public static final String NAME = "TekTopia";
    public static final SimpleNetworkWrapper NETWORK;
    public static final ResourceLocation VILLAGE_STORAGE;
    @Mod.Instance(value="tektopia")
    public static TekVillager instance;
    @SidedProxy(serverSide="net.tangotek.tektopia.proxy.ServerProxy", clientSide="net.tangotek.tektopia.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static <T extends IAnimated> AnimationHandler<T> getNewAnimationHandler(Class<T> animatedClass) {
        return proxy.getNewAnimationHandler(animatedClass);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("TekTopia is loading!");
        proxy.preInit(event);
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)this, (IGuiHandler)new ModGuiHandler());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent evt) {
        VillageCommands vc = new VillageCommands();
        evt.registerServerCommand((ICommand)vc);
        vc.registerNodes();
        World world = evt.getServer().func_130014_f_();
        world.func_82736_K().func_180262_a("villagerItems", "false", GameRules.ValueType.BOOLEAN_VALUE);
        world.func_82736_K().func_180262_a("villagerSkillRate", "100", GameRules.ValueType.NUMERICAL_VALUE);
        world.func_82736_K().func_180262_a("villageRadius", "100", GameRules.ValueType.NUMERICAL_VALUE);
        world.func_82736_K().func_180262_a("villagerPenPercent", "100", GameRules.ValueType.NUMERICAL_VALUE);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    static {
        DataSerializers.func_187189_a(TekDataSerializers.INT_LIST);
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        VILLAGE_STORAGE = LootTableList.func_186375_a((ResourceLocation)new ResourceLocation(MODID, "village_storage"));
    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {
        static Map<Long, Set<VillageStructureType>> villageStructs = new HashMap<Long, Set<VillageStructureType>>();

        @SubscribeEvent
        public static void worldTick(TickEvent.WorldTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                VillageManager.get(event.world).update();
                ModEntities.onWorldTick(event);
            }
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            ModBlocks.register((IForgeRegistry<Block>)event.getRegistry());
        }

        @SubscribeEvent
        public static void onChunkPopulate(PopulateChunkEvent.Post event) {
            if (event.isHasVillageGenerated()) {
                int i = event.getChunkX() * 16 + 8;
                int k = event.getChunkZ() * 16 + 8;
                int chunkX = event.getChunkX() * 16;
                int chunkZ = event.getChunkZ() * 16;
                TekStructureVillagePieces.replaceVillagers(event.getWorld(), event.getRand(), chunkX, 0.0, chunkZ, chunkX + 25, 255.0, chunkZ + 25);
            }
        }

        private static void setStructureBlock(StructureComponent comp, World world, IBlockState blockState, int x, int y, int z, StructureBoundingBox bbox) {
            try {
                Method setBlockStateMethod = StructureComponent.class.getDeclaredMethod("setBlockState", World.class, IBlockState.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, StructureBoundingBox.class);
                setBlockStateMethod.setAccessible(true);
                setBlockStateMethod.invoke((Object)comp, world, blockState, x, y, z, bbox);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        private static boolean isNaturalBlock(IBlockState blockState) {
            return blockState.func_177230_c() instanceof BlockStone || blockState.func_177230_c() instanceof BlockDirt || blockState.func_177230_c() instanceof BlockSand;
        }

        @SubscribeEvent
        public static void onPlayerSleepEvent(PlayerSleepInBedEvent event) {
            if (!VillageManager.get(event.getEntityPlayer().func_130014_f_()).canSleepAt(event.getPos())) {
                event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_HERE);
            }
        }

        @SubscribeEvent
        public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            ModEntities.onPlayerLoggedIn(event);
        }

        @SubscribeEvent
        @SideOnly(value=Side.CLIENT)
        public static void onItemToolTip(ItemTooltipEvent event) {
            ModItems.onItemToolTip(event);
        }

        @SubscribeEvent
        public static void onPlayerCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        }

        @SubscribeEvent
        public static void onCropGrowEvent(BlockEvent.CropGrowEvent.Post event) {
            BlockCrops cropBlock;
            if (event.getState().func_177230_c() instanceof BlockCrops && (cropBlock = (BlockCrops)event.getState().func_177230_c()).func_185525_y(event.getState())) {
                VillageManager.get(event.getWorld()).onCropGrowEvent((BlockEvent.CropGrowEvent)event);
            }
        }

        @SubscribeEvent
        public static void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
            ModBlocks.onHarvestDropsEvent(event);
        }

        @SubscribeEvent
        public static void onBabySpawnEvent(BabyEntitySpawnEvent event) {
            ModEntities.onBabySpawnEvent(event);
        }

        @SubscribeEvent
        public static void onLivingDropsEvent(LivingDropsEvent event) {
            ModEntities.onLivingDropsEvent(event);
        }

        @SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event) {
            ModPotions.registerPotions((IForgeRegistry<Potion>)event.getRegistry());
        }

        @SubscribeEvent
        public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
            ModPotions.registerPotionTypes((IForgeRegistry<PotionType>)event.getRegistry());
        }

        @SubscribeEvent
        public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
            ModSoundEvents.registerSounds((IForgeRegistry<SoundEvent>)event.getRegistry());
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ModItems.register((IForgeRegistry<Item>)event.getRegistry());
            ModBlocks.registerItemBlocks((IForgeRegistry<Item>)event.getRegistry());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            ModItems.registerModels();
            ModBlocks.registerModels();
        }

        @CraftStudioLoader
        public static void loadStuff() {
            proxy.registerModels();
            proxy.registerAnims();
        }
    }
}

