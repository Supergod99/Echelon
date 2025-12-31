package elocindev.tierify.compat;

import draylar.tiered.api.ModifierUtils;
import elocindev.tierify.Tierify;
import elocindev.tierify.config.TreasureBagProfiles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class ArmageddonTreasureBagHooks {
    private ArmageddonTreasureBagHooks() {}

    public static ItemStack maybeReforgeFromHeldBag(ItemStack spawned, Entity entity) {
        if (spawned == null || spawned.isEmpty()) return spawned;
        if (!Tierify.CONFIG.treasureBagDropModifier) return spawned;

        // Don’t overwrite already-tiered items
        NbtCompound tierTag = spawned.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (tierTag != null && tierTag.contains(Tierify.NBT_SUBTAG_DATA_KEY)) return spawned;

        if (!(entity instanceof PlayerEntity player)) return spawned;

        Identifier bagId = resolveBagIdFromHands(player);
        if (bagId == null) return spawned;

        TreasureBagProfiles.Entry profile = TreasureBagProfiles.get(bagId);
        if (profile == null) return spawned;

        // Per-spawned-item chance roll (as requested)
        if (Math.random() > profile.chance()) return spawned;

        ModifierUtils.setItemStackAttributeEntityWeightedWithCustomWeights(player, spawned, profile.weights());
        return spawned;
    }

    private static Identifier resolveBagIdFromHands(PlayerEntity player) {
        // Check mainhand/offhand; return the first hand whose item is configured as a treasure bag
        ItemStack main = player.getMainHandStack();
        if (!main.isEmpty()) {
            Identifier id = Registries.ITEM.getId(main.getItem());
            if (TreasureBagProfiles.get(id) != null) return id;
        }

        ItemStack off = player.getOffHandStack();
        if (!off.isEmpty()) {
            Identifier id = Registries.ITEM.getId(off.getItem());
            if (TreasureBagProfiles.get(id) != null) return id;
        }

        return null;
    }
}
