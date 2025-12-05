package elocindev.tierify.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import elocindev.tierify.Tierify;
import elocindev.tierify.TierifyClient;
import elocindev.tierify.screen.client.PerfectLabelAnimator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow @Nullable protected Slot focusedSlot;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    // 1. Capture Stack (Standard)
    @Inject(method = "render", at = @At("HEAD"))
    private void captureHandledStack(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            TierifyClient.CURRENT_TOOLTIP_STACK = this.focusedSlot.getStack();
        }
    }

    // 2. Release Stack (Standard)
    @Inject(method = "render", at = @At("RETURN"))
    private void releaseHandledStack(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        TierifyClient.CURRENT_TOOLTIP_STACK = ItemStack.EMPTY;
    }

    // 3. OVERLAY RENDER (Label Only)
    // We do NOT draw the box/border here to avoid double-rendering.
    // We ONLY draw the Perfect Label on top of the existing tooltip.
    @Inject(method = "render", at = @At("RETURN"))
    private void renderPerfectOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        
        // Safety Checks
        if (this.client == null || this.client.player == null) return;
        if (this.focusedSlot == null || !this.focusedSlot.hasStack()) return;

        ItemStack stack = this.focusedSlot.getStack();
        NbtCompound tieredTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);

        // Only render if it's a Perfect Item
        if (tieredTag != null && tieredTag.getBoolean("Perfect")) {
            
            TextRenderer textRenderer = this.client.textRenderer;
            if (textRenderer == null) return;

            // 1. Calculate Position
            int x = mouseX + 12;
            int y = mouseY - 12;
            int width = this.width;
            int height = this.height;
            
            // Screen Clamping
            if (x + 100 > width) x -= 28 + 100;
            if (y + 20 > height) y = height - 20;

            // 2. Render the "Perfect" Label
            // We use Z-Level 2000 to be absolutely sure it's on top of everything.
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 2000); 

            MutableText perfectText = PerfectLabelAnimator.getPerfectLabel();
            float scale = 0.75f; 

            // Position the label inside the tooltip box (approximate)
            // y + 2 puts it near the top title line.
            context.getMatrices().push();
            context.getMatrices().translate(x + 4, y + 2, 0); 
            context.getMatrices().scale(scale, scale, 1f);
            
            context.drawText(textRenderer, perfectText, 0, 0, 0xFFFFFF, true);
            
            context.getMatrices().pop();
            context.getMatrices().pop();
        }
    }
}
