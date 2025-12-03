package elocindev.tierify.screen.client;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class PerfectLabelAnimator {

    private static final String WORD = "✯Perfect✯";

    // Total cycle: 8 seconds
    private static final float TOTAL_PERIOD_MS = 8000.0f;

    // 5 tiers: Rare, Epic, Legendary, Mythic, Perfect
    private static final int TIER_COUNT = 5;
    private static final float TIER_SLOT_FRACTION = 1.0f / TIER_COUNT; // 0.2
    private static final float TIER_DURATION_MS = TOTAL_PERIOD_MS * TIER_SLOT_FRACTION; // 1600ms

    // Crossfade length: 0.25s per tier window
    private static final float CROSSFADE_MS = 250.0f;
    private static final float CROSSFADE_FRACTION = CROSSFADE_MS / TIER_DURATION_MS; // ~0.15625

    // How much the gradient shifts per character 
    private static final float CHAR_WAVE_SPACING = 0.12f;

    // Rare deep blue → cyan pulse
    private static final int[] RARE_COLORS = new int[]{
            rgb(80, 150, 255),
            rgb(0, 60, 160),
            rgb(120, 220, 255)
    };

    // Epic purple / magenta wave
    private static final int[] EPIC_COLORS = new int[]{
            rgb(180, 70, 255),
            rgb(100, 0, 180),
            rgb(230, 150, 255)
    };

    // Legendary hot gold → amber
    private static final int[] LEGENDARY_COLORS = new int[]{
            rgb(255, 180, 0),
            rgb(255, 220, 80),
            rgb(255, 140, 0)
    };

    // Mythic crimson → eldritch magenta
    private static final int[] MYTHIC_COLORS = new int[]{
            rgb(255, 60, 60),
            rgb(180, 0, 80),
            rgb(255, 120, 180)
    };

    // Perfect: deep gold → white → deep cyan
    private static final int[] PERFECT_COLORS = new int[]{
            rgb(224, 164, 20),   // deep gold
            rgb(255, 255, 255),  // white
            rgb(0, 183, 255)     // deep cyan
    };

    // Constant divine star color (gold/white blend)
    private static final int STAR_COLOR = rgb(245, 225, 160);

    public static void clientTick() {
        // no-op; uses System.currentTimeMillis()
    }

    public static MutableText getPerfectLabel() {
        if (WORD == null || WORD.isEmpty()) {
            return Text.empty();
        }

        int length = WORD.length();
        MutableText result = Text.empty();

        long now = System.currentTimeMillis();
        float cyclePhase = (TOTAL_PERIOD_MS <= 0.0f)
                ? 0.0f
                : (now % (long) TOTAL_PERIOD_MS) / TOTAL_PERIOD_MS; // 0..1

        // Determine which tier slot we're in (0..4) and local phase (0..1 within that tier)
        int tierIndex = (int) (cyclePhase / TIER_SLOT_FRACTION);
        if (tierIndex >= TIER_COUNT) {
            tierIndex = TIER_COUNT - 1;
        }
        float tierStart = tierIndex * TIER_SLOT_FRACTION;
        float tierLocalPhase = (cyclePhase - tierStart) / TIER_SLOT_FRACTION; // 0..1

        // Precompute crossfade info
        // Default: use current tier only
        int primaryTier = tierIndex;
        Integer secondaryTier = null; // previous or next, for crossfade
        float secondaryWeight = 0.0f; // how much of secondary tier is mixed in
        float primaryWeight = 1.0f;

        if (tierLocalPhase < CROSSFADE_FRACTION) {
            // Fade in from previous tier -> current tier
            secondaryTier = (tierIndex - 1 + TIER_COUNT) % TIER_COUNT;
            float t = tierLocalPhase / CROSSFADE_FRACTION;
            secondaryWeight = clamp01(1.0f - t);
            primaryWeight = clamp01(t);
        } else if (tierLocalPhase > 1.0f - CROSSFADE_FRACTION) {
            // Fade out from current tier -> next tier
            secondaryTier = (tierIndex + 1) % TIER_COUNT;
            float t = (tierLocalPhase - (1.0f - CROSSFADE_FRACTION)) / CROSSFADE_FRACTION;
            primaryWeight = clamp01(1.0f - t);
            secondaryWeight = clamp01(t);
        }

        // Slow drift of gradient within the tier
        // (0..1 over the 1.6s tier window)
        float tierDrift = tierLocalPhase; // slow, smooth

        for (int i = 0; i < length; i++) {
            char c = WORD.charAt(i);

            if (Character.isWhitespace(c)) {
                result.append(Text.literal(String.valueOf(c)));
                continue;
            }

            boolean isStar = (i == 0 || i == length - 1) && (c == '✯');

            int rgb;

            if (isStar) {
                // Stars: constant divine color, no gradient, no tier cycling
                rgb = STAR_COLOR;
            } else {
                // Compute a per-character phase that drifts slowly across the word
                float charPhase = (tierDrift + i * CHAR_WAVE_SPACING) % 1.0f;
                if (charPhase < 0.0f) charPhase += 1.0f;

                // Color from primary tier
                int primaryColor = getTierGradientColor(primaryTier, charPhase);

                if (secondaryTier != null && secondaryWeight > 0.0f) {
                    int secondaryColor = getTierGradientColor(secondaryTier, charPhase);
                    rgb = mixColor(primaryColor, secondaryColor, secondaryWeight);
                } else {
                    rgb = primaryColor;
                }
            }

            Style style = Style.EMPTY
                    .withColor(TextColor.fromRgb(rgb))
                    .withBold(!isStar); // letters bold, stars NOT bold

            result.append(Text.literal(String.valueOf(c)).setStyle(style));
        }

        return result;
    }

    // Get the color for a given tier and phase (0..1) across the word
    private static int getTierGradientColor(int tierIndex, float t) {
        t = wrap01(t);

        int[] stops;
        switch (tierIndex) {
            case 0: // Rare
                stops = RARE_COLORS;
                break;
            case 1: // Epic
                stops = EPIC_COLORS;
                break;
            case 2: // Legendary
                stops = LEGENDARY_COLORS;
                break;
            case 3: // Mythic
                stops = MYTHIC_COLORS;
                break;
            case 4: // Perfect
            default:
                stops = PERFECT_COLORS;
                break;
        }

        // 3-stop gradient: c0 -> c1 -> c2
        int c0 = stops[0];
        int c1 = stops[1];
        int c2 = stops[2];

        if (t < 0.5f) {
            float local = t * 2.0f; // 0..1
            return mixColor(c0, c1, local);
        } else {
            float local = (t - 0.5f) * 2.0f; // 0..1
            return mixColor(c1, c2, local);
        }
    }

    // helpers

    private static int rgb(int r, int g, int b) {
        r &= 0xFF;
        g &= 0xFF;
        b &= 0xFF;
        return (r << 16) | (g << 8) | b;
    }

    private static int mixColor(int c1, int c2, float t) {
        t = clamp01(t);

        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (r << 16) | (g << 8) | b;
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

