package elocindev.tierify.forge.apex;

import elocindev.tierify.TierifyConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ApexEffectRegistry {

    private static final Map<ResourceLocation, ApexEffect> EFFECTS = new HashMap<>();

    private ApexEffectRegistry() {}

    public static void register(ResourceLocation reforgeId, ApexEffect effect) {
        Objects.requireNonNull(reforgeId, "reforgeId");
        Objects.requireNonNull(effect, "effect");
        EFFECTS.put(reforgeId, effect);
    }

    public static ApexEffect get(ResourceLocation reforgeId) {
        return EFFECTS.get(reforgeId);
    }

    public static ApexEffect resolveFor(ItemStack stack) {
        ResourceLocation reforgeId = getReforgeId(stack);
        return reforgeId != null ? EFFECTS.get(reforgeId) : null;
    }

    public static void clear() {
        EFFECTS.clear();
    }

    public static ResourceLocation getReforgeId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null || !tiered.contains(TierifyConstants.NBT_SUBTAG_DATA_KEY, Tag.TAG_STRING)) return null;
        String id = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        return id == null || id.isEmpty() ? null : ResourceLocation.tryParse(id);
    }
}
