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

// We purposely use String targets to keep the dependency OPTIONAL.
@SuppressWarnings("all")
@Mixin(targets = {
    "net.goo.brutality.item.base.BrutalityCurioItem",
    "net.goo.brutality.item.base.BrutalityAnkletItem",
    "net.goo.brutality.item.curios.charm.Greed", 
    "net.goo.brutality.item.curios.charm.Lust",
    "net.goo.brutality.item.curios.charm.ResplendentFeather" 
}, remap = false)
public class BrutalityStackingFixMixin {

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true, remap = false)
    private void fixHardcodedUUIDs(SlotContext slotContext, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        Multimap<EntityAttribute, EntityAttributeModifier> originalMap = cir.getReturnValue();
        if (originalMap.isEmpty()) return;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> newMap = ImmutableMultimap.builder();
        originalMap.forEach((attribute, modifier) -> {
            // Formula: Hash of (Original Hardcoded UUID + Slot Identifier + Slot Index)
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
