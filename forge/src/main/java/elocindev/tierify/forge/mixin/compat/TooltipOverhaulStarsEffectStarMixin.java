package elocindev.tierify.forge.mixin.compat;

import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "dev.xylonity.tooltipoverhaul.client.style.effect.StarsEffect$Star", remap = false)
public class TooltipOverhaulStarsEffectStarMixin {
    private static final float TWINKLE_MIN = 0.05f;
    private static final float TWINKLE_MAX = 1.6f;
    private static final boolean SHIMMER_ENABLED = true;
    private static final float SHIMMER_MIN = 0.7f;
    private static final float SHIMMER_MAX = 1.5f;
    private static final float SIZE_MIN = 0.4f;
    private static final float SIZE_MAX = 1.9f;
    private static final float TWINKLE_SPEED = 0.03f;
    private static final float SHIMMER_SPEED = 0.017f;

    @Shadow private float cx;
    @Shadow private float cy;
    @Shadow private long birth;

    @ModifyArgs(
            method = "updateAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/xylonity/tooltipoverhaul/client/style/effect/StarsEffect;draw(Lorg/joml/Matrix4f;FFFFFFIIII)V"
            )
    )
    private void tierify$twinkleAndShimmer(Args args) {
        long now = Util.getMillis();
        float phase = (float) ((Math.sin((now + birth) * TWINKLE_SPEED + cx * 0.18 + cy * 0.12) + 1.0) * 0.5);
        float twinkle = TWINKLE_MIN + (TWINKLE_MAX - TWINKLE_MIN) * phase;

        int alpha = (int) args.get(10);
        args.set(10, clamp(Math.round(alpha * twinkle)));

        float sizeScale = SIZE_MIN + (SIZE_MAX - SIZE_MIN) * phase;
        float tStart = ((Number) args.get(5)).floatValue();
        float tEnd = ((Number) args.get(6)).floatValue();
        args.set(5, tStart * sizeScale);
        args.set(6, tEnd * sizeScale);

        if (!SHIMMER_ENABLED) return;

        float shimmerPhase = (float) ((Math.sin((now + birth) * SHIMMER_SPEED + cx * 0.07) + 1.0) * 0.5);
        float shimmer = SHIMMER_MIN + (SHIMMER_MAX - SHIMMER_MIN) * shimmerPhase;
        int r = (int) args.get(7);
        int g = (int) args.get(8);
        int b = (int) args.get(9);
        float boost = 18.0f * shimmerPhase;
        args.set(7, clamp(Math.round(r * shimmer + boost)));
        args.set(8, clamp(Math.round(g * shimmer + boost * 0.8f)));
        args.set(9, clamp(Math.round(b * shimmer + boost * 0.6f)));
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
