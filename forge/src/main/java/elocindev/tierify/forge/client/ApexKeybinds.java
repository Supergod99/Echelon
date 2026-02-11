package elocindev.tierify.forge.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class ApexKeybinds {

    public static final String CATEGORY = "key.categories.tiered";
    private static KeyMapping armorActive;
    private static KeyMapping mainhandActive;
    private static KeyMapping offhandActive;

    private ApexKeybinds() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        if (armorActive == null) {
            armorActive = new KeyMapping("key.tiered.apex_armor_active", GLFW.GLFW_KEY_K, CATEGORY);
        }
        if (mainhandActive == null) {
            mainhandActive = new KeyMapping("key.tiered.apex_mainhand_active", GLFW.GLFW_KEY_J, CATEGORY);
        }
        if (offhandActive == null) {
            offhandActive = new KeyMapping("key.tiered.apex_offhand_active", GLFW.GLFW_KEY_L, CATEGORY);
        }
        event.register(armorActive);
        event.register(mainhandActive);
        event.register(offhandActive);
    }

    public static boolean consumeArmorActive() {
        return armorActive != null && armorActive.consumeClick();
    }

    public static boolean consumeMainhandActive() {
        return mainhandActive != null && mainhandActive.consumeClick();
    }

    public static boolean consumeOffhandActive() {
        return offhandActive != null && offhandActive.consumeClick();
    }
}
