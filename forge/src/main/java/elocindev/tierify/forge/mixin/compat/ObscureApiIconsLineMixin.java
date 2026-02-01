package elocindev.tierify.forge.mixin.compat;

import com.obscuria.obscureapi.client.TooltipBuilder;
import elocindev.tierify.TierifyConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = TooltipBuilder.Modules.class, remap = false)
public class ObscureApiIconsLineMixin {

    @Unique
    private static String[] TIERIFY_ICON_MARKERS;

    @Inject(method = "buildIcons", at = @At("TAIL"), remap = false)
    private static void tierify$ensureTieredIcons(ItemStack stack, List<Component> list, CallbackInfo ci) {
        if (stack == null || list == null || stack.isEmpty()) return;
        if (!hasTierTag(stack)) return;
        if (!equipmentIconsEnabled()) return;
        if (containsObscureIcons(list)) return;

        invokePutIcons(list, stack);
    }

    @Unique
    private static boolean hasTierTag(ItemStack stack) {
        CompoundTag tiered = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (tiered == null) return false;
        String tierId = tiered.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
        return (tierId != null && !tierId.isEmpty()) || tiered.getBoolean("Perfect");
    }

    @Unique
    private static boolean equipmentIconsEnabled() {
        try {
            Class<?> clientCfg = Class.forName("com.obscuria.obscureapi.ObscureAPIConfig$Client");
            Field field = clientCfg.getField("equipmentIcons");
            Object value = field.get(null);
            Method get = value.getClass().getMethod("get");
            Object out = get.invoke(value);
            return out instanceof Boolean b && b;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Unique
    private static boolean containsObscureIcons(List<Component> list) {
        ensureIconMarkers();
        if (TIERIFY_ICON_MARKERS == null || TIERIFY_ICON_MARKERS.length == 0) {
            return true;
        }

        for (Component component : list) {
            if (component == null) continue;
            String text = component.getString();
            if (text == null || text.isEmpty()) continue;
            for (String marker : TIERIFY_ICON_MARKERS) {
                if (marker == null || marker.isEmpty()) continue;
                if (text.contains(marker)) return true;
            }
        }

        return false;
    }

    @Unique
    private static void ensureIconMarkers() {
        if (TIERIFY_ICON_MARKERS != null) return;

        List<String> markers = new ArrayList<>();
        addMarker(markers, "DURABILITY");
        addMarker(markers, "DAMAGE");
        addMarker(markers, "ARMOR");
        addMarker(markers, "ARMOR_TOUGHNESS");
        addMarker(markers, "KNOCKBACK_RESISTANCE");
        addMarker(markers, "ATTACK_SPEED_MEDIUM");
        addMarker(markers, "ATTACK_SPEED_FAST");
        addMarker(markers, "ATTACK_SPEED_SLOW");

        TIERIFY_ICON_MARKERS = markers.toArray(new String[0]);
    }

    @Unique
    private static void addMarker(List<String> markers, String enumName) {
        String icon = resolveIcon(enumName);
        if (icon == null || icon.isEmpty()) return;
        String cleaned = stripFormatting(icon);
        if (!cleaned.isEmpty()) {
            markers.add(cleaned);
        }
    }

    @Unique
    private static String resolveIcon(String enumName) {
        try {
            Class<?> icons = Class.forName("com.obscuria.obscureapi.api.utils.Icons");
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Object constant = Enum.valueOf((Class<? extends Enum>) icons.asSubclass(Enum.class), enumName);
            Method get = icons.getMethod("get");
            Object out = get.invoke(constant);
            return (out instanceof String s) ? s : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static void invokePutIcons(List<Component> list, ItemStack stack) {
        try {
            Class<?> iconsClass = Class.forName("com.obscuria.obscureapi.client.TooltipBuilder$AttributeIcons");
            Method m = iconsClass.getDeclaredMethod("putIcons", List.class, ItemStack.class);
            m.setAccessible(true);
            m.invoke(null, list, stack);
        } catch (Throwable ignored) {
        }
    }

    @Unique
    private static String stripFormatting(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\u00A7' && i + 1 < s.length()) {
                i++;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }
}
