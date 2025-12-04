package elocindev.tierify.screen.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;


public class PerfectMarkerComponent implements TooltipComponent {

    public static final PerfectMarkerComponent INSTANCE = new PerfectMarkerComponent();

    private PerfectMarkerComponent() {
    }

    @Override
    public int getHeight() {

        return 10; // ≈ vanilla line height (9) + 1px padding
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {

        return 0;
    }
}
