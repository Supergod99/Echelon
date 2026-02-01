package elocindev.tierify.forge.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public final class ApexEffectGradientAnimatorForge {

    public static final ResourceLocation FONT_MAIN =
            ResourceLocation.fromNamespaceAndPath("tiered", "apex_effect");
    public static final ResourceLocation FONT_SMALL =
            ResourceLocation.fromNamespaceAndPath("tiered", "apex_effect_small");

    private static final int[][] APEX_EFFECT_COLORS = new int[][]{
            // Deep ember shadows (orange-leaning, not brown)
            {  82,  24,   8 },
            {  96,  28,   8 },
            { 112,  34,   9 },
            { 128,  40,  10 },
            { 145,  48,  12 },
            // Volcanic orange ramp
            { 168,  58,  12 },
            { 190,  70,  14 },
            { 210,  84,  16 },
            { 228,  98,  20 },
            // Red heat accents (brief)
            { 242,  86,  46 },  // hot ember red-orange
            { 255, 110,  66 },  // flare (hint of red)
            { 255, 140,  70 },  // back toward gold
            // Core golds
            { 255, 164,  72 },
            { 255, 178,  76 },
            { 255, 192,  84 },
            { 255, 206,  96 },
            { 255, 220, 110 },
            { 255, 232, 128 },
            // Premium pale gold + ivory lift
            { 255, 240, 150 },
            { 255, 246, 176 },
            { 255, 250, 205 },
            { 255, 252, 228 },
            // Specular whites (the “premier” part)
            { 255, 255, 245 },
            { 250, 255, 252 },  // cool rim
            { 255, 255, 245 },
            { 245, 255, 255 },  // airy sparkle
            { 255, 255, 245 },
            // Return down through pale gold smoothly
            { 255, 252, 228 },
            { 255, 248, 205 },
            { 255, 242, 176 },
            { 255, 234, 140 },
            { 255, 224, 112 },
            { 255, 212,  92 },
            // Warm gold -> orange
            { 255, 198,  78 },
            { 255, 184,  70 },
            { 255, 168,  64 },
            { 255, 150,  58 },
            // Second tiny heat pass (keeps it exciting)
            { 255, 128,  62 },
            { 255, 104,  56 },
            { 244,  86,  44 },
            // Back to volcanic orange ramp
            { 228,  98,  20 },
            { 210,  84,  16 },
            { 190,  70,  14 },
            { 168,  58,  12 },
            // Back into ember shadows
            { 145,  48,  12 },
            { 128,  40,  10 },
            { 112,  34,   9 },
            {  96,  28,   8 },
            // Loop-friendly end (matches start exactly)
            {  82,  24,   8 }
    };
    
    private static final int[][] APEX_HINT_COLORS = new int[][]{
            {255, 185,  90},
            {255, 205, 110},
            {255, 225, 135},
            {255, 238, 165},
            {255, 248, 205},
            {255, 255, 245},
            {255, 246, 200},
            {255, 232, 150},
            {255, 212, 115},
            {255, 192,  95}
    };

        private static final int[][] APEX_BODY_COLORS = new int[][]{
            {255, 236, 190},
            {255, 226, 160},
            {255, 214, 130},
            {255, 202, 110},
            {255, 192,  95},
            {255, 210, 125},
            {255, 232, 175},
            {255, 248, 220}
    };

    public static MutableComponent animate(Component base) {
        if (base == null) return Component.empty();

        String raw = base.getString();
        if (raw.isEmpty()) return Component.empty();

        int length = raw.length();
        MutableComponent result = Component.empty();

        long now = System.currentTimeMillis();
        double timeOffset = (now / 65L) % 100.0;

        for (int i = 0; i < length; i++) {
            char c = raw.charAt(i);
            if (Character.isWhitespace(c)) {
                result.append(Component.literal(String.valueOf(c)));
                continue;
            }

            double basePos = (length == 1) ? 50.0 : (i * (100.0 / (length - 1)));
            double animatedPos = (basePos + timeOffset) % 100.0;
            int rgb = getColorFromGradient((int) animatedPos, APEX_EFFECT_COLORS);

            Style style = Style.EMPTY.withColor(TextColor.fromRgb(rgb)).withFont(FONT_MAIN);
            result.append(Component.literal(String.valueOf(c)).setStyle(style));
        }

        return result;
    }

    public static MutableComponent animateHint(Component base) {
        if (base == null) return Component.empty();

        String raw = base.getString();
        if (raw.isEmpty()) return Component.empty();

        int length = raw.length();
        MutableComponent result = Component.empty();

        long now = System.currentTimeMillis();
        // slower than the title for a “premium” feel
        double timeOffset = (now / 95L) % 100.0;

        for (int i = 0; i < length; i++) {
            char c = raw.charAt(i);
            if (Character.isWhitespace(c)) {
                result.append(Component.literal(String.valueOf(c)));
                continue;
            }

            double basePos = (length == 1) ? 50.0 : (i * (100.0 / (length - 1)));
            double animatedPos = (basePos + timeOffset) % 100.0;

            int rgb = getColorFromGradient((int) animatedPos, APEX_HINT_COLORS);

            Style style = Style.EMPTY.withColor(TextColor.fromRgb(rgb)).withFont(FONT_SMALL);
            result.append(Component.literal(String.valueOf(c)).setStyle(style));
        }

        return result;
    }

    public static MutableComponent animateBody(Component base) {
        if (base == null) return Component.empty();

        String raw = base.getString();
        if (raw.isEmpty()) return Component.empty();

        int length = raw.length();
        MutableComponent result = Component.empty();

        long now = System.currentTimeMillis();
        // Slightly slower than the title; a "glint" rather than a marquee.
        double timeOffset = (now / 85L) % 100.0;

        for (int i = 0; i < length; i++) {
        char c = raw.charAt(i);

        double basePos = (length == 1) ? 50.0 : (i * (100.0 / (length - 1)));
        double animatedPos = (basePos + timeOffset) % 100.0;

        int rgb = getColorFromGradient((int) animatedPos, APEX_BODY_COLORS);

        Style style = Style.EMPTY
                .withColor(TextColor.fromRgb(rgb))
                .withFont(FONT_MAIN);

        result.append(Component.literal(String.valueOf(c)).setStyle(style));
    }

        return result;
    }

    public static MutableComponent plain(Component base) {
        if (base == null) return Component.empty();
        return base.copy();
    }

    private static int getColorFromGradient(int percentage, int[][] colors) {
        if (colors == null || colors.length == 0) {
            return rgb(255, 255, 255);
        }
        if (colors.length == 1) {
            int[] c = colors[0];
            return rgb(c[0], c[1], c[2]);
        }

        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;

        int segments = colors.length;
        double segmentLength = 100.0 / segments;

        int segmentIndex = (int) Math.floor(percentage / segmentLength);
        if (segmentIndex >= segments) segmentIndex = segments - 1;

        int nextIndex = (segmentIndex + 1) % segments;
        double localStart = segmentIndex * segmentLength;
        double t = (percentage - localStart) / segmentLength;
        t = t * t * (3.0 - 2.0 * t); // smoothstep (non-linear blend)

        int[] c1 = colors[segmentIndex];
        int[] c2 = colors[nextIndex];

        int r = lerp(c1[0], c2[0], t);
        int g = lerp(c1[1], c2[1], t);
        int b = lerp(c1[2], c2[2], t);

        return rgb(r, g, b);
    }

    private static int lerp(int a, int b, double t) {
        return a + (int) Math.round((b - a) * t);
    }

    private static int rgb(int r, int g, int b) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        return (r << 16) | (g << 8) | b;
    }
}
