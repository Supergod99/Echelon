# Decisions

## 2026-07-05: Display Name Is Echelon, Namespace Remains `tiered`

Status: Active

Echelon is the project display name. `tiered` remains the active modid, resource namespace, data namespace, language namespace, mixin-facing identity, and saved item/config compatibility surface. Renaming `tiered` is a breaking migration and must not happen casually.

## 2026-07-05: Forge-Only Is The Active Target

Status: Active

The active target is Forge-only, Minecraft 1.20.1, Forge 47.4.10, Java 17. Future work should assume this target unless the user explicitly asks for a coordinated target change.

## 2026-07-05: Fabric Snapshot Is Reference-Only

Status: Active

Fabric snapshot files under `reference/` are historical context only. They are not an active source set, build target, dependency source, mixin source, or version authority.

## 2026-07-05: Existing Mixins Are Active Architecture

Status: Active

`tiered.forge.mixins.json`, `TieredForgeMixinPlugin`, the refmap name, the jar manifest mixin attribute, and all active mixin classes are part of current architecture. Do not remove, move, rename, or retarget them without a narrow mixin task and client/server load verification.

## 2026-07-05: New Mixins Are A Last Resort

Status: Active

Prefer normal Forge APIs, events, reload listeners, configs, data files, capabilities, or menu/network paths. Add a new mixin only when those approaches are insufficient, and document the target, reason, guard behavior, and test coverage in `docs/MIXIN_NOTES.md`.

## 2026-07-05: Tooltip Overhaul And Obscure API Are Optional Compatibility Targets

Status: Active

Tooltip Overhaul and Obscure API are optional compatibility targets. Their integration paths are guarded through the mixin plugin and must keep vanilla/no-mod behavior working.

## 2026-07-05: Apothic Attributes And Placebo Are Active Dependencies

Status: Active

`forge/build.gradle` declares Placebo and Apothic Attributes as implementation dependencies. `mods.toml` also marks AttributesLib (`attributeslib`) mandatory. Treat these as active dependencies unless build files and metadata are intentionally changed by a future task.

## 2026-07-05: Durable Docs Live Under `docs/`

Status: Active

Durable Codex/developer documentation belongs under `docs/`. Root markdown should stay limited to `README.md`, `CHANGELOG.md`, and license files unless a future task explicitly adds another root document.
