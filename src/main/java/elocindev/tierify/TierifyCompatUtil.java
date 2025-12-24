package elocindev.tierify;

import com.google.common.collect.Multimap;
import draylar.tiered.api.AttributeTemplate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

public final class TierifyCompatUtil {
    private TierifyCompatUtil() {}

    /**
     * Checks whether the modifier that AttributeTemplate.realize(...) WOULD add
     * is already present in the multimap, for this (template, slot, stack).
     *
     * This mirrors AttributeTemplate.realize UUID/name construction exactly. :contentReference[oaicite:4]{index=4}
     */
    public static boolean hasSameModifierAlready(
            Multimap<EntityAttribute, EntityAttributeModifier> modifiers,
            AttributeTemplate template,
            EquipmentSlot slot,
            ItemStack stack
    ) {
        if (modifiers == null || template == null || slot == null || stack == null) return false;

        String attributeTypeID = template.getAttributeTypeID();
        if (attributeTypeID == null || attributeTypeID.isEmpty()) return false;

        EntityAttribute key = Registries.ATTRIBUTE.get(new Identifier(attributeTypeID));
        if (key == null) return false;

        // Mirror AttributeTemplate.realize salt logic :contentReference[oaicite:5]{index=5}
        String salt;
        NbtCompound tierTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (tierTag != null && tierTag.contains("TierUUID")) {
            salt = tierTag.getUuid("TierUUID").toString();
        } else {
            salt = stack.getItem().getTranslationKey();
        }

        // Mirror AttributeTemplate.realize UUID generation :contentReference[oaicite:6]{index=6}
        UUID expectedId = UUID.nameUUIDFromBytes(
                (attributeTypeID + "_" + slot.getName() + "_" + salt).getBytes(StandardCharsets.UTF_8)
        );

        EntityAttributeModifier base = template.getEntityAttributeModifier();
        if (base == null) return false;

        // Mirror AttributeTemplate.realize naming :contentReference[oaicite:7]{index=7}
        String expectedName = base.getName() + "_" + slot.getName();
        double expectedValue = base.getValue();
        EntityAttributeModifier.Operation expectedOp = base.getOperation();

        Collection<EntityAttributeModifier> existingForAttr = modifiers.get(key);
        if (existingForAttr == null || existingForAttr.isEmpty()) return false;

        for (EntityAttributeModifier existing : existingForAttr) {
            // Strongest: exact UUID match
            if (expectedId.equals(existing.getId())) return true;

            // Secondary: name/op/value match (in case some other layer rewrote UUIDs)
            if (expectedOp == existing.getOperation()
                    && Double.compare(expectedValue, existing.getValue()) == 0
                    && expectedName.equals(existing.getName())) {
                return true;
            }
        }

        return false;
    }
}
