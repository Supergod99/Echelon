package draylar.tiered.api;

import elocindev.tierify.Tierify;
import elocindev.tierify.util.SetBonusUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SetBonusLogic {

    private static final UUID SET_BONUS_ID = UUID.fromString("98765432-1234-1234-1234-987654321012");
    private static final String BONUS_NAME = "Tierify Set Bonus";

    /**
     * Stores ONLY the extra multiplier contributed by the set bonus (e.g. 0.12 when 0.60 gets +20%).
     * ItemStackMixin must include this key when calculating max durability.
     *
     * Important: this key MUST be cleared from items when the set is not active, including items that
     * were unequipped (moved into inventory). Otherwise durability will appear "stuck" until the item
     * is re-equipped and rewritten.
     */
    private static final String DURABLE_SB_KEY = "durable_set_bonus";

    public static void updatePlayerSetBonus(ServerPlayerEntity player) {
        // If disabled, remove both the attribute bonus + the durable bonus NBT.
        if (!Tierify.CONFIG.enableArmorSetBonuses) {
            removeSetBonus(player);
            clearDurableSetBonusEverywhere(player);
            return;
        }

        // Always clear previous state first (prevents stale modifiers / stale durable deltas)
        removeSetBonus(player);
        clearDurableSetBonusEverywhere(player);

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

        // Ensure this tier actually defines durable and it's positive.
        Double durableTierBase = null;
        for (AttributeTemplate template : attribute.getAttributes()) {
            if ("tiered:generic.durable".equals(template.getAttributeTypeID())) {
                double v = template.getEntityAttributeModifier().getValue();
                if (v > 0.0D) durableTierBase = v;
                break;
            }
        }
        if (durableTierBase == null) return;

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

            // Find where "durable" is stored (Tierify extra preferred, then tier tag, then legacy root).
            NbtCompound extra = root.contains(Tierify.NBT_SUBTAG_EXTRA_KEY, 10) ? root.getCompound(Tierify.NBT_SUBTAG_EXTRA_KEY) : null;
            NbtCompound tier  = root.contains(Tierify.NBT_SUBTAG_KEY, 10)       ? root.getCompound(Tierify.NBT_SUBTAG_KEY)       : null;

            NbtCompound container = null;
            if (extra != null && extra.contains("durable")) container = extra;
            else if (tier != null && tier.contains("durable")) container = tier;
            else if (root.contains("durable")) container = root;

            if (container == null) continue;
            if (!container.contains("durable")) continue;

            float baseMult = container.getFloat("durable");
            if (!Float.isFinite(baseMult) || baseMult <= 0.0F) continue;

            // extra multiplier: base * pct, e.g. 0.60 * 0.20 = 0.12
            float sb = baseMult * pct;

            float prev = container.contains(DURABLE_SB_KEY) ? container.getFloat(DURABLE_SB_KEY) : 0.0F;
            if (Math.abs(prev - sb) > 1.0e-6f) {
                container.putFloat(DURABLE_SB_KEY, sb);

                // Write back in case the underlying implementation returns copies.
                if (container == extra) root.put(Tierify.NBT_SUBTAG_EXTRA_KEY, extra);
                else if (container == tier) root.put(Tierify.NBT_SUBTAG_KEY, tier);
            }
        }
    }

    /**
     * Removes the transient durable set bonus NBT from ALL items the player owns (armor + inventory + offhand),
     * and clamps damage if needed.
     *
     * This is the critical fix for “stuck” durability: when an item is unequipped it keeps its NBT unless we
     * also scrub inventory stacks (not just currently-equipped slots).
     */
    private static void clearDurableSetBonusEverywhere(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();

        // Main inventory
        for (int i = 0; i < inv.main.size(); i++) {
            clearDurableSetBonusOnStack(inv.main.get(i));
        }

        // Armor inventory (these are often the same stacks as equipped slots, but this is harmless)
        for (int i = 0; i < inv.armor.size(); i++) {
            clearDurableSetBonusOnStack(inv.armor.get(i));
        }

        // Offhand inventory
        for (int i = 0; i < inv.offHand.size(); i++) {
            clearDurableSetBonusOnStack(inv.offHand.get(i));
        }
    }

    private static void clearDurableSetBonusOnStack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasNbt()) return;

        NbtCompound root = stack.getNbt();
        if (root == null) return;

        boolean changed = false;

        // Remove from Tierify extra if present
        if (root.contains(Tierify.NBT_SUBTAG_EXTRA_KEY, 10)) {
            NbtCompound extra = root.getCompound(Tierify.NBT_SUBTAG_EXTRA_KEY);
            if (extra.contains(DURABLE_SB_KEY)) {
                extra.remove(DURABLE_SB_KEY);
                root.put(Tierify.NBT_SUBTAG_EXTRA_KEY, extra);
                changed = true;
            }
        }

        // Remove from Tier tag if present (covers any variants that stored it there)
        if (root.contains(Tierify.NBT_SUBTAG_KEY, 10)) {
            NbtCompound tier = root.getCompound(Tierify.NBT_SUBTAG_KEY);
            if (tier.contains(DURABLE_SB_KEY)) {
                tier.remove(DURABLE_SB_KEY);
                root.put(Tierify.NBT_SUBTAG_KEY, tier);
                changed = true;
            }
        }

        // Remove legacy root placement if present
        if (root.contains(DURABLE_SB_KEY)) {
            root.remove(DURABLE_SB_KEY);
            changed = true;
        }

        if (!changed) return;

        // Safety clamp: if max drops below current damage, prevent invalid state.
        int max = stack.getMaxDamage();
        if (max > 0 && stack.getDamage() >= max) {
            stack.setDamage(max - 1);
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
