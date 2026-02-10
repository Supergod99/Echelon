package elocindev.tierify.forge.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractTexture.class)
public abstract class FontTextureSwizzleMixin {

    @Unique
    private boolean tierify$swizzleKnown = false;
    @Unique
    private boolean tierify$isSingleChannel = false;
    @Unique
    private int tierify$appliedSwizzleMode = -1;

    @Inject(method = "bind", at = @At("TAIL"))
    private void tierify$fixFontSwizzle(CallbackInfo ci) {
        if (!((Object) this instanceof FontTexture)) return;

        if (!tierify$swizzleKnown) {
            int internalFormat = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
            tierify$isSingleChannel = internalFormat == GL30.GL_R8 || internalFormat == GL11.GL_RED;
            tierify$swizzleKnown = true;
        }

        int wantedMode = tierify$isSingleChannel ? 1 : 0;
        if (tierify$appliedSwizzleMode == wantedMode) return;

        if (tierify$isSingleChannel) {
            // R8 mask font atlas: make RGB = 1, alpha = mask (red channel)
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_R, GL11.GL_ONE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_G, GL11.GL_ONE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_B, GL11.GL_ONE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_A, GL11.GL_RED);
        } else {
            // Identity mapping for RGBA textures (safe reset if any swizzles were changed earlier).
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_R, GL11.GL_RED);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_G, GL11.GL_GREEN);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_B, GL11.GL_BLUE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_A, GL11.GL_ALPHA);
        }
        tierify$appliedSwizzleMode = wantedMode;
    }
}

