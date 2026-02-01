package elocindev.tierify.forge.network.c2s;

import elocindev.tierify.forge.screen.SalvageUpgradeMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TrySalvageUpgradeC2S {

    public TrySalvageUpgradeC2S() {}

    public static void encode(TrySalvageUpgradeC2S msg, FriendlyByteBuf buf) {
        // no payload
    }

    public static TrySalvageUpgradeC2S decode(FriendlyByteBuf buf) {
        return new TrySalvageUpgradeC2S();
    }

    public static void handle(TrySalvageUpgradeC2S msg, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ServerPlayer sp = ctx.getSender();
        ctx.enqueueWork(() -> {
            if (sp == null) return;
            if (sp.containerMenu instanceof SalvageUpgradeMenu menu) {
                menu.doUpgrade(sp);
            }
        });
        ctx.setPacketHandled(true);
    }
}
