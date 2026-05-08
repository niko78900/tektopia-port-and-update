package net.tangotek.tektopia.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.RegistryObject;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.client.animation.TekCraftStudioRenderer;
import net.tangotek.tektopia.client.animation.TekVisualAssetInventory;
import net.tangotek.tektopia.client.gui.TekVillagerScreen;
import net.tangotek.tektopia.registry.TekContainers;
import net.tangotek.tektopia.registry.TekEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.ScreenManager;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TekClientEvents {
    public static KeyBinding STATUS_KEY;
    private static final ResourceLocation VILLAGER_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/villager/villager.png");
    private static final ResourceLocation ZOMBIE_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/zombie/zombie.png");
    private static final ResourceLocation GUARD_MODEL =
            new ResourceLocation(TekTopiaPort.MODID, "craftstudio/models/entity/guard_m.csjsmodel");
    private static final ResourceLocation GUARD_TEXTURE =
            new ResourceLocation(TekTopiaPort.MODID, "textures/entity/guard_m.png");

    private TekClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            STATUS_KEY = new KeyBinding("key.tektopia.status", GLFW.GLFW_KEY_O, "key.categories.tektopia");
            ClientRegistry.registerKeyBinding(STATUS_KEY);
            ScreenManager.register(TekContainers.TEK_VILLAGER.get(), TekVillagerScreen::new);
            TekVisualAssetInventory.logClientCoverage();
            registerCraft(TekEntities.TEK_GUARD, "guard_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_FARMER, "tek_farmer", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_BLACKSMITH, "tek_blacksmith", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_MINER, "tek_miner", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_LUMBERJACK, "tek_lumberjack", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_CHEF, "tek_chef", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_RANCHER, "tek_rancher", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_BUTCHER, "tek_butcher", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_MERCHANT, "tek_merchant", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_NOMAD, "tek_nomad", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_NECROMANCER, "tek_necromancer", ZOMBIE_TEXTURE);
            registerCraft(TekEntities.TEK_ARCHITECT, "tek_architect", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_TRADESMAN, "tek_tradesman", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_CHILD, "tek_child", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_TEACHER, "tek_teacher", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_BARD, "tek_bard", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_CLERIC, "tek_cleric", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_DRUID, "tek_druid", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_ENCHANTER, "tek_enchanter", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_NITWIT, "tek_nitwit", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_SPIRIT_SKULL, "tek_spirit_skull", ZOMBIE_TEXTURE);
            registerCraft(TekEntities.TEK_DEATH_CLOUD, "tek_death_cloud", ZOMBIE_TEXTURE);
            registerCraft(TekEntities.TEK_CAPTAIN_AURA, "tek_captain_aura", VILLAGER_TEXTURE);
        });
    }

    private static <T extends MobEntity> void registerCraft(
            RegistryObject<EntityType<T>> type,
            String assetName,
            ResourceLocation fallbackTexture
    ) {
        RenderingRegistry.registerEntityRenderingHandler(
                type.get(),
                manager -> new TekCraftStudioRenderer<>(manager, model(assetName), texture(assetName), fallbackTexture)
        );
    }

    private static ResourceLocation model(String assetName) {
        return new ResourceLocation(TekTopiaPort.MODID, "craftstudio/models/entity/" + assetName + ".csjsmodel");
    }

    private static ResourceLocation texture(String assetName) {
        return new ResourceLocation(TekTopiaPort.MODID, "textures/entity/" + assetName + ".png");
    }
}
