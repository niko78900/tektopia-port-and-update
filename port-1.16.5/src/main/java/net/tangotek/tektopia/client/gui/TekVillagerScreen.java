package net.tangotek.tektopia.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.tangotek.tektopia.network.TekClientSyncCache;

public class TekVillagerScreen extends ContainerScreen<TekVillagerContainer> {
    public TekVillagerScreen(TekVillagerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 256;
        this.imageHeight = 210;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xD0202020);
        fill(matrixStack, this.leftPos + 6, this.topPos + 18, this.leftPos + this.imageWidth - 6, this.topPos + this.imageHeight - 8, 0xD0101010);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, "TekTopia Villager #" + this.menu.getEntityId(), 8, 6, 0xFFFFFF);
        TekClientSyncCache.GuiSnapshotState snapshot = TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
        int y = 24;
        if (snapshot == null || snapshot.lines.isEmpty()) {
            this.font.draw(matrixStack, "Waiting for server snapshot...", 10, y, 0xAAAAAA);
            return;
        }
        for (String line : snapshot.lines) {
            this.font.draw(matrixStack, trim(line, 88), 10, y, 0xD8E8FF);
            y += 10;
            if (y > this.imageHeight - 18) {
                break;
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static String trim(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }
}
