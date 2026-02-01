package elocindev.tierify.forge.mixin.compat;

import dev.xylonity.tooltipoverhaul.client.wrap.TooltipWrapper;
import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.compat.ApexEffectTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

@Mixin(value = TooltipWrapper.class, remap = false)
public class TooltipOverhaulWrapperMixin {

    @Inject(method = "wrap", at = @At("HEAD"), cancellable = true, remap = false)
    private static void tierify$skipWrap(Font font,
                                         List<ClientTooltipComponent> orig,
                                         int screenWidth,
                                         ItemStack stack,
                                         CallbackInfoReturnable<List<ClientTooltipComponent>> cir) {
        List<ClientTooltipComponent> wrapped = wrapPreservingTitle(font, stack, orig, screenWidth, false);
        if (wrapped != null) {
            cir.setReturnValue(wrapped);
        }
    }

    @Inject(method = "wrapHalf", at = @At("HEAD"), cancellable = true, remap = false)
    private static void tierify$skipWrapHalf(Font font,
                                             List<ClientTooltipComponent> orig,
                                             int screenWidth,
                                             ItemStack stack,
                                             CallbackInfoReturnable<List<ClientTooltipComponent>> cir) {
        List<ClientTooltipComponent> wrapped = wrapPreservingTitle(font, stack, orig, screenWidth, true);
        if (wrapped != null) {
            cir.setReturnValue(wrapped);
        }
    }

    private static List<ClientTooltipComponent> wrapPreservingTitle(Font font,
                                                                    ItemStack stack,
                                                                    List<ClientTooltipComponent> orig,
                                                                    int screenWidth,
                                                                    boolean halfScreen) {
        if (!ForgeTierifyConfig.tieredTooltip()) return null;
        if (stack == null || stack.isEmpty()) return null;
        if (orig == null || orig.isEmpty()) return null;

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return null;

        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if ((tierId == null || tierId.isEmpty()) && !tiered.getBoolean("Perfect")) return null;

        int paddingX = resolveTooltipOverhaulPaddingX();
        int basePadding = paddingX * 2 + 4;
        int iconPadding = stack.isEmpty() ? 0 : 26;
        int maxAllowed = Math.max(60, (halfScreen ? screenWidth / 2 - 8 : (int) (screenWidth * 0.75F)) - basePadding - iconPadding);

        for (ClientTooltipComponent component : orig) {
            if (component.getWidth(font) > maxAllowed) {
                List<ClientTooltipComponent> wrapped = wrapTailPreserveTitle(font, orig, screenWidth, stack, halfScreen);
                return replaceApexEffectLine(wrapped);
            }
        }

        return replaceApexEffectLine(orig);
    }

    private static int resolveTooltipOverhaulPaddingX() {
        try {
            Class<?> renderer = Class.forName("dev.xylonity.tooltipoverhaul.client.TooltipRenderer");
            java.lang.reflect.Field field = renderer.getField("PADDING_X");
            Object value = field.get(null);
            if (value instanceof Number number) {
                return number.intValue();
            }
        } catch (Throwable ignored) {
        }
        return 4;
    }

    private static List<ClientTooltipComponent> wrapTailPreserveTitle(Font font,
                                                                      List<ClientTooltipComponent> orig,
                                                                      int screenWidth,
                                                                      ItemStack stack,
                                                                      boolean halfScreen) {
        if (orig.size() <= 1) {
            return orig;
        }

        List<ClientTooltipComponent> tail = tierify$wrapInternal(font, orig.subList(1, orig.size()), screenWidth, stack, halfScreen);
        if (tail == null || tail.isEmpty()) {
            return orig;
        }

        List<ClientTooltipComponent> out = new ArrayList<>(1 + tail.size());
        out.add(orig.get(0));
        out.addAll(tail);
        return replaceApexEffectLine(out);
    }

    private static final String APEX_EFFECT_LABEL = "Apex Effect";
 

    private static List<ClientTooltipComponent> replaceApexEffectLine(List<ClientTooltipComponent> components) {
        if (components == null || components.isEmpty()) return components;

        List<ClientTooltipComponent> out = null;

        for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent c = components.get(i);
            if (isApexEffectLine(c)) {
                if (out == null) out = new ArrayList<>(components);
                out.set(i, new ApexEffectTooltipComponent());
                break; // only one line expected
            }
        }

        return out != null ? out : components;
    }

    private static boolean isApexEffectLine(ClientTooltipComponent component) {
        String text = extractText(component);
        if (text == null) return false;
        text = text.replaceAll("\\s+", " ").trim();
        return text.equals(APEX_EFFECT_LABEL);
    }

    private static String extractText(Object component) {
        if (component == null) return null;

        for (Class<?> type = component.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field f : type.getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    Object v = f.get(component);
                    if (v == null) continue;

                    // Direct FormattedCharSequence
                    if (v instanceof FormattedCharSequence seq) {
                        return flatten(seq);
                    }

                    // Direct Component
                    if (v instanceof net.minecraft.network.chat.Component c) {
                        String s = c.getString();
                        if (s != null && !s.isEmpty()) return s;
                    }

                    // List of FormattedCharSequence
                    if (v instanceof java.util.List<?> list) {
                        StringBuilder sb = new StringBuilder();
                        boolean any = false;
                        for (Object e : list) {
                            if (e instanceof FormattedCharSequence s) {
                                sb.append(flatten(s));
                                any = true;
                            }
                        }
                        if (any) return sb.toString();
                    }
                } catch (Throwable ignored) {}
            }
        }

        return null;
    }

    private static String flatten(FormattedCharSequence seq) {
        StringBuilder out = new StringBuilder();
        seq.accept((idx, style, codePoint) -> {
            out.appendCodePoint(codePoint);
            return true;
        });
        return out.toString();
    }


    @Invoker(value = "wrapInternal", remap = false)
    private static List<ClientTooltipComponent> tierify$wrapInternal(Font font,
                                                                     List<ClientTooltipComponent> orig,
                                                                     int screenWidth,
                                                                     ItemStack stack,
                                                                     boolean halfScreen) {
        throw new AssertionError("Invoker should be patched by Mixin.");
    }
}
