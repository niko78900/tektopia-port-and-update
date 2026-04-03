/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 */
package net.tangotek.tektopia.pathing;

import net.minecraft.entity.player.EntityPlayerMP;
import net.tangotek.tektopia.pathing.PathingGraph;

public class PathingGraphListener {
    private EntityPlayerMP playerMP;

    public PathingGraphListener(EntityPlayerMP player) {
        this.playerMP = player;
    }

    public void update(PathingGraph graph) {
    }

    public boolean isPlayer(EntityPlayerMP player) {
        return player == this.playerMP;
    }
}

