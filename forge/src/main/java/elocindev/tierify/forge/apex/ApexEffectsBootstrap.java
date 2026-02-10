package elocindev.tierify.forge.apex;

import elocindev.tierify.TierifyCommon;
import net.minecraft.resources.ResourceLocation;

public final class ApexEffectsBootstrap {

    private static final int ARMOR_BOOST_TICKS = 6 * 20;
    private static final int ARMOR_BOOST_COOLDOWN_TICKS = 30 * 20;
    private static final int SPELL_COOLDOWN_TICKS = 20 * 20;
    private static final int SLOW_TIME_COOLDOWN_TICKS = 30 * 20;
    private static final int SLOW_TIME_DURATION_TICKS = 8 * 20;
    private static final int NO_DAMAGE_SHIELD_DELAY_TICKS = 12 * 20;
    private static final float NO_DAMAGE_SHIELD_FRACTION = 0.25f;
    private static final ApexEffect.ApexEffectHandler NOOP_HANDLER = ctx -> {};

    private ApexEffectsBootstrap() {}

    public static void init() {
        // Apex tooltip recipe (line2 auto-derives from trigger/counter unless you add a line2 key):
        // - Active cooldown: trigger=ACTIVE_USE, counter=COOLDOWN_TICKS, counterValue=<ticks>
        // - Passive: trigger=PASSIVE_TICK
        // - On hit (every N): trigger=ON_HIT, counter=EVERY_N, counterValue=<N>
        // - On taking damage: trigger=ON_HURT
        // - On bow use: trigger=ON_BOW_USE
        // - On spell use: trigger=ON_SPELL_USE
        // - On block break: trigger=ON_BLOCK_BREAK
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
        ApexEffectRegistry.register(
                ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_2"),
                new ApexEffect(
                        ApexEffect.ApexTriggerType.PASSIVE_TICK,
                        ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                        NO_DAMAGE_SHIELD_DELAY_TICKS,
                        ctx -> ApexActiveEffects.applyNoDamageShield(
                                ctx.player(),
                                NO_DAMAGE_SHIELD_DELAY_TICKS,
                                NO_DAMAGE_SHIELD_FRACTION
                        )
                )
        );
        registerTooltipOnly("mythic_armor_3",
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0);
        registerTooltipOnly("mythic_armor_4",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_5",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_6",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_7",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_8",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_9",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_10",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_11",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_12",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_13",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_14",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_15",
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0);
        registerTooltipOnly("mythic_armor_16",
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0);
        registerTooltipOnly("mythic_armor_17",
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0);
        ApexEffectRegistry.register(
                ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_18"),
                new ApexEffect(
                        ApexEffect.ApexTriggerType.ACTIVE_USE,
                        ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                        SLOW_TIME_COOLDOWN_TICKS,
                        ctx -> ApexActiveEffects.applySlowTimeAura(
                                ctx.player(),
                                SLOW_TIME_DURATION_TICKS,
                                SLOW_TIME_COOLDOWN_TICKS
                        )
                )
        );
        registerTooltipOnly("mythic_armor_19",
                ApexEffect.ApexTriggerType.ON_SPELL_USE,
                ApexEffect.ApexCounterModel.COOLDOWN_TICKS,
                SPELL_COOLDOWN_TICKS);
        registerTooltipOnly("mythic_armor_20",
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0);
        registerTooltipOnly("mythic_armor_21",
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0);
    }

    private static void registerTooltipOnly(String id,
                                            ApexEffect.ApexTriggerType triggerType,
                                            ApexEffect.ApexCounterModel counterModel,
                                            int counterValue) {
        ApexEffectRegistry.register(
                ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, id),
                new ApexEffect(triggerType, counterModel, counterValue, NOOP_HANDLER)
        );
    }
}
