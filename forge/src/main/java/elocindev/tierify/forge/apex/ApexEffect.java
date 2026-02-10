package elocindev.tierify.forge.apex;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

public record ApexEffect(ApexTriggerType triggerType,
                         ApexCounterModel counterModel,
                         int counterValue,
                         ApexEffectHandler handler) {

    public ApexEffect {
        Objects.requireNonNull(triggerType, "triggerType");
        Objects.requireNonNull(counterModel, "counterModel");
        Objects.requireNonNull(handler, "handler");
    }

    public enum ApexTriggerType {
        ACTIVE_USE,
        ON_HIT,
        ON_HURT,
        ON_BLOCK_BREAK,
        PASSIVE_TICK,
        ON_BOW_USE,
        ON_SPELL_USE
    }

    public enum ApexCounterModel {
        EVERY_N,
        COOLDOWN_TICKS
    }

    @FunctionalInterface
    public interface ApexEffectHandler {
        void apply(ApexEffectContext ctx);
    }

    public record ApexEffectContext(ServerPlayer player,
                                    ItemStack stack,
                                    Level level,
                                    Entity target) {
    }
}
