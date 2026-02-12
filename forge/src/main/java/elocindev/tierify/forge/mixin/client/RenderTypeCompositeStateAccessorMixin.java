package elocindev.tierify.forge.mixin.client;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeState.class)
public interface RenderTypeCompositeStateAccessorMixin {
    @Accessor("textureState")
    RenderStateShard.EmptyTextureStateShard tierify$getTextureState();
}
