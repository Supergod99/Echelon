package draylar.tiered.api;

import elocindev.tierify.Tierify;
import elocindev.tierify.util.SetBonusUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SetBonusLogic {

    private static final UUID SET_BONUS_ID = UUID.fromString("98765432-1234-1234-1234-987654321012");
    private static final String BONUS_NAME = "Tierify Set Bonus";

    public static void updatePlayerSetBonus(ServerPlayerEntity player) {
        if (!Tierify.CONFIG.enableArmorSetBonuses) {
            removeSetBonus(player);
            return;
        }
    
        removeSetBonus(player);

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
    }

    private static void applySetBonus(ServerPlayerEntity player, Identifier tierId, float setBonusPercent) {
        PotentialAttribute attribute = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tierId);
        if (attribute == null) return;

        for (AttributeTemplate template : attribute.getAttributes()) {
            double baseValue = template.getEntityAttributeModifier().getValue();

            // Only boost positive stats
            if (baseValue <= 0.0D) continue;

            EntityAttribute entityAttribute = Registries.ATTRIBUTE.get(new Identifier(template.getAttributeTypeID()));
            if (entityAttribute == null) continue;

            EntityAttributeInstance instance = player.getAttributeInstance(entityAttribute);
            if (instance == null) continue;

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

    public static void removeSetBonus(ServerPlayerEntity player) {
        for (EntityAttribute attribute : Registries.ATTRIBUTE) {
            EntityAttributeInstance instance = player.getAttributeInstance(attribute);
            if (instance != null && instance.getModifier(SET_BONUS_ID) != null) {
                instance.removeModifier(SET_BONUS_ID);
            }
        }
    }
}
