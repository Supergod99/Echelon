package elocindev.tierify.mixin;

import elocindev.tierify.Tierify;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract NbtCompound getNbt();
    @Shadow public abstract boolean hasNbt();

    @Inject(method = "getMaxDamage", at = @At("TAIL"), cancellable = true)
    private void getMaxDamageMixin(CallbackInfoReturnable<Integer> cir) {
        if (!hasNbt()) return;

        NbtCompound root = getNbt();
        if (root == null) return;
        // namespaced TierifyExtra
        NbtCompound extra = root.getCompound(Tierify.NBT_SUBTAG_EXTRA_KEY);
        if (extra != null && extra.contains("durable")) {
            applyDurable(extra, cir);
            return;
        }

        // fallback: only honor root "durable" if item is tiered
        if (root.contains(Tierify.NBT_SUBTAG_KEY) && root.contains("durable")) {
            applyDurable(root, cir);
        }
    }

    private static void applyDurable(NbtCompound tag, CallbackInfoReturnable<Integer> cir) {
        int base = cir.getReturnValue();
        if (base <= 0) return; // never make non-damageable items damageable
        // Flat durability add (int), allow both + and -
        if (tag.contains("durable", 3 /* INT */)) {
            int delta = tag.getInt("durable");
            if (delta != 0) {
                cir.setReturnValue(Math.max(1, base + delta));
            }
            return;
        }
        // Multiplier (float/double), allow both + and -
        if (tag.contains("durable")) {
            float mult = tag.getFloat("durable"); // works for float/double numeric tags
            if (!Float.isFinite(mult) || mult == 0.0F) return;
            // Keep the old truncation behavior you previously had (important for parity)
            int delta = (int) (mult * base);
            if (delta != 0) {
                cir.setReturnValue(Math.max(1, base + delta));
            }
        }
    }
}
