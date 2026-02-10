package elocindev.tierify.forge.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ApexRollCounterEffect extends MobEffect {

    public ApexRollCounterEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xB2E2FF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
