package elocindev.tierify.forge.network.c2s;

import elocindev.tierify.forge.mixin.ItemCombinerMenuAccessor;
import elocindev.tierify.forge.screen.ReforgeMenu;
import elocindev.tierify.forge.screen.SalvageMenu;
import elocindev.tierify.forge.screen.SalvageUpgradeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class OpenSalvageUpgradeC2S {

    public OpenSalvageUpgradeC2S() {}

    public static void encode(OpenSalvageUpgradeC2S msg, FriendlyByteBuf buf) {
        // no payload
    }

    public static OpenSalvageUpgradeC2S decode(FriendlyByteBuf buf) {
        return new OpenSalvageUpgradeC2S();
    }

    public static void handle(OpenSalvageUpgradeC2S msg, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ServerPlayer sp = ctx.getSender();
        ctx.enqueueWork(() -> {
            if (sp == null) return;

            ContainerLevelAccess access;
            if (sp.containerMenu instanceof AnvilMenu anvil) {
                access = ((ItemCombinerMenuAccessor) anvil).tierify$getAccess();
            } else if (sp.containerMenu instanceof SalvageMenu salvage) {
                access = salvage.getAccess();
            } else if (sp.containerMenu instanceof SalvageUpgradeMenu upgrade) {
                access = upgrade.getAccess();
            } else if (sp.containerMenu instanceof ReforgeMenu reforge) {
                access = reforge.getAccess();
            } else {
                return;
            }

            AtomicReference<BlockPos> posRef = new AtomicReference<>(BlockPos.ZERO);
            access.execute((level, pos) -> posRef.set(pos));

            NetworkHooks.openScreen(
                    sp,
                    new SimpleMenuProvider(
                            (id, inv, player) -> new SalvageUpgradeMenu(id, inv, access),
                            Component.translatable("screen.tiered.salvage_upgrading_screen")
                    ),
                    buf -> buf.writeBlockPos(posRef.get())
            );
        });
        ctx.setPacketHandled(true);
    }
}
