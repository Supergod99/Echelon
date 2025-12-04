package elocindev.tierify.screen.client;

import net.minecraft.client.gui.tooltip.TooltipComponent;

/**
 * A unique marker inserted into the tooltip list.
 * We detect this exact class in TieredTooltip and render the Perfect label.
 */
public class PerfectMarkerComponent implements TooltipComponent {
    public static final PerfectMarkerComponent INSTANCE = new PerfectMarkerComponent();
    private PerfectMarkerComponent() {}
}
