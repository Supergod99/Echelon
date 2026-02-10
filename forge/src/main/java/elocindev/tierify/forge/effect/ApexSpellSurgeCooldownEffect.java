package elocindev.tierify.forge.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ApexSpellSurgeCooldownEffect extends MobEffect {

    public ApexSpellSurgeCooldownEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x7CCBFF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}

