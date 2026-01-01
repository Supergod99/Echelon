package draylar.tiered.api;

import elocindev.tierify.Tierify;
import elocindev.tierify.util.SetBonusUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SetBonusLogic {

    private static final UUID SET_BONUS_ID = UUID.fromString("98765432-1234-1234-1234-987654321012");
    private static final String BONUS_NAME = "Tierify Set Bonus";

    // Stores ONLY the extra multiplier contributed by the set bonus (e.g. 0.12 when 0.60 gets +20%).
    // ItemStackMixin must include this key when calculating max durability.
    private static final String DURABLE_SB_KEY = "durable_set_bonus";

    public static void updatePlayerSetBonus(ServerPlayerEntity player) {
        // If disabled, remove both the attribute bonus + the durable bonus NBT.
        if (!Tierify.CONFIG.enableArmorSetBonuses) {
            removeSetBonus(player);
            clearDurableSetBonus(player);
            return;
        }

        // Always clear previous state first (prevents stale modifiers / stale durable deltas)
        removeSetBonus(player);
        clearDurableSetBonus(player);

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!SetBonusUtils.hasSetBonus(player, chest)) {
            return;
        }

        Identifier tierId = ModifierUtils.getAttributeID(chest);
        if (tierId == null) {
            return;
        }

        float pct = SetBonusUtils.hasPerfectSetBonus(player, chest)
                ? Tierify.CONFIG.armorSetPerfectBonusPercent
                : Tierify.CONFIG.armorSetBonusMultiplier;

        pct = Math.max(0.0f, pct);

        applySetBonus(player, tierId, pct);
        applyDurableSetBonus(player, tierId, pct);
    }

    /**
     * Applies temporary player attribute modifiers for set bonus.
     * NOTE: We explicitly skip "tiered:generic.durable" because durability is item-derived (max damage),
     * and must be handled via item NBT (see applyDurableSetBonus / ItemStackMixin).
     */
    private static void applySetBonus(ServerPlayerEntity player, Identifier tierId, float setBonusPercent) {
        PotentialAttribute attribute = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tierId);
        if (attribute == null) return;

        for (AttributeTemplate template : attribute.getAttributes()) {
            // Durability is handled separately through item NBT.
            if ("tiered:generic.durable".equals(template.getAttributeTypeID())) continue;

            double baseValue = template.getEntityAttributeModifier().getValue();

            // Only boost positive stats
            if (baseValue <= 0.0D) continue;

            EntityAttribute entityAttribute = Registries.ATTRIBUTE.get(new Identifier(template.getAttributeTypeID()));
            if (entityAttribute == null) continue;

            EntityAttributeInstance instance = player.getAttributeInstance(entityAttribute);
            if (instance == null) continue;

            // 4 pieces worth of bonus 
            double bonusAmount = baseValue * (double) setBonusPercent * 4.0D;

            EntityAttributeModifier bonusModifier = new EntityAttributeModifier(
                    SET_BONUS_ID,
                    BONUS_NAME,
                    bonusAmount,
                    template.getEntityAttributeModifier().getOperation()
            );

            instance.addTemporaryModifier(bonusModifier);
        }
    }

    /**
     * Applies the set bonus delta for tiered:generic.durable by writing a transient NBT key
     * onto each equipped armor piece in the active set.
     *
     * The base durable value remains in "durable"; we write ONLY the delta into DURABLE_SB_KEY.
     * ItemStackMixin must sum durable + durable_set_bonus when computing max damage.
     */
    private static void applyDurableSetBonus(ServerPlayerEntity player, Identifier tierId, float pct) {
        if (pct <= 0.0f) return;

        PotentialAttribute attribute = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tierId);
        if (attribute == null) return;

        // Find durable base value on this tier (we only care if it's positive).
        Double durableBase = null;
        for (AttributeTemplate template : attribute.getAttributes()) {
            if ("tiered:generic.durable".equals(template.getAttributeTypeID())) {
                double v = template.getEntityAttributeModifier().getValue();
                if (v > 0.0D) durableBase = v;
                break;
            }
        }
        if (durableBase == null) return;

        EquipmentSlot[] slots = new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack == null || stack.isEmpty() || !stack.hasNbt()) continue;

            // Only apply to pieces that are actually part of the set bonus.
            if (!SetBonusUtils.hasSetBonus(player, stack)) continue;

            NbtCompound root = stack.getNbt();
            if (root == null) continue;

            // Prefer Tierify extra subtag; fallback to legacy location.
            NbtCompound extra = root.getCompound(Tierify.NBT_SUBTAG_EXTRA_KEY);

            NbtCompound tag =
                    (extra != null && extra.contains("durable")) ? extra
                    : (root.contains(Tierify.NBT_SUBTAG_KEY) && root.contains("durable")) ? root
                    : null;

            if (tag == null) continue;

            // Only multiplier durable gets set bonus here 
            if (!tag.contains("durable")) continue;

            float baseMult = tag.getFloat("durable");
            if (!Float.isFinite(baseMult) || baseMult <= 0.0F) continue;

            // extra multiplier: base * pct, e.g. 0.60 * 0.20 = 0.12
            float sb = baseMult * pct;

            float prev = tag.contains(DURABLE_SB_KEY) ? tag.getFloat(DURABLE_SB_KEY) : 0.0F;
            if (Math.abs(prev - sb) > 1.0e-6f) {
                tag.putFloat(DURABLE_SB_KEY, sb);

                // If we modified the extra compound, ensure it's written back.
                if (tag == extra) {
                    root.put(Tierify.NBT_SUBTAG_EXTRA_KEY, extra);
                }
            }
        }
    }

    /**
     * Removes the transient durable set bonus NBT from equipped armor, and clamps damage if needed.
     * This prevents invalid states if the item was "using" the extra durability.
     */
    private static void clearDurableSetBonus(ServerPlayerEntity player) {
        EquipmentSlot[] slots = new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack == null || stack.isEmpty() || !stack.hasNbt()) continue;

            NbtCompound root = stack.getNbt();
            if (root == null) continue;

            NbtCompound extra = root.getCompound(Tierify.NBT_SUBTAG_EXTRA_KEY);

            NbtCompound tag = null;
            if (extra != null && extra.contains(DURABLE_SB_KEY)) {
                tag = extra;
            } else if (root.contains(DURABLE_SB_KEY)) {
                tag = root;
            }

            if (tag == null) continue;

            tag.remove(DURABLE_SB_KEY);
            if (tag == extra) {
                root.put(Tierify.NBT_SUBTAG_EXTRA_KEY, extra);
            }

            // Safety clamp: if max drops below current damage, prevent invalid state.
            int max = stack.getMaxDamage();
            if (max > 0 && stack.getDamage() >= max) {
                stack.setDamage(max - 1);
            }
        }
    }

    public static void removeSetBonus(ServerPlayerEntity player) {
        for (EntityAttribute attribute : Registries.ATTRIBUTE) {
            EntityAttributeInstance instance = player.getAttributeInstance(attribute);
            if (instance != null && instance.getModifier(SET_BONUS_ID) != null) {
                instance.removeModifier(SET_BONUS_ID);
            }
        }
    }
}
