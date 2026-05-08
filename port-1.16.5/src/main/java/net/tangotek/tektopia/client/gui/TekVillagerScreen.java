package net.tangotek.tektopia.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.tangotek.tektopia.network.TekClientSyncCache;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.network.message.PacketAIFilter;
import net.tangotek.tektopia.network.message.PacketTradeAction;

public class TekVillagerScreen extends ContainerScreen<TekVillagerContainer> {
    private static final int TAB_MAIN = 0;
    private static final int TAB_FILTERS = 1;
    private int selectedTab = TAB_MAIN;
    private int filterScroll = 0;

    public TekVillagerScreen(TekVillagerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 280;
        this.imageHeight = 232;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.selectedTab == TAB_MAIN) {
            this.renderInventoryStacks(matrixStack, mouseX, mouseY);
        }
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xD0202020);
        fill(matrixStack, this.leftPos + 6, this.topPos + 30, this.leftPos + this.imageWidth - 6, this.topPos + this.imageHeight - 8, 0xD0101010);
        drawTab(matrixStack, this.leftPos + 8, this.topPos + 17, 44, "Main", this.selectedTab == TAB_MAIN);
        drawTab(matrixStack, this.leftPos + 54, this.topPos + 17, 62, "AI Filters", this.selectedTab == TAB_FILTERS);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, "TekTopia Villager #" + this.menu.getEntityId(), 8, 6, 0xFFFFFF);
        TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
        int y = 38;
        if (snapshot == null || snapshot.lines.isEmpty()) {
            this.font.draw(matrixStack, "Waiting for server snapshot...", 10, y, 0xAAAAAA);
            return;
        }
        if (this.selectedTab == TAB_FILTERS) {
            this.renderFilterTab(matrixStack, snapshot);
            return;
        }
        for (String line : snapshot.lines) {
            if (line.startsWith("AI filters ")) {
                continue;
            }
            this.font.draw(matrixStack, trim(line, 94), 10, y, 0xD8E8FF);
            y += 10;
            if (y > 112) {
                break;
            }
        }
        this.font.draw(matrixStack, "Inventory", 10, 128, 0xFFFFFF);
        if (snapshot.inventory.isEmpty()) {
            this.font.draw(matrixStack, "No inventory snapshot.", 70, 128, 0xAAAAAA);
        }
        this.renderTradeButton(matrixStack, snapshot);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int localX = (int) mouseX - this.leftPos;
        int localY = (int) mouseY - this.topPos;
        if (localY >= 17 && localY <= 30) {
            if (localX >= 8 && localX <= 52) {
                this.selectedTab = TAB_MAIN;
                return true;
            }
            if (localX >= 54 && localX <= 116) {
                this.selectedTab = TAB_FILTERS;
                return true;
            }
        }
        if (this.selectedTab == TAB_FILTERS) {
            String clicked = this.filterAt(localX, localY);
            if (!clicked.isEmpty()) {
                TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
                boolean current = snapshot != null && snapshot.filters.getOrDefault(clicked, true);
                TekNetwork.sendToServer(new PacketAIFilter(this.menu.getEntityId(), clicked, !current));
                TekClientSyncCache.updateAIFilter(this.menu.getEntityId(), clicked, !current);
                return true;
            }
        }
        if (this.selectedTab == TAB_MAIN) {
            TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
            String action = tradeAction(snapshot);
            if (!action.isEmpty() && localX >= 184 && localX <= 270 && localY >= 123 && localY <= 139) {
                TekNetwork.sendToServer(new PacketTradeAction(this.menu.getEntityId(), action));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.selectedTab == TAB_FILTERS) {
            TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
            int maxScroll = Math.max(0, sortedFilters(snapshot).size() - 15);
            this.filterScroll = Math.max(0, Math.min(maxScroll, this.filterScroll + (delta < 0 ? 1 : -1)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void renderFilterTab(MatrixStack matrixStack, TekClientSyncCache.GuiSnapshotState snapshot) {
        List<String> filters = sortedFilters(snapshot);
        if (filters.isEmpty()) {
            this.font.draw(matrixStack, "No AI filters registered for this villager.", 10, 42, 0xAAAAAA);
            return;
        }
        int y = 42;
        int end = Math.min(filters.size(), this.filterScroll + 15);
        for (int i = this.filterScroll; i < end; i++) {
            String filter = filters.get(i);
            boolean enabled = snapshot.filters.getOrDefault(filter, true);
            fill(matrixStack, 10, y, 20, y + 10, 0xFF404040);
            fill(matrixStack, 11, y + 1, 19, y + 9, enabled ? 0xFF5DBB63 : 0xFF202020);
            this.font.draw(matrixStack, filter + "=" + enabled, 26, y + 1, enabled ? 0xD8FFD8 : 0xFFCCCC);
            y += 11;
        }
        if (filters.size() > 15) {
            this.font.draw(matrixStack, "Scroll " + (this.filterScroll + 1) + "-" + end + "/" + filters.size(), 10, this.imageHeight - 20, 0xAAAAAA);
        }
    }

    private void renderInventoryStacks(MatrixStack matrixStack, int mouseX, int mouseY) {
        TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
        if (snapshot == null || snapshot.inventory.isEmpty()) {
            return;
        }
        int startX = this.leftPos + 10;
        int startY = this.topPos + 142;
        for (int slot = 0; slot < 27; slot++) {
            int x = startX + (slot % 9) * 20;
            int y = startY + (slot / 9) * 20;
            fill(matrixStack, x - 1, y - 1, x + 17, y + 17, 0xFF303030);
            fill(matrixStack, x, y, x + 16, y + 16, 0xFF111111);
            if (slot >= snapshot.inventory.size()) {
                continue;
            }
            ItemStack stack = snapshot.inventory.get(slot);
            if (stack.isEmpty()) {
                continue;
            }
            this.itemRenderer.renderAndDecorateItem(stack, x, y);
            this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y);
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                this.renderTooltip(matrixStack, stack, mouseX, mouseY);
            }
        }
    }

    private void renderTradeButton(MatrixStack matrixStack, TekClientSyncCache.GuiSnapshotState snapshot) {
        String action = tradeAction(snapshot);
        if (action.isEmpty()) {
            return;
        }
        String label = "Buy Token";
        if ("structure_token".equals(action)) {
            label = "Buy Structure";
        } else if ("profession_token".equals(action)) {
            label = "Buy Profession";
        }
        fill(matrixStack, 184, 123, 270, 139, 0xFF263646);
        fill(matrixStack, 184, 138, 270, 139, 0xFF7DB6FF);
        this.font.draw(matrixStack, label, 190, 128, 0xFFFFFF);
    }

    private String filterAt(int localX, int localY) {
        if (localX < 10 || localX > 20 || localY < 42 || localY > 208) {
            return "";
        }
        int row = (localY - 42) / 11;
        TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
        List<String> filters = sortedFilters(snapshot);
        int index = this.filterScroll + row;
        return index >= 0 && index < filters.size() ? filters.get(index) : "";
    }

    private static List<String> sortedFilters(TekClientSyncCache.GuiSnapshotState snapshot) {
        if (snapshot == null || snapshot.filters.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> filters = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : snapshot.filters.entrySet()) {
            filters.add(entry.getKey());
        }
        Collections.sort(filters);
        return filters;
    }

    private static String tradeAction(TekClientSyncCache.GuiSnapshotState snapshot) {
        if (snapshot == null || snapshot.lines.isEmpty()) {
            return "";
        }
        for (String line : snapshot.lines) {
            if (line.contains("action=structure_token")) {
                return "structure_token";
            }
            if (line.contains("action=profession_token")) {
                return "profession_token";
            }
        }
        return "";
    }

    private static void drawTab(MatrixStack matrixStack, int x, int y, int width, String label, boolean selected) {
        fill(matrixStack, x, y, x + width, y + 13, selected ? 0xFF303A44 : 0xFF202020);
        fill(matrixStack, x, y + 12, x + width, y + 13, selected ? 0xFF7DB6FF : 0xFF404040);
        net.minecraft.client.Minecraft.getInstance().font.draw(matrixStack, label, x + 5, y + 3, selected ? 0xFFFFFF : 0xAAAAAA);
    }

    private static String trim(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }
}
