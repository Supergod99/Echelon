package elocindev.tierify.mixin.client;

import draylar.tiered.api.BorderTemplate;
import elocindev.tierify.Tierify;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    private void renderOverlayMixin(DrawContext context, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo info) {
        if (stack.hasNbt() && stack.getOrCreateSubNbt(Tierify.NBT_SUBTAG_KEY) != null) {
            NbtCompound tierTag = stack.getOrCreateSubNbt(Tierify.NBT_SUBTAG_KEY);

            if (tierTag.contains("BorderTier")) {
                Identifier tier = new Identifier(tierTag.getString("BorderTier"));
                BorderTemplate template = Tierify.DATA_LOADER.getBorders().get(tier);

                if (template != null) {
                    context.drawTexture(template.getIndex(), x, y, 0, 0, 16, 16, 16, 16);
                }
            }
        }
    }
}
