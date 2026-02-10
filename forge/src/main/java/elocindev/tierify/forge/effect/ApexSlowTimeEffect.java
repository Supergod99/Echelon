package elocindev.tierify.forge.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ApexSlowTimeEffect extends MobEffect {

    public ApexSlowTimeEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x9FE8FF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
