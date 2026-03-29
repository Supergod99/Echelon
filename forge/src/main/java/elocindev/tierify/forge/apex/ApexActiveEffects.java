package elocindev.tierify.forge.apex;

import elocindev.tierify.TierifyCommon;
import elocindev.tierify.util.StarApexUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.OwnableEntity;
import elocindev.tierify.forge.registry.ForgeMobEffectRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;
import elocindev.tierify.forge.registry.ForgeAttributeRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public final class ApexActiveEffects {

    private static final String TAG_ARMOR_BOOST_UNTIL = "tierify_apex_armor_boost_until";
    private static final String TAG_ARMOR_BOOST_COOLDOWN_UNTIL = "tierify_apex_armor_boost_cooldown_until";
    private static final String TAG_ARMOR_BOOST_REFORGE = "tierify_apex_armor_boost_reforge";
    private static final String TAG_SLOW_TIME_UNTIL = "tierify_apex_slow_time_until";
    private static final String TAG_SLOW_TIME_COOLDOWN_UNTIL = "tierify_apex_slow_time_cooldown_until";
    private static final String TAG_NO_DAMAGE_LAST_HURT = "tierify_apex_no_damage_last_hurt";
    private static final String TAG_NO_DAMAGE_ACTIVE = "tierify_apex_no_damage_active";
    private static final String TAG_UNBREAKABLE_ARMOR_ACTIVE = "tierify_apex_unbreakable_armor_active";
    private static final String TAG_SUMMON_HEALTH_APPLIED = "tierify_apex_summon_health_applied";
    private static final String TAG_SPELL_PROC_COOLDOWN_PREFIX = "tierify_apex_spell_proc_cooldown_";
    private static final String TAG_ROLL_DR_UNTIL = "tierify_apex_roll_dr_until";
    private static final String TAG_ROLL_COUNTER_UNTIL = "tierify_apex_roll_counter_until";
    private static final String TAG_ROLL_COOLDOWN_UNTIL = "tierify_apex_roll_cooldown_until";
    private static final String TAG_ROLL_WAS_ROLLING = "tierify_apex_roll_was_rolling";
    private static final String TAG_RANGED_MOMENTUM_STACKS = "tierify_apex_ranged_momentum_stacks";
    private static final String TAG_RANGED_MOMENTUM_UNTIL = "tierify_apex_ranged_momentum_until";
    private static final String TAG_RANGED_MOMENTUM_WEAPON = "tierify_apex_ranged_momentum_weapon";
    private static final ResourceLocation NO_DAMAGE_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_2");
    private static final ResourceLocation SUMMON_APEX_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_10");
    private static final ResourceLocation SLOW_TIME_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_13");
    private static final ResourceLocation UNBREAKABLE_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_14");
    private static final ResourceLocation ROLL_COUNTER_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_3");
    private static final ResourceLocation RANGED_MOMENTUM_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_15");
    private static final ResourceLocation ARS_MANA_DISCOUNT_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_11");
    private static final ResourceLocation ARS_SPELL_DAMAGE_REFORGE =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_12");
    private static final ResourceLocation ARS_SPELL_POWER_ID =
            ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "generic.ars_spell_power");
    private static final ResourceLocation ARROW_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath("attributeslib", "arrow_damage");
    private static final ResourceLocation DRAW_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("attributeslib", "draw_speed");
    private static final ResourceLocation ARROW_VELOCITY_ID =
            ResourceLocation.fromNamespaceAndPath("attributeslib", "arrow_velocity");
    private static final TagKey<EntityType<?>> IRONS_SUMMONS_TAG = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "summons"));
    private static final TagKey<EntityType<?>> TRAVELOPTICS_SUMMONS_TAG = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("traveloptics", "summons"));
    private static final int SPELL_PROC_COOLDOWN_TICKS = 20 * 20;
    private static final int ROLL_EFFECT_COOLDOWN_TICKS = 10 * 20;
    private static final int ROLL_COUNTER_WINDOW_TICKS = 3 * 20;
    private static final int FALLBACK_ROLL_DURATION_TICKS = 10;
    private static final int MOMENTUM_DURATION_TICKS = 10 * 20;
    private static final int MOMENTUM_MAX_STACKS = 20;
    private static final int MOMENTUM_STACKS_PER_HIT = 1;
    private static final int MOMENTUM_BONUS_STACKS_PER_KILL = 2;
    private static final double SLOW_TIME_RADIUS = 16.0D;
    private static final double APEX_SUMMON_HEALTH_MULTIPLIER = 1.30D;
    private static final double APEX_ARMOR_BOOST_MULTIPLIER = 0.50D;
    private static final double ROLL_DR_MULTIPLIER = 0.60D;
    private static final double ROLL_COUNTER_DAMAGE_MULTIPLIER = 1.35D;
    private static final double MOMENTUM_ARROW_DAMAGE_PER_STACK = 0.015D;
    private static final double MOMENTUM_DRAW_SPEED_PER_STACK = 0.010D;
    private static final double MOMENTUM_ARROW_VELOCITY_PER_STACK = 0.0075D;
    private static final double ARS_MANA_COST_MULTIPLIER = 0.50D;
    private static final double ARS_APEX_DAMAGE_MULTIPLIER = 1.30D;
    private static final UUID ARMOR_BOOST_UUID = UUID.fromString("6c8c7c7f-4f9e-4a36-9bda-3c6e77cc37e6");
    private static final UUID SUMMON_HEALTH_UUID = UUID.fromString("18ec89c7-059f-4ef8-bfef-e4d46436979e");
    private static final UUID MOMENTUM_ARROW_DAMAGE_UUID = UUID.fromString("0700e2c8-aef2-423a-a460-43ace453ee49");
    private static final UUID MOMENTUM_DRAW_SPEED_UUID = UUID.fromString("d34ac564-ebd1-4e26-aaf8-d3c9ec9f8d19");
    private static final UUID MOMENTUM_ARROW_VELOCITY_UUID = UUID.fromString("5efa213e-9d70-48fa-a779-bf26052ede59");
    private static final ConcurrentMap<String, Method> NO_ARG_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Set<String> NO_ARG_METHOD_MISSING = ConcurrentHashMap.newKeySet();
    private static final ConcurrentMap<Class<?>, Method> SET_AMOUNT_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final Set<Class<?>> SET_AMOUNT_METHOD_MISSING = ConcurrentHashMap.newKeySet();
    private static final Set<String> ON_START_ROLLING_HOOKED = ConcurrentHashMap.newKeySet();
    private static final Set<String> ARS_COMPAT_HOOKED = ConcurrentHashMap.newKeySet();
    private static final ConcurrentMap<ResourceLocation, SlowTimeSnapshot> SLOW_TIME_SNAPSHOT_CACHE = new ConcurrentHashMap<>();
    private static volatile boolean ROLLING_REFLECTION_INIT = false;
    private static volatile Class<?> ROLLING_ENTITY_CLASS;
    private static volatile Method GET_ROLL_MANAGER_METHOD;
    private static volatile Method IS_ROLLING_METHOD;
    private static volatile boolean ROLL_INVULN_FIELD_INIT = false;
    private static volatile Field ROLL_INVULN_FIELD;
    private static final Map<ResourceLocation, Set<String>> SPELL_SCHOOLS_BY_REFORGE = Map.ofEntries(
            Map.entry(ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_7"), Set.of("fire", "lightning")),
            Map.entry(ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_4"), Set.of("aqua", "ice")),
            Map.entry(ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_5"), Set.of("blood", "eldritch")),
            Map.entry(ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_9"), Set.of("ender", "sound")),
            Map.entry(ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_6"), Set.of("holy", "evocation")),
            Map.entry(ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_8"), Set.of("nature", "geomancy"))
    );
    private static final Map<String, Set<String>> SPELL_SCHOOL_ALIASES = Map.of(
            "geomancy", Set.of("geomancy", "geo")
    );

    private ApexActiveEffects() {}

    private record SlowTimeSnapshot(long tick, List<AABB> zones) {}

    public static void initSpellDamageCompat() {
        try {
            Class<?> eventClass = Class.forName("io.redspace.ironsspellbooks.api.events.SpellDamageEvent");
            registerSpellDamageListener(eventClass);
        } catch (ClassNotFoundException ignored) {
            // Spells mod not present; skip school-proc apex effects.
        }
    }

    public static void initArsSpellCompat() {
        if (!ARS_COMPAT_HOOKED.add("ars_spell_events")) return;
        try {
            Class<?> costEventClass = Class.forName("com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent");
            registerArsSpellCostListener(costEventClass);
        } catch (ClassNotFoundException ignored) {
            // Ars Nouveau not present; skip ars-specific apex effects.
        }

        try {
            Class<?> damagePreEventClass = Class.forName("com.hollingsworth.arsnouveau.api.event.SpellDamageEvent$Pre");
            registerArsSpellDamageListener(damagePreEventClass);
        } catch (ClassNotFoundException ignored) {
            // Ars Nouveau not present or event shape changed.
        }
    }

    public static void initCombatRollCompat() {
        if (!ON_START_ROLLING_HOOKED.add("combatroll_player_start")) return;

        try {
            Class<?> rollEventsClass = Class.forName("net.combatroll.api.event.ServerSideRollEvents");
            Class<?> listenerClass = Class.forName("net.combatroll.api.event.ServerSideRollEvents$PlayerStartRolling");
            Object event = rollEventsClass.getField("PLAYER_START_ROLLING").get(null);
            if (event == null) return;

            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                    listenerClass.getClassLoader(),
                    new Class<?>[] { listenerClass },
                    (ignoredProxy, method, args) -> {
                        if ("onPlayerStartedRolling".equals(method.getName())
                                && args != null
                                && args.length >= 1
                                && args[0] instanceof ServerPlayer player) {
                            onPlayerStartedRolling(player);
                        }
                        return null;
                    }
            );
            Method register = null;
            for (Method method : event.getClass().getMethods()) {
                if ("register".equals(method.getName()) && method.getParameterCount() == 1) {
                    register = method;
                    break;
                }
            }
            if (register == null) {
                return;
            }
            register.invoke(event, proxy);
        } catch (ReflectiveOperationException ignored) {
            // Combat Roll not present or API changed; keep feature disabled.
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerSpellDamageListener(Class<?> eventClass) {
        Consumer listener = event -> onSpellDamageEvent(event);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, (Class) eventClass, listener);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerArsSpellCostListener(Class<?> eventClass) {
        Consumer listener = event -> onArsSpellCostEvent(event);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, (Class) eventClass, listener);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerArsSpellDamageListener(Class<?> eventClass) {
        Consumer listener = event -> onArsSpellDamageEvent(event);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, (Class) eventClass, listener);
    }

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
        if (attr == null) return;
        attr.removeModifier(ARMOR_BOOST_UUID);
        attr.addTransientModifier(new AttributeModifier(
                ARMOR_BOOST_UUID,
                "tierify_apex_armor_boost",
                APEX_ARMOR_BOOST_MULTIPLIER,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));

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
        float currentAbsorption = player.getAbsorptionAmount();
        if (currentAbsorption < targetAbsorption) {
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
                tickPassiveEffects(player);
                updateSpellSurgeIndicator(player);
                return;
            }

            ResourceLocation current = getMatchingApexArmorSetReforge(player);
            if (current == null || !current.equals(expected)) {
                clearArmorBoost(player, data);
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

        if (shouldPollRollState(player, data)) {
            pollCombatRollState(player);
        } else if (data.contains(TAG_ROLL_WAS_ROLLING)) {
            data.remove(TAG_ROLL_WAS_ROLLING);
        }
        tickRollCounter(player);

        tickPassiveEffects(player);
        updateSpellSurgeIndicator(player);
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
        effect.handler().apply(new ApexEffect.ApexEffectContext(
                player,
                stack,
                player.level(),
                null
        ));
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

    private static void setUnbreakableArmorActive(ServerPlayer player, boolean active) {
        CompoundTag data = player.getPersistentData();
        if (active) {
            data.putBoolean(TAG_UNBREAKABLE_ARMOR_ACTIVE, true);
        } else {
            data.remove(TAG_UNBREAKABLE_ARMOR_ACTIVE);
        }
    }

    private static void clearUnbreakableArmorActive(ServerPlayer player) {
        setUnbreakableArmorActive(player, false);
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

    public static void tryEmpowerSummon(Entity entity) {
        if (!(entity instanceof LivingEntity summon)) return;
        if (summon.level().isClientSide()) return;
        if (!isSummonEntity(summon)) return;

        ServerPlayer owner = resolveSummonOwnerPlayer(summon);
        if (owner == null) return;

        double totalHealthMultiplier = getOwnerSummonHealthMultiplier(owner);
        if (SUMMON_APEX_REFORGE.equals(getMatchingApexArmorSetReforge(owner))) {
            totalHealthMultiplier *= APEX_SUMMON_HEALTH_MULTIPLIER;
        }
        if (totalHealthMultiplier <= 1.0D) return;

        applySummonHealthBonus(summon, totalHealthMultiplier);
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

    public static float getRollDamageTakenMultiplier(ServerPlayer player) {
        if (player == null) return 1.0f;
        if (!ROLL_COUNTER_REFORGE.equals(getMatchingApexArmorSetReforge(player))) return 1.0f;

        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        long until = data.getLong(TAG_ROLL_DR_UNTIL);
        if (until <= now) {
            data.remove(TAG_ROLL_DR_UNTIL);
            return 1.0f;
        }
        return (float) ROLL_DR_MULTIPLIER;
    }

    public static float applyRollCounterDamageBonus(ServerPlayer player, boolean eligibleHit, float baseAmount) {
        if (player == null || !eligibleHit || baseAmount <= 0.0f) return baseAmount;
        if (!ROLL_COUNTER_REFORGE.equals(getMatchingApexArmorSetReforge(player))) {
            clearRollCounter(player, false);
            return baseAmount;
        }

        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        long counterUntil = data.getLong(TAG_ROLL_COUNTER_UNTIL);
        if (counterUntil <= now) {
            data.remove(TAG_ROLL_COUNTER_UNTIL);
            player.removeEffect(ForgeMobEffectRegistry.APEX_ROLL_COUNTER.get());
            return baseAmount;
        }

        // Consume on first eligible hit; bonus never stacks.
        data.remove(TAG_ROLL_COUNTER_UNTIL);
        player.removeEffect(ForgeMobEffectRegistry.APEX_ROLL_COUNTER.get());
        return (float) (baseAmount * ROLL_COUNTER_DAMAGE_MULTIPLIER);
    }

    private static boolean isSlowTimeSource(ServerPlayer player) {
        if (player == null || !player.isAlive()) return false;
        CompoundTag data = player.getPersistentData();
        if (!data.contains(TAG_SLOW_TIME_UNTIL)) return false;
        long until = data.getLong(TAG_SLOW_TIME_UNTIL);
        if (until <= player.level().getGameTime()) return false;
        ResourceLocation current = getMatchingApexArmorSetReforge(player);
        return SLOW_TIME_REFORGE.equals(current);
    }

    private static void onPlayerStartedRolling(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) return;
        if (!ROLL_COUNTER_REFORGE.equals(getMatchingApexArmorSetReforge(player))) return;

        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        if (data.getLong(TAG_ROLL_COOLDOWN_UNTIL) > now) return;

        int rollDuration = resolveRollDurationTicks();
        data.putLong(TAG_ROLL_DR_UNTIL, now + rollDuration);
        data.putLong(TAG_ROLL_COUNTER_UNTIL, now + ROLL_COUNTER_WINDOW_TICKS);
        data.putLong(TAG_ROLL_COOLDOWN_UNTIL, now + ROLL_EFFECT_COOLDOWN_TICKS);
        syncRollCounterEffect(player, ROLL_COUNTER_WINDOW_TICKS);
    }

    private static void tickRollCounter(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();

        if (!ROLL_COUNTER_REFORGE.equals(getMatchingApexArmorSetReforge(player))) {
            clearRollCounter(player, false);
            return;
        }

        long drUntil = data.getLong(TAG_ROLL_DR_UNTIL);
        if (drUntil <= now) {
            data.remove(TAG_ROLL_DR_UNTIL);
        }

        long counterUntil = data.getLong(TAG_ROLL_COUNTER_UNTIL);
        if (counterUntil <= now) {
            data.remove(TAG_ROLL_COUNTER_UNTIL);
            player.removeEffect(ForgeMobEffectRegistry.APEX_ROLL_COUNTER.get());
        } else {
            int remaining = (int) Math.min(Integer.MAX_VALUE, counterUntil - now);
            syncRollCounterEffect(player, remaining);
        }

        long cooldownUntil = data.getLong(TAG_ROLL_COOLDOWN_UNTIL);
        if (cooldownUntil <= now && !data.contains(TAG_ROLL_COUNTER_UNTIL) && !data.contains(TAG_ROLL_DR_UNTIL)) {
            data.remove(TAG_ROLL_COOLDOWN_UNTIL);
        }
    }

    private static void clearRollCounter(ServerPlayer player, boolean clearCooldown) {
        CompoundTag data = player.getPersistentData();
        data.remove(TAG_ROLL_DR_UNTIL);
        data.remove(TAG_ROLL_COUNTER_UNTIL);
        data.remove(TAG_ROLL_WAS_ROLLING);
        if (clearCooldown) {
            data.remove(TAG_ROLL_COOLDOWN_UNTIL);
        }
        player.removeEffect(ForgeMobEffectRegistry.APEX_ROLL_COUNTER.get());
    }

    private static void pollCombatRollState(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        boolean rolling = isPlayerRolling(player) || isPlayerRollInvulnerable(player);
        boolean wasRolling = data.getBoolean(TAG_ROLL_WAS_ROLLING);
        if (rolling && !wasRolling) {
            onPlayerStartedRolling(player);
        }
        if (rolling) {
            data.putBoolean(TAG_ROLL_WAS_ROLLING, true);
        } else {
            data.remove(TAG_ROLL_WAS_ROLLING);
        }
    }

    private static boolean shouldPollRollState(ServerPlayer player, CompoundTag data) {
        if (ROLL_COUNTER_REFORGE.equals(getMatchingApexArmorSetReforge(player))) {
            return true;
        }
        return data.contains(TAG_ROLL_DR_UNTIL)
                || data.contains(TAG_ROLL_COUNTER_UNTIL)
                || data.contains(TAG_ROLL_COOLDOWN_UNTIL)
                || data.contains(TAG_ROLL_WAS_ROLLING);
    }

    private static boolean isPlayerRolling(ServerPlayer player) {
        if (!ensureRollingReflection()) return false;
        try {
            if (!ROLLING_ENTITY_CLASS.isInstance(player)) return false;
            Object rollManager = GET_ROLL_MANAGER_METHOD.invoke(player);
            if (rollManager == null) return false;
            Object rolling = IS_ROLLING_METHOD.invoke(rollManager);
            return (rolling instanceof Boolean b) && b;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean ensureRollingReflection() {
        if (ROLLING_REFLECTION_INIT) {
            return ROLLING_ENTITY_CLASS != null
                    && GET_ROLL_MANAGER_METHOD != null
                    && IS_ROLLING_METHOD != null;
        }
        synchronized (ApexActiveEffects.class) {
            if (ROLLING_REFLECTION_INIT) {
                return ROLLING_ENTITY_CLASS != null
                        && GET_ROLL_MANAGER_METHOD != null
                        && IS_ROLLING_METHOD != null;
            }
            try {
                ROLLING_ENTITY_CLASS = Class.forName("net.combatroll.internals.RollingEntity");
                Class<?> rollManagerClass = Class.forName("net.combatroll.internals.RollManager");
                GET_ROLL_MANAGER_METHOD = ROLLING_ENTITY_CLASS.getMethod("getRollManager");
                IS_ROLLING_METHOD = rollManagerClass.getMethod("isRolling");
            } catch (ReflectiveOperationException ignored) {
                ROLLING_ENTITY_CLASS = null;
                GET_ROLL_MANAGER_METHOD = null;
                IS_ROLLING_METHOD = null;
            } finally {
                ROLLING_REFLECTION_INIT = true;
            }
            return ROLLING_ENTITY_CLASS != null
                    && GET_ROLL_MANAGER_METHOD != null
                    && IS_ROLLING_METHOD != null;
        }
    }

    private static boolean isPlayerRollInvulnerable(ServerPlayer player) {
        Field field = findRollInvulnerableField(player);
        if (field == null) return false;
        try {
            Object raw = field.get(player);
            if (raw instanceof Integer ticks) {
                return ticks > 0;
            }
        } catch (IllegalAccessException ignored) {
            return false;
        }
        return false;
    }

    private static Field findRollInvulnerableField(ServerPlayer player) {
        if (ROLL_INVULN_FIELD_INIT) return ROLL_INVULN_FIELD;
        synchronized (ApexActiveEffects.class) {
            if (ROLL_INVULN_FIELD_INIT) return ROLL_INVULN_FIELD;
            Class<?> cls = player.getClass();
            while (cls != null) {
                try {
                    Field field = cls.getDeclaredField("invulnerableTicks");
                    field.setAccessible(true);
                    ROLL_INVULN_FIELD = field;
                    break;
                } catch (NoSuchFieldException ignored) {
                    cls = cls.getSuperclass();
                }
            }
            ROLL_INVULN_FIELD_INIT = true;
            return ROLL_INVULN_FIELD;
        }
    }

    private static int resolveRollDurationTicks() {
        try {
            Class<?> rollManager = Class.forName("net.combatroll.internals.RollManager");
            Method rollDuration = rollManager.getMethod("rollDuration");
            Object value = rollDuration.invoke(null);
            if (value instanceof Number number) {
                return Math.max(1, number.intValue());
            }
        } catch (ReflectiveOperationException ignored) {
            // Fall back to safe default if Combat Roll internals move.
        }
        return FALLBACK_ROLL_DURATION_TICKS;
    }

    private static void syncRollCounterEffect(ServerPlayer player, int remainingTicks) {
        MobEffectInstance current = player.getEffect(ForgeMobEffectRegistry.APEX_ROLL_COUNTER.get());
        if (current != null && Math.abs(current.getDuration() - remainingTicks) <= 5) {
            return;
        }
        player.addEffect(new MobEffectInstance(
                ForgeMobEffectRegistry.APEX_ROLL_COUNTER.get(),
                Math.max(1, remainingTicks),
                0,
                false,
                false,
                true
        ));
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

    private static void onArsSpellCostEvent(Object event) {
        if (event == null) return;
        Object context = readField(event, "context");
        ServerPlayer player = resolveArsCaster(context);
        if (player == null) return;

        ResourceLocation reforgeId = getMatchingApexArmorSetReforge(player);
        if (!ARS_MANA_DISCOUNT_REFORGE.equals(reforgeId)) return;

        Object currentCostObj = readField(event, "currentCost");
        if (!(currentCostObj instanceof Number number)) return;
        int currentCost = number.intValue();
        int discounted = (int) Math.floor(currentCost * ARS_MANA_COST_MULTIPLIER);
        writeField(event, "currentCost", Math.max(0, discounted));
    }

    private static void onArsSpellDamageEvent(Object event) {
        if (event == null) return;

        Object casterObj = readField(event, "caster");
        if (!(casterObj instanceof ServerPlayer player)) return;

        double multiplier = 1.0D + Math.max(0.0D, getPlayerAttributeValue(player, ARS_SPELL_POWER_ID));
        ResourceLocation reforgeId = getMatchingApexArmorSetReforge(player);
        if (ARS_SPELL_DAMAGE_REFORGE.equals(reforgeId)) {
            multiplier *= ARS_APEX_DAMAGE_MULTIPLIER;
        }
        if (multiplier <= 1.0D) return;

        Object damageObj = readField(event, "damage");
        if (!(damageObj instanceof Number number)) return;
        float scaled = (float) (number.floatValue() * multiplier);
        writeField(event, "damage", scaled);
    }

    private static void onSpellDamageEvent(Object event) {
        if (event == null) return;

        Object source = invokeNoArgs(event, "getSpellDamageSource");
        if (source == null) return;

        Object casterObj = invokeNoArgs(source, "getEntity");
        if (!(casterObj instanceof ServerPlayer player)) return;

        ResourceLocation reforgeId = getMatchingApexArmorSetReforge(player);
        if (reforgeId == null) return;

        Set<String> expectedSchoolPaths = SPELL_SCHOOLS_BY_REFORGE.get(reforgeId);
        if (expectedSchoolPaths == null || expectedSchoolPaths.isEmpty()) return;

        String actualSchoolPath = resolveSchoolPath(source);
        if (actualSchoolPath == null || !matchesSpellSchools(expectedSchoolPaths, actualSchoolPath)) return;
        if (!consumeSpellProc(player, reforgeId)) return;

        Object amountObj = invokeNoArgs(event, "getAmount");
        if (!(amountObj instanceof Number amount)) return;
        setSpellDamageAmount(event, amount.floatValue() * 1.5f);
    }

    private static String resolveSchoolPath(Object source) {
        Object schoolType = null;

        // Iron's SpellDamageSource exposes spell() (not getSpell()).
        Object spell = invokeNoArgs(source, "spell");
        if (spell == null) {
            spell = invokeNoArgs(source, "getSpell");
        }
        if (spell != null) {
            schoolType = invokeNoArgs(spell, "getSchoolType");
        }
        if (schoolType == null) {
            schoolType = invokeNoArgs(source, "getSchoolType");
        }
        if (schoolType == null) {
            schoolType = invokeNoArgs(source, "getSchool");
        }
        if (schoolType == null) return null;

        if (schoolType instanceof ResourceLocation rl) {
            return rl.getPath();
        }

        Object idObj = invokeNoArgs(schoolType, "getId");
        if (idObj instanceof ResourceLocation rl) {
            return rl.getPath();
        }
        if (idObj != null) {
            ResourceLocation parsed = ResourceLocation.tryParse(idObj.toString());
            if (parsed != null) return parsed.getPath();
        }
        return null;
    }

    private static boolean matchesSpellSchools(Set<String> expectedSchoolPaths, String actualSchoolPath) {
        for (String expectedSchoolPath : expectedSchoolPaths) {
            if (expectedSchoolPath.equals(actualSchoolPath)) {
                return true;
            }
            Set<String> aliases = SPELL_SCHOOL_ALIASES.get(expectedSchoolPath);
            if (aliases != null && aliases.contains(actualSchoolPath)) {
                return true;
            }
        }
        return false;
    }

    private static boolean consumeSpellProc(ServerPlayer player, ResourceLocation reforgeId) {
        CompoundTag data = player.getPersistentData();
        long now = player.level().getGameTime();
        String key = TAG_SPELL_PROC_COOLDOWN_PREFIX + reforgeId.getPath();
        long cooldownUntil = data.getLong(key);
        if (cooldownUntil > now) {
            return false;
        }
        data.putLong(key, now + SPELL_PROC_COOLDOWN_TICKS);
        syncSpellSurgeCooldownEffect(player, SPELL_PROC_COOLDOWN_TICKS);
        return true;
    }

    private static void updateSpellSurgeIndicator(ServerPlayer player) {
        if (player == null) return;

        ResourceLocation reforgeId = getMatchingApexArmorSetReforge(player);
        if (reforgeId == null || !SPELL_SCHOOLS_BY_REFORGE.containsKey(reforgeId)) {
            player.removeEffect(ForgeMobEffectRegistry.APEX_SPELL_SURGE_COOLDOWN.get());
            return;
        }

        CompoundTag data = player.getPersistentData();
        String key = TAG_SPELL_PROC_COOLDOWN_PREFIX + reforgeId.getPath();
        long now = player.level().getGameTime();
        long cooldownUntil = data.getLong(key);

        if (cooldownUntil > now) {
            int remaining = (int) Math.min(Integer.MAX_VALUE, cooldownUntil - now);
            syncSpellSurgeCooldownEffect(player, remaining);
            return;
        }

        if (cooldownUntil > 0L) {
            data.remove(key);
            player.removeEffect(ForgeMobEffectRegistry.APEX_SPELL_SURGE_COOLDOWN.get());
            player.displayClientMessage(Component.translatable("message.tiered.apex_spell_surge_ready"), true);
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.PLAYERS,
                    0.45f,
                    1.35f
            );
            return;
        }

        player.removeEffect(ForgeMobEffectRegistry.APEX_SPELL_SURGE_COOLDOWN.get());
    }

    private static void syncSpellSurgeCooldownEffect(ServerPlayer player, int remainingTicks) {
        MobEffectInstance current = player.getEffect(ForgeMobEffectRegistry.APEX_SPELL_SURGE_COOLDOWN.get());
        if (current != null && Math.abs(current.getDuration() - remainingTicks) <= 5) {
            return;
        }
        player.addEffect(new MobEffectInstance(
                ForgeMobEffectRegistry.APEX_SPELL_SURGE_COOLDOWN.get(),
                Math.max(1, remainingTicks),
                0,
                false,
                false,
                true
        ));
    }

    private static boolean isSummonEntity(LivingEntity summon) {
        EntityType<?> type = summon.getType();
        if (type.is(IRONS_SUMMONS_TAG) || type.is(TRAVELOPTICS_SUMMONS_TAG)) {
            return true;
        }

        String className = summon.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains(".entity.summon")
                || className.contains(".entity.summons.")
                || (className.contains("ironsspellbooks") && className.contains("summon"))
                || (className.contains("traveloptics") && className.contains("summon"));
    }

    private static ServerPlayer resolveSummonOwnerPlayer(LivingEntity summon) {
        if (!(summon.level() instanceof ServerLevel level)) return null;

        if (summon instanceof OwnableEntity ownable) {
            Entity ownerEntity = ownable.getOwner();
            ServerPlayer owner = asServerPlayer(ownerEntity, level);
            if (owner != null) return owner;

            UUID ownerId = ownable.getOwnerUUID();
            if (ownerId != null) {
                Player byId = level.getPlayerByUUID(ownerId);
                if (byId instanceof ServerPlayer sp) {
                    owner = sp;
                }
                if (owner != null) return owner;
            }
        }

        String[] ownerMethods = {
                "getOwner", "getSummoner", "getCaster", "owner", "summoner", "caster",
                "getOwnerUUID", "getSummonerUUID", "getCasterUUID"
        };
        for (String method : ownerMethods) {
            Object value = invokeNoArgs(summon, method);
            ServerPlayer owner = asServerPlayer(value, level);
            if (owner != null) return owner;
        }
        return null;
    }

    private static ServerPlayer asServerPlayer(Object owner, ServerLevel level) {
        if (owner instanceof ServerPlayer player) return player;
        if (owner instanceof Player player) {
            Player byId = level.getPlayerByUUID(player.getUUID());
            return (byId instanceof ServerPlayer sp) ? sp : null;
        }
        if (owner instanceof Entity entity) {
            Player byId = level.getPlayerByUUID(entity.getUUID());
            return (byId instanceof ServerPlayer sp) ? sp : null;
        }
        if (owner instanceof UUID id) {
            Player byId = level.getPlayerByUUID(id);
            return (byId instanceof ServerPlayer sp) ? sp : null;
        }
        if (owner instanceof String s) {
            try {
                Player byId = level.getPlayerByUUID(UUID.fromString(s));
                return (byId instanceof ServerPlayer sp) ? sp : null;
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private static double getOwnerSummonHealthMultiplier(ServerPlayer owner) {
        AttributeInstance summonHealth = owner.getAttribute(ForgeAttributeRegistry.SUMMON_HEALTH.get());
        if (summonHealth == null) return 1.0D;
        return Math.max(1.0D, summonHealth.getValue());
    }

    private static double getPlayerAttributeValue(ServerPlayer player, ResourceLocation attributeId) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) return 0.0D;
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return 0.0D;
        return instance.getValue();
    }

    private static void applySummonHealthBonus(LivingEntity summon, double totalHealthMultiplier) {
        CompoundTag data = summon.getPersistentData();
        if (data.getBoolean(TAG_SUMMON_HEALTH_APPLIED)) return;

        AttributeInstance maxHealth = summon.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;

        if (maxHealth.getModifier(SUMMON_HEALTH_UUID) == null) {
            float beforeHealth = summon.getHealth();
            maxHealth.addTransientModifier(new AttributeModifier(
                    SUMMON_HEALTH_UUID,
                    "tierify_apex_summon_health",
                    Math.max(0.0D, totalHealthMultiplier - 1.0D),
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
            float boostedHealth = Math.min(summon.getMaxHealth(), (float) (beforeHealth * totalHealthMultiplier));
            summon.setHealth(Math.max(1.0f, boostedHealth));
        }

        data.putBoolean(TAG_SUMMON_HEALTH_APPLIED, true);
    }

    private static Object invokeNoArgs(Object target, String methodName) {
        Class<?> cls = target.getClass();
        String cacheKey = cls.getName() + "#" + methodName;
        if (NO_ARG_METHOD_MISSING.contains(cacheKey)) {
            return null;
        }

        Method method = NO_ARG_METHOD_CACHE.get(cacheKey);
        if (method == null) {
            try {
                method = cls.getMethod(methodName);
                NO_ARG_METHOD_CACHE.put(cacheKey, method);
            } catch (NoSuchMethodException ignored) {
                NO_ARG_METHOD_MISSING.add(cacheKey);
                return null;
            }
        }

        try {
            return method.invoke(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object readField(Object target, String fieldName) {
        if (target == null || fieldName == null) return null;
        try {
            Field field = target.getClass().getField(fieldName);
            return field.get(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static void writeField(Object target, String fieldName, Object value) {
        if (target == null || fieldName == null) return;
        try {
            Field field = target.getClass().getField(fieldName);
            field.set(target, value);
        } catch (ReflectiveOperationException ignored) {
            // Keep default value if event internals change.
        }
    }

    private static ServerPlayer resolveArsCaster(Object context) {
        if (context == null) return null;

        Object living = invokeNoArgs(context, "getUnwrappedCaster");
        if (living instanceof ServerPlayer player) {
            return player;
        }
        return null;
    }

    private static void setSpellDamageAmount(Object event, float amount) {
        Class<?> cls = event.getClass();
        if (SET_AMOUNT_METHOD_MISSING.contains(cls)) {
            return;
        }

        Method method = SET_AMOUNT_METHOD_CACHE.get(cls);
        if (method == null) {
            try {
                method = cls.getMethod("setAmount", float.class);
                SET_AMOUNT_METHOD_CACHE.put(cls, method);
            } catch (NoSuchMethodException ignored) {
                SET_AMOUNT_METHOD_MISSING.add(cls);
                return;
            }
        }

        try {
            method.invoke(event, amount);
        } catch (ReflectiveOperationException ignored) {
            // Keep vanilla damage if event signature differs.
        }
    }
}
