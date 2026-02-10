package elocindev.tierify.forge.compat;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.screen.client.SalvageScreen;
import elocindev.tierify.forge.screen.client.SalvageUpgradeScreen;
import net.minecraft.client.renderer.Rect2i;

@EmiEntrypoint
public final class TierifyEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(SalvageScreen.class, (screen, consumer) -> {
            if (!ForgeTierifyConfig.jeiReserveExtraAreas()) return;
            for (Rect2i rect : screen.getJeiExtraAreas()) {
                consumer.accept(new Bounds(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
            }
        });

        registry.addExclusionArea(SalvageUpgradeScreen.class, (screen, consumer) -> {
            if (!ForgeTierifyConfig.jeiReserveExtraAreas()) return;
            for (Rect2i rect : screen.getJeiExtraAreas()) {
                consumer.accept(new Bounds(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
            }
        });
    }
}
