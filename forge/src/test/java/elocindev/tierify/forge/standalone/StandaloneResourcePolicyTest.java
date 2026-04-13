package elocindev.tierify.forge.standalone;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class StandaloneResourcePolicyTest {

    private static final Pattern REFORGE_ID_PATTERN = Pattern.compile(
            "^tiered:(common|uncommon|rare|epic|legendary|mythic)_(armor|tool|melee|ranged|fishing|shield|elytra)_(\\d+)$"
    );
    private static final Pattern MYTHIC_ARMOR_ID_PATTERN = Pattern.compile("mythic_armor_(\\d+)");
    private static final Set<String> RECIPE_REFERENCE_KEYS = Set.of(
            "item",
            "tag",
            "ingredient",
            "ingredients",
            "result",
            "base",
            "input",
            "output",
            "catalyst",
            "addition",
            "template",
            "contents"
    );
    private static final Set<String> STANDALONE_TOOLTIP_DECIDERS = Set.of(
            "tiered:limestone_chunk",
            "tiered:pyrite_chunk",
            "tiered:galena_chunk",
            "tiered:charoite",
            "tiered:crown_topaz",
            "tiered:painite",
            "tiered:perfect_border",
            "tiered:perfect",
            "tiered:apex_border",
            "tiered:perfect_star_border"
    );
    private static final Set<String> FORBIDDEN_POLICY_TOKENS = Set.of(
            "ironsspellbooks",
            "arsnouveau",
            "combatroll",
            "traveloptics",
            "familiarslib",
            "generic.ars_spell_power",
            "generic.summon_health",
            "spell_surge",
            "roll_counter"
    );
    private static final Set<String> FORBIDDEN_EXTERNAL_CONTENT_TOKENS = Set.of(
            "infernal_sovereign",
            "infernalsovereign",
            "software.bernie.geckolib",
            "geckolib.initialize",
            "infernalsovereignarmor"
    );
    private static final Set<String> FORBIDDEN_DOC_TOKENS = Set.of(
            "linggango",
            "pack-only",
            "pack only"
    );

    @Test
    void modsTomlRequiresOnlyForgeMinecraftAndAttributesLib() throws IOException {
        Path root = locateForgeProjectRoot();
        Path modsToml = resolveResource(root, "META-INF/mods.toml");
        Set<String> mandatoryDependencies = collectMandatoryDependencies(modsToml);
        assertEquals(Set.of("attributeslib", "forge", "minecraft"), new TreeSet<>(mandatoryDependencies));
    }

    @Test
    void gradleDoesNotLoadOptionalModsAtRuntimeForStandaloneDevRuns() throws IOException {
        Path root = locateForgeProjectRoot();
        Path buildGradle = resolveProjectFile(root, "build.gradle");
        List<String> lines = Files.readAllLines(buildGradle, StandardCharsets.UTF_8);

        for (String line : lines) {
            String trimmed = line.trim();
            assertFalse(trimmed.startsWith("runtimeOnly fg.deobf(\"curse.maven:"),
                    "Found runtimeOnly CurseMaven dependency: " + trimmed);
            assertFalse(trimmed.contains("geckolib-388172"),
                    "Found Geckolib dependency: " + trimmed);
            assertFalse(trimmed.startsWith("runtimeOnly") && trimmed.contains("tooltipoverhaul-1327508"),
                    "Tooltip Overhaul must not be runtimeOnly: " + trimmed);
        }
    }

    @Test
    void itemAttributeFilesUseStandaloneAttributesAndContiguousIds() throws IOException {
        Path root = locateForgeProjectRoot();
        Path itemAttributesDir = resolveResource(root, "data/tiered/item_attributes");
        List<Path> files;
        try (Stream<Path> stream = Files.walk(itemAttributesDir)) {
            files = stream.filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .collect(Collectors.toList());
        }

        Set<String> validNamespaces = Set.of("minecraft", "tiered", "attributeslib");
        Map<String, Set<Integer>> groupNumbers = new HashMap<>();
        Set<String> allIds = new TreeSet<>();

        for (Path file : files) {
            JsonObject rootJson = readObject(file);
            String id = getRequiredString(rootJson, "id", file);
            allIds.add(id);

            String stem = stripExtension(file.getFileName().toString());
            assertEquals(stem, id.substring(id.indexOf(':') + 1),
                    "Filename stem must match id path for " + file);

            Matcher matcher = REFORGE_ID_PATTERN.matcher(id);
            assertTrue(matcher.matches(), "Unexpected reforge id format: " + id + " in " + file);

            String namespace = id.substring(0, id.indexOf(':'));
            assertEquals("tiered", namespace, "Reforge ids should remain in the tiered namespace: " + id);

            JsonArray attributes = rootJson.getAsJsonArray("attributes");
            assertNotNull(attributes, "Missing attributes array in " + file);
            assertFalse(attributes.isEmpty(), "Attributes array must be non-empty: " + file);

            for (JsonElement attributeElement : attributes) {
                assertTrue(attributeElement.isJsonObject(), "Attribute entry must be an object in " + file);
                JsonObject attribute = attributeElement.getAsJsonObject();
                String type = getRequiredString(attribute, "type", file);
                String typeNamespace = type.contains(":") ? type.substring(0, type.indexOf(':')) : "minecraft";
                assertTrue(validNamespaces.contains(typeNamespace),
                        "Forbidden attribute namespace '" + typeNamespace + "' in " + file + " (" + type + ")");

                if ("tiered".equals(typeNamespace)) {
                    String typePath = type.substring(type.indexOf(':') + 1);
                    assertFalse(
                            "generic.summon_health".equals(typePath) || "generic.ars_spell_power".equals(typePath),
                            "Forbidden attribute type in " + file + ": " + type
                    );
                }

                if (attribute.has("modifier") && attribute.get("modifier").isJsonObject()) {
                    JsonObject modifier = attribute.getAsJsonObject("modifier");
                    if (modifier.has("name") && modifier.get("name").isJsonPrimitive()) {
                        assertEquals(id, modifier.get("name").getAsString(),
                                "Modifier name must match id for " + file);
                    }
                }
            }

            int number = Integer.parseInt(matcher.group(3));
            String groupKey = matcher.group(1) + ":" + matcher.group(2);
            groupNumbers.computeIfAbsent(groupKey, ignored -> new TreeSet<>()).add(number);

        }

        for (Map.Entry<String, Set<Integer>> entry : groupNumbers.entrySet()) {
            Set<Integer> numbers = entry.getValue();
            assertFalse(numbers.isEmpty(), "Empty numbering set for " + entry.getKey());
            int expected = 1;
            for (int actual : numbers) {
                assertEquals(expected, actual, "Numbering must be contiguous for " + entry.getKey());
                expected++;
            }
        }

        assertFalse(allIds.contains("tiered:generic.summon_health"));
        assertFalse(allIds.contains("tiered:generic.ars_spell_power"));
    }

    @Test
    void tooltipBorderDecidersReferenceExistingStandaloneIds() throws IOException {
        Path root = locateForgeProjectRoot();
        Path tooltipBorders = resolveResource(root, "assets/tiered/tooltips/tooltip_borders.json");
        Path itemAttributesDir = resolveResource(root, "data/tiered/item_attributes");
        Set<String> validIds = collectReforgeIds(itemAttributesDir);
        validIds.addAll(STANDALONE_TOOLTIP_DECIDERS);

        JsonObject rootJson = readObject(tooltipBorders);
        JsonArray tooltips = rootJson.getAsJsonArray("tooltips");
        assertNotNull(tooltips, "Missing tooltips array");

        for (JsonElement tooltipElement : tooltips) {
            assertTrue(tooltipElement.isJsonObject(), "Tooltip entry must be an object");
            JsonObject tooltip = tooltipElement.getAsJsonObject();
            JsonArray deciders = tooltip.getAsJsonArray("decider");
            assertNotNull(deciders, "Missing decider array in tooltip entry");

            Set<String> seen = new HashSet<>();
            for (JsonElement deciderElement : deciders) {
                String decider = deciderElement.getAsString();
                assertTrue(seen.add(decider), "Duplicate decider in tooltip entry: " + decider);
                assertTrue(validIds.contains(decider), "Invalid tooltip decider: " + decider);
            }
        }
    }

    @Test
    void recipesUseOnlyMinecraftAndTieredItemsAndCoverProgressionItems() throws IOException {
        Path root = locateForgeProjectRoot();
        Path recipesDir = resolveResource(root, "data/tiered/recipes");
        List<Path> files;
        try (Stream<Path> stream = Files.walk(recipesDir)) {
            files = stream.filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .collect(Collectors.toList());
        }

        Set<String> results = new HashSet<>();
        for (Path file : files) {
            JsonObject rootJson = readObject(file);
            collectRecipeReferences(rootJson, file);
            collectRecipeResults(rootJson, results, file);
        }

        assertTrue(results.containsAll(Set.of(
                "tiered:limestone_chunk",
                "tiered:pyrite_chunk",
                "tiered:galena_chunk",
                "tiered:charoite",
                "tiered:crown_topaz",
                "tiered:painite",
                "tiered:stardust",
                "tiered:stellar_core",
                "tiered:apex_crux",
                "tiered:cleansing_stone"
        )), "Recipe results do not cover the full progression set: " + results);
    }

    @Test
    void defaultConfigsAreStandaloneSafe() throws IOException {
        Path root = locateForgeProjectRoot();
        Path common = resolveResource(root, "echelon-defaults/echelon-common.toml");
        Path treasureBagProfiles = resolveResource(root, "echelon-defaults/echelon-treasure-bag-profiles.txt");

        String commonText = Files.readString(common, StandardCharsets.UTF_8);
        assertTrue(commonText.contains("treasureBagDropModifier = false"),
                "Common defaults must disable treasure bag drops.");

        List<String> lines = Files.readAllLines(treasureBagProfiles, StandardCharsets.UTF_8);
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                assertTrue(trimmed.startsWith("#"),
                        "Treasure bag profile defaults must be commented out: " + trimmed);
            }
        }
    }

    @Test
    void geckolibAndInfernalSovereignContentAreRemoved() throws IOException {
        Path root = locateForgeProjectRoot();
        Path resourcesRoot = resolveProjectFile(root, "src/main/resources");
        try (Stream<Path> stream = Files.walk(resourcesRoot)) {
            for (Path file : stream.filter(Files::isRegularFile).collect(Collectors.toList())) {
                String pathLower = file.toString().toLowerCase(Locale.ROOT);
                for (String token : FORBIDDEN_EXTERNAL_CONTENT_TOKENS) {
                    assertFalse(pathLower.contains(token),
                            "Forbidden content remains in resource path " + file + ": " + token);
                }

                if (!isTextishFile(file)) {
                    continue;
                }

                String textLower = Files.readString(file, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                for (String token : FORBIDDEN_EXTERNAL_CONTENT_TOKENS) {
                    assertFalse(textLower.contains(token),
                            "Forbidden content remains in " + file + ": " + token);
                }
            }
        }
    }

    @Test
    void documentationReferencesAreStandaloneSafe() throws IOException {
        Path root = locateRepositoryRoot();
        for (Path file : collectDocumentationFiles(root)) {
            String pathLower = file.toString().toLowerCase(Locale.ROOT);
            String textLower = Files.readString(file, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
            for (String token : FORBIDDEN_DOC_TOKENS) {
                assertFalse(pathLower.contains(token) || textLower.contains(token),
                        "Standalone documentation must not use forbidden wording in " + file + ": " + token);
            }
        }
    }

    @Test
    void apexCodeUsesOnlyStandaloneReferencesAndExistingMythicArmorIds() throws IOException {
        Path root = locateForgeProjectRoot();
        Path itemAttributesDir = resolveResource(root, "data/tiered/item_attributes");
        Set<String> validIds = collectReforgeIds(itemAttributesDir);

        List<Path> files = List.of(
                resolveProjectFile(root, "src/main/java/elocindev/tierify/forge/apex/ApexEffectsBootstrap.java"),
                resolveProjectFile(root, "src/main/java/elocindev/tierify/forge/apex/ApexActiveEffects.java"),
                resolveProjectFile(root, "src/main/java/elocindev/tierify/forge/event/ForgeGameplayEventSubscriber.java"),
                resolveProjectFile(root, "src/main/java/elocindev/tierify/TierifyForge.java")
        );

        for (Path file : files) {
            String textLower = Files.readString(file, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
            for (String token : FORBIDDEN_POLICY_TOKENS) {
                assertFalse(textLower.contains(token),
                        "Forbidden token remains in " + file + ": " + token);
            }

            Matcher matcher = MYTHIC_ARMOR_ID_PATTERN.matcher(textLower);
            while (matcher.find()) {
                String id = "tiered:mythic_armor_" + matcher.group(1);
                assertTrue(validIds.contains(id), "Unknown mythic armor reforge id in " + file + ": " + id);
            }
        }
    }

    private static Path locateForgeProjectRoot() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        if (Files.isDirectory(cwd.resolve("src/main/resources"))) {
            return cwd;
        }
        if (Files.isDirectory(cwd.resolve("forge/src/main/resources"))) {
            return cwd.resolve("forge");
        }
        if (Files.isDirectory(cwd.resolve("forge").resolve("src/main/resources"))) {
            return cwd.resolve("forge");
        }
        fail("Could not locate Forge project root from " + cwd);
        return cwd;
    }

    private static Path locateRepositoryRoot() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        if (Files.exists(cwd.resolve("README.md")) || Files.isDirectory(cwd.resolve("docs"))) {
            return cwd;
        }

        Path parent = cwd.getParent();
        if (parent != null && (Files.exists(parent.resolve("README.md")) || Files.isDirectory(parent.resolve("docs")))) {
            return parent;
        }

        fail("Could not locate repository root from " + cwd);
        return cwd;
    }

    private static Path resolveProjectFile(Path root, String relativePath) {
        Path direct = root.resolve(relativePath);
        if (Files.exists(direct)) {
            return direct;
        }
        Path nested = root.resolve("forge").resolve(relativePath);
        if (Files.exists(nested)) {
            return nested;
        }
        fail("Missing file: " + relativePath + " from root " + root);
        return direct;
    }

    private static Path resolveResource(Path root, String relativePath) {
        Path direct = root.resolve("src/main/resources").resolve(relativePath);
        if (Files.exists(direct)) {
            return direct;
        }
        Path nested = root.resolve("forge/src/main/resources").resolve(relativePath);
        if (Files.exists(nested)) {
            return nested;
        }
        fail("Missing resource: " + relativePath + " from root " + root);
        return direct;
    }

    private static JsonObject readObject(Path file) throws IOException {
        String text = Files.readString(file, StandardCharsets.UTF_8);
        JsonElement element = JsonParser.parseString(text);
        assertTrue(element.isJsonObject(), "Expected JSON object in " + file);
        return element.getAsJsonObject();
    }

    private static String getRequiredString(JsonObject object, String key, Path file) {
        assertTrue(object.has(key), "Missing key '" + key + "' in " + file);
        assertTrue(object.get(key).isJsonPrimitive(), "Key '" + key + "' must be a primitive in " + file);
        return object.get(key).getAsString();
    }

    private static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(0, dot) : filename;
    }

    private static String unquote(String value) {
        String trimmed = value.trim();
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static List<Path> collectDocumentationFiles(Path root) throws IOException {
        List<Path> files = new ArrayList<>();

        try (Stream<Path> stream = Files.list(root)) {
            files.addAll(stream.filter(Files::isRegularFile)
                    .filter(StandaloneResourcePolicyTest::isDocumentationFile)
                    .collect(Collectors.toList()));
        }

        Path docsDir = root.resolve("docs");
        if (Files.isDirectory(docsDir)) {
            try (Stream<Path> stream = Files.walk(docsDir)) {
                files.addAll(stream.filter(Files::isRegularFile)
                        .filter(StandaloneResourcePolicyTest::isDocumentationFile)
                        .filter(path -> !path.toString().toLowerCase(Locale.ROOT).contains("docs\\superpowers\\")
                                && !path.toString().toLowerCase(Locale.ROOT).contains("docs/superpowers/"))
                        .collect(Collectors.toList()));
            }
        }

        return files;
    }

    private static boolean isDocumentationFile(Path path) {
        String lower = path.toString().toLowerCase(Locale.ROOT);
        if (lower.contains("\\build\\") || lower.contains("/build/")
                || lower.contains("\\generated\\") || lower.contains("/generated/")
                || lower.contains("\\test\\") || lower.contains("/test/")) {
            return false;
        }
        return lower.endsWith(".md") || lower.endsWith(".txt");
    }

    private static Set<String> collectMandatoryDependencies(Path modsToml) throws IOException {
        Set<String> mandatoryDependencies = new LinkedHashSet<>();
        List<String> lines = Files.readAllLines(modsToml, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!"[[dependencies.tiered]]".equals(line)) {
                continue;
            }

            String modId = null;
            boolean mandatory = false;
            int j = i + 1;
            while (j < lines.size()) {
                String next = lines.get(j).trim();
                if (next.startsWith("[[")) {
                    break;
                }
                if (next.startsWith("modId=")) {
                    modId = unquote(next.substring("modId=".length()).trim());
                } else if (next.startsWith("mandatory=")) {
                    mandatory = Boolean.parseBoolean(next.substring("mandatory=".length()).trim());
                }
                j++;
            }

            if (mandatory && modId != null && !modId.isBlank()) {
                mandatoryDependencies.add(modId);
            }
            i = j - 1;
        }
        return mandatoryDependencies;
    }

    private static Set<String> collectReforgeIds(Path itemAttributesDir) throws IOException {
        Set<String> ids = new TreeSet<>();
        try (Stream<Path> stream = Files.walk(itemAttributesDir)) {
            List<Path> files = stream.filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .collect(Collectors.toList());
            for (Path file : files) {
                JsonObject rootJson = readObject(file);
                ids.add(getRequiredString(rootJson, "id", file));
            }
        }
        return ids;
    }

    private static void collectRecipeReferences(JsonElement element, Path file) {
        collectRecipeReferences(element, file, new ArrayDeque<>());
    }

    private static void collectRecipeReferences(JsonElement element, Path file, Deque<String> path) {
        if (element == null || element.isJsonNull()) {
            return;
        }

        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                path.addLast(entry.getKey());
                collectRecipeReferences(entry.getValue(), file, path);
                path.removeLast();
            }
            return;
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                path.addLast("[" + i + "]");
                collectRecipeReferences(array.get(i), file, path);
                path.removeLast();
            }
            return;
        }

        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return;
        }

        String value = element.getAsString();
        if (!value.contains(":") || path.stream().noneMatch(RECIPE_REFERENCE_KEYS::contains)) {
            return;
        }

        String namespace = value.substring(0, value.indexOf(':'));
        assertTrue(namespace.equals("minecraft") || namespace.equals("tiered"),
                "Forbidden recipe item namespace '" + namespace + "' in " + file + " at " + String.join(".", path) + " -> " + value);
    }

    private static void assertNamespaceAllowed(String itemId, Path file, String path) {
        if (itemId == null || itemId.isBlank() || !itemId.contains(":")) {
            return;
        }
        String namespace = itemId.substring(0, itemId.indexOf(':'));
        assertTrue(namespace.equals("minecraft") || namespace.equals("tiered"),
                "Forbidden recipe item namespace '" + namespace + "' in " + file + " at " + path + " -> " + itemId);
    }

    private static void collectRecipeResults(JsonObject rootJson, Set<String> results, Path file) {
        if (rootJson.has("result")) {
            JsonElement result = rootJson.get("result");
            if (result.isJsonPrimitive()) {
                String resultId = result.getAsString();
                results.add(resultId);
                assertNamespaceAllowed(resultId, file, "result");
            } else if (result.isJsonObject()) {
                JsonObject resultObject = result.getAsJsonObject();
                if (resultObject.has("item") && resultObject.get("item").isJsonPrimitive()) {
                    String resultId = resultObject.get("item").getAsString();
                    results.add(resultId);
                    assertNamespaceAllowed(resultId, file, "result.item");
                }
            }
        }
    }

    private static boolean isTextishFile(Path path) {
        String lower = path.toString().toLowerCase(Locale.ROOT);
        return lower.endsWith(".java")
                || lower.endsWith(".json")
                || lower.endsWith(".txt")
                || lower.endsWith(".toml")
                || lower.endsWith(".md")
                || lower.endsWith(".mcmeta")
                || lower.endsWith(".gpl");
    }
}
