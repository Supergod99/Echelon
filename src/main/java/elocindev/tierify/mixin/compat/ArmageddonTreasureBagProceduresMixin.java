package elocindev.tierify.mixin.compat;

import elocindev.tierify.compat.ArmageddonTreasureBagHooks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(
        targets = {
                "net.mcreator.armageddonmod.procedures.ArionTreasurebagRightclickedProcedure",
                "net.mcreator.armageddonmod.procedures.IronColossusTreasureBagRightclickedProcedure"
        },
        remap = false
)
public abstract class ArmageddonTreasureBagProceduresMixin {

    /**
     * Armageddon procedures spawn loot with:
     *   new ItemEntity(_level, x, y, z, new ItemStack(...))
     * We rewrite the ItemStack argument (index 4) to a possibly-reforged stack.
     *
     * remap=true matters under Connector so the ItemEntity ctor target can be matched.
     */
    @ModifyArg(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"
            ),
            index = 4,
            remap = true,
            require = 0
    )
    private static ItemStack echelon$maybeReforgeSpawnedStack(
            ItemStack stack,
            @Coerce Object levelAccessor,
            double x, double y, double z,
            @Coerce Object entityObj
    ) {
        try {
            Entity yarnEntity = (Entity) entityObj;
            return ArmageddonTreasureBagHooks.maybeReforgeFromHeldBag(stack, yarnEntity);
        } catch (Throwable t) {
            return stack;
        }
    }
}
