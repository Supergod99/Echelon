# Test Plan

Use the smallest verification that covers the change. Documentation-only changes normally need a markdown review and `git diff --name-only`; code, data, resource, build, config, mixin, and compatibility work need focused Gradle and smoke checks.

On Windows, `.\gradlew.bat` is the local wrapper equivalent of the `./gradlew` commands below.

## Baseline Commands

```bash
./gradlew compileJava
./gradlew build
./gradlew test
./gradlew runClient
./gradlew runServer
```

Expected baseline:

- Java 17 toolchain is used.
- Minecraft target is 1.20.1.
- Forge target is 47.4.10.
- The jar manifest includes `MixinConfigs: tiered.forge.mixins.json`.
- Client and server dev runs load the active mixin config without missing-target failures.

## Docs-Only Verification

- `git diff --name-only` should show markdown/docs changes only, except when the task explicitly allows another file.
- Re-open every changed markdown file and check that required project facts are present.
- Confirm no Java, resource, Gradle build logic, mixin class, mixin config, or gameplay data file changed.

## Tooltip Smoke Tests

- Run `./gradlew runClient`.
- Inspect representative Common, Uncommon, Rare, Epic, Legendary, Mythic, Perfect, starred, and Apex items.
- Verify tooltip borders, tier gradient labels, tier plates when enabled, Perfect label, Apex label/component, and set bonus text.
- Toggle `tieredTooltip`, `showPlatesOnName`, and `centerName` where relevant.
- Repeat with Tooltip Overhaul present when testing compatibility.

## Reforge And Salvage Tests

- Reforge eligible armor, melee, ranged, tool, shield, elytra, fishing rod, and staff/wand items.
- Test Limestone, Pyrite, Galena, Charoite, Crown Topaz, and Painite tier materials.
- Test Cleansing Stone removal behavior.
- Verify a vanilla item with a repair ingredient accepts that ingredient and rejects the generic fallback (for example, a diamond sword accepts a diamond but rejects an iron ingot).
- Verify an item without an explicit reforge mapping or repair ingredient still accepts an item from `tiered:reforge_base_item`.
- With a modded stackable, reforgeable item, verify normal clicks and shift-clicks move only one item into the target slot, leave the remainder in the cursor or inventory, and consume one base plus one reforge material per operation.
- Verify damaged item behavior follows `allowReforgingDamaged`.
- Open Reforge, Salvage, and Salvage Upgrade screens from the intended anvil/tab flows.
- Salvage tiered gear and verify none/lower/same/higher outcomes are plausible against config.
- Upgrade salvage tier and verify `salvageMaxTier` is respected.

## Loot And Drop Tests

- With `lootContainerModifier` enabled, inspect representative generated chest loot.
- Verify reforge material injection using `echelon-reforge-material-profiles.txt`.
- With entity drop/equipment toggles enabled, verify explicit ID, `*`, and `modid:*` profile matching where supported.
- Verify dimension tier weights in Overworld, Nether, End, and one modded dimension override.
- Confirm all-zero dimension weights disable rolls only when `dimensionTierWeightsZeroMeansNoModifier` is true.
- Verify Armageddon treasure-bag profiles with Armageddon loaded.

## Stars And Apex Tests

- Add stars to Mythic gear from zero to five stars.
- Verify attribute scaling, tooltip star display, and persistence after reload.
- Upgrade eligible five-star Mythic gear to Apex.
- Trigger active Apex effects through keybind/network flow.
- Trigger passive Apex effects through their normal gameplay events.
- Verify Apex cooldown, counter, spell, summon, roll, ranged momentum, and unbreakable armor behavior where applicable.

## Perfect Roll Tests

- Force or repeat reforge attempts until a Perfect item is produced, or use available command/debug tooling if already present.
- Verify negative attribute entries are suppressed on Perfect items.
- Verify positive attributes still apply.
- Verify the Perfect label and special border render.
- Verify Perfect set bonus behavior when a full matching armor set is equipped.

## Optional Compatibility Smoke Tests

- Tooltip Overhaul: compare vanilla tooltip and Tooltip Overhaul rendering paths.
- Obscure API: verify guarded icon/line mixins do not crash and show expected attribute presentation.
- JEI and EMI: verify Echelon menus reserve expected screen areas and do not overlap recipe widgets.
- Curios/Brutality: verify UUID salt compatibility does not duplicate or collide modifiers.
- Armageddon: verify treasure bag hooks and guarded mixins load only when the mod is present.
- Ars Nouveau, Iron's Spells-style spell mods, and Combat Roll: verify Apex hooks initialize and no-op safely when absent.

## Mixin And Plugin Guard Tests

- Build after any mixin class, mixin config, or plugin guard change.
- Run `./gradlew runClient` after tooltip, screen, client, or optional compat mixin changes.
- Run `./gradlew runServer` after common/server mixin changes to confirm no client-only class leaks.
- Test optional guarded mixins both with and without the target mod where practical.
- Check logs for mixin apply failures, missing targets, refmap warnings, duplicate injections, and plugin guard surprises.
