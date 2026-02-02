package elocindev.tierify.forge.compat;

import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.apex.ApexEffect;
import elocindev.tierify.forge.apex.ApexEffectRegistry;
import elocindev.tierify.forge.client.ApexEffectGradientAnimatorForge;
import elocindev.tierify.forge.client.PerfectLabelAnimatorForge;
import elocindev.tierify.forge.client.TierifyTooltipBorderRendererForge;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.item.ReforgeAddition;
import elocindev.tierify.util.StarApexUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class TooltipOverhaulCompatForge {
    private static final Logger LOGGER = LogManager.getLogger("tiered");
    private static final boolean DEBUG_TOOLTIP_COMPARE =
            Boolean.parseBoolean(System.getProperty("tierify.debug.tooltip_compare", "false"));
    private static final String MOD_ID = "tooltipoverhaul";
    private static final float SET_BONUS_LABEL_NUDGE_Y = 4.0f;
    private static final ResourceLocation SET_BONUS_CREST =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/setbonusicon2.png");
    private static final ResourceLocation SET_BONUS_CREST_ACTIVE =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/setbonusicon_active.png");
    private static final int SET_BONUS_CREST_TEX_W = 10;
    private static final int SET_BONUS_CREST_TEX_H = 14;
    private static final float SET_BONUS_CREST_SCALE = 0.6f;
    private static final int STAR_RIBBON_GAP_PX = 2;
    private static final int STAR_RIBBON_PAD_X = 6;
    private static final int STAR_GAP_PX = 2;
    private static final float STAR_ICON_SCALE = 0.5f;
    private static final float STAR_BORDER_SCALE = STAR_ICON_SCALE;
    private static final int APEX_STAR_COUNT = 3;
    private static final int APEX_CREST_EXTRA_PX = 2;
    private static final int APEX_CREST_GAP_PX = 4;
    private static final float APEX_CREST_SCALE = 1.20f;
    private static final int APEX_GLOW_COLOR_RGB = 0xF39C38;
    private static final int APEX_GLOW_ALPHA_MIN = 40;
    private static final int APEX_GLOW_ALPHA_MAX = 120;
    private static final double APEX_GLOW_PULSE_MS = 350.0;
    private static final int APEX_SWEEP_COLOR_RGB = 0xF7C45A;
    private static final int APEX_SWEEP_EDGE_RGB = 0xC74A2C;
    private static final int APEX_PERFECT_SWEEP_COLOR_RGB = 0xE8FFFF;
    private static final int APEX_PERFECT_SWEEP_EDGE_RGB = 0x8FE3FF;
    private static final int APEX_SWEEP_ALPHA_MAX = 200;
    private static final float APEX_SWEEP_LEN_FRAC = 0.35f;
    private static final double APEX_SWEEP_PERIOD_MS = 8000.0;
    private static final int APEX_SWEEP_THICKNESS = 1;
    private static final int RIBBON_LEFT_W = 24;
    private static final int RIBBON_MID_W = 20;
    private static final int RIBBON_RIGHT_W = 24;
    private static final int RIBBON_H = 10;
    private static final int STAR_TEX_W = 17;
    private static final int STAR_TEX_H = 16;
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
    private static final float STAR_RIBBON_SCALE = 0.75f;
    private static final ResourceLocation APEX_CROWN_LEFT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/crown/crown_left.png");
    private static final ResourceLocation APEX_CROWN_MID =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/crown/crown_center.png");
    private static final ResourceLocation APEX_CROWN_RIGHT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/crown/crown_right.png");
    private static final int APEX_PLATE_TEX_W = 68;
    private static final int APEX_PLATE_TEX_H = 20;
    private static final int APEX_PLATE_PAD_X = 6;
    private static final int APEX_PLATE_LEFT_W = 24;
    private static final int APEX_PLATE_MID_W = 20;
    private static final int APEX_PLATE_RIGHT_W = 24;
    private static final float APEX_PLATE_SCALE = 0.9f;
    private static final float APEX_PLATE_CONTENT_NUDGE_PX = 0.5f;
    private static final float APEX_PLATE_HEIGHT_SCALE = 0.8f;
    private static final float APEX_PLATE_STAR_SCALE = 1.75f;
    private static final float APEX_CREST_APEX_SCALE = 1.05f;
    private static final ResourceLocation STAR_RIBBON_LEFT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/ribbon/ribbon_left.png");
    private static final ResourceLocation STAR_RIBBON_MID =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/ribbon/ribbon_mid.png");
    private static final ResourceLocation STAR_RIBBON_RIGHT =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/ribbon/ribbon_right.png");
    private static final ResourceLocation STAR_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/star.png");
    private static final ResourceLocation PERFECT_STAR_ICON =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/gui/perfect_star.png");
    private static final ResourceLocation APEX_CREST =
            ResourceLocation.fromNamespaceAndPath("tiered", "textures/item/apex_crux.png");
    private static final int APEX_CREST_TEX_W = 36;
    private static final int APEX_CREST_TEX_H = 36;

    private static Object LAYER_PROXY;
    private static volatile Field TO_PADDING_X_FIELD;
    private static volatile Field TO_PADDING_Y_FIELD;
    private static volatile boolean TO_PADDING_LOOKED_UP = false;
    private static volatile Method TO_GET_EXTRA_TEXT_POSITION;
    private static volatile Class<?> TO_TEXT_TYPE_CLASS;
    private static volatile Class<?> TO_TEXT_AXIS_CLASS;
    private static volatile Class<?> TO_TOOLTIP_CONTEXT_CLASS;
    private static volatile boolean TO_EXTRA_TEXT_LOOKED_UP = false;

    private TooltipOverhaulCompatForge() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static void init() {
        if (!isLoaded() || LAYER_PROXY != null) return;
        try {
            Class<?> rendererClass = Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipRenderer");
            Field layersField = rendererClass.getDeclaredField("LAYERS_MAIN");
            layersField.setAccessible(true);
            Object layersObj = layersField.get(null);
            if (!(layersObj instanceof List<?>)) return;
            @SuppressWarnings("unchecked")
            List<Object> layers = (List<Object>) layersObj;

            Class<?> layerInterface = Class.forName("dev.xylonity.tooltipoverhaul.client.layer.ITooltipLayer");
            Object proxy = Proxy.newProxyInstance(
                    layerInterface.getClassLoader(),
                    new Class<?>[]{layerInterface},
                    new LayerHandler()
            );

            layers.add(proxy);
            LAYER_PROXY = proxy;
        } catch (Throwable ignored) {
        }
    }

    private static final class LayerHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("render".equals(name) && args != null && args.length >= 7) {
                renderLayer(args);
                return null;
            }

            if ("getDepth".equals(name) || "depth".equals(name) || "layerDepth".equals(name)) {
                Object depth = resolveLayerDepth();
                if (depth != null && method.getReturnType().isInstance(depth)) return depth;
                return null;
            }

            Class<?> ret = method.getReturnType();
            if (ret == boolean.class) return false;
            if (ret == byte.class) return (byte) 0;
            if (ret == short.class) return (short) 0;
            if (ret == int.class) return 0;
            if (ret == long.class) return 0L;
            if (ret == float.class) return 0.0f;
            if (ret == double.class) return 0.0d;
            if (ret == char.class) return '\0';
            return null;
        }
    }

    private static Object resolveLayerDepth() {
        try {
            Class<?> depthClass = Class.forName("dev.xylonity.tooltipoverhaul.client.layer.LayerDepth");
            if (depthClass.isEnum()) {
                @SuppressWarnings("unchecked")
                Object val = Enum.valueOf((Class<Enum>) depthClass, "BACKGROUND_OVERLAY");
                return val;
            }
            Field f = depthClass.getField("BACKGROUND_OVERLAY");
            return f.get(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static float resolveLayerDepthZ() {
        Object depth = resolveLayerDepth();
        if (depth == null) return 400.0f;

        Float value = readDepthValue(depth, "getZ", "getZLevel", "getZIndex", "z");
        if (value != null) return value;

        value = readDepthField(depth, "z", "Z", "depth", "level");
        return value != null ? value : 400.0f;
    }

    private static Float readDepthValue(Object depth, String... names) {
        for (String name : names) {
            try {
                Method m = depth.getClass().getMethod(name);
                m.setAccessible(true);
                Object out = m.invoke(depth);
                if (out instanceof Number number) return number.floatValue();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static Float readDepthField(Object depth, String... names) {
        for (String name : names) {
            try {
                Field f = depth.getClass().getField(name);
                f.setAccessible(true);
                Object out = f.get(depth);
                if (out instanceof Number number) return number.floatValue();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static void renderLayer(Object[] args) {
        if (!ForgeTierifyConfig.tieredTooltip()) return;

        Object ctx = args[0];
        Object size = findSizeArg(args);
        Object pos = findPosArg(args, size);
        Object fontObj = (args.length > 5) ? args[5] : null;
        if (!(fontObj instanceof Font)) {
            fontObj = findFontArg(args);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        ItemStack stack = getItemStack(ctx);
        ItemStack ctxStack = stack;
        if (stack == null || stack.isEmpty()) {
            debugTooltipRenderSummary("skip_no_ctx_stack", ItemStack.EMPTY, ItemStack.EMPTY, null, null);
            return;
        }
        List<ClientTooltipComponent> components = findTooltipComponents(ctx);
        List<Component> textLines = findTooltipTextLines(ctx);
        String tooltipTitle = findTooltipTitleText(components, textLines);
        stack = resolveStackForRenderedTooltip(stack, components, textLines);
        if (stack == null || stack.isEmpty()) {
            debugTooltipRenderSummary("skip_unresolved_stack", ctxStack, ItemStack.EMPTY, tooltipTitle, null);
            return;
        }

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        String tierId = null;
        boolean isPerfect = false;
        boolean hasTieredTag = false;
        boolean isApex = false;
        String lookupKey;

        if (tiered != null && tiered.contains(TierifyConstants.NBT_SUBTAG_DATA_KEY)) {
            tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
            isPerfect = tiered.getBoolean("Perfect");
            if ((tierId == null || tierId.isEmpty()) && !isPerfect) {
                debugTooltipRenderSummary("skip_missing_tier_id", ctxStack, stack, tooltipTitle, null);
                return;
            }
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
            hasTieredTag = true;
        } else if (stack.getItem() instanceof ReforgeAddition) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id == null) {
                debugTooltipRenderSummary("skip_reforge_without_id", ctxStack, stack, tooltipTitle, null);
                return;
            }
            lookupKey = id.toString();
        } else {
            debugTooltipRenderSummary("skip_not_tiered_or_reforge", ctxStack, stack, tooltipTitle, null);
            return;
        }

        boolean usePerfectBorder = isPerfect;
        TierifyTooltipBorderRendererForge.Template template = TierifyTooltipBorderRendererForge.findTemplate(lookupKey, usePerfectBorder);
        if (template == null) {
            debugTooltipRenderSummary("skip_no_template", ctxStack, stack, tooltipTitle, lookupKey);
            return;
        }

        GuiGraphics gg = getGuiGraphics(ctx);
        if (gg == null) {
            debugTooltipRenderSummary("skip_no_guigraphics", ctxStack, stack, tooltipTitle, lookupKey);
            return;
        }

        int width = readPointValue(size, "x", "getX", "field_1343", "width", "getWidth");
        int height = readPointValue(size, "y", "getY", "field_1342", "height", "getHeight");

        Float ctxPosX = tryReadNumber(ctx, "x", "getX", "tooltipX", "getTooltipX");
        Float ctxPosY = tryReadNumber(ctx, "y", "getY", "tooltipY", "getTooltipY");
        Float posX = tryReadNumber(pos, "x", "getX", "field_1343");
        Float posY = tryReadNumber(pos, "y", "getY", "field_1342");
        int x;
        int y;
        if (ctxPosX != null && ctxPosY != null) {
            x = Math.round(ctxPosX);
            y = Math.round(ctxPosY);
        } else if (posX != null && posY != null) {
            x = Math.round(posX);
            y = Math.round(posY);
        } else {
            int[] fallback = fallbackPosFromContext(ctx, width, height);
            if (fallback == null) {
                debugTooltipRenderSummary("skip_no_position", ctxStack, stack, tooltipTitle, lookupKey);
                return;
            }
            x = fallback[0];
            y = fallback[1];
        }

        if (width <= 0 || height <= 0) {
            debugTooltipRenderSummary("skip_non_positive_size", ctxStack, stack, tooltipTitle, lookupKey);
            return;
        }

        float baseZ = resolveLayerDepthZ();

        gg.pose().pushPose();
        gg.pose().translate(0.0F, 0.0F, baseZ);
        TierifyTooltipBorderRendererForge.renderOverlay(gg, x, y, width, height, template);
        if (isApex) {
            float progress = (float) ((Util.getMillis() % APEX_SWEEP_PERIOD_MS) / APEX_SWEEP_PERIOD_MS);
            int sweepCenter = isPerfect ? APEX_PERFECT_SWEEP_COLOR_RGB : APEX_SWEEP_COLOR_RGB;
            int sweepEdge = isPerfect ? APEX_PERFECT_SWEEP_EDGE_RGB : APEX_SWEEP_EDGE_RGB;
            TierifyTooltipBorderRendererForge.renderPerimeterSweep(
                    gg,
                    x, y,
                    width, height,
                    sweepCenter,
                    sweepEdge,
                    APEX_SWEEP_ALPHA_MAX,
                    APEX_SWEEP_THICKNESS,
                    progress,
                    APEX_SWEEP_LEN_FRAC
            );
            TierifyTooltipBorderRendererForge.renderPieces(gg, x, y, width, height, template);
        }
        gg.pose().popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (fontObj instanceof Font font && hasTieredTag) {
            int stars = StarApexUtils.getStars(stack);
            boolean mythicStars = tierId != null && tierId.startsWith("tiered:mythic");
            if (!mythicStars) {
                stars = 0;
            }
            ResourceLocation starIcon = isPerfect ? PERFECT_STAR_ICON : STAR_ICON;
            if (StarApexUtils.isApex(stack)) {
                renderApexCrownPlate(gg, font, x, y, width, height, components, textLines, ctx, stack, baseZ, starIcon, isPerfect);
            } else if (stars > 0) {
                renderStarsRibbon(gg, font, x, y, width, height, components, textLines, ctx, stars, baseZ, starIcon, isPerfect);
            }
            renderSetBonusCrest(gg, font, x, y, width, components, textLines, stack, tierId, baseZ, ctx);
            if (isPerfect) {
                renderPerfectLabel(gg, font, x, y, width, baseZ);
            }
        }
        debugTooltipRenderSummary("rendered", ctxStack, stack, tooltipTitle, lookupKey);
    }

    private static ItemStack getItemStack(Object ctx) {
        Object stack = callNoArg(ctx, "stack", "getStack");
        if (stack instanceof ItemStack itemStack) return itemStack;
        stack = readField(ctx, "stack");
        return (stack instanceof ItemStack itemStack) ? itemStack : null;
    }

    private static ItemStack resolveStackForRenderedTooltip(ItemStack ctxStack,
                                                            List<ClientTooltipComponent> components,
                                                            List<Component> textLines) {
        if (ctxStack == null || ctxStack.isEmpty()) return ctxStack;
        String tooltipTitle = findTooltipTitleText(components, textLines);
        if (tooltipTitle == null || tooltipTitle.isEmpty()) return ctxStack;
        if (isLikelyStatLine(tooltipTitle)) return ctxStack;
        if (tooltipTitleMatchesStack(tooltipTitle, ctxStack)) return ctxStack;
        ItemStack equipped = findMatchingEquippedArmorStack(tooltipTitle);
        if (equipped != null && !equipped.isEmpty()) {
            debugTooltipCompare("resolved equipped tooltip stack: title='{}' ctx='{}' resolved='{}'",
                    tooltipTitle,
                    safeName(ctxStack),
                    safeName(equipped));
            return equipped;
        }
        debugTooltipCompare("unable to resolve tooltip stack; skipping overlay: title='{}' ctx='{}'",
                tooltipTitle,
                safeName(ctxStack));
        return ItemStack.EMPTY;
    }

    private static boolean isLikelyStatLine(String text) {
        if (text == null || text.isEmpty()) return false;
        return text.contains("/") || text.matches(".*\\d.*");
    }

    private static String findTooltipTitleText(List<ClientTooltipComponent> components, List<Component> textLines) {
        if (components != null && !components.isEmpty()) {
            for (ClientTooltipComponent component : components) {
                String s = getTooltipString(component);
                if (s != null && !s.isEmpty()) return s;
            }
        }
        if (textLines != null && !textLines.isEmpty()) {
            for (Component line : textLines) {
                if (line == null) continue;
                String s = line.getString();
                if (s != null && !s.isEmpty()) return s;
            }
        }
        return null;
    }

    private static boolean tooltipTitleMatchesStack(String title, ItemStack stack) {
        if (title == null || title.isEmpty() || stack == null || stack.isEmpty()) return false;
        String normalizedTitle = normalizeTooltipTitle(title);
        if (normalizedTitle.isEmpty()) return false;
        String hoverName = normalizeTooltipTitle(stack.getHoverName().getString());
        if (hoverName.isEmpty()) return false;
        return normalizedTitle.contains(hoverName) || hoverName.contains(normalizedTitle);
    }

    private static ItemStack findMatchingEquippedArmorStack(String title) {
        Player player = Minecraft.getInstance().player;
        if (player == null || title == null || title.isEmpty()) return ItemStack.EMPTY;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armor = player.getItemBySlot(slot);
            if (armor.isEmpty()) continue;
            if (tooltipTitleMatchesStack(title, armor)) {
                debugTooltipCompare("equipped match slot={} title='{}' item='{}'",
                        slot.getName(),
                        title,
                        safeName(armor));
                return armor;
            }
        }
        return ItemStack.EMPTY;
    }

    private static String normalizeTooltipTitle(String value) {
        if (value == null || value.isEmpty()) return "";
        StringBuilder out = new StringBuilder(value.length());
        boolean skipCode = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (skipCode) {
                skipCode = false;
                continue;
            }
            if (c == '\u00A7') {
                skipCode = true;
                continue;
            }
            if (Character.isISOControl(c)) continue;
            out.append(c);
        }
        return out.toString().trim();
    }

    private static void debugTooltipCompare(String message, Object... args) {
        if (!DEBUG_TOOLTIP_COMPARE) return;
        LOGGER.info("[Tierify/TOCompareDebug] " + message, args);
    }

    private static void debugTooltipRenderSummary(String outcome,
                                                  ItemStack ctxStack,
                                                  ItemStack renderStack,
                                                  String tooltipTitle,
                                                  String lookupKey) {
        if (!DEBUG_TOOLTIP_COMPARE) return;
        String tierId = "";
        int stars = 0;
        boolean apex = false;
        if (renderStack != null && !renderStack.isEmpty()) {
            CompoundTag tiered = renderStack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
            if (tiered != null) {
                tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
            }
            stars = StarApexUtils.getStars(renderStack);
            apex = StarApexUtils.isApex(renderStack);
        }
        LOGGER.info("[Tierify/TOCompareDebug] renderSummary outcome={} title='{}' ctx='{}' render='{}' tier='{}' stars={} apex={} lookup='{}'",
                outcome,
                tooltipTitle == null ? "" : tooltipTitle,
                safeName(ctxStack),
                safeName(renderStack),
                tierId,
                stars,
                apex,
                lookupKey == null ? "" : lookupKey);
    }

    private static String safeName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "<empty>";
        try {
            return stack.getHoverName().getString();
        } catch (Throwable ignored) {
            return "<name_error>";
        }
    }

    private static void renderApexNameGlow(GuiGraphics gg,
                                           Font font,
                                           int bgX,
                                           int bgY,
                                           int bgW,
                                           List<ClientTooltipComponent> components,
                                           List<Component> textLines,
                                           Object ctx,
                                           ItemStack stack,
                                           float baseZ) {
        if (stack == null || stack.isEmpty()) return;
        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return;
        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if (tierId == null || tierId.isEmpty()) return;
        Component prefixComponent = Component.translatable(tierId + ".label")
                .withStyle(net.minecraft.ChatFormatting.BOLD)
                .append(" ");
        Component title = stack.getHoverName();
        if (title == null) return;

        int paddingX = resolveTooltipOverhaulPaddingX();
        int extraTitleX = resolveTooltipOverhaulExtraTextOffset(ctx, "TITLE", "X");
        int contentX = bgX + paddingX + extraTitleX;
        int contentW = bgW - (paddingX * 2) - extraTitleX;
        if (contentW <= 0) {
            contentX = bgX;
            contentW = bgW;
        }

        int titleIndex = 0;
        int titleY = bgY + resolveTooltipOverhaulPaddingY() + 4;
        if (components != null && !components.isEmpty()) {
            titleY = computeTooltipOverhaulLineTopY(ctx, components, titleIndex, bgY);
        } else if (textLines != null && !textLines.isEmpty()) {
            titleY = Math.round(textTopYForIndexText(bgY, titleIndex, font.lineHeight));
        }

        int textWidth = font.width(title);
        int drawX = contentX;
        if (ForgeTierifyConfig.centerName()) {
            ClientTooltipComponent component = (components != null && !components.isEmpty()) ? components.get(0) : null;
            Integer alignedX = resolveTooltipOverhaulTitleAlignmentX(ctx, bgX, bgW, font, component);
            drawX = (alignedX != null) ? alignedX : contentX + (contentW - textWidth) / 2;
        }

        int prefixX = drawX;

        long now = Util.getMillis();
        float pulse = (float) ((Math.sin(now / APEX_GLOW_PULSE_MS) + 1.0) * 0.5);
        int alpha = Math.round(APEX_GLOW_ALPHA_MIN + (APEX_GLOW_ALPHA_MAX - APEX_GLOW_ALPHA_MIN) * pulse);
        int color = (alpha << 24) | APEX_GLOW_COLOR_RGB;

        gg.pose().pushPose();
        gg.pose().translate(0.0f, 0.0f, baseZ + 9.0f);
        gg.drawString(font, prefixComponent, prefixX - 1, titleY, color, false);
        gg.drawString(font, prefixComponent, prefixX + 1, titleY, color, false);

        int overlayAlpha = Math.round(60 + (160 * pulse));
        int overlayColor = (overlayAlpha << 24) | APEX_GLOW_COLOR_RGB;
        gg.drawString(font, prefixComponent, prefixX, titleY, overlayColor, false);
        gg.pose().popPose();
    }

    private static Integer resolveTooltipOverhaulTitleAlignmentX(Object ctx,
                                                                 int bgX,
                                                                 int bgW,
                                                                 Font font,
                                                                 ClientTooltipComponent component) {
        if (component == null || ctx == null) return null;
        ItemStack stack = getItemStack(ctx);
        boolean hasIcon = stack != null && !stack.isEmpty();
        boolean disableIcon = resolveTooltipOverhaulDisableIcon(stack);
        int paddingX = resolveTooltipOverhaulPaddingX();
        int firstLineOffset = paddingX + (hasIcon ? 26 : 0) - (disableIcon ? 26 : 0);
        int baseX = bgX + resolveTooltipOverhaulExtraTextOffset(ctx, "TITLE", "X") - (hasIcon ? 0 : 1);
        Point size = new Point(bgW, 0);
        return callTitleAlignmentX(baseX, firstLineOffset, size, component, font, ctx);
    }

    private static Integer callTitleAlignmentX(int posx,
                                               int offset,
                                               Point size,
                                               ClientTooltipComponent component,
                                               Font font,
                                               Object ctx) {
        try {
            Class<?> utilClass = Class.forName("dev.xylonity.tooltipoverhaul.util.Util");
            Class<?> ctxClass = Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipContext");
            Method method = null;
            for (Method m : utilClass.getMethods()) {
                if (!m.getName().equals("getTitleAlignmentX")) continue;
                if (m.getParameterCount() != 6) continue;
                method = m;
                break;
            }
            if (method == null) return null;
            Object out = method.invoke(null, posx, offset, size, component, font, ctxClass.cast(ctx));
            if (out instanceof Number number) return number.intValue();
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static GuiGraphics getGuiGraphics(Object ctx) {
        Object gg = callNoArg(ctx, "graphics", "getGraphics");
        if (gg instanceof GuiGraphics guiGraphics) return guiGraphics;
        gg = readField(ctx, "graphics");
        return (gg instanceof GuiGraphics guiGraphics) ? guiGraphics : null;
    }

    private static Object callNoArg(Object target, String... names) {
        if (target == null) return null;
        for (String name : names) {
            try {
                Method m = target.getClass().getMethod(name);
                m.setAccessible(true);
                return m.invoke(target);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static Object readField(Object target, String name) {
        if (target == null) return null;
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static float readNumber(Object target, String... names) {
        Float value = tryReadNumber(target, names);
        if (value != null) return value;
        return 0.0f;
    }

    private static Float tryReadNumber(Object target, String... names) {
        if (target == null) return null;
        for (String name : names) {
            Object value = readField(target, name);
            if (value instanceof Number number) return number.floatValue();
            try {
                Method m = target.getClass().getMethod(name);
                m.setAccessible(true);
                Object out = m.invoke(target);
                if (out instanceof Number number) return number.floatValue();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static int readPointValue(Object target, String... names) {
        if (target instanceof Point point) {
            if (names.length > 0 && ("y".equalsIgnoreCase(names[0]) || "height".equalsIgnoreCase(names[0]))) {
                return point.y;
            }
            return point.x;
        }
        Float out = tryReadNumber(target, names);
        return (out != null) ? Math.round(out) : 0;
    }

    private static Object findSizeArg(Object[] args) {
        if (args == null) return null;
        if (args.length > 2 && args[2] instanceof Point) {
            return args[2];
        }
        for (Object arg : args) {
            if (arg instanceof Point) return arg;
        }
        return (args.length > 2) ? args[2] : null;
    }

    private static Object findPosArg(Object[] args, Object sizeArg) {
        if (args == null) return null;
        if (args.length > 1 && args[1] != null && args[1] != sizeArg) {
            return args[1];
        }
        for (Object arg : args) {
            if (arg == null || arg == sizeArg || arg instanceof Point) continue;
            String name = arg.getClass().getName();
            if (name.contains("Vec2") || name.contains("Vector2")) {
                return arg;
            }
        }
        for (Object arg : args) {
            if (arg == null || arg == sizeArg || arg instanceof Point) continue;
            if (tryReadNumber(arg, "x", "getX", "field_1343") != null
                    && tryReadNumber(arg, "y", "getY", "field_1342") != null) {
                return arg;
            }
        }
        return (args.length > 1) ? args[1] : null;
    }

    private static Object findFontArg(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof Font) return arg;
        }
        return null;
    }

    private static int[] fallbackPosFromContext(Object ctx, int width, int height) {
        if (ctx == null) return null;
        Float mouseX = tryReadNumber(ctx, "mouseX", "getMouseX");
        Float mouseY = tryReadNumber(ctx, "mouseY", "getMouseY");
        Float screenW = tryReadNumber(ctx, "width", "getWidth", "screenW", "getScreenW");
        Float screenH = tryReadNumber(ctx, "height", "getHeight", "screenH", "getScreenH");
        if (mouseX == null || mouseY == null || screenW == null || screenH == null) return null;

        int margin = 4;
        int xRight = Math.round(mouseX) + 12;
        int xLeft = Math.round(mouseX) - 16 - width;
        int maxX = Math.max(margin, Math.round(screenW) - margin - width);
        int x = (xRight + width <= Math.round(screenW) - margin)
                ? xRight
                : (xLeft >= margin ? xLeft : maxX);

        int clampedHeight = Math.min(height, Math.max(0, Math.round(screenH) - 8));
        int maxY = Math.max(margin, Math.round(screenH) - clampedHeight - margin);
        int y = Math.max(margin, Math.min(Math.round(mouseY) - 12, maxY));

        return new int[]{x, y};
    }

    private static void renderSetBonusLabel(GuiGraphics gg, Font font, int bgX, int bgY, int bgWidth, ItemStack stack, String tierId, float baseZ) {
        Component label = buildSetBonusLabel(stack, tierId);
        if (label == null) return;

        float scale = 0.65f;
        int textWidth = font.width(label);
        float scaledWidth = textWidth * scale;
        float xPos = bgX + (bgWidth - scaledWidth) / 2f;

        float baseHeight = 9f;
        float scaledHeight = baseHeight * scale;

        float topPadding = 4f;
        float gapTop = bgY - 3f;
        float gapBottom = bgY + topPadding;

        float yPos = gapTop + ((gapBottom - gapTop) - scaledHeight) / 2f;
        float yOffset = (baseHeight - scaledHeight) / 2f;
        yPos += yOffset;
        yPos += SET_BONUS_LABEL_NUDGE_Y;

        gg.pose().pushPose();
        gg.pose().translate(xPos, yPos, baseZ + 10.0f);
        gg.pose().scale(scale, scale, 1.0f);
        gg.drawString(font, label, 0, 0, 0xFFFFFF, true);
        gg.pose().popPose();
    }

    private static void renderPerfectLabel(GuiGraphics gg, Font font, int bgX, int bgY, int bgWidth, float baseZ) {
        Component label = PerfectLabelAnimatorForge.animatedLabel(Util.getMillis());
        float scale = 0.65f;
        int textWidth = font.width(label);
        float centeredX = bgX + (bgWidth / 2.0f) - ((textWidth * scale) / 2.0f);
        float fixedY = bgY + 22.0f;

        gg.pose().pushPose();
        gg.pose().translate(centeredX, fixedY, baseZ + 10.0f);
        gg.pose().scale(scale, scale, 1.0f);
        gg.drawString(font, label, 0, 0, 0xFFFFFF, true);
        gg.pose().popPose();
    }

    private static void renderSetBonusCrest(GuiGraphics gg,
                                            Font font,
                                            int bgX,
                                            int bgY,
                                            int bgWidth,
                                            List<ClientTooltipComponent> components,
                                            List<Component> textLines,
                                            ItemStack stack,
                                            String tierId,
                                            float baseZ,
                                            Object ctx) {
        if (!shouldRenderSetBonusCrest(stack, tierId)) return;
        if ((components == null || components.isEmpty()) && (textLines == null || textLines.isEmpty())) return;

        int headerIndex = -1;
        Component header = null;
        int textWidth;
        int lineY;
        int iconW = SET_BONUS_CREST_TEX_W;
        int iconH = SET_BONUS_CREST_TEX_H;

        int fallbackLineY = Math.round(bgY + (font.lineHeight * 4));

        if (components != null && !components.isEmpty()) {
            headerIndex = findArmorHeaderIndex(font, components);
            if (headerIndex >= 0) {
                header = getTooltipText(components.get(headerIndex));
            }
            if (header != null) {
                textWidth = font.width(header);
                lineY = Math.round(textTopYForIndex(bgY, components, headerIndex));
            } else {
                textWidth = font.width(Component.translatable("item.modifiers.feet"));
                lineY = fallbackLineY;
            }
        } else if (textLines != null && !textLines.isEmpty()) {
            headerIndex = findArmorHeaderIndexText(textLines);
            if (headerIndex >= 0) {
                header = textLines.get(headerIndex);
            }
            if (header != null) {
                textWidth = font.width(header);
                lineY = Math.round(textTopYForIndexText(bgY, headerIndex, font.lineHeight));
            } else {
                textWidth = font.width(Component.translatable("item.modifiers.feet"));
                lineY = fallbackLineY;
            }
        } else {
            return;
        }

        int titleOffset = font.lineHeight + 2;
        if (lineY <= bgY + (font.lineHeight * 2)) {
            lineY += titleOffset;
            lineY += font.lineHeight * 2;
        }
        int iconY = lineY + (font.lineHeight - iconH) / 2 - 3;

        LineMetrics durability = findDurabilityLineMetrics(components, textLines, font, bgY, ctx);
        if (durability != null) {
            lineY = durability.topY;
            iconY = lineY + (font.lineHeight - iconH) / 2 - 2;
            textWidth = durability.width;
        }

        if (durability == null) {
            int widthOverride = -1;
            if (textLines != null && !textLines.isEmpty()) {
                widthOverride = findTextWidthForLineY(textLines, font, bgY, lineY);
            }
            if (widthOverride <= 0 && components != null && !components.isEmpty()) {
                widthOverride = findComponentWidthForLineY(components, font, bgY, lineY);
            }
            if (widthOverride > 0) {
                textWidth = widthOverride;
            } else if (header == null) {
                Component widthLine = (textLines != null && !textLines.isEmpty())
                        ? findClosestTextLine(textLines, font, bgY, lineY)
                        : findClosestComponentText(components, font, bgY, lineY);
                if (widthLine != null) {
                    textWidth = font.width(widthLine);
                }
            }
        }

        if (textWidth <= 0) return;

        int paddingX = resolveTooltipOverhaulPaddingX();
        int extraX = resolveTooltipOverhaulExtraTextOffset(ctx);
        int lineStartX = bgX + paddingX + extraX;
        int iconX = lineStartX + textWidth + 2;
        int maxX = bgX + bgWidth - iconW - 2;
        if (iconX > maxX) iconX = Math.max(bgX + 2, maxX);

        boolean active = isSetBonusActive(stack, tierId);

        gg.pose().pushPose();
        gg.pose().translate(0.0F, 0.0F, baseZ + 10.0f);
        drawSetBonusCrest(gg, iconX, iconY, iconW, iconH, active);
        gg.pose().popPose();
    }

    private static void renderStarsRibbon(GuiGraphics gg,
                                          Font font,
                                          int bgX,
                                          int bgY,
                                          int bgW,
                                          int bgH,
                                          List<ClientTooltipComponent> components,
                                          List<Component> textLines,
                                          Object ctx,
                                          int stars,
                                          float baseZ,
                                          ResourceLocation starIcon,
                                          boolean isPerfect) {
        if (stars <= 0) return;

        float starScale = STAR_BORDER_SCALE;
        int starRenderW = Math.round(STAR_TEX_W * starScale);
        int starRenderH = Math.round(STAR_TEX_H * starScale);
        int starGroupW = (stars * starRenderW) + ((stars - 1) * STAR_GAP_PX);

        float centerX = bgX + (bgW / 2.0f);
        int titleIndex = 0;
        int titleY = bgY + resolveTooltipOverhaulPaddingY() + 4;
        if (components != null && !components.isEmpty()) {
            titleY = computeTooltipOverhaulLineTopY(ctx, components, titleIndex, bgY);
        } else if (textLines != null && !textLines.isEmpty()) {
            titleY = Math.round(textTopYForIndexText(bgY, titleIndex, font.lineHeight));
        }
        float starCenterY = titleY - 2.0f - (starRenderH / 2.0f);
        renderStarBand(gg, centerX, starCenterY, stars, starRenderW, starRenderH, starGroupW, starScale, baseZ, starIcon, isPerfect);
    }


    private static void renderStarBand(GuiGraphics gg,
                                       float centerX,
                                       float borderCenterY,
                                       int stars,
                                       int starRenderW,
                                       int starRenderH,
                                       int starGroupW,
                                       float starScale,
                                       float baseZ,
                                       ResourceLocation starIcon,
                                       boolean isPerfect) {
        int starsX = Math.round(centerX - (starGroupW / 2.0f));
        int starY = Math.round(borderCenterY - (starRenderH / 2.0f));
        gg.pose().pushPose();
        gg.pose().translate(0.0F, 0.0F, baseZ + 10.0f);
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

    private static void renderApexCrownPlate(GuiGraphics gg,
                                             Font font,
                                             int bgX,
                                             int bgY,
                                             int bgW,
                                             int bgH,
                                             List<ClientTooltipComponent> components,
                                             List<Component> textLines,
                                             Object ctx,
                                             ItemStack stack,
                                             float baseZ,
                                             ResourceLocation starIcon,
                                             boolean isPerfect) {
        if (stack == null || stack.isEmpty()) return;
        float starScale = STAR_BORDER_SCALE;
        int starRenderW = Math.round(STAR_TEX_W * starScale);
        int starRenderH = Math.round(STAR_TEX_H * starScale);
        int starGroupW = (APEX_STAR_COUNT * starRenderW) + ((APEX_STAR_COUNT - 1) * STAR_GAP_PX);

        float crestBaseScale = (starRenderH + APEX_CREST_EXTRA_PX) / (float) APEX_CREST_TEX_H;
        crestBaseScale *= (APEX_CREST_SCALE * APEX_CREST_APEX_SCALE);
        int crestWBase = Math.round(APEX_CREST_TEX_W * crestBaseScale);
        int crestHBase = Math.round(APEX_CREST_TEX_H * crestBaseScale);
        float crestScale = crestBaseScale * (1.0f + 0.02f * (float) Math.sin(Util.getMillis() / 700.0));

        float centerX = bgX + (bgW / 2.0f);
        int titleIndex = 0;
        int titleY = bgY + resolveTooltipOverhaulPaddingY() + 4;
        if (components != null && !components.isEmpty()) {
            titleY = computeTooltipOverhaulLineTopY(ctx, components, titleIndex, bgY);
        } else if (textLines != null && !textLines.isEmpty()) {
            titleY = Math.round(textTopYForIndexText(bgY, titleIndex, font.lineHeight));
        }
        float bandCenterY = titleY - 2.0f - (starRenderH / 2.0f);
        renderApexCrownBand(gg, centerX, bandCenterY, starScale, starRenderW, starRenderH, starGroupW,
                crestScale, crestWBase, crestHBase, baseZ, starIcon, isPerfect);

    }

    private static void renderApexCrownBand(GuiGraphics gg,
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
        gg.pose().translate(0.0F, 0.0F, baseZ + 10.0f);
        gg.pose().translate(crestCenterX, crestCenterY, 0.0f);
        gg.pose().scale(crestScale, crestScale, 1.0f);
        gg.pose().translate(-APEX_CREST_TEX_W / 2.0f, -APEX_CREST_TEX_H / 2.0f, 0.0f);
        gg.blit(APEX_CREST, 0, 0, 0, 0, APEX_CREST_TEX_W, APEX_CREST_TEX_H, APEX_CREST_TEX_W, APEX_CREST_TEX_H);
        gg.pose().popPose();

        int leftStartX = Math.round(centerX - crestHalfW - APEX_CREST_GAP_PX - starGroupW);
        int rightStartX = Math.round(centerX + crestHalfW + APEX_CREST_GAP_PX);
        int starY = Math.round(borderCenterY - (starRenderH / 2.0f));
        gg.pose().pushPose();
        gg.pose().translate(0.0F, 0.0F, baseZ + 10.0f);
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

    private static int resolveTooltipOverhaulHeight(List<ClientTooltipComponent> components,
                                                    List<Component> textLines,
                                                    int bgY) {
        if (components != null && !components.isEmpty()) {
            int height = (components.size() == 1) ? -2 : 0;
            for (int i = 0; i < components.size(); i++) {
                height += components.get(i).getHeight();
                if (i == 0) height += 2;
            }
            return Math.max(16, height);
        }
        if (textLines != null && !textLines.isEmpty()) {
            return Math.max(16, textLines.size() * 10);
        }
        return 16;
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

    // embers removed

    private static boolean shouldRenderSetBonusCrest(ItemStack stack, String tierId) {
        if (tierId == null || tierId.isEmpty()) return false;
        if (!(stack.getItem() instanceof ArmorItem armor)) return false;
        if (!ForgeTierifyConfig.enableArmorSetBonuses()) return false;

        Player player = Minecraft.getInstance().player;
        if (player == null) return false;

        EquipmentSlot slot = armor.getEquipmentSlot();
        return isEquippedArmorStack(player, slot, stack);
    }

    private static boolean isSetBonusActive(ItemStack stack, String tierId) {
        if (!(stack.getItem() instanceof ArmorItem)) return false;
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return hasPerfectSetBonus(player, tierId) || hasSetBonus(player, tierId);
    }

    private static int findArmorHeaderIndex(Font font, List<ClientTooltipComponent> components) {
        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent component = components.get(i);
            Component text = getTooltipText(component);
            String line = (text != null) ? text.getString() : getTooltipString(component);
            if (isArmorHeaderLine(line)) return i;
        }
        return -1;
    }

    private static int findArmorHeaderIndexText(List<Component> textLines) {
        for (int i = 0; i < textLines.size(); i++) {
            Component text = textLines.get(i);
            if (text == null) continue;
            String line = text.getString();
            if (isArmorHeaderLine(line)) return i;
        }
        return -1;
    }

    private static boolean isArmorHeaderLine(String line) {
        if (line == null) return false;
        String normalized = normalizeHeader(line);
        if (normalized.startsWith("when on")) return true;
        if (normalized.startsWith("when in")) return true;
        if (normalized.contains("when equipped")) return true;

        for (String header : armorHeaderStrings()) {
            String headerNorm = normalizeHeader(header);
            if (normalized.equals(headerNorm) || normalized.startsWith(headerNorm) || normalized.contains(headerNorm)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeHeader(String value) {
        return value.toLowerCase(Locale.ROOT).replace(":", "").trim();
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

    private static void renderApexEffectLabelOverride(GuiGraphics gg,
                                                      Font font,
                                                      int bgX,
                                                      int bgY,
                                                      int bgW,
                                                      List<ClientTooltipComponent> components,
                                                      List<Component> textLines,
                                                      Object ctx,
                                                      ItemStack stack,
                                                      float baseZ) {
        if (!TooltipOverhaulCompatForge.isLoaded()) return;
        if (stack == null || stack.isEmpty()) return;
        if (!StarApexUtils.isApex(stack)) return;
        ApexEffect effect = ApexEffectRegistry.resolveFor(stack);
        if (effect == null || effect.triggerType() != ApexEffect.ApexTriggerType.ACTIVE_USE) return;

        int lineY;
        if (components != null && !components.isEmpty()) {
            lineY = Math.round(textTopYForIndex(bgY, components, components.size()));
        } else if (textLines != null && !textLines.isEmpty()) {
            lineY = Math.round(textTopYForIndexText(bgY, textLines.size(), font.lineHeight));
        } else {
            return;
        }

        int paddingX = resolveTooltipOverhaulPaddingX();
        int extraX = resolveTooltipOverhaulExtraTextOffset(ctx);
        int lineX = bgX + paddingX + extraX;

        Component title = Component.literal("Apex Effect")
                .setStyle(Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(0xFFFFFF))
                        .withFont(ApexEffectGradientAnimatorForge.FONT_MAIN));
        Component shift = Component.literal("[SHIFT]")
                .setStyle(Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(0xB0B0B0))
                        .withFont(ApexEffectGradientAnimatorForge.FONT_SMALL));
        Component line = Component.empty().append(title).append(" ").append(shift);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        gg.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        gg.pose().pushPose();
        gg.pose().translate(0.0F, 0.0F, baseZ + 500.0f);
        MultiBufferSource.BufferSource buffer = gg.bufferSource();
        font.drawInBatch(
                line,
                (float) lineX,
                (float) lineY,
                0xFFFFFF,
                false,
                gg.pose().last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                0xF000F0
        );
        buffer.endBatch();
        gg.pose().popPose();
        gg.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }


    private static String getTooltipString(ClientTooltipComponent component) {
        if (component == null) return null;
        Component text = getTooltipText(component);
        if (text != null) return text.getString();

        FormattedCharSequence seq = getTooltipSequence(component);
        return (seq != null) ? formattedSequenceToString(seq) : null;
    }

    private static FormattedCharSequence getTooltipSequence(ClientTooltipComponent component) {
        if (component == null) return null;
        Class<?> type = component.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (!FormattedCharSequence.class.isAssignableFrom(field.getType())) continue;
            try {
                field.setAccessible(true);
                Object value = field.get(component);
                if (value instanceof FormattedCharSequence seq) {
                    return seq;
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return null;
    }

    private static String formattedSequenceToString(FormattedCharSequence seq) {
        StringBuilder out = new StringBuilder();
        seq.accept((index, style, codePoint) -> {
            out.appendCodePoint(codePoint);
            return true;
        });
        return out.toString();
    }


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

    private static float textTopYForIndexText(int tooltipTopY, int index, int lineHeight) {
        int clamped = Math.max(0, index);
        float y = tooltipTopY;
        for (int i = 0; i < clamped; i++) {
            y += lineHeight;
            if (i == 0) y += 2.0f;
        }
        return y;
    }

    private static Component findClosestTextLine(List<Component> textLines, Font font, int bgY, int lineY) {
        if (textLines == null || textLines.isEmpty()) return null;
        int bestIndex = 0;
        float bestDist = Float.MAX_VALUE;
        for (int i = 0; i < textLines.size(); i++) {
            float y = textTopYForIndexText(bgY, i, font.lineHeight);
            float dist = Math.abs(y - lineY);
            if (dist < bestDist) {
                bestDist = dist;
                bestIndex = i;
            }
        }
        return textLines.get(bestIndex);
    }

    private static Component findClosestComponentText(List<ClientTooltipComponent> components, Font font, int bgY, int lineY) {
        if (components == null || components.isEmpty()) return null;
        int bestIndex = -1;
        float bestDist = Float.MAX_VALUE;
        for (int i = 0; i < components.size(); i++) {
            Component text = getTooltipText(components.get(i));
            if (text == null) continue;
            float y = textTopYForIndex(bgY, components, i);
            float dist = Math.abs(y - lineY);
            if (dist < bestDist) {
                bestDist = dist;
                bestIndex = i;
            }
        }
        return bestIndex >= 0 ? getTooltipText(components.get(bestIndex)) : null;
    }

    private static int findComponentWidthForLineY(List<ClientTooltipComponent> components, Font font, int bgY, int lineY) {
        if (components == null || components.isEmpty()) return -1;
        int targetY = Math.round(lineY);
        int combinedWidth = 0;
        boolean matched = false;
        for (int i = 0; i < components.size(); i++) {
            float top = textTopYForIndex(bgY, components, i);
            if (Math.abs(Math.round(top) - targetY) <= 1) {
                combinedWidth += components.get(i).getWidth(font);
                matched = true;
            }
        }
        if (matched) return combinedWidth;

        int bestWidth = -1;
        float bestDist = Float.MAX_VALUE;
        for (int i = 0; i < components.size(); i++) {
            float top = textTopYForIndex(bgY, components, i);
            float dist = Math.abs(top - lineY);
            if (dist < bestDist) {
                bestDist = dist;
                bestWidth = components.get(i).getWidth(font);
            }
        }
        return bestWidth;
    }

    private static int resolveTooltipOverhaulPaddingX() {
        ensureTooltipOverhaulPaddingFields();
        Field field = TO_PADDING_X_FIELD;
        if (field != null) {
            try {
                Object value = field.get(null);
                if (value instanceof Number number) {
                    return number.intValue();
                }
            } catch (Throwable ignored) {
            }
        }
        return 0;
    }

    private static int resolveTooltipOverhaulPaddingY() {
        ensureTooltipOverhaulPaddingFields();
        Field field = TO_PADDING_Y_FIELD;
        if (field != null) {
            try {
                Object value = field.get(null);
                if (value instanceof Number number) {
                    return number.intValue();
                }
            } catch (Throwable ignored) {
            }
        }
        return 0;
    }

    private static int resolveTooltipOverhaulExtraTextOffset(Object ctx, String typeName, String axisName) {
        if (ctx == null) return 0;
        ensureTooltipOverhaulExtraTextMethod();
        if (TO_GET_EXTRA_TEXT_POSITION == null || TO_TEXT_TYPE_CLASS == null || TO_TEXT_AXIS_CLASS == null
                || TO_TOOLTIP_CONTEXT_CLASS == null) {
            return 0;
        }
        try {
            @SuppressWarnings("unchecked")
            Object type = Enum.valueOf((Class<Enum>) TO_TEXT_TYPE_CLASS, typeName);
            @SuppressWarnings("unchecked")
            Object axis = Enum.valueOf((Class<Enum>) TO_TEXT_AXIS_CLASS, axisName);
            Object value = TO_GET_EXTRA_TEXT_POSITION.invoke(null, ctx, type, axis);
            if (value instanceof Number number) {
                return number.intValue();
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static int resolveTooltipOverhaulExtraTextOffset(Object ctx) {
        return resolveTooltipOverhaulExtraTextOffset(ctx, "DESCRIPTION", "X");
    }

    private static void ensureTooltipOverhaulPaddingFields() {
        if (TO_PADDING_LOOKED_UP) return;
        TO_PADDING_LOOKED_UP = true;
        try {
            Class<?> renderer = Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipRenderer");
            try {
                TO_PADDING_X_FIELD = renderer.getField("PADDING_X");
            } catch (Throwable ignored) {
                TO_PADDING_X_FIELD = null;
            }
            try {
                TO_PADDING_Y_FIELD = renderer.getField("PADDING_Y");
            } catch (Throwable ignored) {
                TO_PADDING_Y_FIELD = null;
            }
        } catch (Throwable ignored) {
            TO_PADDING_X_FIELD = null;
            TO_PADDING_Y_FIELD = null;
        }
    }

    private static void ensureTooltipOverhaulExtraTextMethod() {
        if (TO_EXTRA_TEXT_LOOKED_UP) return;
        TO_EXTRA_TEXT_LOOKED_UP = true;
        try {
            Class<?> util = Class.forName("dev.xylonity.tooltipoverhaul.util.Util");
            TO_TEXT_TYPE_CLASS = Class.forName("dev.xylonity.tooltipoverhaul.util.TextType");
            TO_TEXT_AXIS_CLASS = Class.forName("dev.xylonity.tooltipoverhaul.util.TextAxis");
            TO_TOOLTIP_CONTEXT_CLASS = Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipContext");
            TO_GET_EXTRA_TEXT_POSITION = util.getMethod("getExtraTextPosition",
                    TO_TOOLTIP_CONTEXT_CLASS, TO_TEXT_TYPE_CLASS, TO_TEXT_AXIS_CLASS);
        } catch (Throwable ignored) {
            TO_GET_EXTRA_TEXT_POSITION = null;
            TO_TEXT_TYPE_CLASS = null;
            TO_TEXT_AXIS_CLASS = null;
            TO_TOOLTIP_CONTEXT_CLASS = null;
        }
    }

    private static int findTextWidthForLineY(List<Component> textLines, Font font, int bgY, int lineY) {
        if (textLines == null || textLines.isEmpty()) return -1;
        int bestIndex = 0;
        float bestDist = Float.MAX_VALUE;
        for (int i = 0; i < textLines.size(); i++) {
            float y = textTopYForIndexText(bgY, i, font.lineHeight);
            float dist = Math.abs(y - lineY);
            if (dist < bestDist) {
                bestDist = dist;
                bestIndex = i;
            }
        }
        return font.width(textLines.get(bestIndex));
    }

    private static LineMetrics findDurabilityLineMetrics(List<ClientTooltipComponent> components,
                                                         List<Component> textLines,
                                                         Font font,
                                                         int bgY,
                                                         Object ctx) {
        if (components != null && !components.isEmpty()) {
            int matchIndex = -1;
            float matchTop = 0.0f;
            for (int i = 0; i < components.size(); i++) {
                String line = getTooltipString(components.get(i));
                if (line == null || line.isEmpty()) continue;
                if (line.contains("/")) {
                    matchIndex = i;
                }
            }
            if (matchIndex >= 0) {
                int targetY = computeTooltipOverhaulLineTopY(ctx, components, matchIndex, bgY);
                int width = 0;
                for (int i = 0; i < components.size(); i++) {
                    String line = getTooltipString(components.get(i));
                    if (line == null || line.isEmpty()) continue;
                    int top = computeTooltipOverhaulLineTopY(ctx, components, i, bgY);
                    if (Math.abs(top - targetY) <= 1) {
                        width += components.get(i).getWidth(font);
                    }
                }
                if (width > 0) {
                    return new LineMetrics(width, targetY);
                }
            }
        }

        if (textLines != null && !textLines.isEmpty()) {
            for (int i = 0; i < textLines.size(); i++) {
                Component line = textLines.get(i);
                if (line == null) continue;
                if (line.getString().contains("/")) {
                    int topY = Math.round(textTopYForIndexText(bgY, i, font.lineHeight));
                    int width = font.width(line);
                    if (width > 0) {
                        return new LineMetrics(width, topY);
                    }
                }
            }
        }
        return null;
    }

    private static final class LineMetrics {
        private final int width;
        private final int topY;

        private LineMetrics(int width, int topY) {
            this.width = width;
            this.topY = topY;
        }
    }

    private static int computeTooltipOverhaulLineTopY(Object ctx,
                                                      List<ClientTooltipComponent> components,
                                                      int targetIndex,
                                                      int bgY) {
        if (components == null || components.isEmpty()) return bgY;
        if (targetIndex < 0) return bgY;

        ItemStack stack = getItemStack(ctx);
        boolean hasStack = stack != null && !stack.isEmpty();
        boolean showRating = resolveTooltipOverhaulShowRating(stack);
        boolean disableIcon = resolveTooltipOverhaulDisableIcon(stack);
        boolean disableDivider = resolveTooltipOverhaulDisableDivider(ctx);

        int paddingY = resolveTooltipOverhaulPaddingY();
        int extraTitleY = resolveTooltipOverhaulExtraTextOffset(ctx, "TITLE", "Y");
        int extraDescY = resolveTooltipOverhaulExtraTextOffset(ctx, "DESCRIPTION", "Y");

        int y = bgY + paddingY + 3 + (!showRating && hasStack ? 6 : 0) + extraTitleY;
        int size = components.size();

        for (int i = 0; i < size; i++) {
            if (i == 1) {
                y += (hasStack ? 3 : 0)
                        - (!showRating && hasStack ? 6 : 0)
                        - extraTitleY
                        + extraDescY;
                if (hasStack || disableIcon) {
                    y += 12;
                }
                if (disableDivider) {
                    y -= 6;
                }
            }

            if (i == targetIndex) {
                return y;
            }

            y += components.get(i).getHeight();
            if (hasStack && i == 0 && size > 1) {
                y += 6;
            }
        }

        return y;
    }

    private static boolean resolveTooltipOverhaulShowRating(ItemStack stack) {
        if (stack == null) return true;
        try {
            Class<?> util = Class.forName("dev.xylonity.tooltipoverhaul.util.Util");
            Method method = util.getMethod("shouldShowRating", ItemStack.class);
            Object value = method.invoke(null, stack);
            if (value instanceof Boolean bool) {
                return bool;
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    private static boolean resolveTooltipOverhaulDisableIcon(ItemStack stack) {
        if (stack == null) return false;
        try {
            Class<?> util = Class.forName("dev.xylonity.tooltipoverhaul.util.Util");
            Method method = util.getMethod("shouldDisableIcon", ItemStack.class);
            Object value = method.invoke(null, stack);
            if (value instanceof Boolean bool) {
                return bool;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean resolveTooltipOverhaulDisableDivider(Object ctx) {
        if (ctx == null) return false;
        try {
            Class<?> util = Class.forName("dev.xylonity.tooltipoverhaul.util.Util");
            Method method = util.getMethod("shouldDisableDividerLine",
                    Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipContext"));
            Object value = method.invoke(null, ctx);
            if (value instanceof Boolean bool) {
                return bool;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void drawSetBonusCrest(GuiGraphics gg, int x, int y, int width, int height, boolean active) {
        int renderW = Math.max(1, Math.round(width * SET_BONUS_CREST_SCALE));
        int renderH = Math.max(1, Math.round(height * SET_BONUS_CREST_SCALE));
        int offsetX = (width - renderW) / 2;
        int offsetY = (height - renderH) / 2;
        int drawX = x + offsetX;
        int drawY = y + offsetY;

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        gg.pose().pushPose();
        gg.pose().translate(drawX + (renderW / 2.0f), drawY + (renderH / 2.0f), 0.0f);
        gg.pose().scale(SET_BONUS_CREST_SCALE, SET_BONUS_CREST_SCALE, 1.0f);
        gg.pose().translate(-(SET_BONUS_CREST_TEX_W / 2.0f), -(SET_BONUS_CREST_TEX_H / 2.0f), 0.0f);
        gg.blit(active ? SET_BONUS_CREST_ACTIVE : SET_BONUS_CREST, 0, 0, 0, 0, SET_BONUS_CREST_TEX_W, SET_BONUS_CREST_TEX_H, SET_BONUS_CREST_TEX_W, SET_BONUS_CREST_TEX_H);
        gg.pose().popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static Component buildSetBonusLabel(ItemStack stack, String tierId) {
        if (tierId == null || tierId.isEmpty()) return null;
        if (!(stack.getItem() instanceof ArmorItem armor)) return null;
        if (!ForgeTierifyConfig.enableArmorSetBonuses()) return null;

        Player player = Minecraft.getInstance().player;
        if (player == null) return null;

        EquipmentSlot slot = armor.getEquipmentSlot();
        if (!isEquippedArmorStack(player, slot, stack)) return null;

        if (hasPerfectSetBonus(player, tierId)) {
            int pct = Math.round(ForgeTierifyConfig.armorSetPerfectBonusPercent() * 100.0f);
            return Component.literal("Perfect Set Bonus (+" + pct + "%)")
                    .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD);
        }

        if (hasSetBonus(player, tierId)) {
            int pct = Math.round(ForgeTierifyConfig.armorSetBonusMultiplier() * 100.0f);
            return Component.literal("Set Bonus (+" + pct + "%)")
                    .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD);
        }

        return null;
    }

    private static boolean isEquippedArmorStack(Player player, EquipmentSlot slot, ItemStack stack) {
        if (player == null || slot == null || stack == null || stack.isEmpty()) return false;
        ItemStack equipped = player.getItemBySlot(slot);
        if (equipped.isEmpty()) return false;
        if (equipped == stack) return true;
        UUID equippedTierUuid = getTierUuid(equipped);
        UUID stackTierUuid = getTierUuid(stack);
        return equippedTierUuid != null && equippedTierUuid.equals(stackTierUuid);
    }

    private static UUID getTierUuid(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null || !tiered.hasUUID("TierUUID")) return null;
        return tiered.getUUID("TierUUID");
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

    @SuppressWarnings("unchecked")
    private static List<ClientTooltipComponent> findTooltipComponents(Object ctx) {
        Object list = callNoArg(ctx, "components", "getComponents", "tooltipComponents");
        if (list instanceof List<?> raw && !raw.isEmpty() && raw.get(0) instanceof ClientTooltipComponent) {
            return (List<ClientTooltipComponent>) raw;
        }

        if (ctx == null) return null;
        for (Field f : ctx.getClass().getDeclaredFields()) {
            if (!List.class.isAssignableFrom(f.getType())) continue;
            try {
                f.setAccessible(true);
                Object value = f.get(ctx);
                if (value instanceof List<?> rawList && !rawList.isEmpty()
                        && rawList.get(0) instanceof ClientTooltipComponent) {
                    return (List<ClientTooltipComponent>) rawList;
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Component> findTooltipTextLines(Object ctx) {
        Object list = callNoArg(ctx, "text", "getText", "lines", "getLines");
        if (list instanceof List<?> raw && !raw.isEmpty() && raw.get(0) instanceof Component) {
            return (List<Component>) raw;
        }

        if (ctx == null) return null;
        for (Field f : ctx.getClass().getDeclaredFields()) {
            if (!List.class.isAssignableFrom(f.getType())) continue;
            try {
                f.setAccessible(true);
                Object value = f.get(ctx);
                if (value instanceof List<?> rawList && !rawList.isEmpty()
                        && rawList.get(0) instanceof Component) {
                    return (List<Component>) rawList;
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return null;
    }
}
