package net.tangotek.tektopia.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.caps.IPlayerLicense;
import net.tangotek.tektopia.caps.PlayerLicenseProvider;
import net.tangotek.tektopia.common.ProfessionType;
import net.tangotek.tektopia.common.TekWorkerStatus;
import net.tangotek.tektopia.entities.TekBlacksmithEntity;
import net.tangotek.tektopia.entities.TekFarmerEntity;
import net.tangotek.tektopia.entities.TekGuardEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.registry.TekEntities;
import net.tangotek.tektopia.registry.TekItems;
import net.tangotek.tektopia.structures.TekStructureStorage;
import net.tangotek.tektopia.structures.TekStructureType;
import net.tangotek.tektopia.structures.TekVillageStructure;
import net.tangotek.tektopia.village.TekAnimalPens;
import net.tangotek.tektopia.village.TekVillageEconomy;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageManager;
import net.tangotek.tektopia.village.TekVillageStructureManager;
import net.tangotek.tektopia.worldgen.TekStarterStructureGenerator;

public class TekCommandEvents {
    private static final String FARMER_TARGET_POS_TAG = "tek_farmer_work_target";
    private static final String FARMER_COOLDOWN_TAG = "tek_farmer_work_cooldown";
    private static final String FARMER_CARRY_TAG = "tek_farmer_carry";
    private static final String FARMER_MODE_TAG = "tek_farmer_mode";
    private static final int FARMER_MODE_DELIVER = 1;
    private static final String BLACKSMITH_COOLDOWN_TAG = "tek_blacksmith_work_cooldown";
    private static final String BLACKSMITH_DEMAND_TAG = "tek_blacksmith_demand";
    private static final String BLACKSMITH_MISSING_TAG = "tek_blacksmith_missing";
    private static final String BLACKSMITH_PLAN_TAG = "tek_blacksmith_plan";

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
                                            "Granted playtest kit: core worker spawn eggs, structure tokens, and item frames."
                                    ), true);
                                    return 1;
                                }))
                        .then(Commands.literal("spawn_test_farmer")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekFarmerEntity farmer = TekEntities.TEK_FARMER.get().create(level);
                                    if (farmer == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("Failed to create TekFarmerEntity instance."));
                                        return 0;
                                    }
                                    farmer.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
                                    level.addFreshEntity(farmer);
                                    ctx.getSource().sendSuccess(new StringTextComponent("Spawned test TekFarmerEntity."), true);
                                    return 1;
                                }))
                        .then(Commands.literal("spawn_test_blacksmith")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekBlacksmithEntity blacksmith = TekEntities.TEK_BLACKSMITH.get().create(level);
                                    if (blacksmith == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("Failed to create TekBlacksmithEntity instance."));
                                        return 0;
                                    }
                                    blacksmith.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
                                    level.addFreshEntity(blacksmith);
                                    ctx.getSource().sendSuccess(new StringTextComponent("Spawned test TekBlacksmithEntity."), true);
                                    return 1;
                                }))
                        .then(Commands.literal("spawn_test_worker")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            String type = StringArgumentType.getString(ctx, "type");
                                            Entity entity = createPortEntity(type, player.getLevel());
                                            if (entity == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("Unknown worker/threat type: " + type));
                                                return 0;
                                            }
                                            entity.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
                                            player.getLevel().addFreshEntity(entity);
                                            ctx.getSource().sendSuccess(new StringTextComponent("Spawned test " + type + "."), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("village")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.literal("create")
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(16, 256))
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                                    int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                                    TekVillageManager manager = runtime.villageManagerFor(player.getLevel());
                                                    TekVillage village = manager.createVillage(player.blockPosition(), radius, player.getLevel().getGameTime());
                                                    runtime.saveRuntime(player.getLevel());
                                                    ctx.getSource().sendSuccess(
                                                            new StringTextComponent("Created village " + village.getId() + " at " + village.getCenter() + " radius=" + village.getRadius()),
                                                            true
                                                    );
                                                    return 1;
                                                })))
                                .then(Commands.literal("status")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(player.getLevel());
                                            TekVillage nearest = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                            if (nearest == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            "Nearest village " + nearest.getId()
                                                                    + " center=" + nearest.getCenter()
                                                                    + " radius=" + nearest.getRadius()
                                                                    + " hostiles=" + nearest.getLastKnownHostileCount()
                                                                    + " residents=" + nearest.getResidents().size()
                                                                    + " tokenPurchases=" + nearest.getTokenPurchaseCount()
                                                                    + " structureCost=" + nearest.getStructureTokenCost()
                                                                    + " professionCost=" + nearest.getProfessionTokenCost()
                                                                    + " raidActive=" + nearest.isRaidActive()
                                                                    + " raidLevel=" + nearest.getRaidLevel()
                                                                    + " nextMerchant=" + nearest.getNextMerchantTick()
                                                                    + " nextNomad=" + nearest.getNextNomadTick()
                                                                    + " nextRaid=" + nearest.getNextRaidTick()
                                                                    + " professions=" + nearest.getProfessionCounts()
                                                    ),
                                                    false
                                            );
                                            return 1;
                                        }))
                                .then(Commands.literal("list")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(player.getLevel());
                                            if (manager.getVillages().isEmpty()) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                                return 0;
                                            }
                                            for (TekVillage village : manager.getVillages()) {
                                                ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        village.getId() + " center=" + village.getCenter()
                                                                + " radius=" + village.getRadius()
                                                                + " hostiles=" + village.getLastKnownHostileCount()
                                                                + " residents=" + village.getResidents().size()
                                                                + " structureCost=" + village.getStructureTokenCost()
                                                                + " professionCost=" + village.getProfessionTokenCost()
                                                                + " raidActive=" + village.isRaidActive()
                                                                + " raidLevel=" + village.getRaidLevel()
                                                                + " professions=" + village.getProfessionCounts()
                                                ),
                                                false
                                        );
                                    }
                                            return 1;
                                        }))
                                .then(Commands.literal("remove_nearest")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            TekVillageRuntime runtime = TekVillageRuntime.get();
                                            TekVillageManager manager = runtime.villageManagerFor(player.getLevel());
                                            TekVillage nearest = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                            if (nearest == null) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                                return 0;
                                            }
                                            manager.removeVillage(nearest.getId());
                                            runtime.saveRuntime(player.getLevel());
                                            ctx.getSource().sendSuccess(new StringTextComponent("Removed village " + nearest.getId()), true);
                                            return 1;
                                        }))
                                .then(Commands.literal("clear")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            TekVillageRuntime runtime = TekVillageRuntime.get();
                                            TekVillageManager manager = runtime.villageManagerFor(player.getLevel());
                                            manager.clear();
                                            runtime.saveRuntime(player.getLevel());
                                            ctx.getSource().sendSuccess(new StringTextComponent("Cleared all villages in this dimension."), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("raid_test")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 50))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            int count = IntegerArgumentType.getInteger(ctx, "count");
                                            int spawned = spawnRaidNearNearestVillage(player, count);
                                            if (spawned <= 0) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No village available to spawn a test raid near."));
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("Spawned " + spawned + " test zombies near nearest village."), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("necromancer_raid")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 10))
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            int count = IntegerArgumentType.getInteger(ctx, "count");
                                            int spawned = spawnEntityRaidNearNearestVillage(player, TekEntities.TEK_NECROMANCER.get(), count);
                                            if (spawned <= 0) {
                                                ctx.getSource().sendFailure(new StringTextComponent("No village available to spawn a necromancer raid near."));
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("Spawned " + spawned + " necromancers near nearest village."), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("raid_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillage village = TekVillageRuntime.get().villageManagerFor(level).findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }
                                    List<MonsterEntity> threats = level.getEntitiesOfClass(
                                            MonsterEntity.class,
                                            village.getBounds().inflate(48.0D, 8.0D, 48.0D),
                                            TekCommandEvents::isRaidThreat
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Raid village=" + village.getId()
                                                            + " active=" + village.isRaidActive()
                                                            + " level=" + village.getRaidLevel()
                                                            + " nextRaid=" + village.getNextRaidTick()
                                                            + " alert=" + (village.getLastAlertPos() == null ? "-" : village.getLastAlertPos().toShortString())
                                                            + " threats=" + threats.size()
                                            ),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("raid_clear")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    TekVillage village = runtime.villageManagerFor(level).findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }
                                    List<MonsterEntity> threats = level.getEntitiesOfClass(
                                            MonsterEntity.class,
                                            village.getBounds().inflate(64.0D, 16.0D, 64.0D),
                                            TekCommandEvents::isRaidThreat
                                    );
                                    for (MonsterEntity threat : threats) {
                                        threat.remove();
                                    }
                                    village.setRaidActive(false);
                                    village.clearAlert();
                                    runtime.saveRuntime(level);
                                    ctx.getSource().sendSuccess(new StringTextComponent("Cleared " + threats.size() + " raid threats near village " + village.getId()), true);
                                    return 1;
                                }))
                        .then(Commands.literal("reservations_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    long gameTime = player.getLevel().getGameTime();
                                    int expired = TekVillageEconomy.purgeExpiredReservationsForAll(gameTime);
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Reservations active=" + TekVillageEconomy.getActiveReservationCount(gameTime)
                                                            + " expiredPurged=" + expired
                                                            + " byStorage=" + TekVillageEconomy.getReservationCountsByStorage(gameTime)
                                            ),
                                            false
                                    );
                                    for (String line : TekVillageEconomy.describeReservations(gameTime, 8)) {
                                        ctx.getSource().sendSuccess(new StringTextComponent("Reservation " + line), false);
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("pens_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageStructureManager structureManager = TekVillageRuntime.get().managerFor(level);
                                    ctx.getSource().sendSuccess(new StringTextComponent(TekAnimalPens.describePens(level, structureManager)), false);
                                    return 1;
                                }))
                        .then(Commands.literal("qa_start")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    TekVillageManager manager = runtime.villageManagerFor(level);
                                    TekVillage village = manager.findNearestVillage(player.blockPosition())
                                            .orElseGet(() -> manager.createVillage(player.blockPosition(), 64, level.getGameTime()));
                                    village.setNextMerchantTick(level.getGameTime() + 1200L);
                                    village.setNextNomadTick(level.getGameTime() + 2400L);
                                    village.setNextRaidTick(level.getGameTime() + 6000L);
                                    givePlaytestKit(player);
                                    runtime.saveRuntime(level);
                                    ctx.getSource().sendSuccess(new StringTextComponent("QA village ready: " + village.getId() + " kit granted; visitor and raid timers shortened."), true);
                                    return 1;
                                }))
                        .then(Commands.literal("qa_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    TekVillageManager manager = runtime.villageManagerFor(level);
                                    TekVillageStructureManager structureManager = runtime.managerFor(level);
                                    TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }
                                    int invalid = 0;
                                    for (TekVillageStructure structure : structureManager.getStructures()) {
                                        if (!structure.isValid()) {
                                            invalid++;
                                        }
                                    }
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "QA village=" + village.getId()
                                                            + " residents=" + village.getResidents().size()
                                                            + " professions=" + village.getProfessionCounts()
                                                            + " structures=" + structureManager.getStructures().size()
                                                            + " invalidStructures=" + invalid
                                                            + " reservations=" + TekVillageEconomy.getActiveReservationCount(level.getGameTime())
                                                            + " raidActive=" + village.isRaidActive()
                                                            + " hostiles=" + village.getLastKnownHostileCount()
                                            ),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(new StringTextComponent("Perf: " + manager.formatLastPerformance()), false);
                                    ctx.getSource().sendSuccess(new StringTextComponent("Pens: " + TekAnimalPens.describePens(level, structureManager)), false);
                                    return 1;
                                }))
                        .then(Commands.literal("parity_report")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    TekVillageManager manager = runtime.villageManagerFor(level);
                                    TekVillageStructureManager structureManager = runtime.managerFor(level);
                                    for (String line : buildParityReport(level, manager, structureManager, player.blockPosition())) {
                                        ctx.getSource().sendSuccess(new StringTextComponent(line), false);
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("asset_inventory")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    for (String line : TekVisualCoverageReport.formatReport()) {
                                        ctx.getSource().sendSuccess(new StringTextComponent(line), false);
                                    }
                                    return 1;
                                })
                                .then(Commands.literal("strict")
                                        .executes(ctx -> {
                                            TekVisualCoverageReport.CoverageSummary summary = TekVisualCoverageReport.summarize();
                                            for (String line : summary.formatLines()) {
                                                ctx.getSource().sendSuccess(new StringTextComponent(line), false);
                                            }
                                            if (!summary.isStrictPass()) {
                                                ctx.getSource().sendFailure(new StringTextComponent(
                                                        "Strict visual asset gate failed: partial=" + summary.getPartial()
                                                                + " missing=" + summary.getMissing()
                                                ));
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent(
                                                    "Strict visual asset gate passed: complete=" + summary.getComplete()
                                            ), false);
                                            return 1;
                                        })))
                        .then(Commands.literal("sync_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    TekVillageManager villageManager = runtime.villageManagerFor(level);
                                    TekVillageStructureManager structureManager = runtime.managerFor(level);
                                    TekVillage nearest = villageManager.findNearestVillage(player.blockPosition()).orElse(null);
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Sync protocol=" + TekNetwork.getProtocolVersion()
                                                            + " initialized=" + TekNetwork.isInitialized()
                                                            + " villages=" + villageManager.getVillages().size()
                                                            + " structures=" + structureManager.getStructures().size()
                                                            + " frameAssignments=" + structureManager.getFrameAssignments().size()
                                            ),
                                            false
                                    );
                                    if (nearest != null) {
                                        AxisAlignedBB bounds = nearest.getBounds().inflate(16.0D);
                                        int villagers = level.getEntitiesOfClass(
                                                TekVillagerEntity.class,
                                                bounds,
                                                TekVillagerEntity::isAlive
                                        ).size();
                                        double packetRadius = Math.max(96.0D, nearest.getRadius() + 32.0D);
                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        "Nearest snapshot id=" + nearest.getId()
                                                                + " packetRadius=" + String.format("%.1f", packetRadius)
                                                                + " residents=" + nearest.getResidents().size()
                                                                + " liveVillagers=" + villagers
                                                                + " alertActive=" + nearest.hasActiveAlert(level.getGameTime(), 200L)
                                                                + " raidActive=" + nearest.isRaidActive()
                                                ),
                                                false
                                        );
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("worldgen_test")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                            ServerWorld level = player.getLevel();
                                            String rawType = StringArgumentType.getString(ctx, "type");
                                            TekStructureType structureType = TekStructureType.fromInput(rawType);
                                            if (structureType == null || !TekStarterStructureGenerator.isStarterType(structureType)) {
                                                ctx.getSource().sendFailure(new StringTextComponent("Unknown starter structure type: " + rawType + " (use townhall|storage|home|farm|mineshaft)"));
                                                return 0;
                                            }

                                            BlockPos target = player.blockPosition().relative(player.getDirection(), 10);
                                            TekStarterStructureGenerator.Result result = TekStarterStructureGenerator.generate(
                                                    level,
                                                    structureType,
                                                    target,
                                                    player.getDirection()
                                            );
                                            TekVillageRuntime runtime = TekVillageRuntime.get();
                                            TekVillageStructureManager structureManager = runtime.managerFor(level);
                                            TekVillageStructure structure = structureManager.scanStructure(
                                                    level,
                                                    structureType,
                                                    result.getDoorInside(),
                                                    result.getSignFacing()
                                            );
                                            if (structureType == TekStructureType.TOWNHALL && structure.isValid()) {
                                                TekVillageManager villageManager = runtime.villageManagerFor(level);
                                                int dynamicRadius = Math.max(32, (int) Math.ceil(Math.sqrt(Math.max(1, structure.getFloorTileCount())) * 4.0D));
                                                villageManager.upsertNearestVillage(structure.getDoorInside(), dynamicRadius, level.getGameTime());
                                            }
                                            runtime.saveRuntime(level);

                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent(
                                                            "Generated starter " + structureType.getDisplayName()
                                                                    + " blocks=" + result.getPlacedBlocks()
                                                                    + " doorInside=" + result.getDoorInside().toShortString()
                                                                    + " frame=" + result.getFramePos().toShortString()
                                                                    + " valid=" + structure.isValid()
                                                                    + " validation=" + structure.getValidationSummary()
                                                    ),
                                                    true
                                            );
                                            ctx.getSource().sendSuccess(
                                                    new StringTextComponent("Cached structures: " + structureManager.getStructures().size()),
                                                    false
                                            );
                                            return 1;
                                        })))
                        .then(Commands.literal("worker_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(level);
                                    TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }

                                    AxisAlignedBB bounds = village.getBounds().inflate(12.0D, 4.0D, 12.0D);
                                    List<TekFarmerEntity> farmers = level.getEntitiesOfClass(
                                            TekFarmerEntity.class,
                                            bounds,
                                            farmer -> farmer != null && farmer.isAlive()
                                    );
                                    List<TekBlacksmithEntity> blacksmiths = level.getEntitiesOfClass(
                                            TekBlacksmithEntity.class,
                                            bounds,
                                            smith -> smith != null && smith.isAlive()
                                    );
                                    if (farmers.isEmpty() && blacksmiths.isEmpty()) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No workers found near nearest village."));
                                        return 0;
                                    }

                                    long gameTime = level.getGameTime();
                                    farmers.sort(Comparator.comparingDouble(farmer -> farmer.distanceToSqr(player)));
                                    for (TekFarmerEntity farmer : farmers) {
                                        CompoundNBT data = farmer.getPersistentData();
                                        String mode = data.getInt(FARMER_MODE_TAG) == FARMER_MODE_DELIVER ? "deliver" : "harvest";
                                        long cooldown = Math.max(0L, data.getLong(FARMER_COOLDOWN_TAG) - gameTime);
                                        String target = data.contains(FARMER_TARGET_POS_TAG, 4)
                                                ? BlockPos.of(data.getLong(FARMER_TARGET_POS_TAG)).toShortString()
                                                : "-";
                                        String carry = formatFarmerCarry(data);
                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        "Farmer " + shortId(farmer.getUUID())
                                                                + " mode=" + mode
                                                                + " cooldown=" + cooldown
                                                                + " target=" + target
                                                                + " carry=" + carry
                                                ),
                                                false
                                        );
                                    }

                                    blacksmiths.sort(Comparator.comparingDouble(smith -> smith.distanceToSqr(player)));
                                    for (TekBlacksmithEntity smith : blacksmiths) {
                                        CompoundNBT data = smith.getPersistentData();
                                        long cooldown = Math.max(0L, data.getLong(BLACKSMITH_COOLDOWN_TAG) - gameTime);
                                        String demand = formatBlacksmithDemand(data);
                                        String missing = data.getString(BLACKSMITH_MISSING_TAG);
                                        String plan = data.getString(BLACKSMITH_PLAN_TAG);
                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        "Blacksmith " + shortId(smith.getUUID())
                                                                + " cooldown=" + cooldown
                                                                + " demand=" + demand
                                                                + " missing=" + (missing == null || missing.isEmpty() ? "-" : missing)
                                                                + " plan=" + (plan == null || plan.isEmpty() ? "-" : plan)
                                                ),
                                                false
                                        );
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("workforce_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(level);
                                    TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }
                                    List<TekVillagerEntity> workers = level.getEntitiesOfClass(
                                            TekVillagerEntity.class,
                                            village.getBounds().inflate(12.0D, 4.0D, 12.0D),
                                            worker -> worker != null && worker.isAlive()
                                    );
                                    if (workers.isEmpty()) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia workers found near nearest village."));
                                        return 0;
                                    }
                                    long gameTime = level.getGameTime();
                                    workers.sort(Comparator.comparingDouble(worker -> worker.distanceToSqr(player)));
                                    for (TekVillagerEntity worker : workers) {
                                        CompoundNBT data = worker.getPersistentData();
                                        String type = worker.getType().getRegistryName() == null ? worker.getType().toString() : worker.getType().getRegistryName().getPath();
                                        String mode = data.getString(TekVillageManager.WORKER_MODE_TAG);
                                        String result = data.getString(TekVillageManager.WORKER_LAST_RESULT_TAG);
                                        long cooldown = Math.max(0L, data.getLong(TekVillageManager.WORKER_COOLDOWN_TAG) - gameTime);
                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        type + " " + shortId(worker.getUUID())
                                                                + " mode=" + (mode == null || mode.isEmpty() ? "-" : mode)
                                                                + " result=" + (result == null || result.isEmpty() ? "-" : result)
                                                                + " cooldown=" + cooldown
                                                ),
                                                false
                                        );
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("villager_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    TekVillagerEntity villager = findNearestVillager(ctx.getSource().getPlayerOrException());
                                    if (villager == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia villager within 24 blocks."));
                                        return 0;
                                    }
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent("Villager " + shortId(villager.getUUID()) + " " + villager.formatCoreDebug()),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent("Skills: " + formatSkillSummary(villager)),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent("Inventory: " + formatVillagerInventory(villager)),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("gui_snapshot")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    TekVillagerEntity villager = findNearestVillager(player);
                                    if (villager == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia villager within 24 blocks."));
                                        return 0;
                                    }
                                    for (String line : TekVillagerGuiSnapshotReport.format(villager)) {
                                        ctx.getSource().sendSuccess(new StringTextComponent(line), false);
                                    }
                                    TekNetwork.sendVillagerGuiSnapshot(player, villager);
                                    return 1;
                                }))
                        .then(Commands.literal("villager_set")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("field", StringArgumentType.word())
                                        .then(Commands.argument("value", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    TekVillagerEntity villager = findNearestVillager(ctx.getSource().getPlayerOrException());
                                                    if (villager == null) {
                                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia villager within 24 blocks."));
                                                        return 0;
                                                    }
                                                    String field = StringArgumentType.getString(ctx, "field");
                                                    String value = StringArgumentType.getString(ctx, "value");
                                                    String result = applyVillagerField(villager, field, value);
                                                    if (result == null) {
                                                        ctx.getSource().sendFailure(new StringTextComponent("Unknown or invalid villager field/value: " + field + "=" + value));
                                                        return 0;
                                                    }
                                                    ctx.getSource().sendSuccess(new StringTextComponent(result), true);
                                                    return 1;
                                                }))))
                        .then(Commands.literal("villager_skill")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("profession", StringArgumentType.word())
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                                .executes(ctx -> {
                                                    TekVillagerEntity villager = findNearestVillager(ctx.getSource().getPlayerOrException());
                                                    if (villager == null) {
                                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia villager within 24 blocks."));
                                                        return 0;
                                                    }
                                                    ProfessionType profession = ProfessionType.fromSerializedName(StringArgumentType.getString(ctx, "profession"));
                                                    if (profession == ProfessionType.UNKNOWN) {
                                                        ctx.getSource().sendFailure(new StringTextComponent("Unknown profession."));
                                                        return 0;
                                                    }
                                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                                    villager.setSkill(profession, value);
                                                    ctx.getSource().sendSuccess(
                                                            new StringTextComponent("Set " + shortId(villager.getUUID()) + " " + profession.getSerializedName() + " skill to " + value),
                                                            true
                                                    );
                                                    return 1;
                                                }))))
                        .then(Commands.literal("villager_home_here")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    TekVillagerEntity villager = findNearestVillager(player);
                                    if (villager == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia villager within 24 blocks."));
                                        return 0;
                                    }
                                    villager.setHomePos(player.blockPosition());
                                    ctx.getSource().sendSuccess(new StringTextComponent("Set villager home to " + player.blockPosition().toShortString()), true);
                                    return 1;
                                }))
                        .then(Commands.literal("villager_bed_here")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    TekVillagerEntity villager = findNearestVillager(player);
                                    if (villager == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No TekTopia villager within 24 blocks."));
                                        return 0;
                                    }
                                    villager.setBedPos(player.blockPosition());
                                    ctx.getSource().sendSuccess(new StringTextComponent("Set villager bed to " + player.blockPosition().toShortString()), true);
                                    return 1;
                                }))
                        .then(Commands.literal("economy_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    TekVillageManager manager = runtime.villageManagerFor(level);
                                    TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }

                                    TekVillageStructureManager structureManager = runtime.managerFor(level);
                                    TekStructureStorage storage = resolveStorageStructure(structureManager);
                                    if (storage == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No Storage structure is cached for this dimension."));
                                        return 0;
                                    }
                                    TekVillageEconomy economy = TekVillageEconomy.fromStorage(level, storage);
                                    if (economy.getChests().isEmpty()) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No storage chests were found in cached Storage structure."));
                                        return 0;
                                    }

                                    int iron = economy.countItem(Items.IRON_INGOT);
                                    int gold = economy.countItem(Items.GOLD_INGOT);
                                    int diamond = economy.countItem(Items.DIAMOND);
                                    int wheat = economy.countItem(Items.WHEAT);
                                    int bread = economy.countItem(Items.BREAD);
                                    int potato = economy.countItem(Items.POTATO);
                                    int carrot = economy.countItem(Items.CARROT);
                                    int seeds = economy.countItem(Items.WHEAT_SEEDS);
                                    int logs = economy.countItem(Items.OAK_LOG)
                                            + economy.countItem(Items.SPRUCE_LOG)
                                            + economy.countItem(Items.BIRCH_LOG)
                                            + economy.countItem(Items.JUNGLE_LOG)
                                            + economy.countItem(Items.ACACIA_LOG)
                                            + economy.countItem(Items.DARK_OAK_LOG);
                                    int cobble = economy.countItem(Items.COBBLESTONE);
                                    int wool = economy.countItem(Items.WHITE_WOOL);
                                    int cooked = economy.countItem(Items.COOKED_BEEF)
                                            + economy.countItem(Items.COOKED_PORKCHOP)
                                            + economy.countItem(Items.COOKED_CHICKEN)
                                            + economy.countItem(Items.COOKED_MUTTON);

                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Economy nearest village " + village.getId()
                                                            + " chests=" + economy.getChests().size()
                                                            + " materials[iron=" + iron
                                                            + ",gold=" + gold
                                                            + ",diamond=" + diamond + "]"
                                            ),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Farm stock[wheat=" + wheat
                                                            + ",bread=" + bread
                                                            + ",potato=" + potato
                                                            + ",carrot=" + carrot
                                                            + ",seeds=" + seeds + "]"
                                            ),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Worker stock[logs=" + logs
                                                            + ",cobble=" + cobble
                                                            + ",wool=" + wool
                                                            + ",cooked_meat=" + cooked + "]"
                                            ),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent("Reserved stock " + formatItemCounts(economy.countReservedItems(level.getGameTime()))),
                                            false
                                    );
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Craftable armor " + formatCraftableArmor("iron", iron)
                                                            + " " + formatCraftableArmor("gold", gold)
                                                            + " " + formatCraftableArmor("diamond", diamond)
                                            ),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("trade_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    TekVillage village = TekVillageRuntime.get().villageManagerFor(player.getLevel()).findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }
                                    ctx.getSource().sendSuccess(
                                            new StringTextComponent(
                                                    "Trade village=" + village.getId()
                                                            + " tokenPurchases=" + village.getTokenPurchaseCount()
                                                            + " tier=" + village.getTokenPriceTier()
                                                            + " structureCost=" + village.getStructureTokenCost()
                                                            + " professionCost=" + village.getProfessionTokenCost()
                                                            + " recentSales=" + formatRecentSales(village)
                                            ),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(Commands.literal("guard_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    ServerWorld level = player.getLevel();
                                    TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(level);
                                    TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
                                    if (village == null) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No villages exist in this dimension."));
                                        return 0;
                                    }

                                    AxisAlignedBB bounds = village.getBounds().inflate(12.0D, 4.0D, 12.0D);
                                    List<TekGuardEntity> guards = level.getEntitiesOfClass(
                                            TekGuardEntity.class,
                                            bounds,
                                            guard -> guard != null && guard.isAlive()
                                    );
                                    if (guards.isEmpty()) {
                                        ctx.getSource().sendFailure(new StringTextComponent("No guards found near nearest village."));
                                        return 0;
                                    }

                                    guards.sort(Comparator.comparingDouble(guard -> guard.distanceToSqr(player)));
                                    for (TekGuardEntity guard : guards) {
                                        int head = guard.scoreArmor(guard.getItemBySlot(EquipmentSlotType.HEAD), EquipmentSlotType.HEAD);
                                        int chest = guard.scoreArmor(guard.getItemBySlot(EquipmentSlotType.CHEST), EquipmentSlotType.CHEST);
                                        int legs = guard.scoreArmor(guard.getItemBySlot(EquipmentSlotType.LEGS), EquipmentSlotType.LEGS);
                                        int feet = guard.scoreArmor(guard.getItemBySlot(EquipmentSlotType.FEET), EquipmentSlotType.FEET);
                                        int weapon = guard.scoreWeapon(guard.getMainHandItem());
                                        int total = Math.max(0, head) + Math.max(0, chest) + Math.max(0, legs) + Math.max(0, feet) + Math.max(0, weapon);
                                        String missing = formatGuardMissingSlots(head, chest, legs, feet, weapon);

                                        ctx.getSource().sendSuccess(
                                                new StringTextComponent(
                                                        "Guard " + shortId(guard.getUUID())
                                                                + " total=" + total
                                                                + " head=" + head
                                                                + " chest=" + chest
                                                                + " legs=" + legs
                                                                + " feet=" + feet
                                                                + " weapon=" + weapon
                                                                + " missing=" + missing
                                                                + " equipped[head=" + formatItemId(guard.getItemBySlot(EquipmentSlotType.HEAD))
                                                                + ",chest=" + formatItemId(guard.getItemBySlot(EquipmentSlotType.CHEST))
                                                                + ",legs=" + formatItemId(guard.getItemBySlot(EquipmentSlotType.LEGS))
                                                                + ",feet=" + formatItemId(guard.getItemBySlot(EquipmentSlotType.FEET))
                                                                + ",main=" + formatItemId(guard.getMainHandItem())
                                                                + "]"
                                                ),
                                                false
                                        );
                                    }
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
                                            TekVillageRuntime.get().saveRuntime(level);
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
                                                                + " | valid=" + structure.isValid()
                                                                + " | validation=" + structure.getValidationSummary()
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
                                            TekVillageRuntime.get().saveRuntime(level);
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
                                    ServerWorld level = player.getLevel();
                                    TekVillageRuntime runtime = TekVillageRuntime.get();
                                    if (!runtime.getManager(level.dimension()).isPresent()) {
                                        ctx.getSource().sendSuccess(new StringTextComponent("No structure cache existed for this dimension."), false);
                                        return 1;
                                    }
                                    runtime.managerFor(level).clear();
                                    runtime.saveRuntime(level);
                                    ctx.getSource().sendSuccess(new StringTextComponent("Cleared structure cache for this dimension."), true);
                                    return 1;
                                }))
        );
        TekTopiaPort.LOGGER.info("Registered Phase 6 command scaffold: /tektopia_port ping, /tektopia_port license get|set, spawn_test_*, starter_kit, village, raid_test, necromancer_raid, qa_status, parity_report, asset_inventory, sync_status, worldgen_test, worker_status, workforce_status, villager_status, gui_snapshot, villager_set, villager_skill, villager_home_here, villager_bed_here, economy_status, guard_status, guard_filters, guard_filter, scan_structure, nearest_structure, discover_structures, clear_structure_cache");
    }

    private static List<String> buildParityReport(
            ServerWorld level,
            TekVillageManager manager,
            TekVillageStructureManager structureManager,
            BlockPos origin
    ) {
        long gameTime = level.getGameTime();
        TekVillageEconomy.purgeExpiredReservationsForAll(gameTime);
        List<String> lines = new ArrayList<>();
        lines.add("TekTopia parity report dim=" + level.dimension().location() + " gameTime=" + gameTime);
        lines.add("Runtime villages=" + manager.size()
                + " structures=" + structureManager.getStructures().size()
                + " reservations=" + TekVillageEconomy.getActiveReservationCount(gameTime)
                + " perf=" + manager.formatLastPerformance());

        Map<TekStructureType, Integer> structuresByType = new LinkedHashMap<>();
        Map<TekStructureType, Integer> invalidByType = new LinkedHashMap<>();
        List<String> invalidStructures = new ArrayList<>();
        for (TekVillageStructure structure : structureManager.getStructures()) {
            structuresByType.merge(structure.getType(), 1, Integer::sum);
            if (!structure.isValid()) {
                invalidByType.merge(structure.getType(), 1, Integer::sum);
                invalidStructures.add(structure.getType().getDisplayName()
                        + "@" + structure.getDoorInside().toShortString()
                        + "=" + structure.getValidationSummary());
            }
        }
        lines.add("Structures byType=" + formatEnumCounts(structuresByType)
                + " invalidByType=" + formatEnumCounts(invalidByType));
        for (int i = 0; i < Math.min(6, invalidStructures.size()); i++) {
            lines.add("Invalid structure " + invalidStructures.get(i));
        }

        List<TekVillagerEntity> allVillagers = level.getEntitiesOfClass(
                TekVillagerEntity.class,
                new AxisAlignedBB(origin).inflate(192.0D, 64.0D, 192.0D),
                villager -> villager != null && villager.isAlive()
        );
        Map<ProfessionType, Integer> liveProfessions = new LinkedHashMap<>();
        int hungry = 0;
        int homeless = 0;
        int bedless = 0;
        int activeThoughts = 0;
        for (TekVillagerEntity villager : allVillagers) {
            liveProfessions.merge(villager.getProfessionType(), 1, Integer::sum);
            if (villager.getHunger() < 35) {
                hungry++;
            }
            if (villager.getHomePos() == null) {
                homeless++;
            }
            if (villager.getBedPos() == null) {
                bedless++;
            }
            if (!villager.getThoughtKey().isEmpty() || !villager.getItemThoughtId().isEmpty()) {
                activeThoughts++;
            }
        }
        lines.add("Live villagers nearby=" + allVillagers.size()
                + " professions=" + formatProfessionCounts(liveProfessions)
                + " hungry=" + hungry
                + " homeless=" + homeless
                + " bedless=" + bedless
                + " activeThoughts=" + activeThoughts);

        for (TekVillage village : manager.getVillages()) {
            List<MonsterEntity> threats = level.getEntitiesOfClass(
                    MonsterEntity.class,
                    village.getBounds().inflate(64.0D, 16.0D, 64.0D),
                    TekCommandEvents::isRaidThreat
            );
            lines.add("Village " + shortId(village.getId())
                    + " center=" + village.getCenter().toShortString()
                    + " radius=" + village.getRadius()
                    + " residents=" + village.getResidents().size()
                    + " deaths=" + village.getVillagerDeathCount()
                    + " visitors=" + village.getVisitorSpawnCount()
                    + " professions=" + village.getProfessionCounts()
                    + " raidActive=" + village.isRaidActive()
                    + " raidLevel=" + village.getRaidLevel()
                    + " threats=" + threats.size()
                    + " alert=" + (village.getLastAlertPos() == null ? "-" : village.getLastAlertPos().toShortString())
                    + " tradeTier=" + village.getTokenPriceTier()
                    + " sales=" + formatRecentSales(village));
        }

        for (String reservation : TekVillageEconomy.describeReservations(gameTime, 6)) {
            lines.add("Reservation " + reservation);
        }
        lines.add("Pens " + TekAnimalPens.describePens(level, structureManager));
        return lines;
    }

    private static String formatEnumCounts(Map<TekStructureType, Integer> counts) {
        if (counts.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (TekStructureType type : TekStructureType.values()) {
            int count = counts.getOrDefault(type, 0);
            if (count <= 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(type.name().toLowerCase()).append('=').append(count);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String formatProfessionCounts(Map<ProfessionType, Integer> counts) {
        if (counts.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (ProfessionType type : ProfessionType.values()) {
            if (type == ProfessionType.UNKNOWN) {
                continue;
            }
            int count = counts.getOrDefault(type, 0);
            if (count <= 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(type.getSerializedName()).append('=').append(count);
        }
        int unknown = counts.getOrDefault(ProfessionType.UNKNOWN, 0);
        if (unknown > 0) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append("unknown=").append(unknown);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String formatFarmerCarry(CompoundNBT data) {
        if (!data.contains(FARMER_CARRY_TAG, 10)) {
            return "-";
        }
        CompoundNBT carry = data.getCompound(FARMER_CARRY_TAG);
        if (carry.isEmpty()) {
            return "-";
        }
        List<String> keys = new ArrayList<>(carry.getAllKeys());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            int amount = carry.getInt(key);
            if (amount <= 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(key).append('=').append(amount);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String formatBlacksmithDemand(CompoundNBT data) {
        if (!data.contains(BLACKSMITH_DEMAND_TAG, 10)) {
            return "-";
        }
        CompoundNBT demand = data.getCompound(BLACKSMITH_DEMAND_TAG);
        StringBuilder sb = new StringBuilder();
        appendDemand(sb, demand, "helmet");
        appendDemand(sb, demand, "chestplate");
        appendDemand(sb, demand, "leggings");
        appendDemand(sb, demand, "boots");
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static void appendDemand(StringBuilder sb, CompoundNBT demand, String part) {
        int need = demand.getInt("need_" + part);
        int stock = demand.getInt("stock_" + part);
        int deficit = demand.getInt("deficit_" + part);
        if (sb.length() > 0) {
            sb.append(';');
        }
        sb.append(part).append('(').append(need).append('/').append(stock).append('/').append(deficit).append(')');
    }

    private static TekStructureStorage resolveStorageStructure(TekVillageStructureManager manager) {
        if (manager == null) {
            return null;
        }
        TekVillageStructure structure = manager.getStructure(TekStructureType.STORAGE).orElse(null);
        if (structure instanceof TekStructureStorage) {
            return (TekStructureStorage) structure;
        }
        return null;
    }

    private static String formatCraftableArmor(String material, int units) {
        int helmet = Math.max(0, units / 5);
        int chest = Math.max(0, units / 8);
        int legs = Math.max(0, units / 7);
        int boots = Math.max(0, units / 4);
        int sets = Math.max(0, units / 24);
        return material + "[h=" + helmet + ",c=" + chest + ",l=" + legs + ",b=" + boots + ",sets=" + sets + "]";
    }

    private static String formatGuardMissingSlots(int head, int chest, int legs, int feet, int weapon) {
        StringBuilder missing = new StringBuilder();
        appendMissingSlot(missing, head, "head");
        appendMissingSlot(missing, chest, "chest");
        appendMissingSlot(missing, legs, "legs");
        appendMissingSlot(missing, feet, "feet");
        appendMissingSlot(missing, weapon, "weapon");
        return missing.length() == 0 ? "-" : missing.toString();
    }

    private static void appendMissingSlot(StringBuilder missing, int score, String slot) {
        if (score > 0) {
            return;
        }
        if (missing.length() > 0) {
            missing.append(',');
        }
        missing.append(slot);
    }

    private static String formatItemId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "-";
        }
        Item item = stack.getItem();
        return item.getRegistryName() == null ? item.toString() : item.getRegistryName().toString();
    }

    private static boolean isRaidThreat(MonsterEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        if (entity instanceof ZombieEntity) {
            return true;
        }
        if (entity.getType().getRegistryName() == null) {
            return false;
        }
        String namespace = entity.getType().getRegistryName().getNamespace();
        String path = entity.getType().getRegistryName().getPath();
        if ("tektopia".equals(namespace)) {
            return path.contains("necromancer") || path.contains("spirit_skull") || path.contains("death_cloud");
        }
        return path.contains("pillager")
                || path.contains("vindicator")
                || path.contains("evoker")
                || path.contains("vex")
                || path.contains("ravager")
                || path.contains("witch")
                || path.contains("skeleton");
    }

    private static String shortId(java.util.UUID id) {
        String raw = id.toString();
        return raw.length() > 8 ? raw.substring(0, 8) : raw;
    }

    private static String applyVillagerField(TekVillagerEntity villager, String rawField, String rawValue) {
        String field = rawField == null ? "" : rawField.trim().toLowerCase();
        try {
            switch (field) {
                case "hunger":
                    villager.setHunger(Integer.parseInt(rawValue));
                    return "Set " + shortId(villager.getUUID()) + " hunger=" + villager.getHunger();
                case "happy":
                case "happiness":
                    villager.setHappy(Integer.parseInt(rawValue));
                    return "Set " + shortId(villager.getUUID()) + " happy=" + villager.getHappy();
                case "intelligence":
                case "intel":
                    villager.setIntelligence(Integer.parseInt(rawValue));
                    return "Set " + shortId(villager.getUUID()) + " intelligence=" + villager.getIntelligence();
                case "days":
                case "days_alive":
                case "daysalive":
                    villager.setDaysAlive(Integer.parseInt(rawValue));
                    return "Set " + shortId(villager.getUUID()) + " daysAlive=" + villager.getDaysAlive();
                case "profession":
                    ProfessionType profession = ProfessionType.fromSerializedName(rawValue);
                    if (profession == ProfessionType.UNKNOWN) {
                        return null;
                    }
                    villager.setProfessionType(profession);
                    return "Set " + shortId(villager.getUUID()) + " profession=" + profession.getSerializedName();
                case "status":
                case "work_status":
                    TekWorkerStatus status = TekWorkerStatus.fromSerializedName(rawValue);
                    villager.setWorkerStatus(status);
                    return "Set " + shortId(villager.getUUID()) + " status=" + status.getSerializedName();
                case "sleeping":
                    villager.setSleepingState(parseBoolean(rawValue));
                    return "Set " + shortId(villager.getUUID()) + " sleeping=" + villager.isSleepingState();
                case "sitting":
                    villager.setSittingState(parseBoolean(rawValue));
                    return "Set " + shortId(villager.getUUID()) + " sitting=" + villager.isSittingState();
                case "thought":
                    villager.setThoughtKey(rawValue);
                    return "Set " + shortId(villager.getUUID()) + " thought=" + rawValue;
                default:
                    return null;
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static boolean parseBoolean(String rawValue) {
        String normalized = rawValue == null ? "" : rawValue.trim().toLowerCase();
        return normalized.equals("1") || normalized.equals("true") || normalized.equals("yes") || normalized.equals("on");
    }

    private static String formatSkillSummary(TekVillagerEntity villager) {
        StringBuilder sb = new StringBuilder();
        for (ProfessionType professionType : ProfessionType.values()) {
            int skill = villager.getSkill(professionType);
            if (skill <= 0 || professionType == ProfessionType.UNKNOWN) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(professionType.getSerializedName()).append('=').append(skill);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String formatVillagerInventory(TekVillagerEntity villager) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack stack : villager.getVillagerInventorySnapshot()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(formatItemId(stack)).append('x').append(stack.getCount());
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String formatItemCounts(java.util.Map<Item, Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            return "-";
        }
        List<Item> items = new ArrayList<>(counts.keySet());
        items.sort(Comparator.comparing(item -> item.getRegistryName() == null ? item.toString() : item.getRegistryName().toString()));
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            int count = counts.getOrDefault(item, 0);
            if (count <= 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String itemId = item.getRegistryName() == null ? item.toString() : item.getRegistryName().toString();
            sb.append(itemId).append('=').append(count);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String formatRecentSales(TekVillage village) {
        if (village.getMerchantSaleHistory().isEmpty()) {
            return "-";
        }
        int limit = Math.min(8, village.getMerchantSaleHistory().size());
        return String.join(",", village.getMerchantSaleHistory().subList(0, limit));
    }

    private static TekVillagerEntity findNearestVillager(ServerPlayerEntity player) {
        List<TekVillagerEntity> villagers = player.level.getEntitiesOfClass(
                TekVillagerEntity.class,
                player.getBoundingBox().inflate(24.0D)
        );
        return villagers.stream()
                .min(Comparator.comparingDouble(villager -> villager.distanceToSqr(player)))
                .orElse(null);
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
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_FARMER_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_BLACKSMITH_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_MINER_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_LUMBERJACK_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_CHEF_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_RANCHER_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_BUTCHER_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_MERCHANT_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_NOMAD_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_ARCHITECT_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_TRADESMAN_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_CHILD_SPAWN_EGG.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_TEACHER_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_BARD_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_CLERIC_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_DRUID_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_ENCHANTER_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.TEK_NITWIT_SPAWN_EGG.get(), 1));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_TOWNHALL_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_STORAGE_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_HOME_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_HOME2_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_HOME4_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_HOME6_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_FARM_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_MINESHAFT_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_LUMBER_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_KITCHEN_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_BLACKSMITH_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_BUTCHER_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_RANCH_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_SHEEP_PEN_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_COW_PEN_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_PIG_PEN_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_CHICKEN_COOP_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_GUARD_POST_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_BARRACKS_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_MERCHANT_STALL_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_TAVERN_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_SCHOOL_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.STRUCTURE_LIBRARY_TOKEN.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.CHAIR.get(), 16));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.BEER.get(), 8));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(TekItems.HEART.get(), 2));
        player.inventory.placeItemBackInInventory(player.level, new ItemStack(Items.ITEM_FRAME, 32));
    }

    private static Entity createPortEntity(String rawType, ServerWorld level) {
        String type = rawType == null ? "" : rawType.trim().toLowerCase();
        switch (type) {
            case "guard":
                return TekEntities.TEK_GUARD.get().create(level);
            case "farmer":
                return TekEntities.TEK_FARMER.get().create(level);
            case "blacksmith":
                return TekEntities.TEK_BLACKSMITH.get().create(level);
            case "miner":
                return TekEntities.TEK_MINER.get().create(level);
            case "lumberjack":
            case "lumber":
                return TekEntities.TEK_LUMBERJACK.get().create(level);
            case "chef":
                return TekEntities.TEK_CHEF.get().create(level);
            case "rancher":
                return TekEntities.TEK_RANCHER.get().create(level);
            case "butcher":
                return TekEntities.TEK_BUTCHER.get().create(level);
            case "merchant":
                return TekEntities.TEK_MERCHANT.get().create(level);
            case "nomad":
                return TekEntities.TEK_NOMAD.get().create(level);
            case "architect":
                return TekEntities.TEK_ARCHITECT.get().create(level);
            case "tradesman":
            case "vendor":
                return TekEntities.TEK_TRADESMAN.get().create(level);
            case "child":
                return TekEntities.TEK_CHILD.get().create(level);
            case "teacher":
                return TekEntities.TEK_TEACHER.get().create(level);
            case "bard":
                return TekEntities.TEK_BARD.get().create(level);
            case "cleric":
                return TekEntities.TEK_CLERIC.get().create(level);
            case "druid":
                return TekEntities.TEK_DRUID.get().create(level);
            case "enchanter":
                return TekEntities.TEK_ENCHANTER.get().create(level);
            case "nitwit":
                return TekEntities.TEK_NITWIT.get().create(level);
            case "spirit_skull":
            case "skull":
                return TekEntities.TEK_SPIRIT_SKULL.get().create(level);
            case "death_cloud":
            case "cloud":
                return TekEntities.TEK_DEATH_CLOUD.get().create(level);
            case "necromancer":
            case "necro":
                return TekEntities.TEK_NECROMANCER.get().create(level);
            default:
                return null;
        }
    }

    private static int spawnRaidNearNearestVillage(ServerPlayerEntity player, int count) {
        TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(player.getLevel());
        TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
        if (village == null) {
            return 0;
        }
        ServerWorld level = player.getLevel();
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            ZombieEntity zombie = EntityType.ZOMBIE.create(level);
            if (zombie == null) {
                continue;
            }
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            double dist = village.getRadius() + 8.0D + level.random.nextDouble() * 6.0D;
            int spawnX = (int) Math.floor(village.getCenter().getX() + Math.cos(angle) * dist);
            int spawnZ = (int) Math.floor(village.getCenter().getZ() + Math.sin(angle) * dist);
            int spawnY = level.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawnX, spawnZ);
            BlockPos spawnPos = new BlockPos(spawnX, spawnY, spawnZ);
            zombie.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(zombie);
            spawned++;
        }
        if (spawned > 0) {
            village.setRaidActive(true);
            village.setAlert(player.blockPosition(), level.getGameTime());
            village.setRaidLevel(Math.max(village.getRaidLevel(), Math.max(1, count / 4)));
            TekVillageRuntime.get().saveRuntime(level);
        }
        return spawned;
    }

    private static int spawnEntityRaidNearNearestVillage(ServerPlayerEntity player, EntityType<? extends Entity> entityType, int count) {
        TekVillageManager manager = TekVillageRuntime.get().villageManagerFor(player.getLevel());
        TekVillage village = manager.findNearestVillage(player.blockPosition()).orElse(null);
        if (village == null) {
            return 0;
        }
        ServerWorld level = player.getLevel();
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            Entity entity = entityType.create(level);
            if (entity == null) {
                continue;
            }
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            double dist = village.getRadius() + 10.0D + level.random.nextDouble() * 8.0D;
            int spawnX = (int) Math.floor(village.getCenter().getX() + Math.cos(angle) * dist);
            int spawnZ = (int) Math.floor(village.getCenter().getZ() + Math.sin(angle) * dist);
            int spawnY = level.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawnX, spawnZ);
            BlockPos spawnPos = new BlockPos(spawnX, spawnY, spawnZ);
            entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(entity);
            spawned++;
        }
        if (spawned > 0) {
            village.setRaidActive(true);
            village.setAlert(player.blockPosition(), level.getGameTime());
            village.setRaidLevel(Math.max(village.getRaidLevel(), Math.max(1, count)));
            TekVillageRuntime.get().saveRuntime(level);
        }
        return spawned;
    }

}
