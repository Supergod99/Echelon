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

import java.util.UUID;

// We target the Curios Helper instead of the Brutality mod directly.
// This prevents forcing Brutality to load too early and breaking KubeJS.
@Mixin(targets = "top.theillusivec4.curios.common.CuriosHelper", remap = false)
public class CuriosSafeStackingMixin {

    @Inject(method = "getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Ljava/util/UUID;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;", 
            at = @At("RETURN"), 
            cancellable = true, 
            remap = false)
    private void fixBrutalityUUIDs(SlotContext slotContext, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        // Only run this logic if the item is from Brutality.
        // We use string checking to avoid importing the Brutality class (which caused the crash).
        String itemId = stack.getItem().toString(); // Returns "brutality:item_name" usually
        if (itemId == null || !itemId.contains("brutality")) {
            return;
        }

        Multimap<EntityAttribute, EntityAttributeModifier> originalMap = cir.getReturnValue();
        if (originalMap.isEmpty()) return;

        // Rebuild the map with unique UUIDs
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> newMap = ImmutableMultimap.builder();

        originalMap.forEach((attribute, modifier) -> {
            // Create a unique salt based on the slot index to prevent stacking issues
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
