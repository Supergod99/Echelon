package elocindev.tierify.util;

import elocindev.tierify.Tierify;

import net.minecraft.item.ArmorItem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SetBonusUtils {

    // Checks if the player is wearing 4 pieces of armor that match the Tier ID of the passed itemStack.
    public static boolean hasSetBonus(PlayerEntity player, ItemStack itemStack) {
        if (player == null || itemStack.isEmpty()) return false;

        // Get the Tier ID from the item being looked at
        NbtCompound nbt = itemStack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (nbt == null) return false;
        
        String targetTier = nbt.getString(Tierify.NBT_SUBTAG_DATA_KEY);
        if (targetTier.isEmpty()) return false;

        // Check all 4 armor slots
        int matchCount = 0;
        for (ItemStack armor : player.getInventory().armor) {
            NbtCompound armorNbt = armor.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            if (armorNbt != null && armorNbt.getString(Tierify.NBT_SUBTAG_DATA_KEY).equals(targetTier)) {
                matchCount++;
            }
        }

        // Return true only if we have 4 matches (Full Set)
        return matchCount >= 4;
    }

    public static MutableText getSetBonusActiveLabel(PlayerEntity player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) return null;
        if (!(stack.getItem() instanceof ArmorItem)) return null;
    
        if (hasPerfectSetBonus(player, stack)) {
            int pct = Math.round((float) Tierify.CONFIG.armorSetPerfectBonusPercent * 100.0f);
            return Text.literal("Perfect Set Bonus Active (+" + pct + "%)").formatted(Formatting.GOLD);
        }
    
        if (hasSetBonus(player, stack)) {
            int pct = Math.round((float) Tierify.CONFIG.armorSetBonusMultiplier * 100.0f);
            return Text.literal("Set Bonus Active (+" + pct + "%)").formatted(Formatting.GOLD);
        }
    
        return null;
    }

    // True only if the player has a full matching set AND all 4 pieces are Perfect.
    public static boolean hasPerfectSetBonus(PlayerEntity player, ItemStack itemStack) {
        if (!hasSetBonus(player, itemStack)) return false;

        NbtCompound nbt = itemStack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (nbt == null) return false;

        String targetTier = nbt.getString(Tierify.NBT_SUBTAG_DATA_KEY);
        if (targetTier.isEmpty()) return false;

        for (ItemStack armor : player.getInventory().armor) {
            if (armor.isEmpty()) return false;

            NbtCompound armorNbt = armor.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            if (armorNbt == null) return false;

            String armorTier = armorNbt.getString(Tierify.NBT_SUBTAG_DATA_KEY);
            if (!targetTier.equals(armorTier)) return false;

            if (!armorNbt.getBoolean("Perfect")) return false;
        }

        return true;
    }
}
