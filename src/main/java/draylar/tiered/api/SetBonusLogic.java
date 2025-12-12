package draylar.tiered.api; 

import draylar.tiered.api.AttributeTemplate;
import draylar.tiered.api.ModifierUtils;
import draylar.tiered.api.PotentialAttribute;
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

    // Unique ID to track our temporary bonus
    private static final UUID SET_BONUS_ID = UUID.fromString("98765432-1234-1234-1234-987654321012");
    private static final String BONUS_NAME = "Tierify Set Bonus";
    
    public static void updatePlayerSetBonus(ServerPlayerEntity player) {
        // Check Chestplate as the "representative" item for the set
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);

        if (SetBonusUtils.hasSetBonus(player, chest)) {
             Identifier tierId = ModifierUtils.getAttributeID(chest);
             if (tierId != null) {
                 applySetBonus(player, tierId);
                 float pct = SetBonusUtils.hasPerfectSetBonus(player, chest)
                         ? Tierify.CONFIG.armorSetPerfectBonusPercent
                         : Tierify.CONFIG.armorSetBonusPercent;
                 applySetBonus(player, tierId, pct);
                 return;
             }
        }
        
        // If no set found, remove bonuses
        removeSetBonus(player);
    }

    private static void applySetBonus(ServerPlayerEntity player, Identifier tierId, float setBonusPercent) {
        PotentialAttribute attribute = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tierId);
        if (attribute == null) return;

        for (AttributeTemplate template : attribute.getAttributes()) {
            double baseValue = template.getEntityAttributeModifier().getValue();

            // Only boost positive stats
            if (baseValue <= 0) continue; 

            EntityAttribute entityAttribute = net.minecraft.registry.Registries.ATTRIBUTE.get(new Identifier(template.getAttributeTypeID()));
            if (entityAttribute == null) continue;

            EntityAttributeInstance instance = player.getAttributeInstance(entityAttribute);
            if (instance == null) continue;

            if (instance.getModifier(SET_BONUS_ID) == null) {
                double bonusMultiplier = Math.max(0.0, Tierify.CONFIG.armorSetBonusMultiplier);
                double bonusAmount = baseValue * bonusMultiplier * 4.0;
                EntityAttributeModifier bonusModifier = new EntityAttributeModifier(
                    SET_BONUS_ID, 
                    BONUS_NAME, 
                    bonusAmount, 
                    template.getEntityAttributeModifier().getOperation()
                );
                instance.addTemporaryModifier(bonusModifier);
            }
            // Always refresh so 25% <-> 100% swaps correctly when the set becomes Perfect (or stops being Perfect).
            if (instance.getModifier(SET_BONUS_ID) != null) {
                instance.removeModifier(SET_BONUS_ID);
            }

            double bonusAmount = baseValue * setBonusPercent * 4.0D;
            EntityAttributeModifier bonusModifier = new EntityAttributeModifier(
                SET_BONUS_ID, BONUS_NAME, bonusAmount, template.getEntityAttributeModifier().getOperation()
            );
            instance.addTemporaryModifier(bonusModifier);
        }
    }

    public static void removeSetBonus(ServerPlayerEntity player) {
        for (net.minecraft.entity.attribute.EntityAttribute attribute : net.minecraft.registry.Registries.ATTRIBUTE) {
            net.minecraft.entity.attribute.EntityAttributeInstance instance = player.getAttributeInstance(attribute);
            if (instance != null && instance.getModifier(SET_BONUS_ID) != null) {
                instance.removeModifier(SET_BONUS_ID);
            }
        }
    }
}
