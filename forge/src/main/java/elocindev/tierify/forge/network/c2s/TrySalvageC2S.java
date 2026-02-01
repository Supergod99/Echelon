package elocindev.tierify.forge.network.c2s;

import elocindev.tierify.forge.screen.SalvageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TrySalvageC2S {

    public TrySalvageC2S() {}

    public static void encode(TrySalvageC2S msg, FriendlyByteBuf buf) {
        // no payload
    }

    public static TrySalvageC2S decode(FriendlyByteBuf buf) {
        return new TrySalvageC2S();
    }

    public static void handle(TrySalvageC2S msg, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ServerPlayer sp = ctx.getSender();
        ctx.enqueueWork(() -> {
            if (sp == null) return;
            if (sp.containerMenu instanceof SalvageMenu menu) {
                menu.doSalvage(sp);
            }
        });
        ctx.setPacketHandled(true);
    }
}
