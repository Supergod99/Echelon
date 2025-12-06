package elocindev.tierify.mixin.compat;

import dev.xylonity.tooltipoverhaul.client.style.text.DefaultText;
import dev.xylonity.tooltipoverhaul.client.TooltipContext;
import dev.xylonity.tooltipoverhaul.client.layer.LayerDepth;
import elocindev.tierify.screen.client.component.PerfectTierComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec2f;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.Point;

@Mixin(DefaultText.class)
public class TooltipOverhaulTextMixin {

    // Targetting: component.drawText(font, x, y, matrix, vertexConsumers);
    // FIX: The signature must use the interface 'VertexConsumerProvider', not 'VertexConsumerProvider$Immediate'
    @Redirect(
        method = "render(Ldev/xylonity/tooltipoverhaul/client/layer/LayerDepth;Ldev/xylonity/tooltipoverhaul/client/TooltipContext;Lnet/minecraft/util/math/Vec2f;Ljava/awt/Point;Lnet/minecraft/text/Text;Lnet/minecraft/client/font/TextRenderer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawText(Lnet/minecraft/client/font/TextRenderer;IILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;)V"
        )
    )
    private void tierify$centerPerfectLabel(
            TooltipComponent instance,
            TextRenderer textRenderer,
            int x, int y,
            Matrix4f matrix,
            VertexConsumerProvider vertexConsumers, // FIX: Changed from .Immediate to Interface
            // Captured locals
            LayerDepth depth,
            TooltipContext ctx,
            Vec2f pos,
            Point size,
            Text rarity,
            TextRenderer font
    ) {
        int drawX = x;

        // Check if this component is our special "Perfect" tier label
        if (instance instanceof PerfectTierComponent) {
            // size.x is the width of the entire tooltip background
            // pos.x is the absolute X position
            
            int tooltipWidth = size.x;
            int componentWidth = instance.getWidth(textRenderer);
            
            // Calculate absolute left edge of the tooltip
            int absoluteLeft = (int) pos.x;
            
            // Center formula: Left + (TotalWidth - ComponentWidth) / 2
            drawX = absoluteLeft + (tooltipWidth - componentWidth) / 2;
        }

        // Use the interface method
        instance.drawText(textRenderer, drawX, y, matrix, vertexConsumers);
    }
}
