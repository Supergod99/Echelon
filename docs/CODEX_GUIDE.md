# Codex Guide

This repository is **Echelon**, a Forge-only Minecraft mod. The project display name is Echelon, but the active mod id, resource namespace, data namespace, language namespace, and NBT-facing identity remain `tiered`.

## Active Target

- Loader: Forge only.
- Minecraft: `1.20.1`.
- Forge: `47.4.10`.
- Java: `17`.
- Build archive name: `Echelon`.
- Active modid/resource namespace: `tiered`.
- Root Gradle project delegates to `forge/` through `settings.gradle`.
- Required runtime dependencies confirmed in build metadata: Forge, Minecraft, AttributesLib / Apothic Attributes, and Placebo.

Never rename `tiered` casually. Treat `tiered` as public compatibility surface for resources, datapacks, language keys, NBT, mixin config, and saved item data unless a task explicitly includes a migration plan.

## Before Coding

Always read these first:

- `README.md`
- `docs/CODEX_GUIDE.md`
- `docs/BLUEPRINT.md`
- `docs/TASKS.md`
- `docs/FILE_TREE.md`
- Relevant focused docs such as `docs/COMPAT_NOTES.md`, `docs/MIXIN_NOTES.md`, `docs/TEST_PLAN.md`, `docs/DECISIONS.md`, and `docs/UPSTREAM_REFERENCE.md`

Then inspect the relevant source/resource files for the task. Do not rely on historical plans as active truth.

## Work Rules

- Keep changes narrow and task-scoped.
- Do not touch unrelated subsystems.
- Do not change gameplay behavior unless the task explicitly asks for gameplay work.
- Do not refactor Java for documentation or data-only tasks.
- Do not change build files unless the task is specifically about build/dependency behavior.
- Do not rename packages, config files, resource folders, data namespaces, NBT keys, or `tiered`.
- Preserve unrelated user changes in the worktree. Check `git status --short` before editing.

## Mixins

Existing mixins are allowed and active. Do not remove, disable, move, rename, or alter active mixins casually.

Future mixin changes must be narrowly justified. Add a new mixin only when a normal Forge/API/event/data approach is insufficient, and document the reason in `docs/MIXIN_NOTES.md` or `docs/DECISIONS.md` if it becomes durable architecture.

## Documentation Maintenance

- Update `docs/FILE_TREE.md` after any file/folder create, move, rename, or delete.
- Update `docs/TASKS.md` when checklist items are completed or when active backlog changes.
- Update `docs/DECISIONS.md` for durable architecture, compatibility, namespace, or dependency decisions.
- Keep completed historical checklist noise out of `docs/TASKS.md`; preserve durable facts in `docs/BLUEPRINT.md`, `docs/DECISIONS.md`, or `docs/UPSTREAM_REFERENCE.md`.

## Common Commands

From the repository root:

```powershell
.\gradlew.bat compileJava
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

Useful policy checks:

```powershell
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest
rg -n "geckolib|GeckoLib|software\.bernie|infernal_sovereign|InfernalSovereign" forge/src/main/java forge/src/main/resources forge/build.gradle -S
rg -n "modId=\"tiered\"|tiered.forge.mixins.json|attributeslib|placebo|apothic" forge/src/main/resources/META-INF/mods.toml forge/src/main/resources/tiered.forge.mixins.json forge/build.gradle -S
```

## Source Entry Points

- Mod entrypoint and default config seeding: `forge/src/main/java/elocindev/tierify/TierifyForge.java`
- Shared constants and NBT keys: `forge/src/main/java/elocindev/tierify/TierifyConstants.java`
- Mod id constant: `forge/src/main/java/elocindev/tierify/TierifyCommon.java`
- Reforge data loading/application: `forge/src/main/java/elocindev/tierify/forge/ForgeTieredAttributeSubscriber.java`
- Reforge item mappings reload: `forge/src/main/java/elocindev/tierify/forge/reforge/`
- Reforge, salvage, and salvage-upgrade menus/screens: `forge/src/main/java/elocindev/tierify/forge/screen/`
- Stars and Apex item state helpers: `forge/src/main/java/elocindev/tierify/util/StarApexUtils.java`
- Apex effects: `forge/src/main/java/elocindev/tierify/forge/apex/`
- Client tooltip rendering: `forge/src/main/java/elocindev/tierify/forge/mixin/client/GuiGraphicsTooltipBorderMixin.java`
- Native tooltip border renderer: `forge/src/main/java/elocindev/tierify/forge/client/TierifyTooltipBorderRendererForge.java`
- Optional compat: `forge/src/main/java/elocindev/tierify/forge/compat/`
- Mixin plugin and config: `forge/src/main/java/elocindev/tierify/forge/mixin/TieredForgeMixinPlugin.java`, `forge/src/main/resources/tiered.forge.mixins.json`
- Runtime data: `forge/src/main/resources/data/tiered/`
- Runtime assets: `forge/src/main/resources/assets/tiered/`
- Default configs/profiles: `forge/src/main/resources/echelon-defaults/`
