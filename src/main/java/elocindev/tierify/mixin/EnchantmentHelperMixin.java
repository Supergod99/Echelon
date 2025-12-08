package elocindev.tierify.mixin;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void tierify$addFortuneAttribute(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {

        if (enchantment != Enchantments.FORTUNE) return;
        double fortuneBonus = 0;
        
        if (stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(CustomEntityAttributes.FORTUNE)) {
            Collection<EntityAttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(CustomEntityAttributes.FORTUNE);
            
            for (EntityAttributeModifier mod : modifiers) {
                 if (mod.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                     fortuneBonus += mod.getValue();
                 }
            }
        }

        if (fortuneBonus > 0) {
            cir.setReturnValue(cir.getReturnValue() + (int) fortuneBonus);
        }
    }
}
