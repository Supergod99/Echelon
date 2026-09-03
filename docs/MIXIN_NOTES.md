# Mixin Notes

Mixins are active project behavior. The mixin config and class names are high-risk compatibility surface and must not be casually moved, renamed, deleted, or retargeted.

## Config And Plugin

- Mixin config path: `forge/src/main/resources/tiered.forge.mixins.json`.
- Mixin package: `elocindev.tierify.forge.mixin`.
- Mixin plugin path: `forge/src/main/java/elocindev/tierify/forge/mixin/TieredForgeMixinPlugin.java`.
- Refmap: `tiered.refmap.json`.
- Compatibility level: `JAVA_17`.
- `required`: `true`.
- `defaultRequire`: `1`.

`forge/build.gradle` also adds `MixinConfigs: tiered.forge.mixins.json` to the jar manifest, registers `tiered.refmap.json`, and passes `--mixin.config tiered.forge.mixins.json` to client and server dev runs.

## Plugin Guards

- Tooltip Overhaul mixins apply only with mod id `tooltipoverhaul`; `TooltipOverhaulTitleAlignmentMixin` is currently hard-disabled.
- Obscure API mixins apply only with mod id `obscure_api`.
- Armageddon treasure bag mixins apply only with mod id `armageddon_mod`.
- `CuriosBrutalityUuidSaltMixin` applies only with mod id `curios`.
- `SkillInfoScreenMixin` applies only with mod id `levelz`.
- `FontTextureSwizzleMixin` is currently hard-disabled.
- All other listed mixins return `true` from the plugin and should be treated as active.

## Active/Apply-Eligible Mixins By Purpose

Tiered item, attribute, and durability behavior:

- `ArmorItemMixin`
- `EnchantmentHelperMixin`
- `ItemStackMixin`
- `LivingEntityMixin`
- `ArmorStandEntityMixin`
- `ItemFrameEntityMixin`
- `ItemCombinerMenuAccessor`
- `MerchantMenuMixin`

Loot, drop, and generated item behavior:

- `EntityEquipmentDropMixin`
- `EntityLootDropMixin`
- `compat.ArmageddonTreasureBagProceduresMixin`
- `compat.ArmageddonTreasureBagEntityProceduresMixin`

Client tooltip, screen, and item rendering:

- `client.ItemStackClientMixin`
- `client.GuiGraphicsTooltipBorderMixin`
- `client.ForgeHooksClientMixin`
- `client.ClientTextTooltipAccessor`
- `client.AbstractContainerScreenAccessor`
- `client.ItemBarMixin`
- `compat.TooltipOverhaulFrameMixin`
- `compat.TooltipOverhaulWrapperMixin`
- `compat.SkillInfoScreenMixin`

Optional compatibility presentation and modifier identity:

- `compat.ObscureApiAttackSpeedIconMixin`
- `compat.ObscureApiAttributeIconMathMixin`
- `compat.ObscureApiIconsLineMixin`
- `compat.ObscureApiIconsMixin`
- `compat.CuriosBrutalityUuidSaltMixin`

## Configured But Plugin-Disabled

These classes are named in `tiered.forge.mixins.json`, so their names and paths are still high-risk, but `TieredForgeMixinPlugin` currently returns `false` for them:

- `client.FontTextureSwizzleMixin`
- `compat.TooltipOverhaulTitleAlignmentMixin`

## Rule For Adding Or Changing Mixins

Do not add a new mixin unless a normal Forge API, event, data/reload path, config path, capability, or menu/network approach is insufficient. Any mixin addition or change needs a narrow reason, a target/version check, a plugin guard decision for optional targets, and verification in `docs/TEST_PLAN.md`.

## Safety Checklist

- Search for the target class and method in the current Forge/Minecraft/dependency version before changing injection points.
- Keep accessor mixins aligned with every Java caller.
- If an optional dependency changes, verify the related mixin still has a valid target or is safely gated by the plugin.
- Keep config class names, package names, refmap names, and manifest config names synchronized.
- Run at least `.\gradlew.bat build` after any mixin class or config change.
- Run `.\gradlew.bat runClient` after tooltip, screen, client, or compat mixin changes.
- Run `.\gradlew.bat runServer` after common/server mixin changes.
- Check logs for mixin apply failures, missing targets, refmap warnings, duplicate injections, and unexpected plugin decisions.

## Documentation Rule

Any intentional mixin addition, removal, target change, config change, or plugin guard change must update this file in the same change set.
