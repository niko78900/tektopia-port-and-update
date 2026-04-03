/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 */
package net.tangotek.tektopia.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.tangotek.tektopia.client.RenderVillager;
import net.tangotek.tektopia.entities.EntityBlacksmith;

public class RenderBlacksmith<T extends EntityBlacksmith>
extends RenderVillager<T> {
    public static final Factory FACTORY = new Factory();

    public RenderBlacksmith(RenderManager manager) {
        super(manager, "blacksmith", false, 64, 64, "blacksmith");
    }

    public static class Factory<T extends EntityBlacksmith>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderBlacksmith(manager);
        }
    }
}

