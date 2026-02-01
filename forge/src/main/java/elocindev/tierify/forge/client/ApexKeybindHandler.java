package elocindev.tierify.forge.client;

import elocindev.tierify.forge.network.ForgeNetwork;
import elocindev.tierify.forge.network.c2s.ApexActiveEffectC2S;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ApexKeybindHandler {

    private ApexKeybindHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().player == null) return;

        if (ApexKeybinds.ARMOR_ACTIVE.consumeClick()) {
            ForgeNetwork.CHANNEL.sendToServer(new ApexActiveEffectC2S(ApexActiveEffectC2S.ApexActiveSlot.ARMOR));
        }
        if (ApexKeybinds.MAINHAND_ACTIVE.consumeClick()) {
            ForgeNetwork.CHANNEL.sendToServer(new ApexActiveEffectC2S(ApexActiveEffectC2S.ApexActiveSlot.MAINHAND));
        }
        if (ApexKeybinds.OFFHAND_ACTIVE.consumeClick()) {
            ForgeNetwork.CHANNEL.sendToServer(new ApexActiveEffectC2S(ApexActiveEffectC2S.ApexActiveSlot.OFFHAND));
        }
    }
}
