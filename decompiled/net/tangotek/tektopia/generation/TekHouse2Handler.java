/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.world.gen.structure.StructureBoundingBox
 *  net.minecraft.world.gen.structure.StructureComponent
 *  net.minecraft.world.gen.structure.StructureVillagePieces$PieceWeight
 *  net.minecraft.world.gen.structure.StructureVillagePieces$Start
 *  net.minecraft.world.gen.structure.StructureVillagePieces$Village
 *  net.minecraftforge.fml.common.registry.VillagerRegistry$IVillageCreationHandler
 */
package net.tangotek.tektopia.generation;

import java.util.List;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.tangotek.tektopia.generation.TekHouse2;

public class TekHouse2Handler
implements VillagerRegistry.IVillageCreationHandler {
    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random parRandom, int size) {
        System.out.println("Getting village TekHouse2 piece weight");
        return new StructureVillagePieces.PieceWeight(this.getComponentClass(), 100, 5 + size * 2);
    }

    public Class<? extends StructureVillagePieces.Village> getComponentClass() {
        return TekHouse2.class;
    }

    public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight parPieceWeight, StructureVillagePieces.Start parStart, List<StructureComponent> parPiecesList, Random parRand, int parMinX, int parMinY, int parMinZ, EnumFacing parFacing, int parType) {
        System.out.println("TekHouse2 buildComponent() at " + parMinX + ", " + parMinY + ", " + parMinZ);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_175897_a((int)parMinX, (int)parMinY, (int)parMinZ, (int)0, (int)0, (int)0, (int)4, (int)6, (int)5, (EnumFacing)parFacing);
        return TekHouse2Handler.canVillageGoDeeper(structureboundingbox) && StructureComponent.func_74883_a(parPiecesList, (StructureBoundingBox)structureboundingbox) == null ? new TekHouse2(parStart, parType, parRand, structureboundingbox, parFacing) : null;
    }

    protected static boolean canVillageGoDeeper(StructureBoundingBox structurebb) {
        return structurebb != null && structurebb.field_78895_b > 10;
    }
}

