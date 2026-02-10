package elocindev.tierify.forge.compat;

import elocindev.tierify.TierifyCommon;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.screen.client.SalvageScreen;
import elocindev.tierify.forge.screen.client.SalvageUpgradeScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@JeiPlugin
public final class TierifyJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "jei_plugin");

    private static final IGuiContainerHandler<SalvageScreen> SALVAGE_HANDLER = new IGuiContainerHandler<>() {
        @Override
        public List<Rect2i> getGuiExtraAreas(SalvageScreen screen) {
            if (!ForgeTierifyConfig.jeiReserveExtraAreas()) {
                return List.of();
            }
            return screen.getJeiExtraAreas();
        }
    };

    private static final IGuiContainerHandler<SalvageUpgradeScreen> SALVAGE_UPGRADE_HANDLER = new IGuiContainerHandler<>() {
        @Override
        public List<Rect2i> getGuiExtraAreas(SalvageUpgradeScreen screen) {
            if (!ForgeTierifyConfig.jeiReserveExtraAreas()) {
                return List.of();
            }
            return screen.getJeiExtraAreas();
        }
    };

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SalvageScreen.class, SALVAGE_HANDLER);
        registration.addGuiContainerHandler(SalvageUpgradeScreen.class, SALVAGE_UPGRADE_HANDLER);
    }
}
