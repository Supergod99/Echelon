package elocindev.tierify.forge.screen;

import elocindev.tierify.forge.item.ReforgeAddition;
import elocindev.tierify.forge.registry.ForgeMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SalvageUpgradeMenu extends AbstractContainerMenu {
    private final Container inputs = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            SalvageUpgradeMenu.this.slotsChanged(this);
        }
    };

    private final ContainerLevelAccess access;
    private final DataSlot upgradeReady = DataSlot.standalone();
    private final DataSlot salvageLevel = DataSlot.standalone();
    private final DataSlot upgradeRemaining = DataSlot.standalone();
    private final Player player;

    public SalvageUpgradeMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, ContainerLevelAccess.create(inv.player.level(), buf.readBlockPos()));
    }

    public SalvageUpgradeMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(ForgeMenuTypes.SALVAGE_UPGRADE.get(), id);
        this.access = access;
        this.player = inv.player;

        addDataSlot(upgradeReady);
        addDataSlot(salvageLevel);
        addDataSlot(upgradeRemaining);
        if (!inv.player.level().isClientSide) {
            salvageLevel.set(SalvageMenu.getSalvageLevel(inv.player));
            updateRemainingCost();
        }

        addSlot(new Slot(inputs, 0, 80, 34));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    public boolean isUpgradeReady() {
        return upgradeReady.get() == 1;
    }

    public int getSalvageLevel() {
        return getUnlockedTier();
    }

    public int getUpgradeTargetTier() {
        ItemStack input = inputs.getItem(0);
        int targetTier = getUpgradeCandidateTier(input);
        if (targetTier <= 0) return 0;
        int remaining = upgradeRemaining.get();
        if (remaining <= 0) return targetTier;
        return input.getCount() >= remaining ? targetTier : 0;
    }

    public int getUpgradeRemainingCost() {
        return upgradeRemaining.get();
    }

    public ContainerLevelAccess getAccess() {
        return access;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (!player.level().isClientSide) {
            salvageLevel.set(SalvageMenu.getSalvageLevel(player));
            updateRemainingCost();
        }
        upgradeReady.set(computeReady() ? 1 : 0);
    }

    private boolean computeReady() {
        ItemStack input = inputs.getItem(0);
        if (input.isEmpty()) return false;
        return getUpgradeCandidateTier(input) > 0;
    }

    public void doUpgrade(ServerPlayer sp) {
        ItemStack input = inputs.getItem(0);
        int targetTier = getUpgradeCandidateTier(input);
        if (targetTier <= 0) return;

        int remaining = SalvageMenu.getRemainingUpgradeCost(sp, targetTier);
        if (remaining <= 0) {
            SalvageMenu.clearUpgradeProgress(sp, targetTier);
            SalvageMenu.setSalvageLevel(sp, targetTier);
            salvageLevel.set(targetTier);
            updateRemainingCost();
            SalvageMenu.playUpgradeSound(access);
            return;
        }

        int toConsume = Math.min(remaining, input.getCount());
        if (toConsume <= 0) return;
        consumeInput(toConsume);
        SalvageMenu.addUpgradeProgress(sp, targetTier, toConsume);

        remaining = SalvageMenu.getRemainingUpgradeCost(sp, targetTier);
        if (remaining <= 0) {
            SalvageMenu.clearUpgradeProgress(sp, targetTier);
            SalvageMenu.setSalvageLevel(sp, targetTier);
            salvageLevel.set(targetTier);
            SalvageMenu.playUpgradeSound(access);
        }
        updateRemainingCost();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            clearContainer(player, inputs);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return AnvilMenuValidity.stillValidAnvil(access, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemStack);
            } else if (index >= 1 && index < 37) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemStack;
    }

    private int getUnlockedTier() {
        int level = salvageLevel.get();
        if (level < 0) return 0;
        return Math.min(level, SalvageMenu.getConfigMaxTier());
    }

    private int getUpgradeCandidateTier(ItemStack input) {
        if (!(input.getItem() instanceof ReforgeAddition reforge)) return 0;
        int targetTier = reforge.getTier();
        if (targetTier <= 0 || targetTier > SalvageMenu.getConfigMaxTier()) return 0;
        int unlocked = getUnlockedTier();
        if (targetTier == 6 && unlocked >= 6 && unlocked < SalvageMenu.getConfigMaxTier()) {
            int nextTier = unlocked + 1;
            return SalvageMenu.getUpgradeCostCount(nextTier) > 0 ? nextTier : 0;
        }
        if (SalvageMenu.getUpgradeCostCount(targetTier) <= 0) return 0;
        return targetTier == (unlocked + 1) ? targetTier : 0;
    }

    private void updateRemainingCost() {
        int targetTier = getUnlockedTier() + 1;
        int maxTier = SalvageMenu.getConfigMaxTier();
        if (targetTier <= 0 || targetTier > maxTier) {
            upgradeRemaining.set(0);
            return;
        }
        upgradeRemaining.set(SalvageMenu.getRemainingUpgradeCost(player, targetTier));
    }

    private void consumeInput() {
        consumeInput(1);
    }

    private void consumeInput(int count) {
        ItemStack input = inputs.getItem(0);
        if (input.isEmpty()) return;
        if (count <= 0) return;
        input.shrink(count);
        if (input.isEmpty()) {
            inputs.setItem(0, ItemStack.EMPTY);
        } else {
            inputs.setItem(0, input);
        }
    }

}
