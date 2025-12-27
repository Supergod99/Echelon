package elocindev.tierify.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Collection;

@Mixin(targets = "com.obscuria.obscureapi.client.TooltipBuilder$AttributeIcons", remap = false)
public class ObscureApiAttributeIconMathMixin {

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private static void tierify$fixGetIcon(boolean percent, String icon, double base, Collection modifier,
                                          CallbackInfoReturnable<String> cir) {
        if (modifier == null || modifier.isEmpty()) {
            cir.setReturnValue("");
            return;
        }

        // out[0] = computed value, out[1] = multBase, out[2] = readAny(1 or 0)
        double[] out = computeVanillaLike(base, modifier);
        if (out[2] == 0.0) return; // fail-safe: if we couldn't read anything, don't override

        double value = out[0];
        double multBase = out[1];

        if (value == 0.0) {
            cir.setReturnValue("");
            return;
        }

        double shown = percent ? (value * 100.0) : value;
        String formatted = new DecimalFormat("##.#").format(shown).replace(".0", "");

        // Preserve Obscure’s “green when multiply_base positive” behavior
        String green = (multBase > 0.0) ? "§2" : "";

        cir.setReturnValue(icon + green + formatted + (percent ? "% " : " "));
    }

    private static double[] computeVanillaLike(double base, Collection mods) {
        double add = 0.0;
        double multBase = 0.0;
        double multTotal = 1.0;
        boolean readAny = false;

        for (Object m : mods) {
            if (m == null) continue;

            Double amount = readModifierAmount(m);
            String op = readModifierOperationName(m);
            if (amount == null || op == null) continue;

            readAny = true;

            switch (op) {
                case "ADDITION" -> add += amount;
                case "MULTIPLY_BASE" -> multBase += amount;
                case "MULTIPLY_TOTAL" -> multTotal *= (1.0 + amount);
                default -> { /* ignore */ }
            }
        }

        if (!readAny) return new double[] { 0.0, 0.0, 0.0 };

        double d0 = base + add;
        double d1 = d0 + (d0 * multBase);
        d1 *= multTotal;

        return new double[] { d1, multBase, 1.0 };
    }

    private static Double readModifierAmount(Object mod) {
        Double v = invokeDoubleNoArgs(mod, "getValue");
        if (v != null) return v;

        v = invokeDoubleNoArgs(mod, "getAmount");
        if (v != null) return v;

        // Mojmap/obf 1.20.1 
        v = invokeDoubleNoArgs(mod, "m_22218_");
        if (v != null) return v;

        return null;
    }

    private static String readModifierOperationName(Object mod) {
        Object op = invokeNoArgs(mod, "getOperation");
        if (op == null) op = invokeNoArgs(mod, "m_22217_");
        if (op == null) return null;

        if (op instanceof Enum<?> e) return e.name();

        try {
            Method name = op.getClass().getMethod("name");
            Object out = name.invoke(op);
            return (out instanceof String s) ? s : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object invokeNoArgs(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            m.setAccessible(true);
            return m.invoke(target);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Double invokeDoubleNoArgs(Object target, String methodName) {
        Object out = invokeNoArgs(target, methodName);
        return (out instanceof Number n) ? n.doubleValue() : null;
    }
}
