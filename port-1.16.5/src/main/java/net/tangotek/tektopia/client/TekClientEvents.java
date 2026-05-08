package net.tangotek.tektopia.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.client.animation.TekCraftStudioRenderer;
import net.tangotek.tektopia.client.animation.TekVisualAssetInventory;
import net.tangotek.tektopia.client.gui.TekVillagerScreen;
import net.tangotek.tektopia.registry.TekContainers;
import net.tangotek.tektopia.registry.TekEntities;
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
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_GUARD.get(),
                    manager -> new TekCraftStudioRenderer<>(manager, GUARD_MODEL, GUARD_TEXTURE, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_FARMER.get(),
                    TekFarmerRenderer::new
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_BLACKSMITH.get(),
                    TekBlacksmithRenderer::new
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_MINER.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_LUMBERJACK.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_CHEF.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_RANCHER.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_BUTCHER.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_MERCHANT.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_NOMAD.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_NECROMANCER.get(),
                    manager -> new TekBipedRenderer<>(manager, ZOMBIE_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_ARCHITECT.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_TRADESMAN.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_CHILD.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_TEACHER.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_BARD.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_CLERIC.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_DRUID.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_ENCHANTER.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_NITWIT.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_SPIRIT_SKULL.get(),
                    manager -> new TekBipedRenderer<>(manager, ZOMBIE_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_DEATH_CLOUD.get(),
                    manager -> new TekBipedRenderer<>(manager, ZOMBIE_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_CAPTAIN_AURA.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
        });
    }
}
