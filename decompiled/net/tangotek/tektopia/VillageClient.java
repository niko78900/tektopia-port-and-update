/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.util.math.BlockPos
 */
package net.tangotek.tektopia;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.Village;

public class VillageClient {
    private BlockPos center;
    private int villageSize;

    public VillageClient(ByteBuf buf) {
        this.center = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.villageSize = buf.readInt();
    }

    public VillageClient(Village village) {
        this.center = village.getOrigin();
        this.villageSize = village.getSize();
    }

    public int getMaxX() {
        return this.center.func_177958_n() + this.villageSize;
    }

    public int getMaxZ() {
        return this.center.func_177952_p() + this.villageSize;
    }

    public int getMinX() {
        return this.center.func_177958_n() - this.villageSize;
    }

    public int getMinZ() {
        return this.center.func_177952_p() - this.villageSize;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.center.func_177958_n());
        buf.writeInt(this.center.func_177956_o());
        buf.writeInt(this.center.func_177952_p());
        buf.writeInt(this.villageSize);
    }
}

