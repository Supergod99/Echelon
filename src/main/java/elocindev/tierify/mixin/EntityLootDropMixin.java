package elocindev.tierify.mixin;

import elocindev.tierify.Tierify;
import elocindev.tierify.config.EntityLootDropProfiles;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class EntityLootDropMixin {

    @Redirect(
        method = "dropLoot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;Ljava/util/function/Consumer;)V"
        ),
        require = 0
    )
    private void echelon$wrapGenerateLoot(LootTable table, LootContextParameterSet params, Consumer<ItemStack> consumer) {
        LivingEntity self = (LivingEntity) (Object) this;
        Consumer<ItemStack> wrapped = echelon$wrapConsumer(self, consumer, null);
        table.generateLoot(params, wrapped);
    }

    @Redirect(
        method = "dropLoot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V"
        ),
        require = 0
    )
    private void echelon$wrapGenerateLootSeed(LootTable table, LootContextParameterSet params, long seed, Consumer<ItemStack> consumer) {
        LivingEntity self = (LivingEntity) (Object) this;
        // Use a derived RNG so we don't interfere with loot-table RNG consumption
        Random rng = Random.create(seed ^ 0x5EEDC0DEL);
        Consumer<ItemStack> wrapped = echelon$wrapConsumer(self, consumer, rng);
        table.generateLoot(params, seed, wrapped);
    }

    private Consumer<ItemStack> echelon$wrapConsumer(LivingEntity self, Consumer<ItemStack> original, @Nullable Random rng) {
        if (self.getWorld().isClient()) return original;
        if (!Tierify.CONFIG.entityLootDropModifier) return original;

        Identifier entityId = Registries.ENTITY_TYPE.getId(self.getType());
        EntityLootDropProfiles.Entry profile = EntityLootDropProfiles.get(entityId);
        if (profile == null) return original;

        final Random rollRng = (rng != null) ? rng : Random.create();

        return (ItemStack stack) -> {
            if (stack == null || stack.isEmpty()) {
                original.accept(stack);
                return;
            }

            // Don't overwrite existing Tierify tier
            NbtCompound tierTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            if (tierTag != null && tierTag.contains(Tierify.NBT_SUBTAG_DATA_KEY)) { // :contentReference[oaicite:8]{index=8}
                original.accept(stack);
                return;
            }

            if (rollRng.nextFloat() < profile.chance()) {
                ModifierUtils.setItemStackAttributeEntityWeightedWithCustomWeights(null, stack, profile.weights());
            }

            original.accept(stack);
        };
    }
}
