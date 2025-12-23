package elocindev.tierify.mixin.compat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack; // Use standard Minecraft ItemStack
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

// CRITICAL CHANGE: 
// 1. Target "ICurioItem" (The Interface used by KnightsPendant).
// 2. Define this file as an 'interface' (public interface) to match the target type.
// 3. remap = false because we are targeting a Forge/Connector library interface.
@Mixin(targets = "top.theillusivec4.curios.api.type.capability.ICurioItem", remap = false)
public interface CuriosInterfaceMixin {
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true, remap = false)
    private void fixBrutalityStacking(SlotContext slotContext, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        // 1. SAFETY CHECK
        if (stack == null || stack.isEmpty()) return;
        // We check if it's a Brutality item.
        // This covers KnightsPendant, Warped Sliceblade, and all others.
        String itemId = stack.getItem().toString(); 
        if (!itemId.contains("brutality")) {
            return;
        }
        Multimap<EntityAttribute, EntityAttributeModifier> originalMap = cir.getReturnValue();
        if (originalMap == null || originalMap.isEmpty()) return;
        // 2. THE UNIVERSAL FIX
        // We rebuild the attribute map.
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> newMap = ImmutableMultimap.builder();

        originalMap.forEach((attribute, modifier) -> {
            // THE SECRET SAUCE:
            // We take the original hardcoded UUID (like the one you found for Lethality: a23c6244...)
            // And we combine it with the Slot Identifier and Slot Index.
            // This forces the game to treat "Lethality on Ring 1" as different from "Lethality on Ring 2".
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
