# Test Plan

Use this matrix when changing code, data, assets, configs, or compatibility behavior. Documentation-only changes can usually stop at status/diff checks unless they alter policy or required commands.

## Command Checks

From the repository root:

```powershell
./gradlew compileJava
./gradlew build
./gradlew test
./gradlew runClient
./gradlew runServer
```

On Windows PowerShell, `.\gradlew.bat <task>` is equivalent.

Run the focused policy test when changing dependencies, docs, resources, reforge data, tooltip deciders, recipes, or Apex mappings:

```powershell
./gradlew test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest
```

Run `./gradlew compileJava --rerun-tasks --warning-mode all` when touching mixins, Forge lifecycle code, or build tooling.

## Tooltip Smoke Tests

- Native tooltip rendering with no optional tooltip mods installed.
- Tooltip Overhaul absent and present.
- Tier borders for all six tiers.
- Gradient tier labels and long custom item names.
- Perfect label, Perfect border, and Perfect starred Mythic border.
- Star ribbon for 1-5 stars.
- Apex crown plate, Apex star band, Apex effect label, and effect description.
- Set bonus crest and Perfect set bonus text.
- Large GUI scale and narrow screen wrapping without overlap.

## Reforge/Salvage Tests

- Reforge each tier material: Limestone, Pyrite, Galena, Charoite, Crown Topaz, and Painite.
- Cleanse tiered gear with Cleansing Stone.
- Confirm damaged-item reforge behavior follows `allowReforgingDamaged`.
- Confirm preview text matches applied modifiers, including Perfect negative suppression.
- Salvage tiered gear into expected material tiers.
- Validate salvage fail/lower/same/higher odds against config.
- Validate salvage upgrade deposits, persisted progress, upgrade levels, and max tier handling.
- Confirm player salvage data survives clone/relog paths where practical.

## Loot/Drop Tests

- Loot-container gear rolls follow `lootContainerModifier` and `lootContainerModifierChance`.
- Reforge material chest drops follow `reforgeMaterialLootModifier`, chance, profiles, and dimension weights.
- Entity loot-table drops follow `entityLootDropModifier` and profile gates.
- Entity equipment drops follow `entityEquipmentDropModifier`.
- Dimension tier profiles and all-zero behavior work as configured.
- Crafting and merchant reforge assignment obey their config switches.
- Armageddon treasure-bag profiles are inactive by default and apply only when enabled and loaded.

## Stars/Apex Tests

- Stars apply only to valid Mythic, non-Apex gear and clamp to 0-5.
- Apex applies only to valid 5-star Mythic gear.
- Star scaling is +5% per star.
- Apex scaling reaches +50% total.
- Stars and Apex state persist through inventory moves, anvil use, reforge, cleanse, salvage checks, and relog.
- Apex active effects respect cooldowns, trigger type, full-set requirements, and server authority.

## Perfect Roll Tests

- Perfect chance follows `perfectRollChance`.
- Perfect NBT survives expected item flows.
- Negative attribute entries are not applied to Perfect items.
- Positive attribute entries still scale with stars/Apex.
- Perfect tooltip label and border render consistently with native and optional tooltip paths.

## Optional Compatibility Smoke Tests

Run first without optional mods. Then add local jars only for smoke passes; do not promote optional jars to runtime dependencies unless a task explicitly decides that.

- Tooltip Overhaul: guarded mixins load only when present, frame replacement works, title alignment remains stable, and there is no double rendering.
- Obscure API: icon rows, attack speed icon handling, and set bonus math remain stable.
- JEI: plugin discovery does not crash and recipes/categories remain usable.
- EMI: plugin/exclusion behavior does not crash.
- Curios/Brutality: guarded UUID salt path activates only when Curios is loaded.
- Armageddon treasure bags: hooks apply configured profiles or produce clear diagnostics for unsupported generated procedure signatures.

## Mixin/Plugin Guard Tests

- Confirm `tiered.forge.mixins.json` is packaged and passed to client/server dev runs.
- Confirm optional mixins are guarded in `TieredForgeMixinPlugin`.
- Confirm intentionally disabled mixins remain documented.
- Run client and dedicated server startup after mixin target changes.
- Prefer a focused compile plus startup smoke test whenever changing mixin target names, class names, injection points, or plugin guards.
