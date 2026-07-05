# Mixin Notes

- Mixin config path: `forge/src/main/resources/tiered.forge.mixins.json`
- Mixin package: `elocindev.tierify.forge.mixin`
- Mixin plugin path: `elocindev.tierify.forge.mixin.TieredForgeMixinPlugin`
- Refmap: `tiered.refmap.json`

Mixin config and class names are high-risk compatibility surface. Do not casually move, rename, remove, or disable the config, plugin, package, or listed mixin classes.

## Rule For Adding/Changing Mixins

Use normal Forge APIs, events, registries, data, configs, or network packets first. Add a new mixin only when those approaches are insufficient for the target behavior. Any mixin addition or target change must be narrow, justified, guarded when optional, documented here, and verified with compile plus client/server startup or focused smoke tests.

## Gameplay/Data Mixins

- `ArmorStandEntityMixin`: armor stand equipment/tier behavior.
- `ArmorItemMixin`: armor item behavior hooks.
- `EntityEquipmentDropMixin`: equipment drop tier assignment.
- `EntityLootDropMixin`: entity loot drop tier assignment.
- `EnchantmentHelperMixin`: enchantment-related tier behavior.
- `ItemFrameEntityMixin`: item frame handling for tiered stacks.
- `ItemCombinerMenuAccessor`: accessor for anvil/reforge menu integration.
- `ItemStackMixin`: item stack tier data and attribute behavior.
- `LivingEntityMixin`: living entity/load behavior such as health handling.
- `MerchantMenuMixin`: merchant/trade reforge assignment path.

## Client Tooltip/UI Mixins

- `client.ItemStackClientMixin`: item name gradients, Apex effect lines, Perfect/star/Apex tooltip content.
- `client.GuiGraphicsTooltipBorderMixin`: native tooltip borders, title layout, Perfect label, stars, Apex crown plate, set bonus crest.
- `client.ForgeHooksClientMixin`: tooltip wrapping guard for tiered tooltips.
- `client.ClientTextTooltipAccessor`: client tooltip accessor.
- `client.AbstractContainerScreenAccessor`: screen accessor used by GUI integration.
- `client.ItemBarMixin`: item bar client rendering hooks.
- `client.FontTextureSwizzleMixin`: listed but currently disabled by plugin guard.

## Optional Compatibility Mixins

- Tooltip Overhaul: `compat.TooltipOverhaulFrameMixin`, `compat.TooltipOverhaulTitleAlignmentMixin`, `compat.TooltipOverhaulWrapperMixin`.
- Obscure API: `compat.ObscureApiAttackSpeedIconMixin`, `compat.ObscureApiAttributeIconMathMixin`, `compat.ObscureApiIconsLineMixin`, `compat.ObscureApiIconsMixin`.
- LevelZ: `compat.SkillInfoScreenMixin`.
- Armageddon treasure bags: `compat.ArmageddonTreasureBagProceduresMixin`, `compat.ArmageddonTreasureBagEntityProceduresMixin`.
- Curios/Brutality: `compat.CuriosBrutalityUuidSaltMixin`.

## Current Plugin Guards

- Tooltip Overhaul mixins require `tooltipoverhaul`, except `TooltipOverhaulTitleAlignmentMixin`, which currently returns `false`.
- Obscure API mixins require `obscure_api`.
- `SkillInfoScreenMixin` requires `levelz`.
- Armageddon treasure-bag mixins require `armageddon_mod`.
- `CuriosBrutalityUuidSaltMixin` requires `curios`.
- `FontTextureSwizzleMixin` currently returns `false`.
- All other listed mixins return `true`.

## Verification Focus

- Entity loot/equipment mixins: profile gating, config gates, and dimension-weighted tier rolls.
- Living entity mixin: max-health load behavior and relog safety.
- Item stack/client mixins: name gradients, Apex effect lines, Perfect/star/Apex tooltip content.
- GuiGraphics tooltip mixin: native border bounds, title centering, set bonus crest, Perfect label, stars, Apex crown plate, and large GUI scale.
- ForgeHooks client mixin: wrapping guard for tiered tooltips.
- Tooltip Overhaul mixins: guarded load, frame replacement, scroll/expanded mode title alignment, and no double render.
- Obscure API mixins: icon row insertion, attack speed icon correction, modifier math, and set bonus values.
- Armageddon treasure-bag mixins: generated procedure target names and no-op behavior when absent.
