# Codex Guide

This is the first stop for future Codex work on Echelon. It records the active project shape, the files to read before coding, and the guardrails that keep small tasks from turning into accidental migrations.

## Project Facts

- Project display name: Echelon.
- Active target: Forge-only, Minecraft 1.20.1, Forge 47.4.10, Java 17.
- Active modid, resource namespace, data namespace, and language namespace: `tiered`.
- Active package root: `elocindev.tierify`.
- Current version from `gradle.properties`: `1.2.6`.
- Main Forge project directory: `forge/`, selected by `settings.gradle`.
- Root Gradle project name: `Echelon`.

## Read Before Coding

At the start of every new or resumed task, read `README.md` and every Markdown file under `docs/` in full once. Do not repeatedly reread unchanged documents during the same task.

Always read these before making code, data, resource, or build changes:

1. `README.md`
2. `docs/CODEX_GUIDE.md`
3. `docs/BLUEPRINT.md`
4. `docs/TASKS.md`
5. `docs/FILE_TREE.md`
6. Any relevant focused docs, especially `docs/TEST_PLAN.md`, `docs/DECISIONS.md`, `docs/COMPAT_NOTES.md`, and `docs/MIXIN_NOTES.md`.

For build, dependency, loader, or metadata work, also read `gradle.properties`, `settings.gradle`, `forge/build.gradle`, and `forge/src/main/resources/META-INF/mods.toml`.

For mixin-adjacent work, read `forge/src/main/resources/tiered.forge.mixins.json`, `forge/src/main/java/elocindev/tierify/forge/mixin/TieredForgeMixinPlugin.java`, and `docs/MIXIN_NOTES.md`.

## Guardrails

- Never rename `tiered` casually. It is the active modid and namespace for resources, data, language keys, saved item NBT, configs, integration code, and user content.
- Keep changes narrow and task-scoped.
- Do not touch unrelated subsystems while fixing or documenting one area.
- Do not change gameplay behavior during documentation-only work.
- Do not refactor Java code during documentation-only work.
- Do not change build files during documentation-only work.
- Do not rename `tiered`, `elocindev.tierify`, config filenames, resource paths, data namespaces, or language keys as cleanup.
- Do not remove or alter active mixins as cleanup.
- Existing mixins are allowed and active architecture, but any future mixin change must be narrowly justified.
- Do not add new mixins unless a normal Forge API, event, capability, data, config, or reload-listener approach is insufficient.
- Preserve server/client separation. Client screens, tooltip rendering, keybinds, and visual animation code stay client-side; server-side logic must not depend on client-only classes.

## Documentation Rules

- Update `docs/FILE_TREE.md` after any file or folder create, move, rename, or delete.
- Update `docs/TASKS.md` when checklist items are completed, removed, or newly accepted as durable backlog.
- Update `docs/DECISIONS.md` for durable architecture decisions, target changes, namespace decisions, dependency policy, or compatibility policy.
- Update `docs/COMPAT_NOTES.md` when optional dependency behavior, plugin guards, or compatibility expectations change.
- Update `docs/MIXIN_NOTES.md` in the same change set as any intentional mixin addition, removal, target change, config change, or plugin-guard change.

## Workflow

1. Classify the task as docs-only, data/resource-only, Java behavior, build/config, mixin, or compatibility work.
2. Read the required docs and the smallest relevant source/resource surface.
3. Make the smallest coherent change that satisfies the request.
4. Avoid opportunistic cleanup in adjacent packages.
5. Run the smallest useful verification from `docs/TEST_PLAN.md`.
6. Report changed files and verification clearly.

## Response And Rate-Limit Economy

- Be precise and concise. Do not restate the request, repository facts, documentation, diffs, or command output unless needed to explain a decision or risk.
- Batch related searches and file reads where practical. Inspect only the source, resources, and history needed for the task after the required documentation pass.
- Avoid routine play-by-play. Give progress updates only for a meaningful finding, decision, blocker, or long-running operation.
- Use the smallest verification that proves the change. Do not skip required safety or correctness checks to reduce usage.
- For a routine completed task, keep the final response near 150 words or less and include only: outcome, exact files/behavior changed, verification performed, and any real caveat. Omit empty sections and unsolicited next steps.
- When no files changed, say so directly. When a command was not run, do not imply that it passed.

## Reusable Continuation Prompt

Replace `<TASK>` with the requested work:

```text
Continue the Echelon project with this task: <TASK>

Before editing, read README.md and every Markdown file under docs/ in full, then follow all documented constraints. Inspect git status and preserve pre-existing or unrelated changes. Read only the additional source/resources needed for this task. Implement the smallest coherent solution; do not perform unrelated cleanup. Verify it with the smallest applicable checks in docs/TEST_PLAN.md. Update the relevant docs when the change affects documented behavior, architecture, tasks, compatibility, mixins, tests, or the file tree.

Keep progress and final responses rate-limit-conscious: concise, precise, and non-repetitive. Do not paste large diffs, logs, or restate known context. The final response must state the outcome, exact files and behavior changed, checks run with results, and only genuine caveats; keep it near 150 words unless correctness requires more detail.
```

## Common Commands

Run these from the repository root. On Windows, `.\gradlew.bat` is the local wrapper equivalent of `./gradlew`.

```powershell
.\gradlew.bat compileJava
.\gradlew.bat build
.\gradlew.bat test
.\gradlew.bat runClient
.\gradlew.bat runServer
```

For docs-only changes, `git diff --name-only` plus a focused markdown review is usually enough.
