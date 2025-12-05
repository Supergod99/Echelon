package elocindev.tierify.mixin.client;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import elocindev.tierify.Tierify;
import elocindev.tierify.TierifyClient;
import elocindev.tierify.util.TieredTooltip;
import elocindev.tierify.util.BorderTemplate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow @Nullable protected Slot focusedSlot;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    // 1. Capture Stack
    @Inject(method = "render", at = @At("HEAD"))
    private void captureHandledStack(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            TierifyClient.CURRENT_TOOLTIP_STACK = this.focusedSlot.getStack();
        }
    }

    // 2. Release Stack
    @Inject(method = "render", at = @At("RETURN"))
    private void releaseHandledStack(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        TierifyClient.CURRENT_TOOLTIP_STACK = ItemStack.EMPTY;
    }

    // 3. HOSTILE TAKEOVER RENDER (The Fix)
    @Inject(method = "render", at = @At("RETURN"))
    private void renderTierifyOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        
        // Safety Check 1: Ensure Client/Player exists (Fixes Reforge Crash)
        if (this.client == null || this.client.player == null) return;
        
        if (this.focusedSlot == null || !this.focusedSlot.hasStack()) return;

        ItemStack stack = this.focusedSlot.getStack();
        NbtCompound tieredTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);

        if (tieredTag != null) {
            boolean isPerfect = tieredTag.getBoolean("Perfect");
            TextRenderer textRenderer = this.client.textRenderer;

            if (textRenderer == null) return; // Paranoid Check
            
            // 1. Calculate Position
            int x = mouseX + 12;
            int y = mouseY - 12;
            int width = this.width;
            int height = this.height;
            
            if (x + 100 > width) x -= 28 + 100;
            if (y + 20 > height) y = height - 20;

            // 2. Fetch Components
            List<Text> textList = stack.getTooltip(this.client.player, TooltipContext.Default.BASIC);
            
            if (textList == null) return; // Paranoid Check

            List<TooltipComponent> components = textList.stream()
                .map(Text::asOrderedText)
                .map(TooltipComponent::of)
                .collect(Collectors.toList());

            // 3. Select Template
            String lookupKey = isPerfect ? "{BorderTier:\"tiered:perfect\"}" : "{Tier:\"" + tieredTag.getString(Tierify.NBT_SUBTAG_DATA_KEY) + "\"}";
            
            // Safety Check 2: Ensure Templates list is valid
            if (TierifyClient.BORDER_TEMPLATES == null) return;

            for (int i = 0; i < TierifyClient.BORDER_TEMPLATES.size(); i++) {
                BorderTemplate template = TierifyClient.BORDER_TEMPLATES.get(i);
                
                // Safety Check 3: Ensure individual template is valid
                if (template != null && template.containsDecider(lookupKey)) {
                    
                    context.getMatrices().push();
                    context.getMatrices().translate(0, 0, 500); 

                    TieredTooltip.renderTieredTooltipFromComponents(
                        context, 
                        textRenderer, 
                        components, 
                        x, 
                        y, 
                        (TooltipPositioner)null, 
                        template
                    );
                    
                    context.getMatrices().pop();
                    return;
                }
            }
        }
    }
}
