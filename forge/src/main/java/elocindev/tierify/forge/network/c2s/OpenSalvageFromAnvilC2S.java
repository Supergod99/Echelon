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
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class OpenSalvageFromAnvilC2S {

    public OpenSalvageFromAnvilC2S() {}

    public static void encode(OpenSalvageFromAnvilC2S msg, FriendlyByteBuf buf) {
        // no payload
    }

    public static OpenSalvageFromAnvilC2S decode(FriendlyByteBuf buf) {
        return new OpenSalvageFromAnvilC2S();
    }

    public static void handle(OpenSalvageFromAnvilC2S msg, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ServerPlayer sp = ctx.getSender();
        ctx.enqueueWork(() -> {
            if (sp == null) return;

            ContainerLevelAccess access;
            if (sp.containerMenu instanceof ItemCombinerMenu combiner) {
                access = ((ItemCombinerMenuAccessor) combiner).tierify$getAccess();
            } else if (sp.containerMenu instanceof ReforgeMenu reforge) {
                access = reforge.getAccess();
            } else if (sp.containerMenu instanceof SalvageMenu salvage) {
                access = salvage.getAccess();
            } else if (sp.containerMenu instanceof SalvageUpgradeMenu upgrade) {
                access = upgrade.getAccess();
            } else {
                access = ContainerLevelAccess.create(sp.level(), sp.blockPosition());
            }

            AtomicReference<BlockPos> posRef = new AtomicReference<>(null);
            access.execute((level, pos) -> posRef.set(pos));
            BlockPos pos = posRef.get();
            if (pos == null) {
                pos = sp.blockPosition();
            }
            final BlockPos finalPos = pos;

            NetworkHooks.openScreen(
                    sp,
                    new SimpleMenuProvider(
                            (id, inv, player) -> new SalvageMenu(id, inv, access),
                            Component.translatable("screen.tiered.salvaging_screen")
                    ),
                    buf -> buf.writeBlockPos(finalPos)
            );
        });
        ctx.setPacketHandled(true);
    }
}
