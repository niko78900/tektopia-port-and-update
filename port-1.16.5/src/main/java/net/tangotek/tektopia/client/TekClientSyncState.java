package net.tangotek.tektopia.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, value = Dist.CLIENT)
public final class TekClientSyncState {
    private static final long THOUGHT_TTL_TICKS = 100L;
    private static final Map<Integer, TimedValue> THOUGHTS = new HashMap<>();
    private static final Map<Integer, TimedValue> ITEM_THOUGHTS = new HashMap<>();
    private static final Map<Integer, CompoundNBT> VILLAGER_GUI_SNAPSHOTS = new HashMap<>();
    private static final Map<UUID, String> LICENSES = new HashMap<>();

    private static CompoundNBT latestVillageSnapshot = new CompoundNBT();
    private static CompoundNBT latestPathingSnapshot = new CompoundNBT();
    private static boolean pathingOverlayClearRequested;

    private TekClientSyncState() {
    }

    public static void setVillagerThought(int entityId, String thoughtKey) {
        updateTimedValue(THOUGHTS, entityId, thoughtKey);
    }

    public static void setVillagerItemThought(int entityId, String itemId) {
        updateTimedValue(ITEM_THOUGHTS, entityId, itemId);
    }

    public static void setVillageSnapshot(CompoundNBT snapshot) {
        latestVillageSnapshot = snapshot == null ? new CompoundNBT() : snapshot.copy();
    }

    public static CompoundNBT getLatestVillageSnapshot() {
        return latestVillageSnapshot.copy();
    }

    public static void setVillagerGuiSnapshot(CompoundNBT snapshot) {
        if (snapshot == null || snapshot.getInt("entityId") < 0) {
            return;
        }
        VILLAGER_GUI_SNAPSHOTS.put(snapshot.getInt("entityId"), snapshot.copy());
    }

    public static CompoundNBT getVillagerGuiSnapshot(int entityId) {
        CompoundNBT snapshot = VILLAGER_GUI_SNAPSHOTS.get(entityId);
        return snapshot == null ? new CompoundNBT() : snapshot.copy();
    }

    public static void setPathingSnapshot(boolean clearOnly, CompoundNBT payload) {
        pathingOverlayClearRequested = clearOnly;
        latestPathingSnapshot = payload == null ? new CompoundNBT() : payload.copy();
    }

    public static boolean consumePathingOverlayClearRequested() {
        boolean result = pathingOverlayClearRequested;
        pathingOverlayClearRequested = false;
        return result;
    }

    public static CompoundNBT getLatestPathingSnapshot() {
        return latestPathingSnapshot.copy();
    }

    public static void setLicense(UUID playerId, String licenseData) {
        if (playerId == null || licenseData == null || licenseData.isEmpty()) {
            return;
        }
        LICENSES.put(playerId, licenseData);
    }

    public static String getLicense(UUID playerId) {
        return LICENSES.get(playerId);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            THOUGHTS.clear();
            ITEM_THOUGHTS.clear();
            VILLAGER_GUI_SNAPSHOTS.clear();
            LICENSES.clear();
            latestVillageSnapshot = new CompoundNBT();
            latestPathingSnapshot = new CompoundNBT();
            pathingOverlayClearRequested = false;
            return;
        }
        long gameTime = minecraft.level.getGameTime();
        tickThoughtParticles(minecraft, THOUGHTS, gameTime, false);
        tickThoughtParticles(minecraft, ITEM_THOUGHTS, gameTime, true);
    }

    private static void updateTimedValue(Map<Integer, TimedValue> values, int entityId, String value) {
        if (value == null || value.isEmpty()) {
            values.remove(entityId);
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        long now = minecraft.level == null ? 0L : minecraft.level.getGameTime();
        values.put(entityId, new TimedValue(value, now + THOUGHT_TTL_TICKS));
    }

    private static void tickThoughtParticles(Minecraft minecraft, Map<Integer, TimedValue> values, long gameTime, boolean itemThought) {
        Iterator<Map.Entry<Integer, TimedValue>> iter = values.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, TimedValue> entry = iter.next();
            if (entry.getValue().expiresAt < gameTime) {
                iter.remove();
                continue;
            }
            if (gameTime % 20L != 0L) {
                continue;
            }
            Entity entity = minecraft.level.getEntity(entry.getKey());
            if (entity == null || !entity.isAlive()) {
                iter.remove();
                continue;
            }
            double x = entity.getX();
            double y = entity.getY() + entity.getBbHeight() + 0.35D;
            double z = entity.getZ();
            if (itemThought) {
                Item item = null;
                try {
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getValue().value));
                } catch (IllegalArgumentException ignored) {
                    iter.remove();
                    continue;
                }
                if (item != null) {
                    minecraft.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(item)), x, y, z, 0.0D, 0.05D, 0.0D);
                }
            } else {
                minecraft.level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0D, 0.05D, 0.0D);
            }
        }
    }

    private static final class TimedValue {
        private final String value;
        private final long expiresAt;

        private TimedValue(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }
}
