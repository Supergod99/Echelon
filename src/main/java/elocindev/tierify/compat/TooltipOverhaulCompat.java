package elocindev.tierify.compat;

import elocindev.tierify.mixin.compat.TooltipRendererAccessor;

public class TooltipOverhaulCompat {
    public static void init() {
        try {
            TooltipRendererAccessor.getLayersMain().add(new TierifyBorderLayer());
        } catch (Throwable t) {
            System.out.println("[Tierify] Failed to inject TierifyBorderLayer: " + t.getMessage());
        }
    }
}
