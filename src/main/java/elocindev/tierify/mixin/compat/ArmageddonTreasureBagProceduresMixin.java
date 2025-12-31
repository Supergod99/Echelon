package elocindev.tierify.mixin.compat;

import elocindev.tierify.compat.ArmageddonTreasureBagHooks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
    targets = {
        // Include all Armageddon treasure-bag procedures found in the mod (safe even if some are missing).
        "net.mcreator.armageddonmod.procedures.ArionTreasurebagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.ElderGuardianTreasurebagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.EldorathTreasureBagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.ElvenitePaladinTreasureBagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.EnderDragonTreasurebagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.GoblinLordTreasureBagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.IronColossusTreasureBagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.NyxarisTheVeilOfOblivionTreasureBagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.SanghorLordOfBloodTreasureBagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.TheBringerOfDoomTreasureBagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.TheCalamitiesTreasureBagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.VaedricTreasureBagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.ZoranthNewbornOfTheZenithTreasureBagRightclickedOnBlockProcedure",
        "net.mcreator.armageddonmod.procedures.ZoranthTreasureBagRightclickedProcedure"
    },
    remap = false
)
public abstract class ArmageddonTreasureBagProceduresMixin {

    /**
     * Carries the "bag user" entity from execute(...) into the ItemEntity constructor site.
     * ThreadLocal prevents cross-thread bleed (dedicated server thread vs client thread).
     */
    private static final ThreadLocal<Object> ECHELON$BAG_USER = new ThreadLocal<>();

    // --- Capture context (MCreator variants) ---

    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 0)
    private static void echelon$captureUser_execute_xyz(
        @Coerce Object levelAccessor,
        double x, double y, double z,
        @Coerce Object entityObj,
        CallbackInfo ci
    ) {
        ECHELON$BAG_USER.set(entityObj);
    }

    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 0)
    private static void echelon$captureUser_execute_entityOnly(
        @Coerce Object levelAccessor,
        @Coerce Object entityObj,
        CallbackInfo ci
    ) {
        ECHELON$BAG_USER.set(entityObj);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 0)
    private static void echelon$clearUser_execute_xyz(
        @Coerce Object levelAccessor,
        double x, double y, double z,
        @Coerce Object entityObj,
        CallbackInfo ci
    ) {
        ECHELON$BAG_USER.remove();
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 0)
    private static void echelon$clearUser_execute_entityOnly(
        @Coerce Object levelAccessor,
        @Coerce Object entityObj,
        CallbackInfo ci
    ) {
        ECHELON$BAG_USER.remove();
    }

    // --- Rewrite spawned ItemStack in "new ItemEntity(..., stack)" ---

    /**
     * Yarn-side signature (will be remapped under Connector).
     */
    @ModifyArg(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"
        ),
        index = 4,
        remap = true,
        require = 0
    )
    private static ItemStack echelon$maybeReforgeSpawnedStack_yarn(ItemStack stack) {
        return echelon$maybeReforgeSpawnedStack(stack);
    }

    /**
     * Mojmap/Forge-side signature (literal; do not remap).
     */
    @ModifyArg(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"
        ),
        index = 4,
        remap = false,
        require = 0
    )
    private static ItemStack echelon$maybeReforgeSpawnedStack_mojmap(ItemStack stack) {
        return echelon$maybeReforgeSpawnedStack(stack);
    }

    private static ItemStack echelon$maybeReforgeSpawnedStack(ItemStack stack) {
        Object entityObj = ECHELON$BAG_USER.get();
        if (entityObj == null) return stack;

        try {
            // Under Connector, this cast is expected to be valid after remapping.
            Entity user = (Entity) entityObj;
            return ArmageddonTreasureBagHooks.maybeReforgeFromHeldBag(stack, user);
        } catch (Throwable t) {
            // Absolute safety: never crash the game because of this compatibility hook.
            return stack;
        }
    }
}
