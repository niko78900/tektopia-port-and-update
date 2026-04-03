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
import net.tangotek.tektopia.entities.EntityArchitect;

public class RenderArchitect<T extends EntityArchitect>
extends RenderVillager<T> {
    public static final Factory FACTORY = new Factory();

    public RenderArchitect(RenderManager manager) {
        super(manager, "architect", false, 64, 64, "architect");
    }

    public static class Factory<T extends EntityArchitect>
    implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderArchitect(manager);
        }
    }
}

