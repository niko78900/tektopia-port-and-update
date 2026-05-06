package net.tangotek.tektopia.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.tangotek.tektopia.common.container.TekVillagerContainer;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.network.message.PacketAIFilter;

public class TekVillagerScreen extends ContainerScreen<TekVillagerContainer> {
    private static final int FILTER_ROWS = 8;
    private final List<Button> filterButtons = new ArrayList<>();
    private Tab activeTab = Tab.MAIN;
    private int filterScroll;
    private Button mainTabButton;
    private Button filtersTabButton;
    private Button scrollUpButton;
    private Button scrollDownButton;

    public TekVillagerScreen(TekVillagerContainer menu, PlayerInventory inventory, ITextComponent title) {
        super(menu, inventory, title);
        this.imageWidth = 220;
        this.imageHeight = 202;
    }

    @Override
    protected void init() {
        super.init();
        this.filterButtons.clear();
        int tabY = this.topPos + 8;
        this.mainTabButton = this.addButton(new Button(this.leftPos + 10, tabY, 72, 20, new StringTextComponent("Status"), button -> this.activeTab = Tab.MAIN));
        this.filtersTabButton = this.addButton(new Button(this.leftPos + 86, tabY, 72, 20, new StringTextComponent("AI"), button -> this.activeTab = Tab.FILTERS));
        this.scrollUpButton = this.addButton(new Button(this.leftPos + 186, this.topPos + 38, 20, 20, new StringTextComponent("^"), button -> {
            this.filterScroll = Math.max(0, this.filterScroll - 1);
            this.refreshFilterButtons();
        }));
        this.scrollDownButton = this.addButton(new Button(this.leftPos + 186, this.topPos + 154, 20, 20, new StringTextComponent("v"), button -> {
            this.filterScroll++;
            this.refreshFilterButtons();
        }));
        for (int row = 0; row < FILTER_ROWS; row++) {
            final int rowIndex = row;
            Button filterButton = this.addButton(new Button(this.leftPos + 14, this.topPos + 40 + row * 14, 166, 14, new StringTextComponent(""), button -> this.toggleFilter(rowIndex)));
            this.filterButtons.add(filterButton);
        }
        this.refreshFilterButtons();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.refreshFilterButtons();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        fill(matrixStack, this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xF0E8E0D0);
        fill(matrixStack, this.leftPos + 4, this.topPos + 4, this.leftPos + this.imageWidth - 4, this.topPos + this.imageHeight - 4, 0xFFEFE7D7);
        fill(matrixStack, this.leftPos + 8, this.topPos + 34, this.leftPos + this.imageWidth - 8, this.topPos + this.imageHeight - 8, 0xFFF8F5ED);
        if (this.activeTab == Tab.MAIN) {
            this.renderMainTab(matrixStack);
        } else {
            this.renderFilterTab(matrixStack);
        }
    }

    private void renderMainTab(MatrixStack matrixStack) {
        CompoundNBT snapshot = this.snapshot();
        int x = this.leftPos + 14;
        int y = this.topPos + 40;
        this.font.draw(matrixStack, text(snapshot, "name", "Villager") + " (" + text(snapshot, "profession", "-") + ")", x, y, 0x2C241A);
        this.font.draw(matrixStack, "Health " + formatFloat(snapshot, "health") + "/" + formatFloat(snapshot, "maxHealth")
                + " Hunger " + snapshot.getInt("hunger"), x, y + 12, 0x2C241A);
        this.font.draw(matrixStack, "Happy " + snapshot.getInt("happiness") + " Intelligence " + snapshot.getInt("intelligence")
                + " Days " + snapshot.getInt("daysAlive"), x, y + 24, 0x2C241A);
        this.font.draw(matrixStack, "Status " + text(snapshot, "status", "-"), x, y + 36, 0x2C241A);
        this.font.draw(matrixStack, "Home " + formatPos(snapshot, "home") + " Bed " + formatPos(snapshot, "bed"), x, y + 48, 0x2C241A);
        this.font.draw(matrixStack, "Thought " + dash(text(snapshot, "thought", "")), x, y + 60, 0x2C241A);
        this.font.draw(matrixStack, "Item " + dash(text(snapshot, "itemThought", "")), x, y + 72, 0x2C241A);
        this.font.draw(matrixStack, "Skills " + formatSkills(snapshot.getCompound("skills")), x, y + 84, 0x2C241A);
        this.renderInventoryGrid(matrixStack, snapshot.getList("inventory", 10), x, y + 100);
    }

    private void renderFilterTab(MatrixStack matrixStack) {
        this.font.draw(matrixStack, "AI Filters", this.leftPos + 14, this.topPos + 28, 0x2C241A);
        List<String> names = this.filterNames();
        int total = names.size();
        String range = total == 0 ? "0/0" : (Math.min(total, this.filterScroll + 1) + "-" + Math.min(total, this.filterScroll + FILTER_ROWS) + "/" + total);
        this.font.draw(matrixStack, range, this.leftPos + 160, this.topPos + 28, 0x2C241A);
    }

    private void renderInventoryGrid(MatrixStack matrixStack, ListNBT inventory, int x, int y) {
        for (int slot = 0; slot < 27; slot++) {
            int cellX = x + (slot % 9) * 21;
            int cellY = y + (slot / 9) * 17;
            fill(matrixStack, cellX, cellY, cellX + 19, cellY + 15, 0xFFE3D6BE);
        }
        for (int i = 0; i < inventory.size(); i++) {
            CompoundNBT itemTag = inventory.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < 0 || slot >= 27) {
                continue;
            }
            ItemStack stack = ItemStack.of(itemTag);
            if (stack.isEmpty()) {
                continue;
            }
            String name = stack.getItem().getRegistryName() == null ? stack.getItem().toString() : stack.getItem().getRegistryName().getPath();
            String label = name.length() > 2 ? name.substring(0, 2) : name;
            int cellX = x + (slot % 9) * 21 + 2;
            int cellY = y + (slot / 9) * 17 + 4;
            this.font.draw(matrixStack, label + stack.getCount(), cellX, cellY, 0x2C241A);
        }
    }

    private void refreshFilterButtons() {
        boolean filtersVisible = this.activeTab == Tab.FILTERS;
        this.mainTabButton.visible = true;
        this.filtersTabButton.visible = true;
        this.scrollUpButton.visible = filtersVisible;
        this.scrollDownButton.visible = filtersVisible;
        List<String> names = this.filterNames();
        int maxScroll = Math.max(0, names.size() - FILTER_ROWS);
        this.filterScroll = Math.max(0, Math.min(this.filterScroll, maxScroll));
        this.scrollUpButton.active = filtersVisible && this.filterScroll > 0;
        this.scrollDownButton.active = filtersVisible && this.filterScroll < maxScroll;
        CompoundNBT filters = this.snapshot().getCompound("filters");
        for (int row = 0; row < this.filterButtons.size(); row++) {
            Button button = this.filterButtons.get(row);
            int index = this.filterScroll + row;
            button.visible = filtersVisible && index < names.size();
            if (!button.visible) {
                continue;
            }
            String name = names.get(index);
            button.setMessage(new StringTextComponent((filters.getBoolean(name) ? "[x] " : "[ ] ") + name));
        }
    }

    private void toggleFilter(int row) {
        List<String> names = this.filterNames();
        int index = this.filterScroll + row;
        if (index < 0 || index >= names.size()) {
            return;
        }
        String name = names.get(index);
        boolean enabled = this.snapshot().getCompound("filters").getBoolean(name);
        TekNetwork.sendToServer(new PacketAIFilter(this.menu.getEntityId(), name, !enabled));
    }

    private CompoundNBT snapshot() {
        return TekClientSyncState.getVillagerGuiSnapshot(this.menu.getEntityId());
    }

    private List<String> filterNames() {
        List<String> names = new ArrayList<>(this.snapshot().getCompound("filters").getAllKeys());
        Collections.sort(names);
        return names;
    }

    private static String text(CompoundNBT snapshot, String key, String fallback) {
        String value = snapshot.getString(key);
        return value == null || value.isEmpty() ? fallback : value;
    }

    private static String dash(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private static String formatFloat(CompoundNBT snapshot, String key) {
        return String.format("%.1f", snapshot.getFloat(key));
    }

    private static String formatPos(CompoundNBT snapshot, String key) {
        if (!snapshot.contains(key, 4)) {
            return "-";
        }
        BlockPos pos = BlockPos.of(snapshot.getLong(key));
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static String formatSkills(CompoundNBT skills) {
        if (skills == null || skills.isEmpty()) {
            return "-";
        }
        List<String> keys = new ArrayList<>(skills.getAllKeys());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(key).append('=').append(skills.getInt(key));
        }
        return sb.toString();
    }

    private enum Tab {
        MAIN,
        FILTERS
    }
}
