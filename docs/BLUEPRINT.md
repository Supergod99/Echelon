# Blueprint

Echelon is a Forge port and extension of the Tiered/Tierify lineage. The public display name is Echelon, but the active technical identity remains `tiered` for compatibility with existing resources, data packs, configs, saved item data, and integrations.

## Active Shape

- Loader and target: Forge-only, Minecraft 1.20.1, Forge 47.4.10.
- Java target: 17.
- Active modid/resource namespace: `tiered`.
- Display name in `mods.toml`: Echelon.
- Main entry point: `elocindev.tierify.TierifyForge`.
- Mixin config: `forge/src/main/resources/tiered.forge.mixins.json`.
- Default config/profile source: `forge/src/main/resources/echelon-defaults/`.

## Runtime Entry And Registration

`TierifyForge` registers Echelon items, menus, sounds, attributes, mob effects, global loot modifier codecs, network packets, Apex effects, config specs, and config/profile reload hooks. It copies default files from `echelon-defaults` into `config/echelon/` when missing.

Server-side gameplay and data loading live mainly in `ForgeTieredAttributeSubscriber`, `ForgeGameplayEventSubscriber`, `ForgeServerTickSubscriber`, `TierifyLootModifier`, config/profile parsers, reforge/salvage menus, and Apex effect code. Client-only rendering and controls live under `forge/client`, `screen/client`, and client mixins.

## Reforge System

The reforge system applies data-driven tier templates to valid equipment. Six tiers are active: Common, Uncommon, Rare, Epic, Legendary, and Mythic.

- Attribute templates live under `forge/src/main/resources/data/tiered/item_attributes/`.
- Reforge item/material mappings live under `forge/src/main/resources/data/tiered/reforge_items/`.
- Reforge material tags live under `forge/src/main/resources/data/tiered/tags/items/reforge_tier_1.json` through `reforge_tier_6.json`, plus `reforge_tier_cleanse.json`.
- Registered reforge materials include Limestone Chunk, Pyrite Chunk, Galena Chunk, Charoite, Crown Topaz, Painite, and Cleansing Stone.
- `ReforgeMenu`, `ReforgeScreen`, `ForgeScreenTabs`, and `TryReforgeC2S` cover the anvil tab/screen flow.
- The reforge target slot accepts one item, and server-side readiness rejects stacked targets so one operation cannot modify a whole stack.
- `ForgeReforgeReloadListener` and `ForgeReforgeData` load reforge base mappings.
- `ForgeTieredAttributeSubscriber` applies, clears, rolls, scales, and syncs tiered attribute data.

## Salvage And Salvage Upgrade

Salvage is active. `SalvageMenu`, `SalvageUpgradeMenu`, their client screens, and the related C2S packets provide salvaging and salvage-upgrade flows. Common config values under `[echelon.salvage]` control max salvage tier and none/lower/higher outcome chances.

Keep salvage work server-authoritative. Client screens should request actions; server menu/network handlers should validate inventory state and apply results.

## Stars And Apex

Stars and Apex are active progression layers on Mythic gear.

- `StarApexUtils` stores and reads `Stars` and `Apex` state.
- Stars can raise Mythic gear up to five stars.
- Apex upgrades apply to eligible five-star Mythic gear and add an additional scaling bonus.
- Apex effect registration lives in `ApexEffectRegistry` and `ApexEffectsBootstrap`.
- Active effect execution lives in `ApexActiveEffects`, mob effect classes, server tick/event hooks, C2S keybind packets, and client keybind handlers.
- Apex tooltip rendering uses `ApexEffectTooltipComponent`, lang keys, gradient animation, and optional Tooltip Overhaul integration.

## Perfect Roll Mechanic

Perfect Roll is active and configurable through `perfectRollChance` in `echelon-common.toml`.

- Perfect state is stored on tiered item data as `Perfect`.
- Perfect items skip negative attribute entries during attribute application.
- Perfect items render the Perfect label and special tooltip border/client presentation.
- Perfect armor set behavior is also referenced by set bonus logic and related config values.

## Tooltip And Client Rendering

Client tooltip presentation includes custom borders, animated gradient labels, tier plates, Perfect labels, Apex labels, and optional compatibility rendering.

- Tier name and tooltip text insertion: `ForgeTierNameTooltipSubscriber`.
- Gradient animation: `TierGradientAnimatorForge`, `PerfectLabelAnimatorForge`, and `ApexEffectGradientAnimatorForge`.
- Border data and rendering: `ForgeTooltipBorderReloadListener`, `TierifyTooltipBorderRendererForge`, and `assets/tiered/tooltips/tooltip_borders.json`.
- Low-level tooltip hooks are mixin-backed. Treat tooltip mixins as active and high-risk.
- JEI/EMI screen overlap protection is controlled by `jeiReserveExtraAreas` in `echelon-client.toml`.

## Data, Resources, And Config

Active data/resources are under `forge/src/main/resources/`.

- `data/tiered/item_attributes/`: tier and item attribute templates.
- `data/tiered/reforge_items/`: reforge base/material mappings.
- `data/tiered/tags/items/`: reforge material and base item tags.
- `data/tiered/recipes/`: reforge material, cleansing stone, stardust, and stellar core recipes.
- `data/tiered/loot_modifiers/` and `data/forge/loot_modifiers/`: global loot modifier data and Forge registration.
- `assets/tiered/lang/en_us.json`: active English strings.
- `assets/tiered/textures/`, `models/`, `font/`, `sounds/`, and `tooltips/`: active client assets.
- `echelon-defaults/`: default common/client config and profile files copied to `config/echelon/`.

## Loot, Drops, And Tier Assignment

Tier assignment is shared by direct reforging, generated loot, entity item handling, entity equipment drops, crafting/merchant options, and compatibility hooks.

- Chest loot uses `TierifyLootModifier` through Forge global loot modifiers.
- Entity loot-table drops and entity equipment drops use active mixins plus profile/config checks.
- Dimension tier weights are profile-driven when `useDimensionTierWeights` is enabled.
- Entity and treasure-bag profiles support explicit IDs and wildcard defaults where the parser supports them.
- Reforge material loot profiles control material injection into loot.

## Compatibility Systems

Active compatibility areas include:

- Tooltip Overhaul: custom frame/wrapper tooltip compatibility guarded by the mixin plugin.
- Obscure API: attribute icon and tooltip line mixins guarded by the mixin plugin.
- JEI: plugin class and reserved GUI areas.
- EMI: plugin class, tag exclusions, and reserved GUI behavior.
- Curios/Brutality: UUID salt compatibility mixin, currently guarded by Curios presence.
- Armageddon treasure bags: hooks, profile support, and guarded procedure mixins.
- Optional spell/combat integrations: Ars Nouveau, Iron's Spells-style spell hooks, and Combat Roll hooks in Apex code.
