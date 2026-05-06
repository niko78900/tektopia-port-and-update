package net.tangotek.tektopia.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.registry.TekEntities;
import net.minecraft.util.ResourceLocation;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TekClientEvents {
    private static final ResourceLocation VILLAGER_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/villager/villager.png");
    private static final ResourceLocation PILLAGER_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/illager/pillager.png");
    private static final ResourceLocation VINDICATOR_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/illager/vindicator.png");
    private static final ResourceLocation ZOMBIE_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/zombie/zombie.png");
    private static final ResourceLocation HUSK_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/zombie/husk.png");
    private static final ResourceLocation DROWNED_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/zombie/drowned.png");
    private static final ResourceLocation SKELETON_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation STRAY_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/skeleton/stray.png");

    private TekClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_GUARD.get(),
                    TekGuardRenderer::new
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
                    manager -> new TekBipedRenderer<>(manager, ZOMBIE_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_LUMBERJACK.get(),
                    manager -> new TekBipedRenderer<>(manager, HUSK_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_CHEF.get(),
                    manager -> new TekBipedRenderer<>(manager, VILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_RANCHER.get(),
                    manager -> new TekBipedRenderer<>(manager, DROWNED_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_BUTCHER.get(),
                    manager -> new TekBipedRenderer<>(manager, VINDICATOR_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_MERCHANT.get(),
                    manager -> new TekBipedRenderer<>(manager, PILLAGER_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_NOMAD.get(),
                    manager -> new TekBipedRenderer<>(manager, STRAY_TEXTURE)
            );
            RenderingRegistry.registerEntityRenderingHandler(
                    TekEntities.TEK_NECROMANCER.get(),
                    manager -> new TekBipedRenderer<>(manager, SKELETON_TEXTURE)
            );
            TekTopiaPort.LOGGER.info("Registered Biped-first renderers for all TekTopia 1.16.5 entities");
        });
    }
}
