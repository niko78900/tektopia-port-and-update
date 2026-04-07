package net.tangotek.tektopia.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Comparator;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.registry.TekEntities;

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
                        .then(Commands.literal("license")
                                .then(Commands.literal("get")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            IPlayerLicense cap = player.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).orElse(null);
                                            String data = cap == null ? null : cap.getLicenseData();
                                            String msg = data == null ? "No license data stored." : "License data stored (length=" + data.length() + ").";
                                            ctx.getSource().sendSuccess(new StringTextComponent(msg), false);
                                            return 1;
                                        }))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("data", StringArgumentType.greedyString())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                                    String data = StringArgumentType.getString(ctx, "data");
                                                    player.getCapability(PlayerLicenseProvider.PLAYER_LICENSE_CAPABILITY).ifPresent(cap -> cap.setLicenseData(data));
                                                    ctx.getSource().sendSuccess(new StringTextComponent("License data updated for testing."), false);
                                                    return 1;
                                                }))))
                        .then(Commands.literal("spawn_test_guard")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekGuardEntity guard = TekEntities.TEK_GUARD.get().create(level);
                                    if (guard == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("Failed to create TekGuardEntity instance."));
                                        return 0;
                                    }
                                    guard.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
                                    level.addFreshEntity(guard);
                                    ctx.getSource().sendSuccess(new StringTextComponent("Spawned test TekGuardEntity."), true);
                                    return 1;
                                }))
                        .then(Commands.literal("guard_filters")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    TekGuardEntity guard = findNearestGuard(ctx.getSource().getPlayerOrException());
                                    if (guard == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No TekGuardEntity within 24 blocks."));
                                        return 0;
                                    }
                                    List<String> filters = guard.getAIFilters();
                                    if (filters.isEmpty()) {
                                        ctx.getSource().sendSuccess(new StringTextComponent("Guard has no registered AI filters."), false);
                                        return 1;
                                    }
                                    for (String filter : filters) {
                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(filter + " = " + guard.isAIFilterEnabled(filter)),
                                                false
                                        );
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("guard_filter")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("filter", StringArgumentType.string())
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(ctx -> {
                                                    TekGuardEntity guard = findNearestGuard(ctx.getSource().getPlayerOrException());
                                                    if (guard == null) {
                                                        ctx.getSource().sendFailure(new StringTextComponent("No TekGuardEntity within 24 blocks."));
                                                        return 0;
                                                    }
                                                    String filter = StringArgumentType.getString(ctx, "filter");
                                                    boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                                    if (!guard.setGuardFilter(filter, enabled)) {
                                                        ctx.getSource().sendFailure(new StringTextComponent("Unknown guard filter: " + filter));
                                                        return 0;
                                                    }
                                                    ctx.getSource().sendSuccess(new StringTextComponent("Set " + filter + " = " + enabled), true);
                                                    return 1;
                                                }))))
        );
        TekTopiaPort.LOGGER.info("Registered Phase 5 command scaffold: /tektopia_port ping, /tektopia_port license get|set, /tektopia_port spawn_test_guard, /tektopia_port guard_filters, /tektopia_port guard_filter");
    }

    private static TekGuardEntity findNearestGuard(ServerPlayerEntity player) {
        List<TekGuardEntity> guards = player.level.getEntitiesOfClass(
                TekGuardEntity.class,
                player.getBoundingBox().inflate(24.0D)
        );
        return guards.stream()
                .min(Comparator.comparingDouble(guard -> guard.distanceToSqr(player)))
                .orElse(null);
    }
}
