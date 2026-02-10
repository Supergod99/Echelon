package elocindev.tierify.forge.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ApexRangedMomentumEffect extends MobEffect {

    public ApexRangedMomentumEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x63D3FF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}

