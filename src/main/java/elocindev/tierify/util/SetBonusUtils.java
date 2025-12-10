package elocindev.tierify.util;

import elocindev.tierify.Tierify;
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
}
