# Upstream Reference

This file separates historical context from the active Forge build. Use it to orient porting and comparison work, not to override current project facts.

## Tiered/Tierify Lineage

- Tiered by Draylar is the original project lineage.
- TieredZ by Globox_Z is a fork in that lineage.
- Tierify by ElocinDev is the direct fork Echelon builds from.
- Echelon keeps the `tiered` modid and Tierify package/API shape for compatibility while using Echelon as the display name.

## Fabric Snapshot Status

The local ignored directory `reference/fabric_snapshot/` is reference-only. It is not an active build, source set, loader target, dependency source, or mixin source.

Observed snapshot facts:

- Minecraft: 1.20.1.
- Loader: Fabric Loader 0.14.21.
- Mappings: Yarn 1.20.1+build.10.
- Fabric API: 0.86.0+1.20.1.
- Snapshot mod version: 1.2.1.
- Snapshot modid: `tiered`.
- Snapshot name: Echelon.
- Snapshot entrypoints reference Fabric classes such as `elocindev.tierify.Tierify` and `TierifyClient`.

Historical Fabric dependencies included Fabric API, Cloth Config, Mod Menu, Reach Entity Attributes, AutoTag, Necronomicon, LibZ, Tooltip Overhaul, Brutality, LevelZ, TooltipFix, EasyAnvils, Curios, Obscure API, and MixinExtras. Do not assume those are active Forge dependencies.

## `reference/` Folder Purpose

`reference/` is ignored local context. It may contain historical snapshots, decompiled third-party files, old notes, or comparison material. Read it only when a task explicitly needs upstream/history comparison. Do not import code, resources, dependency assumptions, or file layout from `reference/` without checking the active Forge tree and build files.

## Inactive/Reference/Debug Directories

These local ignored folders are not active source:

- `reference/`: historical snapshots and local comparison material.
- `reference/fabric_snapshot/`: inactive Fabric snapshot.
- `reference/context files/`: decompiled/reference material for compatibility research.
- `debugging_info/`: local debug notes or dumps.
- `.tmp_sources/`: temporary extracted/reference source material.
- `net/`: local stub/reference files outside the active Forge source set.
- `forge/run/`, `forge/run-server/`, and `forge/logs/`: dev runtime output, configs, logs, worlds, screenshots, and generated runtime files.

## What To Reuse Carefully

- Data-driven modifier ideas and `data/tiered/item_attributes` concepts.
- `tiered` namespace compatibility expectations.
- README explanations for modifiers, verifiers, NBT values, tooltip borders, and reforge item data where they still match Forge behavior.
- Upstream bug context when it directly maps to active Forge code.

## What Not To Treat As Active Source

- Fabric loader, Loom, Yarn mappings, Fabric entrypoints, Fabric mixin config names, Fabric dependencies, and Fabric-specific APIs.
- Snapshot version numbers or old release targets.
- Decompiled third-party code in `reference/context files/`.
- Local debug output, runtime configs, generated resource packs, crash reports, and world saves under ignored dev folders.
- Old feature, porting, or backlog checklist files unless a new task explicitly asks to recover historical context.

## Obsolete Backlog Treatment

Fabric snapshot backlog and old planning files are not active work. Their durable lesson is captured here: current work targets Forge-only Echelon, while Fabric and other upstream material are reference-only.
