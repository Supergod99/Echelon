package elocindev.tierify.mixin.compat;

import dev.xylonity.tooltipoverhaul.client.style.text.DefaultText;
import elocindev.tierify.screen.client.component.PerfectTierComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.Point;

@Mixin(DefaultText.class)
public class TooltipOverhaulTextMixin {

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            // Standard Yarn Name. Loom will automatically convert this to method_32665 in the refmap.
            target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawText(Lnet/minecraft/client/font/TextRenderer;IILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V"
        )
    )
    private void tierify$centerPerfectLabel(TooltipComponent instance, 
                                            TextRenderer textRenderer, 
                                            int x, int y, 
                                            org.joml.Matrix4f matrix, 
                                            VertexConsumerProvider.Immediate vertexConsumers, 
                                            dev.xylonity.tooltipoverhaul.client.layer.LayerDepth depth, 
                                            dev.xylonity.tooltipoverhaul.client.TooltipContext ctx, 
                                            Vec2f pos, 
                                            Point size) {
        
        int drawX = x;
        
        if (instance instanceof PerfectTierComponent) {
            // size.x is the width of the entire tooltip background
            // pos.x is the left absolute edge
            
            int tooltipWidth = size.x;
            int componentWidth = instance.getWidth(textRenderer);
            
            // Calculate Center relative to the tooltip box
            int absoluteLeft = (int) pos.x;
            
            // Center formula
            drawX = absoluteLeft + (tooltipWidth - componentWidth) / 2;
        }

        instance.drawText(textRenderer, drawX, y, matrix, vertexConsumers);
    }
}
