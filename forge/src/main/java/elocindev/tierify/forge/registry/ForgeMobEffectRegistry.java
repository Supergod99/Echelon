package elocindev.tierify.forge.registry;

import elocindev.tierify.TierifyCommon;
import elocindev.tierify.forge.effect.ApexArmorBoostEffect;
import elocindev.tierify.forge.effect.ApexRangedMomentumEffect;
import elocindev.tierify.forge.effect.ApexRollCounterEffect;
import elocindev.tierify.forge.effect.ApexSlowTimeEffect;
import elocindev.tierify.forge.effect.ApexSpellSurgeCooldownEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ForgeMobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TierifyCommon.MODID);

    public static final RegistryObject<MobEffect> APEX_ARMOR_BOOST =
            MOB_EFFECTS.register("apex_armor_boost", ApexArmorBoostEffect::new);
    public static final RegistryObject<MobEffect> APEX_SLOW_TIME =
            MOB_EFFECTS.register("apex_slow_time", ApexSlowTimeEffect::new);
    public static final RegistryObject<MobEffect> APEX_SPELL_SURGE_COOLDOWN =
            MOB_EFFECTS.register("apex_spell_surge_cooldown", ApexSpellSurgeCooldownEffect::new);
    public static final RegistryObject<MobEffect> APEX_ROLL_COUNTER =
            MOB_EFFECTS.register("apex_roll_counter", ApexRollCounterEffect::new);
    public static final RegistryObject<MobEffect> APEX_RANGED_MOMENTUM =
            MOB_EFFECTS.register("apex_ranged_momentum", ApexRangedMomentumEffect::new);

    private ForgeMobEffectRegistry() {}
}
