package net.tangotek.tektopia.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.tangotek.tektopia.network.message.PacketAIFilter;
import net.tangotek.tektopia.network.message.PacketLicense;
import net.tangotek.tektopia.network.message.PacketPathingNode;
import net.tangotek.tektopia.network.message.PacketStorageGuiSnapshot;
import net.tangotek.tektopia.network.message.PacketTradeAction;
import net.tangotek.tektopia.network.message.PacketTradeGuiSnapshot;
import net.tangotek.tektopia.network.message.PacketVillage;
import net.tangotek.tektopia.network.message.PacketVillageGuiSnapshot;
import net.tangotek.tektopia.network.message.PacketVillagerGuiSnapshot;
import net.tangotek.tektopia.network.message.PacketVillagerItemThought;
import net.tangotek.tektopia.network.message.PacketVillagerThought;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.common.TekStorageGuiSnapshotReport;
import net.tangotek.tektopia.common.TekTradeGuiSnapshotReport;
import net.tangotek.tektopia.common.TekVillageGuiSnapshotReport;
import net.tangotek.tektopia.common.TekVillagerGuiSnapshotReport;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.village.TekVillage;
import net.tangotek.tektopia.village.TekVillageRuntime;
import net.tangotek.tektopia.village.TekVillageStructureManager;

public final class TekNetwork {
    private static final String PROTOCOL_VERSION = "5";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TekTopiaPort.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean initialized = false;

    private TekNetwork() {
    }

    public static void register() {
        if (initialized) {
            return;
        }
        initialized = true;
        int id = 0;
        CHANNEL.registerMessage(id++, PacketAIFilter.class, PacketAIFilter::encode, PacketAIFilter::decode, PacketAIFilter::handle);
        CHANNEL.registerMessage(id++, PacketLicense.class, PacketLicense::encode, PacketLicense::decode, PacketLicense::handle);
        CHANNEL.registerMessage(id++, PacketPathingNode.class, PacketPathingNode::encode, PacketPathingNode::decode, PacketPathingNode::handle);
        CHANNEL.registerMessage(id++, PacketTradeAction.class, PacketTradeAction::encode, PacketTradeAction::decode, PacketTradeAction::handle);
        CHANNEL.registerMessage(id++, PacketTradeGuiSnapshot.class, PacketTradeGuiSnapshot::encode, PacketTradeGuiSnapshot::decode, PacketTradeGuiSnapshot::handle);
        CHANNEL.registerMessage(id++, PacketVillage.class, PacketVillage::encode, PacketVillage::decode, PacketVillage::handle);
        CHANNEL.registerMessage(id++, PacketVillageGuiSnapshot.class, PacketVillageGuiSnapshot::encode, PacketVillageGuiSnapshot::decode, PacketVillageGuiSnapshot::handle);
        CHANNEL.registerMessage(id++, PacketStorageGuiSnapshot.class, PacketStorageGuiSnapshot::encode, PacketStorageGuiSnapshot::decode, PacketStorageGuiSnapshot::handle);
        CHANNEL.registerMessage(id++, PacketVillagerGuiSnapshot.class, PacketVillagerGuiSnapshot::encode, PacketVillagerGuiSnapshot::decode, PacketVillagerGuiSnapshot::handle);
        CHANNEL.registerMessage(id++, PacketVillagerItemThought.class, PacketVillagerItemThought::encode, PacketVillagerItemThought::decode, PacketVillagerItemThought::handle);
        CHANNEL.registerMessage(id++, PacketVillagerThought.class, PacketVillagerThought::encode, PacketVillagerThought::decode, PacketVillagerThought::handle);
        TekTopiaPort.LOGGER.info("TekTopia network channel initialized");
    }

    public static String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static <MSG> void sendToServer(MSG msg) {
        CHANNEL.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayerEntity player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG> void sendToAll(MSG msg) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToTracking(MSG msg, Entity tracked) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> tracked), msg);
    }

    public static <MSG> void sendToNearby(MSG msg, RegistryKey<World> dimension, double x, double y, double z, double radius) {
        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(x, y, z, radius, dimension);
        CHANNEL.send(PacketDistributor.NEAR.with(() -> point), msg);
    }

    public static void sendVillageState(ServerWorld level, TekVillage village, TekVillageStructureManager structureManager) {
        if (level == null || village == null) {
            return;
        }
        sendToNearby(
                PacketVillage.from(village, structureManager, level.getGameTime()),
                level.dimension(),
                village.getCenter().getX() + 0.5D,
                village.getCenter().getY() + 0.5D,
                village.getCenter().getZ() + 0.5D,
                Math.max(96.0D, village.getRadius() + 32.0D)
        );
    }

    public static void sendVillageStateToPlayer(ServerPlayerEntity player, TekVillage village, TekVillageStructureManager structureManager) {
        if (player == null || village == null) {
            return;
        }
        sendToPlayer(PacketVillage.from(village, structureManager, player.getLevel().getGameTime()), player);
        sendVillageGuiSnapshots(player, village, structureManager);
    }

    public static void sendVillagerState(ServerWorld level, TekVillagerEntity villager) {
        if (level == null || villager == null) {
            return;
        }
        sendToNearby(
                new PacketVillagerThought(
                        villager.getId(),
                        villager.getThoughtKey(),
                        villager.getWorkerStatus().getSerializedName(),
                        villager.getProfessionType().getSerializedName()
                ),
                level.dimension(),
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                96.0D
        );
        sendToNearby(
                new PacketVillagerItemThought(villager.getId(), villager.getItemThoughtId()),
                level.dimension(),
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                96.0D
        );
    }

    public static void sendVillagerStateToPlayer(ServerPlayerEntity player, TekVillagerEntity villager) {
        if (player == null || villager == null) {
            return;
        }
        sendToPlayer(
                new PacketVillagerThought(
                        villager.getId(),
                        villager.getThoughtKey(),
                        villager.getWorkerStatus().getSerializedName(),
                        villager.getProfessionType().getSerializedName()
                ),
                player
        );
        sendToPlayer(new PacketVillagerItemThought(villager.getId(), villager.getItemThoughtId()), player);
        sendVillagerGuiSnapshot(player, villager);
    }

    public static void sendVillagerGuiSnapshot(ServerPlayerEntity player, TekVillagerEntity villager) {
        if (player == null || villager == null) {
            return;
        }
        TekVillage village = villager.level instanceof ServerWorld
                ? TekVillageRuntime.get().villageManagerFor((ServerWorld) villager.level).findNearestVillage(villager.blockPosition()).orElse(null)
                : null;
        sendToPlayer(
                PacketVillagerGuiSnapshot.from(villager, TekVillagerGuiSnapshotReport.format(villager, village), player.getLevel().getGameTime()),
                player
        );
        if (village != null && villager.level instanceof ServerWorld) {
            sendVillageGuiSnapshots(player, village, TekVillageRuntime.get().managerFor((ServerWorld) villager.level));
        }
    }

    public static void sendVillageGuiSnapshots(ServerPlayerEntity player, TekVillage village, TekVillageStructureManager structureManager) {
        if (player == null || village == null) {
            return;
        }
        ServerWorld level = player.getLevel();
        String villageId = village.getId().toString();
        long gameTime = level.getGameTime();
        sendToPlayer(
                new PacketVillageGuiSnapshot(
                        villageId,
                        TekVillageGuiSnapshotReport.format(level, village, structureManager),
                        gameTime
                ),
                player
        );
        sendToPlayer(
                new PacketStorageGuiSnapshot(
                        villageId,
                        TekStorageGuiSnapshotReport.format(level, village, structureManager),
                        gameTime
                ),
                player
        );
        sendToPlayer(
                new PacketTradeGuiSnapshot(
                        villageId,
                        TekTradeGuiSnapshotReport.format(player, village, level),
                        gameTime
                ),
                player
        );
    }
}
