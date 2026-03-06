package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import com.aetherteam.aether.network.AetherPayloadContext;

public record QueenDialoguePacket(int queenID) implements CustomPacketPayload {
    public static final Type<QueenDialoguePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "open_valkyrie_queen_dialogue"));

    public static final StreamCodec<RegistryFriendlyByteBuf, QueenDialoguePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        QueenDialoguePacket::queenID,
        QueenDialoguePacket::new);

    @Override
    public Type<QueenDialoguePacket> type() {
        return TYPE;
    }

    public static void execute(QueenDialoguePacket payload, AetherPayloadContext context) {
        Player player = context.player();
        if (player != null && player.level().getEntity(payload.queenID()) instanceof ValkyrieQueen valkyrieQueen) {
            valkyrieQueen.openDialogueScreen();
        }
    }
}
