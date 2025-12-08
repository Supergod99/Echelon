package elocindev.tierify.mixin.compat;

import dev.xylonity.tooltipoverhaul.item.WeaponProfile;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.collect.Multimap;

@Mixin(WeaponProfile.class)
public class MixinWeaponProfile {

    @Overwrite(remap = false)
    public String getAttackSpeedRating(ItemStack stack, PlayerEntity player) {


        double baseSpeed = 4.0; 
        double addedValue = 0.0;
        double multiplyBase = 0.0;
        double multiplyTotal = 0.0;

        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);

        if (modifiers.containsKey(EntityAttributes.GENERIC_ATTACK_SPEED)) {
            for (EntityAttributeModifier mod : modifiers.get(EntityAttributes.GENERIC_ATTACK_SPEED)) {
                if (mod.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    addedValue += mod.getValue();
                } else if (mod.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE) {
                    multiplyBase += mod.getValue();
                } else if (mod.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    multiplyTotal += mod.getValue();
                }
            }
        }

        double speed = (baseSpeed + addedValue) * (1.0 + multiplyBase) * (1.0 + multiplyTotal);


        if (speed >= 3.0) return "Very Fast";
        else if (speed >= 2.0) return "Fast";
        else if (speed >= 1.2) return "Medium"; 
        else if (speed > 0.6) return "Slow";    
        else return "Very Slow";
    }
}
