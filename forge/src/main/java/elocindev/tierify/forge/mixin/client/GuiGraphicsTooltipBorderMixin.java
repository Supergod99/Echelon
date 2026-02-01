package elocindev.tierify.forge.mixin.client;

import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.client.PerfectLabelAnimatorForge;
import elocindev.tierify.forge.client.TierGradientAnimatorForge;
import elocindev.tierify.forge.client.TierifyTooltipBorderRendererForge;
import elocindev.tierify.forge.compat.TooltipOverhaulCompatForge;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.item.ReforgeAddition;
import elocindev.tierify.util.StarApexUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Forge tooltip hook:
 * - draws Tierify tooltip borders above the vanilla background
 * - reserves space and renders Perfect label below the item name (centered + scaled + animated gradient)
 * - (Apex) reserves space for future top-line elements
 *
 * IMPORTANT: Forge/vanilla sometimes passes an immutable tooltip component list (List.of(...)).
 * We must replace it with a mutable copy before inserting spacer components, or the game will crash.
 */
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsTooltipBorderMixin {

    // Added by Forge patch on 1.20.x; used to track which ItemStack the tooltip belongs to.
    @Shadow private ItemStack tooltipStack;

    @Unique private static final float TIERIFY_LABEL_SCALE = 0.65f;
    @Unique private static final float SET_BONUS_LABEL_NUDGE_Y = -1.0f;

    @Unique private static final int TIERIFY_BASE_SPACER_PX = 2; // +2 from vanilla's first-line gap yields 4px top padding like Fabric
    @Unique private static final int TIERIFY_STAR_BAND_PX = 12;
    @Unique private static final int TIERIFY_SETBONUS_EXTRA_PX = 6; // Extra header band height for Set Bonus label
    @Unique private static final int TIERIFY_PERFECT_SPACER_PX = 6;
    @Unique private static final int SET_BONUS_CREST_SIZE = 8;
    @Unique private static final int SET_BONUS_CREST_TEX_SIZE = 8;
    @Unique private static final float SET_BONUS_CREST_SCALE = 0.6f;
    @Unique private static final int SET_BONUS_CREST_GAP = 4;
    @Unique private static final int STAR_RIBBON_GAP_PX = 2;
    @Unique private static final float STAR_RIBBON_SCALE = 0.75f;
    @Unique private static final float STAR_ICON_SCALE = 0.5f;
    @Unique private static final float STAR_BORDER_SCALE = STAR_ICON_SCALE;
    @Unique private static final int STAR_RIBBON_PAD_X = 6;
    @Unique private static final int STAR_GAP_PX = 2;
    @Unique private static final int APEX_STAR_COUNT = 3;
    @Unique private static final int APEX_CREST_EXTRA_PX = 2;
    @Unique private static final int APEX_CREST_GAP_PX = 4;
    @Unique private static final float APEX_CREST_SCALE = 1.20f;
    @Unique private static final int APEX_GLOW_COLOR_RGB = 0xF39C38;
    @Unique private static final int APEX_GLOW_ALPHA_MIN = 40;
    @Unique private static final int APEX_GLOW_ALPHA_MAX = 120;
    @Unique private static final double APEX_GLOW_PULSE_MS = 350.0;
    @Unique private static final int APEX_SWEEP_COLOR_RGB = 0xF7C45A;
    @Unique private static final int APEX_SWEEP_EDGE_RGB = 0xC74A2C;
    @Unique private static final int APEX_PERFECT_SWEEP_COLOR_RGB = 0xE8FFFF;
    @Unique private static final int APEX_PERFECT_SWEEP_EDGE_RGB = 0x8FE3FF;
    @Unique private static final int APEX_SWEEP_ALPHA_MAX = 200;
    @Unique private static final float APEX_SWEEP_LEN_FRAC = 0.35f;
    @Unique private static final double APEX_SWEEP_PERIOD_MS = 8000.0;
    @Unique private static final int APEX_SWEEP_THICKNESS = 1;
    @Unique private static final int RIBBON_LEFT_W = 24;
    @Unique private static final int RIBBON_MID_W = 20;
    @Unique private static final int RIBBON_RIGHT_W = 24;
    @Unique private static final int RIBBON_H = 10;
    @Unique private static final int STAR_TEX_W = 17;
    @Unique private static final int STAR_TEX_H = 16;
    @Unique private static final long APEX_SHIMMER_STEP_MS = 250L;
    @Unique private static final float APEX_SHIMMER_SCALE_MIN = 0.94f;
    @Unique private static final float APEX_SHIMMER_SCALE_MAX = 1.14f;
    @Unique private static final float APEX_SHIMMER_ALPHA_BASE = 0.75f;
    @Unique private static final float APEX_SHIMMER_ALPHA_PEAK = 1.0f;
    @Unique private static final float APEX_SHIMMER_WIDTH = 1.2f;
    @Unique private static final float APEX_SHIMMER_WARM_R = 1.0f;
    @Unique private static final float APEX_SHIMMER_WARM_G = 0.95f;
    @Unique private static final float APEX_SHIMMER_WARM_B = 0.85f;
    @Unique private static final float APEX_SHIMMER_COOL_R = 0.95f;
    @Unique private static final float APEX_SHIMMER_COOL_G = 1.0f;
    @Unique private static final float APEX_SHIMMER_COOL_B = 1.0f;
    @Unique private static final ResourceLocation APEX_CROWN_LEFT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/crown/crown_left.png");
    @Unique private static final ResourceLocation APEX_CROWN_MID =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/crown/crown_center.png");
    @Unique private static final ResourceLocation APEX_CROWN_RIGHT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/crown/crown_right.png");
    @Unique private static final int APEX_PLATE_TEX_W = 68;
    @Unique private static final int APEX_PLATE_TEX_H = 20;
    @Unique private static final int APEX_PLATE_PAD_X = 6;
    @Unique private static final int APEX_PLATE_LEFT_W = 24;
    @Unique private static final int APEX_PLATE_MID_W = 20;
    @Unique private static final int APEX_PLATE_RIGHT_W = 24;
    @Unique private static final float APEX_PLATE_HEIGHT_SCALE = 0.8f;
    @Unique private static final float APEX_PLATE_STAR_SCALE = 1.75f;
    @Unique private static final float APEX_CREST_APEX_SCALE = 1.05f;
    @Unique private static final ResourceLocation STAR_RIBBON_LEFT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/ribbon/ribbon_left.png");
    @Unique private static final ResourceLocation STAR_RIBBON_MID =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/ribbon/ribbon_mid.png");
    @Unique private static final ResourceLocation STAR_RIBBON_RIGHT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/ribbon/ribbon_right.png");
    @Unique private static final ResourceLocation STAR_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/star.png");
    @Unique private static final ResourceLocation PERFECT_STAR_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/perfect_star.png");
    @Unique private static final ResourceLocation SET_BONUS_CREST_ACTIVE =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/setbonusicon_active.png");
    @Unique private static final ResourceLocation APEX_CREST =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/item/apex_crux.png");
    @Unique private static final int APEX_CREST_TEX_W = 36;
    @Unique private static final int APEX_CREST_TEX_H = 36;

    @Unique private int tierify$tooltipX;
    @Unique private int tierify$tooltipWidth;
    @Unique private int tierify$renderX;
    @Unique private int tierify$renderY;
    @Unique private int tierify$renderW;
    @Unique private int tierify$renderH;
    @Unique private int tierify$centerTitleIndex = -1;
    @Unique private int tierify$titleTextY = Integer.MIN_VALUE;
    @Unique private int tierify$titleLineCount = 1;

    /**
     * Lightweight tooltip component used only to reserve a small amount of vertical space.
     * Vanilla's ClientTextTooltip reserves a full font line (9px) even when empty, which is too tall for 0.65-scaled labels.
     */
    @Unique
    private static final class TierifySpacerComponent implements ClientTooltipComponent {
        private final int height;

        private TierifySpacerComponent(int height) {
            this.height = Math.max(0, height);
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getWidth(Font font) {
            return 0;
        }
    }

    @Unique
    private static final class TierifyWidthComponent implements ClientTooltipComponent {
        private final int width;

        private TierifyWidthComponent(int width) {
            this.width = Math.max(0, width);
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth(Font font) {
            return width;
        }

        @Override
        public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        }
    }

    private final class TierifyCenteredTitleComponent implements ClientTooltipComponent {
        private final ClientTooltipComponent delegate;
        private final boolean firstLine;

        private TierifyCenteredTitleComponent(ClientTooltipComponent delegate, boolean firstLine) {
            this.delegate = delegate;
            this.firstLine = firstLine;
        }

        @Override
        public int getHeight() {
            return delegate.getHeight();
        }

        @Override
        public int getWidth(Font font) {
            return delegate.getWidth(font);
        }

        @Override
        public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
            if (firstLine) {
                tierify$titleTextY = y;
            }
            int drawX = x;
            if (tierify$centerTitleIndex >= 0) {
                int textW = delegate.getWidth(font);
                drawX = tierify$tooltipX + (tierify$tooltipWidth - textW) / 2;
            }
            delegate.renderText(font, drawX, y, matrix, buffer);
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
            delegate.renderImage(font, x, y, graphics);
        }
    }

    /**
     * Ensure tooltip components list is mutable, and inject a spacer after the title if Perfect.
     *
     * This fixes the crash:
     * UnsupportedOperationException when calling List.add(...) on an immutable list.
     */
    @ModifyVariable(
            method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private List<ClientTooltipComponent> tierify$makeComponentsMutable(List<ClientTooltipComponent> components) {
        if (!ForgeTierifyConfig.tieredTooltip()) return components;
        if (components == null || components.isEmpty()) return components;

        ItemStack stack = this.tooltipStack;
        if (stack == null || stack.isEmpty()) return components;

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return components;

        boolean isPerfect = tiered.getBoolean("Perfect");

        if (TooltipOverhaulCompatForge.isLoaded()) {
            if (!isPerfect) return components;

            Font font = Minecraft.getInstance().font;
            int baseLine = (font != null) ? font.lineHeight : 9;
            int spacerHeight = (int) (baseLine * TIERIFY_LABEL_SCALE) + 4;

            List<ClientTooltipComponent> copy = new ArrayList<>(components);
            if (isPerfect) {
                int insertAt = Math.min(1, copy.size());
                copy.add(insertAt, new TierifySpacerComponent(spacerHeight));
            }
            return copy;
        }

        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);

        Font font = Minecraft.getInstance().font;
        int maxWidth = 0;
        if (font != null) {
            for (ClientTooltipComponent c : components) {
                maxWidth = Math.max(maxWidth, c.getWidth(font));
            }
        }
        int titleLineCount = computeTitleLineCount(font, components, stack);

        List<ClientTooltipComponent> copy = new ArrayList<>(components);

        // 1) Reserve Fabric-style top padding.
        int topSpacerHeight = TIERIFY_BASE_SPACER_PX;
        if (topSpacerHeight > 0) {
            copy.add(0, new TierifySpacerComponent(topSpacerHeight));
        }

        // 2) If Perfect label is going to render, reserve a full line AFTER the title.
        //    Title is at index 0 normally, or index 1 if we inserted Set Bonus spacer.
        if (isPerfect) {
            int titleIndex = (copy.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
            int insertAt = Math.min(titleIndex + Math.max(1, titleLineCount), copy.size());
            copy.add(insertAt, new TierifySpacerComponent(TIERIFY_PERFECT_SPACER_PX));
        }

        // 3) Add Fabric-style horizontal padding by widening the tooltip background.
        if (maxWidth > 0) {
            copy.add(new TierifyWidthComponent(maxWidth + 8));
        }

        // Wrap title lines to allow centering and capture title Y without a @Redirect.
        int wrapTitleIndex = (copy.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
        int wrapTitleEnd = Math.min(copy.size(), wrapTitleIndex + Math.max(1, titleLineCount));
        for (int i = wrapTitleIndex; i < wrapTitleEnd; i++) {
            ClientTooltipComponent component = copy.get(i);
            if (component instanceof TierifySpacerComponent || component instanceof TierifyWidthComponent) continue;
            copy.set(i, new TierifyCenteredTitleComponent(component, i == wrapTitleIndex));
        }

        return copy;
    }

    @Inject(
            method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V",
            at = @At("HEAD")
    )
    private void tierify$prepareTitleCentering(Font font,
                                               List<ClientTooltipComponent> components,
                                               int mouseX,
                                               int mouseY,
                                               ClientTooltipPositioner positioner,
                                               CallbackInfo ci) {
        tierify$centerTitleIndex = -1;
        tierify$titleTextY = Integer.MIN_VALUE;
        tierify$titleLineCount = 1;
        tierify$renderX = 0;
        tierify$renderY = 0;
        tierify$renderW = 0;
        tierify$renderH = 0;

        if (!ForgeTierifyConfig.tieredTooltip() || TooltipOverhaulCompatForge.isLoaded()) return;
        if (components == null || components.isEmpty()) return;

        ItemStack stack = this.tooltipStack;
        if (stack == null || stack.isEmpty()) return;

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return;

        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        boolean isPerfect = tiered.getBoolean("Perfect");
        if ((tierId == null || tierId.isEmpty()) && !isPerfect) return;

        int titleIndex = (components.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
        if (titleIndex >= components.size()) return;

        tierify$titleLineCount = computeTitleLineCount(font, components, stack);

        if (!ForgeTierifyConfig.centerName()) return;

        int w = 0;
        int h = (components.size() == 1) ? -2 : 0;
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent c = components.get(i);
            w = Math.max(w, c.getWidth(font));
            h += c.getHeight();
            if (i == 0) h += 2;
        }
        w = Math.max(w, 64);
        h = Math.max(h, 16);

        int screenW = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        Vector2ic pos = positioner.positionTooltip(screenW, screenH, mouseX, mouseY, w, h);
        tierify$tooltipX = pos.x();
        tierify$tooltipWidth = w;
        tierify$centerTitleIndex = titleIndex;
    }

    @Redirect(
            method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
            )
    )
    private Vector2ic tierify$cacheTooltipRenderPos(ClientTooltipPositioner positioner,
                                                    int screenWidth,
                                                    int screenHeight,
                                                    int mouseX,
                                                    int mouseY,
                                                    int width,
                                                    int height) {
        Vector2ic pos = positioner.positionTooltip(screenWidth, screenHeight, mouseX, mouseY, width, height);
        tierify$renderX = pos.x();
        tierify$renderY = pos.y();
        tierify$renderW = width;
        tierify$renderH = height;
        return pos;
    }

    @Inject(
            method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void tierify$afterVanillaTooltipBackground(Font font,
                                                      List<ClientTooltipComponent> components,
                                                      int mouseX,
                                                      int mouseY,
                                                      ClientTooltipPositioner positioner,
                                                      CallbackInfo ci) {
        if (!ForgeTierifyConfig.tieredTooltip() || TooltipOverhaulCompatForge.isLoaded()) return;
        if (components == null || components.isEmpty()) return;

        ItemStack stack = this.tooltipStack;
        if (stack == null || stack.isEmpty()) return;

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        boolean hasTieredTag = tiered != null;
        String tierId = null;
        boolean isPerfect = false;
        boolean isApex = false;
        String lookupKey;

        if (tiered != null) {
            tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
            isPerfect = tiered.getBoolean("Perfect");
            if ((tierId == null || tierId.isEmpty()) && !isPerfect) return;
              isApex = StarApexUtils.isApex(stack);
              int stars = StarApexUtils.getStars(stack);
              boolean mythicStars = tierId != null && tierId.startsWith("tiered:mythic") && stars > 0;
              boolean usePerfectStarBorder = isPerfect && mythicStars;
              if (usePerfectStarBorder) {
                  lookupKey = "tiered:perfect";
              } else if (isPerfect) {
                  lookupKey = "tiered:perfect";
              } else {
                  lookupKey = tierId;
              }
        } else if (stack.getItem() instanceof ReforgeAddition) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id == null) return;
            lookupKey = id.toString();
        } else {
            return;
        }

        // Use the same tooltip bounds/position that vanilla already computed.
        int x = tierify$renderX;
        int y = tierify$renderY;
        int w = tierify$renderW;
        int h = tierify$renderH;

        if (w <= 0 || h <= 0) {
            int widthFallback = 0;
            int heightFallback = (components.size() == 1) ? -2 : 0;

            for (int i = 0; i < components.size(); i++) {
                ClientTooltipComponent c = components.get(i);
                widthFallback = Math.max(widthFallback, c.getWidth(font));
                heightFallback += c.getHeight();
                if (i == 0) heightFallback += 2;
            }

            w = Math.max(widthFallback, 64);
            h = Math.max(heightFallback, 16);

            int screenW = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            Vector2ic pos = positioner.positionTooltip(screenW, screenH, mouseX, mouseY, w, h);
            x = pos.x();
            y = pos.y();
        }

        GuiGraphics gg = (GuiGraphics) (Object) this;

        // 1) Border overlay
        int tierIndex = (tierId != null && !tierId.isEmpty()) ? TierGradientAnimatorForge.getTierFromId(tierId) : 0;
        boolean usePerfectBorder = isPerfect;
        gg.pose().pushPose();
        gg.pose().translate(0.0F, 0.0F, 400.0F);
        try {
            TierifyTooltipBorderRendererForge.render(
                    gg,
                    x, y,
                    w, h,
                    lookupKey,
                    tierIndex,
                    usePerfectBorder
            );
            if (isApex) {
                float progress = (float) ((Util.getMillis() % APEX_SWEEP_PERIOD_MS) / APEX_SWEEP_PERIOD_MS);
                int sweepCenter = isPerfect ? APEX_PERFECT_SWEEP_COLOR_RGB : APEX_SWEEP_COLOR_RGB;
                int sweepEdge = isPerfect ? APEX_PERFECT_SWEEP_EDGE_RGB : APEX_SWEEP_EDGE_RGB;
                TierifyTooltipBorderRendererForge.renderPerimeterSweep(
                        gg,
                        x, y,
                        w, h,
                        sweepCenter,
                        sweepEdge,
                        APEX_SWEEP_ALPHA_MAX,
                        APEX_SWEEP_THICKNESS,
                        progress,
                        APEX_SWEEP_LEN_FRAC
                );
                TierifyTooltipBorderRendererForge.Template template =
                        TierifyTooltipBorderRendererForge.findTemplate(lookupKey, usePerfectBorder);
                TierifyTooltipBorderRendererForge.renderPieces(gg, x, y, w, h, template);
            }
        } finally {
            gg.pose().popPose();
        }

        if (hasTieredTag && tierId != null && !tierId.isEmpty()) {
              int stars = StarApexUtils.getStars(stack);
              boolean mythicStars = tierId != null && tierId.startsWith("tiered:mythic");
              if (!mythicStars) {
                  stars = 0;
              }
              ResourceLocation starIcon = isPerfect ? PERFECT_STAR_ICON : STAR_ICON;
              if (StarApexUtils.isApex(stack)) {
                  renderApexCrownPlate(gg, font, x, y, w, components, starIcon, isPerfect);
              } else if (stars > 0) {
                  renderStarsRibbon(gg, x, y, w, components, stars, starIcon, isPerfect);
              }
            // 2) Overlay labels (scaled/centered).
            renderSetBonusCrest(gg, font, x, y, w, components, stack, tierId);
            renderPerfectLabel(gg, font, x, y, w, components, stack, tierId);
        }
    }


    @Unique
    private static float textTopYForIndex(int tooltipTopY, List<ClientTooltipComponent> components, int index) {
        if (components == null || components.isEmpty()) return tooltipTopY + 4.0f;

        int clamped = Math.max(0, Math.min(index, components.size()));
        float y = tooltipTopY;
        for (int i = 0; i < clamped; i++) {
            y += components.get(i).getHeight();
            if (i == 0) y += 2.0f;
        }
        return y;
    }


    private static void renderSetBonusLabel(GuiGraphics gg, Font font, int x, int y, int w, List<ClientTooltipComponent> components, ItemStack stack, String tierId, int titleTextY) {
        Component label = buildSetBonusLabel(stack, tierId);
        if (label == null) return;

        float scale = TIERIFY_LABEL_SCALE;
        int textW = font.width(label);
        float scaledW = textW * scale;

        float xPos = x + (w - scaledW) / 2.0f;

        float lineH = font.lineHeight;
        float scaledH = lineH * scale;

        // Center within the actual top padding band up to the title line (Fabric parity, GUI-scale safe).
        int titleIndex = (components.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
        float titleTop = (titleTextY >= y) ? titleTextY : textTopYForIndex(y, components, titleIndex);
        int topPadding = Math.max(4, Math.round(titleTop - y));
        float gapTop = y;
        float gapBottom = y + topPadding;
        float yPos = gapTop + ((gapBottom - gapTop) - scaledH) / 2.0f;
        float yOffset = (lineH - scaledH) / 2.0f;
        yPos += yOffset;
        yPos += SET_BONUS_LABEL_NUDGE_Y;

        gg.pose().pushPose();
        gg.pose().translate(xPos, yPos, 450.0f);
        gg.pose().scale(scale, scale, 1.0f);
        gg.drawString(font, label, 0, 0, 0xFFFFFF, false);
        gg.pose().popPose();
    }

    private static void renderPerfectLabel(GuiGraphics gg, Font font, int x, int y, int w, List<ClientTooltipComponent> components, ItemStack stack, String tierId) {
        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return;
        if (!tiered.getBoolean("Perfect")) return;

        int titleIndex = (components.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
        int titleLineCount = computeTitleLineCount(font, components, stack);
        int perfectSpacerIndex = titleIndex + Math.max(1, titleLineCount);

        // Render centered within the spacer line injected after the title.
        float scale = TIERIFY_LABEL_SCALE;

        MutableComponent perfect = PerfectLabelAnimatorForge.animatedLabel(Util.getMillis());

        int textW = font.width(perfect);
        float scaledW = textW * scale;

        float xPos = x + (w - scaledW) / 2.0f;

        float lineH = font.lineHeight;
        float scaledH = lineH * scale;

        int spacerH = (perfectSpacerIndex >= 0 && perfectSpacerIndex < components.size() && components.get(perfectSpacerIndex) instanceof TierifySpacerComponent s)
                ? s.getHeight()
                : font.lineHeight;

        float lineTopY = textTopYForIndex(y, components, perfectSpacerIndex);
        float yPos = lineTopY + (spacerH - scaledH) / 2.0f - 1.0f;

        gg.pose().pushPose();
        gg.pose().translate(xPos, yPos, 450.0f);
        gg.pose().scale(scale, scale, 1.0f);
        gg.drawString(font, perfect, 0, 0, 0xFFFFFF, false);
        gg.pose().popPose();
    }

    private static void renderSetBonusCrest(GuiGraphics gg,
                                            Font font,
                                            int x,
                                            int y,
                                            int w,
                                            List<ClientTooltipComponent> components,
                                            ItemStack stack,
                                            String tierId) {
        if (!shouldRenderSetBonusCrest(stack, tierId)) return;
        if (components == null || components.isEmpty()) return;

        int headerIndex = findArmorHeaderIndex(font, components);
        if (headerIndex < 0) return;

        Component header = getTooltipText(components.get(headerIndex));
        if (header == null) return;

        int textWidth = font.width(header);
        int iconSize = SET_BONUS_CREST_SIZE;

        int iconX = x + textWidth + SET_BONUS_CREST_GAP - 2;
        int maxX = x + w - iconSize - 2;
        if (iconX > maxX) iconX = Math.max(x + 2, maxX);

        int lineY = Math.round(textTopYForIndex(y, components, headerIndex));
        int iconY = lineY + (font.lineHeight - iconSize) / 2 - 2;

        boolean active = isSetBonusActive(stack, tierId);
        drawSetBonusCrest(gg, iconX, iconY, iconSize, active);
    }

    private void renderStarsRibbon(GuiGraphics gg,
                                   int x,
                                   int y,
                                   int w,
                                   List<ClientTooltipComponent> components,
                                   int stars,
                                   ResourceLocation starIcon,
                                   boolean isPerfect) {
        if (stars <= 0 || components == null || components.isEmpty()) return;

        float starScale = STAR_BORDER_SCALE;
        int starRenderW = Math.round(STAR_TEX_W * starScale);
        int starRenderH = Math.round(STAR_TEX_H * starScale);
        int starGroupW = (stars * starRenderW) + ((stars - 1) * STAR_GAP_PX);

        float centerX = x + (w / 2.0f);
        int titleIndex = (components.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
        float titleTop = (tierify$titleTextY >= y)
                ? tierify$titleTextY
                : textTopYForIndex(y, components, titleIndex);
        float starCenterY = titleTop - 2.0f - (starRenderH / 2.0f);
        renderStarBand(gg, centerX, starCenterY, stars, starRenderW, starRenderH, starGroupW, starScale, starIcon, isPerfect);
    }

    private void renderApexCrownPlate(GuiGraphics gg,
                                      Font font,
                                      int x,
                                      int y,
                                      int w,
                                      List<ClientTooltipComponent> components,
                                      ResourceLocation starIcon,
                                      boolean isPerfect) {
        if (components == null || components.isEmpty()) return;

        float starScale = STAR_BORDER_SCALE;
        int starRenderW = Math.round(STAR_TEX_W * starScale);
        int starRenderH = Math.round(STAR_TEX_H * starScale);
        int starGroupW = (APEX_STAR_COUNT * starRenderW) + ((APEX_STAR_COUNT - 1) * STAR_GAP_PX);

        float crestBaseScale = (starRenderH + APEX_CREST_EXTRA_PX) / (float) APEX_CREST_TEX_H;
        crestBaseScale *= (APEX_CREST_SCALE * APEX_CREST_APEX_SCALE);
        int crestWBase = Math.round(APEX_CREST_TEX_W * crestBaseScale);
        int crestHBase = Math.round(APEX_CREST_TEX_H * crestBaseScale);
        float crestScale = crestBaseScale * (1.0f + 0.02f * (float) Math.sin(Util.getMillis() / 700.0));

        float centerX = x + (w / 2.0f);
        int titleIndex = (components.get(0) instanceof TierifySpacerComponent) ? 1 : 0;
        float titleTop = (tierify$titleTextY >= y)
                ? tierify$titleTextY
                : textTopYForIndex(y, components, titleIndex);
        float bandCenterY = titleTop - 2.0f - (starRenderH / 2.0f);
        renderApexCrownBand(gg, centerX, bandCenterY, starScale, starRenderW, starRenderH, starGroupW,
                crestScale, crestWBase, crestHBase, baseZ(), starIcon, isPerfect);
    }

    @Unique
    private static float baseZ() {
        return 450.0f;
    }

    @Unique
    private void renderStarBand(GuiGraphics gg,
                                float centerX,
                                float borderCenterY,
                                int stars,
                                int starRenderW,
                                int starRenderH,
                                int starGroupW,
                                float starScale,
                                ResourceLocation starIcon,
                                boolean isPerfect) {
        int starsX = Math.round(centerX - (starGroupW / 2.0f));
        int starY = Math.round(borderCenterY - (starRenderH / 2.0f));
        gg.pose().pushPose();
        gg.pose().translate(0.0f, 0.0f, baseZ());
        long now = Util.getMillis();
        int totalStars = Math.max(1, stars);
        float shimmerPos = computeApexShimmerPos(now, totalStars);
        for (int i = 0; i < stars; i++) {
            int starX = starsX + (i * (starRenderW + STAR_GAP_PX));
            float shimmerScale = applyApexStarShimmer(i, shimmerPos, totalStars, isPerfect);
            gg.pose().pushPose();
            gg.pose().translate(starX + (starRenderW / 2.0f), starY + (starRenderH / 2.0f), 0.0f);
            gg.pose().scale(starScale * shimmerScale, starScale * shimmerScale, 1.0f);
            gg.pose().translate(-STAR_TEX_W / 2.0f, -STAR_TEX_H / 2.0f, 0.0f);
            gg.blit(starIcon, 0, 0, 0, 0, STAR_TEX_W, STAR_TEX_H, STAR_TEX_W, STAR_TEX_H);
            gg.pose().popPose();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        gg.pose().popPose();
    }

    @Unique
    private void renderApexCrownBand(GuiGraphics gg,
                                     float centerX,
                                     float borderCenterY,
                                     float starScale,
                                     int starRenderW,
                                     int starRenderH,
                                     int starGroupW,
                                     float crestScale,
                                     int crestWBase,
                                     int crestHBase,
                                     float baseZ,
                                     ResourceLocation starIcon,
                                     boolean isPerfect) {
        int bandH = Math.max(starRenderH, crestHBase);
        int bandY = Math.round(borderCenterY - (bandH / 2.0f));

        float crestHalfW = crestWBase / 2.0f;
        int crestX = Math.round(centerX - crestHalfW);
        int crestY = Math.round(borderCenterY - (crestHBase / 2.0f));
        float crestCenterX = crestX + (crestWBase / 2.0f);
        float crestCenterY = crestY + (crestHBase / 2.0f);
        gg.pose().pushPose();
        gg.pose().translate(0.0f, 0.0f, baseZ);
        gg.pose().translate(crestCenterX, crestCenterY, 0.0f);
        gg.pose().scale(crestScale, crestScale, 1.0f);
        gg.pose().translate(-APEX_CREST_TEX_W / 2.0f, -APEX_CREST_TEX_H / 2.0f, 0.0f);
        gg.blit(APEX_CREST, 0, 0, 0, 0, APEX_CREST_TEX_W, APEX_CREST_TEX_H, APEX_CREST_TEX_W, APEX_CREST_TEX_H);
        gg.pose().popPose();

        int leftStartX = Math.round(centerX - crestHalfW - APEX_CREST_GAP_PX - starGroupW);
        int rightStartX = Math.round(centerX + crestHalfW + APEX_CREST_GAP_PX);
        int starY = Math.round(borderCenterY - (starRenderH / 2.0f));
        gg.pose().pushPose();
        gg.pose().translate(0.0f, 0.0f, baseZ);
        long now = Util.getMillis();
        int totalStars = APEX_STAR_COUNT * 2;
        float shimmerPos = computeApexShimmerPos(now, totalStars);
        for (int i = 0; i < APEX_STAR_COUNT; i++) {
            int starX = leftStartX + (i * (starRenderW + STAR_GAP_PX));
            float shimmerScale = applyApexStarShimmer(i, shimmerPos, totalStars, isPerfect);
            gg.pose().pushPose();
            gg.pose().translate(starX + (starRenderW / 2.0f), starY + (starRenderH / 2.0f), 0.0f);
            gg.pose().scale(starScale * shimmerScale, starScale * shimmerScale, 1.0f);
            gg.pose().translate(-STAR_TEX_W / 2.0f, -STAR_TEX_H / 2.0f, 0.0f);
            gg.blit(starIcon, 0, 0, 0, 0, STAR_TEX_W, STAR_TEX_H, STAR_TEX_W, STAR_TEX_H);
            gg.pose().popPose();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        for (int i = 0; i < APEX_STAR_COUNT; i++) {
            int starX = rightStartX + (i * (starRenderW + STAR_GAP_PX));
            int index = i + APEX_STAR_COUNT;
            float shimmerScale = applyApexStarShimmer(index, shimmerPos, totalStars, isPerfect);
            gg.pose().pushPose();
            gg.pose().translate(starX + (starRenderW / 2.0f), starY + (starRenderH / 2.0f), 0.0f);
            gg.pose().scale(starScale * shimmerScale, starScale * shimmerScale, 1.0f);
            gg.pose().translate(-STAR_TEX_W / 2.0f, -STAR_TEX_H / 2.0f, 0.0f);
            gg.blit(starIcon, 0, 0, 0, 0, STAR_TEX_W, STAR_TEX_H, STAR_TEX_W, STAR_TEX_H);
            gg.pose().popPose();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        gg.pose().popPose();

        // embers removed
    }

    @Unique
    private static int computeTooltipHeight(List<ClientTooltipComponent> components) {
        if (components == null || components.isEmpty()) return 16;
        int height = (components.size() == 1) ? -2 : 0;
        for (int i = 0; i < components.size(); i++) {
            height += components.get(i).getHeight();
            if (i == 0) height += 2;
        }
        return Math.max(16, height);
    }

    @Unique
    private static float applyApexStarShimmer(int index, float shimmerPos, int totalStars, boolean isPerfect) {
        float distance = Math.abs(index - shimmerPos);
        distance = Math.min(distance, totalStars - distance);
        float weight = 1.0f - Math.min(1.0f, distance / APEX_SHIMMER_WIDTH);
        float alpha = lerp(APEX_SHIMMER_ALPHA_BASE, APEX_SHIMMER_ALPHA_PEAK, weight);
        float baseR = isPerfect ? APEX_SHIMMER_COOL_R : APEX_SHIMMER_WARM_R;
        float baseG = isPerfect ? APEX_SHIMMER_COOL_G : APEX_SHIMMER_WARM_G;
        float baseB = isPerfect ? APEX_SHIMMER_COOL_B : APEX_SHIMMER_WARM_B;
        float r = Math.min(1.0f, baseR * (0.9f + 0.1f * weight));
        float g = Math.min(1.0f, baseG * (0.9f + 0.1f * weight));
        float b = Math.min(1.0f, baseB * (0.9f + 0.1f * weight));
        RenderSystem.setShaderColor(r, g, b, alpha);
        return lerp(APEX_SHIMMER_SCALE_MIN, APEX_SHIMMER_SCALE_MAX, weight);
    }

    private static float computeApexShimmerPos(long now, int totalStars) {
        float speedScale = switch (Math.max(1, totalStars)) {
            case 1 -> 3.0f;
            case 2 -> 2.0f;
            case 3 -> 1.5f;
            default -> 1.0f;
        };
        float period = Math.max(1.0f, APEX_SHIMMER_STEP_MS * totalStars * speedScale);
        float t = (now % (long) period) / period;
        return t * totalStars;
    }

    @Unique
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    // embers removed

    private static boolean shouldRenderSetBonusCrest(ItemStack stack, String tierId) {
        if (tierId == null || tierId.isEmpty()) return false;
        if (!(stack.getItem() instanceof ArmorItem armor)) return false;
        if (!ForgeTierifyConfig.enableArmorSetBonuses()) return false;

        Player player = Minecraft.getInstance().player;
        if (player == null) return false;

        EquipmentSlot slot = armor.getEquipmentSlot();
        return player.getItemBySlot(slot) == stack;
    }

    private static boolean isSetBonusActive(ItemStack stack, String tierId) {
        if (!(stack.getItem() instanceof ArmorItem)) return false;
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return hasPerfectSetBonus(player, tierId) || hasSetBonus(player, tierId);
    }

    private static int findArmorHeaderIndex(Font font, List<ClientTooltipComponent> components) {
        String[] headers = armorHeaderStrings();
        for (int i = 0; i < components.size(); i++) {
            Component text = getTooltipText(components.get(i));
            if (text == null) continue;
            String line = text.getString();
            for (String header : headers) {
                if (line.equals(header)) return i;
            }
        }
        return -1;
    }

    private static String[] armorHeaderStrings() {
        return new String[]{
                Component.translatable("item.modifiers.feet").getString(),
                Component.translatable("item.modifiers.legs").getString(),
                Component.translatable("item.modifiers.chest").getString(),
                Component.translatable("item.modifiers.head").getString()
        };
    }

    private static Component getTooltipText(ClientTooltipComponent component) {
        if (component == null) return null;
        Class<?> type = component.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (!Component.class.isAssignableFrom(field.getType())) continue;
            try {
                field.setAccessible(true);
                Object value = field.get(component);
                if (value instanceof Component comp) {
                    return comp;
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return null;
    }

    @Unique
    private static void renderApexNameGlow(Font font,
                                           ItemStack stack,
                                           int x,
                                           int y,
                                           Matrix4f matrix,
                                           MultiBufferSource.BufferSource buffer) {
        if (stack == null || stack.isEmpty()) return;
        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return;
        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if (tierId == null || tierId.isEmpty()) return;

        Component prefix = Component.translatable(tierId + ".label")
                .withStyle(net.minecraft.ChatFormatting.BOLD)
                .append(" ");
        int prefixX = x;

        long now = Util.getMillis();
        float pulse = (float) ((Math.sin(now / APEX_GLOW_PULSE_MS) + 1.0) * 0.5);
        int alpha = Math.round(APEX_GLOW_ALPHA_MIN + (APEX_GLOW_ALPHA_MAX - APEX_GLOW_ALPHA_MIN) * pulse);
        int color = (alpha << 24) | APEX_GLOW_COLOR_RGB;

        font.drawInBatch(prefix, prefixX - 1, y, color, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        font.drawInBatch(prefix, prefixX + 1, y, color, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);

        int overlayAlpha = Math.round(60 + (160 * pulse));
        int overlayColor = (overlayAlpha << 24) | APEX_GLOW_COLOR_RGB;
        font.drawInBatch(prefix, prefixX, y, overlayColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
    }

    private static void drawSetBonusCrest(GuiGraphics gg, int x, int y, int size, boolean active) {
        int renderSize = Math.max(1, Math.round(size * SET_BONUS_CREST_SCALE));
        int offset = (size - renderSize) / 2;
        int drawX = x + offset;
        int drawY = y + offset;

        gg.pose().pushPose();
        gg.pose().translate(drawX + (renderSize / 2.0f), drawY + (renderSize / 2.0f), 0.0f);
        gg.pose().scale(SET_BONUS_CREST_SCALE, SET_BONUS_CREST_SCALE, 1.0f);
        gg.pose().translate(-(SET_BONUS_CREST_TEX_SIZE / 2.0f), -(SET_BONUS_CREST_TEX_SIZE / 2.0f), 0.0f);
        if (active) {
            gg.blit(SET_BONUS_CREST_ACTIVE, 0, 0, 0, 0, SET_BONUS_CREST_TEX_SIZE, SET_BONUS_CREST_TEX_SIZE, SET_BONUS_CREST_TEX_SIZE, SET_BONUS_CREST_TEX_SIZE);
        } else {
            gg.fill(0, 0, SET_BONUS_CREST_TEX_SIZE, SET_BONUS_CREST_TEX_SIZE, 0xFF4A4A4A);
            gg.fill(1, 1, SET_BONUS_CREST_TEX_SIZE - 1, SET_BONUS_CREST_TEX_SIZE - 1, 0xFF8A8A8A);
            gg.fill(1, 1, SET_BONUS_CREST_TEX_SIZE - 1, 2, 0xFFB0B0B0);
            gg.fill(1, 1, 2, SET_BONUS_CREST_TEX_SIZE - 1, 0xFFB0B0B0);
        }
        gg.pose().popPose();
    }

    @Nullable
    private static Component buildSetBonusLabel(ItemStack stack, String tierId) {
        if (tierId == null || tierId.isEmpty()) return null;
        if (!(stack.getItem() instanceof ArmorItem armor)) return null;
        if (!ForgeTierifyConfig.enableArmorSetBonuses()) return null;

        Player player = Minecraft.getInstance().player;
        if (player == null) return null;

        // Only show if the hovered stack IS the equipped stack instance.
        EquipmentSlot slot = armor.getEquipmentSlot();
        if (player.getItemBySlot(slot) != stack) return null;

        if (hasPerfectSetBonus(player, tierId)) {
            int pct = Math.round(ForgeTierifyConfig.armorSetPerfectBonusPercent() * 100.0f);
            return Component.literal("Perfect Set Bonus (+" + pct + "%)")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        }

        if (hasSetBonus(player, tierId)) {
            int pct = Math.round(ForgeTierifyConfig.armorSetBonusMultiplier() * 100.0f);
            return Component.literal("Set Bonus (+" + pct + "%)")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        }

        return null;
    }

    private static boolean hasSetBonus(Player player, String tierId) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack armor = player.getItemBySlot(slot);
            if (armor.isEmpty()) return false;

            CompoundTag tiered = armor.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
            if (tiered == null) return false;

            String id = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
            if (!tierId.equals(id)) return false;
        }
        return true;
    }

    private static boolean hasPerfectSetBonus(Player player, String tierId) {
        if (!hasSetBonus(player, tierId)) return false;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack armor = player.getItemBySlot(slot);
            if (armor.isEmpty()) return false;

            CompoundTag tiered = armor.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
            if (tiered == null) return false;

            if (!tiered.getBoolean("Perfect")) return false;
        }

        return true;
    }

    @Unique
    private static int computeTitleLineCount(Font font, List<ClientTooltipComponent> components, ItemStack stack) {
        if (font == null || components == null || components.isEmpty()) return 1;
        if (stack == null || stack.isEmpty()) return 1;

        Component title = stack.getHoverName();
        if (title == null) return 1;

        int maxTextWidth = 0;
        for (ClientTooltipComponent c : components) {
            if (c instanceof TierifySpacerComponent || c instanceof TierifyWidthComponent) continue;
            maxTextWidth = Math.max(maxTextWidth, c.getWidth(font));
        }
        if (maxTextWidth <= 0) {
            maxTextWidth = Math.max(1, font.width(title));
        }

        return Math.max(1, font.split(title, maxTextWidth).size());
    }
}
