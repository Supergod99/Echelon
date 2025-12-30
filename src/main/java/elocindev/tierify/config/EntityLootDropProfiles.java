package elocindev.tierify.config;

import elocindev.tierify.Tierify;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import draylar.tiered.api.ModifierUtils;

public final class EntityLootDropProfiles {
    private static final Logger LOGGER = LogManager.getLogger("Echelon-EntityLootDropProfiles");

    public record Entry(float chance, int[] weights) {}

    private static final Map<Identifier, Entry> EXACT = new HashMap<>();
    private static final Map<String, Entry> NAMESPACE_WILDCARD = new HashMap<>();
    private static Entry GLOBAL_WILDCARD = null;

    private EntityLootDropProfiles() {}

    public static void reload() {
        EXACT.clear();
        NAMESPACE_WILDCARD.clear();
        GLOBAL_WILDCARD = null;

        String fileName = Tierify.CONFIG.entityLootDropProfilesFile;
        if (fileName == null || fileName.isBlank()) fileName = "echelon-entity-drop-profiles.txt";

        Path path = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        ensureExists(path);

        final List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to read {}", path, e);
            return;
        }

        int loaded = 0;

        for (int idx = 0; idx < lines.size(); idx++) {
            String raw = lines.get(idx);
            String line = stripComments(raw).trim();
            if (line.isEmpty()) continue;

            int eq = line.indexOf('=');
            if (eq <= 0 || eq >= line.length() - 1) {
                LOGGER.warn("Invalid line {}: {}", idx + 1, raw);
                continue;
            }

            String left = line.substring(0, eq).trim();
            String right = line.substring(eq + 1).trim();

            String[] parts = right.split("\\|", 2);
            if (parts.length != 2) {
                LOGGER.warn("Invalid line {} (expected chance|weights): {}", idx + 1, raw);
                continue;
            }

            Float chance = parseChance(parts[0].trim());
            int[] weights = ModifierUtils.parseWeightProfile(parts[1].trim());
            if (chance == null || weights == null) {
                LOGGER.warn("Invalid line {} (bad chance or weights): {}", idx + 1, raw);
                continue;
            }

            Entry entry = new Entry(chance, weights);

            if (left.equals("*")) {
                GLOBAL_WILDCARD = entry;
                loaded++;
                continue;
            }

            if (left.endsWith(":*")) {
                String ns = left.substring(0, left.length() - 2);
                if (!ns.isEmpty()) {
                    NAMESPACE_WILDCARD.put(ns, entry);
                    loaded++;
                }
                continue;
            }

            try {
                EXACT.put(new Identifier(left), entry);
                loaded++;
            } catch (Exception e) {
                LOGGER.warn("Invalid entity id on line {}: {}", idx + 1, raw);
            }
        }

        LOGGER.info("Loaded {} entity loot-drop profiles from {}", loaded, path.getFileName());
    }

    public static Entry get(Identifier entityId) {
        Entry exact = EXACT.get(entityId);
        if (exact != null) return exact;

        Entry ns = NAMESPACE_WILDCARD.get(entityId.getNamespace());
        if (ns != null) return ns;

        return GLOBAL_WILDCARD;
    }

    private static void ensureExists(Path path) {
        if (Files.exists(path)) return;

        try {
            Files.createDirectories(path.getParent());
            String sample =
                    "# Echelon entity loot-drop profiles\n" +
                    "# Format: entity_id=chance|weights\n" +
                    "# weights: either 6 ints (Common..Mythic) OR a preset: overworld|nether|end|global\n" +
                    "# Wildcards:\n" +
                    "#   *=chance|weights         (global default)\n" +
                    "#   modid:*=chance|weights   (namespace default)\n" +
                    "\n" +
                    "# Example:\n" +
                    "# cataclysm:ignis=0.50|0,0,5,10,5,2\n";
            Files.writeString(path, sample, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to create sample file {}", path, e);
        }
    }

    private static String stripComments(String s) {
        int hash = s.indexOf('#');
        int slashes = s.indexOf("//");

        int cut = -1;
        if (hash >= 0) cut = hash;
        if (slashes >= 0) cut = (cut < 0) ? slashes : Math.min(cut, slashes);

        return (cut >= 0) ? s.substring(0, cut) : s;
    }

    private static Float parseChance(String s) {
        try {
            float f = Float.parseFloat(s);
            if (f < 0.0f) f = 0.0f;
            if (f > 1.0f) f = 1.0f;
            return f;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
