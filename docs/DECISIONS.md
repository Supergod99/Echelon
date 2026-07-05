# Decisions

Record durable architecture and compatibility decisions here. Do not use this file as a task checklist.

## Current Durable Decisions

- The display name is Echelon, but the active mod id and namespace remain `tiered`.
- Never rename `tiered` casually. Any namespace migration would need an explicit data, NBT, resource, mixin, and compatibility migration plan.
- The active build target is Forge-only Minecraft 1.20.1 with Forge 47.4.10 and Java 17.
- The Fabric snapshot is reference-only. It can inform behavior investigations but is not active source or an active backlog.
- Sovereign armor, Geckolib, and Infernal Sovereign work is obsolete/removed; its planning files and empty armor model/renderer/item placeholder packages should stay absent unless a new explicit product decision revives that work.
- Existing mixins remain part of the architecture. They must not be removed, moved, renamed, or disabled casually.
- Tooltip Overhaul and Obscure API are optional compatibility targets. Native Echelon tooltip rendering must remain functional without Tooltip Overhaul.
- JEI, EMI, Curios/Brutality, Armageddon treasure bags, and modded reforge item mappings are optional compatibility surfaces, not mandatory runtime dependencies.
- Apothic Attributes / AttributesLib and Placebo are active dependencies as confirmed by `forge/build.gradle`; `mods.toml` declares `attributeslib` mandatory.
- Config filenames under the generated `echelon/` folder are stable public surface.
- Data-driven reforges remain under `data/tiered/item_attributes`; renumbering or ID churn is compatibility-sensitive.
- Optional datapack reforge item mappings may reference absent mods because missing optional item IDs must remain safe.
- Completed Apex/Stars and Salvaging task logs are historical. Active behavior belongs in `docs/BLUEPRINT.md`, active follow-up work belongs in `docs/TASKS.md`, and validation belongs in `docs/TEST_PLAN.md`.

## Deferred Technical Decisions

- Tooltip Overhaul reflection caching may help performance, but it needs profiling and validation across Tooltip Overhaul render contexts first.
- Atomic default config writes are desirable for crash resilience, but must preserve current filenames and Windows behavior.
- Armageddon treasure-bag diagnostics should help unsupported generated procedure signatures without noisy warnings across optional-mod load orders.
- Font license file placement should reduce startup log noise while preserving license distribution in release artifacts.
- Deprecated Forge config API migration is deferred until client and dedicated server lifecycle equivalence is proven.
- Gradle deprecation cleanup should identify plugin/task ownership before build script changes.
