package elocindev.tierify.screen.client;

import net.minecraft.text.Text;

public class PerfectLabelAnimator {

    // Aqua pulse base colors
    private static final int[] COLORS = new int[] {
        0x66E7FF, 
        0x33D4FF,
        0x11C8FF,
        0x00BFFF, 
        0x11C8FF,
        0x33D4FF
    };

    // Characters in PERFECT 
    private static final char[] CHARS = "PERFECT".toCharArray();

    private static int tick = 0;
    private static final int SPEED = 2;   // lower = faster pulse

    public static void clientTick() {
        tick++;
    }

    public static String getPerfectLabel() {

        StringBuilder out = new StringBuilder();

        // Add the two stars before the word
        out.append("§b✯ "); // static star

        // Generate ripple pulse across letters
        for (int i = 0; i < CHARS.length; i++) {

            // where in pulse cycle this letter is
            int phase = (tick / SPEED + i) % COLORS.length;

            int rgb = COLORS[phase];

            // convert 0xRRGGBB → §x§R§R§G§G§B§B
            out.append(toGradient(rgb));

            // bold for perfect
            out.append("§l");

            out.append(CHARS[i]);
        }

        // end with a star
        out.append(" §b✯");

        return out.toString();
    }

    private static String toGradient(int rgb) {
        String hex = String.format("%06X", rgb);
        return "§x§" +
               hex.charAt(0) + "§" +
               hex.charAt(1) + "§" +
               hex.charAt(2) + "§" +
               hex.charAt(3) + "§" +
               hex.charAt(4) + "§" +
               hex.charAt(5);
    }
}
