package elocindev.tierify.mixin.compat;

import elocindev.tierify.compat.ArmageddonTreasureBagHooks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
        targets = {
                "net.mcreator.armageddonmod.procedures.ArionTreasurebagRightclickedProcedure",
                "net.mcreator.armageddonmod.procedures.IronColossusTreasureBagRightclickedProcedure"
        },
        remap = false
)
public abstract class ArmageddonTreasureBagProceduresMixin {

    @Unique
    private static final ThreadLocal<Entity> ECHELON$BAG_USER = new ThreadLocal<>();

    @Inject(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            require = 0
    )
    private static void echelon$captureBagUser(
            Object levelAccessor,
            double x, double y, double z,
            Object entityObj,
            CallbackInfo ci
    ) {
        if (entityObj instanceof Entity e) ECHELON$BAG_USER.set(e);
        else ECHELON$BAG_USER.remove();
    }

    @Inject(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At("RETURN"),
            require = 0
    )
    private static void echelon$clearBagUser(
            Object levelAccessor,
            double x, double y, double z,
            Object entityObj,
            CallbackInfo ci
    ) {
        ECHELON$BAG_USER.remove();
    }

    @ModifyArg(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    // Keep this Yarn target + remap=true so it can match under Connector too
                    target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"
            ),
            index = 4,
            remap = true,
            require = 0
    )
    private static ItemStack echelon$maybeReforgeSpawnedStack(ItemStack stack) {
        Entity user = ECHELON$BAG_USER.get();
        return (user != null) ? ArmageddonTreasureBagHooks.maybeReforgeFromHeldBag(stack, user) : stack;
    }
}
