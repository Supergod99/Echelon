package elocindev.tierify.mixin.client;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import elocindev.tierify.TierifyClient;
import elocindev.tierify.Tierify;
import elocindev.tierify.util.TieredTooltip;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(DrawContext.class)
public class TieredDrawContextMixin {

    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

    @Inject(method = "drawItemTooltip", at = @At("HEAD"), cancellable = true)
    private void drawItemTooltipMixin(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo info) {

        System.out.println("TIERIFY DEBUG: drawItemTooltipMixin called!");

        if (FabricLoader.getInstance().isModLoaded("tooltipoverhaul")) {
            return;
        }

        if (Tierify.CLIENT_CONFIG.tieredTooltip && stack.hasNbt() && stack.getNbt().contains(Tierify.NBT_SUBTAG_KEY)) {
            String tierId = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY).getString(Tierify.NBT_SUBTAG_DATA_KEY);

            String lookupKey = "{Tier:\"" + tierId + "\"}";
            
            if (stack.getSubNbt(Tierify.NBT_SUBTAG_KEY).getBoolean("Perfect")) {
                lookupKey = "{BorderTier:\"tiered:perfect\"}";
            }

            for (int i = 0; i < TierifyClient.BORDER_TEMPLATES.size(); i++) {
                if (TierifyClient.BORDER_TEMPLATES.get(i).containsDecider(lookupKey)) {

                    List<Text> text = Screen.getTooltipFromItem(client, stack);
                    List<TooltipComponent> list = text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
                    stack.getTooltipData().ifPresent(data -> list.add(1, TooltipComponent.of(data)));

                    TieredTooltip.renderTieredTooltipFromComponents((DrawContext) (Object) this, textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, TierifyClient.BORDER_TEMPLATES.get(i));
                    
                    info.cancel();
                    break;
                }
            }
        }
    }
}
