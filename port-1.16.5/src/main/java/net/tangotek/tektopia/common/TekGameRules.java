package net.tangotek.tektopia.common;

import net.tangotek.tektopia.TekTopiaPort;

public final class TekGameRules {
    private TekGameRules() {
    }

    public static void bootstrap() {
        // Phase 2 scaffold: custom gamerule migration is deferred until gameplay systems are ported.
        TekTopiaPort.LOGGER.info("TekTopia gamerule migration scaffold initialized");
    }
}
