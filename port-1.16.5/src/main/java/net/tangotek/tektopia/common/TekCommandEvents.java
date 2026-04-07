package net.tangotek.tektopia.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Comparator;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.registry.TekEntities;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageStructureManager;

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
                        .then(Commands.literal("starter_kit")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    givePlaytestKit(player);
                                    ctx.getSource().sendSuccess(new StringTextComponent(
                                            "Granted playtest kit: guard spawn egg, Town Hall/Storage tokens, and item frames."
                                    ), true);
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
                        .then(Commands.literal("scan_structure")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String rawType = StringArgumentType.getString(ctx, "type");
                                            TekStructureType structureType = TekStructureType.fromInput(rawType);
                                            if (structureType == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("Unknown structure type: " + rawType + " (use townhall|storage)"));
                                                return 0;
                                            }
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            ServerWorld level = player.getLevel();
                                            TekVillageStructureManager manager = TekVillageRuntime.get().managerFor(level);
                                            TekVillageStructure structure = manager.scanStructure(level, structureType, player.blockPosition(), player.getDirection());
                                            AxisAlignedBB bounds = structure.getBounds();
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            "Scanned " + structureType.getDisplayName()
                                                                    + " | floorTiles=" + structure.getFloorTileCount()
                                                                    + " | avgCeiling=" + String.format("%.2f", structure.getAverageCeilingHeight())
                                                                    + " | safeSpot=" + structure.getSafeSpot()
                                                    ),
                                                    true
                                            );
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            "Bounds min=(" + (int) bounds.minX + "," + (int) bounds.minY + "," + (int) bounds.minZ + ")"
                                                                    + " max=(" + (int) bounds.maxX + "," + (int) bounds.maxY + "," + (int) bounds.maxZ + ")"
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })))
                        .then(Commands.literal("scan_structure_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    TekVillageStructureManager manager = TekVillageRuntime.get().getManager(player.getLevel().dimension()).orElse(null);
                                    if (manager == null || manager.getStructures().isEmpty()) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No structure scans cached for this dimension."));
                                        return 0;
                                    }
                                    for (TekVillageStructure structure : manager.getStructures()) {
                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        structure.getType().name() + " | floorTiles=" + structure.getFloorTileCount()
                                                                + " | avgCeiling=" + String.format("%.2f", structure.getAverageCeilingHeight())
                                                ),
                                                false
                                        );
                                    }
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent("Frame assignments: " + manager.getFrameAssignments().size()),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("nearest_structure")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            String rawType = StringArgumentType.getString(ctx, "type");
                                            TekStructureType structureType = TekStructureType.fromInput(rawType);
                                            if (structureType == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("Unknown structure type: " + rawType + " (use townhall|storage)"));
                                                return 0;
                                            }
                                            TekVillageStructureManager manager = TekVillageRuntime.get().getManager(player.getLevel().dimension()).orElse(null);
                                            if (manager == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No structure cache exists for this dimension."));
                                                return 0;
                                            }
                                            TekVillageStructure structure = manager.getStructure(structureType).orElse(null);
                                            if (structure == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No cached structure for type " + structureType.name()));
                                                return 0;
                                            }
                                            AxisAlignedBB bounds = structure.getBounds();
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            structureType.getDisplayName()
                                                                    + " | doorInside=" + structure.getDoorInside()
                                                                    + " | safeSpot=" + structure.getSafeSpot()
                                                                    + " | floorTiles=" + structure.getFloorTileCount()
                                                    ),
                                                    false
                                            );
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            "Bounds min=(" + (int) bounds.minX + "," + (int) bounds.minY + "," + (int) bounds.minZ + ")"
                                                                    + " max=(" + (int) bounds.maxX + "," + (int) bounds.maxY + "," + (int) bounds.maxZ + ")"
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        })))
                        .then(Commands.literal("discover_structures")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(4, 128))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            ServerWorld level = player.getLevel();
                                            int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                            TekVillageStructureManager manager = TekVillageRuntime.get().managerFor(level);
                                            int discovered = manager.scanStructuresFromFrames(level, player.blockPosition(), radius);
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            "Discovered " + discovered + " frame markers within radius " + radius
                                                                    + ". Cached structures: " + manager.getStructures().size()
                                                    ),
                                                    true
                                            );
                                            return 1;
                                        })))
                        .then(Commands.literal("clear_structure_cache")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    if (!TekVillageRuntime.get().getManager(player.getLevel().dimension()).isPresent()) {
                                        ctx.getSource().sendSuccess(new StringTextComponent("No structure cache existed for this dimension."), false);
                                        return 1;
                                    }
                                    TekVillageRuntime.get().clear(player.getLevel().dimension());
                                    ctx.getSource().sendSuccess(new StringTextComponent("Cleared structure cache for this dimension."), true);
                                    return 1;
                                }))
        );
        TekTopiaPort.LOGGER.info("Registered Phase 6 command scaffold: /tektopia_port ping, /tektopia_port license get|set, /tektopia_port spawn_test_guard, /tektopia_port starter_kit, /tektopia_port guard_filters, /tektopia_port guard_filter, /tektopia_port scan_structure, /tektopia_port scan_structure_status, /tektopia_port nearest_structure, /tektopia_port discover_structures <radius>, /tektopia_port clear_structure_cache");
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

    private static void givePlaytestKit(ServerPlayerEntity player) {
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_GUARD_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_TOWNHALL_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_STORAGE_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(Items.ITEM_FRAME, 12));
    }

}
