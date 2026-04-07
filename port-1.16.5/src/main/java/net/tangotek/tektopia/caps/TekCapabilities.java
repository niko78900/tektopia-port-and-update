package net.tangotek.tektopia.caps;

import java.util.concurrent.Callable;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class TekCapabilities {
    private static boolean registered = false;

    private TekCapabilities() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        CapabilityManager.INSTANCE.register(
                IPlayerLicense.class,
                new PlayerLicense(),
                (Callable<IPlayerLicense>) PlayerLicense::new
        );

        CapabilityManager.INSTANCE.register(
                IVillageData.class,
                new VillageData(),
                (Callable<IVillageData>) VillageData::new
        );
    }
}
