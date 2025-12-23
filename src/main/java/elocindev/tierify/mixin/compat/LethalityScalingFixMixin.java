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
import net.minecraft.entity.DamageUtil;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

/**
 * Connector-safe lethality fix (Fabric project, Forge mod via Sinytra):
 * - Does NOT reference Brutality classes
 * - Only activates when Brutality lethality attribute exists AND is non-zero
 * - Prevents 0/infinite damage by sanitizing + clamping
 * - Ensures lethality NEVER reduces damage: returns max(baselineAfterArmor, lethalityAdjustedAfterArmor)
 *
 * Notes:
 * - We intentionally focus ONLY on lethality here (per your request).
 * - Armor penetration remains handled by whatever mod currently owns it in your stack.
 */
@Mixin(value = LivingEntity.class, priority = 3000)
public abstract class LethalityScalingFixMixin {

    @Unique private static final Logger LOG = LogUtils.getLogger();

    // Flip true only if you want to verify numbers live.
    @Unique private static final boolean DEBUG = false;

    @Unique private static final Identifier BRUTALITY_LETHALITY_ID = new Identifier("brutality", "lethality");

    @Unique private static final double EPS = 1.0e-6;
    @Unique private static final float MAX_DAMAGE_CAP = 1_000_000.0F;

    /* -------------------------------------------------------
     * Primary: Yarn/Fabric name
     * ------------------------------------------------------- */
    @Inject(method = "applyArmorToDamage", at = @At("HEAD"), cancellable = true)
    private void echelon$fixLethality_applyArmorToDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        Float out = echelon$computeLethalityOnlyAdjustedDamage(source, amount);
        if (out != null) cir.setReturnValue(out);
    }

    /* -------------------------------------------------------
     * Secondary: Mojmap/Forge name (optional, Connector-safe)
     * ------------------------------------------------------- */
    @Dynamic("Present in some runtimes / mappings; Connector-safe optional hook")
    @Inject(
        method = "getDamageAfterArmorAbsorb(Lnet/minecraft/entity/damage/DamageSource;F)F",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        expect = 0
    )
    private void echelon$fixLethality_getDamageAfterArmorAbsorb(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        Float out = echelon$computeLethalityOnlyAdjustedDamage(source, amount);
        if (out != null) cir.setReturnValue(out);
    }

    /* -------------------------------------------------------
     * Tertiary: Intermediary-ish fallback (optional)
     * ------------------------------------------------------- */
    @Dynamic("Fallback for intermediary-named armor reduction method in some pipelines")
    @Inject(
        method = "method_26323(Lnet/minecraft/entity/damage/DamageSource;F)F",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        expect = 0
    )
    private void echelon$fixLethality_method_26323(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        Float out = echelon$computeLethalityOnlyAdjustedDamage(source, amount);
        if (out != null) cir.setReturnValue(out);
    }

    /**
     * Returns:
     * - null if we should NOT override vanilla / other mods
     * - a finite float damage value if we should override
     */
    @Unique
    private Float echelon$computeLethalityOnlyAdjustedDamage(DamageSource source, float amount) {
        if (source == null) return null;

        // Don't touch bypass-armor damage.
        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)) return null;

        // Don't touch non-finite or non-positive damage.
        if (amount <= 0.0F) return null;

        // Attacker must be a LivingEntity to have attributes.
        if (!(source.getAttacker() instanceof LivingEntity attacker)) return null;

        // Only run if Brutality lethality attribute exists in the registry.
        EntityAttribute lethalityAttr = Registries.ATTRIBUTE.get(BRUTALITY_LETHALITY_ID);
        if (lethalityAttr == null) return null;

        EntityAttributeInstance lethalityInst = attacker.getAttributeInstance(lethalityAttr);
        if (lethalityInst == null) return null;

        double lethality = lethalityInst.getValue();
        if (!Double.isFinite(lethality) || Math.abs(lethality) < EPS) {
            // IMPORTANT: if there's no lethality, do NOT override anything.
            return null;
        }

        // Sanitize incoming amount ONLY if we're actively overriding.
        if (!Float.isFinite(amount)) {
            if (Float.isNaN(amount)) return null;
            amount = MAX_DAMAGE_CAP;
        }

        LivingEntity target = (LivingEntity) (Object) this;

        float armor = target.getArmor();
        float toughness = (float) target.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        // Clamp armor to a safe range for stability.
        if (!Float.isFinite(armor)) return null;
        if (!Float.isFinite(toughness)) toughness = 0.0F;

        // Baseline: what would damage-after-armor be WITHOUT lethality?
        float baselineArmor = Math.max(0.0F, armor);
        float baselineOut = echelon$damageAfterArmorCompat(target, source, amount, baselineArmor, toughness);
        if (!Float.isFinite(baselineOut)) return null;
        if (baselineOut < 0.0F) baselineOut = 0.0F;

        // Lethality-adjusted: reduce effective armor, but NEVER below 0 (keeps your “no more 0/infinite” win).
        float effectiveArmor = armor - (float) lethality;
        if (!Float.isFinite(effectiveArmor)) return null;
        if (effectiveArmor < 0.0F) effectiveArmor = 0.0F;

        float adjustedOut = echelon$damageAfterArmorCompat(target, source, amount, effectiveArmor, toughness);
        if (!Float.isFinite(adjustedOut)) {
            // If something went weird downstream, fall back to baseline (never worse than normal).
            return baselineOut;
        }
        if (adjustedOut < 0.0F) adjustedOut = 0.0F;

        // Key guarantee: lethality should never reduce damage.
        float out = Math.max(baselineOut, adjustedOut);

        if (DEBUG) {
            LOG.info("[Echelon] LethalityFix: lethality={} armor={} effArmor={} amount={} baseline={} adjusted={} out={}",
                lethality, armor, effectiveArmor, amount, baselineOut, adjustedOut, out);
        }

        return out;
    }

    /**
     * Uses AttributesLib / ApothicAttributes' ALCombatRules armor formula when present, otherwise vanilla.
     * Connector-safe via reflection.
     */
    @Unique
    private static float echelon$damageAfterArmorCompat(LivingEntity target, DamageSource source, float damage, float armor, float toughness) {
        // Try AttributesLib first (reflection).
        try {
            Class<?> cls = Class.forName("dev.shadowsoffire.attributeslib.api.ALCombatRules");

            // Prefer the 5-arg overload: getDamageAfterArmor(LivingEntity, DamageSource, float, float, float)
            for (Method m : cls.getMethods()) {
                if (!m.getName().equals("getDamageAfterArmor")) continue;

                Class<?>[] p = m.getParameterTypes();
                if (p.length == 5
                    && p[0].isInstance(target)
                    && p[1].isInstance(source)
                    && p[2] == float.class
                    && p[3] == float.class
                    && p[4] == float.class) {

                    Object r = m.invoke(null, target, source, damage, armor, toughness);
                    if (r instanceof Float f) return f;
                }
            }
        } catch (Throwable ignored) {
            // Fall through to vanilla.
        }

        return DamageUtil.getDamageLeft(damage, armor, toughness);
    }
}
