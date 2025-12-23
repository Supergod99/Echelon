package elocindev.tierify.mixin.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
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

@Mixin(value = LivingEntity.class, priority = 3000)
public abstract class LethalityScalingFixMixin {

    @Unique private static final Logger LOG = LogUtils.getLogger();

    // Flip to true while testing; set false once confirmed.
    @Unique private static final boolean DEBUG = true;

    @Unique private static final Identifier BRUTALITY_LETHALITY_ID = new Identifier("brutality", "lethality");
    @Unique private static final double EPS = 1.0e-6;

    /**
     * When Apothic's ALCombatRules is present, negative armor is treated as "unarmored" (armor<=0 => amount),
     * so allowing big negative values is unnecessary and can amplify instability elsewhere.
     */
    @Unique private static final float MIN_EFFECTIVE_ARMOR_APOTHIC = 0.0F;

    /**
     * If Apothic is NOT present, we can allow a small negative window (bonus damage) but still clamp.
     */
    @Unique private static final float MIN_EFFECTIVE_ARMOR_VANILLA = -30.0F;

    // Reflection cache for ALCombatRules.getDamageAfterArmor(...)
    @Unique private static volatile boolean AL_LOOKED_UP = false;
    @Unique private static volatile Method AL_GET_DAMAGE_AFTER_ARMOR = null;

    /* -------------------------------------------------------
     * Primary: Yarn/Fabric name (explicit descriptor)
     * ------------------------------------------------------- */
    @Inject(
        method = "applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F",
        at = @At("HEAD"),
        cancellable = true
    )
    private void echelon$fixLethality_applyArmorToDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        Float out = echelon$computeLethalityAdjustedDamage(source, amount);
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
        Float out = echelon$computeLethalityAdjustedDamage(source, amount);
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
        Float out = echelon$computeLethalityAdjustedDamage(source, amount);
        if (out != null) cir.setReturnValue(out);
    }

    @Unique
    private Float echelon$computeLethalityAdjustedDamage(DamageSource source, float amount) {
        if (source == null) return null;
        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)) return null;
        if (!Float.isFinite(amount) || amount <= 0.0F) return null;

        if (!(source.getAttacker() instanceof PlayerEntity attacker)) return null;

        EntityAttribute lethalityAttr = Registries.ATTRIBUTE.get(BRUTALITY_LETHALITY_ID);
        if (lethalityAttr == null) return null;

        EntityAttributeInstance lethalityInst = attacker.getAttributeInstance(lethalityAttr);
        if (lethalityInst == null) return null;

        double lethality = lethalityInst.getValue();
        if (!Double.isFinite(lethality) || Math.abs(lethality) < EPS) {
            // Critical: with no lethality, we do NOT override anything.
            return null;
        }

        LivingEntity target = (LivingEntity) (Object) this;

        float armor = target.getArmor();
        float toughness = (float) target.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        float effectiveArmor = armor - (float) lethality;
        if (!Float.isFinite(effectiveArmor)) return null;

        // If Apothic is present, clamp to >= 0 to match its semantics (armor<=0 => amount).
        boolean hasApothic = echelon$ensureALCombatRulesLookup();
        float minArmor = hasApothic ? MIN_EFFECTIVE_ARMOR_APOTHIC : MIN_EFFECTIVE_ARMOR_VANILLA;
        if (effectiveArmor < minArmor) effectiveArmor = minArmor;

        Float out = hasApothic
            ? echelon$callALCombatRules(target, source, amount, effectiveArmor, toughness)
            : null;

        if (out == null) {
            // Fallback to vanilla/Yarn formula
            out = DamageUtil.getDamageLeft(amount, effectiveArmor, toughness);
        }

        if (!Float.isFinite(out)) return null;

        if (DEBUG) {
            LOG.info("[Echelon] LethalityFix: amount={} lethality={} armor={} tough={} effArmor={} apothic={} -> out={}",
                amount, lethality, armor, toughness, effectiveArmor, hasApothic, out);
        }

        return out;
    }

    /**
     * Attempts to locate dev.shadowsoffire.attributeslib.api.ALCombatRules#getDamageAfterArmor(...) at runtime.
     * Returns true if found and cached.
     */
    @Unique
    private static boolean echelon$ensureALCombatRulesLookup() {
        if (AL_LOOKED_UP) return AL_GET_DAMAGE_AFTER_ARMOR != null;
        AL_LOOKED_UP = true;

        try {
            Class<?> cls = Class.forName("dev.shadowsoffire.attributeslib.api.ALCombatRules");
            for (Method m : cls.getDeclaredMethods()) {
                if (!m.getName().equals("getDamageAfterArmor")) continue;
                if (m.getReturnType() != float.class) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p.length != 5) continue;

                // We only accept the method if the first two params match our runtime types by name,
                // otherwise invocation will fail under Connector mapping mismatches.
                // p[0] should be LivingEntity, p[1] should be DamageSource (whatever mapping is active).
                if (!p[0].getName().endsWith("LivingEntity")) continue;
                if (!p[1].getName().endsWith("DamageSource")) continue;

                m.setAccessible(true);
                AL_GET_DAMAGE_AFTER_ARMOR = m;
                break;
            }
        } catch (Throwable ignored) {
            AL_GET_DAMAGE_AFTER_ARMOR = null;
        }

        return AL_GET_DAMAGE_AFTER_ARMOR != null;
    }

    /**
     * Calls ALCombatRules.getDamageAfterArmor via reflection.
     * Returns null if invocation fails (mapping mismatch, etc.).
     */
    @Unique
    private static Float echelon$callALCombatRules(LivingEntity target, DamageSource src, float amount, float armor, float toughness) {
        Method m = AL_GET_DAMAGE_AFTER_ARMOR;
        if (m == null) return null;
        try {
            Object r = m.invoke(null, target, src, amount, armor, toughness);
            return (r instanceof Float f) ? f : null;
        } catch (Throwable t) {
            // If this fails once (usually due to type mismatch), disable Apothic path for the session.
            AL_GET_DAMAGE_AFTER_ARMOR = null;
            return null;
        }
    }
}
