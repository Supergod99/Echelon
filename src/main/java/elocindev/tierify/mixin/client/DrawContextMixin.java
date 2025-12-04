package elocindev.tierify.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import elocindev.tierify.TierifyClient;
import elocindev.tierify.Tierify;
import elocindev.tierify.util.TieredTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Redirect(method = "drawItemTooltip", 
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void redirectDrawTooltipMixin(DrawContext context, TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, TextRenderer tr, ItemStack stack) {
        // Note: 'stack' is available because we are redirecting inside drawItemTooltip(..., ItemStack stack, ...)
        
        if (Tierify.CLIENT_CONFIG.tieredTooltip && stack.hasNbt() && stack.getNbt().contains("Tiered")) {
            String nbtString = stack.getNbt().getCompound("Tiered").asString();
            
            for (int i = 0; i < TierifyClient.BORDER_TEMPLATES.size(); i++) {
                boolean matchesDecider = !TierifyClient.BORDER_TEMPLATES.get(i).containsStack(stack) 
                                         && TierifyClient.BORDER_TEMPLATES.get(i).containsDecider(nbtString);
                boolean matchesStack = TierifyClient.BORDER_TEMPLATES.get(i).containsStack(stack);

                if (matchesDecider) {
                    TierifyClient.BORDER_TEMPLATES.get(i).addStack(stack);
                } 
                else if (matchesStack) {
                    List<TooltipComponent> list = new ArrayList<>();
                    int wrapWidth = 350; 

                    for (int k = 0; k < text.size(); k++) {
                        Text t = text.get(k);
                        int width = textRenderer.getWidth(t);

                        if (k == 0 || width <= wrapWidth) {
                            list.add(TooltipComponent.of(t.asOrderedText()));
                        } else {
                            List<OrderedText> wrapped = textRenderer.wrapLines(t, wrapWidth);
                            for (OrderedText line : wrapped) {
                                list.add(TooltipComponent.of(line));
                            }
                        }
                    }

                    data.ifPresent(d -> {
                        if (list.size() > 1) {
                            list.add(1, TooltipComponent.of(d));
                        } else {
                            list.add(TooltipComponent.of(d));
                        }
                    });

                    TieredTooltip.renderTieredTooltipFromComponents(
                        context, 
                        textRenderer, 
                        list, 
                        x, 
                        y, 
                        HoveredTooltipPositioner.INSTANCE, 
                        TierifyClient.BORDER_TEMPLATES.get(i)
                    );
                    return; // Return early, skipping vanilla render
                }
            }
        }
        
        // Fallback to vanilla
        context.drawTooltip(textRenderer, text, data, x, y);
    }
}
