# Compatibility Notes

Echelon keeps the active modid `tiered` while integrating with a Forge 1.20.1 mod ecosystem. Compatibility work should preserve that identity and should not change dependencies unless the user explicitly asks for dependency work.

## Required Runtime Metadata

Declared in `forge/src/main/resources/META-INF/mods.toml`:

- Forge `[47,)`, with the active project target at Forge 47.4.10.
- Minecraft `[1.20.1]`.
- AttributesLib (`attributeslib`), mandatory metadata dependency.

## Build Dependency Intent

Declared in `forge/build.gradle`:

- Implementation dependencies include Placebo, Apothic Attributes, LionfishAPI, Cloth API, Better Combat, and Citadel.
- Tooltip Overhaul and Obscure API are compile-only and runtime for optional compat/mixin testing.
- JEI, EMI, and Ars Nouveau have compile-time integration points and runtime smoke-test dependencies.
- Many extra mods are runtime-only for local compatibility validation.

## Tooltip Overhaul

- Active files: `TooltipOverhaulCompatForge`, `TooltipOverhaulFrameMixin`, `TooltipOverhaulWrapperMixin`, and `TooltipOverhaulTitleAlignmentMixin`.
- Plugin guard: mixin classes beginning with `elocindev.tierify.forge.mixin.compat.TooltipOverhaul` apply only when mod id `tooltipoverhaul` is loaded.
- Special case: `TooltipOverhaulTitleAlignmentMixin` is currently hard-disabled by the plugin.
- Test both vanilla tooltip rendering and Tooltip Overhaul rendering after tooltip changes.

## Obscure API

- Active files: `ObscureApiAttackSpeedIconMixin`, `ObscureApiAttributeIconMathMixin`, `ObscureApiIconsLineMixin`, and `ObscureApiIconsMixin`.
- Plugin guard: Obscure API mixins apply only when mod id `obscure_api` is loaded.
- Treat these as optional. Vanilla/no-Obscure behavior must continue to load.

## JEI

- Active file: `TierifyJeiPlugin`.
- Build setup: JEI is compile-only and runtime for local validation.
- Client config `jeiReserveExtraAreas` reserves Echelon screen areas so JEI overlays do not collide with Reforge, Salvage, and Salvage Upgrade UI.

## EMI

- Active file: `TierifyEmiPlugin`.
- Active resource: `assets/emi/tag/exclusions/tiered.json`.
- Build setup: EMI is compile-only and runtime for local validation.
- Verify reserved areas and tag exclusions when Echelon screens or item tags change.

## Curios/Brutality

- Active file: `CuriosBrutalityUuidSaltMixin`.
- Plugin guard: this mixin applies when mod id `curios` is loaded.
- The compatibility concern is UUID salt behavior for Curios/Brutality-style equipment interactions. Test with Curios present and with the relevant Brutality-style environment when available.

## Armageddon Treasure Bags

- Active files: `ArmageddonTreasureBagHooks`, `ArmageddonTreasureBagProceduresMixin`, and `ArmageddonTreasureBagEntityProceduresMixin`.
- Plugin guard: Armageddon mixins apply only when mod id `armageddon_mod` is loaded.
- Active profiles: `echelon-treasure-bag-profiles.txt` and the `treasureBagDropModifier` config.
- Verify profile matching and item reforging with Armageddon loaded.

## Other Known Guards And Optional Hooks

- `SkillInfoScreenMixin` applies only when mod id `levelz` is loaded.
- `FontTextureSwizzleMixin` is currently hard-disabled by the plugin.
- Apex spell hooks no-op when supported spell mods are absent.
- Combat Roll integration is initialized reflectively and should no-op safely when Combat Roll is absent.
- Optional compatibility should fail closed: absence of an optional mod must not crash client or server startup.

## Compatibility Rules

- Before deleting a compat class, search for mixin config entries, plugin guards, registration paths, resources, lang keys, and build dependencies.
- When adding or changing an optional integration, document whether it is implementation, compile-only, runtime-only, or metadata-required.
- For tooltip work, test both vanilla tooltip rendering and Tooltip Overhaul rendering where practical.
