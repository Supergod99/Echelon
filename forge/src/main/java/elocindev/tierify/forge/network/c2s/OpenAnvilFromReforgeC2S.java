package elocindev.tierify.forge.network.c2s;

import elocindev.tierify.forge.screen.ReforgeMenu;
import elocindev.tierify.forge.screen.SalvageMenu;
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

public record OpenAnvilFromReforgeC2S() {

    public static void encode(OpenAnvilFromReforgeC2S msg, FriendlyByteBuf buf) {}
    public static OpenAnvilFromReforgeC2S decode(FriendlyByteBuf buf) { return new OpenAnvilFromReforgeC2S(); }

    public static void handle(OpenAnvilFromReforgeC2S msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        ServerPlayer sp = c.getSender();
        if (sp == null) {
            c.setPacketHandled(true);
            return;
        }

        c.enqueueWork(() -> {
            ContainerLevelAccess access;
            if (sp.containerMenu instanceof ReforgeMenu reforgeMenu) {
                access = reforgeMenu.getAccess();
            } else if (sp.containerMenu instanceof SalvageMenu salvageMenu) {
                access = salvageMenu.getAccess();
            } else {
                return;
            }

            AtomicReference<BlockPos> posRef = new AtomicReference<>(BlockPos.ZERO);
            access.execute((level, pos) -> posRef.set(pos));

            NetworkHooks.openScreen(
                sp,
                new SimpleMenuProvider(
                    (id, inv, player) -> new AnvilMenu(id, inv, access),
                    Component.translatable("container.repair")
                ),
                buf -> buf.writeBlockPos(posRef.get())
            );
        });

        c.setPacketHandled(true);
    }
}
