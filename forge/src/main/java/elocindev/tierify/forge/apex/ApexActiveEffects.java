package elocindev.tierify.forge.apex;

import elocindev.tierify.TierifyCommon;
import elocindev.tierify.util.StarApexUtils;
import elocindev.tierify.forge.registry.ForgeMobEffectRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

public final class ApexActiveEffects {

    private static final String TAG_ARMOR_BOOST_UNTIL = "tierify_apex_armor_boost_until";
    private static final String TAG_ARMOR_BOOST_COOLDOWN_UNTIL = "tierify_apex_armor_boost_cooldown_until";
    private static final String TAG_ARMOR_BOOST_REFORGE = "tierify_apex_armor_boost_reforge";
    private static final String TAG_SLOW_TIME_UNTIL = "tierify_apex_slow_time_until";
    private static final String TAG_SLOW_TIME_COOLDOWN_UNTIL = "tierify_apex_slow_time_cooldown_until";
    private static final String TAG_NO_DAMAGE_LAST_HURT = "tierify_apex_no_damage_last_hurt";
    private static final String TAG_NO_DAMAGE_ACTIVE = "tierify_apex_no_damage_active";
    private static final String TAG_UNBREAKABLE_ARMOR_ACTIVE = "tierify_apex_unbreakable_armor_active";
    private static final String TAG_RANGED_MOMENTUM_STACKS = "tierify_apex_ranged_momentum_stacks";
    private static final String TAG_RANGED_MOMENTUM_UNTIL = "tierify_apex_ranged_momentum_until";
    private static final String TAG_RANGED_MOMENTUM_WEAPON = "tierify_apex_ranged_momentum_weapon";

    private static final ResourceLocation NO_DAMAGE_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_2");
    private static final ResourceLocation SLOW_TIME_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_5");
    private static final ResourceLocation UNBREAKABLE_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_6");
    private static final ResourceLocation RANGED_MOMENTUM_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_7");

    private static final ResourceLocation ARROW_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath("attributeslib", "arrow_damage");
    private static final ResourceLocation DRAW_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("attributeslib", "draw_speed");
    private static final ResourceLocation ARROW_VELOCITY_ID =
            ResourceLocation.fromNamespaceAndPath("attributeslib", "arrow_velocity");

    private static final int MOMENTUM_DURATION_TICKS = 10 * 20;
    private static final int MOMENTUM_MAX_STACKS = 20;
    private static final int MOMENTUM_STACKS_PER_HIT = 1;
    private static final int MOMENTUM_BONUS_STACKS_PER_KILL = 2;
    private static final double SLOW_TIME_RADIUS = 16.0D;
    private static final double APEX_ARMOR_BOOST_MULTIPLIER = 0.50D;
    private static final double MOMENTUM_ARROW_DAMAGE_PER_STACK = 0.015D;
    private static final double MOMENTUM_DRAW_SPEED_PER_STACK = 0.010D;
    private static final double MOMENTUM_ARROW_VELOCITY_PER_STACK = 0.0075D;

    private static final UUID ARMOR_BOOST_UUID = UUID.fromString("6c8c7c7f-4f9e-4a36-9bda-3c6e77cc37e6");
    private static final UUID MOMENTUM_ARROW_DAMAGE_UUID = UUID.fromString("0700e2c8-aef2-423a-a460-43ace453ee49");
    private static final UUID MOMENTUM_DRAW_SPEED_UUID = UUID.fromString("d34ac564-ebd1-4e26-aaf8-d3c9ec9f8d19");
    private static final UUID MOMENTUM_ARROW_VELOCITY_UUID = UUID.fromString("5efa213e-9d70-48fa-a779-bf26052ede59");

    private static final ConcurrentMap<ResourceLocation, SlowTimeSnapshot> SLOW_TIME_SNAPSHOT_CACHE = new ConcurrentHashMap<>();

    private ApexActiveEffects() {}

    private record SlowTimeSnapshot(long tick, List<AABB> zones) {}

    public static ResourceLocation getMatchingApexArmorSetReforge(ServerPlayer player) {
        if (player == null) return null;

        ResourceLocation match = null;
        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !StarApexUtils.isApex(stack)) return null;

            ResourceLocation id = ApexEffectRegistry.getReforgeId(stack);
            if (id == null) return null;
            if (match == null) {
                match = id;
            } else if (!match.equals(id)) {
                return null;
            }
        }
        return match;
    }

    public static void applyArmorBoost(ServerPlayer player,
                                       ResourceLocation reforgeId,
                                       int durationTicks,
                                       int cooldownTicks) {
        if (player == null || reforgeId == null || durationTicks <= 0) return;

        long now = player.level().getGameTime();
        long cooldownUntil = player.getPersistentData().getLong(TAG_ARMOR_BOOST_COOLDOWN_UNTIL);
        if (now < cooldownUntil) return;

        CompoundTag data = player.getPersistentData();
        data.putLong(TAG_ARMOR_BOOST_UNTIL, now + durationTicks);
        data.putLong(TAG_ARMOR_BOOST_COOLDOWN_UNTIL, now + Math.max(cooldownTicks, durationTicks));
        data.putString(TAG_ARMOR_BOOST_REFORGE, reforgeId.toString());

        AttributeInstance attr = player.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_BOOST_UUID);
            attr.addTransientModifier(new AttributeModifier(
                    ARMOR_BOOST_UUID,
                    "tierify_apex_armor_boost",
                    APEX_ARMOR_BOOST_MULTIPLIER,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        player.addEffect(new MobEffectInstance(
                ForgeMobEffectRegistry.APEX_ARMOR_BOOST.get(),
                durationTicks,
                0,
                false,
                false,
                true
        ));
        player.level().playSound(null,
                player.blockPosition(),
                SoundEvents.BEACON_POWER_SELECT,
                SoundSource.PLAYERS,
                1.0f,
                1.0f);
    }

    public static void applySlowTimeAura(ServerPlayer player, int durationTicks, int cooldownTicks) {
        if (player == null || durationTicks <= 0) return;

        long now = player.level().getGameTime();
        long cooldownUntil = player.getPersistentData().getLong(TAG_SLOW_TIME_COOLDOWN_UNTIL);
        if (now < cooldownUntil) return;

        CompoundTag data = player.getPersistentData();
        data.putLong(TAG_SLOW_TIME_UNTIL, now + durationTicks);
        data.putLong(TAG_SLOW_TIME_COOLDOWN_UNTIL, now + Math.max(cooldownTicks, durationTicks));
        player.addEffect(new MobEffectInstance(
                ForgeMobEffectRegistry.APEX_SLOW_TIME.get(),
                durationTicks,
                0,
                false,
                false,
                true
        ));

        player.level().playSound(null,
                player.blockPosition(),
                SoundEvents.BEACON_POWER_SELECT,
                SoundSource.PLAYERS,
                1.0f,
                0.9f);
    }

    public static void applyNoDamageShield(ServerPlayer player, int delayTicks, float shieldFraction) {
        if (player == null || delayTicks <= 0 || shieldFraction <= 0.0f) return;

        long now = player.level().getGameTime();
        long lastHurt = player.getPersistentData().getLong(TAG_NO_DAMAGE_LAST_HURT);
        if (now - lastHurt < delayTicks) return;

        float maxHealth = player.getMaxHealth();
        if (maxHealth <= 0.0f) return;

        float targetAbsorption = maxHealth * shieldFraction;
        if (player.getAbsorptionAmount() < targetAbsorption) {
            player.setAbsorptionAmount(targetAbsorption);
        }
    }

    public static void markPlayerHurt(ServerPlayer player) {
        if (player == null) return;
        player.getPersistentData().putLong(TAG_NO_DAMAGE_LAST_HURT, player.level().getGameTime());
    }

    public static void tick(ServerPlayer player) {
        if (player == null) return;

        CompoundTag data = player.getPersistentData();
        if (data.contains(TAG_ARMOR_BOOST_UNTIL)) {
            long now = player.level().getGameTime();
            long until = data.getLong(TAG_ARMOR_BOOST_UNTIL);
            if (now >= until) {
                clearArmorBoost(player, data);
            }

            String idStr = data.getString(TAG_ARMOR_BOOST_REFORGE);
            ResourceLocation expected = ResourceLocation.tryParse(idStr);
            if (expected == null) {
                clearArmorBoost(player, data);
            } else {
                ResourceLocation current = getMatchingApexArmorSetReforge(player);
                if (current == null || !current.equals(expected)) {
                    clearArmorBoost(player, data);
                }
            }
        }

        if (data.contains(TAG_SLOW_TIME_UNTIL)) {
            long now = player.level().getGameTime();
            long until = data.getLong(TAG_SLOW_TIME_UNTIL);
            ResourceLocation current = getMatchingApexArmorSetReforge(player);
            if (now >= until || !SLOW_TIME_REFORGE.equals(current)) {
                data.remove(TAG_SLOW_TIME_UNTIL);
                player.removeEffect(ForgeMobEffectRegistry.APEX_SLOW_TIME.get());
            }

            long cooldownUntil = data.getLong(TAG_SLOW_TIME_COOLDOWN_UNTIL);
            if (cooldownUntil <= now && !data.contains(TAG_SLOW_TIME_UNTIL)) {
                data.remove(TAG_SLOW_TIME_COOLDOWN_UNTIL);
            }
        }

        tickPassiveEffects(player);
    }

    public static boolean shouldApplySlowTime(LivingEntity target) {
        if (target == null || !target.isAlive()) return false;
        if (target.level().isClientSide()) return false;
        if (target instanceof Player) return false;
        if (!(target.level() instanceof ServerLevel level)) return false;

        SlowTimeSnapshot snapshot = getSlowTimeSnapshot(level);
        if (snapshot.zones().isEmpty()) return false;

        AABB targetBounds = target.getBoundingBox();
        for (AABB zone : snapshot.zones()) {
            if (zone.intersects(targetBounds)) {
                return true;
            }
        }
        return false;
    }

    public static void setUnbreakableArmorActive(ServerPlayer player, boolean active) {
        if (player == null) return;
        CompoundTag data = player.getPersistentData();
        if (active) {
            data.putBoolean(TAG_UNBREAKABLE_ARMOR_ACTIVE, true);
        } else {
            data.remove(TAG_UNBREAKABLE_ARMOR_ACTIVE);
        }
    }

    public static boolean isUnbreakableArmorActive(ServerPlayer player) {
        if (player == null) return false;
        return player.getPersistentData().getBoolean(TAG_UNBREAKABLE_ARMOR_ACTIVE);
    }

    public static void onRangedProjectileHit(ServerPlayer player) {
        addRangedMomentumStacks(player, MOMENTUM_STACKS_PER_HIT);
    }

    public static void onRangedProjectileMiss(ServerPlayer player) {
        if (player == null) return;
        clearRangedMomentum(player);
    }

    public static void onRangedProjectileKill(ServerPlayer player) {
        addRangedMomentumStacks(player, MOMENTUM_BONUS_STACKS_PER_KILL);
    }

    private static void clearArmorBoost(ServerPlayer player, CompoundTag data) {
        data.remove(TAG_ARMOR_BOOST_UNTIL);
        data.remove(TAG_ARMOR_BOOST_REFORGE);
        AttributeInstance attr = player.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_BOOST_UUID);
        }
        player.removeEffect(ForgeMobEffectRegistry.APEX_ARMOR_BOOST.get());

        long now = player.level().getGameTime();
        long cooldownUntil = data.getLong(TAG_ARMOR_BOOST_COOLDOWN_UNTIL);
        if (cooldownUntil <= now) {
            data.remove(TAG_ARMOR_BOOST_COOLDOWN_UNTIL);
        }
    }

    private static void tickPassiveEffects(ServerPlayer player) {
        ResourceLocation reforgeId = getMatchingApexArmorSetReforge(player);
        if (reforgeId == null) {
            clearNoDamageActive(player);
            clearUnbreakableArmorActive(player);
            clearRangedMomentum(player);
            return;
        }

        ApexEffect effect = ApexEffectRegistry.get(reforgeId);
        if (effect == null || effect.triggerType() != ApexEffect.ApexTriggerType.PASSIVE_TICK) {
            clearNoDamageActive(player);
            clearUnbreakableArmorActive(player);
            clearRangedMomentum(player);
            return;
        }

        if (NO_DAMAGE_REFORGE.equals(reforgeId)) {
            ensureNoDamageActive(player);
        } else {
            clearNoDamageActive(player);
        }

        if (UNBREAKABLE_REFORGE.equals(reforgeId)) {
            setUnbreakableArmorActive(player, true);
        } else {
            clearUnbreakableArmorActive(player);
        }

        if (RANGED_MOMENTUM_REFORGE.equals(reforgeId)) {
            tickRangedMomentum(player);
        } else {
            clearRangedMomentum(player);
        }

        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        effect.handler().apply(new ApexEffect.ApexEffectContext(player, stack, player.level(), null));
    }

    private static void ensureNoDamageActive(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        String active = data.getString(TAG_NO_DAMAGE_ACTIVE);
        if (NO_DAMAGE_REFORGE.toString().equals(active)) return;
        data.putString(TAG_NO_DAMAGE_ACTIVE, NO_DAMAGE_REFORGE.toString());
        data.putLong(TAG_NO_DAMAGE_LAST_HURT, player.level().getGameTime());
    }

    private static void clearNoDamageActive(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_NO_DAMAGE_ACTIVE)) return;
        data.remove(TAG_NO_DAMAGE_ACTIVE);
        data.putLong(TAG_NO_DAMAGE_LAST_HURT, player.level().getGameTime());
    }

    private static void clearUnbreakableArmorActive(ServerPlayer player) {
        setUnbreakableArmorActive(player, false);
    }

    private static SlowTimeSnapshot getSlowTimeSnapshot(ServerLevel level) {
        long now = level.getGameTime();
        ResourceLocation dimensionId = level.dimension().location();
        SlowTimeSnapshot cached = SLOW_TIME_SNAPSHOT_CACHE.get(dimensionId);
        if (cached != null && cached.tick() == now) {
            return cached;
        }

        List<AABB> zones = new ArrayList<>();
        for (ServerPlayer player : level.players()) {
            if (isSlowTimeSource(player)) {
                zones.add(player.getBoundingBox().inflate(SLOW_TIME_RADIUS));
            }
        }
        SlowTimeSnapshot rebuilt = new SlowTimeSnapshot(now, List.copyOf(zones));
        SLOW_TIME_SNAPSHOT_CACHE.put(dimensionId, rebuilt);
        return rebuilt;
    }

    private static boolean isSlowTimeSource(ServerPlayer player) {
        if (player == null || !player.isAlive()) return false;
        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_SLOW_TIME_UNTIL)) return false;
        long until = data.getLong(TAG_SLOW_TIME_UNTIL);
        if (until <= player.level().getGameTime()) return false;
        return SLOW_TIME_REFORGE.equals(getMatchingApexArmorSetReforge(player));
    }

    private static void tickRangedMomentum(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        String currentWeapon = getMainHandWeaponKey(player);
        String trackedWeapon = data.getString(TAG_RANGED_MOMENTUM_WEAPON);
        if (!trackedWeapon.isEmpty() && !trackedWeapon.equals(currentWeapon)) {
            clearRangedMomentum(player);
            return;
        }

        int stacks = data.getInt(TAG_RANGED_MOMENTUM_STACKS);
        if (stacks <= 0) {
            clearRangedMomentum(player);
            return;
        }

        long until = data.getLong(TAG_RANGED_MOMENTUM_UNTIL);
        if (until <= player.level().getGameTime()) {
            clearRangedMomentum(player);
            return;
        }

        int remaining = (int) Math.min(Integer.MAX_VALUE, until - player.level().getGameTime());
        stacks = Math.min(stacks, MOMENTUM_MAX_STACKS);
        data.putInt(TAG_RANGED_MOMENTUM_STACKS, stacks);
        data.putString(TAG_RANGED_MOMENTUM_WEAPON, currentWeapon);
        applyRangedMomentumModifiers(player, stacks);
        syncRangedMomentumEffect(player, stacks, remaining);
    }

    private static void addRangedMomentumStacks(ServerPlayer player, int addStacks) {
        if (player == null || addStacks <= 0) return;

        if (!RANGED_MOMENTUM_REFORGE.equals(getMatchingApexArmorSetReforge(player))) {
            clearRangedMomentum(player);
            return;
        }

        CompoundTag data = player.getPersistentData();
        String currentWeapon = getMainHandWeaponKey(player);
        String trackedWeapon = data.getString(TAG_RANGED_MOMENTUM_WEAPON);
        if (!trackedWeapon.isEmpty() && !trackedWeapon.equals(currentWeapon)) {
            clearRangedMomentum(player);
            data = player.getPersistentData();
        }

        int currentStacks = data.getInt(TAG_RANGED_MOMENTUM_STACKS);
        int nextStacks = Math.min(MOMENTUM_MAX_STACKS, currentStacks + addStacks);
        data.putInt(TAG_RANGED_MOMENTUM_STACKS, nextStacks);
        long until = player.level().getGameTime() + MOMENTUM_DURATION_TICKS;
        data.putLong(TAG_RANGED_MOMENTUM_UNTIL, until);
        data.putString(TAG_RANGED_MOMENTUM_WEAPON, currentWeapon);
        applyRangedMomentumModifiers(player, nextStacks);
        syncRangedMomentumEffect(player, nextStacks, MOMENTUM_DURATION_TICKS);
    }

    private static void clearRangedMomentum(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.remove(TAG_RANGED_MOMENTUM_STACKS);
        data.remove(TAG_RANGED_MOMENTUM_UNTIL);
        data.remove(TAG_RANGED_MOMENTUM_WEAPON);
        clearRangedMomentumModifiers(player);
        player.removeEffect(ForgeMobEffectRegistry.APEX_RANGED_MOMENTUM.get());
    }

    private static void clearRangedMomentumModifiers(ServerPlayer player) {
        applyTransientMultiplier(player, ARROW_DAMAGE_ID, MOMENTUM_ARROW_DAMAGE_UUID, "tierify_apex_ranged_momentum_damage", 0.0D);
        applyTransientMultiplier(player, DRAW_SPEED_ID, MOMENTUM_DRAW_SPEED_UUID, "tierify_apex_ranged_momentum_draw_speed", 0.0D);
        applyTransientMultiplier(player, ARROW_VELOCITY_ID, MOMENTUM_ARROW_VELOCITY_UUID, "tierify_apex_ranged_momentum_velocity", 0.0D);
    }

    private static void applyRangedMomentumModifiers(ServerPlayer player, int stacks) {
        double damageBonus = Math.max(0.0D, stacks * MOMENTUM_ARROW_DAMAGE_PER_STACK);
        double drawSpeedBonus = Math.max(0.0D, stacks * MOMENTUM_DRAW_SPEED_PER_STACK);
        double velocityBonus = Math.max(0.0D, stacks * MOMENTUM_ARROW_VELOCITY_PER_STACK);
        applyTransientMultiplier(player, ARROW_DAMAGE_ID, MOMENTUM_ARROW_DAMAGE_UUID, "tierify_apex_ranged_momentum_damage", damageBonus);
        applyTransientMultiplier(player, DRAW_SPEED_ID, MOMENTUM_DRAW_SPEED_UUID, "tierify_apex_ranged_momentum_draw_speed", drawSpeedBonus);
        applyTransientMultiplier(player, ARROW_VELOCITY_ID, MOMENTUM_ARROW_VELOCITY_UUID, "tierify_apex_ranged_momentum_velocity", velocityBonus);
    }

    private static void applyTransientMultiplier(ServerPlayer player,
                                                 ResourceLocation attributeId,
                                                 UUID modifierId,
                                                 String modifierName,
                                                 double amount) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) return;
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(modifierId);
        if (amount == 0.0D) return;
        instance.addTransientModifier(new AttributeModifier(
                modifierId,
                modifierName,
                amount,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
    }

    private static String getMainHandWeaponKey(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return "";
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static void syncRangedMomentumEffect(ServerPlayer player, int stacks, int remainingTicks) {
        int amplifier = Math.max(0, Math.min(MOMENTUM_MAX_STACKS - 1, stacks - 1));
        int duration = Math.max(1, remainingTicks);
        MobEffectInstance current = player.getEffect(ForgeMobEffectRegistry.APEX_RANGED_MOMENTUM.get());
        if (current != null
                && current.getAmplifier() == amplifier
                && Math.abs(current.getDuration() - duration) <= 5) {
            return;
        }
        player.addEffect(new MobEffectInstance(
                ForgeMobEffectRegistry.APEX_RANGED_MOMENTUM.get(),
                duration,
                amplifier,
                false,
                false,
                true
        ));
    }
}
