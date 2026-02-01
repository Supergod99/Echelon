package elocindev.tierify.forge.apex;

import elocindev.tierify.TierifyCommon;
import net.minecraft.resources.ResourceLocation;

public final class ApexEffectsBootstrap {

    private static final int ARMOR_BOOST_TICKS = 10 * 20;
    private static final int ARMOR_BOOST_COOLDOWN_TICKS = 60 * 20;

    private ApexEffectsBootstrap() {}

    public static void init() {
        ApexEffectRegistry.register(
                ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_1"),
                new ApexEffect(
                        ApexEffect.ApexTriggerType.ACTIVE_USE,
                        ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                        ARMOR_BOOST_COOLDOWN_TICKS,
                        ctx -> ApexActiveEffects.applyArmorBoost(
                                ctx.player(),
                                ApexEffectRegistry.getReforgeId(ctx.stack()),
                                ARMOR_BOOST_TICKS,
                                ARMOR_BOOST_COOLDOWN_TICKS
                        )
                )
        );
    }
}
