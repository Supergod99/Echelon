# Standalone Apothic Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert the `standalone-apothic` branch into a standalone Echelon build that requires only Forge, Minecraft, and Apothic Attributes / AttributesLib at runtime.

**Architecture:** Add failing standalone policy tests first, then make the branch satisfy them in small commits: dependency cleanup, Geckolib/Infernal deletion, reforge pruning and renumbering, Apex replacement, standalone recipes/defaults, tooltip independence, docs, and final build verification.

**Tech Stack:** ForgeGradle 6, Forge 1.20.1, Java 17, Gson JSON resource parsing, JUnit 5, PowerShell verification commands.

---

## File Structure

- `forge/build.gradle`: remove standalone-forbidden runtime dev dependencies and add JUnit 5.
- `forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java`: resource-policy tests for dependencies, reforge data, recipes, defaults, Geckolib removal, Tooltip Overhaul runtime independence, and Apex references.
- `tools/standalone_reforge_prune.py`: structured JSON rewrite helper for reforge pruning and renumbering.
- `forge/src/main/resources/META-INF/mods.toml`: required dependency list.
- `forge/src/main/java/elocindev/tierify/TierifyForge.java`: startup cleanup.
- `forge/src/main/java/elocindev/tierify/forge/registry/ForgeAttributeRegistry.java`: remove pack-only Echelon attributes.
- `forge/src/main/java/elocindev/tierify/forge/event/ForgeAttributeSubscriber.java`: stop attaching removed attributes.
- `forge/src/main/java/elocindev/tierify/forge/ForgeTieredAttributeSubscriber.java`: remove Ars spell power display special case.
- `forge/src/main/java/elocindev/tierify/forge/apex/ApexEffectsBootstrap.java`: register standalone Apex mappings.
- `forge/src/main/java/elocindev/tierify/forge/apex/ApexActiveEffects.java`: keep standalone Apex behavior and remove spell/summon/Combat Roll paths.
- `forge/src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java`: remove calls into deleted pack-only Apex hooks.
- `forge/src/main/java/elocindev/tierify/forge/registry/ForgeMobEffectRegistry.java`: remove obsolete spell surge and roll counter effects.
- `forge/src/main/resources/data/tiered/item_attributes/**/*.json`: prune forbidden attributes and renumber kept entries.
- `forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json`: remap deciders to kept ids.
- `forge/src/main/resources/assets/tiered/lang/en_us.json`: remove dead keys and update Apex text.
- `forge/src/main/resources/data/tiered/recipes/*.json`: replace pack-only recipes and add missing standalone recipes.
- `forge/src/main/resources/echelon-defaults/echelon-common.toml`: default treasure bag modifier off.
- `forge/src/main/resources/echelon-defaults/echelon-treasure-bag-profiles.txt`: comments only by default.
- `README.md`: standalone dependency and optional compatibility docs.

### Task 1: Add Standalone Policy Tests

**Files:**
- Modify: `forge/build.gradle`
- Create: `forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java`

- [ ] **Step 1: Add JUnit 5**

In `forge/build.gradle`, add inside `dependencies { ... }`:

```groovy
testImplementation "org.junit.jupiter:junit-jupiter:5.10.2"
```

Add after the dependency block:

```groovy
tasks.named("test", Test).configure {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Create `StandaloneResourcePolicyTest`**

Create `forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java` with these tests:

```java
package elocindev.tierify.forge.standalone;

import com.google.gson.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

final class StandaloneResourcePolicyTest {
    private static final Gson GSON = new Gson();
    private static final Set<String> ALLOWED_ATTRIBUTE_NAMESPACES = Set.of("minecraft", "tiered", "attributeslib");
    private static final Set<String> FORBIDDEN_TIERED_ATTRIBUTE_PATHS = Set.of("generic.summon_health", "generic.ars_spell_power");
    private static final Set<String> ALLOWED_RECIPE_NAMESPACES = Set.of("minecraft", "tiered");
    private static final Set<String> CORE_RECIPE_RESULTS = Set.of("tiered:limestone_chunk", "tiered:pyrite_chunk", "tiered:galena_chunk", "tiered:charoite", "tiered:crown_topaz", "tiered:painite", "tiered:stardust", "tiered:stellar_core", "tiered:apex_crux", "tiered:cleansing_stone");
    private static final Pattern REFORGE_ID = Pattern.compile("^(common|uncommon|rare|epic|legendary|mythic)_(armor|tool|melee|ranged|fishing|shield|elytra)_(\\d+)$");

    @Test void modsTomlRequiresOnlyForgeMinecraftAndAttributesLib() throws IOException {
        String toml = read(resources().resolve("META-INF/mods.toml"));
        Set<String> mandatory = new TreeSet<>();
        Matcher block = Pattern.compile("(?s)\\[\\[dependencies\\.tiered]](.*?)(?=\\n\\[\\[|\\z)").matcher(toml);
        while (block.find()) {
            String text = block.group(1);
            Matcher modId = Pattern.compile("modId\\s*=\\s*\"([^\"]+)\"").matcher(text);
            if (text.contains("mandatory=true") && modId.find()) mandatory.add(modId.group(1));
        }
        assertEquals(Set.of("attributeslib", "forge", "minecraft"), mandatory);
    }

    @Test void gradleDoesNotLoadOptionalModsAtRuntimeForStandaloneDevRuns() throws IOException {
        String gradle = read(project().resolve("build.gradle"));
        List<String> runtimeMods = gradle.lines().filter(line -> line.trim().startsWith("runtimeOnly fg.deobf(\"curse.maven:")).toList();
        assertTrue(runtimeMods.isEmpty(), "Remove runtimeOnly CurseMaven dev mods: " + runtimeMods);
        assertFalse(gradle.contains("curse.maven:geckolib"));
        assertFalse(gradle.contains("runtimeOnly fg.deobf(\"curse.maven:tooltipoverhaul"));
    }

    @Test void itemAttributeFilesUseStandaloneAttributesAndContiguousIds() throws IOException {
        Map<String, Set<Integer>> numbers = new TreeMap<>();
        List<String> failures = new ArrayList<>();
        for (Path file : jsonFiles(resources().resolve("data/tiered/item_attributes"))) {
            JsonObject json = readJson(file);
            String id = json.get("id").getAsString();
            String path = id.substring("tiered:".length());
            String stem = file.getFileName().toString().replaceFirst("\\.json$", "");
            if (!id.startsWith("tiered:") || !stem.equals(path)) failures.add(file + " has mismatched id " + id);
            Matcher m = REFORGE_ID.matcher(path);
            if (!m.matches()) failures.add(file + " has non-standalone id " + id);
            else numbers.computeIfAbsent(m.group(1) + "_" + m.group(2), ignored -> new TreeSet<>()).add(Integer.parseInt(m.group(3)));
            JsonArray attrs = json.getAsJsonArray("attributes");
            if (attrs == null || attrs.isEmpty()) failures.add(file + " has no attributes");
            else for (JsonElement el : attrs) {
                JsonObject attr = el.getAsJsonObject();
                Id type = Id.parse(attr.get("type").getAsString());
                if (!ALLOWED_ATTRIBUTE_NAMESPACES.contains(type.namespace())) failures.add(file + " uses " + type);
                if ("tiered".equals(type.namespace()) && FORBIDDEN_TIERED_ATTRIBUTE_PATHS.contains(type.path())) failures.add(file + " uses " + type);
                JsonObject modifier = attr.getAsJsonObject("modifier");
                if (modifier != null && modifier.has("name")) assertEquals(id, modifier.get("name").getAsString());
            }
        }
        numbers.forEach((group, set) -> { int i = 1; for (int n : set) { if (n != i) failures.add(group + " expected " + i + " but found " + n); i++; } });
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    @Test void tooltipBorderDecidersReferenceExistingStandaloneIds() throws IOException {
        Set<String> valid = new HashSet<>(reforgeIds());
        valid.addAll(Set.of("tiered:limestone_chunk", "tiered:pyrite_chunk", "tiered:galena_chunk", "tiered:charoite", "tiered:crown_topaz", "tiered:painite", "tiered:perfect_border", "tiered:perfect", "tiered:apex_border", "tiered:perfect_star_border"));
        JsonObject root = readJson(resources().resolve("assets/tiered/tooltips/tooltip_borders.json"));
        List<String> failures = new ArrayList<>();
        for (JsonElement tooltip : root.getAsJsonArray("tooltips")) {
            JsonObject object = tooltip.getAsJsonObject();
            Set<String> seen = new HashSet<>();
            for (JsonElement decider : object.getAsJsonArray("decider")) {
                String value = decider.getAsString();
                if (!seen.add(value)) failures.add("Duplicate decider " + value + " in index " + object.get("index"));
                if (!valid.contains(value)) failures.add("Unknown decider " + value + " in index " + object.get("index"));
            }
        }
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    @Test void recipesUseOnlyMinecraftAndTieredItemsAndCoverProgressionItems() throws IOException {
        Set<String> results = new TreeSet<>();
        List<String> failures = new ArrayList<>();
        for (Path file : jsonFiles(resources().resolve("data/tiered/recipes"))) {
            JsonObject json = readJson(file);
            List<String> ids = new ArrayList<>();
            collectItemIds(json, ids);
            for (String id : ids) if (!ALLOWED_RECIPE_NAMESPACES.contains(Id.parse(id).namespace())) failures.add(file + " references " + id);
            collectRecipeResult(json, results);
        }
        Set<String> missing = new TreeSet<>(CORE_RECIPE_RESULTS);
        missing.removeAll(results);
        if (!missing.isEmpty()) failures.add("Missing recipe results: " + missing);
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    @Test void defaultConfigsAreStandaloneSafe() throws IOException {
        assertTrue(read(resources().resolve("echelon-defaults/echelon-common.toml")).contains("treasureBagDropModifier = false"));
        List<String> active = read(resources().resolve("echelon-defaults/echelon-treasure-bag-profiles.txt")).lines().map(String::trim).filter(s -> !s.isEmpty()).filter(s -> !s.startsWith("#")).toList();
        assertTrue(active.isEmpty(), "Standalone treasure bag profiles must be opt-in only: " + active);
    }

    @Test void geckolibAndInfernalSovereignContentAreRemoved() throws IOException {
        List<String> failures = new ArrayList<>();
        for (Path file : sourceAndResourceFiles()) {
            String path = project().relativize(file).toString().replace('\\', '/').toLowerCase(Locale.ROOT);
            String text = read(file).toLowerCase(Locale.ROOT);
            if (path.contains("infernal_sovereign") || path.contains("infernalsovereign") || text.contains("software.bernie.geckolib") || text.contains("geckolib.initialize") || text.contains("infernal_sovereign") || text.contains("infernalsovereignarmor")) failures.add(path);
        }
        assertTrue(failures.isEmpty(), "Remove Geckolib/Infernal Sovereign references: " + failures);
    }

    @Test void apexCodeUsesOnlyStandaloneReferencesAndExistingMythicArmorIds() throws IOException {
        Set<String> reforgeIds = reforgeIds();
        List<String> forbidden = List.of("ironsspellbooks", "arsnouveau", "combatroll", "traveloptics", "familiarslib", "generic.ars_spell_power", "generic.summon_health", "spell_surge", "roll_counter");
        List<Path> files = List.of(project().resolve("src/main/java/elocindev/tierify/forge/apex/ApexEffectsBootstrap.java"), project().resolve("src/main/java/elocindev/tierify/forge/apex/ApexActiveEffects.java"), project().resolve("src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java"), project().resolve("src/main/java/elocindev/tierify/TierifyForge.java"));
        List<String> failures = new ArrayList<>();
        for (Path file : files) {
            String text = read(file);
            String lower = text.toLowerCase(Locale.ROOT);
            forbidden.forEach(token -> { if (lower.contains(token)) failures.add(file.getFileName() + " contains " + token); });
            Matcher m = Pattern.compile("mythic_armor_\\d+").matcher(text);
            while (m.find()) if (!reforgeIds.contains("tiered:" + m.group())) failures.add(file.getFileName() + " references missing tiered:" + m.group());
        }
        assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
    }

    private static Path project() { Path cwd = Paths.get("").toAbsolutePath(); return Files.isDirectory(cwd.resolve("src/main/resources")) ? cwd : cwd.resolve("forge"); }
    private static Path resources() { return project().resolve("src/main/resources"); }
    private static String read(Path path) throws IOException { return Files.readString(path, StandardCharsets.UTF_8); }
    private static JsonObject readJson(Path path) throws IOException { return GSON.fromJson(read(path), JsonObject.class); }
    private static List<Path> jsonFiles(Path root) throws IOException { try (Stream<Path> s = Files.walk(root)) { return s.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".json")).sorted().toList(); } }
    private static Set<String> reforgeIds() throws IOException { Set<String> ids = new TreeSet<>(); for (Path f : jsonFiles(resources().resolve("data/tiered/item_attributes"))) ids.add(readJson(f).get("id").getAsString()); return ids; }
    private static List<Path> sourceAndResourceFiles() throws IOException { List<Path> out = new ArrayList<>(); for (Path root : List.of(project().resolve("src/main/java"), resources())) try (Stream<Path> s = Files.walk(root)) { s.filter(Files::isRegularFile).filter(p -> p.toString().matches(".*\\.(java|json|txt|mcmeta|gpl)$")).forEach(out::add); } return out; }
    private static void collectItemIds(JsonElement e, List<String> out) { if (e == null || e.isJsonNull()) return; if (e.isJsonObject()) e.getAsJsonObject().entrySet().forEach(x -> { if ("item".equals(x.getKey()) && x.getValue().isJsonPrimitive()) out.add(x.getValue().getAsString()); else collectItemIds(x.getValue(), out); }); else if (e.isJsonArray()) e.getAsJsonArray().forEach(x -> collectItemIds(x, out)); }
    private static void collectRecipeResult(JsonObject recipe, Set<String> out) { JsonElement r = recipe.get("result"); if (r == null) return; if (r.isJsonPrimitive()) out.add(r.getAsString()); else if (r.isJsonObject() && r.getAsJsonObject().has("item")) out.add(r.getAsJsonObject().get("item").getAsString()); }
    private record Id(String namespace, String path) { static Id parse(String value) { return value.contains(":") ? new Id(value.split(":", 2)[0], value.split(":", 2)[1]) : new Id("minecraft", value); } }
}
```

- [ ] **Step 3: Run the failing policy test**

Run:

```powershell
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest
```

Expected: the task fails and lists current Linggango-specific resources.

- [ ] **Step 4: Commit the failing policy test**

Run:

```powershell
git add forge/build.gradle forge/src/test/java/elocindev/tierify/forge/standalone/StandaloneResourcePolicyTest.java
git commit -m "test: add standalone resource policy checks"
```

---

### Task 2: Remove Runtime Dependency Drift And Geckolib Content

**Files:**
- Modify: `forge/build.gradle`
- Modify: `forge/src/main/resources/META-INF/mods.toml`
- Modify: `forge/src/main/java/elocindev/tierify/TierifyForge.java`
- Modify: `forge/src/main/resources/assets/tiered/lang/en_us.json`
- Delete: Infernal Sovereign Java classes and assets listed below

- [ ] **Step 1: Trim Gradle dependencies**

Remove every line matching:

```groovy
runtimeOnly fg.deobf("curse.maven:
```

Remove both Geckolib dependency lines:

```groovy
runtimeOnly fg.deobf("curse.maven:geckolib-388172:7025129")
compileOnly fg.deobf("curse.maven:geckolib-388172:7025129")
```

Keep compile-only optional API lines for Tooltip Overhaul, Obscure API, JEI, EMI, JOML, DataFixerUpper, and Brigadier.

- [ ] **Step 2: Remove Geckolib from `mods.toml`**

Delete the `[[dependencies.tiered]]` block whose `modId` is `geckolib`. Leave mandatory `forge`, `minecraft`, and `attributeslib`.

- [ ] **Step 3: Remove Geckolib startup**

In `TierifyForge.java`, delete:

```java
import software.bernie.geckolib.GeckoLib;
GeckoLib.initialize();
```

- [ ] **Step 4: Delete Infernal Sovereign content**

Delete:

```text
forge/src/main/java/elocindev/tierify/forge/item/armor/InfernalSovereignArmorItem.java
forge/src/main/java/elocindev/tierify/forge/client/armor/model/InfernalSovereignArmorGeoModel.java
forge/src/main/java/elocindev/tierify/forge/client/armor/renderer/InfernalSovereignArmorRenderer.java
forge/src/main/resources/assets/tiered/animations/armor/infernal_sovereign.animation.json
forge/src/main/resources/assets/tiered/geo/armor/infernal_sovereign.geo.json
forge/src/main/resources/assets/tiered/models/item/infernal_sovereign_helmet.json
forge/src/main/resources/assets/tiered/models/item/infernal_sovereign_chestplate.json
forge/src/main/resources/assets/tiered/models/item/infernal_sovereign_leggings.json
forge/src/main/resources/assets/tiered/models/item/infernal_sovereign_boots.json
forge/src/main/resources/assets/tiered/textures/armor/infernal_sovereign.png
forge/src/main/resources/assets/tiered/textures/armor/infernal_sovereign_emissive.png
forge/src/main/resources/assets/tiered/textures/armor/infernal_sovereign_glowmask.png
forge/src/main/resources/assets/tiered/textures/armor/palettes/infernal_sovereign.gpl
```

Delete the four `item.tiered.infernal_sovereign_*` language keys from `en_us.json`.

- [ ] **Step 5: Verify and commit**

Run:

```powershell
rg -n "geckolib|GeckoLib|software\.bernie|InfernalSovereign|infernal_sovereign" forge/src/main/java forge/src/main/resources forge/build.gradle -S
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.geckolibAndInfernalSovereignContentAreRemoved
git add forge/build.gradle forge/src/main/resources/META-INF/mods.toml forge/src/main/java/elocindev/tierify/TierifyForge.java forge/src/main/resources/assets/tiered/lang/en_us.json
git add -u forge/src/main/java/elocindev/tierify/forge forge/src/main/resources/assets/tiered
git commit -m "refactor: remove Geckolib and Infernal Sovereign content"
```

Expected: `rg` has no matches, targeted test passes, commit succeeds.

---

### Task 3: Prune And Renumber Standalone Reforge Data

**Files:**
- Create: `tools/standalone_reforge_prune.py`
- Modify/Delete/Rename: `forge/src/main/resources/data/tiered/item_attributes/**/*.json`
- Modify: `forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json`
- Modify: `forge/src/main/resources/assets/tiered/lang/en_us.json`

- [ ] **Step 1: Create the structured reforge prune tool**

Create `tools/standalone_reforge_prune.py`:

```python
#!/usr/bin/env python3
import json, re
from collections import OrderedDict, defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ATTR_ROOT = ROOT / "forge/src/main/resources/data/tiered/item_attributes"
TOOLTIP_FILE = ROOT / "forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json"
LANG_FILE = ROOT / "forge/src/main/resources/assets/tiered/lang/en_us.json"
REPORT_FILE = ROOT / "build/standalone-reforge-id-map.json"
ALLOWED_NS = {"minecraft", "tiered", "attributeslib"}
FORBIDDEN_TIERED = {"generic.summon_health", "generic.ars_spell_power"}
ID_RE = re.compile(r"^(common|uncommon|rare|epic|legendary|mythic)_(armor|tool|melee|ranged|fishing|shield|elytra|staffwand)_(\d+)$")
REFORGE_RE = re.compile(r"^tiered:(common|uncommon|rare|epic|legendary|mythic)_(armor|tool|melee|ranged|fishing|shield|elytra|staffwand)_(\d+)$")
SPECIAL = {"tiered:limestone_chunk", "tiered:pyrite_chunk", "tiered:galena_chunk", "tiered:charoite", "tiered:crown_topaz", "tiered:painite", "tiered:perfect_border", "tiered:perfect", "tiered:apex_border", "tiered:perfect_star_border"}

def load(path):
    return json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=OrderedDict)

def save(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=4, ensure_ascii=False) + "\n", encoding="utf-8")

def attr_ok(attr):
    raw = attr.get("type", "")
    ns, item = raw.split(":", 1) if ":" in raw else ("minecraft", raw)
    return ns in ALLOWED_NS and not (ns == "tiered" and item in FORBIDDEN_TIERED)

def replace_strings(value, id_map):
    if isinstance(value, str):
        return id_map.get(value, value)
    if isinstance(value, list):
        return [replace_strings(v, id_map) for v in value]
    if isinstance(value, dict):
        return OrderedDict((k, replace_strings(v, id_map)) for k, v in value.items())
    return value

def main():
    candidates, removed, rewritten = [], set(), set()
    for file in sorted(ATTR_ROOT.rglob("*.json")):
        data = load(file)
        old_id = data["id"]
        path = old_id.split(":", 1)[1]
        match = ID_RE.match(path)
        original = list(data.get("attributes", []))
        kept = [a for a in original if attr_ok(a)]
        if not match or not kept:
            removed.add(old_id)
            continue
        if len(kept) != len(original):
            rewritten.add(old_id)
        data["attributes"] = kept
        candidates.append({"file": file, "data": data, "old": old_id, "tier": match.group(1), "kind": match.group(2), "number": int(match.group(3))})

    grouped = defaultdict(list)
    for entry in candidates:
        grouped[(entry["file"].parent, entry["tier"], entry["kind"])].append(entry)
    id_map, kept_entries = OrderedDict(), []
    for key in sorted(grouped.keys(), key=lambda k: (str(k[0]), k[1], k[2])):
        for new_num, entry in enumerate(sorted(grouped[key], key=lambda e: e["number"]), start=1):
            new_path = f"{entry['tier']}_{entry['kind']}_{new_num}"
            entry["new"] = f"tiered:{new_path}"
            entry["new_file"] = entry["file"].parent / f"{new_path}.json"
            id_map[entry["old"]] = entry["new"]
            kept_entries.append(entry)

    for entry in candidates:
        entry["file"].unlink()
    for entry in kept_entries:
        data = replace_strings(entry["data"], id_map)
        data["id"] = entry["new"]
        for attr in data.get("attributes", []):
            modifier = attr.get("modifier")
            if isinstance(modifier, dict) and "name" in modifier:
                modifier["name"] = entry["new"]
        save(entry["new_file"], data)

    kept_ids = set(id_map.values())
    tooltip = load(TOOLTIP_FILE)
    for tooltip_entry in tooltip.get("tooltips", []):
        out, seen = [], set()
        for raw in tooltip_entry.get("decider", []):
            mapped = id_map.get(raw, raw)
            keep = mapped in kept_ids or mapped in SPECIAL or not REFORGE_RE.match(mapped)
            if keep and mapped not in seen:
                out.append(mapped)
                seen.add(mapped)
        tooltip_entry["decider"] = out
    save(TOOLTIP_FILE, tooltip)

    lang = load(LANG_FILE)
    new_lang = OrderedDict()
    for key, value in lang.items():
        if key.startswith("tiered:") and key.endswith(".label") and REFORGE_RE.match(key[:-6]):
            mapped = id_map.get(key[:-6])
            if mapped:
                new_lang[f"{mapped}.label"] = value
            continue
        if key.startswith("tooltip.tiered.apex_effect.mythic_armor_"):
            parts = key.split(".")
            mapped = id_map.get("tiered:" + parts[4])
            if mapped:
                parts[4] = mapped.split(":", 1)[1]
                new_lang[".".join(parts)] = value
            continue
        new_lang[key] = value
    save(LANG_FILE, new_lang)
    REPORT_FILE.parent.mkdir(parents=True, exist_ok=True)
    REPORT_FILE.write_text(json.dumps({"kept": len(kept_entries), "removed": sorted(removed), "rewritten": sorted(rewritten), "id_map": id_map}, indent=2) + "\n", encoding="utf-8")
    print(f"kept={len(kept_entries)} removed={len(removed)} rewritten={len(rewritten)}")
    print(f"report={REPORT_FILE}")

if __name__ == "__main__":
    main()
```

- [ ] **Step 2: Run the prune tool and inspect mythic armor mapping**

Run:

```powershell
python tools/standalone_reforge_prune.py
Get-Content build/standalone-reforge-id-map.json | Select-String -Pattern 'mythic_armor' -Context 0,0
```

Expected current mapping:

```text
tiered:mythic_armor_1 -> tiered:mythic_armor_1
tiered:mythic_armor_2 -> tiered:mythic_armor_2
tiered:mythic_armor_3 -> tiered:mythic_armor_3
tiered:mythic_armor_11 -> tiered:mythic_armor_4
tiered:mythic_armor_13 -> tiered:mythic_armor_5
tiered:mythic_armor_14 -> tiered:mythic_armor_6
tiered:mythic_armor_15 -> tiered:mythic_armor_7
```

- [ ] **Step 3: Run data policy tests**

Run:

```powershell
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.itemAttributeFilesUseStandaloneAttributesAndContiguousIds --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.tooltipBorderDecidersReferenceExistingStandaloneIds
```

Expected: both tests pass.

- [ ] **Step 4: Commit pruned data**

Run:

```powershell
git add tools/standalone_reforge_prune.py forge/src/main/resources/data/tiered/item_attributes forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json forge/src/main/resources/assets/tiered/lang/en_us.json
git add -u forge/src/main/resources/data/tiered/item_attributes
git commit -m "data: prune and renumber standalone reforges"
```

---

### Task 4: Replace Pack-Only Apex Effects

**Files:**
- Modify: `forge/src/main/java/elocindev/tierify/TierifyForge.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/apex/ApexEffectsBootstrap.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/apex/ApexActiveEffects.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/registry/ForgeMobEffectRegistry.java`
- Delete: `forge/src/main/java/elocindev/tierify/forge/effect/ApexSpellSurgeCooldownEffect.java`
- Delete: `forge/src/main/java/elocindev/tierify/forge/effect/ApexRollCounterEffect.java`
- Modify: `forge/src/main/resources/assets/tiered/lang/en_us.json`

- [ ] **Step 1: Remove optional Apex compat initialization**

In `TierifyForge.java`, delete:

```java
ApexActiveEffects.initSpellDamageCompat();
ApexActiveEffects.initArsSpellCompat();
ApexActiveEffects.initCombatRollCompat();
```

- [ ] **Step 2: Register standalone Apex mapping**

In `ApexEffectsBootstrap.init()`, use this mapping:

```text
mythic_armor_1: active armor boost, 6s duration, 30s cooldown
mythic_armor_2: passive absorption after 12s without damage
mythic_armor_3: passive unbreakable armor
mythic_armor_4: tooltip-only passive armor focus
mythic_armor_5: active slow-time aura, 8s duration, 30s cooldown
mythic_armor_6: passive unbreakable armor
mythic_armor_7: passive ranged momentum using AttributesLib arrow attributes
```

Use this code shape for new passive registrations:

```java
ApexEffectRegistry.register(
        ResourceLocation.fromNamespaceAndPath(TierifyCommon.MODID, "mythic_armor_3"),
        new ApexEffect(
                ApexEffect.ApexTriggerType.PASSIVE_TICK,
                ApexEffect.ApexCounterModel.EVERY_N,
                0,
                ctx -> ApexActiveEffects.setUnbreakableArmorActive(ctx.player(), true)
        )
);
```

- [ ] **Step 3: Shrink Apex active behavior**

In `ApexActiveEffects.java`, remove fields and methods used only by spell schools, Ars, summon empowerment, or Combat Roll. Keep public methods for matching Apex armor, armor boost, slow time, no-damage shield, hurt tracking, passive ticking, unbreakable armor, slow-time checks, and ranged momentum projectile events.

Update constants:

```java
NO_DAMAGE_REFORGE = tiered:mythic_armor_2
SLOW_TIME_REFORGE = tiered:mythic_armor_5
UNBREAKABLE_REFORGE = tiered:mythic_armor_6
RANGED_MOMENTUM_REFORGE = tiered:mythic_armor_7
```

- [ ] **Step 4: Remove gameplay calls into deleted hooks**

In `ForgeGameplayEventSubscriber.java`, delete calls to:

```java
ApexActiveEffects.tryEmpowerSummon
ApexActiveEffects.applyRollCounterDamageBonus
ApexActiveEffects.getRollDamageTakenMultiplier
```

Keep calls to `markPlayerHurt`, `shouldApplySlowTime`, `tick`, and ranged momentum methods.

- [ ] **Step 5: Remove obsolete mob effects**

In `ForgeMobEffectRegistry.java`, remove registrations/imports for `APEX_SPELL_SURGE_COOLDOWN`, `APEX_ROLL_COUNTER`, `ApexSpellSurgeCooldownEffect`, and `ApexRollCounterEffect`. Delete the two effect classes.

- [ ] **Step 6: Update Apex language**

Ensure `en_us.json` contains standalone Apex effect lines for mythic armor ids 1-7:

```json
"tooltip.tiered.apex_effect.mythic_armor_1.line1": "Active Ability: +50% increased armor for 6s",
"tooltip.tiered.apex_effect.mythic_armor_1.line2": "Cooldown 30s",
"tooltip.tiered.apex_effect.mythic_armor_2.line1": "If you avoid damage for 12s, gain absorption equal to 25% max health",
"tooltip.tiered.apex_effect.mythic_armor_2.line2": "Passive",
"tooltip.tiered.apex_effect.mythic_armor_3.line1": "Apex armor does not lose durability while worn",
"tooltip.tiered.apex_effect.mythic_armor_3.line2": "Passive",
"tooltip.tiered.apex_effect.mythic_armor_4.line1": "Apex plating strengthens this armor-focused roll",
"tooltip.tiered.apex_effect.mythic_armor_4.line2": "Passive",
"tooltip.tiered.apex_effect.mythic_armor_5.line1": "Slow time around you in a 16-block radius for 8s",
"tooltip.tiered.apex_effect.mythic_armor_5.line2": "Active Ability: Cooldown 30s",
"tooltip.tiered.apex_effect.mythic_armor_6.line1": "Apex armor is unbreakable while worn",
"tooltip.tiered.apex_effect.mythic_armor_6.line2": "Passive",
"tooltip.tiered.apex_effect.mythic_armor_7.line1": "Consecutive projectile hits build Momentum (+1 on hit, +2 on kill, max 20)",
"tooltip.tiered.apex_effect.mythic_armor_7.line2": "Passive"
```

- [ ] **Step 7: Verify and commit**

Run:

```powershell
rg -n "ironsspellbooks|arsnouveau|combatroll|traveloptics|familiarslib|generic\.ars_spell_power|generic\.summon_health|spell_surge|roll_counter" forge/src/main/java/elocindev/tierify/forge/apex forge/src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java forge/src/main/java/elocindev/tierify/TierifyForge.java forge/src/main/resources/assets/tiered/lang/en_us.json -S
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.apexCodeUsesOnlyStandaloneReferencesAndExistingMythicArmorIds
git add forge/src/main/java/elocindev/tierify/TierifyForge.java forge/src/main/java/elocindev/tierify/forge/apex forge/src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java forge/src/main/java/elocindev/tierify/forge/registry/ForgeMobEffectRegistry.java forge/src/main/resources/assets/tiered/lang/en_us.json
git add -u forge/src/main/java/elocindev/tierify/forge/effect
git commit -m "refactor: replace pack-only apex effects"
```

Expected: scan has no matches, test passes, commit succeeds.

---

### Task 5: Remove Pack-Only Echelon Attributes

**Files:**
- Modify: `forge/src/main/java/elocindev/tierify/forge/registry/ForgeAttributeRegistry.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/event/ForgeAttributeSubscriber.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/ForgeTieredAttributeSubscriber.java`
- Modify: `forge/src/main/java/elocindev/tierify/forge/mixin/client/ItemStackClientMixin.java`
- Modify: `forge/src/main/resources/assets/tiered/lang/en_us.json`

- [ ] **Step 1: Delete removed attributes from registry**

In `ForgeAttributeRegistry.java`, delete the `SUMMON_HEALTH` and `ARS_SPELL_POWER` registry objects.

- [ ] **Step 2: Stop adding removed attributes to players**

In `ForgeAttributeSubscriber.java`, delete:

```java
event.add(EntityType.PLAYER, ForgeAttributeRegistry.SUMMON_HEALTH.get());
event.add(EntityType.PLAYER, ForgeAttributeRegistry.ARS_SPELL_POWER.get());
```

- [ ] **Step 3: Remove Ars special display handling**

In `ForgeTieredAttributeSubscriber.java`, delete the `ARS_SPELL_POWER_ID` constant and the branch that formats it as a percentage.

- [ ] **Step 4: Remove client tooltip percentage handling**

In `ItemStackClientMixin.java`, remove:

```java
ResourceLocation.fromNamespaceAndPath("tiered", "generic.ars_spell_power")
```

- [ ] **Step 5: Remove language keys**

Delete `generic.summon_health` and `generic.ars_spell_power` from `en_us.json`.

- [ ] **Step 6: Verify and commit**

Run:

```powershell
rg -n "SUMMON_HEALTH|ARS_SPELL_POWER|generic\.summon_health|generic\.ars_spell_power" forge/src/main/java forge/src/main/resources -S
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.itemAttributeFilesUseStandaloneAttributesAndContiguousIds
git add forge/src/main/java/elocindev/tierify/forge/registry/ForgeAttributeRegistry.java forge/src/main/java/elocindev/tierify/forge/event/ForgeAttributeSubscriber.java forge/src/main/java/elocindev/tierify/forge/ForgeTieredAttributeSubscriber.java forge/src/main/java/elocindev/tierify/forge/mixin/client/ItemStackClientMixin.java forge/src/main/resources/assets/tiered/lang/en_us.json
git commit -m "refactor: remove pack-only Echelon attributes"
```

Expected: scan has no matches, test passes, commit succeeds.

---

### Task 6: Replace Pack-Only Recipes And Defaults

**Files:**
- Delete: `forge/src/main/resources/data/tiered/recipes/limestone_to_create_limestone.json`
- Rename/Modify: `forge/src/main/resources/data/tiered/recipes/charoite_modded.json` to `forge/src/main/resources/data/tiered/recipes/charoite.json`
- Rename/Modify: `forge/src/main/resources/data/tiered/recipes/crown_topaz_modded.json` to `forge/src/main/resources/data/tiered/recipes/crown_topaz.json`
- Rename/Modify: `forge/src/main/resources/data/tiered/recipes/painite_modded.json` to `forge/src/main/resources/data/tiered/recipes/painite.json`
- Create: `forge/src/main/resources/data/tiered/recipes/stardust.json`
- Create: `forge/src/main/resources/data/tiered/recipes/apex_crux.json`
- Modify: `forge/src/main/resources/echelon-defaults/echelon-common.toml`
- Modify: `forge/src/main/resources/echelon-defaults/echelon-treasure-bag-profiles.txt`

- [ ] **Step 1: Replace Charoite recipe**

Rename `charoite_modded.json` to `charoite.json`:

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [" Q ", "ABA", " Q "],
  "key": {
    "A": { "item": "minecraft:amethyst_shard" },
    "B": { "item": "minecraft:blaze_powder" },
    "Q": { "item": "minecraft:quartz" }
  },
  "result": { "item": "tiered:charoite", "count": 2 }
}
```

- [ ] **Step 2: Replace Crown Topaz recipe**

Rename `crown_topaz_modded.json` to `crown_topaz.json`:

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [" E ", "GPG", " B "],
  "key": {
    "B": { "item": "minecraft:blaze_rod" },
    "E": { "item": "minecraft:ender_eye" },
    "G": { "item": "minecraft:gold_ingot" },
    "P": { "item": "tiered:pyrite_chunk" }
  },
  "result": { "item": "tiered:crown_topaz", "count": 2 }
}
```

- [ ] **Step 3: Replace Painite recipe**

Rename `painite_modded.json` to `painite.json`:

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [" D ", "NCE", " G "],
  "key": {
    "C": { "item": "tiered:crown_topaz" },
    "D": { "item": "minecraft:dragon_breath" },
    "E": { "item": "minecraft:echo_shard" },
    "G": { "item": "tiered:galena_chunk" },
    "N": { "item": "minecraft:netherite_scrap" }
  },
  "result": { "item": "tiered:painite", "count": 2 }
}
```

- [ ] **Step 4: Add Stardust recipe**

Create `stardust.json`:

```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    { "item": "minecraft:amethyst_shard" },
    { "item": "minecraft:glowstone_dust" },
    { "item": "minecraft:ender_pearl" }
  ],
  "result": { "item": "tiered:stardust", "count": 2 }
}
```

- [ ] **Step 5: Add Apex Crux recipe**

Create `apex_crux.json`:

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": ["DSD", "EPE", "DND"],
  "key": {
    "D": { "item": "minecraft:dragon_breath" },
    "E": { "item": "minecraft:echo_shard" },
    "N": { "item": "minecraft:nether_star" },
    "P": { "item": "tiered:painite" },
    "S": { "item": "tiered:stellar_core" }
  },
  "result": { "item": "tiered:apex_crux", "count": 1 }
}
```

- [ ] **Step 6: Delete Create limestone conversion**

Delete `forge/src/main/resources/data/tiered/recipes/limestone_to_create_limestone.json`.

- [ ] **Step 7: Disable treasure bag defaults**

In `echelon-common.toml`, set `treasureBagDropModifier = false`. Replace `echelon-treasure-bag-profiles.txt` with comments describing opt-in profile syntax and no active entries.

- [ ] **Step 8: Verify and commit**

Run:

```powershell
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.recipesUseOnlyMinecraftAndTieredItemsAndCoverProgressionItems --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.defaultConfigsAreStandaloneSafe
git add forge/src/main/resources/data/tiered/recipes forge/src/main/resources/echelon-defaults/echelon-common.toml forge/src/main/resources/echelon-defaults/echelon-treasure-bag-profiles.txt
git add -u forge/src/main/resources/data/tiered/recipes
git commit -m "data: add standalone recipes and defaults"
```

Expected: targeted tests pass, commit succeeds.

---

### Task 7: Preserve Tooltip Independence

**Files:**
- Modify only if smoke tests expose a regression:
  - `forge/src/main/java/elocindev/tierify/forge/mixin/client/GuiGraphicsTooltipBorderMixin.java`
  - `forge/src/main/java/elocindev/tierify/forge/compat/TooltipOverhaulCompatForge.java`
  - `forge/src/main/java/elocindev/tierify/forge/mixin/compat/TooltipOverhaulWrapperMixin.java`
  - `forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json`

- [ ] **Step 1: Verify Tooltip Overhaul is absent from runtime dependencies**

Run:

```powershell
rg -n "runtimeOnly.*tooltipoverhaul|runtimeOnly fg.deobf\(\"curse.maven:" forge/build.gradle -S
.\gradlew.bat test --tests elocindev.tierify.forge.standalone.StandaloneResourcePolicyTest.gradleDoesNotLoadOptionalModsAtRuntimeForStandaloneDevRuns
```

Expected: scan has no matches, test passes.

- [ ] **Step 2: Confirm native tooltip code path is present**

Run:

```powershell
rg -n "TooltipOverhaulCompatForge\.isLoaded\(\)|TierifyTooltipBorderRendererForge\.render\(|event\.registerReloadListener|TooltipOverhaulCompatForge\.init\(\)" forge/src/main/java/elocindev/tierify/forge/mixin/client/GuiGraphicsTooltipBorderMixin.java forge/src/main/java/elocindev/tierify/forge/client/ForgeTooltipBorderReloadListener.java forge/src/main/java/elocindev/tierify/forge/screen/client/ReforgeScreen.java forge/src/main/java/elocindev/tierify/forge/mixin/client/ItemStackClientMixin.java forge/src/main/java/elocindev/tierify/forge/client/ForgeClientSetup.java -S
```

Expected: matches show the native loader, native renderer, reforge preview renderer, and Tooltip Overhaul guards.

- [ ] **Step 3: Run no-Tooltip-Overhaul client smoke test**

Run `.\gradlew.bat runClient`. In-game, check non-perfect borders, perfect borders/label, Apex crown/star/perimeter/text visuals, and the reforge preview border.

- [ ] **Step 4: Run optional Tooltip Overhaul smoke test**

Place a local Tooltip Overhaul jar in `forge/run/mods` for this run only. Do not add it back to `forge/build.gradle`. Run `.\gradlew.bat runClient` and repeat Step 3 checks. Expected: guarded adapter renders without double-rendering.

- [ ] **Step 5: Commit only if tooltip fixes were required**

Run only when files changed:

```powershell
git add forge/src/main/java/elocindev/tierify/forge/mixin/client/GuiGraphicsTooltipBorderMixin.java forge/src/main/java/elocindev/tierify/forge/compat/TooltipOverhaulCompatForge.java forge/src/main/java/elocindev/tierify/forge/mixin/compat/TooltipOverhaulWrapperMixin.java forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json
git commit -m "fix: preserve standalone tooltip rendering"
```

---

### Task 8: Update README And Spec Notes

**Files:**
- Modify: `README.md`
- Modify: `docs/superpowers/specs/2026-04-13-standalone-apothic-design.md`

- [ ] **Step 1: Update README introduction**

Replace the opening paragraph with:

```markdown
**Echelon** is a standalone Forge 1.20.1 reforge and item-progression mod based on Tierify. It adds tiered reforges, perfect rolls, custom tooltip presentation, salvage progression, stars, Apex upgrades, and datapack-driven configuration.
```

- [ ] **Step 2: Update installation**

Replace the Installation section with:

```markdown
### Installation
Echelon is a Forge mod for Minecraft 1.20.1. It requires:

- Forge 47.4.10+
- Apothic Attributes / AttributesLib

Tooltip Overhaul, JEI, EMI, Obscure API, Curios, and modded item mappings are optional compatibility paths. The mod loads and renders its own tooltip borders without Tooltip Overhaul installed.
```

- [ ] **Step 3: Update reforge and attribute docs**

Replace the optional-dependency reforge count sentence with:

```markdown
The bundled standalone reforges use vanilla, Echelon, and Apothic Attributes by default. Optional datapacks or compatibility mappings can extend which items participate in reforging when other mods are installed.
```

Replace the `Types:` line with a list limited to vanilla, Echelon standalone attributes, and AttributesLib examples.

- [ ] **Step 4: Add spec note**

Append to the Docs section in `docs/superpowers/specs/2026-04-13-standalone-apothic-design.md`:

```markdown
The README should not describe Linggango as the only intended runtime target for this branch.
```

- [ ] **Step 5: Commit docs**

Run:

```powershell
git add README.md docs/superpowers/specs/2026-04-13-standalone-apothic-design.md
git commit -m "docs: describe standalone dependencies"
```

---

### Task 9: Final Verification And Build

**Files:**
- Read-only verification across the repository.

- [ ] **Step 1: Run full tests**

Run `.\gradlew.bat test`. Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 2: Run full build**

Run `.\gradlew.bat build`. Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Run final forbidden scans**

Run:

```powershell
rg -n "geckolib|GeckoLib|software\.bernie|infernal_sovereign|InfernalSovereign" forge/src/main/java forge/src/main/resources forge/build.gradle -S
rg -n "irons_spellbooks|ars_nouveau|spell_power|combatroll|brutality|reach-entity-attributes|traveloptics|familiarslib|gtbcs_geomancy_plus|armageddon_mod|terramity|endrem|biomancy|create:limestone" forge/src/main/resources/data/tiered/item_attributes forge/src/main/resources/data/tiered/recipes forge/src/main/resources/assets/tiered/tooltips forge/src/main/resources/assets/tiered/lang/en_us.json -S
rg -n "runtimeOnly fg.deobf\(\"curse.maven:|modId=\"geckolib\"|modId=\"tooltipoverhaul\"" forge/build.gradle forge/src/main/resources/META-INF/mods.toml -S
```

Expected: all scans exit with no matches.

- [ ] **Step 4: Review final state**

Run:

```powershell
git status --short --branch
git diff --stat
```

Expected: branch is `standalone-apothic`; remaining diffs are intentional.

- [ ] **Step 5: Commit final verification fixes if needed**

Run only if small fixes were required:

```powershell
git add forge README.md docs/superpowers/specs/2026-04-13-standalone-apothic-design.md tools/standalone_reforge_prune.py
git commit -m "chore: finalize standalone verification"
```

---

## Self-Review Checklist

- Spec coverage: Tasks cover dependency trimming, Tooltip Overhaul runtime independence, Geckolib and Infernal Sovereign deletion, reforge pruning and renumbering, Apex replacement, recipes, defaults, docs, tests, smoke tests, and full build verification.
- Placeholder scan: This plan contains no empty implementation steps.
- Type consistency: Package paths use the existing `elocindev.tierify` and `elocindev.tierify.forge` structure. The Apex id mapping follows the current prune result after removing pack-only Echelon attributes.
