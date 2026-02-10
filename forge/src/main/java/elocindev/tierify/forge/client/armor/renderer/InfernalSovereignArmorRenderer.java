package elocindev.tierify.forge.client.armor.renderer;

import elocindev.tierify.forge.client.armor.model.InfernalSovereignArmorGeoModel;
import elocindev.tierify.forge.item.armor.InfernalSovereignArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class InfernalSovereignArmorRenderer extends GeoArmorRenderer<InfernalSovereignArmorItem> {
    public InfernalSovereignArmorRenderer() {
        super(new InfernalSovereignArmorGeoModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
