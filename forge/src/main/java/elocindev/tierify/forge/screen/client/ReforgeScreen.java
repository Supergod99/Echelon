package elocindev.tierify.forge.screen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import elocindev.tierify.TierifyCommon;
import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.ForgeTieredAttributeSubscriber;
import elocindev.tierify.forge.client.ApexEffectGradientAnimatorForge;
import elocindev.tierify.forge.client.TierGradientAnimatorForge;
import elocindev.tierify.forge.client.TierifyTooltipBorderRendererForge;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.network.ForgeNetwork;
import elocindev.tierify.forge.network.c2s.TryReforgeC2S;
import elocindev.tierify.forge.reforge.ForgeReforgeData;
import elocindev.tierify.forge.screen.ReforgeMenu;
import elocindev.tierify.util.StarApexUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ReforgeScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<ReforgeMenu> {

    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/reforging_screen.png");
    private static final int PREVIEW_PAD_X = 8;
    private static final int PREVIEW_PAD_Y = 6;
    private static final int PREVIEW_TITLE_GAP = 4;
    private static final int PREVIEW_STAR_BAND_PX = 12;
    private static final int PREVIEW_STAR_NUDGE_Y = 6;
    private static final float PREVIEW_Z = 450.0f;
    private static final float STAR_ICON_SCALE = 0.5f;
    private static final float STAR_BORDER_SCALE = STAR_ICON_SCALE;
    private static final int STAR_GAP_PX = 2;
    private static final int STAR_TEX_W = 17;
    private static final int STAR_TEX_H = 16;
    private static final int APEX_STAR_COUNT = 3;
    private static final int APEX_CREST_EXTRA_PX = 2;
    private static final int APEX_CREST_GAP_PX = 4;
    private static final float APEX_CREST_SCALE = 1.20f;
    private static final float APEX_CREST_APEX_SCALE = 0.95f;
    private static final long APEX_SHIMMER_STEP_MS = 250L;
    private static final float APEX_SHIMMER_SCALE_MIN = 0.94f;
    private static final float APEX_SHIMMER_SCALE_MAX = 1.14f;
    private static final float APEX_SHIMMER_ALPHA_BASE = 0.75f;
    private static final float APEX_SHIMMER_ALPHA_PEAK = 1.0f;
    private static final float APEX_SHIMMER_WIDTH = 1.2f;
    private static final float APEX_SHIMMER_WARM_R = 1.0f;
    private static final float APEX_SHIMMER_WARM_G = 0.95f;
    private static final float APEX_SHIMMER_WARM_B = 0.85f;
    private static final float APEX_SHIMMER_COOL_R = 0.95f;
    private static final float APEX_SHIMMER_COOL_G = 1.0f;
    private static final float APEX_SHIMMER_COOL_B = 1.0f;
    private static final ResourceLocation STAR_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/star.png");
    private static final ResourceLocation PERFECT_STAR_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/perfect_star.png");
    private static final ResourceLocation APEX_CREST =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/item/apex_crux.png");
    private static final int APEX_CREST_TEX_W = 36;
    private static final int APEX_CREST_TEX_H = 36;

    private static final TagKey<Item> TAG_REFORGE_BASE_ITEM = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "reforge_base_item")
    );

    private ReforgeButton reforgeButton;
    private ItemStack lastTarget = ItemStack.EMPTY;
    private List<Item> baseItems = Collections.emptyList();

    public ReforgeScreen(ReforgeMenu menu, Inventory inv, Component title) {
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

        // Fabric: (i + 79, j + 56), 18x18 sprite-button :contentReference[oaicite:3]{index=3}
        this.reforgeButton = new ReforgeButton(left + 79, top + 56);
        this.addRenderableWidget(this.reforgeButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (reforgeButton != null) {
            reforgeButton.setDisabled(!menu.isReforgeReady());
        }
    }


    @Override
    protected void renderBg(GuiGraphics gg, float partial, int mouseX, int mouseY) {
        gg.blit(TEX, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partial) {
        renderBackground(gg);
        super.render(gg, mouseX, mouseY, partial);
        renderReforgePreview(gg);
        renderTooltip(gg, mouseX, mouseY);

        if (isPointWithinBounds(79, 56, 18, 18, mouseX, mouseY)) {
            ItemStack target = menu.getSlot(1).getItem();
            if (target.isEmpty()) {
                baseItems = Collections.emptyList();
                lastTarget = target;
            } else if (target != lastTarget) {
                lastTarget = target;
                baseItems = new ArrayList<>();

                Set<Item> items = ForgeReforgeData.getBaseItemsForTarget(target.getItem());
                if (!items.isEmpty()) {
                    baseItems.addAll(items);
                } else if (target.getItem() instanceof TieredItem tool) {
                    var ing = tool.getTier().getRepairIngredient();
                    ItemStack[] matches = (ing == null) ? new ItemStack[0] : ing.getItems();
                    if (matches.length > 0) {
                        for (ItemStack stack : matches) {
                            baseItems.add(stack.getItem());
                        }
                    } else {
                        BuiltInRegistries.ITEM.getTag(TAG_REFORGE_BASE_ITEM)
                                .ifPresent(tag -> tag.forEach(holder -> baseItems.add(holder.value())));
                    }
                } else if (target.getItem() instanceof ArmorItem armor) {
                    var ing = armor.getMaterial().getRepairIngredient();
                    ItemStack[] matches = (ing == null) ? new ItemStack[0] : ing.getItems();
                    if (matches.length > 0) {
                        for (ItemStack stack : matches) {
                            baseItems.add(stack.getItem());
                        }
                    } else {
                        BuiltInRegistries.ITEM.getTag(TAG_REFORGE_BASE_ITEM)
                                .ifPresent(tag -> tag.forEach(holder -> baseItems.add(holder.value())));
                    }
                } else {
                    BuiltInRegistries.ITEM.getTag(TAG_REFORGE_BASE_ITEM)
                            .ifPresent(tag -> tag.forEach(holder -> baseItems.add(holder.value())));
                }
            }

            List<Component> tooltip = new ArrayList<>();
            if (!baseItems.isEmpty()) {
                ItemStack ingredient = menu.getSlot(0).getItem();
                if (ingredient.isEmpty() || !baseItems.contains(ingredient.getItem())) {
                    tooltip.add(Component.translatable("screen.tiered.reforge_ingredient"));
                    for (Item item : baseItems) {
                        tooltip.add(new ItemStack(item).getHoverName());
                    }
                }
            }

            if (!ForgeTierifyConfig.allowReforgingDamaged()
                    && target.isDamageableItem()
                    && target.isDamaged()) {
                tooltip.add(Component.translatable("screen.tiered.reforge_damaged"));
            }

            if (!tooltip.isEmpty()) {
                List<FormattedCharSequence> lines = tooltip.stream()
                        .flatMap(c -> this.font.split(c, 200).stream())
                        .toList();
                gg.renderTooltip(this.font, lines, mouseX, mouseY);
            }
        }
    }

    /**
     * Forge port of Fabric's inner ReforgeButton:
     * - 18x18
     * - uses reforging_screen.png right-strip sprites:
     *   u = 176 + (hover ? 18 : 0) + (disabled ? 36 : 0) :contentReference[oaicite:5]{index=5}
     */
    private final class ReforgeButton extends AbstractWidget {
        private boolean disabled = true;

        private ReforgeButton(int x, int y) {
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

            // Texture is 256x256 in your asset; specify full size to avoid any blit overload mismatch.
            gg.blit(TEX, getX(), getY(), u, 0, this.width, this.height, 256, 256);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (!disabled && menu.isReforgeReady()) {
                ForgeNetwork.CHANNEL.sendToServer(new TryReforgeC2S());
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {
            // Optional: keep empty (Fabric uses ScreenTexts.EMPTY as label)
        }
    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, int mouseX, int mouseY) {
        int relX = mouseX - this.leftPos;
        int relY = mouseY - this.topPos;
        return relX >= x && relX < x + width && relY >= y && relY < y + height;
    }

    private void renderReforgePreview(GuiGraphics gg) {
        ItemStack target = menu.getSlot(1).getItem();
        if (target.isEmpty()) return;

        CompoundTag tiered = target.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return;

        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if (tierId == null || tierId.isEmpty()) return;

        ResourceLocation tierRl = ResourceLocation.tryParse(tierId);
        if (tierRl == null) return;

        boolean isApex = StarApexUtils.isApex(target);
        boolean isPerfect = tiered.getBoolean("Perfect");
        int stars = StarApexUtils.getStars(target);
        boolean mythicStars = tierId.startsWith("tiered:mythic");
        if (!mythicStars) {
            stars = 0;
        }
        int tierIndex = TierGradientAnimatorForge.getTierFromId(tierId);

        List<Component> lines = buildPreviewLines(target, tierId, isApex, tierIndex);

        if (lines.isEmpty()) return;

        int maxTextWidth = 0;
        for (Component line : lines) {
            maxTextWidth = Math.max(maxTextWidth, this.font.width(line));
        }
        int extraTop = (isApex || stars > 0) ? PREVIEW_STAR_BAND_PX : 0;
        int width = Math.max(64, maxTextWidth + (PREVIEW_PAD_X * 2));
        int height = computeTooltipHeight(lines.size()) + (PREVIEW_PAD_Y * 2) + extraTop;
        Rect2i previewRect = computePreviewRect(width, height, extraTop);
        int x = previewRect.getX();
        int y = previewRect.getY();

        TierifyTooltipBorderRendererForge.render(gg, x, y, width, height, tierId, tierIndex, isPerfect);

        int innerWidth = width - (PREVIEW_PAD_X * 2);
        int lineY = y + PREVIEW_PAD_Y + extraTop - (extraTop > 0 ? PREVIEW_STAR_NUDGE_Y : 0);
        ResourceLocation starIcon = isPerfect ? PERFECT_STAR_ICON : STAR_ICON;
        if (isApex) {
            renderApexCrownPlatePreview(gg, x, width, lineY, starIcon, isPerfect);
        } else if (stars > 0) {
            renderStarsRibbonPreview(gg, x, width, lineY, stars, starIcon, isPerfect);
        }
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            int lineW = this.font.width(line);
            int drawX = x + PREVIEW_PAD_X + (innerWidth - lineW) / 2;
            gg.drawString(this.font, line, drawX, lineY, 0xFFFFFF, false);
            lineY += this.font.lineHeight;
            if (i == 0) lineY += PREVIEW_TITLE_GAP;
        }
    }

    private List<Component> buildPreviewLines(ItemStack target, String tierId, boolean isApex, int tierIndex) {
        List<Component> lines = new ArrayList<>();
        Component label = Component.translatable(tierId + ".label");
        MutableComponent title = isApex
                ? ApexEffectGradientAnimatorForge.animateWithFont(label, ApexEffectGradientAnimatorForge.FONT_PREFIX)
                : TierGradientAnimatorForge.animate(label, tierIndex);
        lines.add(title);

        List<Component> attrs = ForgeTieredAttributeSubscriber.buildReforgePreviewAttributes(target);
        if (isApex) {
            for (Component line : attrs) {
                String plain = line.getString();
                String trimmed = plain.trim();
                if (trimmed.startsWith("-")) {
                    lines.add(line);
                } else {
                    MutableComponent grad = TierGradientAnimatorForge.animate(Component.literal(plain), 4);
                    lines.add(stripBoldRecursive(grad));
                }
            }
        } else {
            lines.addAll(attrs);
        }
        return lines;
    }

    private Rect2i computePreviewRect(int width, int height, int extraTop) {
        int x = this.leftPos - width - 8;
        int y = this.topPos + 10;
        int maxX = this.width - width - 6;
        int maxY = this.height - height - 6;
        if (x < 2) x = 2;
        if (x > maxX) x = Math.max(2, maxX);
        if (y > maxY) y = Math.max(2, maxY);
        if (y < 2) y = 2;
        return new Rect2i(x, y, width, height);
    }

    private int computeTooltipHeight(int lineCount) {
        if (lineCount <= 0) return 16;
        int height = (lineCount == 1) ? -2 : 0;
        for (int i = 0; i < lineCount; i++) {
            height += this.font.lineHeight;
            if (i == 0) height += 2;
        }
        return Math.max(16, height);
    }

    private static MutableComponent stripBoldRecursive(MutableComponent root) {
        root.setStyle(root.getStyle().withBold(false));
        for (Component sib : root.getSiblings()) {
            if (sib instanceof MutableComponent mc) {
                stripBoldRecursive(mc);
            }
        }
        return root;
    }

    private static void renderStarsRibbonPreview(GuiGraphics gg,
                                                 int x,
                                                 int width,
                                                 int titleLineY,
                                                 int stars,
                                                 ResourceLocation starIcon,
                                                 boolean isPerfect) {
        if (stars <= 0) return;
        float starScale = STAR_BORDER_SCALE;
        int starRenderW = Math.round(STAR_TEX_W * starScale);
        int starRenderH = Math.round(STAR_TEX_H * starScale);
        int starGroupW = (stars * starRenderW) + ((stars - 1) * STAR_GAP_PX);
        float centerX = x + (width / 2.0f);
        float starCenterY = titleLineY - 2.0f - (starRenderH / 2.0f);
        renderStarBandPreview(gg, centerX, starCenterY, stars, starRenderW, starRenderH, starGroupW, starScale, starIcon, isPerfect);
    }

    private static void renderApexCrownPlatePreview(GuiGraphics gg,
                                                    int x,
                                                    int width,
                                                    int titleLineY,
                                                    ResourceLocation starIcon,
                                                    boolean isPerfect) {
        float starScale = STAR_BORDER_SCALE;
        int starRenderW = Math.round(STAR_TEX_W * starScale);
        int starRenderH = Math.round(STAR_TEX_H * starScale);
        int starGroupW = (APEX_STAR_COUNT * starRenderW) + ((APEX_STAR_COUNT - 1) * STAR_GAP_PX);

        float crestBaseScale = (starRenderH + APEX_CREST_EXTRA_PX) / (float) APEX_CREST_TEX_H;
        crestBaseScale *= (APEX_CREST_SCALE * APEX_CREST_APEX_SCALE);
        int crestWBase = Math.round(APEX_CREST_TEX_W * crestBaseScale);
        int crestHBase = Math.round(APEX_CREST_TEX_H * crestBaseScale);
        float crestScale = crestBaseScale * (1.0f + 0.02f * (float) Math.sin(net.minecraft.Util.getMillis() / 700.0));

        float centerX = x + (width / 2.0f);
        float bandCenterY = titleLineY - 2.0f - (starRenderH / 2.0f);
        renderApexCrownBandPreview(gg, centerX, bandCenterY, starScale, starRenderW, starRenderH, starGroupW,
                crestScale, crestWBase, crestHBase, starIcon, isPerfect);
    }

    private static void renderStarBandPreview(GuiGraphics gg,
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
        gg.pose().translate(0.0f, 0.0f, PREVIEW_Z);
        long now = net.minecraft.Util.getMillis();
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

    private static void renderApexCrownBandPreview(GuiGraphics gg,
                                                   float centerX,
                                                   float borderCenterY,
                                                   float starScale,
                                                   int starRenderW,
                                                   int starRenderH,
                                                   int starGroupW,
                                                   float crestScale,
                                                   int crestWBase,
                                                   int crestHBase,
                                                   ResourceLocation starIcon,
                                                   boolean isPerfect) {
        float crestHalfW = crestWBase / 2.0f;
        int crestX = Math.round(centerX - crestHalfW);
        int crestY = Math.round(borderCenterY - (crestHBase / 2.0f));
        float crestCenterX = crestX + (crestWBase / 2.0f);
        float crestCenterY = crestY + (crestHBase / 2.0f);
        gg.pose().pushPose();
        gg.pose().translate(0.0f, 0.0f, PREVIEW_Z);
        gg.pose().translate(crestCenterX, crestCenterY, 0.0f);
        gg.pose().scale(crestScale, crestScale, 1.0f);
        gg.pose().translate(-APEX_CREST_TEX_W / 2.0f, -APEX_CREST_TEX_H / 2.0f, 0.0f);
        gg.blit(APEX_CREST, 0, 0, 0, 0, APEX_CREST_TEX_W, APEX_CREST_TEX_H, APEX_CREST_TEX_W, APEX_CREST_TEX_H);
        gg.pose().popPose();

        int leftStartX = Math.round(centerX - crestHalfW - APEX_CREST_GAP_PX - starGroupW);
        int rightStartX = Math.round(centerX + crestHalfW + APEX_CREST_GAP_PX);
        int starY = Math.round(borderCenterY - (starRenderH / 2.0f));
        gg.pose().pushPose();
        gg.pose().translate(0.0f, 0.0f, PREVIEW_Z);
        long now = net.minecraft.Util.getMillis();
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
    }

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

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
