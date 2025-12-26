package elocindev.tierify.mixin.compat;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.attribute.EntityAttributeModifier;

/*
    Fixes Obscure API's attack-speed category icon mapping at the source.
    Uses vanilla-like attribute evaluation:
    d0 = base + sum(addition)
    d1 = d0 + d0 * sum(multiply_base)
    d1 *= product(1 + multiply_total)
 */
@Mixin(targets = "com.obscuria.obscureapi.client.TooltipBuilder$AttributeIcons", remap = false)
public class ObscureApiAttackSpeedIconMixin {

    @Inject(method = "getAttackSpeedIcon", at = @At("HEAD"), cancellable = true)
    private static void tierify$fixAttackSpeedIcon(Collection<?> modifier, CallbackInfoReturnable<String> cir) {
        if (modifier == null || modifier.isEmpty()) return;

        double speed = computeAttackSpeedVanillaLike(modifier);

        final String iconEnumName;
        if (speed >= 3.0) iconEnumName = "ATTACK_SPEED_VERY_FAST";
        else if (speed >= 2.0) iconEnumName = "ATTACK_SPEED_FAST";
        else if (speed >= 1.0) iconEnumName = "ATTACK_SPEED_MEDIUM";
        else if (speed > 0.6) iconEnumName = "ATTACK_SPEED_SLOW";
        else iconEnumName = "ATTACK_SPEED_VERY_SLOW";

        String icon = getObscureIcon(iconEnumName);
        if (icon != null && !icon.isEmpty()) {
            cir.setReturnValue(icon + " ");
        }
        // If icon lookup fails, fall through to Obscure API's original logic.
    }

    private static double computeAttackSpeedVanillaLike(Collection<?> mods) {
        final double base = 4.0;

        double add = 0.0;
        double multBase = 0.0;
        double multTotal = 1.0;

        for (Object o : mods) {
            if (!(o instanceof EntityAttributeModifier m)) continue;

            double v = m.getValue();
            switch (m.getOperation()) {
                case ADDITION -> add += v;
                case MULTIPLY_BASE -> multBase += v;
                case MULTIPLY_TOTAL -> multTotal *= (1.0 + v);
            }
        }

        double d0 = base + add;
        double d1 = d0 + (d0 * multBase);
        d1 *= multTotal;
        return d1;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String getObscureIcon(String enumConstantName) {
        try {
            Class<?> icons = Class.forName("com.obscuria.obscureapi.api.utils.Icons");
            Object constant = Enum.valueOf((Class<? extends Enum>) icons, enumConstantName);
            return (String) icons.getMethod("get").invoke(constant);
        } catch (Throwable t) {
            return null;
        }
    }
}
