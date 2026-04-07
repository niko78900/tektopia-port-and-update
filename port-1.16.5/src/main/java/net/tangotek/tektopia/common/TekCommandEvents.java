package net.tangotek.tektopia.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;

public class TekCommandEvents {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("tektopia_port")
                        .then(Commands.literal("ping")
                                .executes(ctx -> {
                                    ctx.getSource().sendSuccess(new StringTextComponent("TekTopia port command path is active."), false);
                                    return 1;
                                }))
        );
        TekTopiaPort.LOGGER.info("Registered Phase 2 command scaffold: /tektopia_port ping");
    }
}
