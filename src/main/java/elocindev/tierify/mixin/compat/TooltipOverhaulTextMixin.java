package elocindev.tierify.mixin.compat;

import dev.xylonity.tooltipoverhaul.client.style.text.DefaultText;
import dev.xylonity.tooltipoverhaul.client.TooltipContext;
import dev.xylonity.tooltipoverhaul.client.layer.LayerDepth;
import elocindev.tierify.screen.client.component.PerfectTierComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.class_241; // This is Vec2f/Vector2f in Mojang mappings
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.Point;

@Mixin(DefaultText.class)
public class TooltipOverhaulTextMixin {

    // Targetting: component.method_32665(font, x, y, ...);
    // In Yarn: TooltipComponent.drawText(TextRenderer, int, int, Matrix4f, VertexConsumerProvider.Immediate)
    @Redirect(
        method = "render(Ldev/xylonity/tooltipoverhaul/client/layer/LayerDepth;Ldev/xylonity/tooltipoverhaul/client/TooltipContext;Lnet/minecraft/class_241;Ljava/awt/Point;Lnet/minecraft/class_2561;Lnet/minecraft/class_327;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;method_32665(Lnet/minecraft/client/font/TextRenderer;IILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V"
        )
    )
    private void tierify$centerPerfectLabel(
            TooltipComponent instance,
            TextRenderer textRenderer,
            int x, int y,
            Matrix4f matrix,
            VertexConsumerProvider.Immediate vertexConsumers,
            // Captured locals from the render method
            LayerDepth depth,
            TooltipContext ctx,
            class_241 pos, // This is the 'pos' argument from render()
            Point size // This is the 'size' argument from render()
    ) {
        int drawX = x;

        // Check if this component is our special "Perfect" tier label
        if (instance instanceof PerfectTierComponent) {
            // size.x is the width of the entire tooltip background
            // pos.field_1343 is the X coordinate (float)
            
            int tooltipWidth = size.x;
            int componentWidth = instance.getWidth(textRenderer);
            
            // Calculate absolute left edge of the tooltip
            int absoluteLeft = (int) pos.field_1343;
            
            // Center the text: Left + (TotalWidth - TextWidth) / 2
            drawX = absoluteLeft + (tooltipWidth - componentWidth) / 2;
        }

        // Proceed with the original draw call using our (potentially modified) X
        instance.drawText(textRenderer, drawX, y, matrix, vertexConsumers);
    }
}
