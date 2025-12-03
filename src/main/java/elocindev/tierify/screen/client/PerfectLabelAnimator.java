package elocindev.tierify.screen.client;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class PerfectLabelAnimator {

    // New label text: symmetrical cosmic brackets
    private static final String WORD = "✯Perfect✯";

    // Global breathing / hue cycle (~4 seconds)
    private static final float PERIOD_MS = 4000.0f;
    // Per-letter phase offset to create a wave
    private static final float WAVE_SPACING = 0.12f;

    // Sweeping highlight streak (~2.6 seconds per pass)
    private static final float STREAK_PERIOD_MS = 2600.0f;
    private static final float STREAK_WIDTH = 0.28f;   // how wide the beam is
    private static final float STREAK_INTENSITY = 0.45f;

    // Synchronized starburst pulse (~2.5 seconds)
    private static final float STARBURST_PERIOD_MS = 2500.0f;
    // How strong the pulse gets at peak
    private static final float STARBURST_PULSE_INTENSITY = 0.60f;

    // Base vertical "pillar of radiance" strength
    private static final float VERTICAL_OVERLAY_STRENGTH = 0.22f;

    // Permanent halo for stars (on top of all other effects)
    private static final float STAR_HALO_BASE = 0.35f;
    private static final float STAR_HALO_EXTRA_FROM_PULSE = 0.50f;

    // Breathing brightness (bloom-ish)
    private static final float BLOOM_INTENSITY = 0.55f; // how much we push toward white at peak

    // We keep these HSV values as a mild global hue drift; most color comes from tri-gradient
    private static final float BASE_HUE = 0.12f;            // golden-ish base
    private static final float HUE_WARP_AMPLITUDE = 0.02f;
    private static final float SATURATION = 0.9f;
    private static final float MIN_VALUE = 0.75f;
    private static final float MAX_VALUE = 1.00f;

    // Core tri-color gradient for "pinnacle" look
    private static final int GOLD_COLOR = 0xFFE55C;   // bright stellar gold
    private static final int WHITE_COLOR = 0xFFFFFF;  // white flame
    private static final int CYAN_COLOR = 0x8FD7FF;   // cyan starfall

    public static void clientTick() {
        // no-op; animation uses System.currentTimeMillis()
    }

    public static MutableText getPerfectLabel() {
        String word = WORD;
        if (word == null || word.isEmpty()) {
            return Text.empty();
        }

        MutableText result = Text.empty();
        int length = word.length();
        if (length <= 0) {
            return result;
        }

        long now = System.currentTimeMillis();

        // Global 0..1 phase for breathing & hue warp
        float phase = (PERIOD_MS <= 0.0f)
                ? 0.0f
                : (now % (long) PERIOD_MS) / PERIOD_MS;

        // Global 0..1 for the sweeping streak
        float streakPhase = (STREAK_PERIOD_MS <= 0.0f)
                ? 0.0f
                : (now % (long) STREAK_PERIOD_MS) / STREAK_PERIOD_MS;

        // Global 0..1 for synchronized starburst pulse
        float starburstPhase = (STARBURST_PERIOD_MS <= 0.0f)
                ? 0.0f
                : (now % (long) STARBURST_PERIOD_MS) / STARBURST_PERIOD_MS;

        // Smooth pulse curve for starburst (0 at min, 1 at peak)
        float starburstPulse = 0.5f - 0.5f * (float) Math.cos(2.0 * Math.PI * starburstPhase);
        // Emphasize the peak a bit more
        starburstPulse = starburstPulse * starburstPulse; // square to sharpen

        // Breathing (global) for bloom-style brightness
        float breathe = 0.5f - 0.5f * (float) Math.cos(2.0 * Math.PI * phase);
        float value = MIN_VALUE + (MAX_VALUE - MIN_VALUE) * breathe;

        // Gentle hue warp (global)
        float hue = BASE_HUE + HUE_WARP_AMPLITUDE
                * (float) Math.sin(2.0 * Math.PI * phase);
        float saturation = SATURATION;

        int globalBaseRgb = hsvToRgb(hue, saturation, value);

        for (int i = 0; i < length; i++) {
            char c = word.charAt(i);

            // Preserve whitespace if ever introduced
            if (Character.isWhitespace(c)) {
                result.append(Text.literal(String.valueOf(c)));
                continue;
            }

            boolean isStar = (i == 0 || i == length - 1) && (c == '✯');

            // Local phase for tri-gradient + subtle wave
            float localPhase = (phase + i * WAVE_SPACING) % 1.0f;
            if (localPhase < 0.0f) {
                localPhase += 1.0f;
            }

            // Base tri-color gradient across time (pinnacle cosmic look)
            int triColor = getTriGradientColor(localPhase);

            // Start from a blend of tri-color and a global golden-ish HSV base
            int rgb = mixColor(triColor, globalBaseRgb, 0.35f);

            // Breathing bloom: push toward white as we breathe
            rgb = mixColor(rgb, WHITE_COLOR, BLOOM_INTENSITY * breathe);

            // Vertical pillar of radiance overlay (center is brightest)
            float verticalFactor;
            if (length == 1) {
                verticalFactor = 1.0f;
            } else {
                verticalFactor = (float) i / (float) (length - 1); // 0..1
            }
            // Map to -1..1 then invert_abs to peak at center
            verticalFactor = (verticalFactor - 0.5f) * 2.0f;
            float verticalCenter = 1.0f - Math.abs(verticalFactor);
            float pillarAmount = verticalCenter * VERTICAL_OVERLAY_STRENGTH;
            rgb = mixColor(rgb, WHITE_COLOR, pillarAmount);

            // Sweeping chromatic highlight streak (comet arc)
            float pos = (length == 1) ? 0.5f : (float) i / (float) (length - 1);
            float dist = circularDistance(pos, streakPhase);
            if (dist < STREAK_WIDTH) {
                float streakT = 1.0f - (dist / STREAK_WIDTH); // 0..1
                float streakAmt = streakT * STREAK_INTENSITY;

                // Streak highlight color follows the same tri gradient as time,
                // but keyed to streakPhase so it shifts chromatically as it passes.
                int streakColor = getTriGradientColor(streakPhase);
                // Blend the highlight color in
                rgb = mixColor(rgb, streakColor, streakAmt);
                // And a touch of extra white for bloom-y edge
                rgb = mixColor(rgb, WHITE_COLOR, streakAmt * 0.35f);
            }

            // Synchronized starburst halo for both stars
            if (isStar) {
                float halo = STAR_HALO_BASE + STAR_HALO_EXTRA_FROM_PULSE * starburstPulse;
                halo = clamp01(halo);

                // First, bias toward gold to make the stars look regal
                rgb = mixColor(rgb, GOLD_COLOR, halo * 0.6f);
                // Then, push toward white for the divine flare
                rgb = mixColor(rgb, WHITE_COLOR, halo);
            }

            Style style = Style.EMPTY
                    .withColor(TextColor.fromRgb(rgb))
                    .withBold(true);

            result.append(Text.literal(String.valueOf(c)).setStyle(style));
        }

        return result;
    }

    /**
     * Returns a tri-phase gradient color cycling:
     * GOLD -> WHITE -> CYAN -> back to GOLD over phase 0..1.
     */
    private static int getTriGradientColor(float t) {
        t = wrap01(t);

        if (t < (1.0f / 3.0f)) {
            // 0.0 .. 0.333: GOLD -> WHITE
            float local = t * 3.0f; // 0..1
            return mixColor(GOLD_COLOR, WHITE_COLOR, local);
        } else if (t < (2.0f / 3.0f)) {
            // 0.333 .. 0.666: WHITE -> CYAN
            float local = (t - 1.0f / 3.0f) * 3.0f; // 0..1
            return mixColor(WHITE_COLOR, CYAN_COLOR, local);
        } else {
            // 0.666 .. 1.0: CYAN -> GOLD
            float local = (t - 2.0f / 3.0f) * 3.0f; // 0..1
            return mixColor(CYAN_COLOR, GOLD_COLOR, local);
        }
    }

    /**
     * Mixes two RGB colors linearly by t (0..1).
     */
    private static int mixColor(int base, int add, float t) {
        t = clamp01(t);

        int r1 = (base >> 16) & 0xFF;
        int g1 = (base >> 8) & 0xFF;
        int b1 = base & 0xFF;

        int r2 = (add >> 16) & 0xFF;
        int g2 = (add >> 8) & 0xFF;
        int b2 = add & 0xFF;

        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Circular distance on [0,1] ring (for streak).
     */
    private static float circularDistance(float a, float b) {
        float d = Math.abs(a - b);
        return Math.min(d, 1.0f - d);
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


