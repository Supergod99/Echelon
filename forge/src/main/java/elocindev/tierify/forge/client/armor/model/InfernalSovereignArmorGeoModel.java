package elocindev.tierify.forge.client.armor.model;

import elocindev.tierify.TierifyCommon;
import elocindev.tierify.forge.item.armor.InfernalSovereignArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class InfernalSovereignArmorGeoModel extends GeoModel<InfernalSovereignArmorItem> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "geo/armor/infernal_sovereign.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "textures/armor/infernal_sovereign.png");
    private static final ResourceLocation ANIMATION =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "animations/armor/infernal_sovereign.animation.json");

    @Override
    public ResourceLocation getModelResource(InfernalSovereignArmorItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(InfernalSovereignArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(InfernalSovereignArmorItem animatable) {
        return ANIMATION;
    }
}
