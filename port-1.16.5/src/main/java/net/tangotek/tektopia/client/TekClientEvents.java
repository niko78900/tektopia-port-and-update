package net.tangotek.tektopia.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.registry.TekEntities;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TekClientEvents {
    private TekClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                RenderingRegistry.registerEntityRenderingHandler(
                        TekEntities.TEK_GUARD.get(),
                        TekGuardRenderer::new
                )
        );
    }
}
