package elocindev.tierify.forge.network;

import elocindev.tierify.TierifyCommon;
import elocindev.tierify.forge.network.c2s.OpenAnvilFromReforgeC2S;
import elocindev.tierify.forge.network.c2s.OpenReforgeFromAnvilC2S;
import elocindev.tierify.forge.network.c2s.OpenSalvageFromAnvilC2S;
import elocindev.tierify.forge.network.c2s.OpenSalvageUpgradeC2S;
import elocindev.tierify.forge.network.c2s.ApexActiveEffectC2S;
import elocindev.tierify.forge.network.c2s.TryReforgeC2S;
import elocindev.tierify.forge.network.c2s.TrySalvageC2S;
import elocindev.tierify.forge.network.c2s.TrySalvageUpgradeC2S;
import elocindev.tierify.forge.network.s2c.AttributeSyncS2C;
import elocindev.tierify.forge.network.s2c.ConfigSyncS2C;
import elocindev.tierify.forge.network.s2c.ReforgeItemsSyncS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ForgeNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "net"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    public static void init() {
        int id = 0;

        CHANNEL.messageBuilder(OpenReforgeFromAnvilC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenReforgeFromAnvilC2S::encode)
                .decoder(OpenReforgeFromAnvilC2S::decode)
                .consumerMainThread(OpenReforgeFromAnvilC2S::handle)
                .add();

        CHANNEL.messageBuilder(OpenAnvilFromReforgeC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenAnvilFromReforgeC2S::encode)
                .decoder(OpenAnvilFromReforgeC2S::decode)
                .consumerMainThread(OpenAnvilFromReforgeC2S::handle)
                .add();

        CHANNEL.messageBuilder(OpenSalvageFromAnvilC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenSalvageFromAnvilC2S::encode)
                .decoder(OpenSalvageFromAnvilC2S::decode)
                .consumerMainThread(OpenSalvageFromAnvilC2S::handle)
                .add();

        CHANNEL.messageBuilder(OpenSalvageUpgradeC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenSalvageUpgradeC2S::encode)
                .decoder(OpenSalvageUpgradeC2S::decode)
                .consumerMainThread(OpenSalvageUpgradeC2S::handle)
                .add();

        CHANNEL.messageBuilder(ApexActiveEffectC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ApexActiveEffectC2S::encode)
                .decoder(ApexActiveEffectC2S::decode)
                .consumerMainThread(ApexActiveEffectC2S::handle)
                .add();

        CHANNEL.messageBuilder(TryReforgeC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TryReforgeC2S::encode)
                .decoder(TryReforgeC2S::decode)
                .consumerMainThread(TryReforgeC2S::handle)
                .add();

        CHANNEL.messageBuilder(TrySalvageC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TrySalvageC2S::encode)
                .decoder(TrySalvageC2S::decode)
                .consumerMainThread(TrySalvageC2S::handle)
                .add();

        CHANNEL.messageBuilder(TrySalvageUpgradeC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TrySalvageUpgradeC2S::encode)
                .decoder(TrySalvageUpgradeC2S::decode)
                .consumerMainThread(TrySalvageUpgradeC2S::handle)
                .add();

        CHANNEL.messageBuilder(AttributeSyncS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(AttributeSyncS2C::encode)
                .decoder(AttributeSyncS2C::decode)
                .consumerMainThread(AttributeSyncS2C::handle)
                .add();

        CHANNEL.messageBuilder(ReforgeItemsSyncS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ReforgeItemsSyncS2C::encode)
                .decoder(ReforgeItemsSyncS2C::decode)
                .consumerMainThread(ReforgeItemsSyncS2C::handle)
                .add();

        CHANNEL.messageBuilder(ConfigSyncS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ConfigSyncS2C::encode)
                .decoder(ConfigSyncS2C::decode)
                .consumerMainThread(ConfigSyncS2C::handle)
                .add();
    }

    private ForgeNetwork() {}
}
