package elocindev.tierify.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import elocindev.tierify.TierifyClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
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

    /**
     * CAPTURE PHASE:
     * We do NOT render anything here. We just tell TierifyClient which item is being looked at.
     * This allows the game to build the tooltip list normally (keeping Icons/Lines),
     * and then DrawContextMixin (the next file) will draw the border.
     */
    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    private void captureHandledStack(DrawContext context, int x, int y, CallbackInfo info) {
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            TierifyClient.CURRENT_TOOLTIP_STACK = this.focusedSlot.getStack();
        }
    }

    /**
     * RELEASE PHASE:
     * Clean up after we are done so we don't accidentally draw borders on other things.
     */
    @Inject(method = "drawMouseoverTooltip", at = @At("RETURN"))
    private void releaseHandledStack(DrawContext context, int x, int y, CallbackInfo info) {
        TierifyClient.CURRENT_TOOLTIP_STACK = ItemStack.EMPTY;
    }
}
