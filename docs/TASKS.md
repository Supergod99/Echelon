# Tasks

This is the active durable backlog. Keep it limited to unfinished or future-relevant work. Do not re-add completed historical checklist noise from old planning files or ignored reference/debug folders.

## Active Priorities

- Keep `README.md` and `docs/` aligned when user-facing behavior, build targets, dependencies, config names, profile formats, or namespace facts change.
- Preserve the `tiered` namespace unless a future task explicitly requests a coordinated breaking migration.
- When editing data-driven reforges, verify JSON shape, tags, weights, lang labels, attribute IDs, and tooltip output together.
- When editing loot, drop, or profile behavior, update the matching `echelon-defaults` profile examples and run focused smoke checks.
- When touching Stars, Apex, Perfect Roll, Salvage, or armor set bonuses, record the exact manual scenario in `docs/TEST_PLAN.md` or this file.
- Keep `docs/FILE_TREE.md` current after file/folder creates, moves, renames, or deletes.

## Bugs/Regressions

- No confirmed open bug is recorded by this documentation pass.
- Track tooltip border, gradient label, Perfect label, and Apex label regressions here when reproduced.
- Track reforge/salvage inventory validation regressions here when reproduced.
- Track loot/profile wildcard, dimension weight, or treasure-bag regressions here when reproduced.
- Track mixin load failures, missing targets, and optional dependency guard regressions here when reproduced.

## Compatibility Work

- Finalize the standalone release using the revision-specific comparison and release gates in [STANDALONE_COMPARISON.md](STANDALONE_COMPARISON.md). Recommended base: `standalone-apothic`; carry over the current branch's repair-ingredient and single-target reforge validation, then verify a clean minimal client/server and native tooltips before release.

- Tooltip Overhaul: keep vanilla and Tooltip Overhaul tooltip paths visually aligned and guarded.
- Obscure API: verify guarded icon/line mixins when the optional dependency updates.
- JEI and EMI: verify reserved screen areas for Reforge, Salvage, and Salvage Upgrade screens.
- Curios/Brutality: verify UUID salt behavior with Curios present and with Brutality-style equipment interactions.
- Armageddon treasure bags: verify profile-driven treasure-bag reforging with Armageddon loaded.
- Optional Apex integrations: smoke-test Ars Nouveau, Iron's Spells-style spell hooks, and Combat Roll behavior when those mods are in the dev runtime.

## Cleanup/Refactor Candidates

- Review README typos and wording separately from behavior work.
- Add focused tests for config/profile parsers and mixin plugin guards where practical.
- Prune ignored local reference/debug folders only when the user explicitly asks for local workspace cleanup.
- Consider narrowing old compatibility assumptions in docs when source or build files prove they are no longer true.
- Do not refactor Java purely for cleanup while working on docs, data, or resource tasks.

## Deferred/Historical Ideas

- Fabric snapshot work is reference-only and not an active port target.
- Completed Apex, Stars, Salvage, and historical armor planning checklists should not be reintroduced unless a new issue describes fresh work.
- Old root scratch files such as Fabric backlog, salvage plans, Apex notes, or debug dumps are not durable docs.
