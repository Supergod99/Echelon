package elocindev.tierify.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import draylar.tiered.api.ModifierUtils;
import elocindev.tierify.Tierify;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommandInit {

    private static final List<String> TIER_LIST = List.of("common", "uncommon", "rare", "epic", "legendary", "mythic");

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("tiered").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.literal("tier").then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("common").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 0);
            })).then(CommandManager.literal("uncommon").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 1);
            })).then(CommandManager.literal("rare").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 2);
            })).then(CommandManager.literal("epic").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 3);
            })).then(CommandManager.literal("legendary").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 4);
            })).then(CommandManager.literal("mythic").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 5);
            })))).then(CommandManager.literal("untier").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), -1);
            }))));
        });
    }

    // 0: common; 1: uncommon; 2: rare; 3: epic; 4: legendary; 5: mythic
    private static int executeCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int tier) {
        Iterator<ServerPlayerEntity> it = targets.iterator();
    
        while (it.hasNext()) {
            ServerPlayerEntity player = it.next();
            ItemStack itemStack = player.getMainHandStack();
    
            if (itemStack.isEmpty()) {
                source.sendFeedback(() -> Text.translatable("commands.tiered.failed", player.getDisplayName()), true);
                continue;
            }
    
            // Untier
            if (tier == -1) {
                if (itemStack.getSubNbt(Tierify.NBT_SUBTAG_KEY) != null) {
                    ModifierUtils.removeItemStackAttribute(itemStack);
                    source.sendFeedback(() -> Text.translatable(
                            "commands.tiered.untier",
                            itemStack.getItem().getName(itemStack).getString(),
                            player.getDisplayName()
                    ), true);
                } else {
                    source.sendFeedback(() -> Text.translatable(
                            "commands.tiered.untier_failed",
                            itemStack.getItem().getName(itemStack).getString(),
                            player.getDisplayName()
                    ), true);
                }
                continue;
            }
    
            // Build list of potential attributes valid for this item
            ArrayList<Identifier> potentialAttributes = new ArrayList<>();
            Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
                if (attribute.isValid(Registries.ITEM.getId(itemStack.getItem()))) {
                    potentialAttributes.add(new Identifier(attribute.getID()));
                }
            });
    
            if (potentialAttributes.isEmpty()) {
                source.sendFeedback(() -> Text.translatable(
                        "commands.tiered.tiering_failed",
                        itemStack.getItem().getName(itemStack).getString(),
                        player.getDisplayName()
                ), true);
                continue;
            }
    
            // Filter to the requested tier rarity
            List<Identifier> potentialTier = new ArrayList<>();
            String tierName = TIER_LIST.get(tier);
    
            for (Identifier id : potentialAttributes) {
                String path = id.getPath();
    
                // same filtering rules you already had
                if (!path.contains(tierName)) continue;
                if (tierName.equals("common") && path.contains("uncommon")) continue;
    
                potentialTier.add(id);
            }
    
            if (potentialTier.isEmpty()) {
                source.sendFeedback(() -> Text.translatable(
                        "commands.tiered.tiering_failed",
                        itemStack.getItem().getName(itemStack).getString(),
                        player.getDisplayName()
                ), true);
                continue;
            }
    
            // Remove any existing Tierify attribute then apply the new one
            ModifierUtils.removeItemStackAttribute(itemStack);
    
            Identifier attribute = potentialTier.get(player.getWorld().getRandom().nextInt(potentialTier.size()));
            if (attribute != null) {
                // Canonical Tierify application path (writes Tiered tag, TierUUID, borders, TierifyExtra, etc.)
                ModifierUtils.setItemStackAttribute(attribute, itemStack);
    
                source.sendFeedback(() -> Text.translatable(
                        "commands.tiered.tier",
                        itemStack.getItem().getName(itemStack).getString(),
                        player.getDisplayName()
                ), true);
            } else {
                source.sendFeedback(() -> Text.translatable(
                        "commands.tiered.tiering_failed",
                        itemStack.getItem().getName(itemStack).getString(),
                        player.getDisplayName()
                ), true);
            }
        }
        return 1;
    }
}
