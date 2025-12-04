package elocindev.tierify.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.tiered.api.BorderTemplate;
import elocindev.tierify.TierifyClient;
import elocindev.tierify.Tierify;
import elocindev.tierify.util.TieredTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow @Nullable protected Slot focusedSlot;

    // We extend Screen, so we need a constructor matching super
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawMouseoverTooltip", 
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"), 
            cancellable = true)
    protected void drawMouseoverTooltipMixin(DrawContext context, int x, int y, CallbackInfo info) {
        
        // Safety check
        if (this.focusedSlot == null || !this.focusedSlot.hasStack()) return;
        
        ItemStack stack = this.focusedSlot.getStack();

        if (Tierify.CLIENT_CONFIG.tieredTooltip && stack.hasNbt() && stack.getNbt().contains("Tiered")) {
            
            // --- FIX: Pass 'this.client' to the static method ---
            // This generates the list of text lines (Lore, Name, Attributes)
            List<Text> text = Screen.getTooltipFromItem(this.client, stack);
            
            // Get the bundle/icon data
            Optional<TooltipData> data = stack.getTooltipData();

            // --- 1. PERFECT BORDER OVERRIDE ---
            NbtCompound tierTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            if (tierTag != null && tierTag.getBoolean("Perfect")) {

                for (int p = 0; p < TierifyClient.BORDER_TEMPLATES.size(); p++) {
                    BorderTemplate template = TierifyClient.BORDER_TEMPLATES.get(p);

                    if (template.containsDecider("{BorderTier:\"tiered:perfect\"}")) {
                        if (!template.containsStack(stack)) {
                            template.addStack(stack);
                        }

                        List<TooltipComponent> list = new ArrayList<>();
                        int wrapWidth = 350;

                        // Wrap text logic
                        for (int k = 0; k < text.size(); k++) {
                            Text t = text.get(k);
                            int width = this.textRenderer.getWidth(t);

                            // Don't wrap title (k=0) or short lines
                            if (k == 0 || width <= wrapWidth) {
                                list.add(TooltipComponent.of(t.asOrderedText()));
                            } else {
                                List<OrderedText> wrapped = this.textRenderer.wrapLines(t, wrapWidth);
                                for (OrderedText line : wrapped) {
                                    list.add(TooltipComponent.of(line));
                                }
                            }
                        }

                        // Insert Icon/Bundle data if present
                        data.ifPresent(d -> {
                            if (list.size() > 1) {
                                list.add(1, TooltipComponent.of(d));
                            } else {
                                list.add(TooltipComponent.of(d));
                            }
                        });

                        // Render with Custom Border
                        TieredTooltip.renderTieredTooltipFromComponents(
                                context,
                                this.textRenderer,
                                list,
                                x,
                                y,
                                HoveredTooltipPositioner.INSTANCE,
                                template
                        );

                        info.cancel();
                        return;
                    }
                }
            }

            // --- 2. STANDARD TIER BORDER ---
            String nbtString = stack.getNbt().getCompound("Tiered").asString();
            for (int i = 0; i < TierifyClient.BORDER_TEMPLATES.size(); i++) {
                if (!TierifyClient.BORDER_TEMPLATES.get(i).containsStack(stack) && TierifyClient.BORDER_TEMPLATES.get(i).containsDecider(nbtString)) {
                    TierifyClient.BORDER_TEMPLATES.get(i).addStack(stack);
                } else if (TierifyClient.BORDER_TEMPLATES.get(i).containsStack(stack)) {
                    
                    List<TooltipComponent> list = new ArrayList<>();
                    int wrapWidth = 350;

                    for (int k = 0; k < text.size(); k++) {
                        Text t = text.get(k);
                        int width = this.textRenderer.getWidth(t);

                        if (k == 0 || width <= wrapWidth) {
                            list.add(TooltipComponent.of(t.asOrderedText()));
                        } else {
                            List<OrderedText> wrapped = this.textRenderer.wrapLines(t, wrapWidth);
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

                    TieredTooltip.renderTieredTooltipFromComponents(context, this.textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, TierifyClient.BORDER_TEMPLATES.get(i));

                    info.cancel();
                    break;
                }
            }
        }
    }
}
