package elocindev.tierify.screen.client.component;

import elocindev.tierify.screen.client.PerfectLabelAnimator;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class PerfectTierComponent implements TooltipComponent {
    private static final float SCALE = 0.65f;

    @Override
    public int getHeight() {
        return (int) (9 * SCALE) + 4; // Reserve height
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        Text text = PerfectLabelAnimator.getPerfectLabel();
        return (int) (textRenderer.getWidth(text) * SCALE); // Reserve width
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        // No rendering here. The text is rendered centered by TierifyBorderLayer.
    }
}
