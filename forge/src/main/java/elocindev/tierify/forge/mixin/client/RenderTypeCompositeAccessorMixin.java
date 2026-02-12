package elocindev.tierify.forge.mixin.client;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public interface RenderTypeCompositeAccessorMixin {
    @Accessor("state")
    RenderType.CompositeState tierify$getState();
}
