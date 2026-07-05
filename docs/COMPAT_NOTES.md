# Compat Notes

## Required Runtime

- Forge for Minecraft 1.20.1.
- AttributesLib / Apothic Attributes.
- Placebo, because `forge/build.gradle` includes it as an implementation dependency alongside Apothic Attributes.

## Optional Integrations

- Tooltip Overhaul: optional tooltip frame/layout adapter.
- Obscure API: optional attribute icon rows, attack-speed icon handling, and tooltip math compatibility.
- JEI: optional recipe/plugin integration.
- EMI: optional recipe/plugin integration plus tag exclusion resource.
- Curios/Brutality: optional UUID salt compatibility through Curios presence.
- Armageddon treasure bags: optional treasure-bag reforge hooks and profile handling.
- Modded item mappings: optional `data/tiered/reforge_items` JSON files for other mods.

## Plugin Guards And Optional Behavior

`TieredForgeMixinPlugin` guards optional mixins:

- Tooltip Overhaul mixins apply only when `tooltipoverhaul` is loaded, except `TooltipOverhaulTitleAlignmentMixin`, which is currently listed but returns `false`.
- Obscure API mixins apply only when `obscure_api` is loaded.
- `SkillInfoScreenMixin` applies only when `levelz` is loaded.
- Armageddon treasure-bag mixins apply only when `armageddon_mod` is loaded.
- `CuriosBrutalityUuidSaltMixin` applies only when `curios` is loaded.
- `FontTextureSwizzleMixin` is currently listed but returns `false`.

Keep optional integrations guarded and no-op safe when the target mod is absent. Do not make optional mods mandatory runtime dependencies unless `docs/DECISIONS.md` is updated with a durable decision.

## Tooltip Overhaul

Native Echelon rendering is the primary path. Tooltip Overhaul behavior is an optional adapter path:

- Native border data: `assets/tiered/tooltips/tooltip_borders.json`
- Native renderer: `TierifyTooltipBorderRendererForge`
- Native client mixin: `GuiGraphicsTooltipBorderMixin`
- Wrapping guard: `ForgeHooksClientMixin`
- Optional adapter: `TooltipOverhaulCompatForge`
- Optional mixins: `TooltipOverhaulFrameMixin`, `TooltipOverhaulTitleAlignmentMixin`, `TooltipOverhaulWrapperMixin`

Smoke test Tooltip Overhaul in normal and expanded/scrolling tooltip modes when touching tooltip dimensions, wrappers, or title alignment.

## Obscure API

Obscure API support is compile-only and guarded. Active compat mixins are:

- `ObscureApiAttackSpeedIconMixin`
- `ObscureApiAttributeIconMathMixin`
- `ObscureApiIconsLineMixin`
- `ObscureApiIconsMixin`

Smoke test attribute icon rows, set bonus math, Perfect item lines, and tooltip title spacing.

## JEI And EMI

- JEI plugin: `TierifyJeiPlugin`
- EMI plugin: `TierifyEmiPlugin`
- EMI exclusion resource: `assets/emi/tag/exclusions/tiered.json`

Keep these integrations optional. Recipe/category changes should be checked with and without the optional mod loaded.

## Curios/Brutality

`CuriosBrutalityUuidSaltMixin` is guarded by `curios`. Treat Brutality-specific behavior as optional compatibility; the active guard is Curios presence. Smoke test that the path activates only when the relevant optional mod stack is loaded.

## Armageddon Treasure Bags

Armageddon treasure-bag compatibility uses:

- `ArmageddonTreasureBagHooks`
- `ArmageddonTreasureBagProceduresMixin`
- `ArmageddonTreasureBagEntityProceduresMixin`
- `TreasureBagProfiles`
- `echelon-defaults/echelon-treasure-bag-profiles.txt`

Default config keeps treasure-bag drops disabled. When enabled, profile files control chance and tier weights. Generated procedure class names can change upstream, so future fixes should add clear diagnostics without making Armageddon mandatory.

## Fragile Areas

- Optional mixin target class names can drift between optional mod versions.
- Tooltip Overhaul render contexts vary by mode and version.
- Obscure API tooltip rows can affect title alignment and modifier math.
- Large GUI scale tooltip wrapping must preserve title, Perfect label, star ribbon, and Apex crown spacing.
- Optional item IDs in `data/tiered/reforge_items` may be absent and must remain safe.
