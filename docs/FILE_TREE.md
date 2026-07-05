# File Tree

Generated from repository root. Build outputs, Gradle caches, IDE folders, runtime folders, temporary source extracts, logs, and other cache/scratch files are omitted.

## Notes

- Active source/resources: `forge/src/main/java`, `forge/src/main/resources`, and `forge/src/test/java`.
- Active project docs: top-level `README.md`, `CHANGELOG.md`, and `docs/*.md`.
- Active helper tooling: `tools/`.
- Reference/debug folders: `reference/`, `debugging_info/`, `net/`, and `docs/superpowers/` are not active source. Use them only for research, comparison, or diagnostics.
- Do not treat `reference/fabric_snapshot`, decompiled jars, local logs, or scratch/debug captures as implementation targets.
- Obsolete Sovereign armor / Geckolib planning docs and empty armor model/renderer/item placeholder packages are intentionally absent. Active armor gameplay data remains under `forge/src/main/resources/data/tiered/item_attributes/all_armor`.

```text
.gitignore
CHANGELOG.md
debugging_info/echelon-client.toml
debugging_info/echelon-common.toml
debugging_info/font_overrides.txt
debugging_info/mods_list.txt
debugging_info/obscure_tooltips-client.toml
debugging_info/obscure-api-client.toml
debugging_info/options.txt
docs/BLUEPRINT.md
docs/CODEX_GUIDE.md
docs/COMPAT_NOTES.md
docs/DECISIONS.md
docs/FILE_TREE.md
docs/MIXIN_NOTES.md
docs/superpowers/plans/2026-04-13-standalone-apothic-implementation.md
docs/superpowers/specs/2026-04-13-standalone-apothic-design.md
docs/TASKS.md
docs/TEST_PLAN.md
docs/UPSTREAM_REFERENCE.md
forge/build.gradle
forge/src/main/gradle.properties
forge/src/main/java/draylar/tiered/api/SetBonusLogic.java
forge/src/main/java/elocindev/tierify/forge/apex/ApexActiveEffects.java
forge/src/main/java/elocindev/tierify/forge/apex/ApexEffect.java
forge/src/main/java/elocindev/tierify/forge/apex/ApexEffectRegistry.java
forge/src/main/java/elocindev/tierify/forge/apex/ApexEffectsBootstrap.java
forge/src/main/java/elocindev/tierify/forge/client/ApexEffectGradientAnimatorForge.java
forge/src/main/java/elocindev/tierify/forge/client/ApexKeybindHandler.java
forge/src/main/java/elocindev/tierify/forge/client/ApexKeybinds.java
forge/src/main/java/elocindev/tierify/forge/client/ForgeClientSetup.java
forge/src/main/java/elocindev/tierify/forge/client/ForgeScreenTabs.java
forge/src/main/java/elocindev/tierify/forge/client/ForgeTierNameTooltipSubscriber.java
forge/src/main/java/elocindev/tierify/forge/client/ForgeTooltipBorderReloadListener.java
forge/src/main/java/elocindev/tierify/forge/client/PerfectLabelAnimatorForge.java
forge/src/main/java/elocindev/tierify/forge/client/TierGradientAnimatorForge.java
forge/src/main/java/elocindev/tierify/forge/client/TierifyFontSwizzleContext.java
forge/src/main/java/elocindev/tierify/forge/client/TierifyTooltipBorderRendererForge.java
forge/src/main/java/elocindev/tierify/forge/command/ForgeCommandInit.java
forge/src/main/java/elocindev/tierify/forge/compat/ApexEffectTooltipComponent.java
forge/src/main/java/elocindev/tierify/forge/compat/ArmageddonTreasureBagHooks.java
forge/src/main/java/elocindev/tierify/forge/compat/TierifyEmiPlugin.java
forge/src/main/java/elocindev/tierify/forge/compat/TierifyJeiPlugin.java
forge/src/main/java/elocindev/tierify/forge/compat/TooltipOverhaulCompatForge.java
forge/src/main/java/elocindev/tierify/forge/config/DimensionTierWeightProfiles.java
forge/src/main/java/elocindev/tierify/forge/config/EntityLootDropProfiles.java
forge/src/main/java/elocindev/tierify/forge/config/ForgeTierifyConfig.java
forge/src/main/java/elocindev/tierify/forge/config/ReforgeMaterialLootProfiles.java
forge/src/main/java/elocindev/tierify/forge/config/TreasureBagProfiles.java
forge/src/main/java/elocindev/tierify/forge/effect/ApexArmorBoostEffect.java
forge/src/main/java/elocindev/tierify/forge/effect/ApexRangedMomentumEffect.java
forge/src/main/java/elocindev/tierify/forge/effect/ApexSlowTimeEffect.java
forge/src/main/java/elocindev/tierify/forge/event/ForgeAttributeSubscriber.java
forge/src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java
forge/src/main/java/elocindev/tierify/forge/ForgeServerTickSubscriber.java
forge/src/main/java/elocindev/tierify/forge/ForgeTieredAttributeSubscriber.java
forge/src/main/java/elocindev/tierify/forge/item/DescribedItem.java
forge/src/main/java/elocindev/tierify/forge/item/ReforgeAddition.java
forge/src/main/java/elocindev/tierify/forge/loot/TierifyLootModifier.java
forge/src/main/java/elocindev/tierify/forge/mixin/ArmorItemMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/ArmorStandEntityMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/AbstractContainerScreenAccessor.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/ClientTextTooltipAccessor.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/FontTextureSwizzleMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/ForgeHooksClientMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/GuiGraphicsTooltipBorderMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/ItemBarMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/client/ItemStackClientMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/ArmageddonTreasureBagEntityProceduresMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/ArmageddonTreasureBagProceduresMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/CuriosBrutalityUuidSaltMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/ObscureApiAttackSpeedIconMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/ObscureApiAttributeIconMathMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/ObscureApiIconsLineMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/ObscureApiIconsMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/SkillInfoScreenMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/TooltipOverhaulFrameMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/TooltipOverhaulTitleAlignmentMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/compat/TooltipOverhaulWrapperMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/EnchantmentHelperMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/EntityEquipmentDropMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/EntityLootDropMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/ItemCombinerMenuAccessor.java
forge/src/main/java/elocindev/tierify/forge/mixin/ItemFrameEntityMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/ItemStackMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/LivingEntityMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/MerchantMenuMixin.java
forge/src/main/java/elocindev/tierify/forge/mixin/TieredForgeMixinPlugin.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/ApexActiveEffectC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/OpenAnvilFromReforgeC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/OpenReforgeFromAnvilC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/OpenSalvageFromAnvilC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/OpenSalvageUpgradeC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/TryReforgeC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/TrySalvageC2S.java
forge/src/main/java/elocindev/tierify/forge/network/c2s/TrySalvageUpgradeC2S.java
forge/src/main/java/elocindev/tierify/forge/network/ForgeNetwork.java
forge/src/main/java/elocindev/tierify/forge/network/s2c/AttributeSyncS2C.java
forge/src/main/java/elocindev/tierify/forge/network/s2c/ConfigSyncS2C.java
forge/src/main/java/elocindev/tierify/forge/network/s2c/ReforgeItemsSyncS2C.java
forge/src/main/java/elocindev/tierify/forge/reforge/ForgeReforgeData.java
forge/src/main/java/elocindev/tierify/forge/reforge/ForgeReforgeReloadListener.java
forge/src/main/java/elocindev/tierify/forge/registry/ForgeAttributeRegistry.java
forge/src/main/java/elocindev/tierify/forge/registry/ForgeItemRegistry.java
forge/src/main/java/elocindev/tierify/forge/registry/ForgeMenuTypes.java
forge/src/main/java/elocindev/tierify/forge/registry/ForgeMobEffectRegistry.java
forge/src/main/java/elocindev/tierify/forge/registry/ForgeSoundRegistry.java
forge/src/main/java/elocindev/tierify/forge/screen/AnvilMenuValidity.java
forge/src/main/java/elocindev/tierify/forge/screen/client/ReforgeScreen.java
forge/src/main/java/elocindev/tierify/forge/screen/client/SalvageScreen.java
forge/src/main/java/elocindev/tierify/forge/screen/client/SalvageUpgradeScreen.java
forge/src/main/java/elocindev/tierify/forge/screen/ReforgeMenu.java
forge/src/main/java/elocindev/tierify/forge/screen/SalvageMenu.java
forge/src/main/java/elocindev/tierify/forge/screen/SalvageUpgradeMenu.java
forge/src/main/java/elocindev/tierify/forge/util/ForgeAttributeHelper.java
forge/src/main/java/elocindev/tierify/platform/ForgePlatformHelper.java
forge/src/main/java/elocindev/tierify/platform/Platform.java
forge/src/main/java/elocindev/tierify/platform/PlatformHelper.java
forge/src/main/java/elocindev/tierify/server/SetBonusTickHandler.java
forge/src/main/java/elocindev/tierify/TierifyCommon.java
forge/src/main/java/elocindev/tierify/TierifyConstants.java
forge/src/main/java/elocindev/tierify/TierifyForge.java
forge/src/main/java/elocindev/tierify/util/SetBonusUtils.java
forge/src/main/java/elocindev/tierify/util/StarApexUtils.java
forge/src/main/java/elocindev/tierify/util/TagFallbackMatcher.java
forge/src/main/resources/assets/emi/tag/exclusions/tiered.json
forge/src/main/resources/assets/legendarytooltips/frame_definitions.json
forge/src/main/resources/assets/legendarytooltips/textures/gui/tiered_borders.png
forge/src/main/resources/assets/libz/textures/gui/icons.png
forge/src/main/resources/assets/tiered/font/apex_effect.json
forge/src/main/resources/assets/tiered/font/apex_effect.ttf
forge/src/main/resources/assets/tiered/font/apex_effect_prefix.json
forge/src/main/resources/assets/tiered/font/apex_effect_small.json
forge/src/main/resources/assets/tiered/font/LICENSE.txt
forge/src/main/resources/assets/tiered/font/OFL.txt
forge/src/main/resources/assets/tiered/font/plates.json
forge/src/main/resources/assets/tiered/font/test_effect.ttf
forge/src/main/resources/assets/tiered/lang/en_us.json
forge/src/main/resources/assets/tiered/models/item/apex_crux.json
forge/src/main/resources/assets/tiered/models/item/charoite.json
forge/src/main/resources/assets/tiered/models/item/cleansing_stone.json
forge/src/main/resources/assets/tiered/models/item/crown_topaz.json
forge/src/main/resources/assets/tiered/models/item/galena_chunk.json
forge/src/main/resources/assets/tiered/models/item/limestone_chunk.json
forge/src/main/resources/assets/tiered/models/item/painite.json
forge/src/main/resources/assets/tiered/models/item/pyrite_chunk.json
forge/src/main/resources/assets/tiered/models/item/stardust.json
forge/src/main/resources/assets/tiered/models/item/stellar_core.json
forge/src/main/resources/assets/tiered/sounds.json
forge/src/main/resources/assets/tiered/sounds/reforge_sound_common.ogg
forge/src/main/resources/assets/tiered/sounds/reforge_sound_epic.ogg
forge/src/main/resources/assets/tiered/sounds/reforge_sound_legendary.ogg
forge/src/main/resources/assets/tiered/sounds/reforge_sound_mythic.ogg
forge/src/main/resources/assets/tiered/sounds/reforge_sound_rare.ogg
forge/src/main/resources/assets/tiered/sounds/reforge_sound_uncommon.ogg
forge/src/main/resources/assets/tiered/textures/gui/anvil_tab_icon.png
forge/src/main/resources/assets/tiered/textures/gui/apex_crown_plate.png
forge/src/main/resources/assets/tiered/textures/gui/backbuttonsalvage.png
forge/src/main/resources/assets/tiered/textures/gui/crown/crown_center.png
forge/src/main/resources/assets/tiered/textures/gui/crown/crown_left.png
forge/src/main/resources/assets/tiered/textures/gui/crown/crown_right.png
forge/src/main/resources/assets/tiered/textures/gui/halo1.png
forge/src/main/resources/assets/tiered/textures/gui/perfect_star.png
forge/src/main/resources/assets/tiered/textures/gui/reforge_tab_icon.png
forge/src/main/resources/assets/tiered/textures/gui/reforging_screen.png
forge/src/main/resources/assets/tiered/textures/gui/ribbon/ribbon_left.png
forge/src/main/resources/assets/tiered/textures/gui/ribbon/ribbon_mid.png
forge/src/main/resources/assets/tiered/textures/gui/ribbon/ribbon_right.png
forge/src/main/resources/assets/tiered/textures/gui/salvage_upgrade_icon.png
forge/src/main/resources/assets/tiered/textures/gui/salvage_upgrade_icon2.png
forge/src/main/resources/assets/tiered/textures/gui/salvaging_screen.png
forge/src/main/resources/assets/tiered/textures/gui/setbonusicon_active.png
forge/src/main/resources/assets/tiered/textures/gui/setbonusicon2.png
forge/src/main/resources/assets/tiered/textures/gui/star.png
forge/src/main/resources/assets/tiered/textures/gui/tiered_borders.png
forge/src/main/resources/assets/tiered/textures/gui/upgradeslabel.png
forge/src/main/resources/assets/tiered/textures/gui/upgrading_screen.png
forge/src/main/resources/assets/tiered/textures/icon.png
forge/src/main/resources/assets/tiered/textures/item/apex_crux.png
forge/src/main/resources/assets/tiered/textures/item/charoite.png
forge/src/main/resources/assets/tiered/textures/item/cleansing_stone.png
forge/src/main/resources/assets/tiered/textures/item/crown_topaz.png
forge/src/main/resources/assets/tiered/textures/item/galena_chunk.png
forge/src/main/resources/assets/tiered/textures/item/LICENSE.md
forge/src/main/resources/assets/tiered/textures/item/limestone_chunk.png
forge/src/main/resources/assets/tiered/textures/item/painite.png
forge/src/main/resources/assets/tiered/textures/item/pyrite_chunk.png
forge/src/main/resources/assets/tiered/textures/item/stardust.png
forge/src/main/resources/assets/tiered/textures/item/stellar_core.png
forge/src/main/resources/assets/tiered/textures/mob_effect/apex_armor_boost.png
forge/src/main/resources/assets/tiered/textures/mob_effect/apex_ranged_momentum.png
forge/src/main/resources/assets/tiered/textures/mob_effect/apex_roll_counter.png
forge/src/main/resources/assets/tiered/textures/plates/common.png
forge/src/main/resources/assets/tiered/textures/plates/epic.png
forge/src/main/resources/assets/tiered/textures/plates/legendary.png
forge/src/main/resources/assets/tiered/textures/plates/mythic.png
forge/src/main/resources/assets/tiered/textures/plates/rare.png
forge/src/main/resources/assets/tiered/textures/plates/uncommon.png
forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json
forge/src/main/resources/data/forge/loot_modifiers/global_loot_modifiers.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_1.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_2.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_3.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_4.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_5.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_6.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_7.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_1.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_2.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_3.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_4.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_5.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_6.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_7.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_1.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_2.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_3.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_4.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_5.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_6.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_7.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_1.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_2.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_3.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_4.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_5.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_6.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_7.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_1.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_2.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_3.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_4.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_5.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_6.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_7.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_1.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_2.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_3.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_4.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_5.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_6.json
forge/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_7.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_1.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_2.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_3.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_4.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_1.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_2.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_3.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_4.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_5.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_1.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_2.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_3.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_4.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_5.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_1.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_2.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_3.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_4.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_5.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_1.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_2.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_3.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_4.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_5.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_1.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_2.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_3.json
forge/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_4.json
forge/src/main/resources/data/tiered/item_attributes/elytra/common_elytra_1.json
forge/src/main/resources/data/tiered/item_attributes/elytra/common_elytra_2.json
forge/src/main/resources/data/tiered/item_attributes/elytra/epic_elytra_1.json
forge/src/main/resources/data/tiered/item_attributes/elytra/epic_elytra_2.json
forge/src/main/resources/data/tiered/item_attributes/elytra/legendary_elytra_1.json
forge/src/main/resources/data/tiered/item_attributes/elytra/legendary_elytra_2.json
forge/src/main/resources/data/tiered/item_attributes/elytra/mythic_elytra_1.json
forge/src/main/resources/data/tiered/item_attributes/elytra/mythic_elytra_2.json
forge/src/main/resources/data/tiered/item_attributes/elytra/rare_elytra_1.json
forge/src/main/resources/data/tiered/item_attributes/elytra/rare_elytra_2.json
forge/src/main/resources/data/tiered/item_attributes/elytra/uncommon_elytra_1.json
forge/src/main/resources/data/tiered/item_attributes/elytra/uncommon_elytra_2.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/common_fishing_1.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/common_fishing_2.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/epic_fishing_1.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/epic_fishing_2.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/legendary_fishing_1.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/legendary_fishing_2.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/mythic_fishing_1.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/mythic_fishing_2.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/rare_fishing_1.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/rare_fishing_2.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/uncommon_fishing_1.json
forge/src/main/resources/data/tiered/item_attributes/fishing_rod/uncommon_fishing_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_1.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_3.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_4.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_5.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_6.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_7.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_8.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_1.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_3.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_4.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_5.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_6.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_7.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_8.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_1.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_3.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_4.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_5.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_6.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_7.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_8.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_1.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_3.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_4.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_5.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_6.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_7.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_8.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_1.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_3.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_4.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_5.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_6.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_7.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_8.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_1.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_2.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_3.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_4.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_5.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_6.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_7.json
forge/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_8.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_1.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_2.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_3.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_4.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_5.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_6.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_7.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_8.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_1.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_2.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_3.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_4.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_5.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_6.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_7.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_8.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_1.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_2.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_3.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_4.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_5.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_6.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_7.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_8.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_1.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_2.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_3.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_4.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_5.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_6.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_7.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_8.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_1.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_2.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_3.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_4.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_5.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_6.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_7.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_8.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_1.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_2.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_3.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_4.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_5.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_6.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_7.json
forge/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_8.json
forge/src/main/resources/data/tiered/item_attributes/shields/common_shield_1.json
forge/src/main/resources/data/tiered/item_attributes/shields/common_shield_2.json
forge/src/main/resources/data/tiered/item_attributes/shields/common_shield_3.json
forge/src/main/resources/data/tiered/item_attributes/shields/common_shield_4.json
forge/src/main/resources/data/tiered/item_attributes/shields/common_shield_5.json
forge/src/main/resources/data/tiered/item_attributes/shields/epic_shield_1.json
forge/src/main/resources/data/tiered/item_attributes/shields/epic_shield_2.json
forge/src/main/resources/data/tiered/item_attributes/shields/epic_shield_3.json
forge/src/main/resources/data/tiered/item_attributes/shields/epic_shield_4.json
forge/src/main/resources/data/tiered/item_attributes/shields/epic_shield_5.json
forge/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_1.json
forge/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_2.json
forge/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_3.json
forge/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_4.json
forge/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_5.json
forge/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_1.json
forge/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_2.json
forge/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_3.json
forge/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_4.json
forge/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_5.json
forge/src/main/resources/data/tiered/item_attributes/shields/rare_shield_1.json
forge/src/main/resources/data/tiered/item_attributes/shields/rare_shield_2.json
forge/src/main/resources/data/tiered/item_attributes/shields/rare_shield_3.json
forge/src/main/resources/data/tiered/item_attributes/shields/rare_shield_4.json
forge/src/main/resources/data/tiered/item_attributes/shields/rare_shield_5.json
forge/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_1.json
forge/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_2.json
forge/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_3.json
forge/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_4.json
forge/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_5.json
forge/src/main/resources/data/tiered/loot_modifiers/tierify_loot.json
forge/src/main/resources/data/tiered/recipes/apex_crux.json
forge/src/main/resources/data/tiered/recipes/charoite.json
forge/src/main/resources/data/tiered/recipes/cleansing_stone.json
forge/src/main/resources/data/tiered/recipes/crown_topaz.json
forge/src/main/resources/data/tiered/recipes/galena_vanilla.json
forge/src/main/resources/data/tiered/recipes/limestone_vanilla.json
forge/src/main/resources/data/tiered/recipes/painite.json
forge/src/main/resources/data/tiered/recipes/pyrite_vanilla.json
forge/src/main/resources/data/tiered/recipes/stardust.json
forge/src/main/resources/data/tiered/recipes/stellar_core.json
forge/src/main/resources/data/tiered/reforge_items/bow.json
forge/src/main/resources/data/tiered/reforge_items/compat_mutantmonsters.json
forge/src/main/resources/data/tiered/reforge_items/crossbow.json
forge/src/main/resources/data/tiered/reforge_items/elytra.json
forge/src/main/resources/data/tiered/reforge_items/fishing_rods.json
forge/src/main/resources/data/tiered/reforge_items/irons_spellbooks_mithril.json
forge/src/main/resources/data/tiered/reforge_items/lethalitynihilite.json
forge/src/main/resources/data/tiered/reforge_items/mekatana.json
forge/src/main/resources/data/tiered/reforge_items/netherite_equipment.json
forge/src/main/resources/data/tiered/reforge_items/terramityblackmatter.json
forge/src/main/resources/data/tiered/reforge_items/terramitycthoniccrystal.json
forge/src/main/resources/data/tiered/reforge_items/terramitydaemoniumchunk.json
forge/src/main/resources/data/tiered/reforge_items/terramityfairydust.json
forge/src/main/resources/data/tiered/reforge_items/terramityhellspecalloy.json
forge/src/main/resources/data/tiered/reforge_items/terramityiridium.json
forge/src/main/resources/data/tiered/reforge_items/terramitypixiealloy.json
forge/src/main/resources/data/tiered/reforge_items/terramityreverium.json
forge/src/main/resources/data/tiered/reforge_items/terramityruby.json
forge/src/main/resources/data/tiered/reforge_items/terramitytopaz.json
forge/src/main/resources/data/tiered/reforge_items/terramitywardensoul.json
forge/src/main/resources/data/tiered/reforge_items/trident.json
forge/src/main/resources/data/tiered/tags/items/main_offhand_item.json
forge/src/main/resources/data/tiered/tags/items/reforge_base_item.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_1.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_2.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_3.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_4.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_5.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_6.json
forge/src/main/resources/data/tiered/tags/items/reforge_tier_cleanse.json
forge/src/main/resources/echelon-defaults/echelon-client.toml
forge/src/main/resources/echelon-defaults/echelon-common.toml
forge/src/main/resources/echelon-defaults/echelon-dimension-tier-profiles.txt
forge/src/main/resources/echelon-defaults/echelon-entity-drop-profiles.txt
forge/src/main/resources/echelon-defaults/echelon-reforge-material-profiles.txt
forge/src/main/resources/echelon-defaults/echelon-treasure-bag-profiles.txt
forge/src/main/resources/META-INF/mods.toml
forge/src/main/resources/pack.mcmeta
forge/src/main/resources/tiered.forge.mixins.json
forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java
gradle.properties
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
gradlew
gradlew.bat
LICENSE_CODE
LICENSE_RESOURCES
net/minecraftforge/client/ForgeHooksClient.java
README.md
reference/armor_stands-png-png.png
reference/context files/ApothicAttributes_CONTEXT.txt
reference/context files/Armageddon_CONTEXT.txt
reference/context files/BRUTALITY_CONTEXT.txt
reference/context files/lethalitydecompiled.zip
reference/context files/lethalitydecompiled/decompiled/assets/lethality/animations/acidic_slash.animation.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/animations/gael_skull_idle.animation.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/attack_animations/greatsword_pose.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/attack_animations/pickle_pong.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/attack_animations/violence_throw.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/attack_animations/violence_throw_horizontal.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/attack_animations/violence_throw_horizontal_2.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/blockstates/nihilite_ore.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/grapesoda.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/grapesoda.ttf
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/homicide.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/homicide.ttf
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/mkart.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/mkart.ttf
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/vermin.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/font/vermin.ttf
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/acidic_slash.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/bbb_projection.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/blood_scythe.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/devils_pitchfork.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/devils_scythe.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/forbidden_scythe.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/gael_skull.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/hf_meowrasama_slash.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/hook_model.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/pickle_ball.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/real_slash.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/starlight_stab.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/ultra_sos_bite.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/geo/vehemence_bolt.geo.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/lang/en_us.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/block/nihilite_ore.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/112_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/128_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/32_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/48_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/64_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/80_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/96_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/handheld/normal_handheld.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/alfajor_de_maicena.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/annihilation_alloy.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/backup_sos.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/battle_maid_boots.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/battle_maid_chestplate.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/battle_maid_helmet.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/battle_maid_leggings.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/bladecrest_oathsword.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/blighted_cleaver.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/blood_coin.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/broken_biome_blade.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/broken_biome_blade_arid_grandeur.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/broken_biome_blade_biting_embrace.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/broken_biome_blade_decays_retort.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/broken_biome_blade_grovetenders_touch.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/caustic_edge.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/charged_redstone_piece.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/conductite_bafpb.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/cosmilite_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/defiled_greatsword.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/devils_devastation.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/diamond_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/dimlite_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/escarapela.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/exalted_oathblade.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/exodium_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/fairy_pickle.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/flowey.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/forbidden_oathblade.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/foreign_hook.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/gaels_greatsword.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/gamblers_blade.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/golden_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/grievance.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hellspec_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hf_battle_maid_boots.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hf_battle_maid_chestplate.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hf_battle_maid_helmet.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hf_battle_maid_leggings.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hf_meowrasama.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/hf_meowrasama_active.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/horseless_headless_horsemanns_headtaker.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/improbability_steel.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/iridium_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/iron_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/mate.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/mate_dulce.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/miasma.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/midas_touch.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/netherite_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/nightfall.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/nightmare_sword.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/nihilite.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/nihilite_ore.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/nyxium_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/onyx_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/pale_branch.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/pickle_paddle.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/pixie_alloy.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/raw_nihilite.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/real_knife.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/reverium_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/ruby_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/sacrifice.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/sapphire_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/skysplitter_sword.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/soft_cloth.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/sorrowful_ghast.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/starlight.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/stone_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/stop_sign.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/sword_of_acclaim.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/sword_of_majesty.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/sword_of_splendor.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/tainted_blade.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/topaz_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/ultra_backup_sos.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/vehemence.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/violence.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/virentium_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/void_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/warden_spine.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/models/item/wooden_bafs.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/acidic_bubble.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/acidic_bubble_fork.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/acidic_impact.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/crumbling.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/custom_particle.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/cute_sparkles.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/forbidden_glint.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/gael_smoke.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/hex_flame.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/real_crit.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/particles/tranquility.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds.json
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/bloodshed_start.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/mate_drink.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/megalo_strike_back.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/nightfall_hit.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/nightfall_shield_recast.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/nightfall_shield_use.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/nightfall_slam.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/sounds/real_knife_slash.ogg
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/block/nihilite_ore.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/acidic_slash.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/bbb_projection.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/blood_scythe.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/devils_pitchfork.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/devils_scythe.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/forbidden_scythe.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/gael_skull.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_0_0.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_0_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_0_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_0_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_1_0.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_1_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_1_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hf_meowrasama_slash_1_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hook.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/hook_chain.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/pickle_ball.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/real_slash.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_0.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_4.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_5.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/starlight_stab_6.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/ultra_sos_bite_0.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/ultra_sos_bite_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/ultra_sos_bite_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/entity/vehemence_bolt.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/biome_menu.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/bleed.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/cursor_icon.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/fire.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/genocide_charge_bar.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/ice.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/gui/poison.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/alfajor_de_maicena.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/annihilation_alloy.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/annihilation_alloy.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/backup_sos.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/battle_maid_boots.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/battle_maid_chestplate.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/battle_maid_helmet.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/battle_maid_leggings.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/bladecrest_oathsword.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/bladecrest_oathsword_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/blighted_cleaver.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/blighted_cleaver_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/blood_coin.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/broken_biome_blade.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/broken_biome_blade_arid_grandeur.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/broken_biome_blade_biting_embrace.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/broken_biome_blade_decays_retort.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/broken_biome_blade_grovetenders_touch.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/broken_biome_blade_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/caustic_edge.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/caustic_edge_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/charged_redstone_piece.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/charged_redstone_piece.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/conductite_bafpb.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/conductite_bafpb_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/cosmilite_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/cosmilite_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/defiled_greatsword.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/defiled_greatsword_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/devils_devastation.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/devils_devastation_emissive.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/devils_devastation_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/diamond_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/diamond_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/dimlite_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/dimlite_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/escarapela.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/exalted_oathblade.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/exalted_oathblade_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/exodium_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/exodium_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/fairy_pickle.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/flowey.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/forbidden_oathblade.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/forbidden_oathblade_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/foreign_hook.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/foreign_hook_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/gaels_greatsword.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/gaels_greatsword_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/gamblers_blade.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/gamblers_blade_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/golden_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/golden_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/grievance.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/grievance_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hellspec_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hellspec_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_battle_maid_boots.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_battle_maid_chestplate.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_battle_maid_helmet.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_battle_maid_leggings.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_meowrasama.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_meowrasama.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_meowrasama_active.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_meowrasama_active.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_meowrasama_active_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/hf_meowrasama_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/horselessheadlesshorsemannsheadtaker.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/horselessheadlesshorsemannsheadtaker_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/improbability_steel.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/iridium_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/iridium_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/iron_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/iron_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/mate_dulce_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/mate_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/miasma.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/miasma_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/midas_touch.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/midas_touch_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/netherite_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/netherite_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nightfall.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nightfall_3d.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nightfall_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nightmare_sword.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nightmare_sword_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nightmare_sword_layer.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nihilite.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nihilite.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nyxium_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/nyxium_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/onyx_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/onyx_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/pale_branch.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/pickle_paddle.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/pickle_paddle_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/pixie_alloy.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/pixie_alloy.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/raw_nihilite.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/real_knife.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/real_knife_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/reverium_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/reverium_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/ruby_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/ruby_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sacrifice.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sacrifice_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sapphire_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sapphire_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/skysplitter_sword.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/skysplitter_sword_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/soft_cloth.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sorrowful_ghast.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/starlight.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/starlight_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/stone_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/stone_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/stop_sign.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/stop_sign_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sword_of_acclaim.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sword_of_acclaim_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sword_of_majesty.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sword_of_majesty_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sword_of_splendor.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/sword_of_splendor_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/tainted_blade.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/tainted_blade_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/topaz_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/topaz_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/ultra_backup_sos.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/vehemence.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/vehemence_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/violence.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/violence_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/virentium_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/virentium_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/void_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/void_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/warden_spine.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/wooden_bafs.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/item/wooden_bafs_gui.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/acid_venom.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/blade_mode.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/bmmode.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/brimstone_flames.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/brimstone_flames_buff.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/crumbling.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/cuteness.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/hellfire.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/hex_flames.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/hfmmode.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/iron_will.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/rage.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/stained_calamity.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/mob_effect/tranquility.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/models/armor/battle_maid_layer_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/models/armor/battle_maid_layer_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/models/armor/hf_battle_maid_layer.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/models/armor/hf_battle_maid_layer_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/models/armor/hf_battle_maid_layer_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_bubble_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_bubble_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_bubble_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_bubble_4.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_impact_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_impact_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_impact_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/acidic_impact_4.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/crumbling.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/custom_particle_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/custom_particle_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/custom_particle_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/custom_particle_4.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/custom_particle_5.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/cute_sparkles_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/cute_sparkles_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/cute_sparkles_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/forbidden_glint_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/forbidden_glint_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/forbidden_glint_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/forbidden_glint_4.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/forbidden_glint_5.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke.png.mcmeta
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke_1.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke_2.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke_3.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke_4.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/gael_smoke_5.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/hex_flame.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/real_crit.png
reference/context files/lethalitydecompiled/decompiled/assets/lethality/textures/particle/tranquility.png
reference/context files/lethalitydecompiled/decompiled/data/curios/tags/items/charm.json
reference/context files/lethalitydecompiled/decompiled/data/curios/tags/items/hands.json
reference/context files/lethalitydecompiled/decompiled/data/curios/tags/items/heart.json
reference/context files/lethalitydecompiled/decompiled/data/curios/tags/items/necklace.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/damage_type/acid_venom.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/damage_type/brimstone_flames.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/damage_type/hellfire.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/damage_type/hex_flames.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/damage_type/meowrasama_slash.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/forge/biome_modifier/nihilite_ore_biome_modifier.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/loot_tables/blocks/nihilite_ore.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/alfajor_de_maicena.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/annihilation_alloy.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/backup_sos.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/battle_maid_boots.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/battle_maid_chestplate.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/battle_maid_helmet.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/battle_maid_leggings.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/bladecrest_oathsword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/blighted_cleaver.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/broken_biome_blade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/caustic_edge.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/charged_redstone_piece.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/conductite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/conjuror_boots.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/conjuror_chestplate.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/conjuror_helmet.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/conjuror_leggings.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/cosmilite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/defiled_greatsword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/devils_devastation.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/diamond_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/dimlite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/escarapela.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/exalted_oathblade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/excalibur.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/exodium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/fairy_pickle.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/flowey.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/forbidden_oathblade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/foreign_hook.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/gaels_greatsword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/gamblers_blade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/golden_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/grievance.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/hellspec_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/hf_battle_maid_boots.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/hf_battle_maid_chestplate.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/hf_battle_maid_helmet.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/hf_battle_maid_leggings.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/hf_meowrasama.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/horseless_headless_horsemanns_headtaker.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/improbability_steel.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/iridium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/iron_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/mate.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/mate_dulce.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/miasma.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/midas_touch.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/netherite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/nightfall.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/nightmare_sword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/nihilite.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/nyxium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/onyx_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/pale_branch.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/pickle_paddle.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/pixie_alloy.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/real_knife.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/reverium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/ruby_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/sacrifice.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/sapphire_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/soft_cloth.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/sorrowful_ghast.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/starlight.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/stone_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/stop_sign.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/tainted_blade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/topaz_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/ultra_backup_sos.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/vehemence.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/violence.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/virentium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/void_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/warden_spine.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/recipes/wooden_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/tags/items/bosscaliburs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/tags/mob_effect/stacking.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/bladecrest_oathsword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/blighted_cleaver.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/caustic_edge.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/conductite_bafpb.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/cosmilite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/defiled_greatsword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/devils_devastation.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/diamond_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/dimlite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/exalted_oathblade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/exodium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/forbidden_oathblade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/foreign_hook.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/gaels_greatsword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/gamblers_blade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/golden_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/grievance.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/hellspec_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/hf_meowrasama.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/horseless_headless_horsemanns_headtaker.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/iridium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/iron_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/miasma.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/midas_touch.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/netherite_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/nightfall.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/nightmare_sword.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/nyxium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/onyx_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/pickle_paddle.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/real_knife.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/reverium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/ruby_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/sacrifice.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/sapphire_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/starlight.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/stone_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/stop_sign.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/tainted_blade.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/topaz_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/vehemence.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/violence.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/virentium_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/void_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/weapon_attributes/wooden_bafs.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/worldgen/configured_feature/nihilite_ore.json
reference/context files/lethalitydecompiled/decompiled/data/lethality/worldgen/placed_feature/nihilite_ore.json
reference/context files/lethalitydecompiled/decompiled/data/minecraft/tags/blocks/mineable/pickaxe.json
reference/context files/lethalitydecompiled/decompiled/data/minecraft/tags/blocks/needs_diamond_tools.json
reference/context files/lethalitydecompiled/decompiled/logo.png
reference/context files/lethalitydecompiled/decompiled/META-INF/jarjar/metadata.json
reference/context files/lethalitydecompiled/decompiled/META-INF/jarjar/mixinextras-forge-0.4.1.jar
reference/context files/lethalitydecompiled/decompiled/META-INF/MANIFEST.MF
reference/context files/lethalitydecompiled/decompiled/META-INF/mods.toml
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/block/custom/NihiliteOre.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/block/ModBlocks.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/BladeModeEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/ModEffectEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/AcidicSlashModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/BBBProjectionModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/BloodScytheModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/DevilsPitchforkModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/DevilsScytheModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/ForbiddenScytheModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/GaelSkullModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/HFMeowrasamaSlashModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/HookModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/ModelCatEarsHelmet.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/PickleBallModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/RealSlashModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/StarlightStabModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/UltraSOSBiteModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/model/VehemenceBoltModel.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/RadialInputHandler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/RadialModeHandler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/registry/KeyBindRegistry.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/AcidicSlashRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/BBBProjectionRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/BloodScytheRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/DevilsPitchforkRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/DevilsScytheRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/ForbiddenScytheRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/GaelSkullRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/HFMeowrasamaSlashRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/HookRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/PickleBallRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/RadialOverlayRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/RealSlashRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/StarlightStabRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/UltraSOSBiteRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/client/renderer/VehemenceBoltRenderer.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/DataGenerators.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/loot/ModBlockLootTables.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/ModBlockStateProvider.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/ModBlockTagGenerator.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/ModItemModelProvider.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/ModItemTagGenerator.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/ModLootTableProvider.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/datagen/ModRecipeProvider.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/AcidicSlashEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/BBBProjectionEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/BloodScytheEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/DevilsPitchforkEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/DevilsScytheEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/ForbiddenScytheEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/GaelSkullEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/HFMeowrasamaSlashEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/HookEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/ModEntities.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/PickleBallEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/RealSlashEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/StarlightStabEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/UltraSOSBiteEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/entity/VehemenceBoltEntity.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/BrokenBiomeBladeEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ClientArmorAbilityHandler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ClientForgeEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ClientModEventHandler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ClientOverlayHandler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/CommonForgeEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/DamageBoostHandler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/GamblersBladeEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/HFMeowrasamaEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/MidasTouchEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/NightfallEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/NightmareSwordEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/SacrificeEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ScheduledViolence.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/StopSignEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ViolenceDamageScheduler.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/events/ViolenceEvents.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/init/ModDamageTypes.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/init/ModMobEffects.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/init/ModSounds.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/AlfajorDeMaicenaItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/AnnihilationAlloyItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/BattleMaidBootsItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/BattleMaidChestplateItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/BattleMaidHelmetItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/BattleMaidLeggingsItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/HFBattleMaidBootsItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/HFBattleMaidChestplateItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/HFBattleMaidHelmetItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/armor/HFBattleMaidLeggingsItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/BladecrestOathswordItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/BlightedCleaverItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/BloodCoinItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/BrokenBiomeBladeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/CausticEdgeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ConductiteBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/CosmiliteBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/BackupSOSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/BaseCurioItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/ChargedRedstonePieceItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/FloweyItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/IFluidWalk.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/PaleBranchItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/SorrowfulGhastItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/UltraBackupSOSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/curios/WardenSpineItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/DefiledGreatswordItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/DevilsDevastationItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/DiamondBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/DimliteBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/EscarapelaItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ExaltedOathbladeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ExodiumBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/FairyPickleItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ForbiddenOathbladeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ForeignHookItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/FuelItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/GaelsGreatswordItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/GamblersBladeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/GoldenBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/GrievanceItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/HellspecBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/HFMeowrasamaItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/HorselessHeadlessHorsemannsHeadtakerItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ImprobabilitySteelItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/IridiumBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/IronBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/MateDulceItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/MateItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/MetalDetectorItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/MiasmaItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/MidasTouchItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/NetheriteBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/NightfallItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/NightmareSwordItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/NihiliteItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/NyxiumBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/OnyxBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/PicklePaddleItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/PixieAlloyItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/RawNihiliteItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/RealKnifeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ReveriumBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/RubyBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SacrificeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SapphireBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SkysplitterSwordItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SoftClothItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/StarlightItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/StoneBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/StopSignItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SwordOfAcclaimItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SwordOfMajestyItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/SwordOfSplendorItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/TaintedBladeItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/TopazBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/VehemenceItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/ViolenceItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/VirentiumBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/VoidBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/custom/WoodenBAFSItem.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/ModArmorMaterials.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/ModCreativeModTabs.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/ModFoods.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/ModItems.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/item/ModToolTiers.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/LethalityMod.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/networking/ModMessages.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/networking/packets/C2S/AcidicSlashC2S.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/networking/packets/C2S/BattleMaidAbilityPacket.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/networking/SelectModePacket.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/AcidicBubble.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/AcidicBubbleFork.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/AcidicImpact.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/Crumbling.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/CustomParticle.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/CuteSparkles.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/GaelSmoke.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/HexFlame.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/ModParticles.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/RealCrit.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/particles/Tranquility.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/AcidVenomEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/BladeModeEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/BMEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/BrimstoneFlamesBuffEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/BrimstoneFlamesEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/CrumblingEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/CutenessEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/HellfireEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/HexFlamesEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/HFMEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/IronWillEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/IStackingEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/RageEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/StainedCalamityEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/potion/TranquilityEffect.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/util/ModRarities.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/util/ModTags.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/util/ModUtils.java
reference/context files/lethalitydecompiled/decompiled/net/daphne/lethality/util/TooltipGlitchHelper.java
reference/context files/lethalitydecompiled/decompiled/pack.mcmeta
reference/context files/OBSCUREAPI_CONTEXT.txt
reference/context files/TOOLTIP_OVERHAUL_CONTEXT.txt
reference/Detailed Armory 1.0.0.zip
reference/fabric_snapshot/.gitignore
reference/fabric_snapshot/build.gradle
reference/fabric_snapshot/CHANGELOG.md
reference/fabric_snapshot/gradle.properties
reference/fabric_snapshot/gradle/wrapper/gradle-wrapper.jar
reference/fabric_snapshot/gradle/wrapper/gradle-wrapper.properties
reference/fabric_snapshot/gradlew
reference/fabric_snapshot/gradlew.bat
reference/fabric_snapshot/LICENSE_CODE
reference/fabric_snapshot/LICENSE_RESOURCES
reference/fabric_snapshot/README.md
reference/fabric_snapshot/resources/legendary_chestplate.png
reference/fabric_snapshot/resources/logo.png
reference/fabric_snapshot/settings.gradle
reference/fabric_snapshot/src/main/java/draylar/tiered/api/AttributeTemplate.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/BorderTemplate.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/CustomEntityAttributes.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/ItemVerifier.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/ModifierUtils.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/PotentialAttribute.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/SetBonusLogic.java
reference/fabric_snapshot/src/main/java/draylar/tiered/api/TieredItemTags.java
reference/fabric_snapshot/src/main/java/draylar/tiered/util/AttributeHelper.java
reference/fabric_snapshot/src/main/java/draylar/tiered/util/TieredTooltip.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/access/AnvilScreenHandlerAccess.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/command/CommandInit.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/compat/ArmageddonTreasureBagHooks.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/compat/ItemBordersCompat.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/compat/TierifyBorderLayer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/compat/TooltipOverhaulCompat.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/config/ClientConfig.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/config/CommonConfig.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/config/CommonConfigAutoSync.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/config/EntityLootDropProfiles.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/config/ModMenuIntegration.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/config/TreasureBagProfiles.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/data/AttributeDataLoader.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/data/ReforgeDataLoader.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/data/TooltipBorderLoader.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/gson/EntityAttributeModifierDeserializer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/gson/EntityAttributeModifierSerializer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/gson/EquipmentSlotDeserializer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/gson/EquipmentSlotSerializer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/gson/FormattingDeserializer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/gson/TextColorDeserializer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/item/ReforgeAddition.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/AnvilScreenHandlerMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/ArmorItemMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/ArmorStandEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/ArrowEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/BowItemMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/client/AnvilScreenMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/client/DrawContextMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/client/HandledScreenAccessor.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/client/HandledScreenMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/client/ItemStackClientMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/ArmageddonTreasureBagProceduresMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/CuriosBrutalityUuidSaltMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/LethalityScalingFixMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/ModAnvilScreenMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/ObscureApiAttackSpeedIconMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/ObscureApiAttributeIconMathMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/ObscureApiIconsMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/SkillInfoScreenMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/TieredMixinPlugin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/TooltipOverhaulFrameMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/TooltipOverhaulRenderMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/compat/TooltipRendererAccessor.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/CrossbowItemMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/EnchantmentHelperMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/EntityEquipmentDropMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/EntityLootDropMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/ItemFrameEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/ItemMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/ItemStackMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/LivingEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/LootTableMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/MerchantScreenHandlerMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/MobEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/PlayerEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/SpectralArrowEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/mixin/TridentEntityMixin.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/network/TieredClientPacket.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/network/TieredServerPacket.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/registry/ItemRegistry.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/registry/SoundRegistry.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/component/PerfectTierComponent.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/PerfectBorderRenderer.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/PerfectLabelAnimator.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/ReforgeScreen.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/TierGradientAnimator.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/widget/AnvilTab.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/client/widget/ReforgeTab.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/screen/ReforgeScreenHandler.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/Tierify.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/TierifyClient.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/util/AttributeHelper.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/util/SetBonusUtils.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/util/TagFallbackMatcher.java
reference/fabric_snapshot/src/main/java/elocindev/tierify/util/TieredTooltip.java
reference/fabric_snapshot/src/main/resources/assets/emi/tag/exclusions/tiered.json
reference/fabric_snapshot/src/main/resources/assets/legendarytooltips/frame_definitions.json
reference/fabric_snapshot/src/main/resources/assets/legendarytooltips/textures/gui/tiered_borders.png
reference/fabric_snapshot/src/main/resources/assets/minecraft/font/default.json
reference/fabric_snapshot/src/main/resources/assets/tiered/lang/en_us.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/charoite.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/cleansing_stone.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/crown_topaz.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/galena_chunk.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/limestone_chunk.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/painite.json
reference/fabric_snapshot/src/main/resources/assets/tiered/models/item/pyrite_chunk.json
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds.json
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds/reforge_sound_common.ogg
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds/reforge_sound_epic.ogg
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds/reforge_sound_legendary.ogg
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds/reforge_sound_mythic.ogg
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds/reforge_sound_rare.ogg
reference/fabric_snapshot/src/main/resources/assets/tiered/sounds/reforge_sound_uncommon.ogg
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/gui/anvil_tab_icon.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/gui/reforge_tab_icon.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/gui/reforging_screen.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/gui/tiered_borders.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/icon.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/charoite.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/cleansing_stone.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/crown_topaz.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/galena_chunk.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/LICENSE.md
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/limestone_chunk.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/painite.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/item/pyrite_chunk.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/plates/common.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/plates/epic.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/plates/legendary.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/plates/mythic.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/plates/rare.png
reference/fabric_snapshot/src/main/resources/assets/tiered/textures/plates/uncommon.png
reference/fabric_snapshot/src/main/resources/assets/tiered/tooltips/tooltip_borders.json
reference/fabric_snapshot/src/main/resources/data/c/tags/items/armors.json
reference/fabric_snapshot/src/main/resources/data/c/tags/items/bows.json
reference/fabric_snapshot/src/main/resources/data/c/tags/items/shields.json
reference/fabric_snapshot/src/main/resources/data/c/tags/items/tools/melee_weapons.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_10.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_11.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_12.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_13.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_14.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_15.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_16.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_17.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_18.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_19.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_20.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/common_armor_9.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_10.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_11.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_12.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_13.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_14.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_15.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_16.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_17.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_18.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_19.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_20.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/epic_armor_9.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_10.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_11.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_12.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_13.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_14.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_15.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_16.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_17.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_18.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_19.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_20.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/legendary_armor_9.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_10.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_11.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_12.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_13.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_14.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_15.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_16.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_17.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_18.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_19.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_20.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/mythic_armor_9.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_10.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_11.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_12.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_13.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_14.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_15.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_16.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_17.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_18.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_19.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_20.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/rare_armor_9.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncommon_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_10.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_11.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_12.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_13.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_14.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_15.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_16.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_17.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_18.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_19.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_20.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_armor/uncomon_armor_9.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/common_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/epic_tool_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/legendary_tool_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/mythic_tool_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/rare_tool_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncommon_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncomon_tool_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncomon_tool_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncomon_tool_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/all_tools/uncomon_tool_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/common_elytra_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/common_elytra_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/epic_elytra_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/epic_elytra_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/legendary_elytra_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/legendary_elytra_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/mythic_elytra_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/mythic_elytra_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/rare_elytra_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/rare_elytra_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/uncommon_elytra_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/elytra/uncommon_elytra_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/common_fishing_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/common_fishing_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/epic_fishing_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/epic_fishing_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/legendary_fishing_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/legendary_fishing_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/mythic_fishing_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/mythic_fishing_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/rare_fishing_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/rare_fishing_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/uncommon_fishing_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/fishing_rod/uncommon_fishing_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/common_melee_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/epic_melee_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/legendary_melee_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/mythic_melee_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/rare_melee_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncommon_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/melee_weapons/uncomon_melee_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/common_ranged_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/epic_ranged_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/legendary_ranged_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/mythic_ranged_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/rare_ranged_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncommon_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_7.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/ranged_weapons/uncomon_ranged_8.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/common_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/common_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/common_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/common_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/common_shield_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/epic_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/epic_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/epic_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/epic_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/epic_shield_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/legendary_shield_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/mythic_shield_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/rare_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/rare_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/rare_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/rare_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/rare_shield_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncommon_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncomon_shield_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncomon_shield_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncomon_shield_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncomon_shield_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/shields/uncomon_shield_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/common_staffwand_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/common_staffwand_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/epic_staffwand_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/epic_staffwand_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/legendary_staffwand_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/legendary_staffwand_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/mythic_staffwand_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/mythic_staffwand_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/rare_staffwand_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/rare_staffwand_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/uncommon_staffwand_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/item_attributes/staves_and_wands/uncommon_staffwand_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/charoite_modded.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/cleansing_stone.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/crown_topaz_modded.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/galena_vanilla.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/limestone_to_create_limestone.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/limestone_vanilla.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/painite_modded.json
reference/fabric_snapshot/src/main/resources/data/tiered/recipes/pyrite_vanilla.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/bow.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/compat_mutantmonsters.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/crossbow.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/elytra.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/fishing_rods.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/lethalitynihilite.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramityblackmatter.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramitycthoniccrystal.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramitydaemoniumchunk.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramityfairydust.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramityhellspecalloy.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramityiridium.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramitypixiealloy.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramityreverium.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramityruby.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramitytopaz.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/terramitywardensoul.json
reference/fabric_snapshot/src/main/resources/data/tiered/reforge_items/trident.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/main_offhand_item.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_base_item.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_1.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_2.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_3.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_4.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_5.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_6.json
reference/fabric_snapshot/src/main/resources/data/tiered/tags/items/reforge_tier_cleanse.json
reference/fabric_snapshot/src/main/resources/fabric.mod.json
reference/fabric_snapshot/src/main/resources/tiered.mixins.json
resources/legendary_chestplate.png
resources/logo.png
settings.gradle
tools/standalone_reforge_prune.py
```
