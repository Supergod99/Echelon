package elocindev.tierify.forge.mixin.client;

import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontTexture.class)
public interface FontTextureAccessorMixin {
    @Accessor("renderTypes")
    GlyphRenderTypes tierify$getRenderTypes();
}
