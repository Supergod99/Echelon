package elocindev.tierify.forge.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

/**
 * Forge port of Fabric's PerfectLabelAnimator.
 *
 * Goals:
 *  - mathematically / palette-identical to Fabric
 *  - stars use their own pulsing base color (not tier gradients)
 *  - letters remain bold; stars are not bold
 */
public final class PerfectLabelAnimatorForge {
    private PerfectLabelAnimatorForge() {}

    private static final String WORD = "\u272fPerfect\u272f";
    private static final String WORD_TEXT = "Perfect";

    private static final float TOTAL_PERIOD_MS = 8000.0f;
    private static final double PERFECT_SWEEP_RATE_MS = 45.0;
    private static final double PERFECT_SHEEN_PERIOD_MS = 2200.0;
    private static final float PERFECT_SHEEN_STRENGTH = 0.08f;
    private static final float PERFECT_GRADIENT_SOFTEN = 0.012f;

    // Perfect
    private static final int[][] PERFECT_FROST_PRISM = new int[][]{
        // icy cyan ramp (cool, clean)
        {195, 250, 255},
        {180, 245, 255},
        {170, 240, 255},
        {160, 235, 255},
        {150, 230, 255},
        {145, 225, 255},
        {140, 220, 255},
        {138, 216, 255},

        // mist -> near-white
        {165, 232, 255},
        {185, 242, 255},
        {205, 248, 255},
        {225, 252, 255},
        {245, 254, 255},
        {255, 255, 255},
        {248, 255, 255},
        {235, 255, 255},

        // soft cyan return (slightly bluer)
        {215, 255, 255},
        {205, 255, 255},
        {195, 250, 255} // loop-friendly end (matches start)
    };

    private static final int STAR_BASE_COLOR = rgb(212, 240, 255); // #D4F0FF

    private static final float STAR_PULSE_MIN = 0.6f;
    private static final float STAR_PULSE_MAX = 1.6f;

    public static MutableComponent animatedLabel(long nowMs) {
        return animatedLabelInternal(nowMs, WORD, true);
    }

    public static MutableComponent animatedText(long nowMs) {
        return animatedLabelInternal(nowMs, WORD_TEXT, false);
    }

    private static MutableComponent animatedLabelInternal(long nowMs, String word, boolean includeStars) {
        long t = (nowMs > 0L) ? nowMs : System.currentTimeMillis();

        if (word == null || word.isEmpty()) {
            return Component.empty();
        }

        int length = word.length();
        MutableComponent result = Component.empty();

        float cyclePhase = (TOTAL_PERIOD_MS <= 0.0f)
                ? 0.0f
                : (t % (long) TOTAL_PERIOD_MS) / TOTAL_PERIOD_MS;

        double timeOffset = (PERFECT_SWEEP_RATE_MS <= 0.0)
                ? 0.0
                : (t / PERFECT_SWEEP_RATE_MS) % 100.0;

        float sheenPhase = (PERFECT_SHEEN_PERIOD_MS <= 0.0)
                ? 0.0f
                : (float) ((t % PERFECT_SHEEN_PERIOD_MS) / PERFECT_SHEEN_PERIOD_MS);
        float sheenPulse = 0.5f - 0.5f * (float) Math.cos(2.0 * Math.PI * sheenPhase);

        // Star pulse
        float starPulse = 0.5f - 0.5f * (float) Math.cos(2.0 * Math.PI * cyclePhase);
        float starLum = STAR_PULSE_MIN + (STAR_PULSE_MAX - STAR_PULSE_MIN) * starPulse;

        char starChar = word.charAt(0);

        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);

            boolean isStar = includeStars && (i == 0 || i == length - 1) && c == starChar;

            int rgb;

            if (isStar) {
                rgb = scaleColor(STAR_BASE_COLOR, starLum);
            } else {
                double basePos = (length <= 1) ? 50.0 : (i * (100.0 / (length - 1)));
                float charPhase = (float) ((basePos + timeOffset) % 100.0) / 100.0f;
                int base = getColorFromGradient(charPhase, PERFECT_FROST_PRISM);
                int ahead = getColorFromGradient(wrap01(charPhase + PERFECT_GRADIENT_SOFTEN), PERFECT_FROST_PRISM);
                int behind = getColorFromGradient(wrap01(charPhase - PERFECT_GRADIENT_SOFTEN), PERFECT_FROST_PRISM);
                int smoothed = mixColor(mixColor(base, ahead, 0.5f), behind, 0.5f);
                rgb = mixColor(smoothed, 0xFFFFFF, PERFECT_SHEEN_STRENGTH * sheenPulse);
            }

            Style style = Style.EMPTY
                    .withColor(TextColor.fromRgb(rgb))
                    .withBold(true)
                    .withItalic(false);

            result.append(Component.literal(String.valueOf(c)).setStyle(style));
        }

        return result;
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private static int rgb(int r, int g, int b) {
        r &= 0xFF;
        g &= 0xFF;
        b &= 0xFF;
        return (r << 16) | (g << 8) | b;
    }

    private static int scaleColor(int color, float lum) {
        int r = (int) (clamp01(((color >> 16) & 0xFF) * lum / 255f) * 255);
        int g = (int) (clamp01(((color >> 8) & 0xFF) * lum / 255f) * 255);
        int b = (int) (clamp01((color & 0xFF) * lum / 255f) * 255);
        return rgb(r, g, b);
    }

    private static int mixColor(int a, int b, float t) {
        t = clamp01(t);

        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;

        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        int r = Math.round(ar + (br - ar) * t);
        int g = Math.round(ag + (bg - ag) * t);
        int bl = Math.round(ab + (bb - ab) * t);

        return (r << 16) | (g << 8) | bl;
    }

    private static int getColorFromGradient(float t, int[][] colors) {
        if (colors == null || colors.length == 0) {
            return rgb(255, 255, 255);
        }
        if (colors.length == 1) {
            int[] c = colors[0];
            return rgb(c[0], c[1], c[2]);
        }
        t = wrap01(t);
        int lastIndex = colors.length - 1;
        float scaled = t * lastIndex;
        int idx = (int) Math.floor(scaled);
        if (idx < 0) idx = 0;
        if (idx >= lastIndex) idx = lastIndex - 1;
        float localT = scaled - idx;
        localT = localT * localT * (3.0f - 2.0f * localT);

        int[] c1 = colors[idx];
        int[] c2 = colors[idx + 1];

        int r = lerp(c1[0], c2[0], localT);
        int g = lerp(c1[1], c2[1], localT);
        int b = lerp(c1[2], c2[2], localT);

        return rgb(r, g, b);
    }

    private static int lerp(int a, int b, float t) {
        return a + Math.round((b - a) * t);
    }

    private static float wrap01(float v) {
        v %= 1.0f;
        if (v < 0f) v += 1.0f;
        return v;
    }
}
