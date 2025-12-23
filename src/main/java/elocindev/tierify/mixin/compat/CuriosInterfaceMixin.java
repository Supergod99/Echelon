package elocindev.tierify.mixin.compat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

@Mixin(ICurioItem.class)
public class CuriosInterfaceMixin {

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true, remap = false)
    private void fixBrutalityStacking(SlotContext slotContext, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {   
        // If the stack is empty or NOT a Brutality item, stop immediately.
        if (stack == null || stack.isEmpty()) return;
        // We use string checking to verify the mod ID without importing broken classes.
        String itemId = stack.getItem().toString(); 
        if (!itemId.contains("brutality")) {
            return;
        }

        Multimap<EntityAttribute, EntityAttributeModifier> originalMap = cir.getReturnValue();
        if (originalMap == null || originalMap.isEmpty()) return;
        // Rebuild the attribute map with unique UUIDs.
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> newMap = ImmutableMultimap.builder();
        originalMap.forEach((attribute, modifier) -> {
            // Generate a unique salt based on: Original UUID + Slot Name + Slot Index
            // This guarantees that two identical Rings in two different slots get different UUIDs.
            String salt = modifier.getId().toString() + ":" + slotContext.identifier() + ":" + slotContext.index();
            UUID uniqueID = UUID.nameUUIDFromBytes(salt.getBytes());

            EntityAttributeModifier newModifier = new EntityAttributeModifier(
                uniqueID,
                modifier.getName(),
                modifier.getValue(),
                modifier.getOperation()
            );

            newMap.put(attribute, newModifier);
        });

        cir.setReturnValue(newMap.build());
    }
}
