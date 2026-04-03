/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.potion.PotionType
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.IForgeRegistryEntry
 */
package net.tangotek.tektopia;

import java.awt.Color;
import javax.annotation.Nullable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tangotek.tektopia.items.PotionBless;

public class ModPotions {
    public static final PotionBless potionBless = new PotionBless(new Color(255, 255, 0).getRGB());
    public static final PotionType potionBlessType = ModPotions.createPotionType(new PotionEffect((Potion)potionBless, 1));

    private static PotionType createPotionType(PotionEffect potionEffect) {
        return ModPotions.createPotionType(potionEffect, null);
    }

    private static PotionType createPotionType(PotionEffect potionEffect, @Nullable String namePrefix) {
        ResourceLocation potionName = potionEffect.func_188419_a().getRegistryName();
        ResourceLocation potionTypeName = namePrefix != null ? new ResourceLocation(potionName.func_110624_b(), namePrefix + potionName.func_110623_a()) : potionName;
        return (PotionType)new PotionType(potionName.toString(), new PotionEffect[]{potionEffect}).setRegistryName(potionTypeName);
    }

    public static void registerPotions(IForgeRegistry<Potion> registry) {
        registry.registerAll((IForgeRegistryEntry[])new Potion[]{potionBless});
    }

    public static void registerPotionTypes(IForgeRegistry<PotionType> registry) {
        registry.registerAll((IForgeRegistryEntry[])new PotionType[]{potionBlessType});
    }
}

