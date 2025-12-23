package elocindev.tierify.mixin.compat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// We target the Capability Interface 'ICurio'.
// This ensures we catch the modifiers even if the Item class itself doesn't implement ICurioItem.
@Mixin(targets = "top.theillusivec4.curios.api.type.capability.ICurio", remap = false)
public interface CuriosInterfaceMixin {

    // A hardcoded set of Brutality's static UUIDs that cause stacking issues.
    // We match against these to know if we should apply the fix.
    Set<UUID> BRUTALITY_UUIDS = new HashSet<>(Set.of(
        UUID.fromString("1574cbcf-e1ef-4d47-ac7c-726da719955c"), // Daemonium Whetstone
        UUID.fromString("f2235ea1-85b3-4d8b-a5c4-e5da054f4f47"), // Wire Cutters (Armor Pen)
        UUID.fromString("c736e8f3-e67a-41be-9c10-48ecb422ce0c"), // Wire Cutters (Crit Dmg)
        UUID.fromString("385c1f5b-1e50-4c28-bb9e-39198e2847da"), // Devils Anklet
        UUID.fromString("0ff5d362-c7e2-48b9-983e-a9897beee2ae"), // Emerald Anklet
        UUID.fromString("b84f46c5-9ac2-4bab-bbd2-ada485d732bc"), // Deadshot Brooch
        UUID.fromString("f22b0f7c-2446-4f4b-9c00-1d8e1d384542"), // Deck Of Cards
        UUID.fromString("8cec320c-659a-4221-b8e1-2e73e734f831"), // Scribes Index
        UUID.fromString("e9b30c40-eaf9-475c-ac92-905f76983256"), // Self Repair (Lifesteal)
        UUID.fromString("a130ccf5-aac9-447a-b519-fd38057772e7"), // Self Repair (Health)
        UUID.fromString("7c4d948e-6e54-42a0-8232-65129435fe99"), // Soldiers Syringe
        UUID.fromString("e2bc1e9c-5cde-4de1-8e3a-60d97d6673d8"), // Spite Shard
        UUID.fromString("b47c5a0d-e217-4f50-805f-f03a6930cd86"), // Vampiric Talisman
        UUID.fromString("fd88bba5-e451-4ed7-aa8a-b3f19e14a5ee")  // Target Cube
    ));

    @Inject(method = "getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Ljava/util/UUID;)Lcom/google/common/collect/Multimap;", 
            at = @At("RETURN"), 
            cancellable = true, 
            remap = false)
    private void fixBrutalityStacking(SlotContext slotContext, UUID uuid, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        Multimap<EntityAttribute, EntityAttributeModifier> originalMap = cir.getReturnValue();
        if (originalMap == null || originalMap.isEmpty()) return;

        // Check if any of the modifiers in the map match our known "Bad UUIDs"
        boolean needsFix = false;
        for (EntityAttributeModifier mod : originalMap.values()) {
            if (BRUTALITY_UUIDS.contains(mod.getId())) {
                needsFix = true;
                break;
            }
        }

        // If no Brutality modifiers found, exit to save performance
        if (!needsFix) return;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> newMap = ImmutableMultimap.builder();

        originalMap.forEach((attribute, modifier) -> {
            UUID finalUUID = modifier.getId();

            // If this is a Brutality modifier, SALT THE UUID.
            // This ensures every slot gets a unique ID, fixing the 0 damage / stacking bug.
            if (BRUTALITY_UUIDS.contains(finalUUID)) {
                String salt = finalUUID.toString() + ":" + slotContext.identifier() + ":" + slotContext.index();
                finalUUID = UUID.nameUUIDFromBytes(salt.getBytes());
            }

            EntityAttributeModifier newModifier = new EntityAttributeModifier(
                finalUUID,
                modifier.getName(),
                modifier.getValue(),
                modifier.getOperation()
            );

            newMap.put(attribute, newModifier);
        });

        cir.setReturnValue(newMap.build());
    }
}
