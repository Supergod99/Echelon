package elocindev.tierify.forge.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class ApexKeybinds {

    public static final String CATEGORY = "key.categories.tiered";
    public static final KeyMapping ARMOR_ACTIVE = new KeyMapping(
            "key.tiered.apex_armor_active",
            GLFW.GLFW_KEY_K,
            CATEGORY
    );
    public static final KeyMapping MAINHAND_ACTIVE = new KeyMapping(
            "key.tiered.apex_mainhand_active",
            GLFW.GLFW_KEY_J,
            CATEGORY
    );
    public static final KeyMapping OFFHAND_ACTIVE = new KeyMapping(
            "key.tiered.apex_offhand_active",
            GLFW.GLFW_KEY_L,
            CATEGORY
    );

    private ApexKeybinds() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ARMOR_ACTIVE);
        event.register(MAINHAND_ACTIVE);
        event.register(OFFHAND_ACTIVE);
    }
}
