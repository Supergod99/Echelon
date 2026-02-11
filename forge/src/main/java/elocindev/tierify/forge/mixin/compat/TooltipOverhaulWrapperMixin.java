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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

@Mixin(value = TooltipWrapper.class, remap = false)
public class TooltipOverhaulWrapperMixin {

    @Inject(method = "wrap", at = @At("RETURN"), cancellable = true, remap = false)
    private static void tierify$afterWrap(Font font,
                                          List<ClientTooltipComponent> orig,
                                          int screenWidth,
                                          ItemStack stack,
                                          CallbackInfoReturnable<List<ClientTooltipComponent>> cir) {
        List<ClientTooltipComponent> patched = patchWrappedResult(stack, cir.getReturnValue());
        if (patched != null) {
            cir.setReturnValue(patched);
        }
    }

    @Inject(method = "wrapHalf", at = @At("RETURN"), cancellable = true, remap = false)
    private static void tierify$afterWrapHalf(Font font,
                                              List<ClientTooltipComponent> orig,
                                              int screenWidth,
                                              ItemStack stack,
                                              CallbackInfoReturnable<List<ClientTooltipComponent>> cir) {
        List<ClientTooltipComponent> patched = patchWrappedResult(stack, cir.getReturnValue());
        if (patched != null) {
            cir.setReturnValue(patched);
        }
    }

    private static List<ClientTooltipComponent> patchWrappedResult(ItemStack stack,
                                                                   List<ClientTooltipComponent> wrapped) {
        if (!ForgeTierifyConfig.tieredTooltip()) return null;
        if (stack == null || stack.isEmpty()) return null;
        if (wrapped == null || wrapped.isEmpty()) return null;

        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return null;

        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if ((tierId == null || tierId.isEmpty()) && !tiered.getBoolean("Perfect")) return null;

        return replaceApexEffectLine(wrapped);
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
}
