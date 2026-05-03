package net.tangotek.tektopia.structures;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public enum TekStructureType {
    TOWNHALL("Town Hall"),
    STORAGE("Storage"),
    HOME("Home"),
    FARM("Farm"),
    MINESHAFT("Mineshaft"),
    LUMBER_AREA("Lumber Area"),
    KITCHEN("Kitchen"),
    BUTCHER("Butcher"),
    RANCH_PEN("Ranch Pen"),
    GUARD_POST("Guard Post"),
    BARRACKS("Barracks"),
    MERCHANT_STALL("Merchant Stall");

    private final String displayName;

    TekStructureType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public TekVillageStructure create(ServerWorld level, BlockPos doorInside, Direction signFacing) {
        switch (this) {
            case TOWNHALL:
                return new TekStructureTownHall(level, doorInside, signFacing);
            case STORAGE:
                return new TekStructureStorage(level, doorInside, signFacing);
            case HOME:
            case FARM:
            case MINESHAFT:
            case LUMBER_AREA:
            case KITCHEN:
            case BUTCHER:
            case RANCH_PEN:
            case GUARD_POST:
            case BARRACKS:
            case MERCHANT_STALL:
                return new TekStructureWorksite(level, this, doorInside, signFacing);
            default:
                throw new IllegalStateException("Unhandled structure type: " + this);
        }
    }

    @Nullable
    public static TekStructureType fromInput(String input) {
        if (input == null) {
            return null;
        }
        String normalized = input.trim().toLowerCase();
        switch (normalized) {
            case "townhall":
            case "town_hall":
            case "town-hall":
                return TOWNHALL;
            case "storage":
                return STORAGE;
            case "home":
                return HOME;
            case "farm":
                return FARM;
            case "mineshaft":
            case "mine":
                return MINESHAFT;
            case "lumber":
            case "lumber_area":
            case "lumber-area":
                return LUMBER_AREA;
            case "kitchen":
                return KITCHEN;
            case "butcher":
                return BUTCHER;
            case "ranch":
            case "ranch_pen":
            case "ranch-pen":
                return RANCH_PEN;
            case "guardpost":
            case "guard_post":
            case "guard-post":
                return GUARD_POST;
            case "barracks":
                return BARRACKS;
            case "merchant":
            case "merchantstall":
            case "merchant_stall":
            case "merchant-stall":
                return MERCHANT_STALL;
            default:
                return null;
        }
    }

    @Nullable
    public static TekStructureType fromFrameItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        ResourceLocation itemName = stack.getItem().getRegistryName();
        if (itemName != null && "tektopia".equals(itemName.getNamespace())) {
            String path = itemName.getPath();
            if (path.contains("townhall") || path.contains("town_hall")) {
                return TOWNHALL;
            }
            if (path.contains("storage")) {
                return STORAGE;
            }
            if (path.contains("home")) {
                return HOME;
            }
            if (path.contains("farm")) {
                return FARM;
            }
            if (path.contains("mineshaft") || path.contains("mine")) {
                return MINESHAFT;
            }
            if (path.contains("lumber")) {
                return LUMBER_AREA;
            }
            if (path.contains("kitchen")) {
                return KITCHEN;
            }
            if (path.contains("butcher")) {
                return BUTCHER;
            }
            if (path.contains("ranch")) {
                return RANCH_PEN;
            }
            if (path.contains("guard_post") || path.contains("guardpost")) {
                return GUARD_POST;
            }
            if (path.contains("barracks")) {
                return BARRACKS;
            }
            if (path.contains("merchant")) {
                return MERCHANT_STALL;
            }
        }

        String display = stack.getHoverName().getString().trim().toLowerCase();
        if (display.contains("town hall") || display.contains("townhall")) {
            return TOWNHALL;
        }
        if (display.contains("storage")) {
            return STORAGE;
        }
        if (display.contains("home")) {
            return HOME;
        }
        if (display.contains("farm")) {
            return FARM;
        }
        if (display.contains("mineshaft") || display.contains("mine")) {
            return MINESHAFT;
        }
        if (display.contains("lumber")) {
            return LUMBER_AREA;
        }
        if (display.contains("kitchen")) {
            return KITCHEN;
        }
        if (display.contains("butcher")) {
            return BUTCHER;
        }
        if (display.contains("ranch")) {
            return RANCH_PEN;
        }
        if (display.contains("guard post") || display.contains("guardpost")) {
            return GUARD_POST;
        }
        if (display.contains("barracks")) {
            return BARRACKS;
        }
        if (display.contains("merchant")) {
            return MERCHANT_STALL;
        }
        return null;
    }
}
