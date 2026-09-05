# Standalone Release Comparison

Audited 2026-09-04. This is a source comparison and release checklist, not a runtime certification or an implemented branch migration.

## Compared Revisions

- Standalone: `standalone-apothic` at `a07d8f1` (2026-07-05).
- Current: `forge-only-migration` at `960c97e` (2026-09-03).
- Common ancestor: `cf04dac7`; standalone has 18 unique commits, current has 2. Neither tip contains the other.
- Local branches match their locally stored `origin/` refs. No remote fetch was performed.
- Endpoint diff: 297 files, 12,151 insertions, 7,597 deletions. This includes documentation and JSON formatting, not just behavior changes.
- Working tree was clean before this documentation task.

## Differences That Matter For Release

| Area | Standalone Apothic | Current branch |
| --- | --- | --- |
| Platform and identity | Forge 47.4.10, Minecraft 1.20.1, Java 17, version property 1.2.6, modid `tiered` | Same; build targets, version properties, and `mods.toml` are identical between tips |
| Required metadata | Forge, Minecraft, AttributesLib | Same; metadata alone does not establish standalone readiness |
| Development dependencies | Placebo and Apothic implementation dependencies; optional Tooltip Overhaul, Obscure API, JEI, EMI, Ars compile APIs remain | Adds LionfishAPI, Cloth API, Better Combat, Citadel as implementation dependencies and many runtime test mods, including Tooltip Overhaul, Armageddon, spell mods, Terramity and Geckolib |
| Bundled reforges | 220 templates; 7 armor rolls per tier; no staff/wand category | 280 templates; 15 armor rolls per tier; 2 staff/wand rolls per tier |
| Attribute data | Vanilla, Echelon and AttributesLib attribute types | Also references Ars Nouveau, Iron's Spells, Spell Power, Combat Roll, Brutality, Reach Entity Attributes, Traveloptics, FamiliarsLib and Geomancy Plus |
| Echelon attributes | Removes summon-health and Ars spell-power attributes and their application paths | Registers and applies both |
| Apex | Removes spell, summon and Combat Roll behavior; retains armor boost, absorption, slow time, unbreakable armor and Apothic ranged momentum; remaps armor IDs | Contains spell surge/cooldown, summon and roll integrations, including two additional mob-effect classes |
| Recipes | Vanilla/Echelon recipes for Charoite, Crown Topaz, Painite, Stardust and Apex Crux | Charoite needs End Remastered/Biomancy, Crown Topaz needs Terramity/Armageddon, Painite needs Armageddon; no Stardust or Apex Crux crafting recipe; also ships a Create limestone conversion |
| Treasure bags | Bundled toggle false; profile is opt-in comments | Bundled toggle true; nine active Armageddon profiles |
| Native tooltips | Handles reforge materials without tier NBT; reserves star/Apex header space; uses textured set crests and equipped-stack matching by UUID/tags | Lacks those standalone changes; uses stricter stack-instance checks and different crest drawing |
| Reforge validation | Older menu accepts generic fallback even when a repair ingredient exists; target slot is not capped at one | Caps target at one, rejects stacked targets server-side, and enforces repair ingredients before generic fallback |
| Automated safeguards | Nine JUnit resource-policy tests, JUnit wiring, pruning script and standalone design/implementation docs | Those standalone additions are absent |
| Removed custom content | Infernal Sovereign/associated weapon assets and Geckolib source integration removed | Removal also completed here; Geckolib remains in the development runtime |

The other template category counts are identical: tools 28, elytra 12, fishing rods 12, melee 48, ranged 48, shields 30. Many retained JSON files still differ because standalone strips external attributes, adjusts values or normalizes formatting. Equal counts do not mean equal rolls.

Both retain Perfect rolls, salvage, stars, Apex upgrades, loot/profile systems, optional reforge-item mappings, and guarded compatibility. The mixin configuration and plugin are identical; some client mixin implementations differ. Standalone means independence from the modpack, not removal of all optional compatibility.

## Recommended Release Path

Use `standalone-apothic` as the candidate base. It already implements the standalone policy. Port the focused `ReforgeMenu.java` validation change from `b1121d7`, along with its README and test scenarios. Avoid blindly taking that entire commit: it also replaces documentation and overlaps custom-content removal already completed on standalone. The other current-only commit, `960c97e`, only changes `.gitignore`.

Before calling the candidate final:

1. Run standalone's focused policy suite, then `test` and `build`. Its test path is `forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java`; its helper is `tools/standalone_reforge_prune.py`. They do not exist on the current branch.
2. Launch a clean client and dedicated server with Echelon, Forge, Apothic Attributes and its required dependency chain, including Placebo. Check optional-mod absence and mixin logs. A pack-filled development run cannot prove this requirement.
3. Complete `TEST_PLAN.md` gameplay checks: obtain all progression materials in survival, reforge/cleanse/salvage, add stars and Apex, trigger each retained Apex effect, and verify Perfect/set bonuses. Include the newer repair-ingredient and single-item checks.
4. Inspect native tooltips without Tooltip Overhaul across all tiers, materials, Perfect, stars, Apex and equipped set bonuses; repeat the optional compatibility pass separately.
5. Review renumbered IDs before promising existing-world support. For example, slow time changes from `mythic_armor_13` to `mythic_armor_5`; the standalone design explicitly declines Linggango migration aliases. Preserving `tiered` alone does not preserve every saved reforge's meaning.
6. Test fresh configs and existing configs separately: defaults are copied only when missing, so changing bundled treasure-bag defaults does not reset an existing installation.
7. Verify the produced jar's version, metadata, dependency instructions, licenses, README and release notes. Both tips say 1.2.6; that does not prove the standalone artifact has been built, uploaded or released.

## Evidence And Verification

Comparison used `git log`, `git merge-base`, `git rev-list --left-right --count`, endpoint diffs, tree counts, attribute namespace scans, and focused source/resource reads at the revisions above. Primary evidence is `forge/build.gradle`, `ReforgeMenu.java`, `ApexActiveEffects.java`, `ApexEffectsBootstrap.java`, client tooltip mixins, attribute/item registries, `data/tiered/item_attributes/`, `data/tiered/recipes/`, and `echelon-defaults/` under the active Forge source tree.

Documentation-only verification follows `TEST_PLAN.md`: review changed Markdown, inspect `git diff --name-only`, and run `git diff --check`. No Gradle build, automated gameplay test, client/server launch, branch switch, merge, or publication was performed during this audit. Release readiness remains unverified.
