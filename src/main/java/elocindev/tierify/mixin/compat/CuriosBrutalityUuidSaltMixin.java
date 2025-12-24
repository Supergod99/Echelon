package elocindev.tierify.mixin.compat;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Mixin(targets = "top.theillusivec4.curios.mixin.CuriosImplMixinHooks", remap = false)
public abstract class CuriosBrutalityUuidSaltMixin {

    @Unique
    private static boolean echelon$isBrutality(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id != null && "brutality".equals(id.getNamespace());
    }

    @Inject(
            method = "getAttributeModifiers",
            at = @At("RETURN"),
            cancellable = true,
            require = 0
    )
    private static void echelon$rewriteBrutalityCurioModifierUuids(
            Object slotContext, // SlotContext (Curios) - keep Object to avoid compile dep
            UUID slotUuid,
            ItemStack stack,
            CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir
    ) {
        if (!echelon$isBrutality(stack)) return;

        Multimap<EntityAttribute, EntityAttributeModifier> original = cir.getReturnValue();
        if (original == null || original.isEmpty()) return;

        Multimap<EntityAttribute, EntityAttributeModifier> out = LinkedHashMultimap.create();

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        String saltBase = slotUuid.toString() + "|" + itemId;

        for (Map.Entry<EntityAttribute, EntityAttributeModifier> e : original.entries()) {
            EntityAttribute attr = e.getKey();
            EntityAttributeModifier mod = e.getValue();

            // Deterministic unique UUID per (slotUuid + item + originalUUID)
            UUID newId = UUID.nameUUIDFromBytes(
                    (saltBase + "|" + mod.getId().toString()).getBytes(StandardCharsets.UTF_8)
            );

            out.put(attr, new EntityAttributeModifier(newId, mod.getName(), mod.getValue(), mod.getOperation()));
        }

        cir.setReturnValue(out);
    }
}
