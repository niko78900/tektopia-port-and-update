package net.tangotek.tektopia.client;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.entities.TekDeathCloudEntity;
import net.tangotek.tektopia.entities.TekSpiritSkullEntity;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.registry.TekParticles;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, value = Dist.CLIENT)
public final class TekClientParticleEvents {
    private static final double PARTICLE_RANGE_SQR = 96.0D * 96.0D;
    private static int clientTicks;

    private TekClientParticleEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld level = minecraft.level;
        if (level == null || minecraft.player == null || minecraft.isPaused()) {
            return;
        }
        clientTicks++;
        for (Entity entity : level.entitiesForRendering()) {
            if (entity == null || !entity.isAlive() || entity.distanceToSqr(minecraft.player) > PARTICLE_RANGE_SQR) {
                continue;
            }
            if (entity instanceof TekVillagerEntity) {
                spawnVillagerThoughts(level, (TekVillagerEntity) entity);
            } else if (entity instanceof TekDeathCloudEntity) {
                spawnDarkness(level, entity);
            } else if (entity instanceof TekSpiritSkullEntity) {
                spawnSkull(level, entity);
            }
        }
    }

    private static void spawnVillagerThoughts(ClientWorld level, TekVillagerEntity villager) {
        int salt = villager.getId() & 31;
        double x = villager.getX();
        double y = villager.getY() + villager.getBbHeight() + 0.55D;
        double z = villager.getZ();
        if (!villager.getItemThoughtId().isEmpty() && shouldPulse(40, salt)) {
            level.addParticle(TekParticles.ITEM_THOUGHT.get(), x, y + 0.18D, z, 0.0D, 0.012D, 0.0D);
        } else if (!villager.getThoughtKey().isEmpty() && shouldPulse(40, salt)) {
            level.addParticle(TekParticles.THOUGHT.get(), x, y, z, 0.0D, 0.012D, 0.0D);
        }
    }

    private static void spawnDarkness(ClientWorld level, Entity entity) {
        if (!shouldPulse(6, entity.getId() & 7)) {
            return;
        }
        Random random = level.random;
        double x = entity.getX() + (random.nextDouble() - 0.5D) * entity.getBbWidth();
        double z = entity.getZ() + (random.nextDouble() - 0.5D) * entity.getBbWidth();
        double y = entity.getY() + 0.15D + random.nextDouble() * Math.max(0.2D, entity.getBbHeight());
        level.addParticle(TekParticles.DARKNESS.get(), x, y, z, 0.006D + random.nextDouble() * 0.012D, 0.006D, 0.15D + random.nextDouble() * 0.18D);
    }

    private static void spawnSkull(ClientWorld level, Entity entity) {
        if (!shouldPulse(8, entity.getId() & 7)) {
            return;
        }
        Random random = level.random;
        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() * 0.5D;
        double z = entity.getZ();
        level.addParticle(TekParticles.SKULL.get(), x, y, z, 0.004D + random.nextDouble() * 0.008D, 0.012D, 0.18D + random.nextDouble() * 0.18D);
    }

    private static boolean shouldPulse(int interval, int salt) {
        return (clientTicks + salt) % interval == 0;
    }
}
