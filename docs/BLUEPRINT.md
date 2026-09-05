# Blueprint

Echelon is a Forge-only item progression mod for Minecraft 1.20.1. It inherits Tiered/Tierify concepts while keeping the active namespace `tiered`.

## Architecture Overview

- Reforge system: datapack-driven tier definitions are loaded from `data/tiered/item_attributes`, synced to clients, and applied through `ForgeTieredAttributeSubscriber`. Reforge materials gate tiers 1-6: Limestone, Pyrite, Galena, Charoite, Crown Topaz, and Painite. Cleansing Stone removes tier data through the reforge flow. The target slot and server validation require exactly one item. Base-item validation prefers an explicit reforge mapping, otherwise enforces a defined repair ingredient, and uses the generic reforge-base tag only when neither exists.
- Salvage and salvage-upgrade system: `SalvageMenu`, `SalvageUpgradeMenu`, and their client screens support salvaging tiered gear into reforge materials, config-driven odds, persisted player salvage level, partial upgrade deposits, and upgrade costs.
- Stars and Apex system: `StarApexUtils` stores star count and Apex state in `TierifyExtra`. Stars are limited to Mythic gear, run from 0-5, and scale reforge strength by +5% per star. Apex requires Mythic, 5 stars, and Apex Crux, then uses +50% total scaling and can attach Apex effects.
- Perfect Roll mechanic: a configurable chance marks reforged items as `Perfect`. Perfect reforges keep the upside and suppress negative attribute entries in attribute application and preview display.
- Tooltip presentation: native client rendering owns tier borders, animated gradient tier labels, Perfect label, Perfect border, star ribbon, Apex crown plate, Apex effect label, and set bonus crest. Tooltip data lives in `assets/tiered/tooltips/tooltip_borders.json`, with rendering centered around `GuiGraphicsTooltipBorderMixin`, `ForgeHooksClientMixin`, `ItemStackClientMixin`, and `TierifyTooltipBorderRendererForge`.
- Tier/item attribute data: active reforge definitions live under `forge/src/main/resources/data/tiered/item_attributes`. IDs follow `tiered:<tier>_<category>_<number>` and should remain stable.
- Config/profile systems: bundled defaults live under `forge/src/main/resources/echelon-defaults`; runtime files are seeded under the generated `echelon/` config folder. Profiles cover entity drops, dimension tier weights, reforge material loot, and treasure bags.
- Loot/drop/tier assignment systems: `TierifyLootModifier` handles loot-container gear reforges and reforge material chest drops. Entity loot-table and equipment drops use mixins plus profile/config gates. Crafting and merchant reforge assignment are config-gated Forge event paths.

## Compatibility Systems

- Tooltip Overhaul: optional compile-only integration with guarded mixins and `TooltipOverhaulCompatForge`. Native Echelon tooltip rendering must work when Tooltip Overhaul is absent.
- Obscure API: optional guarded mixins for attribute icon rows, attack speed icon handling, and tooltip math/alignment.
- JEI and EMI: optional compile-only recipe/plugin integration. EMI also has `assets/emi/tag/exclusions/tiered.json`.
- Curios/Brutality: optional guarded UUID salt compatibility through `CuriosBrutalityUuidSaltMixin`.
- Armageddon treasure bags: optional hooks and guarded mixins apply treasure-bag reforge profiles only when the target mod is loaded and config enables the behavior.
- Modded reforge item mappings: JSON files under `data/tiered/reforge_items` may reference absent mods; missing item IDs must remain safe.

## Server And Client Separation

- Server-authoritative behavior includes reforge rolls, salvage actions, salvage upgrades, loot/drop assignment, star/Apex item mutations, Apex active effect handling, config/profile loading, and datapack reloads.
- Client behavior includes tooltip layout/rendering, animated labels, GUI screens, keybind registration, and local display of synced reforge/config data.
- Network packets in `forge/src/main/java/elocindev/tierify/forge/network` bridge client screen actions and server state, then sync attributes, configs, and reforge mappings back to clients.

## Active Source Shape

- `forge/src/main/java/elocindev/tierify`: entrypoint, constants, platform helpers, common utilities, set bonus and star/Apex helpers.
- `forge/src/main/java/elocindev/tierify/forge`: Forge implementation, registries, networking, screens, config, loot, events, compat, and mixins.
- `forge/src/main/resources/assets/tiered`: runtime client assets, language, models, sounds, fonts, tooltip borders, and textures.
- `forge/src/main/resources/data/tiered`: datapack recipes, tags, item attributes, reforge item mappings, weapon attributes, and loot modifier data.
- `forge/src/main/resources/echelon-defaults`: default config and profile seed files.
- `forge/src/test/java/elocindev/tierify/forge/standalone`: policy tests for the Forge-only standalone target.
