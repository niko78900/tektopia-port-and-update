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
    HOME2("Home 2"),
    HOME4("Home 4"),
    HOME6("Home 6"),
    FARM("Farm"),
    MINESHAFT("Mineshaft"),
    LUMBER_AREA("Lumber Area"),
    KITCHEN("Kitchen"),
    BLACKSMITH("Blacksmith"),
    BUTCHER("Butcher"),
    RANCH_PEN("Ranch Pen"),
    SHEEP_PEN("Sheep Pen"),
    COW_PEN("Cow Pen"),
    PIG_PEN("Pig Pen"),
    CHICKEN_COOP("Chicken Coop"),
    GUARD_POST("Guard Post"),
    BARRACKS("Barracks"),
    MERCHANT_STALL("Merchant Stall"),
    TAVERN("Tavern"),
    SCHOOL("School"),
    LIBRARY("Library");

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
            case HOME2:
            case HOME4:
            case HOME6:
            case FARM:
            case MINESHAFT:
            case LUMBER_AREA:
            case KITCHEN:
            case BLACKSMITH:
            case BUTCHER:
            case RANCH_PEN:
            case SHEEP_PEN:
            case COW_PEN:
            case PIG_PEN:
            case CHICKEN_COOP:
            case GUARD_POST:
            case BARRACKS:
            case MERCHANT_STALL:
            case TAVERN:
            case SCHOOL:
            case LIBRARY:
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
            case "home2":
            case "home_2":
            case "home-2":
                return HOME2;
            case "home4":
            case "home_4":
            case "home-4":
                return HOME4;
            case "home6":
            case "home_6":
            case "home-6":
                return HOME6;
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
            case "blacksmith":
            case "smithy":
                return BLACKSMITH;
            case "butcher":
                return BUTCHER;
            case "ranch":
            case "ranch_pen":
            case "ranch-pen":
                return RANCH_PEN;
            case "sheep":
            case "sheeppen":
            case "sheep_pen":
            case "sheep-pen":
                return SHEEP_PEN;
            case "cow":
            case "cowpen":
            case "cow_pen":
            case "cow-pen":
                return COW_PEN;
            case "pig":
            case "pigpen":
            case "pig_pen":
            case "pig-pen":
                return PIG_PEN;
            case "chicken":
            case "chickencoop":
            case "chicken_coop":
            case "chicken-coop":
                return CHICKEN_COOP;
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
            case "tavern":
                return TAVERN;
            case "school":
                return SCHOOL;
            case "library":
                return LIBRARY;
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
            if (path.contains("home2") || path.contains("home_2")) {
                return HOME2;
            }
            if (path.contains("home4") || path.contains("home_4")) {
                return HOME4;
            }
            if (path.contains("home6") || path.contains("home_6")) {
                return HOME6;
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
            if (path.contains("blacksmith") || path.contains("smithy")) {
                return BLACKSMITH;
            }
            if (path.contains("butcher")) {
                return BUTCHER;
            }
            if (path.contains("sheep")) {
                return SHEEP_PEN;
            }
            if (path.contains("cow")) {
                return COW_PEN;
            }
            if (path.contains("pig")) {
                return PIG_PEN;
            }
            if (path.contains("chicken")) {
                return CHICKEN_COOP;
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
            if (path.contains("tavern")) {
                return TAVERN;
            }
            if (path.contains("school")) {
                return SCHOOL;
            }
            if (path.contains("library")) {
                return LIBRARY;
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
            if (display.contains("2")) {
                return HOME2;
            }
            if (display.contains("4")) {
                return HOME4;
            }
            if (display.contains("6")) {
                return HOME6;
            }
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
        if (display.contains("blacksmith") || display.contains("smithy")) {
            return BLACKSMITH;
        }
        if (display.contains("butcher")) {
            return BUTCHER;
        }
        if (display.contains("sheep")) {
            return SHEEP_PEN;
        }
        if (display.contains("cow")) {
            return COW_PEN;
        }
        if (display.contains("pig")) {
            return PIG_PEN;
        }
        if (display.contains("chicken")) {
            return CHICKEN_COOP;
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
        if (display.contains("tavern")) {
            return TAVERN;
        }
        if (display.contains("school")) {
            return SCHOOL;
        }
        if (display.contains("library")) {
            return LIBRARY;
        }
        return null;
    }
}
