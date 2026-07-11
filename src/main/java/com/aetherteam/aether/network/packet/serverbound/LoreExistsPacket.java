package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Communicates whether a language entry for lore exists from the client to the server.
 */
public record LoreExistsPacket(int playerID, ItemStack itemStack, boolean exists) implements CustomPacketPayload {
    public static final Type<LoreExistsPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "check_for_lore_entry"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LoreExistsPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        LoreExistsPacket::playerID,
        ItemStack.OPTIONAL_STREAM_CODEC,
        LoreExistsPacket::itemStack,
        ByteBufCodecs.BOOL,
        LoreExistsPacket::exists,
        LoreExistsPacket::new);

    @Override
    public Type<LoreExistsPacket> type() {
        return TYPE;
    }

    public static void execute(LoreExistsPacket payload, ServerPlayer player) {
        Player playerEntity = player;
        if (playerEntity.level().getServer() != null
            && playerEntity.level().getEntity(payload.playerID()) instanceof ServerPlayer
            && playerEntity.containerMenu instanceof LoreBookMenu menu) {
            menu.setLoreEntryExists(payload.exists());
        }
    }
}
