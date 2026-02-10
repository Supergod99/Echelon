package elocindev.tierify.forge.screen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import elocindev.tierify.forge.client.TierGradientAnimatorForge;
import elocindev.tierify.forge.network.ForgeNetwork;
import elocindev.tierify.forge.network.c2s.OpenSalvageFromAnvilC2S;
import elocindev.tierify.forge.network.c2s.TrySalvageUpgradeC2S;
import elocindev.tierify.forge.screen.SalvageMenu;
import elocindev.tierify.forge.screen.SalvageUpgradeMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SalvageUpgradeScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<SalvageUpgradeMenu> {

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/upgrading_screen.png");
    private static final ResourceLocation UPGRADE_PANEL =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/upgradeslabel.png");
    private static final ResourceLocation BACK_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/backbuttonsalvage.png");
    private static final int PANEL_W = 51;
    private static final int PANEL_H = 64;
    private static final int PANEL_X = 4;
    private static final int PANEL_Y = 2;
    private static final int ICON_W = 25;
    private static final int ICON_H = 17;
    private static final int PLATE_PAD_X = 4;
    private static final int PLATE_PAD_Y = 3;
    private static final int BACK_W = ICON_W + (PLATE_PAD_X * 2);
    private static final int BACK_H = ICON_H + (PLATE_PAD_Y * 2);
    private static final int BACK_PLATE_U = 176;
    private static final int BACK_PLATE_V = 36;
    private static final float TIER_SCALE = 0.65f;
    private static final float TITLE_SCALE = 0.58f;
    private static final int TEXT_BLOCK_OFFSET_Y = 6;
    private static final int TIER_TITLE_Y = 10 + TEXT_BLOCK_OFFSET_Y;
    private static final int TIER_LABEL_Y = 20 + TEXT_BLOCK_OFFSET_Y;
    private static final int COST_TITLE_Y = 32 + TEXT_BLOCK_OFFSET_Y;
    private static final int COST_LINE_Y = 42 + TEXT_BLOCK_OFFSET_Y;
    private static final int COST_ICON_SIZE = 16;
    private static final int COST_ICON_GAP = 2;
    private static final int ODDS_PANEL_W = PANEL_W;
    private static final int ODDS_PANEL_H = PANEL_H;
    private static final int ODDS_PANEL_Y = PANEL_Y - 2;
    private static final int ODDS_PANEL_RIGHT_PAD = 6;
    private static final int ODDS_TEXT_PAD_X = 8;
    private static final float ODDS_SCALE = 0.7f;
    private static final int ODDS_LINE_START_Y = 10;
    private static final int ODDS_LINE_GAP = 11;
    private static final int INFO_U = 176;
    private static final int INFO_V = 18;
    private static final int INFO_W = 18;
    private static final int INFO_H = 18;
    private static final int INFO_GAP_Y = 16;

    private UpgradeButton upgradeButton;
    private BackChip backChip;
    private InfoButton infoButton;
    private boolean showOddsPanel;

    public SalvageUpgradeScreen(SalvageUpgradeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 60;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top = this.topPos;

        this.upgradeButton = new UpgradeButton(left + 79, top + 56);
        this.addRenderableWidget(this.upgradeButton);

        int backX = left + imageWidth - BACK_W - 12;
        int backY = top + 9;
        this.backChip = new BackChip(backX, backY);
        this.addRenderableWidget(this.backChip);

        int infoX = backX + (BACK_W - INFO_W) / 2 + 2;
        int infoY = backY + BACK_H + INFO_GAP_Y - 2;
        this.infoButton = new InfoButton(infoX, infoY);
        this.addRenderableWidget(this.infoButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (upgradeButton != null) {
            upgradeButton.setDisabled(!menu.isUpgradeReady());
        }
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partial, int mouseX, int mouseY) {
        gg.blit(TEX, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (showOddsPanel) {
            gg.blit(UPGRADE_PANEL, leftPos + getOddsPanelX(), topPos + ODDS_PANEL_Y, 0, 0, ODDS_PANEL_W, ODDS_PANEL_H, PANEL_W, PANEL_H);
        }
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partial) {
        renderBackground(gg);
        super.render(gg, mouseX, mouseY, partial);
        renderTooltip(gg, mouseX, mouseY);
        renderOddsRowTooltip(gg, mouseX, mouseY);

        if (isPointWithinBounds(79, 56, 18, 18, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            int upgradeTier = menu.getUpgradeTargetTier();
            if (upgradeTier > 0) {
                tooltip.add(Component.translatable("screen.tiered.salvage_upgrade", upgradeTier));
            }
            if (!tooltip.isEmpty()) {
                List<FormattedCharSequence> lines = tooltip.stream()
                        .flatMap(c -> this.font.split(c, 200).stream())
                        .toList();
                gg.renderTooltip(this.font, lines, mouseX, mouseY);
            }
        }

        if (isPointWithinBounds(backChip.getX() - leftPos, backChip.getY() - topPos, BACK_W, BACK_H, mouseX, mouseY)) {
            List<FormattedCharSequence> lines = this.font
                    .split(Component.translatable("screen.tiered.salvage_back"), 200);
            gg.renderTooltip(this.font, lines, mouseX, mouseY);
        }

        if (isPointWithinBounds(infoButton.getX() - leftPos, infoButton.getY() - topPos, INFO_W, INFO_H, mouseX, mouseY)) {
            List<FormattedCharSequence> lines = this.font
                    .split(Component.literal("Odds"), 200);
            gg.renderTooltip(this.font, lines, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
        Component header = Component.translatable("screen.tiered.salvage_upgrades");
        int headerX = (this.imageWidth - this.font.width(header)) / 2;
        gg.drawString(this.font, header, headerX, 6, 0x404040, false);
        int panelX = PANEL_X;
        int panelY = PANEL_Y;
        drawTierLabel(gg, panelX, panelY, PANEL_W, menu.getSalvageLevel());
        drawUpgradeCost(gg, panelX, panelY, PANEL_W, menu.getSalvageLevel());
        if (showOddsPanel) {
            drawOddsSummary(gg, getOddsPanelX(), ODDS_PANEL_Y, menu.getSalvageLevel());
        }
        gg.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    private void drawTierLabel(GuiGraphics gg, int panelX, int panelY, int panelW, int level) {
        Component prefixComponent = Component.translatable("screen.tiered.salvage_tier");
        String prefixText = prefixComponent.getString().trim();
        if (prefixText.endsWith(":")) {
            prefixText = prefixText.substring(0, prefixText.length() - 1).trim();
        }
        if (prefixText.isEmpty()) {
            prefixText = "Tier";
        }
        Component prefix = Component.literal(prefixText);
        Component tierLabel = buildTierLabel(level);
        drawScaledCentered(gg, prefix, panelX, panelW, panelY + TIER_TITLE_Y, 0x404040, TITLE_SCALE);
        int tierColor = level <= 0 ? 0x404040 : 0xFFFFFF;
        drawScaledCentered(gg, tierLabel, panelX, panelW, panelY + TIER_LABEL_Y, tierColor, TIER_SCALE);
    }

    private void drawUpgradeCost(GuiGraphics gg, int panelX, int panelY, int panelW, int level) {
        Component title = Component.literal("Upgrade Cost");
        drawScaledCentered(gg, title, panelX, panelW, panelY + COST_TITLE_Y, 0x404040, TITLE_SCALE);

        int maxTier = SalvageMenu.getConfigMaxTier();
        if (level >= maxTier) {
            drawScaledCentered(gg, Component.literal("Max"), panelX, panelW, panelY + COST_LINE_Y, 0x404040, TIER_SCALE);
            return;
        }

        int targetTier = level + 1;
        int count = menu.getUpgradeRemainingCost();
        ItemStack material = SalvageMenu.getMaterialForTier(targetTier);
        String countText = count + "x";
        int textWidth = Math.round(this.font.width(countText) * TIER_SCALE);
        int totalWidth = textWidth + COST_ICON_GAP + COST_ICON_SIZE;
        int startX = panelX + (panelW - totalWidth) / 2;
        int textY = panelY + COST_LINE_Y;
        drawScaledText(gg, countText, startX, textY, 0x404040, TIER_SCALE);

        if (!material.isEmpty()) {
            int iconX = startX + textWidth + COST_ICON_GAP;
            int iconY = textY - 4;
            gg.renderItem(material, iconX, iconY);
        }
    }

    private void drawScaledCentered(GuiGraphics gg, Component text, int panelX, int panelW, int y, int color, float scale) {
        gg.pose().pushPose();
        gg.pose().scale(scale, scale, 1.0f);
        int centerX = Math.round((panelX + (panelW / 2.0f)) / scale);
        int scaledY = Math.round(y / scale);
        int textX = centerX - (this.font.width(text) / 2);
        gg.drawString(this.font, text, textX, scaledY, color, false);
        gg.pose().popPose();
    }

    private void drawScaledText(GuiGraphics gg, String text, int x, int y, int color, float scale) {
        gg.pose().pushPose();
        gg.pose().scale(scale, scale, 1.0f);
        int scaledX = Math.round(x / scale);
        int scaledY = Math.round(y / scale);
        gg.drawString(this.font, text, scaledX, scaledY, color, false);
        gg.pose().popPose();
    }

    private void drawOddsSummary(GuiGraphics gg, int panelX, int panelY, int level) {
        SalvageMenu.SalvageOdds odds = SalvageMenu.getSalvageOddsForLevel(level);
        String[] lines = new String[] {
                "Fail " + formatPercent(odds.none()),
                "Lower " + formatPercent(odds.lower()),
                "Same " + formatPercent(odds.same()),
                "Higher " + formatPercent(odds.higher())
        };

        int lineY = panelY + ODDS_LINE_START_Y;
        int textX = panelX + ODDS_TEXT_PAD_X;
        drawScaledText(gg, lines[0], textX, lineY, 0xFFE34A4A, ODDS_SCALE);
        lineY += ODDS_LINE_GAP;
        drawScaledText(gg, lines[1], textX, lineY, 0xFF9E3B3B, ODDS_SCALE);
        lineY += ODDS_LINE_GAP;
        drawScaledText(gg, lines[2], textX, lineY, 0xFF3D7C3D, ODDS_SCALE);
        lineY += ODDS_LINE_GAP;
        drawScaledText(gg, lines[3], textX, lineY, 0xFF4FC94F, ODDS_SCALE);
    }

    private int getOddsPanelX() {
        return this.imageWidth + ODDS_PANEL_RIGHT_PAD;
    }

    private void renderOddsRowTooltip(GuiGraphics gg, int mouseX, int mouseY) {
        if (!showOddsPanel) return;

        int panelX = this.leftPos + getOddsPanelX();
        int panelY = this.topPos + ODDS_PANEL_Y;
        int relX = mouseX - panelX;
        int relY = mouseY - panelY;
        if (relX < 0 || relX >= ODDS_PANEL_W || relY < 0 || relY >= ODDS_PANEL_H) return;

        int row = getOddsRowIndex(relY);
        if (row < 0) return;

        Component title;
        Component desc;
        switch (row) {
            case 0 -> {
                title = Component.translatable("screen.tiered.odds.fail.title");
                desc = Component.translatable("screen.tiered.odds.fail.desc");
            }
            case 1 -> {
                title = Component.translatable("screen.tiered.odds.lower.title");
                desc = Component.translatable("screen.tiered.odds.lower.desc");
            }
            case 2 -> {
                title = Component.translatable("screen.tiered.odds.same.title");
                desc = Component.translatable("screen.tiered.odds.same.desc");
            }
            case 3 -> {
                title = Component.translatable("screen.tiered.odds.higher.title");
                desc = Component.translatable("screen.tiered.odds.higher.desc");
            }
            default -> {
                return;
            }
        }

        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.addAll(this.font.split(title, 220));
        lines.addAll(this.font.split(desc, 220));
        gg.renderTooltip(this.font, lines, mouseX, mouseY);
    }

    private static int getOddsRowIndex(int relY) {
        int start = ODDS_LINE_START_Y - 2;
        int rowHeight = ODDS_LINE_GAP;
        for (int i = 0; i < 4; i++) {
            int y0 = start + (i * rowHeight);
            if (relY >= y0 && relY < y0 + rowHeight) {
                return i;
            }
        }
        return -1;
    }

    public List<Rect2i> getJeiExtraAreas() {
        if (!showOddsPanel) return Collections.emptyList();
        return List.of(new Rect2i(
                this.leftPos + getOddsPanelX(),
                this.topPos + ODDS_PANEL_Y,
                ODDS_PANEL_W,
                ODDS_PANEL_H
        ));
    }

    private static String formatPercent(double value) {
        double percent = value * 100.0;
        double rounded = Math.round(percent * 10.0) / 10.0;
        if (Math.abs(rounded - Math.round(rounded)) < 0.001) {
            return String.format(Locale.ROOT, "%.0f%%", rounded);
        }
        return String.format(Locale.ROOT, "%.1f%%", rounded);
    }

    private static Component buildTierLabel(int level) {
        if (level <= 0) {
            return Component.literal("Locked");
        }
        if (level > 6) {
            Component label = Component.literal("Mythic +" + (level - 6));
            return TierGradientAnimatorForge.animate(label, 5);
        }
        int clamped = Math.max(1, Math.min(level, 6));
        int tierIndex = clamped - 1;
        Component label = switch (clamped) {
            case 2 -> Component.translatable("tiered:uncommon.label");
            case 3 -> Component.translatable("tiered:rare.label");
            case 4 -> Component.translatable("tiered:epic.label");
            case 5 -> Component.translatable("tiered:legendary.label");
            case 6 -> Component.translatable("tiered:mythic.label");
            case 1 -> Component.translatable("tiered:common.label");
            default -> Component.translatable("tiered:common.label");
        };
        return TierGradientAnimatorForge.animate(label, tierIndex);
    }

    private final class InfoButton extends AbstractWidget {
        private boolean pressed = false;

        private InfoButton(int x, int y) {
            super(x, y, INFO_W, INFO_H, Component.empty());
        }

        @Override
        protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int u = INFO_U + ((pressed || this.isHovered()) ? INFO_W : 0);
            gg.blit(TEX, getX(), getY(), u, INFO_V, INFO_W, INFO_H, 256, 256);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && this.isMouseOver(mouseX, mouseY)) {
                pressed = true;
                showOddsPanel = !showOddsPanel;
                playClickSound();
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (button == 0 && pressed) {
                pressed = false;
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {
            // no-op
        }
    }

    private final class UpgradeButton extends AbstractWidget {
        private boolean disabled = true;

        private UpgradeButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        @Override
        protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            int u = 176;
            if (disabled) {
                u += this.width * 2;
            } else if (this.isHovered()) {
                u += this.width;
            }

            gg.blit(TEX, getX(), getY(), u, 0, this.width, this.height, 256, 256);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (!disabled && menu.isUpgradeReady()) {
                playClickSound();
                ForgeNetwork.CHANNEL.sendToServer(new TrySalvageUpgradeC2S());
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {
            // no-op
        }
    }

    private final class BackChip extends AbstractWidget {
        private boolean pressed = false;

        private BackChip(int x, int y) {
            super(x, y, BACK_W, BACK_H, Component.empty());
        }

        @Override
        protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
            float shade = pressed ? 0.9f : (this.isHovered() ? 1.05f : 1.0f);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            int plateU = BACK_PLATE_U + ((pressed || this.isHovered()) ? BACK_W : 0);
            gg.blit(TEX, getX(), getY(), plateU, BACK_PLATE_V, BACK_W, BACK_H, 256, 256);

            RenderSystem.setShaderColor(shade, shade, shade, 1f);
            int pressOffset = pressed ? 1 : 0;
            int iconX = getX() + PLATE_PAD_X + pressOffset;
            int iconY = getY() + PLATE_PAD_Y + pressOffset;
            gg.blit(BACK_ICON, iconX, iconY, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && this.isMouseOver(mouseX, mouseY)) {
                this.pressed = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (button == 0 && this.pressed) {
                this.pressed = false;
                if (this.isMouseOver(mouseX, mouseY)) {
                    playClickSound();
                    ForgeNetwork.CHANNEL.sendToServer(new OpenSalvageFromAnvilC2S());
                }
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {
            // no-op
        }

    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, int mouseX, int mouseY) {
        int relX = mouseX - this.leftPos;
        int relY = mouseY - this.topPos;
        return relX >= x && relX < x + width && relY >= y && relY < y + height;
    }

    private void playClickSound() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
}
