package elocindev.tierify.forge.apex;

import elocindev.tierify.util.StarApexUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class ApexActiveEffects {

    private static final String TAG_ARMOR_BOOST_UNTIL = "tierify_apex_armor_boost_until";
    private static final String TAG_ARMOR_BOOST_COOLDOWN_UNTIL = "tierify_apex_armor_boost_cooldown_until";
    private static final String TAG_ARMOR_BOOST_REFORGE = "tierify_apex_armor_boost_reforge";
    private static final UUID ARMOR_BOOST_UUID = UUID.fromString("6c8c7c7f-4f9e-4a36-9bda-3c6e77cc37e6");

    private ApexActiveEffects() {}

    public static ResourceLocation getMatchingApexArmorSetReforge(ServerPlayer player) {
        if (player == null) return null;
        ResourceLocation match = null;
        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !StarApexUtils.isApex(stack)) return null;
            ResourceLocation id = ApexEffectRegistry.getReforgeId(stack);
            if (id == null) return null;
            if (match == null) {
                match = id;
            } else if (!match.equals(id)) {
                return null;
            }
        }
        return match;
    }

    public static void applyArmorBoost(ServerPlayer player,
                                       ResourceLocation reforgeId,
                                       int durationTicks,
                                       int cooldownTicks) {
        if (player == null || reforgeId == null || durationTicks <= 0) return;

        long now = player.level().getGameTime();
        long cooldownUntil = player.getPersistentData().getLong(TAG_ARMOR_BOOST_COOLDOWN_UNTIL);
        if (now < cooldownUntil) return;

        CompoundTag data = player.getPersistentData();
        data.putLong(TAG_ARMOR_BOOST_UNTIL, now + durationTicks);
        data.putLong(TAG_ARMOR_BOOST_COOLDOWN_UNTIL, now + Math.max(cooldownTicks, durationTicks));
        data.putString(TAG_ARMOR_BOOST_REFORGE, reforgeId.toString());

        AttributeInstance attr = player.getAttribute(Attributes.ARMOR);
        if (attr == null) return;
        attr.removeModifier(ARMOR_BOOST_UUID);
        attr.addTransientModifier(new AttributeModifier(
                ARMOR_BOOST_UUID,
                "tierify_apex_armor_boost",
                1.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    public static void tick(ServerPlayer player) {
        if (player == null) return;
        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_ARMOR_BOOST_UNTIL)) return;

        long now = player.level().getGameTime();
        long until = data.getLong(TAG_ARMOR_BOOST_UNTIL);
        if (now >= until) {
            clearArmorBoost(player, data);
        }

        String idStr = data.getString(TAG_ARMOR_BOOST_REFORGE);
        ResourceLocation expected = ResourceLocation.tryParse(idStr);
        if (expected == null) {
            clearArmorBoost(player, data);
            return;
        }

        ResourceLocation current = getMatchingApexArmorSetReforge(player);
        if (current == null || !current.equals(expected)) {
            clearArmorBoost(player, data);
        }
    }

    private static void clearArmorBoost(ServerPlayer player, CompoundTag data) {
        data.remove(TAG_ARMOR_BOOST_UNTIL);
        data.remove(TAG_ARMOR_BOOST_REFORGE);
        AttributeInstance attr = player.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_BOOST_UUID);
        }
        long now = player.level().getGameTime();
        long cooldownUntil = data.getLong(TAG_ARMOR_BOOST_COOLDOWN_UNTIL);
        if (cooldownUntil <= now) {
            data.remove(TAG_ARMOR_BOOST_COOLDOWN_UNTIL);
        }
    }
}
