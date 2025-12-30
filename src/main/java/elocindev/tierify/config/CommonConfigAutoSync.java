package elocindev.tierify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elocindev.tierify.Tierify;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Pattern;

public final class CommonConfigAutoSync {
    private CommonConfigAutoSync() {}

    public static void ensureCommonConfigHasAllKeys() {
        Path path;
        try {
            path = Paths.get(CommonConfig.getFile());
        } catch (Throwable t) {
            // If getFile() ever changes, fail closed
            return;
        }

        if (!Files.exists(path)) return;

        final String text;
        try {
            text = Files.readString(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return;
        }

        if (!isMissingAnyKeys(text)) return;
        // Try to use Necronomicon's writer if available (preserves its formatting/comments behavior best)
        if (tryInvokeNecronomiconSave(CommonConfig.class)) {
            Tierify.LOGGER.info("[Tierify] Updated echelon-common.json5 with newly added keys.");
            return;
        }
        // Fallback: write JSON (still valid JSON5). This may drop Necronomicon-style comments/formatting
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String out = gson.toJson(CommonConfig.INSTANCE);
            Files.writeString(path, out + "\n", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            Tierify.LOGGER.info("[Tierify] Updated echelon-common.json5 (fallback writer; comments may be lost).");
        } catch (Exception ignored) {}
    }

    private static boolean isMissingAnyKeys(String text) {
        for (Field f : CommonConfig.class.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod)) continue; // skip INSTANCE, etc
            String name = f.getName();
            // Match:  name:   OR  "name":
            Pattern p = Pattern.compile("(?m)^\\s*(?:\""+Pattern.quote(name)+"\"|"+Pattern.quote(name)+")\\s*:");
            if (!p.matcher(text).find()) return true;
        }
        return false;
    }

    private static boolean tryInvokeNecronomiconSave(Class<?> configClass) {
        try {
            Class<?> nec = Class.forName("elocindev.necronomicon.api.config.v1.NecConfigAPI");
            // Try common method names across versions
            for (String mName : new String[] {"saveConfig", "writeConfig", "save"}) {
                for (Method m : nec.getMethods()) {
                    if (!m.getName().equals(mName)) continue;
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == Class.class) {
                        m.invoke(null, configClass);
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {}
        return false;
    }
}
