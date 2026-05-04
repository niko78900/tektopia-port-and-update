package net.tangotek.tektopia.village;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.common.TekWorkerStatus;
import net.tangotek.tektopia.entities.TekVillagerEntity;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.network.message.PacketPathingNode;

public final class TekWorkerNavigator {
    private static final String NAV_TARGET_TAG = "tek_nav_target";
    private static final String NAV_LAST_POS_TAG = "tek_nav_last_pos";
    private static final String NAV_STUCK_TICKS_TAG = "tek_nav_stuck_ticks";
    private static final String NAV_RETRY_TAG = "tek_nav_retries";
    private static final String NAV_FAILURE_TAG = "tek_nav_failure";
    private static final String NAV_LAST_SENT_TARGET_TAG = "tek_nav_last_sent_target";
    private static final int STUCK_LIMIT = 4;
    private static final int RETRY_LIMIT = 3;

    private TekWorkerNavigator() {
    }

    public static NavigationResult moveTo(
            ServerWorld level,
            TekVillagerEntity worker,
            CompoundNBT data,
            String workerTargetTag,
            String cooldownTag,
            BlockPos target,
            double speed,
            double reachSqr,
            long gameTime,
            long stepCooldown,
            long retryCooldown,
            String reason
    ) {
        if (worker == null || data == null || target == null) {
            return NavigationResult.FAILED;
        }
        long targetValue = target.asLong();
        if (!data.contains(NAV_TARGET_TAG, 4) || data.getLong(NAV_TARGET_TAG) != targetValue) {
            data.putLong(NAV_TARGET_TAG, targetValue);
            data.remove(NAV_LAST_POS_TAG);
            data.remove(NAV_STUCK_TICKS_TAG);
            data.remove(NAV_RETRY_TAG);
            data.remove(NAV_FAILURE_TAG);
        }
        if (workerTargetTag != null && !workerTargetTag.isEmpty()) {
            data.putLong(workerTargetTag, targetValue);
        }

        double targetX = target.getX() + 0.5D;
        double targetY = target.getY();
        double targetZ = target.getZ() + 0.5D;
        if (worker.distanceToSqr(targetX, targetY, targetZ) <= reachSqr) {
            clearNavigation(data, workerTargetTag);
            sendPathClear(level, worker);
            return NavigationResult.REACHED;
        }

        worker.setWorkerStatus(TekWorkerStatus.MOVING);
        boolean accepted = worker.getNavigation().moveTo(targetX, targetY, targetZ, speed);
        if (!accepted) {
            data.putInt(NAV_RETRY_TAG, data.getInt(NAV_RETRY_TAG) + 1);
        }
        if (isStuck(worker, data)) {
            data.putInt(NAV_RETRY_TAG, data.getInt(NAV_RETRY_TAG) + 1);
        }

        if (data.getInt(NAV_RETRY_TAG) >= RETRY_LIMIT || data.getInt(NAV_STUCK_TICKS_TAG) >= STUCK_LIMIT) {
            failNavigation(level, worker, data, workerTargetTag, cooldownTag, target, gameTime, retryCooldown, reason);
            return NavigationResult.FAILED;
        }

        if (cooldownTag != null && !cooldownTag.isEmpty()) {
            data.putLong(cooldownTag, gameTime + Math.max(1L, stepCooldown));
        }
        sendPathUpdateIfChanged(level, worker, data, target, reason);
        return NavigationResult.MOVING;
    }

    public static String getLastFailure(CompoundNBT data) {
        return data == null ? "" : data.getString(NAV_FAILURE_TAG);
    }

    public static void clearNavigation(CompoundNBT data, String workerTargetTag) {
        data.remove(NAV_TARGET_TAG);
        data.remove(NAV_LAST_POS_TAG);
        data.remove(NAV_STUCK_TICKS_TAG);
        data.remove(NAV_RETRY_TAG);
        data.remove(NAV_FAILURE_TAG);
        data.remove(NAV_LAST_SENT_TARGET_TAG);
        if (workerTargetTag != null && !workerTargetTag.isEmpty()) {
            data.remove(workerTargetTag);
        }
    }

    private static boolean isStuck(TekVillagerEntity worker, CompoundNBT data) {
        long currentPos = worker.blockPosition().asLong();
        if (data.contains(NAV_LAST_POS_TAG, 4) && data.getLong(NAV_LAST_POS_TAG) == currentPos) {
            data.putInt(NAV_STUCK_TICKS_TAG, data.getInt(NAV_STUCK_TICKS_TAG) + 1);
            return true;
        }
        data.putLong(NAV_LAST_POS_TAG, currentPos);
        data.putInt(NAV_STUCK_TICKS_TAG, 0);
        return false;
    }

    private static void failNavigation(
            ServerWorld level,
            TekVillagerEntity worker,
            CompoundNBT data,
            String workerTargetTag,
            String cooldownTag,
            BlockPos target,
            long gameTime,
            long retryCooldown,
            String reason
    ) {
        String failure = reason == null || reason.isEmpty() ? "unreachable" : reason;
        data.putString(NAV_FAILURE_TAG, failure);
        data.putString(TekVillageManager.WORKER_LAST_RESULT_TAG, failure);
        if (cooldownTag != null && !cooldownTag.isEmpty()) {
            data.putLong(cooldownTag, gameTime + Math.max(20L, retryCooldown));
        }
        if (workerTargetTag != null && !workerTargetTag.isEmpty()) {
            data.remove(workerTargetTag);
        }
        data.remove(NAV_TARGET_TAG);
        data.remove(NAV_LAST_POS_TAG);
        data.remove(NAV_STUCK_TICKS_TAG);
        data.remove(NAV_RETRY_TAG);
        worker.setWorkerStatus(TekWorkerStatus.BLOCKED);
        sendPathUpdate(level, worker, target, failure, false);
    }

    private static void sendPathUpdateIfChanged(ServerWorld level, TekVillagerEntity worker, CompoundNBT data, BlockPos target, String reason) {
        long targetValue = target.asLong();
        if (data.contains(NAV_LAST_SENT_TARGET_TAG, 4) && data.getLong(NAV_LAST_SENT_TARGET_TAG) == targetValue) {
            return;
        }
        data.putLong(NAV_LAST_SENT_TARGET_TAG, targetValue);
        sendPathUpdate(level, worker, target, reason, false);
    }

    private static void sendPathClear(ServerWorld level, TekVillagerEntity worker) {
        if (level == null || worker == null) {
            return;
        }
        TekNetwork.sendToNearby(
                new PacketPathingNode(worker.getId(), worker.blockPosition(), "", true),
                level.dimension(),
                worker.getX(),
                worker.getY(),
                worker.getZ(),
                96.0D
        );
    }

    private static void sendPathUpdate(ServerWorld level, TekVillagerEntity worker, BlockPos target, String reason, boolean clear) {
        if (level == null || worker == null || target == null) {
            return;
        }
        TekNetwork.sendToNearby(
                new PacketPathingNode(worker.getId(), target, reason, clear),
                level.dimension(),
                worker.getX(),
                worker.getY(),
                worker.getZ(),
                96.0D
        );
    }

    public enum NavigationResult {
        REACHED,
        MOVING,
        FAILED
    }
}
