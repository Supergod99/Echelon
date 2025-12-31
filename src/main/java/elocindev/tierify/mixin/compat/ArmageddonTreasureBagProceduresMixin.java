package elocindev.tierify.mixin.compat;

import elocindev.tierify.compat.ArmageddonTreasureBagHooks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(
    targets = {
        "net.mcreator.armageddonmod.procedures.ArionTreasurebagRightclickedProcedure",
        "net.mcreator.armageddonmod.procedures.IronColossusTreasureBagRightclickedProcedure"
        // Add more procedure class names here as Armageddon adds bags (still ONE mixin file).
    },
    remap = false
)
public class ArmageddonTreasureBagProceduresMixin {

    @ModifyArg(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"
        ),
        index = 4,
        require = 0,
        remap = false
    )
    private static net.minecraft.world.item.ItemStack echelon$maybeReforgeSpawnedStack(
        net.minecraft.world.item.ItemStack stack,
        net.minecraft.world.level.LevelAccessor world,
        double x, double y, double z,
        net.minecraft.world.entity.Entity entity
    ) {
        // Armageddon is Mojmap/Forge; use their types here and keep remap=false.
        return (net.minecraft.world.item.ItemStack)(Object)
            ArmageddonTreasureBagHooks.maybeReforgeFromHeldBag((ItemStack)(Object)stack, (Entity)(Object)entity);
    }
}
