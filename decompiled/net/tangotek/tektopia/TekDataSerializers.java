/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 */
package net.tangotek.tektopia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public class TekDataSerializers {
    public static final DataSerializer<List<Integer>> INT_LIST = new DataSerializer<List<Integer>>(){

        public void write(PacketBuffer buf, List<Integer> list) {
            buf.func_150787_b(list.size());
            list.stream().forEach(i -> buf.func_150787_b(i.intValue()));
        }

        public List<Integer> read(PacketBuffer buf) throws IOException {
            int i = buf.func_150792_a();
            ArrayList<Integer> outList = new ArrayList<Integer>();
            for (int j = 0; j < i; ++j) {
                outList.add(buf.func_150792_a());
            }
            return outList;
        }

        public DataParameter<List<Integer>> func_187161_a(int id) {
            return new DataParameter(id, (DataSerializer)this);
        }

        public List<Integer> copyValue(List<Integer> value) {
            return value;
        }
    };

    static {
        DataSerializers.func_187189_a(INT_LIST);
    }
}

