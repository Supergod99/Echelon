# Standalone Apothic Branch Design

Date: 2026-04-13
Branch: standalone-apothic
Base branch: forge-only-migration

## Goal

Create a standalone Echelon variant for Forge 1.20.1 that keeps the existing mod id, `tiered`, and requires only Minecraft Forge plus Apothic Attributes / AttributesLib at runtime. The branch should remain suitable for a separate CurseForge upload while avoiding Linggango-only assumptions in shipped data, recipes, dependencies, Apex effects, and docs.

## Scope

This branch will keep the core Echelon gameplay loop: randomized reforges, perfect rolls, tier materials, cleansing, salvage, stars, Apex upgrades, tooltip presentation, loot-container rolls, entity/crafting/trade config hooks, and optional compatibility hooks that safely no-op when the target mod is absent.

This branch will remove mandatory dependencies and shipped content that make the mod pack-specific. It will not rename the mod id, NBT subtag, resource namespace, config folder, or existing registry namespace.

## Runtime Dependencies

The only required gameplay dependency after the standalone work should be `attributeslib` for Apothic Attributes. `forge` and `minecraft` remain required through `mods.toml`.

Geckolib will be removed from `mods.toml`, Gradle dependencies, and source usage. The unused Infernal Sovereign armor classes and assets will be deleted because they will not be used in either branch.

Compile-time optional integrations can remain only when they are intentional and do not make the built jar fail without the optional mod. Examples include JEI/EMI plugin compile APIs and mixins guarded by the existing mixin plugin. Development-only runtime dependencies should be trimmed for the standalone branch so the dev environment reflects the target dependency set.

Tooltip Overhaul must not be a runtime dependency for this branch. If optional Tooltip Overhaul integration code remains, it should be either reflection-based or compile-only with mixins guarded by `TieredForgeMixinPlugin`, and the built jar must load when Tooltip Overhaul is absent.

## Reforge Data

Bundled `data/tiered/item_attributes` will be reduced to attributes from these namespaces:

- `minecraft`
- `tiered`
- `attributeslib`

Entries that only depend on removed attribute namespaces will be removed. Mixed entries will either have external attributes removed or be rewritten with standalone equivalents. Removed namespaces include `irons_spellbooks`, `ars_nouveau`, `spell_power`, `combatroll`, `brutality`, `reach-entity-attributes`, `traveloptics`, `familiarslib`, and `gtbcs_geomancy_plus`.

After pruning, each category and tier must have contiguous numbering. For example, if `mythic_armor_5` through `mythic_armor_10` are removed and `mythic_armor_11` is kept, the kept entry must be renamed to the next available number such as `mythic_armor_5`. The same renumbering applies to every tier prefix and category, not just armor.

Renumbering must update all references to the reforge IDs:

- the `id` field inside each JSON file
- the filename
- tooltip border deciders
- language keys
- Apex effect registry IDs
- Apex tooltip language keys
- any tests or command expectations that mention the old IDs

The standalone branch does not need to preserve Linggango-world migration aliases for old pack-only reforge IDs. Existing standalone users should see stable IDs after this branch is released.

## Optional Reforge Item Compat

`data/tiered/reforge_items` compatibility mappings for other mods will stay. These mappings are safe because missing item ids are skipped by the loader. They provide value when a standalone user happens to install Lethality, Terramity, Mutant Monsters, Iron's Spells, Mekanism/Mekaweapons, or similar mods.

The docs should describe these as optional mappings, not as required content.

## Recipes And Progression

Linggango-only recipes will be removed or rewritten:

- `charoite_modded.json`
- `crown_topaz_modded.json`
- `painite_modded.json`
- `limestone_to_create_limestone.json`

Standalone recipes must make every core material obtainable with vanilla plus Echelon:

- tier 1: limestone chunk
- tier 2: pyrite chunk
- tier 3: galena chunk
- tier 4: charoite
- tier 5: crown topaz
- tier 6: painite
- stardust
- stellar core
- apex crux
- cleansing stone

Recipes should preserve the existing progression feel: lower-tier materials are overworld-oriented, middle-tier materials lean Nether, and high-tier / Apex materials lean End, Nether Star, Dragon's Breath, Echo Shard, or other late-game vanilla ingredients. Exact recipes should be deterministic, datapack-editable JSON recipes rather than code-gated mechanics.

## Apex Effects

Apex effects tied to removed mods will be removed or replaced. Standalone Apex effects should use vanilla, Echelon attributes, or Apothic Attributes only.

Keep or adapt these standalone-compatible Apex effect themes:

- active armor boost
- no-damage absorption shield
- slow-time aura
- unbreakable or durability-focused armor
- ranged momentum using Apothic arrow attributes
- generic damage, defense, speed, luck, crit, draw speed, arrow velocity, or experience-gained effects where useful

Remove or replace effects tied to Iron's Spells, Ars Nouveau, Combat Roll, summon-specific schools, or other Linggango-only mechanics.

Apex registrations must follow the renumbered `mythic_armor_*` IDs. Tooltip language keys and displayed descriptions must match the new IDs and behavior.

## Defaults And Config

The standalone default config should set `treasureBagDropModifier = false`. The bundled treasure-bag profile file should not ship Armageddon-specific defaults as active standalone content.

Other existing config options can remain unless they expose pack-specific behavior by default. Optional hooks should remain disabled or harmless when supporting mods are absent.

## Assets And Client

Delete Infernal Sovereign assets and models with the Geckolib removal.

Keep Echelon tooltip borders, gradient assets, item models, item textures, sounds, menu screens, JEI/EMI exclusion behavior, and optional tooltip compatibility where they do not require pack-only mods at runtime.

Any removed reforge IDs must also be removed or remapped in tooltip border data so tooltips do not reference dead IDs.

## Tooltip Independence

Echelon's native tooltip system is the primary standalone path. Tiered borders, perfect borders, perfect labels, star ribbons, Apex crown plates, Apex perimeter effects, set-bonus crest display, Apex effect fallback text, and reforge screen previews must all work with only Forge, Minecraft, Echelon, and Apothic Attributes installed.

Tooltip Overhaul remains optional compatibility only. When it is installed, Echelon can use the existing adapter layer and guarded mixins to fit Tooltip Overhaul's layout. When it is not installed, the native `GuiGraphics` tooltip mixin and `TierifyTooltipBorderRendererForge` path must render the same core information without missing borders or UI elements.

Standalone development runs should not include Tooltip Overhaul as `runtimeOnly`, because that would hide failures in the native tooltip path. A `compileOnly` dependency may remain only if direct optional mixins still need Tooltip Overhaul types at compile time.

`assets/tiered/tooltips/tooltip_borders.json` remains the native border source of truth. During reforge pruning and renumbering, its deciders must be cleaned so every kept reforge ID maps correctly and every removed or renamed reforge ID disappears from the tooltip data. The legacy `assets/legendarytooltips` resources should be reviewed as optional resource-pack compatibility and must not be required for Echelon's own tooltip presentation.

## Docs

README and related docs will describe this branch as the standalone Echelon edition. Required dependencies should list Forge and Apothic Attributes only. Optional compatibility should be clearly separated from required dependencies.

The docs should mention that standalone bundled reforges are limited to vanilla, Echelon, and Apothic Attributes by default, while datapacks or optional mod mappings can extend behavior.

## Testing

Verification should include:

- `gradlew test`
- `gradlew build`
- a resource scan proving no standalone-forbidden attribute namespaces remain in bundled `item_attributes`
- a resource scan proving removed reforge IDs are not referenced by lang, tooltip borders, Apex registration, or other bundled data
- a dependency scan proving `mods.toml` requires `attributeslib` but not `geckolib`
- a dependency scan proving Tooltip Overhaul is not a runtime dependency
- a smoke review of core recipes so each material/progression item is obtainable
- a client smoke test without Tooltip Overhaul installed, covering tiered borders, perfect labels, star/Apex visuals, Apex effect tooltip text, and reforge preview rendering
- an optional client smoke test with Tooltip Overhaul installed, covering the guarded compatibility path

Add or update automated tests for loot policy and data consistency so future branch work cannot accidentally reintroduce pack-only attribute namespaces into standalone resources.
