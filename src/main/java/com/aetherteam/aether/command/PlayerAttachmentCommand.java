package com.aetherteam.aether.command;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.network.packet.clientbound.HealthResetPacket;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.aetherteam.aether.network.AetherPacketSender;

import java.util.Collection;

public class PlayerAttachmentCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("aether")
                .then(Commands.literal("player").requires((commandSourceStack) -> commandSourceStack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(Commands.literal("life_shards")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                                .suggests((context, builder) -> {
                                                    PlayerList playerlist = context.getSource().getServer().getPlayerList();
                                                    return SharedSuggestionProvider.suggest(playerlist.getPlayers().stream().map((player) -> player.getGameProfile().name()), builder);
                                                }).then(Commands.argument("value", IntegerArgumentType.integer(0, 10)).executes((context) -> setLifeShards(context.getSource(), GameProfileArgument.getGameProfiles(context, "targets"), IntegerArgumentType.getInteger(context, "value"))))
                                        )
                                )
                        )
                )
        );
    }

    /**
     * Sets the Life Shard (half) heart count of a list of players to a specific value.
     *
     * @param source       The {@link CommandSourceStack}.
     * @param gameProfiles A {@link Collection} of {@link NameAndId} entries to execute the command on.
     * @param value        The {@link Integer} value for the amount of Life Shard hearts.
     * @return An {@link Integer}.
     */
    private static int setLifeShards(CommandSourceStack source, Collection<NameAndId> gameProfiles, int value) {
        ServerLevel level = source.getLevel();
        PlayerList playerList = source.getServer().getPlayerList();
        for (NameAndId gameProfile : gameProfiles) {
            ServerPlayer player = playerList.getPlayer(gameProfile.id());
            if (player != null) {
                var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                data.setSynced(player.getId(), AttachmentSyncable.Direction.CLIENT, AetherPlayerAttachment.LIFE_SHARD_COUNT_SYNC_KEY, value);
                AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
                if (attribute != null) {
                    attribute.removeModifier(data.getLifeShardHealthAttributeModifier().id());
                }
                player.setHealth(player.getMaxHealth());
                AetherPacketSender.sendToPlayersNear(level, player, player.getX(), player.getY(), player.getZ(), 5.0, new HealthResetPacket(player.getId(), value)); // Sync to client.
                source.sendSuccess(() -> Component.translatable("commands.aether.capability.player.life_shards.set", player.getDisplayName(), value), true);
            }
        }
        return 1;
    }
}
