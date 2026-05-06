package net.tangotek.tektopia.network.message;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.tangotek.tektopia.client.TekClientSyncState;
import net.tangotek.tektopia.common.ProfessionType;
import net.tangotek.tektopia.entities.TekVillagerEntity;

public class PacketVillagerGuiSnapshot {
    private final CompoundNBT snapshot;

    public PacketVillagerGuiSnapshot(CompoundNBT snapshot) {
        this.snapshot = snapshot == null ? new CompoundNBT() : snapshot.copy();
    }

    public static void encode(PacketVillagerGuiSnapshot msg, PacketBuffer buf) {
        buf.writeNbt(msg.snapshot);
    }

    public static PacketVillagerGuiSnapshot decode(PacketBuffer buf) {
        return new PacketVillagerGuiSnapshot(buf.readNbt());
    }

    public static void handle(PacketVillagerGuiSnapshot msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    TekClientSyncState.setVillagerGuiSnapshot(msg.snapshot));
        });
        ctx.get().setPacketHandled(true);
    }

    public static PacketVillagerGuiSnapshot createSnapshot(TekVillagerEntity villager) {
        CompoundNBT snapshot = new CompoundNBT();
        if (villager == null) {
            snapshot.putInt("entityId", -1);
            return new PacketVillagerGuiSnapshot(snapshot);
        }
        snapshot.putInt("entityId", villager.getId());
        snapshot.putUUID("uuid", villager.getUUID());
        snapshot.putString("name", villager.getName().getString());
        snapshot.putString("profession", villager.getProfessionType().getSerializedName());
        snapshot.putString("status", villager.getWorkerStatus().getSerializedName());
        snapshot.putFloat("health", villager.getHealth());
        snapshot.putFloat("maxHealth", villager.getMaxHealth());
        snapshot.putInt("hunger", villager.getHunger());
        snapshot.putInt("happiness", villager.getHappy());
        snapshot.putInt("intelligence", villager.getIntelligence());
        snapshot.putInt("daysAlive", villager.getDaysAlive());
        snapshot.putString("thought", villager.getThoughtKey());
        snapshot.putString("itemThought", villager.getItemThoughtId());
        putBlockPos(snapshot, "home", villager.getHomePos());
        putBlockPos(snapshot, "bed", villager.getBedPos());

        CompoundNBT skills = new CompoundNBT();
        for (ProfessionType profession : ProfessionType.values()) {
            if (profession == ProfessionType.UNKNOWN) {
                continue;
            }
            int value = villager.getSkill(profession);
            if (value > 0) {
                skills.putInt(profession.getSerializedName(), value);
            }
        }
        snapshot.put("skills", skills);

        CompoundNBT filters = new CompoundNBT();
        for (String filter : villager.getAIFilters()) {
            filters.putBoolean(filter, villager.isAIFilterEnabled(filter));
        }
        snapshot.put("filters", filters);

        ListNBT inventory = new ListNBT();
        List<ItemStack> stacks = villager.getVillagerInventorySnapshot();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.putByte("Slot", (byte) i);
            stack.save(itemTag);
            inventory.add(itemTag);
        }
        snapshot.put("inventory", inventory);
        return new PacketVillagerGuiSnapshot(snapshot);
    }

    private static void putBlockPos(CompoundNBT tag, String key, BlockPos pos) {
        if (pos != null) {
            tag.putLong(key, pos.asLong());
        }
    }
}
