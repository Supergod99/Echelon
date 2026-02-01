package elocindev.tierify.forge.mixin.compat;

import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@Pseudo
@Mixin(targets = "dev.xylonity.tooltipoverhaul.client.style.text.DefaultText", remap = false)
public class TooltipOverhaulTitleAlignmentMixin {

    @Unique private static int tierify$titleLineCount;
    @Unique private static int tierify$titleBaseX;
    @Unique private static int tierify$titleOffset;
    @Unique private static Point tierify$titleSize;
    @Unique private static Object tierify$titleCtx;
    @Unique private static boolean tierify$applyTitleCentering;
    @Unique private static int tierify$titleTargetY;
    @Unique private static boolean tierify$titleDeltaSet;
    @Unique private static int tierify$titleDeltaY;

    @Unique
    private static final class TierifyCenteredTitleComponent implements ClientTooltipComponent {
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
        public void renderText(Font font, int x, int y, org.joml.Matrix4f matrix, net.minecraft.client.renderer.MultiBufferSource.BufferSource buffer) {
            int drawX = x;
            int drawY = y;
            if (tierify$applyTitleCentering) {
                Integer centeredX = callTitleAlignmentX(tierify$titleBaseX, tierify$titleOffset, tierify$titleSize, delegate, font, tierify$titleCtx);
                if (centeredX != null) {
                    drawX = centeredX;
                }
                if (tierify$titleTargetY != Integer.MIN_VALUE) {
                    if (!tierify$titleDeltaSet && firstLine) {
                        tierify$titleDeltaY = tierify$titleTargetY - y;
                        tierify$titleDeltaSet = true;
                    }
                    drawY = y + tierify$titleDeltaY;
                }
            }
            delegate.renderText(font, drawX, drawY, matrix, buffer);
        }

        @Override
        public void renderImage(Font font, int x, int y, net.minecraft.client.gui.GuiGraphics graphics) {
            delegate.renderImage(font, x, y, graphics);
        }
    }

    @Inject(method = "lambda$render$0", at = @At("HEAD"), remap = false)
    private static void tierify$prepareTitleAlignment(@Coerce Object ctx,
                                                      @Coerce Object depth,
                                                      Component title,
                                                      Vec2 pos,
                                                      Point size,
                                                      Font font,
                                                      CallbackInfo ci) {
        tierify$titleLineCount = 1;
        tierify$titleBaseX = 0;
        tierify$titleOffset = 0;
        tierify$titleSize = size;
        tierify$titleCtx = ctx;
        tierify$applyTitleCentering = false;
        tierify$titleTargetY = Integer.MIN_VALUE;
        tierify$titleDeltaSet = false;
        tierify$titleDeltaY = 0;

        if (!ForgeTierifyConfig.tieredTooltip()) return;

        ItemStack stack = getStack(ctx);
        if (stack == null || stack.isEmpty()) return;

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return;
        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if ((tierId == null || tierId.isEmpty()) && !tiered.getBoolean("Perfect")) return;

        int posX = (int) pos.x;

        int paddingX = readStaticInt("dev.xylonity.tooltipoverhaul.client.TooltipRenderer", "PADDING_X", 4);
        int paddingY = readStaticInt("dev.xylonity.tooltipoverhaul.client.TooltipRenderer", "PADDING_Y", 4);
        boolean hasIcon = !stack.isEmpty();
        boolean shouldShowRating = callStaticBoolean(
                "dev.xylonity.tooltipoverhaul.util.Util",
                "shouldShowRating",
                new Class<?>[]{ItemStack.class},
                new Object[]{stack},
                false
        );
        boolean disableIcon = callStaticBoolean(
                "dev.xylonity.tooltipoverhaul.util.Util",
                "shouldDisableIcon",
                new Class<?>[]{ItemStack.class},
                new Object[]{stack},
                false
        );

        int firstLineOffset = paddingX + (hasIcon ? 26 : 0) - (disableIcon ? 26 : 0);
        int extraX = callExtraTextPosition(ctx, "TITLE", "X");
        int extraY = callExtraTextPosition(ctx, "TITLE", "Y");
        int baseX = posX + extraX - (hasIcon ? 0 : 1);

        int available = Math.max(1, size.x - paddingX - firstLineOffset);
        if (title != null) {
            tierify$titleLineCount = Math.max(1, font.split(title, available).size());
        }

        tierify$titleBaseX = baseX;
        tierify$titleOffset = firstLineOffset;
        int posY = (int) pos.y;
        int ratingOffset = (!shouldShowRating && hasIcon) ? 6 : 0;
        tierify$titleTargetY = posY + paddingY + 3 + ratingOffset + extraY;
        tierify$applyTitleCentering = true;

        List<?> components = readComponents(ctx);
        if (components != null && !components.isEmpty()) {
            int count = Math.min(tierify$titleLineCount, components.size());
            @SuppressWarnings("unchecked")
            List<Object> mutable = (List<Object>) components;
            for (int i = 0; i < count; i++) {
                Object obj = mutable.get(i);
                if (!(obj instanceof ClientTooltipComponent component)) continue;
                if (component instanceof TierifyCenteredTitleComponent) continue;
                try {
                    mutable.set(i, new TierifyCenteredTitleComponent(component, i == 0));
                } catch (UnsupportedOperationException ignored) {
                    break;
                }
            }
        }
    }

    @Unique
    private static ItemStack getStack(Object ctx) {
        Object stack = callNoArg(ctx, "stack", "getStack");
        if (stack instanceof ItemStack itemStack) return itemStack;
        stack = readField(ctx, "stack");
        return (stack instanceof ItemStack itemStack) ? itemStack : null;
    }

    @Unique
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

    @Unique
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

    @Unique
    private static int readStaticInt(String className, String fieldName, int fallback) {
        try {
            Class<?> cls = Class.forName(className);
            Field f = cls.getField(fieldName);
            Object value = f.get(null);
            if (value instanceof Number number) return number.intValue();
        } catch (Throwable ignored) {
        }
        return fallback;
    }

    @Unique
    private static boolean callStaticBoolean(String className, String methodName, Class<?>[] argTypes, Object[] args, boolean fallback) {
        try {
            Class<?> cls = Class.forName(className);
            Method m = cls.getMethod(methodName, argTypes);
            Object out = m.invoke(null, args);
            if (out instanceof Boolean b) return b;
        } catch (Throwable ignored) {
        }
        return fallback;
    }

    @Unique
    private static int callExtraTextPosition(Object ctx, String typeName, String axisName) {
        try {
            Class<?> utilClass = Class.forName("dev.xylonity.tooltipoverhaul.util.Util");
            Class<?> typeEnum = Class.forName("dev.xylonity.tooltipoverhaul.util.TextType");
            Class<?> axisEnum = Class.forName("dev.xylonity.tooltipoverhaul.util.TextAxis");
            @SuppressWarnings("unchecked")
            Object type = Enum.valueOf((Class<Enum>) typeEnum, typeName);
            @SuppressWarnings("unchecked")
            Object axis = Enum.valueOf((Class<Enum>) axisEnum, axisName);
            Method m = utilClass.getMethod("getExtraTextPosition", Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipContext"), typeEnum, axisEnum);
            Object out = m.invoke(null, ctx, type, axis);
            if (out instanceof Number number) return number.intValue();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private static List<?> readComponents(Object ctx) {
        Object out = callNoArg(ctx, "getComponents", "components");
        if (out instanceof List<?>) return (List<?>) out;
        out = readField(ctx, "components");
        return (out instanceof List<?>) ? (List<?>) out : null;
    }

    @Unique
    private static Integer callTitleAlignmentX(int posx, int offset, Point size, ClientTooltipComponent component, Font font, Object ctx) {
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
}
