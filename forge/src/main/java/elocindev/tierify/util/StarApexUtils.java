package elocindev.tierify.util;

import elocindev.tierify.TierifyConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class StarApexUtils {

    private StarApexUtils() {}

    public static int getStars(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        CompoundTag extra = getExtraTag(stack, false);
        if (extra == null) return 0;
        return Mth.clamp(extra.getInt(TierifyConstants.NBT_STARS_KEY), 0, 5);
    }

    public static void setStars(ItemStack stack, int stars) {
        if (stack == null || stack.isEmpty()) return;
        int clamped = Mth.clamp(stars, 0, 5);
        CompoundTag extra = getExtraTag(stack, clamped > 0);
        if (extra == null) return;

        if (clamped == 0) {
            extra.remove(TierifyConstants.NBT_STARS_KEY);
        } else {
            extra.putInt(TierifyConstants.NBT_STARS_KEY, clamped);
        }

        commitExtraTag(stack, extra);
    }

    public static boolean isApex(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag extra = getExtraTag(stack, false);
        return extra != null && extra.getBoolean(TierifyConstants.NBT_APEX_KEY);
    }

    public static void setApex(ItemStack stack, boolean apex) {
        if (stack == null || stack.isEmpty()) return;
        CompoundTag extra = getExtraTag(stack, apex);
        if (extra == null) return;

        if (apex) {
            extra.putBoolean(TierifyConstants.NBT_APEX_KEY, true);
        } else {
            extra.remove(TierifyConstants.NBT_APEX_KEY);
        }

        commitExtraTag(stack, extra);
    }

    public static boolean canApplyStar(ItemStack stack) {
        return isMythicTier(stack) && getStars(stack) < 5 && !isApex(stack);
    }

    public static boolean canApplyApex(ItemStack stack) {
        return isMythicTier(stack) && getStars(stack) >= 5 && !isApex(stack);
    }

    public static boolean hasTier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag tierTag = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tierTag == null) return false;
        return tierTag.contains(TierifyConstants.NBT_SUBTAG_DATA_KEY, Tag.TAG_STRING)
                && !tierTag.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY).isEmpty();
    }

    public static boolean isMythicTier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag tierTag = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tierTag == null) return false;

        String tierStr = tierTag.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        if (tierStr == null || tierStr.isEmpty()) return false;

        ResourceLocation id = ResourceLocation.tryParse(tierStr);
        return id != null && id.getPath().startsWith("mythic");
    }

    private static CompoundTag getExtraTag(ItemStack stack, boolean create) {
        CompoundTag root = create ? stack.getOrCreateTag() : stack.getTag();
        if (root == null) return null;
        if (root.contains(TierifyConstants.NBT_SUBTAG_EXTRA_KEY, Tag.TAG_COMPOUND)) {
            return root.getCompound(TierifyConstants.NBT_SUBTAG_EXTRA_KEY);
        }
        if (!create) return null;
        CompoundTag extra = new CompoundTag();
        root.put(TierifyConstants.NBT_SUBTAG_EXTRA_KEY, extra);
        return extra;
    }

    private static void commitExtraTag(ItemStack stack, CompoundTag extra) {
        CompoundTag root = stack.getOrCreateTag();
        if (extra == null || extra.isEmpty()) {
            root.remove(TierifyConstants.NBT_SUBTAG_EXTRA_KEY);
        } else {
            root.put(TierifyConstants.NBT_SUBTAG_EXTRA_KEY, extra);
        }
    }
}
