package net.tangotek.tektopia.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.RegistryObject;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.client.animation.TekCraftStudioAnimationLibrary;
import net.tangotek.tektopia.client.animation.TekCraftStudioRenderer;
import net.tangotek.tektopia.client.animation.TekVisualAssetInventory;
import net.tangotek.tektopia.client.gui.TekVillagerScreen;
import net.tangotek.tektopia.client.particle.TekLegacyParticles;
import net.tangotek.tektopia.registry.TekContainers;
import net.tangotek.tektopia.registry.TekEntities;
import net.tangotek.tektopia.registry.TekParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.ScreenManager;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TekTopiaPort.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TekClientEvents {
    public static KeyBinding STATUS_KEY;
    private static final ResourceLocation VILLAGER_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/villager/villager.png");
    private static final ResourceLocation ZOMBIE_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/zombie/zombie.png");
    private TekClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            STATUS_KEY = new KeyBinding("key.tektopia.status", GLFW.GLFW_KEY_O, "key.categories.tektopia");
            ClientRegistry.registerKeyBinding(STATUS_KEY);
            ScreenManager.register(TekContainers.TEK_VILLAGER.get(), TekVillagerScreen::new);
            TekVisualAssetInventory.logClientCoverage();
            TekCraftStudioAnimationLibrary.logCoverage();
            registerCraft(TekEntities.TEK_GUARD, "guard_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_FARMER, "farmer_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_BLACKSMITH, "blacksmith_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_MINER, "miner_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_LUMBERJACK, "lumberjack_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_CHEF, "chef_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_RANCHER, "rancher_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_BUTCHER, "butcher_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_MERCHANT, "merchant_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_NOMAD, "nomad_m", "nomad0_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_NECROMANCER, "necromancer", ZOMBIE_TEXTURE);
            registerCraft(TekEntities.TEK_ARCHITECT, "architect_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_TRADESMAN, "tradesman_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_CHILD, "child_m", "child0_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_TEACHER, "teacher_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_BARD, "bard_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_CLERIC, "cleric_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_DRUID, "druid_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_ENCHANTER, "enchanter_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_NITWIT, "nitwit_m", VILLAGER_TEXTURE);
            registerCraft(TekEntities.TEK_SPIRIT_SKULL, "tek_spirit_skull", "spirit_skull", ZOMBIE_TEXTURE);
            registerCraft(TekEntities.TEK_DEATH_CLOUD, "tek_death_cloud", ZOMBIE_TEXTURE);
            registerCraft(TekEntities.TEK_CAPTAIN_AURA, "tek_captain_aura", VILLAGER_TEXTURE);
        });
    }

    @SubscribeEvent
    public static void onParticleFactoryRegister(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(
                TekParticles.THOUGHT.get(),
                sprites -> (data, level, x, y, z, xSpeed, ySpeed, zSpeed) ->
                        TekLegacyParticles.thought(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites)
        );
        Minecraft.getInstance().particleEngine.register(
                TekParticles.ITEM_THOUGHT.get(),
                sprites -> (data, level, x, y, z, xSpeed, ySpeed, zSpeed) ->
                        TekLegacyParticles.itemThought(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites)
        );
        Minecraft.getInstance().particleEngine.register(
                TekParticles.DARKNESS.get(),
                sprites -> (data, level, x, y, z, xSpeed, ySpeed, zSpeed) ->
                        TekLegacyParticles.darkness(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites)
        );
        Minecraft.getInstance().particleEngine.register(
                TekParticles.SKULL.get(),
                sprites -> (data, level, x, y, z, xSpeed, ySpeed, zSpeed) ->
                        TekLegacyParticles.skull(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites)
        );
    }

    private static <T extends MobEntity> void registerCraft(
            RegistryObject<EntityType<T>> type,
            String assetName,
            ResourceLocation fallbackTexture
    ) {
        registerCraft(type, assetName, assetName, fallbackTexture);
    }

    private static <T extends MobEntity> void registerCraft(
            RegistryObject<EntityType<T>> type,
            String modelName,
            String textureName,
            ResourceLocation fallbackTexture
    ) {
        RenderingRegistry.registerEntityRenderingHandler(
                type.get(),
                manager -> new TekCraftStudioRenderer<>(manager, model(modelName), texture(textureName), fallbackTexture)
        );
    }

    private static ResourceLocation model(String modelName) {
        return new ResourceLocation(TekTopiaPort.MODID, "craftstudio/models/entity/" + modelName + ".csjsmodel");
    }

    private static ResourceLocation texture(String textureName) {
        return new ResourceLocation(TekTopiaPort.MODID, "textures/entity/" + textureName + ".png");
    }
}
