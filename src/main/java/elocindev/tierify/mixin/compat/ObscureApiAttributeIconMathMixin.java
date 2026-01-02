package elocindev.tierify.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import elocindev.tierify.Tierify;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(targets = "com.obscuria.obscureapi.client.TooltipBuilder$AttributeIcons", remap = false)
public class ObscureApiAttributeIconMathMixin {

    @Unique private static final UUID TIERIFY$SET_BONUS_ID =
            UUID.fromString("98765432-1234-1234-1234-987654321012");

    @Unique private static final ThreadLocal<ItemStack> TIERIFY$CURRENT_STACK = new ThreadLocal<>();

    // Match Obscure’s common formatting behavior (1 decimal max, strip trailing .0)
    @Unique private static final DecimalFormat TIERIFY$FMT = new DecimalFormat("##.#");

    @Unique private static String TIERIFY$ICON_ARMOR;
    @Unique private static String TIERIFY$ICON_TOUGHNESS;
    @Unique private static String TIERIFY$ICON_KNOCKBACK;

    @Inject(method = "putIcons", at = @At("HEAD"))
    private static void tierify$captureStack(List<?> list, @Coerce Object stackObj, CallbackInfo ci) {
        if (stackObj instanceof ItemStack stack) {
            TIERIFY$CURRENT_STACK.set(stack);
        } else {
            TIERIFY$CURRENT_STACK.remove();
        }
    }

    @Inject(method = "putIcons", at = @At("RETURN"))
    private static void tierify$clearStack(List<?> list, @Coerce Object stackObj, CallbackInfo ci) {
        TIERIFY$CURRENT_STACK.remove();
    }

    @ModifyReturnValue(method = "getIcon", at = @At("RETURN"))
    private static String tierify$fixIconMathAndApplySetBonus(
            String original,
            boolean isPercent,
            String icon,
            double base,
            Collection<?> modifiers
    ) {
        if (icon == null || modifiers == null || modifiers.isEmpty()) return original;

        double[] sums = tierify$sumModifiers(modifiers);
        double add = sums[0];
        double multBase = sums[1];
        double multTotal = sums[2];

        double[] delta = tierify$computeSetBonusDelta(icon);
        if (delta != null) {
            add += delta[0];
            multBase += delta[1];
            multTotal *= delta[2];
        }

        double value = tierify$computeVanillaLikeValue(base, add, multBase, multTotal);
        if (Math.abs(value) < 1.0e-9) return "";

        // Preserve Obscure’s “green when multiply_base positive” behavior
        String green = (multBase > 0.0) ? "§2" : "";

        return tierify$render(icon, green, value, isPercent);
    }

    @Unique
    private static String tierify$render(String icon, String green, double value, boolean percent) {
        double shown = percent ? (value * 100.0) : value;
        String formatted = TIERIFY$FMT.format(shown).replace(".0", "");
        return icon + green + formatted + (percent ? "% " : " ");
    }

    @Unique
    private static double tierify$computeVanillaLikeValue(double base, double add, double multBase, double multTotal) {
        double d0 = base + add;
        double d1 = d0 + (d0 * multBase);
        return d1 * multTotal;
    }

    @Unique
    private static double[] tierify$sumModifiers(Collection<?> modifiers) {
        double add = 0.0;
        double multBase = 0.0;
        double multTotal = 1.0;

        for (Object o : modifiers) {
            if (o == null) continue;

            double amount = tierify$readModifierAmount(o);
            int op = tierify$readModifierOperationOrdinal(o);

            switch (op) {
                case 0 -> add += amount;                 // ADDITION
                case 1 -> multBase += amount;            // MULTIPLY_BASE
                case 2 -> multTotal *= (1.0 + amount);   // MULTIPLY_TOTAL
                default -> { /* ignore */ }
            }
        }

        return new double[]{add, multBase, multTotal};
    }

    @Unique
    private static double tierify$readModifierAmount(Object mod) {
        Double v = (Double) tierify$invokeFirst(mod, "getValue", "getAmount", "m_22218_");
        return v != null ? v : 0.0;
    }

    @Unique
    private static int tierify$readModifierOperationOrdinal(Object mod) {
        Object op = tierify$invokeFirst(mod, "getOperation", "m_22217_", "m_22219_");
        if (op instanceof Enum<?> e) return e.ordinal();
        return 0;
    }

    @Unique
    private static Object tierify$invokeFirst(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                m.setAccessible(true);
                return m.invoke(target);
            } catch (Throwable ignored) {}
        }
        return null;
    }

    @Unique
    private static void tierify$ensureIconsResolved() {
        if (TIERIFY$ICON_ARMOR != null) return;
        TIERIFY$ICON_ARMOR = tierify$resolveObscureIcon("ARMOR");
        TIERIFY$ICON_TOUGHNESS = tierify$resolveObscureIcon("ARMOR_TOUGHNESS");
        TIERIFY$ICON_KNOCKBACK = tierify$resolveObscureIcon("KNOCKBACK_RESISTANCE");
    }

    @Unique
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String tierify$resolveObscureIcon(String enumName) {
        try {
            Class<?> icons = Class.forName("com.obscuria.obscureapi.api.utils.Icons");
            Object e = Enum.valueOf((Class<? extends Enum>) icons.asSubclass(Enum.class), enumName);
            Method get = icons.getMethod("get");
            Object out = get.invoke(e);
            return (out instanceof String s) ? s : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static double[] tierify$computeSetBonusDelta(String icon) {
        if (!Tierify.CONFIG.enableArmorSetBonuses) return null;

        ItemStack hovered = TIERIFY$CURRENT_STACK.get();
        if (hovered == null || hovered.isEmpty()) return null;
        if (!(hovered.getItem() instanceof ArmorItem armor)) return null;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return null;

        String hoveredTier = tierify$getTierId(hovered);
        if (hoveredTier.isEmpty()) return null;

        ItemStack equippedSameSlot = player.getEquippedStack(armor.getSlotType());
        if (equippedSameSlot == null || equippedSameSlot.isEmpty()) return null;
        if (!hoveredTier.equals(tierify$getTierId(equippedSameSlot))) return null;

        if (!tierify$hasFullTierSetEquipped(player, hoveredTier)) return null;

        tierify$ensureIconsResolved();

        String attributeId;
        if (tierify$iconEquals(icon, TIERIFY$ICON_ARMOR)) {
            attributeId = "minecraft:generic.armor";
        } else if (tierify$iconEquals(icon, TIERIFY$ICON_TOUGHNESS)) {
            attributeId = "minecraft:generic.armor_toughness";
        } else if (tierify$iconEquals(icon, TIERIFY$ICON_KNOCKBACK)) {
            attributeId = "minecraft:generic.knockback_resistance";
        } else {
            return null;
        }

        EntityAttribute attr = Registries.ATTRIBUTE.get(new Identifier(attributeId));
        EntityAttributeInstance inst = (attr != null) ? player.getAttributeInstance(attr) : null;
        if (inst == null) return null;

        EntityAttributeModifier bonus = inst.getModifier(TIERIFY$SET_BONUS_ID);
        if (bonus == null) return null;

        double totalAmount = bonus.getValue();
        if (totalAmount <= 0.0) return null;

        double add = 0.0;
        double multBase = 0.0;
        double multTotalFactor = 1.0;

        switch (bonus.getOperation()) {
            case ADDITION -> add += (totalAmount / 4.0);
            case MULTIPLY_BASE -> multBase += (totalAmount / 4.0);
            case MULTIPLY_TOTAL -> {
                double totalFactor = 1.0 + totalAmount;
                if (totalFactor <= 0.0) return null;
                multTotalFactor *= Math.pow(totalFactor, 0.25);
            }
        }

        if (Math.abs(add) < 1.0e-9 && Math.abs(multBase) < 1.0e-9 && Math.abs(multTotalFactor - 1.0) < 1.0e-9) {
            return null;
        }

        return new double[]{add, multBase, multTotalFactor};
    }

    @Unique
    private static boolean tierify$iconEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return true;
        return tierify$stripFormatting(a).trim().equals(tierify$stripFormatting(b).trim());
    }

    @Unique
    private static String tierify$stripFormatting(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '§' && i + 1 < s.length()) { i++; continue; }
            out.append(c);
        }
        return out.toString();
    }

    @Unique
    private static String tierify$getTierId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";
        NbtCompound nbt = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (nbt == null) return "";
        return nbt.getString(Tierify.NBT_SUBTAG_DATA_KEY);
    }

    @Unique
    private static boolean tierify$hasFullTierSetEquipped(PlayerEntity player, String targetTier) {
        if (player == null || targetTier == null || targetTier.isEmpty()) return false;

        int matchCount = 0;
        for (ItemStack armorPiece : player.getInventory().armor) {
            if (armorPiece == null || armorPiece.isEmpty()) return false;
            if (targetTier.equals(tierify$getTierId(armorPiece))) matchCount++;
        }
        return matchCount >= 4;
    }
}
