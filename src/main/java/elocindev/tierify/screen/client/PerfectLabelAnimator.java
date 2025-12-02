package elocindev.tierify.screen.client;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class PerfectLabelAnimator {

    private static final String WORD = "PERFECT";

    // Smooth, slow cycle (~4 seconds)
    private static final float PERIOD_MS = 4000.0f;
    // How far out of phase each letter is
    private static final float WAVE_SPACING = 0.12f;

    // Aqua-ish base hue & warp
    private static final float BASE_HUE = 0.52f;            // ~teal/aqua
    private static final float HUE_WARP_AMPLITUDE = 0.03f;  // small hue wobble

    // Brightness “breathing”
    private static final float MIN_VALUE = 0.6f;
    private static final float MAX_VALUE = 1.0f;
    private static final float SATURATION = 0.9f;

    // Kept for compatibility; no logic needed here now
    public static void clientTick() {
        // no-op; animation uses System.currentTimeMillis()
    }

    
     // per-letter RGB gradient

    public static MutableText getPerfectLabel() {
        String word = WORD;
        if (word == null || word.isEmpty()) {
            return Text.empty();
        }

        MutableText result = Text.empty();
        int length = word.length();

        long now = System.currentTimeMillis();
        float phase = (PERIOD_MS <= 0.0f)
                ? 0.0f
                : (now % (long) PERIOD_MS) / PERIOD_MS; // 0..1

        // Rare flash frame near the start of each cycle
        boolean flashFrame = phase < 0.04f;

        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);

            if (Character.isWhitespace(c)) {
                result.append(Text.literal(String.valueOf(c)));
                continue;
            }

            // Per-letter offset to create a ripple
            float localPhase = (phase + i * WAVE_SPACING) % 1.0f;

            // Smooth brightness pulse 
            float pulse = 0.5f - 0.5f * (float) Math.cos(2.0 * Math.PI * localPhase);
            float value = MIN_VALUE + (MAX_VALUE - MIN_VALUE) * pulse;

            // Gentle hue warp over time
            float hue = BASE_HUE + HUE_WARP_AMPLITUDE *
                    (float) Math.sin(2.0 * Math.PI * phase);

            float saturation = SATURATION;

            if (flashFrame) {
                value = 1.0f;
                saturation = 1.0f;
            }

            int rgb = hsvToRgb(hue, saturation, value);

            Style style = Style.EMPTY
                    .withColor(TextColor.fromRgb(rgb))
                    .withBold(true); // PERFECT is always bold

            result.append(Text.literal(String.valueOf(c)).setStyle(style));
        }

        return result;
    }


    private static int hsvToRgb(float h, float s, float v) {
        h = wrap01(h);
        s = clamp01(s);
        v = clamp01(v);

        float c = v * s;
        float hp = h * 6.0f;
        float x = c * (1.0f - Math.abs(hp % 2.0f - 1.0f));

        float r1, g1, b1;
        if (hp < 1.0f) {
            r1 = c; g1 = x; b1 = 0f;
        } else if (hp < 2.0f) {
            r1 = x; g1 = c; b1 = 0f;
        } else if (hp < 3.0f) {
            r1 = 0f; g1 = c; b1 = x;
        } else if (hp < 4.0f) {
            r1 = 0f; g1 = x; b1 = c;
        } else if (hp < 5.0f) {
            r1 = x; g1 = 0f; b1 = c;
        } else {
            r1 = c; g1 = 0f; b1 = x;
        }

        float m = v - c;
        int r = (int) ((r1 + m) * 255.0f + 0.5f);
        int g = (int) ((g1 + m) * 255.0f + 0.5f);
        int b = (int) ((b1 + m) * 255.0f + 0.5f);

        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    private static float wrap01(float v) {
        v = v % 1.0f;
        if (v < 0f) v += 1.0f;
        return v;
    }
}

