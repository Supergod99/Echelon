package elocindev.tierify.forge.screen;

import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.config.ForgeTierifyConfig;
import elocindev.tierify.forge.item.ReforgeAddition;
import elocindev.tierify.forge.registry.ForgeItemRegistry;
import elocindev.tierify.forge.registry.ForgeMenuTypes;
import elocindev.tierify.forge.registry.ForgeSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

import java.util.Locale;

public class SalvageMenu extends AbstractContainerMenu {
    static final String SALVAGE_LEVEL_KEY = "TierifySalvageLevel";
    static final String SALVAGE_PROGRESS_KEY = "TierifySalvageUpgradeProgress";
    private final Container inputs = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            SalvageMenu.this.slotsChanged(this);
        }
    };

    private final ContainerLevelAccess access;
    private final DataSlot salvageReady = DataSlot.standalone();
    private final DataSlot salvageLevel = DataSlot.standalone();
    private final Player player;

    public SalvageMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, ContainerLevelAccess.create(inv.player.level(), buf.readBlockPos()));
    }

    public SalvageMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(ForgeMenuTypes.SALVAGE.get(), id);
        this.access = access;
        this.player = inv.player;

        addDataSlot(salvageReady);
        addDataSlot(salvageLevel);
        if (!inv.player.level().isClientSide) {
            salvageLevel.set(getSalvageLevel(inv.player));
        }

        addSlot(new Slot(inputs, 0, 45, 47));
        addSlot(new Slot(inputs, 1, 115, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    public boolean isSalvageReady() {
        return salvageReady.get() == 1;
    }

    public int getSalvageLevel() {
        return getUnlockedTier();
    }

    public int getUpgradeTargetTier() {
        return 0;
    }

    public ContainerLevelAccess getAccess() {
        return access;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (!player.level().isClientSide) {
            salvageLevel.set(getSalvageLevel(player));
        }
        salvageReady.set(computeReady() ? 1 : 0);
    }

    private boolean computeReady() {
        if (!inputs.getItem(1).isEmpty()) return false;

        ItemStack input = inputs.getItem(0);
        if (input.isEmpty()) return false;

        int tierIndex = getTierIndex(getTierId(input));
        int unlocked = getUnlockedTier();
        if (tierIndex <= 0 || tierIndex > unlocked) return false;
        ItemStack toAdd = createOutputForTier(tierIndex);
        return !toAdd.isEmpty();
    }

    public void doSalvage(ServerPlayer sp) {
        if (!computeReady()) return;

        ItemStack input = inputs.getItem(0);
        int baseTier = getTierIndex(getTierId(input));
        int unlocked = getUnlockedTier();
        if (baseTier <= 0 || baseTier > unlocked) return;

        int rollTier = rollTierOutcome(baseTier, unlocked, sp.getRandom());
        if (rollTier == 0) {
            consumeInput();
            return;
        }

        ItemStack toAdd = createOutputForTier(rollTier);
        if (toAdd.isEmpty()) return;

        ItemStack output = inputs.getItem(1);
        if (!canAcceptOutput(output, toAdd)) return;
        if (output.isEmpty()) {
            inputs.setItem(1, toAdd);
        } else {
            output.grow(toAdd.getCount());
            inputs.setItem(1, output);
        }

        playTierSound(access, rollTier);
        consumeInput();
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

            if (index == 1 || index == 0) {
                if (!this.moveItemStackTo(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemStack);
            } else if (index >= 2 && index < 38) {
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
        return Math.min(level, getConfigMaxTier());
    }

    private int getUpgradeCandidateTier(ItemStack input) {
        if (!(input.getItem() instanceof ReforgeAddition reforge)) return 0;
        int targetTier = reforge.getTier();
        if (targetTier <= 0 || targetTier > getConfigMaxTier()) return 0;
        if (!inputs.getItem(1).isEmpty()) return 0;
        int unlocked = getUnlockedTier();
        if (targetTier == 6 && unlocked >= 6 && unlocked < getConfigMaxTier()) {
            int nextTier = unlocked + 1;
            return getUpgradeCostCount(nextTier) > 0 ? nextTier : 0;
        }
        if (getUpgradeCostCount(targetTier) <= 0) return 0;
        return targetTier == (unlocked + 1) ? targetTier : 0;
    }

    private boolean tryUpgrade(ServerPlayer sp) {
        ItemStack input = inputs.getItem(0);
        int targetTier = getUpgradeCandidateTier(input);
        if (targetTier <= 0) return false;

        int remaining = getRemainingUpgradeCost(sp, targetTier);
        if (remaining <= 0) {
            clearUpgradeProgress(sp, targetTier);
            setSalvageLevel(sp, targetTier);
            salvageLevel.set(targetTier);
            playUpgradeSound(access);
            return true;
        }

        int toConsume = Math.min(remaining, input.getCount());
        if (toConsume <= 0) return false;
        consumeInput(toConsume);
        addUpgradeProgress(sp, targetTier, toConsume);

        remaining = getRemainingUpgradeCost(sp, targetTier);
        if (remaining <= 0) {
            clearUpgradeProgress(sp, targetTier);
            setSalvageLevel(sp, targetTier);
            salvageLevel.set(targetTier);
            playUpgradeSound(access);
        }
        return true;
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

    private static String getTierId(ItemStack stack) {
        CompoundTag nbt = stack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY);
        if (nbt == null) return "";
        return nbt.getString(TierifyConstants.NBT_SUBTAG_DATA_KEY);
    }

    private static int getTierIndex(String tierId) {
        if (tierId == null || tierId.isEmpty()) return 0;
        String id = tierId.toLowerCase(Locale.ROOT);
        if (id.contains("mythic")) return 6;
        if (id.contains("legendary")) return 5;
        if (id.contains("epic")) return 4;
        if (id.contains("rare")) return 3;
        if (id.contains("uncommon")) return 2;
        if (id.contains("uncomon")) return 2;
        if (id.contains("common")) return 1;
        return 0;
    }

    private static ItemStack createOutputForTier(int tierIndex) {
        return switch (tierIndex) {
            case 1 -> new ItemStack(ForgeItemRegistry.LIMESTONE_CHUNK.get());
            case 2 -> new ItemStack(ForgeItemRegistry.PYRITE_CHUNK.get());
            case 3 -> new ItemStack(ForgeItemRegistry.GALENA_CHUNK.get());
            case 4 -> new ItemStack(ForgeItemRegistry.CHAROITE.get());
            case 5 -> new ItemStack(ForgeItemRegistry.CROWN_TOPAZ.get());
            case 6, 7, 8, 9, 10 -> new ItemStack(ForgeItemRegistry.PAINITE.get());
            default -> ItemStack.EMPTY;
        };
    }

    public static ItemStack getMaterialForTier(int tierIndex) {
        return createOutputForTier(tierIndex);
    }

    public static int getUpgradeCostCount(int targetTier) {
        return switch (targetTier) {
            case 1 -> 32;
            case 2 -> 16;
            case 3 -> 8;
            case 4 -> 4;
            case 5 -> 2;
            case 6 -> 1;
            case 7 -> 2;
            case 8 -> 4;
            case 9 -> 8;
            case 10 -> 16;
            default -> 0;
        };
    }

    public static int getRemainingUpgradeCost(Player player, int targetTier) {
        int cost = getUpgradeCostCount(targetTier);
        if (cost <= 0) return 0;
        int progress = getUpgradeProgress(player, targetTier);
        return Math.max(0, cost - progress);
    }

    public static int getUpgradeProgress(Player player, int targetTier) {
        if (targetTier <= 0) return 0;
        CompoundTag tag = getProgressTag(player);
        return tag.getInt(progressKey(targetTier));
    }

    public static void addUpgradeProgress(Player player, int targetTier, int amount) {
        if (targetTier <= 0 || amount <= 0) return;
        CompoundTag tag = getProgressTag(player);
        String key = progressKey(targetTier);
        int current = tag.getInt(key);
        tag.putInt(key, current + amount);
    }

    public static void clearUpgradeProgress(Player player, int targetTier) {
        if (targetTier <= 0) return;
        CompoundTag tag = getProgressTag(player);
        tag.remove(progressKey(targetTier));
    }

    public static void clearAllUpgradeProgress(Player player) {
        player.getPersistentData().remove(SALVAGE_PROGRESS_KEY);
    }

    private static CompoundTag getProgressTag(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(SALVAGE_PROGRESS_KEY, Tag.TAG_COMPOUND)) {
            data.put(SALVAGE_PROGRESS_KEY, new CompoundTag());
        }
        return data.getCompound(SALVAGE_PROGRESS_KEY);
    }

    private static String progressKey(int targetTier) {
        return "tier_" + targetTier;
    }

    private static boolean canAcceptOutput(ItemStack output, ItemStack toAdd) {
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(output, toAdd)) return false;
        return output.getCount() < output.getMaxStackSize();
    }

    public static int getSalvageLevel(Player player) {
        CompoundTag tag = player.getPersistentData();
        int level = tag.getInt(SALVAGE_LEVEL_KEY);
        if (level < 0) level = 0;
        return Math.min(level, getConfigMaxTier());
    }

    public static void setSalvageLevel(Player player, int level) {
        int clamped = Math.max(0, Math.min(level, getConfigMaxTier()));
        player.getPersistentData().putInt(SALVAGE_LEVEL_KEY, clamped);
        clearAllUpgradeProgress(player);
    }

    public static int getConfigMaxTier() {
        int max = ForgeTierifyConfig.salvageMaxTier();
        if (max < 1) return 1;
        if (max > 10) return 10;
        return max;
    }

    public record SalvageOdds(double none, double lower, double higher, double same) {}

    public static SalvageOdds getSalvageOddsForLevel(int level) {
        int clamped = Math.max(0, Math.min(level, getConfigMaxTier()));
        double none = clampChance(ForgeTierifyConfig.salvageChanceNone());
        double lower = clampChance(ForgeTierifyConfig.salvageChanceLower());
        double higher = clampChance(ForgeTierifyConfig.salvageChanceHigher());
        none = clampChance(none - (0.01 * clamped));
        lower = clampChance(lower - (0.01 * clamped));
        higher = clampChance(higher + (0.005 * clamped));
        double same = Math.max(0.0, 1.0 - (none + lower + higher));
        return new SalvageOdds(none, lower, higher, same);
    }

    private static int rollTierOutcome(int baseTier, int salvageLevel, RandomSource rng) {
        if (baseTier <= 0) return 0;
        int maxTier = getConfigMaxTier();
        SalvageOdds odds = getSalvageOddsForLevel(salvageLevel);
        double chanceNone = odds.none();
        double chanceLower = odds.lower();
        double chanceHigher = odds.higher();

        double roll = rng.nextDouble();
        if (roll < chanceNone) {
            return 0;
        }
        roll -= chanceNone;
        if (roll < chanceLower) {
            return Math.max(1, baseTier - 1);
        }
        roll -= chanceLower;
        if (roll < chanceHigher) {
            return Math.min(maxTier, baseTier + 1);
        }
        return baseTier;
    }

    private static double clampChance(double chance) {
        if (chance < 0.0) return 0.0;
        if (chance > 1.0) return 1.0;
        return chance;
    }

    static void playTierSound(ContainerLevelAccess access, int tierIndex) {
        if (tierIndex <= 0) return;
        access.execute((level, pos) -> playTierSound(level, pos, tierIndex));
    }

    static void playUpgradeSound(ContainerLevelAccess access) {
        access.execute((level, pos) -> playUpgradeSound(level, pos));
    }

    private static void playTierSound(Level level, BlockPos pos, int tierIndex) {
        SoundEvent toPlay = getTierSound(tierIndex);
        if (toPlay == null) {
            toPlay = SoundEvents.ANVIL_USE;
        }
        level.playSound(null, pos, toPlay, SoundSource.BLOCKS, 0.8f, 1.0f);
    }

    private static SoundEvent getTierSound(int tierIndex) {
        return switch (tierIndex) {
            case 1 -> ForgeSoundRegistry.REFORGE_SOUND_COMMON.get();
            case 2 -> ForgeSoundRegistry.REFORGE_SOUND_UNCOMMON.get();
            case 3 -> ForgeSoundRegistry.REFORGE_SOUND_RARE.get();
            case 4 -> ForgeSoundRegistry.REFORGE_SOUND_EPIC.get();
            case 5 -> ForgeSoundRegistry.REFORGE_SOUND_LEGENDARY.get();
            case 6 -> ForgeSoundRegistry.REFORGE_SOUND_MYTHIC.get();
            default -> SoundEvents.ANVIL_USE;
        };
    }

    private static void playUpgradeSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
    }
}
