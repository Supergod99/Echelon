package elocindev.tierify.mixin.client;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import draylar.tiered.api.PotentialAttribute;
import elocindev.tierify.Tierify;
import elocindev.tierify.screen.client.TierGradientAnimator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow public abstract NbtCompound getOrCreateSubNbt(String key);
    @Shadow public abstract boolean hasNbt();
    @Shadow public abstract NbtCompound getSubNbt(String key);
    @Shadow public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot);

    // 1. NAME MODIFICATION (Kept as is, it works fine)
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void getNameMixin(CallbackInfoReturnable<Text> info) {
        if (this.hasNbt() && this.getSubNbt("display") == null && this.getSubNbt(Tierify.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(getOrCreateSubNbt(Tierify.NBT_SUBTAG_KEY).getString(Tierify.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute potentialAttribute = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
            if (potentialAttribute != null) {
                MutableText text = Text.translatable(potentialAttribute.getID() + ".label");
                String tierKey = TierGradientAnimator.getTierFromId(potentialAttribute.getID());
                text = TierGradientAnimator.animate(text, tierKey);
                MutableText vanilla = info.getReturnValue().copy();
                info.setReturnValue(text.append(" ").append(vanilla));
            }
        }
    }

    // 2. SAFE TOOLTIP MODIFICATION (Inject at RETURN to avoid conflicts)
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void modifyTooltipFinal(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> tooltip = cir.getReturnValue();
        if (tooltip == null || tooltip.isEmpty()) return;

        // -- BORDER LOGIC --
        // Sets the NBT so the ItemRendererMixin knows to draw the border
        if (this.hasNbt()) {
            NbtCompound tierTag = this.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            if (tierTag != null && tierTag.getBoolean("Perfect")) {
                tierTag.putString("BorderTier", "tiered:perfect");
            }
        }

        // -- COLOR LOGIC --
        // Scans the existing tooltip and recolors lines that look like attributes
        if (this.hasNbt() && this.getSubNbt(Tierify.NBT_SUBTAG_KEY) != null) {
            applyTierColors(tooltip);
        }

        // -- ATTACK SPEED FIX --
        fixAttackSpeedText(tooltip);
    }

    // 3. EQUIPMENT SLOT HEADER FIX (Kept as is)
    @ModifyExpressionValue(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;", ordinal = 1))
    private MutableText modifyTooltipEquipmentSlot(MutableText original) {
        if (this.hasNbt() && this.getSubNbt(Tierify.NBT_SUBTAG_KEY) != null 
                && this.getAttributeModifiers(EquipmentSlot.MAINHAND) != null && !this.getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty()
                && this.getAttributeModifiers(EquipmentSlot.OFFHAND) != null && !this.getAttributeModifiers(EquipmentSlot.OFFHAND).isEmpty()) {
            return Text.translatable("item.modifiers.hand").formatted(Formatting.GRAY);
        }
        return original;
    }

    // --- HELPER METHODS ---

    private void applyTierColors(List<Text> tooltip) {
        // Determine if we should use Gold (Set Bonus) or Blue (Standard)
        boolean hasSetBonus = checkSetBonus();
        Formatting color = hasSetBonus ? Formatting.GOLD : Formatting.BLUE;

        for (int i = 0; i < tooltip.size(); i++) {
            Text line = tooltip.get(i);
            String text = line.getString();

            // Heuristic: Identify lines that are likely Attribute Modifiers.
            // Vanilla format usually contains a "+" or "Attribute Modifier" and numbers.
            // We specifically look for the structure vanilla uses for attributes.
            if ((text.contains("+") || text.contains("-")) && (text.matches(".*[0-9].*"))) {
                 // Check if the style is default (usually Gray or Blue for vanilla). 
                 // If it is, we overwrite it with our Tier Color.
                 // We convert the line to a MutableText and apply our color style.
                 if (!text.contains("§")) { // Avoid overwriting existing formatted text if possible
                     MutableText coloredLine = Text.literal(text).setStyle(Style.EMPTY.withColor(color));
                     tooltip.set(i, coloredLine);
                 }
            }
        }
    }

    private boolean checkSetBonus() {
        PlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null || !this.hasNbt()) return false;

        NbtCompound nbt = this.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (nbt == null) return false;

        String myTier = nbt.getString(Tierify.NBT_SUBTAG_DATA_KEY);
        if (myTier.isEmpty()) return false;

        int matchCount = 0;
        for (ItemStack armor : clientPlayer.getInventory().armor) {
            NbtCompound aNbt = armor.getSubNbt(Tierify.NBT_SUBTAG_KEY);
            if (aNbt != null && aNbt.getString(Tierify.NBT_SUBTAG_DATA_KEY).equals(myTier)) matchCount++;
        }
        return matchCount >= 4;
    }

    private void fixAttackSpeedText(List<Text> tooltip) {
        double baseSpeed = 4.0;
        double addedValue = 0.0;
        double multiplyBase = 0.0;
        double multiplyTotal = 0.0;

        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = this.getAttributeModifiers(EquipmentSlot.MAINHAND);

        if (modifiers.containsKey(EntityAttributes.GENERIC_ATTACK_SPEED)) {
            for (EntityAttributeModifier mod : modifiers.get(EntityAttributes.GENERIC_ATTACK_SPEED)) {
                if (mod.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    addedValue += mod.getValue();
                } else if (mod.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE) {
                    multiplyBase += mod.getValue();
                } else if (mod.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    multiplyTotal += mod.getValue();
                }
            }
        }

        double speed = (baseSpeed + addedValue) * (1.0 + multiplyBase) * (1.0 + multiplyTotal);

        String correctLabel;
        if (speed >= 3.0) correctLabel = "§2Very Fast";
        else if (speed >= 2.0) correctLabel = "§aFast";
        else if (speed >= 1.2) correctLabel = "§fMedium";
        else if (speed > 0.6) correctLabel = "§cSlow";
        else correctLabel = "§4Very Slow";

        for (int i = 0; i < tooltip.size(); i++) {
            Text line = tooltip.get(i);
            String text = line.getString();

            if (text.contains("Fast") || text.contains("Slow") || text.contains("Medium")) {
                tooltip.set(i, replaceSpeedTextRecursively(line, correctLabel));
                break;
            }
        }
    }

    private Text replaceSpeedTextRecursively(Text original, String replacementLabel) {
        MutableText newNode = processSingleNode(original, replacementLabel);
        for (Text sibling : original.getSiblings()) {
            newNode.append(replaceSpeedTextRecursively(sibling, replacementLabel));
        }
        return newNode;
    }

    private MutableText processSingleNode(Text node, String replacementLabel) {
        // Optimization: Create a shallow copy directly using content and style.
        MutableText copy = MutableText.of(node.getContent()).setStyle(node.getStyle());

        String content = copy.getString();
        String[] targets = {"Very Fast", "Very Slow", "Fast", "Slow", "Medium"};

        for (String target : targets) {
            if (content.contains(target)) {
                String newContent = content.replace(target, replacementLabel);
                return Text.literal(newContent).setStyle(node.getStyle());
            }
        }
        return copy;
    }
}
