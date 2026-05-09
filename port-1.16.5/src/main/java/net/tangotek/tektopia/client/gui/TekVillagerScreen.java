package net.tangotek.tektopia.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.tangotek.tektopia.TekTopiaPort;
import net.tangotek.tektopia.common.TekTradeActions;
import net.tangotek.tektopia.common.TekVillagerContainer;
import net.tangotek.tektopia.network.TekClientSyncCache;
import net.tangotek.tektopia.network.TekNetwork;
import net.tangotek.tektopia.network.message.PacketAIFilter;
import net.tangotek.tektopia.network.message.PacketTradeAction;
import net.tangotek.tektopia.registry.TekItems;

public class TekVillagerScreen extends ContainerScreen<TekVillagerContainer> {
    private static final ResourceLocation GUI_MAIN_TEXTURE =
            new ResourceLocation(TekTopiaPort.MODID, "textures/gui/container/villager_main.png");
    private static final ResourceLocation GUI_AI_TEXTURE =
            new ResourceLocation(TekTopiaPort.MODID, "textures/gui/container/villager_ai.png");
    private static final int SCREEN_WIDTH = 276;
    private static final int SCREEN_HEIGHT = 166;
    private static final int LEGACY_WIDTH = 178;
    private static final int LEGACY_HEIGHT = 157;
    private static final int LEGACY_CENTER_X = (SCREEN_WIDTH - LEGACY_WIDTH) / 2;
    private static final int LEGACY_Y = (SCREEN_HEIGHT - LEGACY_HEIGHT) / 2;
    private static final int VIEW_MAIN = 0;
    private static final int VIEW_AI = 1;
    private static final int MAX_VISIBLE_FILTERS = 10;

    private int selectedView = VIEW_MAIN;
    private int filterScroll = 0;

    public TekVillagerScreen(TekVillagerContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = SCREEN_WIDTH;
        this.imageHeight = SCREEN_HEIGHT;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        TekClientSyncCache.GuiSnapshotState snapshot = this.snapshot();
        VillagerView view = VillagerView.from(snapshot, this.menu.getEntityId());
        Optional<TradeOffer> offer = TradeOffer.from(snapshot);
        int panelX = this.legacyPanelX(offer);
        ItemStack hovered = ItemStack.EMPTY;

        if (this.selectedView == VIEW_AI) {
            hovered = this.renderAiItems(matrixStack, view, panelX, mouseX, mouseY);
        } else {
            hovered = this.renderMainItems(matrixStack, snapshot, view, offer, panelX, mouseX, mouseY);
        }

        if (!hovered.isEmpty()) {
            this.renderTooltip(matrixStack, hovered, mouseX, mouseY);
        }
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        TekClientSyncCache.GuiSnapshotState snapshot = this.snapshot();
        Optional<TradeOffer> offer = TradeOffer.from(snapshot);
        int panelX = this.legacyPanelX(offer);
        ResourceLocation texture = this.selectedView == VIEW_AI ? GUI_AI_TEXTURE : GUI_MAIN_TEXTURE;
        this.minecraft.getTextureManager().bind(texture);
        this.blit(matrixStack, this.leftPos + panelX, this.topPos + LEGACY_Y, 0, 0, LEGACY_WIDTH, LEGACY_HEIGHT);

        if (this.selectedView == VIEW_MAIN && offer.isPresent()) {
            this.renderTradeBackground(matrixStack);
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        TekClientSyncCache.GuiSnapshotState snapshot = this.snapshot();
        VillagerView view = VillagerView.from(snapshot, this.menu.getEntityId());
        Optional<TradeOffer> offer = TradeOffer.from(snapshot);
        int panelX = this.legacyPanelX(offer);

        if (snapshot == null) {
            this.font.draw(matrixStack, "Waiting for villager data...", panelX + 8, LEGACY_Y + 39, 0x404040);
            return;
        }

        if (this.selectedView == VIEW_AI) {
            this.renderAiLabels(matrixStack, snapshot, view, panelX);
            return;
        }

        this.renderMainLabels(matrixStack, view, panelX);
        if (offer.isPresent()) {
            this.renderTradeLabels(matrixStack, offer.get());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TekClientSyncCache.GuiSnapshotState snapshot = this.snapshot();
        Optional<TradeOffer> offer = TradeOffer.from(snapshot);
        int panelX = this.legacyPanelX(offer);
        int localX = (int) mouseX - this.leftPos;
        int localY = (int) mouseY - this.topPos;
        int legacyX = localX - panelX;
        int legacyY = localY - LEGACY_Y;

        if (this.selectedView == VIEW_MAIN) {
            if (legacyX >= 43 && legacyX < 71 && legacyY >= 0 && legacyY < 29) {
                this.selectedView = VIEW_AI;
                this.filterScroll = 0;
                return true;
            }
            if (offer.isPresent() && this.isTradeButton(localX, localY)) {
                TekNetwork.sendToServer(new PacketTradeAction(this.menu.getEntityId(), offer.get().action));
                return true;
            }
        } else {
            if (legacyX >= 14 && legacyX < 42 && legacyY >= 0 && legacyY < 29) {
                this.selectedView = VIEW_MAIN;
                return true;
            }
            String clicked = this.filterAt(snapshot, legacyX, legacyY);
            if (!clicked.isEmpty()) {
                boolean current = snapshot != null && snapshot.filters.getOrDefault(clicked, true);
                TekNetwork.sendToServer(new PacketAIFilter(this.menu.getEntityId(), clicked, !current));
                TekClientSyncCache.updateAIFilter(this.menu.getEntityId(), clicked, !current);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.selectedView == VIEW_AI) {
            TekClientSyncCache.GuiSnapshotState snapshot = this.snapshot();
            int maxScroll = Math.max(0, sortedFilters(snapshot).size() - MAX_VISIBLE_FILTERS);
            this.filterScroll = Math.max(0, Math.min(maxScroll, this.filterScroll + (delta < 0 ? 1 : -1)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void renderMainLabels(MatrixStack matrixStack, VillagerView view, int panelX) {
        int x = panelX;
        int y = LEGACY_Y;
        this.font.draw(matrixStack, trim(view.displayName, 20), x + 27, y + 39, 0x404040);
        this.minecraft.getTextureManager().bind(GUI_MAIN_TEXTURE);
        this.drawStat(matrixStack, x, y, "health", 185, 29, 2, 52, view.health, view.maxHealth);
        this.drawStat(matrixStack, x, y, "hunger", 196, 29, 6, 63, view.hunger, 100.0F);
        this.drawStat(matrixStack, x, y, "happiness", 207, 29, 4, 74, view.happiness, 100.0F);
        this.drawStat(matrixStack, x, y, "intelligence", 218, 29, 0, 85, view.intelligence, 100.0F);

        if (view.skills.isEmpty()) {
            this.font.draw(matrixStack, trim(view.statusText(), 22), x + 110, y + 58, 0x404040);
        } else {
            this.drawSkillLevel(matrixStack, view.skills, 0, x + 110, y + 55);
            this.drawSkillLevel(matrixStack, view.skills, 1, x + 110, y + 73);
            this.drawSkillLevel(matrixStack, view.skills, 2, x + 145, y + 55);
            this.drawSkillLevel(matrixStack, view.skills, 3, x + 145, y + 73);
        }
    }

    private void renderAiLabels(MatrixStack matrixStack, TekClientSyncCache.GuiSnapshotState snapshot, VillagerView view, int panelX) {
        int x = panelX;
        int y = LEGACY_Y;
        List<String> filters = sortedFilters(snapshot);
        if (filters.isEmpty()) {
            this.font.draw(matrixStack, "No AI filters.", x + 20, y + 40, 0x404040);
            return;
        }

        int end = Math.min(filters.size(), this.filterScroll + MAX_VISIBLE_FILTERS);
        for (int i = this.filterScroll; i < end; i++) {
            String filter = filters.get(i);
            boolean enabled = snapshot.filters.getOrDefault(filter, true);
            int rowY = y + 39 + (i - this.filterScroll) * 11;
            this.minecraft.getTextureManager().bind(GUI_AI_TEXTURE);
            this.blit(matrixStack, x + 8, rowY, enabled ? 190 : 180, 57, 10, 10);
            this.font.draw(matrixStack, trim(humanize(filter), 24), x + 20, rowY + 1, 0x404040);
        }

        int scrollY = y + 39;
        if (filters.size() > MAX_VISIBLE_FILTERS) {
            float ratio = (float) this.filterScroll / (float) (filters.size() - MAX_VISIBLE_FILTERS);
            scrollY += (int) (97.0F * ratio);
        }
        this.minecraft.getTextureManager().bind(GUI_AI_TEXTURE);
        this.blit(matrixStack, x + 160, scrollY, filters.size() > MAX_VISIBLE_FILTERS ? 180 : 192, 38, 12, 15);
        this.font.draw(matrixStack, trim(view.displayName, 18), x + 48, y + 14, 0x404040);
    }

    private void renderTradeBackground(MatrixStack matrixStack) {
        int x = this.leftPos + 184;
        int y = this.topPos + LEGACY_Y + 6;
        fill(matrixStack, x, y, x + 86, y + 139, 0xFFC6C6C6);
        fill(matrixStack, x + 4, y + 18, x + 82, y + 73, 0xFF8D8D8D);
        fill(matrixStack, x + 5, y + 19, x + 81, y + 72, 0xFFB0B0B0);
        fill(matrixStack, x + 4, y + 80, x + 82, y + 118, 0xFF9A9A9A);
        fill(matrixStack, x + 5, y + 81, x + 81, y + 117, 0xFFCFCFCF);
        fill(matrixStack, x + 6, y + 124, x + 80, y + 142, 0xFF6F6F6F);
    }

    private void renderTradeLabels(MatrixStack matrixStack, TradeOffer offer) {
        int x = 184;
        int y = LEGACY_Y + 6;
        int emeralds = this.playerEmeraldCount();
        boolean affordable = emeralds >= offer.cost;
        this.font.draw(matrixStack, offer.vendorTitle, x + 8, y + 7, 0x404040);
        this.font.draw(matrixStack, "Cost", x + 10, y + 84, 0x404040);
        this.font.draw(matrixStack, String.valueOf(offer.cost), x + 42, y + 84, affordable ? 0x2F7D32 : 0xAA2020);
        this.font.draw(matrixStack, "Have", x + 10, y + 96, 0x404040);
        this.font.draw(matrixStack, String.valueOf(emeralds), x + 42, y + 96, affordable ? 0x404040 : 0xAA2020);
        this.font.draw(matrixStack, "Trade", x + 26, y + 129, 0xFFFFFF);
    }

    private void drawStat(
            MatrixStack matrixStack,
            int panelX,
            int panelY,
            String name,
            int iconU,
            int iconV,
            int colorIndex,
            int y,
            float value,
            float max
    ) {
        int fullWidth = 67;
        int filledWidth = max <= 0.0F ? 0 : Math.max(0, Math.min(fullWidth, (int) (value / max * fullWidth)));
        this.minecraft.getTextureManager().bind(GUI_MAIN_TEXTURE);
        this.blit(matrixStack, panelX + 8, panelY + y, iconU, iconV, 11, 9);
        this.blit(matrixStack, panelX + 24, panelY + y + 2, 182, 53 + colorIndex * 10, fullWidth, 5);
        if (filledWidth > 0) {
            this.blit(matrixStack, panelX + 24, panelY + y + 2, 182, 58 + colorIndex * 10, filledWidth, 5);
        }
        this.font.draw(matrixStack, String.valueOf(Math.round(value)), panelX + 93, panelY + y + 1, 0x404040);
    }

    private void drawSkillLevel(MatrixStack matrixStack, List<SkillLine> skills, int index, int x, int y) {
        if (index >= skills.size()) {
            return;
        }
        SkillLine skill = skills.get(index);
        this.font.draw(matrixStack, String.valueOf(skill.level), x + 28 - this.font.width(String.valueOf(skill.level)), y + 9, 0xFFFFFF);
    }

    private ItemStack renderMainItems(
            MatrixStack matrixStack,
            TekClientSyncCache.GuiSnapshotState snapshot,
            VillagerView view,
            Optional<TradeOffer> offer,
            int panelX,
            int mouseX,
            int mouseY
    ) {
        int x = this.leftPos + panelX;
        int y = this.topPos + LEGACY_Y;
        ItemStack hovered = ItemStack.EMPTY;
        hovered = this.renderItem(matrixStack, view.professionIcon, x + 20, y + 10, mouseX, mouseY, hovered);
        hovered = this.renderItem(matrixStack, view.professionIcon, x + 5, y + 34, mouseX, mouseY, hovered);

        for (int i = 0; i < Math.min(4, view.skills.size()); i++) {
            int skillX = i < 2 ? x + 110 : x + 145;
            int skillY = (i % 2 == 0) ? y + 55 : y + 73;
            hovered = this.renderItem(matrixStack, view.skills.get(i).icon, skillX, skillY, mouseX, mouseY, hovered);
        }

        if (snapshot != null) {
            hovered = this.renderInventoryItems(matrixStack, snapshot, x, y, mouseX, mouseY, hovered);
        }

        if (offer.isPresent()) {
            hovered = this.renderTradeItems(matrixStack, offer.get(), mouseX, mouseY, hovered);
        }
        return hovered;
    }

    private ItemStack renderAiItems(MatrixStack matrixStack, VillagerView view, int panelX, int mouseX, int mouseY) {
        int x = this.leftPos + panelX;
        int y = this.topPos + LEGACY_Y;
        return this.renderItem(matrixStack, view.professionIcon, x + 20, y + 10, mouseX, mouseY, ItemStack.EMPTY);
    }

    private ItemStack renderInventoryItems(
            MatrixStack matrixStack,
            TekClientSyncCache.GuiSnapshotState snapshot,
            int panelX,
            int panelY,
            int mouseX,
            int mouseY,
            ItemStack hovered
    ) {
        for (int slot = 0; slot < 27; slot++) {
            if (slot >= snapshot.inventory.size()) {
                continue;
            }
            ItemStack stack = snapshot.inventory.get(slot);
            if (stack.isEmpty()) {
                continue;
            }
            int x = panelX + 9 + (slot % 9) * 18;
            int y = panelY + 97 + (slot / 9) * 18;
            hovered = this.renderItem(matrixStack, stack, x, y, mouseX, mouseY, hovered);
        }
        return hovered;
    }

    private ItemStack renderTradeItems(MatrixStack matrixStack, TradeOffer offer, int mouseX, int mouseY, ItemStack hovered) {
        int x = this.leftPos + 184;
        int y = this.topPos + LEGACY_Y + 6;
        ItemStack cost = new ItemStack(Items.EMERALD, Math.max(1, offer.cost));
        hovered = this.renderItem(matrixStack, cost, x + 10, y + 36, mouseX, mouseY, hovered);
        this.drawTradeArrow(matrixStack, x + 36, y + 39);
        hovered = this.renderItem(matrixStack, offer.result, x + 58, y + 36, mouseX, mouseY, hovered);
        if (this.isMouseOver(mouseX, mouseY, x + 6, y + 124, 74, 18) && hovered.isEmpty()) {
            fill(matrixStack, x + 6, y + 124, x + 80, y + 142, 0x66305080);
        }
        return hovered;
    }

    private ItemStack renderItem(MatrixStack matrixStack, ItemStack stack, int x, int y, int mouseX, int mouseY, ItemStack hovered) {
        if (stack.isEmpty()) {
            return hovered;
        }
        this.itemRenderer.renderAndDecorateItem(stack, x, y);
        this.itemRenderer.renderGuiItemDecorations(this.font, stack, x, y);
        if (this.isMouseOver(mouseX, mouseY, x, y, 16, 16)) {
            fill(matrixStack, x, y, x + 16, y + 16, 0x66FFFFFF);
            return stack;
        }
        return hovered;
    }

    private void drawTradeArrow(MatrixStack matrixStack, int x, int y) {
        fill(matrixStack, x, y + 4, x + 15, y + 8, 0xFF5F5F5F);
        fill(matrixStack, x + 11, y, x + 15, y + 12, 0xFF5F5F5F);
        fill(matrixStack, x + 15, y + 2, x + 18, y + 10, 0xFF5F5F5F);
        fill(matrixStack, x + 18, y + 4, x + 20, y + 8, 0xFF5F5F5F);
    }

    private boolean isTradeButton(int localX, int localY) {
        return localX >= 190 && localX < 264 && localY >= LEGACY_Y + 130 && localY < LEGACY_Y + 148;
    }

    private String filterAt(TekClientSyncCache.GuiSnapshotState snapshot, int legacyX, int legacyY) {
        if (snapshot == null || legacyX < 8 || legacyX > 18 || legacyY < 39 || legacyY >= 149) {
            return "";
        }
        int row = (legacyY - 39) / 11;
        List<String> filters = sortedFilters(snapshot);
        int index = this.filterScroll + row;
        return index >= 0 && index < filters.size() ? filters.get(index) : "";
    }

    private int legacyPanelX(Optional<TradeOffer> offer) {
        return this.selectedView == VIEW_MAIN && offer.isPresent() ? 0 : LEGACY_CENTER_X;
    }

    private TekClientSyncCache.GuiSnapshotState snapshot() {
        return TekClientSyncCache.getGuiSnapshots().get(this.menu.getEntityId());
    }

    private int playerEmeraldCount() {
        PlayerEntity player = this.minecraft == null ? null : this.minecraft.player;
        if (player == null) {
            return 0;
        }
        int count = 0;
        for (int slot = 0; slot < player.inventory.getContainerSize(); slot++) {
            ItemStack stack = player.inventory.getItem(slot);
            if (!stack.isEmpty() && stack.getItem() == Items.EMERALD) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
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

    private static ItemStack professionIcon(String profession, Optional<TradeOffer> offer) {
        Item item = professionItem(profession);
        if (item != Items.AIR) {
            return new ItemStack(item);
        }
        return offer.map(tradeOffer -> tradeOffer.result.copy()).orElseGet(() -> new ItemStack(Items.EMERALD));
    }

    private static Item professionItem(String profession) {
        switch (normalize(profession)) {
            case "bard":
                return TekItems.PROF_BARD.get();
            case "blacksmith":
                return TekItems.PROF_BLACKSMITH.get();
            case "butcher":
                return TekItems.PROF_BUTCHER.get();
            case "chef":
                return TekItems.PROF_CHEF.get();
            case "cleric":
                return TekItems.PROF_CLERIC.get();
            case "druid":
                return TekItems.PROF_DRUID.get();
            case "enchanter":
                return TekItems.PROF_ENCHANTER.get();
            case "farmer":
                return TekItems.PROF_FARMER.get();
            case "guard":
                return TekItems.PROF_GUARD.get();
            case "captain":
                return TekItems.PROF_CAPTAIN.get();
            case "lumberjack":
                return TekItems.PROF_LUMBERJACK.get();
            case "miner":
                return TekItems.PROF_MINER.get();
            case "rancher":
                return TekItems.PROF_RANCHER.get();
            case "teacher":
                return TekItems.PROF_TEACHER.get();
            case "child":
                return TekItems.PROF_CHILD.get();
            case "nitwit":
                return TekItems.PROF_NITWIT.get();
            case "nomad":
                return TekItems.PROF_NOMAD.get();
            default:
                return Items.AIR;
        }
    }

    private static ItemStack stackFromRegistryName(String registryName) {
        if (registryName == null || registryName.isEmpty()) {
            return ItemStack.EMPTY;
        }
        try {
            ResourceLocation id = new ResourceLocation(registryName);
            Item item = ForgeRegistries.ITEMS.getValue(id);
            return item == null || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
        } catch (RuntimeException ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static String valueAfter(List<String> lines, String key) {
        if (lines == null) {
            return "";
        }
        for (String line : lines) {
            String value = valueAfter(line, key);
            if (!value.isEmpty()) {
                return value;
            }
        }
        return "";
    }

    private static String valueAfter(String line, String key) {
        if (line == null || key == null) {
            return "";
        }
        int start = line.indexOf(key);
        if (start < 0) {
            return "";
        }
        start += key.length();
        int end = line.indexOf(' ', start);
        if (end < 0) {
            end = line.length();
        }
        return line.substring(start, end).trim();
    }

    private static float floatAfter(List<String> lines, String key, float fallback) {
        String value = valueAfter(lines, key);
        if (value.isEmpty()) {
            return fallback;
        }
        int slash = value.indexOf('/');
        if (slash >= 0) {
            value = value.substring(0, slash);
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static float maxFloatAfter(List<String> lines, String key, float fallback) {
        String value = valueAfter(lines, key);
        int slash = value.indexOf('/');
        if (slash < 0 || slash + 1 >= value.length()) {
            return fallback;
        }
        try {
            return Float.parseFloat(value.substring(slash + 1));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int intAfter(List<String> lines, String key, int fallback) {
        String value = valueAfter(lines, key);
        if (value.isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static List<SkillLine> parseSkills(List<String> lines) {
        if (lines == null) {
            return Collections.emptyList();
        }
        for (String line : lines) {
            if (!line.startsWith("Skills ")) {
                continue;
            }
            String value = line.substring("Skills ".length()).trim();
            if (value.isEmpty() || "-".equals(value)) {
                return Collections.emptyList();
            }
            List<SkillLine> skills = new ArrayList<>();
            String[] entries = value.split(",");
            for (String entry : entries) {
                String[] parts = entry.trim().split("=");
                if (parts.length != 2) {
                    continue;
                }
                try {
                    String profession = normalize(parts[0]);
                    int level = Integer.parseInt(parts[1]);
                    Item item = professionItem(profession);
                    if (level > 0 && item != Items.AIR) {
                        skills.add(new SkillLine(profession, level, new ItemStack(item)));
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore malformed debug snapshot values and keep the GUI renderable.
                }
            }
            skills.sort(Comparator.comparingInt((SkillLine skill) -> skill.level).reversed());
            return skills;
        }
        return Collections.emptyList();
    }

    private static String trim(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static String humanize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return titleCase(value.replace('_', ' '));
    }

    private static String titleCase(String value) {
        String normalized = value == null ? "" : value.trim().replace('_', ' ');
        if (normalized.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String part : normalized.split(" ")) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return builder.toString();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static final class VillagerView {
        private final int entityId;
        private final String profession;
        private final String displayName;
        private final String status;
        private final float health;
        private final float maxHealth;
        private final float hunger;
        private final float happiness;
        private final float intelligence;
        private final List<SkillLine> skills;
        private final ItemStack professionIcon;

        private VillagerView(
                int entityId,
                String profession,
                String status,
                float health,
                float maxHealth,
                float hunger,
                float happiness,
                float intelligence,
                List<SkillLine> skills,
                ItemStack professionIcon
        ) {
            this.entityId = entityId;
            this.profession = profession;
            this.status = status;
            this.health = health;
            this.maxHealth = maxHealth;
            this.hunger = hunger;
            this.happiness = happiness;
            this.intelligence = intelligence;
            this.skills = skills;
            this.professionIcon = professionIcon;
            String title = titleCase(profession);
            this.displayName = (title.isEmpty() ? "TekTopia Villager" : title) + " #" + entityId;
        }

        private static VillagerView from(TekClientSyncCache.GuiSnapshotState snapshot, int entityId) {
            List<String> lines = snapshot == null ? Collections.emptyList() : snapshot.lines;
            Optional<TradeOffer> offer = TradeOffer.from(snapshot);
            String profession = valueAfter(lines, "profession=");
            if (profession.isEmpty()) {
                profession = "villager";
            }
            String status = valueAfter(lines, "status=");
            float health = floatAfter(lines, "health=", 0.0F);
            float maxHealth = maxFloatAfter(lines, "health=", 20.0F);
            float hunger = intAfter(lines, "hunger=", 0);
            float happiness = intAfter(lines, "happiness=", 0);
            float intelligence = intAfter(lines, "intelligence=", 0);
            return new VillagerView(
                    entityId,
                    profession,
                    status,
                    health,
                    maxHealth,
                    hunger,
                    happiness,
                    intelligence,
                    parseSkills(lines),
                    professionIcon(profession, offer)
            );
        }

        private String statusText() {
            return humanize(this.status);
        }
    }

    private static final class SkillLine {
        private final String profession;
        private final int level;
        private final ItemStack icon;

        private SkillLine(String profession, int level, ItemStack icon) {
            this.profession = profession;
            this.level = level;
            this.icon = icon;
        }
    }

    private static final class TradeOffer {
        private final String action;
        private final String vendorTitle;
        private final int cost;
        private final int tier;
        private final ItemStack result;

        private TradeOffer(String action, String vendorTitle, int cost, int tier, ItemStack result) {
            this.action = action;
            this.vendorTitle = vendorTitle;
            this.cost = cost;
            this.tier = tier;
            this.result = result;
        }

        private static Optional<TradeOffer> from(TekClientSyncCache.GuiSnapshotState snapshot) {
            if (snapshot == null || snapshot.lines.isEmpty()) {
                return Optional.empty();
            }
            for (String line : snapshot.lines) {
                if (!line.startsWith("Trade ") || !line.contains(" action=")) {
                    continue;
                }
                String action = valueAfter(line, "action=");
                int cost = "profession_token".equals(action)
                        ? intFromLine(line, "professionCost=", 0)
                        : intFromLine(line, "structureCost=", 0);
                ItemStack result = stackFromRegistryName(valueAfter(line, "next="));
                if (action.isEmpty() || cost <= 0 || result.isEmpty()) {
                    continue;
                }
                String vendorTitle = TekTradeActions.ACTION_PROFESSION_TOKEN.equals(action) ? "Tradesman" : "Architect";
                return Optional.of(new TradeOffer(action, vendorTitle, cost, intFromLine(line, "tier=", 0), result));
            }
            return Optional.empty();
        }

        private static int intFromLine(String line, String key, int fallback) {
            String value = valueAfter(line, key);
            if (value.isEmpty()) {
                return fallback;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
    }
}
