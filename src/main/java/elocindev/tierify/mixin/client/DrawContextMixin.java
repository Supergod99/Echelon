package elocindev.tierify.mixin.client;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import elocindev.tierify.Tierify;
import elocindev.tierify.TierifyClient;
import elocindev.tierify.util.TieredTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Inject(method = "drawItemTooltip", at = @At("HEAD"), cancellable = true)
    private void drawItemTooltipMixin(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo info) {
        // 1. If Tooltip Overhaul is present, let it handle everything.
        if (FabricLoader.getInstance().isModLoaded("tooltipoverhaul") || FabricLoader.getInstance().isModLoaded("legendarytooltips")) {
            return;
        }

        // 2. Check if the item has NBT and the specific Tiered tag
        if (Tierify.CLIENT_CONFIG.tieredTooltip && stack.hasNbt()) {
            
            // Use getSubNbt instead of getOrCreate to prevent modifying the stack during render
            NbtCompound tierTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            
            if (tierTag != null) {
                // 3. Resolve the Lookup Key 
                String tier = tierTag.getString(Tierify.NBT_SUBTAG_DATA_KEY);
                boolean isPerfect = tierTag.getBoolean("Perfect");
                
                // If "Perfect", use the special border key. Otherwise, use the standard Tier ID.
                String lookupKey = isPerfect ? "{BorderTier:\"tiered:perfect\"}" : "{Tier:\"" + tier + "\"}";

                // 4. Find a matching Border Template
                for (int i = 0; i < TierifyClient.BORDER_TEMPLATES.size(); i++) {
                    if (TierifyClient.BORDER_TEMPLATES.get(i).containsDecider(lookupKey)) {

                        // 5. Build the tooltip components (Text + Data)
                        List<Text> text = net.minecraft.client.gui.screen.Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack);
                        List<TooltipComponent> list = text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
                        stack.getTooltipData().ifPresent(data -> list.add(1, TooltipComponent.of(data)));

                        // 6. Render the custom border and tooltip
                        TieredTooltip.renderTieredTooltipFromComponents(
                            (DrawContext) (Object) this, 
                            textRenderer, 
                            list, 
                            x, 
                            y, 
                            HoveredTooltipPositioner.INSTANCE, 
                            TierifyClient.BORDER_TEMPLATES.get(i)
                        );

                        // 7. Cancel the vanilla tooltip render so we don't draw double
                        info.cancel();
                        return;
                    }
                }
            }
        }
    }
}
