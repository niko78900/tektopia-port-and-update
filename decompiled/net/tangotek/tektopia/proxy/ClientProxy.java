/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.stream.JsonWriter
 *  com.leviathanstudio.craftstudio.client.exception.CSMalformedJsonException
 *  com.leviathanstudio.craftstudio.client.json.CSJsonReader
 *  com.leviathanstudio.craftstudio.client.json.CSReadedModel
 *  com.leviathanstudio.craftstudio.client.json.CSReadedModelBlock
 *  com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper
 *  com.leviathanstudio.craftstudio.client.util.EnumRenderType
 *  com.leviathanstudio.craftstudio.client.util.EnumResourceType
 *  com.leviathanstudio.craftstudio.common.animation.AnimationHandler
 *  com.leviathanstudio.craftstudio.common.animation.IAnimated
 *  com.sun.javafx.geom.Vec3f
 *  javax.vecmath.Vector3f
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.resources.IResource
 *  net.minecraft.item.Item
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 */
package net.tangotek.tektopia.proxy;

import com.google.gson.stream.JsonWriter;
import com.leviathanstudio.craftstudio.client.exception.CSMalformedJsonException;
import com.leviathanstudio.craftstudio.client.json.CSJsonReader;
import com.leviathanstudio.craftstudio.client.json.CSReadedModel;
import com.leviathanstudio.craftstudio.client.json.CSReadedModelBlock;
import com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper;
import com.leviathanstudio.craftstudio.client.util.EnumRenderType;
import com.leviathanstudio.craftstudio.client.util.EnumResourceType;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.sun.javafx.geom.Vec3f;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.vecmath.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.tangotek.tektopia.ModEntities;
import net.tangotek.tektopia.VillageClient;
import net.tangotek.tektopia.client.VillageBorderRenderer;
import net.tangotek.tektopia.pathing.PathingNodeClient;
import net.tangotek.tektopia.pathing.PathingOverlayRenderer;
import net.tangotek.tektopia.proxy.CommonProxy;
import net.tangotek.tektopia.proxy.TekClientAnimationHandler;

public class ClientProxy
extends CommonProxy {
    public static final Integer PARTICLE_OFFSET = 1400;
    public static final Integer PARTICLE_THOUGHT = PARTICLE_OFFSET + 1;
    private PathingOverlayRenderer overlayRenderer = new PathingOverlayRenderer();
    private VillageBorderRenderer villageBorderRenderer = new VillageBorderRenderer();
    private VillageClient villageClient = null;
    CSRegistryHelper registry = new CSRegistryHelper("tektopia");

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModEntities.initModels();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public <T extends IAnimated> AnimationHandler<T> getNewAnimationHandler(Class<T> animatedClass) {
        return new TekClientAnimationHandler();
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation((Item)item, (int)meta, (ModelResourceLocation)new ModelResourceLocation("tektopia:" + id, "inventory"));
    }

    @Override
    public void registerModels() {
        super.registerModels();
        CSRegistryHelper registry = new CSRegistryHelper("tektopia");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "lumberjack_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "farmer_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "farmer_f");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "miner_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "blacksmith_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "guard_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "merchant_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "rancher_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "butcher_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "architect_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "tradesman_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "enchanter_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "child_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "child_f");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "chef_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "chef_f");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "teacher_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "nitwit_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "druid_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "druid_f");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "cleric_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "cleric_f");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "nomad_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "bard_m");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "necromancer");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "rancher_hat");
        this.registerBlock("chair");
    }

    public String toString() {
        return super.toString();
    }

    @Override
    public void handleNodeUpdate(PathingNodeClient node) {
        this.overlayRenderer.handleNodeUpdate(node);
    }

    @Override
    public void handleVillage(VillageClient vc) {
        this.villageBorderRenderer.updateVillage(vc);
    }

    private void registerBlock(String resourceNameIn) {
        this.registry.register(EnumResourceType.MODEL, EnumRenderType.BLOCK, resourceNameIn);
        ResourceLocation res = new ResourceLocation("tektopia", "models/block/" + resourceNameIn + ".json");
        try {
            IResource iResource = Minecraft.func_71410_x().func_110442_L().func_110536_a(res);
        }
        catch (IOException var5) {
            this.convertBlock(resourceNameIn);
        }
    }

    private void convertBlock(String resourceNameIn) {
        try {
            ResourceLocation resOld = new ResourceLocation("tektopia", EnumResourceType.MODEL.getPath() + EnumRenderType.BLOCK.getFolderName() + resourceNameIn + EnumResourceType.MODEL.getExtension());
            CSJsonReader jsonReader = new CSJsonReader(resOld);
            CSReadedModel model = jsonReader.readModel();
            File outFile = new File(Minecraft.func_71410_x().field_71412_D, "../src/main/resources/assets/tektopia/models/block/" + resourceNameIn + ".json");
            if (!outFile.exists()) {
                JsonWriter writer = new JsonWriter((Writer)new FileWriter(outFile));
                writer.setIndent("    ");
                writer.beginObject();
                String textureName = resourceNameIn + "_tex";
                writer.name("textures").beginObject();
                writer.name("particle").value("tektopia:blocks/" + resourceNameIn);
                writer.name(textureName).value("tektopia:blocks/" + resourceNameIn);
                writer.endObject();
                writer.name("elements").beginArray();
                for (CSReadedModelBlock block : model.getParents()) {
                    this.writeBlockElement(writer, block, textureName, new Vector3f(8.0f, 24.0f, 8.0f));
                }
                writer.endArray();
                writer.endObject();
                writer.close();
            }
        }
        catch (CSMalformedJsonException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeBlockElement(JsonWriter writer, CSReadedModelBlock block, String textureName, Vector3f offset) throws IOException {
        writer.beginObject();
        writer.name("name").value(block.getName());
        offset.x += block.getRotationPoint().x;
        offset.y -= block.getRotationPoint().y;
        offset.z -= block.getRotationPoint().z;
        Vector3f size = block.getSize();
        size.y = -size.y;
        size.z = -size.z;
        writer.name("from").beginArray();
        Vec3f from = new Vec3f(offset.x - size.x / 2.0f, offset.y - size.y / 2.0f, offset.z - size.z / 2.0f);
        writer.value((double)from.x).value((double)from.y).value((double)from.z);
        writer.endArray();
        writer.name("to").beginArray();
        writer.value((double)(from.x + size.x)).value((double)(from.y + size.y)).value((double)(from.z + size.z));
        writer.endArray();
        writer.name("faces").beginObject();
        this.writeFace(writer, "up", 0.0f, 0.0f, size.x, size.z, textureName);
        this.writeFace(writer, "down", 0.0f, 0.0f, size.x, size.z, textureName);
        this.writeFace(writer, "west", 0.0f, 0.0f, size.z, size.y, textureName);
        this.writeFace(writer, "south", 0.0f, 0.0f, size.x, size.y, textureName);
        this.writeFace(writer, "east", 0.0f, 0.0f, size.z, size.y, textureName);
        this.writeFace(writer, "north", 0.0f, 0.0f, size.x, size.y, textureName);
        writer.endObject();
        writer.endObject();
        for (CSReadedModelBlock child : block.getChilds()) {
            this.writeBlockElement(writer, child, textureName, offset);
        }
    }

    private void writeFace(JsonWriter writer, String name, float u1, float v1, float w, float h, String textureName) throws IOException {
        writer.name(name).beginObject();
        writer.name("uv").beginArray();
        writer.value((double)u1).value((double)v1).value((double)(u1 + w)).value((double)(v1 + h));
        writer.endArray();
        writer.name("texture").value("#" + textureName);
        writer.endObject();
    }

    @Override
    public void registerAnims() {
        super.registerAnims();
        CSRegistryHelper registry = new CSRegistryHelper("tektopia");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_walk");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_chop");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_thor_jump");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_hoe");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_pickup");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_eat");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_hammer");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_sleep");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_sit");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_sit_cheer");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_take");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_summon");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_salute");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_skip");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_run");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_cook");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_teach");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_sit_raise");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_cast_forward");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_flute_1");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_walk_sad");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_read");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_cast_grow");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_cast_bless");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "villager_craft");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "necro_summon");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "necro_walk");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "necro_idle");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "necro_siphon");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "necro_cast_forward");
    }
}

