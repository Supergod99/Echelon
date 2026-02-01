package elocindev.tierify.forge.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import elocindev.tierify.forge.client.ApexEffectGradientAnimatorForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class ApexEffectTooltipComponent implements ClientTooltipComponent {

    private static final String HINT_TEXT = "Hold Shift for details";
    private static final int HINT_RGB = 0xDCC79A; // warm, slightly subdued gold

    @Override
    public int getWidth(Font font) {
        FormattedCharSequence titleSeq = Language.getInstance().getVisualOrder(buildTitle());
        FormattedCharSequence hintSeq = Language.getInstance().getVisualOrder(buildHint());
        return Math.max(font.width(titleSeq), font.width(hintSeq));
    }

    @Override
    public int getHeight() {
        // Compress a few pixels: small hint font otherwise looks like an extra blank line.
        int lh = Minecraft.getInstance().font.lineHeight;
        return lh * 2 - 3;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
        Component title = buildTitle();
        FormattedCharSequence titleSeq = Language.getInstance().getVisualOrder(title);

        Component hint = buildHint();
        FormattedCharSequence hintSeq = Language.getInstance().getVisualOrder(hint);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        font.drawInBatch(titleSeq, (float) x, (float) y, 0xFFFFFFFF, false, matrix, buffer,
                Font.DisplayMode.NORMAL, 0, 0xF000F0);

        int lh = Minecraft.getInstance().font.lineHeight;
        int hintY = y + lh;

        font.drawInBatch(hintSeq, (float) x, (float) hintY, 0xFFFFFFFF, false, matrix, buffer,
                Font.DisplayMode.NORMAL, 0, 0xF000F0);

        buffer.endBatch();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        // Intentionally empty
    }

    private static MutableComponent buildTitle() {
        return ApexEffectGradientAnimatorForge.animate(Component.literal("Apex Effect"));
    }

    private static MutableComponent buildHint() {
        // No gradient: gradient is reserved for the description.
        return Component.literal(HINT_TEXT).setStyle(
                Style.EMPTY
                        .withColor(TextColor.fromRgb(HINT_RGB))
                        .withFont(ApexEffectGradientAnimatorForge.FONT_SMALL)
                        .withItalic(true)
        );
    }
}
