package elocindev.tierify.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import elocindev.tierify.TierifyCommon;
import elocindev.tierify.TierifyConstants;
import elocindev.tierify.forge.ForgeTieredAttributeSubscriber;
import elocindev.tierify.forge.screen.SalvageMenu;
import elocindev.tierify.util.StarApexUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = TierifyCommon.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeCommandInit {

    private static final List<String> TIER_LIST =
            List.of("common", "uncommon", "rare", "epic", "legendary", "mythic");

    private ForgeCommandInit() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tiered")
                .requires(source -> source.hasPermission(3))
                .then(Commands.literal("tier")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.literal("common")
                                        .executes(ctx -> executeCommand(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                0)))
                                .then(Commands.literal("uncommon")
                                        .executes(ctx -> executeCommand(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                1)))
                                .then(Commands.literal("rare")
                                        .executes(ctx -> executeCommand(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                2)))
                                .then(Commands.literal("epic")
                                        .executes(ctx -> executeCommand(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                3)))
                                .then(Commands.literal("legendary")
                                        .executes(ctx -> executeCommand(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                4)))
                                .then(Commands.literal("mythic")
                                        .executes(ctx -> executeCommand(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                5)))))
                .then(Commands.literal("untier")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> executeCommand(
                                        ctx.getSource(),
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        -1))))
                .then(Commands.literal("stars")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 5))
                                .executes(ctx -> executeStarsSelf(
                                        ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "value")))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> executeStars(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                IntegerArgumentType.getInteger(ctx, "value"))))))
                .then(Commands.literal("apex")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> executeApexSelf(
                                        ctx.getSource(),
                                        BoolArgumentType.getBool(ctx, "value")))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> executeApex(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                BoolArgumentType.getBool(ctx, "value"))))))
                .then(Commands.literal("salvage")
                        .then(Commands.literal("reset")
                                .executes(ctx -> executeSalvageResetSelf(ctx.getSource()))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> executeSalvageReset(
                                                ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets")))))));
    }

    private static int executeCommand(CommandSourceStack source, Collection<ServerPlayer> targets, int tier) {
        for (ServerPlayer player : targets) {
            ItemStack itemStack = player.getMainHandItem();

            if (itemStack.isEmpty()) {
                source.sendSuccess(
                        () -> Component.translatable("commands.tiered.failed", player.getDisplayName()),
                        true);
                continue;
            }

            if (tier == -1) {
                if (itemStack.getTagElement(TierifyConstants.NBT_SUBTAG_KEY) != null) {
                    ForgeTieredAttributeSubscriber.clearTieredData(itemStack);
                    source.sendSuccess(
                            () -> Component.translatable(
                                    "commands.tiered.untier",
                                    itemStack.getItem().getName(itemStack).getString(),
                                    player.getDisplayName()),
                            true);
                } else {
                    source.sendSuccess(
                            () -> Component.translatable(
                                    "commands.tiered.untier_failed",
                                    itemStack.getItem().getName(itemStack).getString(),
                                    player.getDisplayName()),
                            true);
                }
                continue;
            }

            List<ResourceLocation> potentialTier =
                    ForgeTieredAttributeSubscriber.findTierIdsForCommand(itemStack, TIER_LIST.get(tier));
            if (potentialTier.isEmpty()) {
                source.sendSuccess(
                        () -> Component.translatable(
                                "commands.tiered.tiering_failed",
                                itemStack.getItem().getName(itemStack).getString(),
                                player.getDisplayName()),
                        true);
                continue;
            }

            ForgeTieredAttributeSubscriber.clearTieredData(itemStack);

            ResourceLocation attribute = potentialTier.get(player.getRandom().nextInt(potentialTier.size()));
            if (ForgeTieredAttributeSubscriber.applyTier(itemStack, attribute, false)) {
                source.sendSuccess(
                        () -> Component.translatable(
                                "commands.tiered.tier",
                                itemStack.getItem().getName(itemStack).getString(),
                                player.getDisplayName()),
                        true);
            } else {
                source.sendSuccess(
                        () -> Component.translatable(
                                "commands.tiered.tiering_failed",
                                itemStack.getItem().getName(itemStack).getString(),
                                player.getDisplayName()),
                        true);
            }
        }

        return 1;
    }

    private static int executeSalvageResetSelf(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Command can only be used by a player."));
            return 0;
        }
        return executeSalvageReset(source, List.of(player));
    }

    private static int executeSalvageReset(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            SalvageMenu.setSalvageLevel(player, 0);
            source.sendSuccess(
                    () -> Component.translatable("commands.tiered.salvage.reset", player.getDisplayName()),
                    true);
        }
        return 1;
    }

    private static int executeStarsSelf(CommandSourceStack source, int stars) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Command can only be used by a player."));
            return 0;
        }
        return executeStars(source, List.of(player), stars);
    }

    private static int executeStars(CommandSourceStack source, Collection<ServerPlayer> targets, int stars) {
        for (ServerPlayer player : targets) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.isEmpty()) {
                source.sendSuccess(
                        () -> Component.translatable("commands.tiered.failed", player.getDisplayName()),
                        true);
                continue;
            }
            StarApexUtils.setStars(itemStack, stars);
            source.sendSuccess(
                    () -> Component.translatable("commands.tiered.stars.set", stars, player.getDisplayName()),
                    true);
        }
        return 1;
    }

    private static int executeApexSelf(CommandSourceStack source, boolean apex) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Command can only be used by a player."));
            return 0;
        }
        return executeApex(source, List.of(player), apex);
    }

    private static int executeApex(CommandSourceStack source, Collection<ServerPlayer> targets, boolean apex) {
        String state = apex ? "true" : "false";
        for (ServerPlayer player : targets) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.isEmpty()) {
                source.sendSuccess(
                        () -> Component.translatable("commands.tiered.failed", player.getDisplayName()),
                        true);
                continue;
            }
            StarApexUtils.setApex(itemStack, apex);
            source.sendSuccess(
                    () -> Component.translatable("commands.tiered.apex.set", state, player.getDisplayName()),
                    true);
        }
        return 1;
    }
}
