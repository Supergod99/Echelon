package elocindev.tierify;

import com.google.common.collect.Multimap;
import draylar.tiered.api.AttributeTemplate;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

/**
 * Small helper for safely avoiding duplicate attribute injection during
 * ModifyItemAttributeModifiersCallback.
 */
public final class TierifyCompatUtil {
    private TierifyCompatUtil() {}

    public static boolean hasSameModifierAlready(
            Multimap<EntityAttribute, EntityAttributeModifier> modifiers,
            AttributeTemplate template
    ) {
        if (modifiers == null || template == null) return false;

        EntityAttribute attr = template.getEntityAttribute();
        if (attr == null) return false;

        EntityAttributeModifier candidate = template.getEntityAttributeModifier();
        if (candidate == null) return false;

        for (EntityAttributeModifier existing : modifiers.get(attr)) {
            // Best-case: stable UUID match
            if (existing.getId().equals(candidate.getId())) return true;

            // Fallback: name + op + amount match (handles cases where UUID may be rewritten/salted)
            if (existing.getOperation() == candidate.getOperation()
                    && Double.compare(existing.getValue(), candidate.getValue()) == 0
                    && existing.getName().equals(candidate.getName())) {
                return true;
            }
        }

        return false;
    }
}
