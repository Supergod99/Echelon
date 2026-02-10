package elocindev.tierify.forge.screen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import elocindev.tierify.forge.client.TierGradientAnimatorForge;
import elocindev.tierify.forge.network.ForgeNetwork;
import elocindev.tierify.forge.network.c2s.TrySalvageC2S;
import elocindev.tierify.forge.network.c2s.OpenSalvageUpgradeC2S;
import elocindev.tierify.forge.screen.SalvageMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SalvageScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<SalvageMenu> {

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/salvaging_screen.png");
    private static final ResourceLocation UPGRADE_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/salvage_upgrade_icon2.png");
    private static final ResourceLocation UPGRADE_PANEL =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/upgradeslabel.png");
    private static final int INFO_U = 176;
    private static final int INFO_V = 18;
    private static final int ICON_W = 25;
    private static final int ICON_H = 17;
    private static final int PLATE_PAD_X = 4;
    private static final int PLATE_PAD_Y = 3;
    private static final int UPGRADE_W = ICON_W + (PLATE_PAD_X * 2);
    private static final int UPGRADE_H = ICON_H + (PLATE_PAD_Y * 2);
    private static final int PLATE_U = 176;
    private static final int PLATE_V = 36;
    private static final int PANEL_W = 51;
    private static final int PANEL_H = 64;
    private static final int ODDS_PANEL_W = PANEL_W;
    private static final int ODDS_PANEL_H = PANEL_H;
    private static final int ODDS_PANEL_Y = 0;
    private static final int ODDS_PANEL_RIGHT_PAD = 6;
    private static final int ODDS_TEXT_PAD_X = 8;
    private static final float ODDS_SCALE = 0.7f;
    private static final int ODDS_LINE_START_Y = 10;
    private static final int ODDS_LINE_GAP = 11;
    private static final int INFO_W = 18;
    private static final int INFO_H = 18;
    private static final int INFO_GAP_Y = 16;

    private SalvageButton salvageButton;
    private UpgradeChip upgradeChip;
    private InfoButton infoButton;
    private boolean showOddsPanel;

    public SalvageScreen(SalvageMenu menu, Inventory inv, Component title) {
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

        this.salvageButton = new SalvageButton(left + 79, top + 47);
        this.addRenderableWidget(this.salvageButton);

        int chipX = left + imageWidth - UPGRADE_W - 12;
        int chipY = top + 9;
        this.upgradeChip = new UpgradeChip(chipX, chipY);
        this.addRenderableWidget(this.upgradeChip);

        int infoX = chipX + (UPGRADE_W - INFO_W) / 2 + 2;
        int infoY = chipY + UPGRADE_H + INFO_GAP_Y - 2;
        this.infoButton = new InfoButton(infoX, infoY);
        this.addRenderableWidget(this.infoButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (salvageButton != null) {
            salvageButton.setDisabled(!menu.isSalvageReady());
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
        renderSalvageHeader(gg);
        if (showOddsPanel) {
            drawOddsSummary(gg, leftPos + getOddsPanelX(), topPos + ODDS_PANEL_Y, menu.getSalvageLevel());
        }
        renderTooltip(gg, mouseX, mouseY);
        renderOddsRowTooltip(gg, mouseX, mouseY);

        if (isPointWithinBounds(79, 47, 18, 18, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            int upgradeTier = menu.getUpgradeTargetTier();
            if (upgradeTier > 0) {
                tooltip.add(Component.translatable("screen.tiered.salvage_upgrade", upgradeTier));
            } else if (menu.isSalvageReady()) {
                tooltip.add(Component.translatable("screen.tiered.salvage_action"));
            }
            if (!tooltip.isEmpty()) {
                List<FormattedCharSequence> lines = tooltip.stream()
                        .flatMap(c -> this.font.split(c, 200).stream())
                        .toList();
                gg.renderTooltip(this.font, lines, mouseX, mouseY);
            }
        }

        if (isPointWithinBounds(upgradeChip.getX() - leftPos, upgradeChip.getY() - topPos, UPGRADE_W, UPGRADE_H, mouseX, mouseY)) {
            List<FormattedCharSequence> lines = this.font
                    .split(Component.translatable("screen.tiered.salvage_upgrades"), 200);
            gg.renderTooltip(this.font, lines, mouseX, mouseY);
        }

        if (isPointWithinBounds(infoButton.getX() - leftPos, infoButton.getY() - topPos, INFO_W, INFO_H, mouseX, mouseY)) {
            List<FormattedCharSequence> lines = this.font
                    .split(Component.literal("Odds"), 200);
            gg.renderTooltip(this.font, lines, mouseX, mouseY);
        }
    }

    private void renderSalvageHeader(GuiGraphics gg) {
        int level = menu.getSalvageLevel();
        Component text = buildTierDisplay(level);
        int x = leftPos + (imageWidth - this.font.width(text)) / 2;
        gg.drawString(this.font, text, x, topPos + 32, 0x404040, false);
    }

    private static Component buildTierDisplay(int level) {
        if (level <= 0) {
            return Component.translatable("screen.tiered.salvage_tier")
                    .append(Component.literal("Locked"));
        }
        if (level > 6) {
            Component label = TierGradientAnimatorForge.animate(Component.literal("Mythic +" + (level - 6)), 5);
            return Component.translatable("screen.tiered.salvage_tier")
                    .append(label);
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
        Component animated = TierGradientAnimatorForge.animate(label, tierIndex);
        return Component.translatable("screen.tiered.salvage_tier").append(animated);
    }

    private final class SalvageButton extends AbstractWidget {
        private boolean disabled = true;

        private SalvageButton(int x, int y) {
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
            if (!disabled && menu.isSalvageReady()) {
                ForgeNetwork.CHANNEL.sendToServer(new TrySalvageC2S());
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {
            // no-op
        }
    }

    private final class UpgradeChip extends AbstractWidget {
        private boolean pressed = false;

        private UpgradeChip(int x, int y) {
            super(x, y, UPGRADE_W, UPGRADE_H, Component.empty());
        }

        @Override
        protected void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
            float shade = pressed ? 0.9f : (this.isHovered() ? 1.05f : 1.0f);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            int plateU = PLATE_U + ((pressed || this.isHovered()) ? UPGRADE_W : 0);
            gg.blit(TEX, getX(), getY(), plateU, PLATE_V, UPGRADE_W, UPGRADE_H, 256, 256);

            RenderSystem.setShaderColor(shade, shade, shade, 1f);
            int pressOffset = pressed ? 1 : 0;
            int iconX = getX() + PLATE_PAD_X + pressOffset;
            int iconY = getY() + PLATE_PAD_Y + pressOffset;
            gg.blit(UPGRADE_ICON, iconX, iconY, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);
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
                    ForgeNetwork.CHANNEL.sendToServer(new OpenSalvageUpgradeC2S());
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

    private void drawScaledText(GuiGraphics gg, String text, int x, int y, int color, float scale) {
        gg.pose().pushPose();
        gg.pose().scale(scale, scale, 1.0f);
        int scaledX = Math.round(x / scale);
        int scaledY = Math.round(y / scale);
        gg.drawString(this.font, text, scaledX, scaledY, color, false);
        gg.pose().popPose();
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
