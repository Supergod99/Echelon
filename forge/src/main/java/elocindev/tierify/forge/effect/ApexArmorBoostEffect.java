package elocindev.tierify.forge.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ApexArmorBoostEffect extends MobEffect {

    public ApexArmorBoostEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xF7C45A);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
