package elocindev.tierify.forge.mixin;

import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.apex.ApexActiveEffects;
import elocindev.tierify.util.StarApexUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getMaxDamage", at = @At("TAIL"), cancellable = true)
    private void tierify$getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        CompoundTag container = null;
        if (tag.contains(TierifyConstants.NBT_SUBTAG_EXTRA_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag extra = tag.getCompound(TierifyConstants.NBT_SUBTAG_EXTRA_KEY);
            if (extra.contains("durable")) {
                container = extra;
            }
        }
        if (container == null && tag.contains(TierifyConstants.NBT_SUBTAG_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag tier = tag.getCompound(TierifyConstants.NBT_SUBTAG_KEY);
            if (tier.contains("durable")) {
                container = tier;
            }
        }
        if (container == null && tag.contains("durable")) {
            container = tag;
        }

        if (container == null) return;

        int base = cir.getReturnValue();
        double starMult = 1.0 + (0.05 * StarApexUtils.getStars(stack));
        if (StarApexUtils.isApex(stack)) {
            starMult += 0.25;
        }
        if (container.contains("durable", Tag.TAG_INT)) {
            int durable = container.getInt("durable");
            if (durable != 0) {
                int scaled = (int) Math.round(durable * starMult);
                cir.setReturnValue(base + scaled);
            }
            return;
        }

        float durable = container.getFloat("durable");
        float bonus = container.getFloat("durable_set_bonus");
        float total = (durable + bonus) * (float) starMult;
        if (total != 0.0f) {
            cir.setReturnValue(base + (int) (total * base));
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void tierify$preventApexUnbreakableDamage(int amount,
                                                      RandomSource random,
                                                      ServerPlayer player,
                                                      CallbackInfoReturnable<Boolean> cir) {
        if (player == null || amount <= 0) return;
        if (!ApexActiveEffects.isUnbreakableArmorActive(player)) return;

        ItemStack stack = (ItemStack) (Object) this;
        if (!(stack.getItem() instanceof ArmorItem)) return;

        cir.setReturnValue(false);
    }
}
