package elocindev.tierify.forge.network.c2s;

import elocindev.tierify.forge.apex.ApexActiveEffects;
import elocindev.tierify.forge.apex.ApexEffect;
import elocindev.tierify.forge.apex.ApexEffectRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class ApexActiveEffectC2S {

    public enum ApexActiveSlot {
        ARMOR,
        MAINHAND,
        OFFHAND
    }

    private final ApexActiveSlot slot;

    public ApexActiveEffectC2S(ApexActiveSlot slot) {
        this.slot = slot;
    }

    public static void encode(ApexActiveEffectC2S msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.slot);
    }

    public static ApexActiveEffectC2S decode(FriendlyByteBuf buf) {
        return new ApexActiveEffectC2S(buf.readEnum(ApexActiveSlot.class));
    }

    public static void handle(ApexActiveEffectC2S msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            switch (msg.slot) {
                case ARMOR -> tryActivateArmor(player);
                case MAINHAND -> tryActivateHand(player, EquipmentSlot.MAINHAND);
                case OFFHAND -> tryActivateHand(player, EquipmentSlot.OFFHAND);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void tryActivateArmor(ServerPlayer player) {
        ResourceLocation reforgeId = ApexActiveEffects.getMatchingApexArmorSetReforge(player);
        if (reforgeId == null) return;

        ApexEffect effect = ApexEffectRegistry.get(reforgeId);
        if (effect == null || effect.triggerType() != ApexEffect.ApexTriggerType.ACTIVE_USE) return;

        ItemStack sample = player.getItemBySlot(EquipmentSlot.CHEST);
        effect.handler().apply(new ApexEffect.ApexEffectContext(
                player,
                sample,
                player.level(),
                null
        ));
    }

    private static void tryActivateHand(ServerPlayer player, EquipmentSlot slot) {
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.isEmpty()) return;
        ApexEffect effect = ApexEffectRegistry.resolveFor(stack);
        if (effect == null || effect.triggerType() != ApexEffect.ApexTriggerType.ACTIVE_USE) return;

        effect.handler().apply(new ApexEffect.ApexEffectContext(
                player,
                stack,
                player.level(),
                null
        ));
    }
}
