package elocindev.tierify.forge.mixin.client;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(RenderStateShard.TextureStateShard.class)
public interface TextureStateShardAccessorMixin {
    @Accessor("texture")
    Optional<ResourceLocation> tierify$getTexture();
}
