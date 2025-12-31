package elocindev.tierify.mixin;

import draylar.tiered.api.ModifierUtils;
import elocindev.tierify.Tierify;
import elocindev.tierify.config.EntityLootDropProfiles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class EntityEquipmentDropMixin {

    private static final org.apache.logging.log4j.Logger LOGGER =
            org.apache.logging.log4j.LogManager.getLogger("Echelon-EquipDrops");

    @ModifyArg(
        method = "dropEquipment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"
        ),
        index = 0,
        require = 0
    )
    private ItemStack echelon$maybeReforgeEquipmentDrop_entityOwner_noYOffset(ItemStack stack) {
        return echelon$maybeReforgeEquipmentDrop(stack);
    }

    @ModifyArg(
        method = "dropEquipment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;"
        ),
        index = 0,
        require = 0
    )
    private ItemStack echelon$maybeReforgeEquipmentDrop_entityOwner_withYOffset(ItemStack stack) {
        return echelon$maybeReforgeEquipmentDrop(stack);
    }

    @ModifyArg(
        method = "dropEquipment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"
        ),
        index = 0,
        require = 0
    )
    private ItemStack echelon$maybeReforgeEquipmentDrop_noYOffset(ItemStack stack) {
        return echelon$maybeReforgeEquipmentDrop(stack);
    }

    @ModifyArg(
        method = "dropEquipment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;"
        ),
        index = 0,
        require = 0
    )
    private ItemStack echelon$maybeReforgeEquipmentDrop_withYOffset(ItemStack stack) {
        return echelon$maybeReforgeEquipmentDrop(stack);
    }

    // Catch equipment drops that bypass dropEquipment but still go through dropStack.
    @ModifyArg(
        method = "dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"
        ),
        index = 0,
        require = 0
    )
    private ItemStack echelon$maybeReforgeAnyMobDrop_dropStack(ItemStack stack) {
        return echelon$maybeReforgeEquipmentDrop(stack);
    }

    private ItemStack echelon$maybeReforgeEquipmentDrop(ItemStack stack) {
        LivingEntity self = (LivingEntity) (Object) this;
    
        if (stack == null || stack.isEmpty()) return stack;
        if (self.getWorld().isClient()) return stack;
    
        if (!(self instanceof MobEntity)) return stack;
        if (!Tierify.CONFIG.entityEquipmentDropModifier) return stack;
    
        NbtCompound tierTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (tierTag != null && tierTag.contains(Tierify.NBT_SUBTAG_DATA_KEY)) {
            return stack;
        }
    
        Identifier entityId = Registries.ENTITY_TYPE.getId(self.getType());
        EntityLootDropProfiles.Entry profile = EntityLootDropProfiles.get(entityId);
        if (profile == null) return stack;
    
        Random rng = Random.create();
        if (rng.nextFloat() > profile.chance()) return stack;
    
        Identifier itemId = Registries.ITEM.getId(stack.getItem());
    
        boolean hadTierBefore = false;
        NbtCompound beforeTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (beforeTag != null && beforeTag.contains(Tierify.NBT_SUBTAG_DATA_KEY)) hadTierBefore = true;
    
        LOGGER.info("[EquipDrop] entity={} item={} chance={} weights={},{},{},{},{},{} hadTierBefore={}",
                entityId,
                itemId,
                profile.chance(),
                profile.weights()[0], profile.weights()[1], profile.weights()[2],
                profile.weights()[3], profile.weights()[4], profile.weights()[5],
                hadTierBefore
        );
    
        ModifierUtils.setItemStackAttributeEntityWeightedWithCustomWeights(null, stack, profile.weights());
    
        boolean hasTierAfter = false;
        NbtCompound afterTag = stack.getSubNbt(Tierify.NBT_SUBTAG_KEY);
        if (afterTag != null && afterTag.contains(Tierify.NBT_SUBTAG_DATA_KEY)) hasTierAfter = true;
    
        LOGGER.info("[EquipDrop] entity={} item={} applied=true hasTierAfter={}",
                entityId, itemId, hasTierAfter
        );
    
        return stack;
    }
}
