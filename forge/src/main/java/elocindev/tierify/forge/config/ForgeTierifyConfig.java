package elocindev.tierify.forge.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public final class ForgeTierifyConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_ARMOR_SET_BONUSES;
    public static final ForgeConfigSpec.DoubleValue ARMOR_SET_BONUS_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ARMOR_SET_PERFECT_BONUS_PERCENT;

    public static final ForgeConfigSpec.DoubleValue PERFECT_ROLL_CHANCE;
    public static final ForgeConfigSpec.BooleanValue ALLOW_REFORGING_DAMAGED;

    public static final ForgeConfigSpec.BooleanValue LOOT_CONTAINER_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue LOOT_CONTAINER_MODIFIER_CHANCE;
    public static final ForgeConfigSpec.BooleanValue TREASURE_BAG_DROP_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<String> TREASURE_BAG_PROFILES_FILE;
    public static final ForgeConfigSpec.BooleanValue REFORGE_MATERIAL_LOOT_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue REFORGE_MATERIAL_LOOT_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<String> REFORGE_MATERIAL_LOOT_PROFILES_FILE;
    public static final ForgeConfigSpec.BooleanValue ENTITY_ITEM_MODIFIER;
    public static final ForgeConfigSpec.BooleanValue ENTITY_LOOT_DROP_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<String> ENTITY_LOOT_DROP_PROFILES_FILE;
    public static final ForgeConfigSpec.BooleanValue ENTITY_EQUIPMENT_DROP_MODIFIER;

    public static final ForgeConfigSpec.ConfigValue<List<? extends Number>> ENTITY_TIER_WEIGHTS;

    public static final ForgeConfigSpec.BooleanValue USE_DIMENSION_TIER_WEIGHTS;
    public static final ForgeConfigSpec.BooleanValue DIMENSION_TIER_WEIGHTS_ZERO_MEANS_NO_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<String> DIMENSION_TIER_PROFILES_FILE;

    public static final ForgeConfigSpec.BooleanValue CRAFTING_MODIFIER;
    public static final ForgeConfigSpec.BooleanValue MERCHANT_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue REFORGE_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue LEVELZ_REFORGE_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue LUCK_REFORGE_MODIFIER;
    public static final ForgeConfigSpec.IntValue SALVAGE_MAX_TIER;
    public static final ForgeConfigSpec.DoubleValue SALVAGE_CHANCE_NONE;
    public static final ForgeConfigSpec.DoubleValue SALVAGE_CHANCE_LOWER;
    public static final ForgeConfigSpec.DoubleValue SALVAGE_CHANCE_HIGHER;

    public static final ForgeConfigSpec.BooleanValue SHOW_REFORGING_TAB;
    public static final ForgeConfigSpec.IntValue X_ICON_POSITION;
    public static final ForgeConfigSpec.IntValue Y_ICON_POSITION;
    public static final ForgeConfigSpec.BooleanValue TIERED_TOOLTIP;
    public static final ForgeConfigSpec.BooleanValue SHOW_PLATES_ON_NAME;
    public static final ForgeConfigSpec.BooleanValue CENTER_NAME;
    public static final ForgeConfigSpec.BooleanValue JEI_RESERVE_EXTRA_AREAS;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER_1_QUALITIES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER_2_QUALITIES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER_3_QUALITIES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER_4_QUALITIES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER_5_QUALITIES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER_6_QUALITIES;

    private static final int[] DEFAULT_ENTITY_WEIGHTS = new int[]{2000, 200, 27, 9, 3, 1};
    private static final int[] DEFAULT_OVERWORLD_WEIGHTS = new int[]{100, 10, 1, 0, 0, 0};
    private static final int[] DEFAULT_NETHER_WEIGHTS = new int[]{10, 100, 10, 1, 0, 0};
    private static final int[] DEFAULT_END_WEIGHTS = new int[]{10, 100, 1000, 100, 10, 1};

    public record SyncedConfig(
            boolean enableArmorSetBonuses,
            float armorSetBonusMultiplier,
            float armorSetPerfectBonusPercent,
            double perfectRollChance,
            boolean allowReforgingDamaged,
            boolean lootContainerModifier,
            float lootContainerModifierChance,
            boolean treasureBagDropModifier,
            String treasureBagProfilesFile,
            boolean entityItemModifier,
            boolean entityLootDropModifier,
            String entityLootDropProfilesFile,
            boolean entityEquipmentDropModifier,
            int entityTier1Weight,
            int entityTier2Weight,
            int entityTier3Weight,
            int entityTier4Weight,
            int entityTier5Weight,
            int entityTier6Weight,
            boolean useDimensionTierWeights,
            boolean dimensionTierWeightsZeroMeansNoModifier,
            int overworldTier1Weight,
            int overworldTier2Weight,
            int overworldTier3Weight,
            int overworldTier4Weight,
            int overworldTier5Weight,
            int overworldTier6Weight,
            int netherTier1Weight,
            int netherTier2Weight,
            int netherTier3Weight,
            int netherTier4Weight,
            int netherTier5Weight,
            int netherTier6Weight,
            int endTier1Weight,
            int endTier2Weight,
            int endTier3Weight,
            int endTier4Weight,
            int endTier5Weight,
            int endTier6Weight,
            List<String> moddedDimensionTierWeightOverrides,
            boolean craftingModifier,
            boolean merchantModifier,
            float reforgeModifier,
            float levelzReforgeModifier,
            float luckReforgeModifier,
            List<String> tier1Qualities,
            List<String> tier2Qualities,
            List<String> tier3Qualities,
            List<String> tier4Qualities,
            List<String> tier5Qualities,
            List<String> tier6Qualities
    ) {}

    private static volatile SyncedConfig syncedConfig;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("echelon");

        builder.push("core");
        ENABLE_ARMOR_SET_BONUSES = builder
                .comment("Turn armor set bonuses on or off.")
                .define("enableArmorSetBonuses", true);

        ARMOR_SET_BONUS_MULTIPLIER = builder
                .comment("Full set bonus. 0.20 means 20%.")
                .defineInRange("armorSetBonusMultiplier", 0.2d, 0.0d, 10.0d);

        ARMOR_SET_PERFECT_BONUS_PERCENT = builder
                .comment("Perfect full set bonus. 1.0 means 100%.")
                .defineInRange("armorSetPerfectBonusPercent", 1.0d, 0.0d, 10.0d);

        PERFECT_ROLL_CHANCE = builder
                .comment("Chance for a perfect reforge. 0.02 means 2%.")
                .defineInRange("perfectRollChance", 0.02d, 0.0d, 1.0d);

        ALLOW_REFORGING_DAMAGED = builder
                .comment("Allow reforging damaged items.")
                .define("allowReforgingDamaged", true);
        builder.pop();

        builder.push("loot_sources");
        LOOT_CONTAINER_MODIFIER = builder
                .comment("Give reforges to chest loot.")
                .define("lootContainerModifier", true);

        LOOT_CONTAINER_MODIFIER_CHANCE = builder
                .comment("Chest loot reforge chance.")
                .defineInRange("lootContainerModifierChance", 0.1d, 0.0d, 1.0d);

        TREASURE_BAG_DROP_MODIFIER = builder
                .comment("Give reforges to treasure bag gear.")
                .define("treasureBagDropModifier", true);

        TREASURE_BAG_PROFILES_FILE = builder
                .comment("Treasure bag profile file path.")
                .define("treasureBagProfilesFile", "echelon/echelon-treasure-bag-profiles.txt");

        REFORGE_MATERIAL_LOOT_MODIFIER = builder
                .comment("Add reforge materials to chest loot.")
                .define("reforgeMaterialLootModifier", true);

        REFORGE_MATERIAL_LOOT_CHANCE = builder
                .comment("Reforge material chance in chest loot.")
                .defineInRange("reforgeMaterialLootChance", 0.1d, 0.0d, 1.0d);

        REFORGE_MATERIAL_LOOT_PROFILES_FILE = builder
                .comment("Reforge material profile file path.")
                .define("reforgeMaterialLootProfilesFile", "echelon/echelon-reforge-material-profiles.txt");

        ENTITY_ITEM_MODIFIER = builder
                .comment("Give reforges to gear mobs spawn wearing.")
                .define("entityItemModifier", false);

        ENTITY_LOOT_DROP_MODIFIER = builder
                .comment("Give reforges to items from mob loot tables.")
                .define("entityLootDropModifier", false);

        ENTITY_LOOT_DROP_PROFILES_FILE = builder
                .comment("Mob loot profile file path.")
                .define("entityLootDropProfilesFile", "echelon/echelon-entity-drop-profiles.txt");

        ENTITY_EQUIPMENT_DROP_MODIFIER = builder
                .comment("Give reforges to gear dropped from mob equipment slots.")
                .define("entityEquipmentDropModifier", true);
        builder.pop();

        builder.push("tier_weights");
        ENTITY_TIER_WEIGHTS = defineWeightList(
                builder,
                "entityTierWeights",
                "Global tier weights [Common,Uncommon,Rare,Epic,Legendary,Mythic].",
                DEFAULT_ENTITY_WEIGHTS
        );
        builder.pop();

        builder.push("dimension_weights");
        USE_DIMENSION_TIER_WEIGHTS = builder
                .comment("Use dimension-specific weights from the profile file.")
                .define("useDimensionTierWeights", true);

        DIMENSION_TIER_WEIGHTS_ZERO_MEANS_NO_MODIFIER = builder
                .comment("If all six weights are 0, skip adding a modifier.")
                .define("dimensionTierWeightsZeroMeansNoModifier", true);

        DIMENSION_TIER_PROFILES_FILE = builder
                .comment("Dimension weight profile file path.")
                .define("dimensionTierProfilesFile", "echelon/echelon-dimension-tier-profiles.txt");
        builder.pop();

        builder.push("other_sources");
        CRAFTING_MODIFIER = builder
                .comment("Give reforges to crafted items.")
                .define("craftingModifier", false);

        MERCHANT_MODIFIER = builder
                .comment("Give reforges to villager trades.")
                .define("merchantModifier", false);

        REFORGE_MODIFIER = builder
                .comment("Bonus weight change from reforging.")
                .defineInRange("reforgeModifier", 0.0d, 0.0d, 10.0d);

        LEVELZ_REFORGE_MODIFIER = builder
                .comment("Extra reforge bonus per smithing level.")
                .defineInRange("levelzReforgeModifier", 0.0d, 0.0d, 10.0d);

        LUCK_REFORGE_MODIFIER = builder
                .comment("Extra reforge bonus per luck point.")
                .defineInRange("luckReforgeModifier", 0.0d, 0.0d, 10.0d);
        builder.pop();

        builder.push("salvage");
        SALVAGE_MAX_TIER = builder
                .comment("Highest salvage tier. 6=Mythic, 10=Mythic+4.")
                .defineInRange("salvageMaxTier", 10, 1, 10);

        SALVAGE_CHANCE_NONE = builder
                .comment("Salvage chance to get nothing.")
                .defineInRange("salvageChanceNone", 0.05d, 0.0d, 1.0d);

        SALVAGE_CHANCE_LOWER = builder
                .comment("Salvage chance for one tier lower.")
                .defineInRange("salvageChanceLower", 0.10d, 0.0d, 1.0d);

        SALVAGE_CHANCE_HIGHER = builder
                .comment("Salvage chance for one tier higher.")
                .defineInRange("salvageChanceHigher", 0.10d, 0.0d, 1.0d);
        builder.pop();

        builder.push("quality_names");
        TIER_1_QUALITIES = builder
                .comment("Names used for common reforges.")
                .defineList("tier1Qualities", List.of("Common"), ForgeTierifyConfig::isString);
        TIER_2_QUALITIES = builder
                .comment("Names used for uncommon reforges.")
                .defineList("tier2Qualities", List.of("Uncomon"), ForgeTierifyConfig::isString);
        TIER_3_QUALITIES = builder
                .comment("Names used for rare reforges.")
                .defineList("tier3Qualities", List.of("Rare"), ForgeTierifyConfig::isString);
        TIER_4_QUALITIES = builder
                .comment("Names used for epic reforges.")
                .defineList("tier4Qualities", List.of("Epic"), ForgeTierifyConfig::isString);
        TIER_5_QUALITIES = builder
                .comment("Names used for legendary reforges.")
                .defineList("tier5Qualities", List.of("Legendary"), ForgeTierifyConfig::isString);
        TIER_6_QUALITIES = builder
                .comment("Names used for mythic reforges.")
                .defineList("tier6Qualities", List.of("Mythic"), ForgeTierifyConfig::isString);
        builder.pop();

        builder.pop();

        SPEC = builder.build();

        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        clientBuilder.push("echelon");
        clientBuilder.push("client");

        SHOW_REFORGING_TAB = clientBuilder
                .comment("Show the Reforge tab in the anvil.")
                .define("showReforgingTab", true);

        X_ICON_POSITION = clientBuilder
                .comment("Move Reforge tab left/right.")
                .defineInRange("xIconPosition", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        Y_ICON_POSITION = clientBuilder
                .comment("Move Reforge tab up/down.")
                .defineInRange("yIconPosition", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        TIERED_TOOLTIP = clientBuilder
                .comment("Enable Echelon tooltip style.")
                .define("tieredTooltip", true);

        SHOW_PLATES_ON_NAME = clientBuilder
                .comment("Show plates on item names.")
                .define("showPlatesOnName", false);

        CENTER_NAME = clientBuilder
                .comment("Center item names in tooltips.")
                .define("centerName", true);

        JEI_RESERVE_EXTRA_AREAS = clientBuilder
                .comment("Reserve space so JEI/EMI does not overlap Echelon panels.")
                .define("jeiReserveExtraAreas", true);

        clientBuilder.pop();
        clientBuilder.pop();
        CLIENT_SPEC = clientBuilder.build();
    }

    private ForgeTierifyConfig() {}

    public static void applySyncedConfig(SyncedConfig config) {
        syncedConfig = config;
    }

    public static SyncedConfig snapshot() {
        int[] entity = entityTierWeights();
        int[] overworld = resolveDimensionWeights(Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS);
        int[] nether = resolveDimensionWeights(Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS);
        int[] end = resolveDimensionWeights(Level.END.location(), DEFAULT_END_WEIGHTS);

        List<? extends String> tier1 = TIER_1_QUALITIES.get();
        List<? extends String> tier2 = TIER_2_QUALITIES.get();
        List<? extends String> tier3 = TIER_3_QUALITIES.get();
        List<? extends String> tier4 = TIER_4_QUALITIES.get();
        List<? extends String> tier5 = TIER_5_QUALITIES.get();
        List<? extends String> tier6 = TIER_6_QUALITIES.get();

        return new SyncedConfig(
                ENABLE_ARMOR_SET_BONUSES.get(),
                ARMOR_SET_BONUS_MULTIPLIER.get().floatValue(),
                ARMOR_SET_PERFECT_BONUS_PERCENT.get().floatValue(),
                PERFECT_ROLL_CHANCE.get(),
                ALLOW_REFORGING_DAMAGED.get(),
                LOOT_CONTAINER_MODIFIER.get(),
                LOOT_CONTAINER_MODIFIER_CHANCE.get().floatValue(),
                TREASURE_BAG_DROP_MODIFIER.get(),
                TREASURE_BAG_PROFILES_FILE.get(),
                ENTITY_ITEM_MODIFIER.get(),
                ENTITY_LOOT_DROP_MODIFIER.get(),
                ENTITY_LOOT_DROP_PROFILES_FILE.get(),
                ENTITY_EQUIPMENT_DROP_MODIFIER.get(),
                entity[0], entity[1], entity[2], entity[3], entity[4], entity[5],
                USE_DIMENSION_TIER_WEIGHTS.get(),
                DIMENSION_TIER_WEIGHTS_ZERO_MEANS_NO_MODIFIER.get(),
                overworld[0], overworld[1], overworld[2], overworld[3], overworld[4], overworld[5],
                nether[0], nether[1], nether[2], nether[3], nether[4], nether[5],
                end[0], end[1], end[2], end[3], end[4], end[5],
                DimensionTierWeightProfiles.rawEntriesForSync(),
                CRAFTING_MODIFIER.get(),
                MERCHANT_MODIFIER.get(),
                REFORGE_MODIFIER.get().floatValue(),
                LEVELZ_REFORGE_MODIFIER.get().floatValue(),
                LUCK_REFORGE_MODIFIER.get().floatValue(),
                tier1 == null ? List.of() : List.copyOf(tier1),
                tier2 == null ? List.of() : List.copyOf(tier2),
                tier3 == null ? List.of() : List.copyOf(tier3),
                tier4 == null ? List.of() : List.copyOf(tier4),
                tier5 == null ? List.of() : List.copyOf(tier5),
                tier6 == null ? List.of() : List.copyOf(tier6)
        );
    }

    public static boolean enableArmorSetBonuses() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.enableArmorSetBonuses() : ENABLE_ARMOR_SET_BONUSES.get();
    }

    public static float armorSetBonusMultiplier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.armorSetBonusMultiplier() : ARMOR_SET_BONUS_MULTIPLIER.get().floatValue();
    }

    public static float armorSetPerfectBonusPercent() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.armorSetPerfectBonusPercent() : ARMOR_SET_PERFECT_BONUS_PERCENT.get().floatValue();
    }

    public static double perfectRollChance() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.perfectRollChance() : PERFECT_ROLL_CHANCE.get();
    }

    public static boolean allowReforgingDamaged() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.allowReforgingDamaged() : ALLOW_REFORGING_DAMAGED.get();
    }

    public static boolean lootContainerModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.lootContainerModifier() : LOOT_CONTAINER_MODIFIER.get();
    }

    public static float lootContainerModifierChance() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.lootContainerModifierChance() : LOOT_CONTAINER_MODIFIER_CHANCE.get().floatValue();
    }

    public static boolean treasureBagDropModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.treasureBagDropModifier() : TREASURE_BAG_DROP_MODIFIER.get();
    }

    public static String treasureBagProfilesFile() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.treasureBagProfilesFile() : TREASURE_BAG_PROFILES_FILE.get();
    }

    public static boolean reforgeMaterialLootModifier() {
        return REFORGE_MATERIAL_LOOT_MODIFIER.get();
    }

    public static float reforgeMaterialLootChance() {
        return REFORGE_MATERIAL_LOOT_CHANCE.get().floatValue();
    }

    public static String reforgeMaterialLootProfilesFile() {
        return REFORGE_MATERIAL_LOOT_PROFILES_FILE.get();
    }

    public static boolean entityItemModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.entityItemModifier() : ENTITY_ITEM_MODIFIER.get();
    }

    public static boolean entityLootDropModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.entityLootDropModifier() : ENTITY_LOOT_DROP_MODIFIER.get();
    }

    public static String entityLootDropProfilesFile() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.entityLootDropProfilesFile() : ENTITY_LOOT_DROP_PROFILES_FILE.get();
    }

    public static boolean entityEquipmentDropModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.entityEquipmentDropModifier() : ENTITY_EQUIPMENT_DROP_MODIFIER.get();
    }

    public static int[] entityTierWeights() {
        SyncedConfig sc = syncedConfig;
        if (sc != null) {
            return new int[] {
                    Math.max(0, sc.entityTier1Weight()),
                    Math.max(0, sc.entityTier2Weight()),
                    Math.max(0, sc.entityTier3Weight()),
                    Math.max(0, sc.entityTier4Weight()),
                    Math.max(0, sc.entityTier5Weight()),
                    Math.max(0, sc.entityTier6Weight())
            };
        }
        return toWeightArray(ENTITY_TIER_WEIGHTS.get(), DEFAULT_ENTITY_WEIGHTS);
    }

    public static int entityTier1Weight() { return entityTierWeights()[0]; }
    public static int entityTier2Weight() { return entityTierWeights()[1]; }
    public static int entityTier3Weight() { return entityTierWeights()[2]; }
    public static int entityTier4Weight() { return entityTierWeights()[3]; }
    public static int entityTier5Weight() { return entityTierWeights()[4]; }
    public static int entityTier6Weight() { return entityTierWeights()[5]; }

    public static boolean useDimensionTierWeights() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.useDimensionTierWeights() : USE_DIMENSION_TIER_WEIGHTS.get();
    }

    public static boolean dimensionTierWeightsZeroMeansNoModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.dimensionTierWeightsZeroMeansNoModifier() : DIMENSION_TIER_WEIGHTS_ZERO_MEANS_NO_MODIFIER.get();
    }

    public static String dimensionTierProfilesFile() {
        return DIMENSION_TIER_PROFILES_FILE.get();
    }

    public static int overworldTier1Weight() { return resolveDimensionWeight(0, Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS); }
    public static int overworldTier2Weight() { return resolveDimensionWeight(1, Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS); }
    public static int overworldTier3Weight() { return resolveDimensionWeight(2, Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS); }
    public static int overworldTier4Weight() { return resolveDimensionWeight(3, Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS); }
    public static int overworldTier5Weight() { return resolveDimensionWeight(4, Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS); }
    public static int overworldTier6Weight() { return resolveDimensionWeight(5, Level.OVERWORLD.location(), DEFAULT_OVERWORLD_WEIGHTS); }

    public static int netherTier1Weight() { return resolveDimensionWeight(0, Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS); }
    public static int netherTier2Weight() { return resolveDimensionWeight(1, Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS); }
    public static int netherTier3Weight() { return resolveDimensionWeight(2, Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS); }
    public static int netherTier4Weight() { return resolveDimensionWeight(3, Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS); }
    public static int netherTier5Weight() { return resolveDimensionWeight(4, Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS); }
    public static int netherTier6Weight() { return resolveDimensionWeight(5, Level.NETHER.location(), DEFAULT_NETHER_WEIGHTS); }

    public static int endTier1Weight() { return resolveDimensionWeight(0, Level.END.location(), DEFAULT_END_WEIGHTS); }
    public static int endTier2Weight() { return resolveDimensionWeight(1, Level.END.location(), DEFAULT_END_WEIGHTS); }
    public static int endTier3Weight() { return resolveDimensionWeight(2, Level.END.location(), DEFAULT_END_WEIGHTS); }
    public static int endTier4Weight() { return resolveDimensionWeight(3, Level.END.location(), DEFAULT_END_WEIGHTS); }
    public static int endTier5Weight() { return resolveDimensionWeight(4, Level.END.location(), DEFAULT_END_WEIGHTS); }
    public static int endTier6Weight() { return resolveDimensionWeight(5, Level.END.location(), DEFAULT_END_WEIGHTS); }

    public static List<String> moddedDimensionTierWeightOverrides() {
        SyncedConfig sc = syncedConfig;
        if (sc != null) return sc.moddedDimensionTierWeightOverrides();
        return DimensionTierWeightProfiles.rawEntriesForSync();
    }

    public static boolean craftingModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.craftingModifier() : CRAFTING_MODIFIER.get();
    }

    public static boolean merchantModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.merchantModifier() : MERCHANT_MODIFIER.get();
    }

    public static float reforgeModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.reforgeModifier() : REFORGE_MODIFIER.get().floatValue();
    }

    public static float levelzReforgeModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.levelzReforgeModifier() : LEVELZ_REFORGE_MODIFIER.get().floatValue();
    }

    public static float luckReforgeModifier() {
        SyncedConfig sc = syncedConfig;
        return sc != null ? sc.luckReforgeModifier() : LUCK_REFORGE_MODIFIER.get().floatValue();
    }

    public static int salvageMaxTier() {
        return SALVAGE_MAX_TIER.get();
    }

    public static double salvageChanceNone() {
        return SALVAGE_CHANCE_NONE.get();
    }

    public static double salvageChanceLower() {
        return SALVAGE_CHANCE_LOWER.get();
    }

    public static double salvageChanceHigher() {
        return SALVAGE_CHANCE_HIGHER.get();
    }

    public static boolean showReforgingTab() {
        return SHOW_REFORGING_TAB.get();
    }

    public static int xIconPosition() {
        return X_ICON_POSITION.get();
    }

    public static int yIconPosition() {
        return Y_ICON_POSITION.get();
    }

    public static boolean tieredTooltip() {
        return TIERED_TOOLTIP.get();
    }

    public static boolean showPlatesOnName() {
        return SHOW_PLATES_ON_NAME.get();
    }

    public static boolean centerName() {
        return CENTER_NAME.get();
    }

    public static boolean jeiReserveExtraAreas() {
        return JEI_RESERVE_EXTRA_AREAS.get();
    }

    public static List<String> getTierQualities(int tier) {
        SyncedConfig sc = syncedConfig;
        if (sc != null) {
            return switch (tier) {
                case 1 -> sc.tier1Qualities();
                case 2 -> sc.tier2Qualities();
                case 3 -> sc.tier3Qualities();
                case 4 -> sc.tier4Qualities();
                case 5 -> sc.tier5Qualities();
                case 6 -> sc.tier6Qualities();
                default -> List.of();
            };
        }
        List<? extends String> list = switch (tier) {
            case 1 -> TIER_1_QUALITIES.get();
            case 2 -> TIER_2_QUALITIES.get();
            case 3 -> TIER_3_QUALITIES.get();
            case 4 -> TIER_4_QUALITIES.get();
            case 5 -> TIER_5_QUALITIES.get();
            case 6 -> TIER_6_QUALITIES.get();
            default -> List.of();
        };
        return list == null ? List.of() : List.copyOf(list);
    }

    private static int resolveDimensionWeight(int index, ResourceLocation dimension, int[] fallback) {
        SyncedConfig sc = syncedConfig;
        if (sc != null) {
            if (dimension.equals(Level.OVERWORLD.location())) {
                return switch (index) {
                    case 0 -> sc.overworldTier1Weight();
                    case 1 -> sc.overworldTier2Weight();
                    case 2 -> sc.overworldTier3Weight();
                    case 3 -> sc.overworldTier4Weight();
                    case 4 -> sc.overworldTier5Weight();
                    default -> sc.overworldTier6Weight();
                };
            }
            if (dimension.equals(Level.NETHER.location())) {
                return switch (index) {
                    case 0 -> sc.netherTier1Weight();
                    case 1 -> sc.netherTier2Weight();
                    case 2 -> sc.netherTier3Weight();
                    case 3 -> sc.netherTier4Weight();
                    case 4 -> sc.netherTier5Weight();
                    default -> sc.netherTier6Weight();
                };
            }
            return switch (index) {
                case 0 -> sc.endTier1Weight();
                case 1 -> sc.endTier2Weight();
                case 2 -> sc.endTier3Weight();
                case 3 -> sc.endTier4Weight();
                case 4 -> sc.endTier5Weight();
                default -> sc.endTier6Weight();
            };
        }

        int[] weights = resolveDimensionWeights(dimension, fallback);
        return weights[index];
    }

    private static int[] resolveDimensionWeights(ResourceLocation dimension, int[] fallback) {
        DimensionTierWeightProfiles.Entry entry = DimensionTierWeightProfiles.get(dimension);
        if (entry == null || entry.weights() == null || entry.weights().length != 6) {
            return fallback.clone();
        }

        int[] out = new int[6];
        for (int i = 0; i < 6; i++) out[i] = Math.max(0, entry.weights()[i]);
        return out;
    }

    private static ForgeConfigSpec.ConfigValue<List<? extends Number>> defineWeightList(
            ForgeConfigSpec.Builder builder,
            String key,
            String comment,
            int[] defaults
    ) {
        List<Number> defaultList = new ArrayList<>(defaults.length);
        for (int value : defaults) defaultList.add(value);

        return builder
                .comment(comment)
                .defineList(key, defaultList, ForgeTierifyConfig::isWeightListValue);
    }

    private static int[] toWeightArray(List<? extends Number> value, int[] fallback) {
        int[] out = fallback.clone();
        if (value == null || value.size() != 6) return out;

        for (int i = 0; i < 6; i++) {
            Number n = value.get(i);
            if (n == null) return fallback.clone();
            out[i] = Math.max(0, n.intValue());
        }
        return out;
    }

    private static boolean isWeightListValue(Object value) {
        return value instanceof Number;
    }

    private static boolean isString(Object value) {
        return value instanceof String;
    }
}
