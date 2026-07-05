# Upstream Reference

Echelon descends from Tiered, TieredZ, and Tierify. The current Forge code under `forge/src/main` is the active source of truth.

## Lineage Notes

- Tiered established the core idea of item tiers, item attribute JSON, tiered NBT, and tooltip presentation.
- TieredZ and Tierify are intermediate forks in the lineage.
- Echelon is the current Forge-only project display name, while `tiered` remains the active namespace for compatibility.
- Some package names still contain `elocindev.tierify` or `draylar.tiered`; do not rename them as cleanup unless a task includes a migration plan.

## Fabric Snapshot Status

The Fabric snapshot is reference-only. It can be used to compare behavior, recover intent, or understand migration decisions, but it is not an active source tree and should not be treated as current implementation.

Historical Fabric snapshot work contributed or informed:

- Reforge materials, item models, textures, recipes, and tier tags.
- Common/client config split, default config seeding, entity drop profiles, treasure bag profiles, reforge material profiles, and dimension tier profiles.
- Loot-container rolls, dimension-weighted tiers, mob equipment weighting, and reforge material chest profiles.
- Native/optional tooltip border behavior, reforge material hover names, set bonus placement, wrapping guards, large GUI scale fixes, and refmap packaging.
- Attribute UUID handling, tag fallback matching, tier commands, and health-load protection.
- Salvaging and Apex/Stars behavior, now active Echelon features rather than migration backlog items.

## `reference/` Folder Purpose

`reference/` is local reference material, not active mod source. It contains upstream snapshots, compatibility context, decompiled jars, images, and archived artifacts that can help explain why active Forge code looks the way it does.

Use it only for research. Do not copy behavior or assets from it into active source without checking license, compatibility, and current Echelon architecture.

## Inactive/Reference/Debug Directories

- `reference/`: upstream and compatibility research material, including `fabric_snapshot` and context files.
- `debugging_info/`: local logs, config captures, options, mod lists, and smoke-test evidence.
- `net/`: local decompiled/reference source snippets, such as Forge client classes, for debugging target behavior.
- `.tmp_sources/`: temporary extracted source/resource scratch area; treat as cache.
- `docs/superpowers/`: historical plan/spec notes. Use only as background when current docs are insufficient.

## Not Active Source

Do not treat these as active source for implementation:

- `reference/`
- `debugging_info/`
- `net/`
- `.tmp_sources/`
- `docs/superpowers/`
- Build outputs, run folders, logs, IDE folders, and Gradle caches

Active source and resources are under `forge/src/main`, active tests are under `forge/src/test`, active top-level project docs are under `docs/`, and active helper tooling is under `tools/`.

## Migration Lessons To Preserve

- Keep `tiered` IDs stable unless a task explicitly includes a data migration plan.
- Prefer Forge-native event/config/network/data patterns when adapting old Fabric behavior.
- Optional integrations must be guarded so missing mods do not prevent startup.
- Tooltip behavior must be checked with native rendering and optional Tooltip Overhaul rendering separately.
- Resource changes need scans for stale tooltip deciders, language keys, Apex mappings, recipes, and tag references.

## Files That Encode Current Policy

- `forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java`
- `tools/standalone_reforge_prune.py`
- `forge/src/main/resources/META-INF/mods.toml`
- `forge/build.gradle`
- `forge/src/main/resources/tiered.forge.mixins.json`
- `forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json`
- `forge/src/main/resources/data/tiered/item_attributes`
