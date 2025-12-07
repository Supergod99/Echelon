package elocindev.tierify.compat;

import dev.xylonity.tooltipoverhaul.client.TooltipContext;
import dev.xylonity.tooltipoverhaul.client.frame.CustomFrameData;
import dev.xylonity.tooltipoverhaul.client.layer.ITooltipLayer;
import dev.xylonity.tooltipoverhaul.client.layer.LayerDepth;
import dev.xylonity.tooltipoverhaul.client.style.TooltipStyle;
import elocindev.tierify.Tierify;
import elocindev.tierify.TierifyClient;
import elocindev.tierify.screen.client.PerfectLabelAnimator;
import elocindev.tierify.screen.client.PerfectBorderRenderer;
import draylar.tiered.api.BorderTemplate;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec2f;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import java.awt.Point;

public class TierifyBorderLayer implements ITooltipLayer {

    @Override
    public void render(TooltipContext ctx, Vec2f pos, Point size, TooltipStyle style, Text rarity, TextRenderer font, CustomFrameData customFrame) {
        // 1. Safety Check
        if (!ctx.stack().hasNbt()) return;

        NbtCompound tierTag = ctx.stack().getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (tierTag == null || !tierTag.contains(Tierify.NBT_SUBTAG_DATA_KEY)) {
            return;
        }

        // 2. Resolve Tier
        String tierId = tierTag.getString(Tierify.NBT_SUBTAG_DATA_KEY);
        boolean isPerfect = tierTag.getBoolean("Perfect");
        String lookupKey = isPerfect ? "{BorderTier:\"tiered:perfect\"}" : "{Tier:\"" + tierId + "\"}";

        // 3. Find Template
        BorderTemplate match = null;
        if (TierifyClient.BORDER_TEMPLATES != null) {
            for (BorderTemplate template : TierifyClient.BORDER_TEMPLATES) {
                if (template.containsDecider(lookupKey)) {
                    match = template;
                    break;
                }
            }
        }

        if (match == null) return;

        // 4. Setup Geometry (Standard Tooltip Bounds)
        // Tooltip Overhaul content starts at pos.x, pos.y
        final int x = (int) pos.x; 
        final int y = (int) pos.y; 
        final int width = size.x;
        final int height = size.y;

        // Calculate Border Colors
        final int startColor = match.getStartGradient();
        final int endColor = match.getEndGradient();

        // Calculate Texture Index
        int rawIndex = match.getIndex();
        final int secondHalf = rawIndex > 7 ? 1 : 0;
        final int index = rawIndex > 7 ? rawIndex - 8 : rawIndex;
        
        final Identifier texture = match.getIdentifier();

        // 5. Draw
        ctx.push(() -> {
            // Z-Level 3000 draws on top of standard background
            ctx.translate(0.0f, 0.0f, LayerDepth.BACKGROUND_OVERLAY.getZ());
            
            DrawContext drawContext = ctx.graphics();
            
            // --- A. Draw Gradient Lines (The "Connectors") ---
            // Replicating logic from TieredTooltip.renderBorder(...)
            // Original logic uses: i = x - 3, j = y - 3, k = w + 6, l = h + 6
            
            int i = x - 3;
            int j = y - 3;
            int k = width + 6;
            int l = height + 6;
            
            // Adjust y for top/bottom lines to match renderHorizontalLine logic (y-1) from original
            // Original: renderHorizontalLine(..., y - 1, ...) where y passed was j+1. So j.
            
            // Vertical Left (at x-3)
            drawContext.fillGradient(i, j + 1, i + 1, j + l - 1, 400, startColor, endColor);
            
            // Vertical Right (at x + width + 2)
            drawContext.fillGradient(i + k - 1, j + 1, i + k, j + l - 1, 400, startColor, endColor);
            
            // Horizontal Top (at y-3)
            drawContext.fillGradient(i, j, i + k, j + 1, 400, startColor, startColor);
            
            // Horizontal Bottom (at y + height + 2)
            drawContext.fillGradient(i, j + l - 1, i + k, j + l, 400, endColor, endColor);


            // --- B. Draw Texture Corners & Header (The "Fancy" Bits) ---
            // Texture corners are drawn at offsets relative to standard border
            // Tiered uses: n - 6 (where n is x)
            int texW = 128;
            int texH = 128;
            
            // Top Left Corner
            drawContext.drawTexture(texture, x - 6, y - 6, 0 + secondHalf * 64, 0 + index * 16, 8, 8, texW, texH);
            // Top Right Corner
            drawContext.drawTexture(texture, x + width - 2, y - 6, 56 + secondHalf * 64, 0 + index * 16, 8, 8, texW, texH);
            // Bottom Left Corner
            drawContext.drawTexture(texture, x - 6, y + height - 2, 0 + secondHalf * 64, 8 + index * 16, 8, 8, texW, texH);
            // Bottom Right Corner
            drawContext.drawTexture(texture, x + width - 2, y + height - 2, 56 + secondHalf * 64, 8 + index * 16, 8, 8, texW, texH);

            // Header Plate (Centered "Gem")
            if (width >= 48) {
                 drawContext.drawTexture(texture, x + (width / 2) - 24, y - 9, 8 + secondHalf * 64, 0 + index * 16, 48, 8, texW, texH);
            }
            
             // Footer Plate (Centered)
             if (width >= 48) {
                 drawContext.drawTexture(texture, x + (width / 2) - 24, y + height + 1, 8 + secondHalf * 64, 8 + index * 16, 48, 8, texW, texH);
            }

            // --- C. Animated Perfect Overlay (Glow) ---
            // This applies the pulsing effect to the corners we just drew
            ctx.push(() -> {
                ctx.translate(0.0f, 0.0f, 10.0f); // Draw slightly above
                PerfectBorderRenderer.renderPerfectBorderOverlay(drawContext, match, x, y, width, height);
            });

            // --- D. Draw "Perfect" Text (Self-Centering) ---
            if (isPerfect) {
                renderPerfectLabel(ctx, font, x, y, width);
            }
        });
    }

    private void renderPerfectLabel(TooltipContext ctx, TextRenderer font, int bgX, int bgY, int bgWidth) {
        MutableText label = PerfectLabelAnimator.getPerfectLabel();
        float scale = 0.65f;
        int textWidth = font.getWidth(label);
        
        // Calculate Centered X based on the background width
        float centeredX = bgX + (bgWidth / 2.0f) - ((textWidth * scale) / 2.0f);
        
        // Calculate Y: Sit exactly on Line 1 (approx 14px down from top)
        float fixedY = bgY + 14.0f; 

        ctx.push(() -> {
            ctx.translate(centeredX, fixedY, LayerDepth.BACKGROUND_OVERLAY.getZ() + 20);
            ctx.scale(scale, scale, 1.0f);
            ctx.graphics().drawText(font, label, 0, 0, 0xFFFFFF, true);
        });
    }
}
