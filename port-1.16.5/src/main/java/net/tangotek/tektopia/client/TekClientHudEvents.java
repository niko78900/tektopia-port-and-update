package net.tangotek.tektopia.client;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.network.TekClientSyncCache;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TekClientHudEvents {
    private TekClientHudEvents() {
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (TekClientEvents.STATUS_KEY != null && TekClientEvents.STATUS_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new TekStatusScreen());
        }
    }

    @SubscribeEvent
    public static void onOverlayText(RenderGameOverlayEvent.Text event) {
        if (TekClientSyncCache.getVillages().isEmpty()) {
            return;
        }
        List<String> left = event.getLeft();
        for (TekClientSyncCache.VillageState village : TekClientSyncCache.getVillages().values()) {
            if (village.raidActive || village.alertActive || village.invalidStructures > 0 || village.reservations > 0) {
                left.add("TekTopia: residents=" + village.residents
                        + " raid=" + village.raidActive
                        + " alert=" + village.alertActive
                        + " invalid=" + village.invalidStructures
                        + " reservations=" + village.reservations);
                break;
            }
        }
        if (!TekClientSyncCache.getPathNodes().isEmpty()) {
            left.add("TekTopia blocked paths: " + TekClientSyncCache.getPathNodes().size());
        }
    }
}
