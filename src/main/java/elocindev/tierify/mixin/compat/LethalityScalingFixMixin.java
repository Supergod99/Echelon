package elocindev.tierify.mixin.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Connector-safe lethality fix:
 * - Does NOT reference Brutality classes.
 * - Only activates when Brutality's lethality attribute exists AND is non-zero.
 * - Tries multiple target method names (Yarn/Mojmap/intermediary) with require=0.
 * - Uses logger (shows in latest.log), not System.out.
 */
@Mixin(value = LivingEntity.class, priority = 3000)
public abstract class LethalityScalingFixMixin {

    @Unique private static final Logger ECHELON_LOG = LogUtils.getLogger();

    // Flip to true temporarily while validating that the injector is firing.
    @Unique private static final boolean DEBUG = true;

    @Unique private static final Identifier BRUTALITY_LETHALITY_ID = new Identifier("brutality", "lethality");

    /**
     * How far below zero we allow armor to go after lethality.
     * Negative armor increases damage; very negative values can cause instability with other mods.
     */
    @Unique private static final float MIN_EFFECTIVE_ARMOR = -30.0F;

    @Unique private static final double EPS = 1.0e-6;

    /**
     * Dual/tri-method strategy in one injector:
     * - Yarn/Fabric: applyArmorToDamage
     * - Mojmap/Forge: getDamageAfterArmorAbsorb
     * - Intermediary fallback sometimes seen in remapped environments: method_26323
     *
     * require=0 ensures "missing method" does not crash under Connector.
     */
    @Inject(
        method = {
            "applyArmorToDamage",
            "getDamageAfterArmorAbsorb",
            // Descriptor variant for Mojmap environments (safe as a string; no direct class refs needed):
            "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F",
            // Intermediary-ish fallback:
            "method_26323"
        },
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void echelon$fixLethality_anyArmorMethod(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        Float out = echelon$computeLethalityAdjustedDamage(source, amount);
        if (out != null) {
            cir.setReturnValue(out);
        }
    }

    /**
     * Returns:
     * - null if we should NOT override vanilla/other-mod behavior
     * - a finite float damage value if we should override
     */
    @Unique
    private Float echelon$computeLethalityAdjustedDamage(DamageSource source, float amount) {
        // Basic sanity: do not touch bypass-armor damage, non-finite, or non-positive damage
        if (source == null) return null;
        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)) return null;
        if (!Float.isFinite(amount) || amount <= 0.0F) return null;

        // Attacker must be a living entity (player, etc.). This is more robust than PlayerEntity-only.
        if (!(source.getAttacker() instanceof net.minecraft.entity.LivingEntity attacker)) return null;

        // Only run if Brutality's attribute actually exists in the registry
        EntityAttribute lethalityAttr = Registries.ATTRIBUTE.get(BRUTALITY_LETHALITY_ID);
        if (lethalityAttr == null) {
            if (DEBUG) ECHELON_LOG.info("[Echelon] LethalityFix: brutality:lethality not present in registry.");
            return null;
        }

        EntityAttributeInstance lethalityInst = attacker.getAttributeInstance(lethalityAttr);
        if (lethalityInst == null) return null;

        double lethality = lethalityInst.getValue();
        if (!Double.isFinite(lethality) || Math.abs(lethality) < EPS) {
            // Critical behavior: with no lethality, we DO NOT override anything.
            return null;
        }

        LivingEntity target = (LivingEntity) (Object) this;

        float armor = target.getArmor();
        float toughness = (float) target.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        // Apply lethality as a direct armor reduction.
        float effectiveArmor = armor - (float) lethality;

        // Clamp only on the negative side.
        if (!Float.isFinite(effectiveArmor)) return null;
        if (effectiveArmor < MIN_EFFECTIVE_ARMOR) effectiveArmor = MIN_EFFECTIVE_ARMOR;

        float out = echelon$vanillaArmorFormula(amount, effectiveArmor, toughness);
        if (!Float.isFinite(out)) return null;

        if (DEBUG) {
            ECHELON_LOG.info(
                "[Echelon] LethalityFix OVERRIDE: amount={} lethality={} armor={} tough={} effArmor={} -> out={}",
                amount, lethality, armor, toughness, effectiveArmor, out
            );
        }

        return out;
    }

    /**
     * Vanilla armor reduction formula (1.20.x equivalent to DamageUtil/CombatRules).
     * Implemented locally to avoid mapping/classname mismatches under Connector.
     */
    @Unique
    private static float echelon$vanillaArmorFormula(float damage, float armor, float toughness) {
        float f = 2.0F + toughness / 4.0F;
        float g = MathHelper.clamp(armor - damage / f, armor * 0.2F, 20.0F);
        return damage * (1.0F - g / 25.0F);
    }
}
