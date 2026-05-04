package net.tangotek.tektopia.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Map;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.tangotek.tektopia.network.TekClientSyncCache;

public class TekStatusScreen extends Screen {
    public TekStatusScreen() {
        super(new StringTextComponent("TekTopia Status"));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        int y = 14;
        this.font.draw(matrixStack, "TekTopia Alpha Status", 12, y, 0xFFFFFF);
        y += 14;

        if (TekClientSyncCache.getVillages().isEmpty()) {
            this.font.draw(matrixStack, "No synced village state yet.", 12, y, 0xAAAAAA);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            return;
        }

        for (TekClientSyncCache.VillageState village : TekClientSyncCache.getVillages().values()) {
            this.font.draw(matrixStack, "Village " + shortId(village.villageId)
                    + " r=" + village.radius
                    + " residents=" + village.residents
                    + " hostiles=" + village.hostiles
                    + " reservations=" + village.reservations
                    + " invalid=" + village.invalidStructures, 12, y, 0xE6E6E6);
            y += 11;
            this.font.draw(matrixStack, "Raid active=" + village.raidActive
                    + " level=" + village.raidLevel
                    + " alert=" + (village.alertActive ? village.alert : "-"), 18, y, village.raidActive ? 0xFF7777 : 0xAAAAAA);
            y += 11;
            this.font.draw(matrixStack, trim("Professions/trade: " + village.professions, 110), 18, y, 0xAAAAAA);
            y += 11;
            this.font.draw(matrixStack, trim("Structures: " + village.structures, 110), 18, y, village.invalidStructures > 0 ? 0xFFCC66 : 0xAAAAAA);
            y += 14;
        }

        this.font.draw(matrixStack, "Villagers", 12, y, 0xFFFFFF);
        y += 12;
        int shown = 0;
        for (TekClientSyncCache.VillagerState villager : TekClientSyncCache.getVillagers().values()) {
            this.font.draw(matrixStack, "#" + villager.entityId
                    + " " + blank(villager.profession)
                    + " status=" + blank(villager.workerStatus)
                    + " thought=" + blank(villager.thoughtKey)
                    + " item=" + blank(villager.itemThought), 18, y, 0xD0D0D0);
            y += 10;
            shown++;
            if (shown >= 12 || y > this.height - 60) {
                break;
            }
        }

        if (!TekClientSyncCache.getPathNodes().isEmpty() && y < this.height - 30) {
            y += 6;
            this.font.draw(matrixStack, "Path/Blocked Workers", 12, y, 0xFFFFFF);
            y += 12;
            for (TekClientSyncCache.PathNodeState path : TekClientSyncCache.getPathNodes().values()) {
                this.font.draw(matrixStack, "#" + path.entityId
                        + " target=" + path.target.toShortString()
                        + " reason=" + blank(path.reason), 18, y, 0xFFCC66);
                y += 10;
                if (y > this.height - 24) {
                    break;
                }
            }
        }

        if (!TekClientSyncCache.getAIFilters().isEmpty() && y < this.height - 24) {
            y += 6;
            this.font.draw(matrixStack, "AI Filters", 12, y, 0xFFFFFF);
            y += 12;
            for (Map.Entry<Integer, Map<String, Boolean>> entry : TekClientSyncCache.getAIFilters().entrySet()) {
                this.font.draw(matrixStack, "#" + entry.getKey() + " " + entry.getValue(), 18, y, 0xAAAAAA);
                y += 10;
                if (y > this.height - 20) {
                    break;
                }
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static String blank(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private static String shortId(String value) {
        return value == null || value.length() <= 8 ? blank(value) : value.substring(0, 8);
    }

    private static String trim(String value, int max) {
        if (value == null || value.length() <= max) {
            return blank(value);
        }
        return value.substring(0, max - 3) + "...";
    }
}
