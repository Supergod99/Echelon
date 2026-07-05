# Tasks

This is the active backlog. Keep it limited to unfinished or future-relevant work; move completed facts to `docs/BLUEPRINT.md` or `docs/DECISIONS.md`.

## Active priorities

- Keep `StandaloneResourcePolicyTest` passing when adding data, recipes, Apex effects, tooltip deciders, dependencies, or docs.
- Validate Stars and Apex persistence through relog, inventory moves, anvil use, reforge, cleanse, salvage, and Apex upgrade paths.
- Validate Star/Apex scaling at baseline, 5-star, and Apex states.
- Validate Perfect Roll behavior so negative attribute entries are suppressed in applied modifiers and preview text.
- Run tooltip smoke passes for long item names, large GUI scales, native rendering, Perfect items, starred Mythic items, Apex items, and set bonus display.
- Run reforge, salvage, salvage-upgrade, loot/drop, Stars/Apex, and Perfect Roll smoke tests before gameplay releases.

## Bugs/regressions

- Resolve runtime log spam from legal files under `assets/tiered/font` without weakening license distribution in release artifacts.
- Investigate deprecated Forge config registration warnings and migrate only after equivalent client/server lifecycle behavior is verified.
- Attribute Gradle deprecation warnings to the owning plugin or task before changing build tooling.
- Check that optional mod hooks no-op cleanly when optional mods are absent.

## Compatibility work

- Cache Tooltip Overhaul reflection by context class only after profiling and validating all supported Tooltip Overhaul render contexts.
- Add clear diagnostics for Armageddon treasure bag hook activation and unsupported generated procedure signatures.
- Smoke test Tooltip Overhaul, Obscure API, JEI, EMI, Curios/Brutality, and Armageddon treasure-bag behavior with local optional jars when needed.
- Keep optional integrations compile-only or guarded unless a durable dependency decision explicitly changes that.

## Cleanup/refactor candidates

- Make default config/profile seeding atomic with temp-file writes and a cross-platform fallback.
- Review large tooltip rendering mixins for local simplification only when paired with visual regression checks.
- Consider extracting repeated profile parsing or tier-weight validation only if future changes create meaningful duplication.
- Keep any cleanup task scoped; do not refactor Java as part of unrelated gameplay, data, or documentation changes.

## Deferred/historical ideas

- Fabric snapshot parity is reference-only. Do not treat Fabric files as an active backlog.
- Sovereign armor, Geckolib, and Infernal Sovereign work are removed/obsolete, not active Echelon work.
- Obsolete Sovereign armor planning files and empty armor model/renderer/item placeholder packages are intentionally absent; keep active armor item-attribute data in place.
- Old one-off migration checklists and completed implementation plans should stay out of this active backlog.
