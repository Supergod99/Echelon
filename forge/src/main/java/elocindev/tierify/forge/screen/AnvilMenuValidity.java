package elocindev.tierify.forge.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Blocks;

final class AnvilMenuValidity {

    private AnvilMenuValidity() {}

    static boolean stillValidAnvil(ContainerLevelAccess access, Player player) {
        return access.evaluate((level, pos) -> {
            boolean validBlock = level.getBlockState(pos).is(Blocks.ANVIL)
                    || level.getBlockState(pos).is(Blocks.CHIPPED_ANVIL)
                    || level.getBlockState(pos).is(Blocks.DAMAGED_ANVIL);
            if (!validBlock) {
                return false;
            }
            return player.distanceToSqr(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D
            ) <= 64.0D;
        }, false);
    }
}
