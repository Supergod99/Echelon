package elocindev.tierify.forge.mixin.compat;

import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(targets = "com.obscuria.obscureapi.client.TooltipBuilder$AttributeIcons", remap = false)
public class ObscureApiAttributeIconMathMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger("tiered");
    @Unique
    private static final boolean DEBUG_OBSCURE_SETBONUS =
            Boolean.parseBoolean(System.getProperty("tierify.debug.obscure_setbonus", "false"));

    @Unique
    private static final UUID TIERIFY_SET_BONUS_ID =
            UUID.fromString("98765432-1234-1234-1234-987654321012");

    @Unique
    private static final ThreadLocal<ItemStack> CURRENT_STACK = new ThreadLocal<>();

    @Unique
    private static final DecimalFormat FORMAT = new DecimalFormat("##.#");

    @Unique
    private static String ICON_ARMOR;
    @Unique
    private static String ICON_TOUGHNESS;
    @Unique
    private static String ICON_KNOCKBACK;

    @Inject(method = "putIcons", at = @At("HEAD"))
    private static void tierify$captureStack(List<?> list, @Coerce Object stackObj, CallbackInfo ci) {
        if (stackObj instanceof ItemStack stack) {
            CURRENT_STACK.set(stack);
        } else {
            CURRENT_STACK.remove();
        }
    }

    @Inject(method = "putIcons", at = @At("RETURN"))
    private static void tierify$clearStack(List<?> list, @Coerce Object stackObj, CallbackInfo ci) {
        CURRENT_STACK.remove();
    }

    @Inject(method = "getIcon", at = @At("RETURN"), cancellable = true)
    private static void tierify$fixIconMathAndApplySetBonus(boolean isPercent,
                                                            String icon,
                                                            double base,
                                                            Collection<?> modifiers,
                                                            CallbackInfoReturnable<String> cir) {
        if (icon == null || modifiers == null || modifiers.isEmpty()) return;
        String original = cir.getReturnValue();

        double[] sums = sumModifiers(modifiers);
        double add = sums[0];
        double multBase = sums[1];
        double multTotal = sums[2];

        double[] delta = computeSetBonusDelta(icon, modifiers);
        if (delta != null) {
            add += delta[0];
            multBase += delta[1];
            multTotal *= delta[2];
        }

        double value = computeVanillaLikeValue(base, add, multBase, multTotal);
        if (Math.abs(value) < 1.0e-9) {
            if (original != null && !original.isEmpty()) {
                return;
            }
            cir.setReturnValue("");
            return;
        }

        String green = (multBase > 0.0) ? "\u00A72" : "";
        cir.setReturnValue(render(icon, green, value, isPercent));
    }

    @Unique
    private static String render(String icon, String green, double value, boolean percent) {
        double shown = percent ? (value * 100.0) : value;
        String formatted = FORMAT.format(shown).replace(".0", "");
        return icon + green + formatted + (percent ? "% " : " ");
    }

    @Unique
    private static double computeVanillaLikeValue(double base, double add, double multBase, double multTotal) {
        double d0 = base + add;
        double d1 = d0 + (d0 * multBase);
        return d1 * multTotal;
    }

    @Unique
    private static double[] sumModifiers(Collection<?> modifiers) {
        double add = 0.0;
        double multBase = 0.0;
        double multTotal = 1.0;

        for (Object o : modifiers) {
            if (o == null) continue;

            double amount = readModifierAmount(o);
            int op = readModifierOperationOrdinal(o);

            switch (op) {
                case 0 -> add += amount;
                case 1 -> multBase += amount;
                case 2 -> multTotal *= (1.0 + amount);
                default -> {
                }
            }
        }

        return new double[] { add, multBase, multTotal };
    }

    @Unique
    private static double readModifierAmount(Object mod) {
        Double v = (Double) invokeFirst(mod, "getValue", "getAmount", "m_22218_");
        return v != null ? v : 0.0;
    }

    @Unique
    private static int readModifierOperationOrdinal(Object mod) {
        Object op = invokeFirst(mod, "getOperation", "m_22217_", "m_22219_");
        if (op instanceof Enum<?> e) return e.ordinal();
        return 0;
    }

    @Unique
    private static boolean isTieredModifier(Object mod) {
        Object name = invokeFirst(mod, "getName", "m_22220_");
        if (!(name instanceof String s)) return false;
        return s.contains("tiered:");
    }

    @Unique
    private static Object invokeFirst(Object target, String... methodNames) {
        for (String name : methodNames) {
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
    private static void ensureIconsResolved() {
        if (ICON_ARMOR != null) return;
        ICON_ARMOR = resolveObscureIcon("ARMOR");
        ICON_TOUGHNESS = resolveObscureIcon("ARMOR_TOUGHNESS");
        ICON_KNOCKBACK = resolveObscureIcon("KNOCKBACK_RESISTANCE");
    }

    @Unique
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String resolveObscureIcon(String enumName) {
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
    private static double[] computeSetBonusDelta(String icon, Collection<?> modifiers) {
        if (!ForgeTierifyConfig.enableArmorSetBonuses()) {
            debugSetBonus("skip set bonus icon patch: armor set bonuses disabled");
            return null;
        }
        if (modifiers == null || modifiers.isEmpty()) {
            debugSetBonus("skip set bonus icon patch: no modifiers icon='{}'", icon);
            return null;
        }

        ItemStack hovered = CURRENT_STACK.get();
        if (hovered == null || hovered.isEmpty()) {
            debugSetBonus("skip set bonus icon patch: no hovered stack icon='{}'", icon);
            return null;
        }
        if (!(hovered.getItem() instanceof ArmorItem armor)) {
            debugSetBonus("skip set bonus icon patch: hovered stack is not armor icon='{}' item='{}'",
                    icon, hovered.getDescriptionId());
            return null;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            debugSetBonus("skip set bonus icon patch: no player icon='{}'", icon);
            return null;
        }

        ItemStack equippedSameSlot = player.getItemBySlot(armor.getEquipmentSlot());
        if (equippedSameSlot == null || equippedSameSlot.isEmpty()) {
            debugSetBonus("skip set bonus icon patch: empty equipped slot='{}' icon='{}'",
                    armor.getEquipmentSlot().getName(), icon);
            return null;
        }
        String hoveredTier = getTierId(hovered);
        if (hoveredTier.isEmpty()) {
            hoveredTier = getTierId(equippedSameSlot);
        }
        if (hoveredTier.isEmpty()) {
            debugSetBonus("skip set bonus icon patch: no tier id icon='{}' hovered='{}' equipped='{}'",
                    icon, hovered.getDescriptionId(), equippedSameSlot.getDescriptionId());
            return null;
        }
        String equippedTier = getTierId(equippedSameSlot);
        if (equippedTier.isEmpty() || !hoveredTier.equals(equippedTier)) {
            debugSetBonus("skip set bonus icon patch: tier mismatch icon='{}' hoveredTier='{}' equippedTier='{}'",
                    icon, hoveredTier, equippedTier);
            return null;
        }

        if (!hasFullTierSetEquipped(player, hoveredTier)) {
            debugSetBonus("skip set bonus icon patch: no full set icon='{}' tier='{}'", icon, hoveredTier);
            return null;
        }

        double pct = hasPerfectTierSetEquipped(player, hoveredTier)
                ? ForgeTierifyConfig.armorSetPerfectBonusPercent()
                : ForgeTierifyConfig.armorSetBonusMultiplier();
        if (pct <= 0.0) {
            debugSetBonus("skip set bonus icon patch: non-positive pct icon='{}' pct='{}'", icon, pct);
            return null;
        }

        ensureIconsResolved();

        if (iconEquals(icon, ICON_ARMOR)) {
            // ok
        } else if (iconEquals(icon, ICON_TOUGHNESS)) {
            // ok
        } else if (iconEquals(icon, ICON_KNOCKBACK)) {
            // ok
        } else {
            debugSetBonus("skip set bonus icon patch: unsupported icon marker='{}'", icon);
            return null;
        }
        Set<UUID> expectedTierUuids = expectedTierModifierUuidsForIcon(icon, hovered, equippedSameSlot);

        double add = 0.0;
        double multBase = 0.0;
        double multTotalFactor = 1.0;

        for (Object mod : modifiers) {
            if (mod == null) continue;
            if (!isTieredModifier(mod) && !matchesExpectedTieredModifier(mod, expectedTierUuids)) continue;

            double amount = readModifierAmount(mod);
            if (amount <= 0.0) continue;

            int op = readModifierOperationOrdinal(mod);
            switch (op) {
                case 0 -> add += (amount * pct);
                case 1 -> multBase += (amount * pct);
                case 2 -> multTotalFactor *= (1.0 + (amount * pct));
                default -> {
                }
            }
        }

        if (Math.abs(add) < 1.0e-9
                && Math.abs(multBase) < 1.0e-9
                && Math.abs(multTotalFactor - 1.0) < 1.0e-9) {
            double[] fromStack = computeSetBonusDeltaFromStack(icon, hovered, equippedSameSlot, pct);
            if (fromStack != null) {
                debugSetBonus("applied set bonus delta from hovered stack icon='{}' tier='{}' add='{}' multBase='{}' multTotal='{}'",
                        icon, hoveredTier, fromStack[0], fromStack[1], fromStack[2]);
                return fromStack;
            }
            debugSetBonus("computed zero set bonus delta icon='{}' tier='{}' expectedIds='{}'",
                    icon, hoveredTier, expectedTierUuids.size());
            return null;
        }

        debugSetBonus("applied set bonus delta icon='{}' tier='{}' add='{}' multBase='{}' multTotal='{}'",
                icon, hoveredTier, add, multBase, multTotalFactor);
        return new double[] { add, multBase, multTotalFactor };
    }

    @Unique
    private static double[] computeSetBonusDeltaFromStack(String icon, ItemStack hovered, ItemStack equipped, double pct) {
        if (hovered == null || hovered.isEmpty()) return null;
        if (!(hovered.getItem() instanceof ArmorItem armor)) return null;
        if (pct <= 0.0) return null;

        String attrId = attributeIdForIcon(icon);
        if (attrId == null || attrId.isEmpty()) return null;

        Set<UUID> expectedTierUuids = expectedTierModifierUuidsForIcon(icon, hovered, equipped);
        Set<String> attrIds = attributeIdVariants(attrId);
        EquipmentSlot slot = armor.getEquipmentSlot();

        double add = 0.0;
        double multBase = 0.0;
        double multTotalFactor = 1.0;

        for (String attrKey : attrIds) {
            ResourceLocation attrRl = ResourceLocation.tryParse(attrKey);
            if (attrRl == null) continue;
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(attrRl);
            if (attr == null) continue;

            Collection<AttributeModifier> mods = hovered.getAttributeModifiers(slot).get(attr);
            if (mods == null || mods.isEmpty()) continue;

            for (AttributeModifier mod : mods) {
                if (mod == null) continue;
                boolean tieredByName = mod.getName() != null && mod.getName().contains("tiered:");
                boolean tieredByUuid = expectedTierUuids.contains(mod.getId());
                if (!tieredByName && !tieredByUuid) continue;

                double amount = mod.getAmount();
                if (amount <= 0.0) continue;

                switch (mod.getOperation()) {
                    case ADDITION -> add += (amount * pct);
                    case MULTIPLY_BASE -> multBase += (amount * pct);
                    case MULTIPLY_TOTAL -> multTotalFactor *= (1.0 + (amount * pct));
                }
            }
        }

        if (Math.abs(add) < 1.0e-9
                && Math.abs(multBase) < 1.0e-9
                && Math.abs(multTotalFactor - 1.0) < 1.0e-9) {
            return null;
        }
        return new double[] { add, multBase, multTotalFactor };
    }

    @Unique
    private static void debugSetBonus(String msg, Object... args) {
        if (!DEBUG_OBSCURE_SETBONUS) return;
        LOGGER.info("[Tierify/ObscureSetBonusDebug] " + msg, args);
    }

    @Unique
    private static boolean matchesExpectedTieredModifier(Object mod, Set<UUID> expected) {
        if (expected == null || expected.isEmpty() || mod == null) return false;
        Object out = invokeFirst(mod, "getId", "m_19437_");
        return out instanceof UUID id && expected.contains(id);
    }

    @Unique
    private static Set<UUID> expectedTierModifierUuidsForIcon(String icon, ItemStack hovered, ItemStack equipped) {
        if (hovered == null || hovered.isEmpty()) return Set.of();
        if (!(hovered.getItem() instanceof ArmorItem armor)) return Set.of();
        if (icon == null || icon.isEmpty()) return Set.of();

        String attrId = attributeIdForIcon(icon);
        if (attrId == null) return Set.of();
        Set<String> attrIds = attributeIdVariants(attrId);

        Set<UUID> ids = new HashSet<>();
        for (String id : attrIds) {
            addExpectedUuids(ids, id, armor.getEquipmentSlot().getName(), hovered);
            addExpectedUuids(ids, id, "head", hovered);
            addExpectedUuids(ids, id, "chest", hovered);
            addExpectedUuids(ids, id, "legs", hovered);
            addExpectedUuids(ids, id, "feet", hovered);
            if (equipped != null && !equipped.isEmpty()) {
                addExpectedUuids(ids, id, armor.getEquipmentSlot().getName(), equipped);
                addExpectedUuids(ids, id, "head", equipped);
                addExpectedUuids(ids, id, "chest", equipped);
                addExpectedUuids(ids, id, "legs", equipped);
                addExpectedUuids(ids, id, "feet", equipped);
            }
        }
        return ids;
    }

    @Unique
    private static Set<String> attributeIdVariants(String attrId) {
        if (attrId == null || attrId.isEmpty()) return Set.of();
        Set<String> out = new HashSet<>();
        out.add(attrId);
        if (attrId.indexOf(':') < 0) {
            out.add("minecraft:" + attrId);
        } else {
            int split = attrId.indexOf(':');
            if (split >= 0 && split + 1 < attrId.length()) {
                out.add(attrId.substring(split + 1));
            }
        }
        return out;
    }

    @Unique
    private static UUID tierModifierUuid(String attrId, String slotName, String salt) {
        String key = attrId + "_" + slotName + "_" + salt;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    @Unique
    private static void addExpectedUuids(Set<UUID> ids, String attrId, String slotName, ItemStack stack) {
        if (ids == null || stack == null || stack.isEmpty()) return;
        UUID tierUuid = getTierUuid(stack);
        if (tierUuid != null) {
            ids.add(tierModifierUuid(attrId, slotName, tierUuid.toString()));
        }
        ids.add(tierModifierUuid(attrId, slotName, stack.getDescriptionId()));
    }

    @Unique
    private static String attributeIdForIcon(String icon) {
        if (iconEquals(icon, ICON_ARMOR)) return "generic.armor";
        if (iconEquals(icon, ICON_TOUGHNESS)) return "generic.armor_toughness";
        if (iconEquals(icon, ICON_KNOCKBACK)) return "generic.knockback_resistance";
        return null;
    }

    @Unique
    private static boolean iconEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return true;
        return stripFormatting(a).trim().equals(stripFormatting(b).trim());
    }

    @Unique
    private static String stripFormatting(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\u00A7' && i + 1 < s.length()) {
                i++;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }

    @Unique
    private static String getTierId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";
        CompoundTag nbt = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (nbt == null) return "";
        return nbt.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
    }

    @Unique
    private static UUID getTierUuid(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        CompoundTag nbt = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (nbt == null || !nbt.hasUUID("TierUUID")) return null;
        return nbt.getUUID("TierUUID");
    }

    @Unique
    private static boolean hasFullTierSetEquipped(Player player, String targetTier) {
        if (player == null || targetTier == null || targetTier.isEmpty()) return false;

        int matchCount = 0;
        for (ItemStack armorPiece : player.getInventory().armor) {
            if (armorPiece == null || armorPiece.isEmpty()) return false;
            if (targetTier.equals(getTierId(armorPiece))) matchCount++;
        }
        return matchCount >= 4;
    }

    @Unique
    private static boolean hasPerfectTierSetEquipped(Player player, String targetTier) {
        if (!hasFullTierSetEquipped(player, targetTier)) return false;

        for (ItemStack armorPiece : player.getInventory().armor) {
            if (armorPiece == null || armorPiece.isEmpty()) return false;
            CompoundTag nbt = armorPiece.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
            if (nbt == null) return false;
            if (!targetTier.equals(nbt.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY))) return false;
            if (!nbt.getBoolean("Perfect")) return false;
        }
        return true;
    }
}
