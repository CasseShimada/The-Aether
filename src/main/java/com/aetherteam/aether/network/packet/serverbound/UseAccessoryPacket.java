package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.impl.AccessoryItemInteractions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public record UseAccessoryPacket(InteractionHand hand, boolean shield) implements CustomPacketPayload {
    public static final Type<UseAccessoryPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "use_accessory"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UseAccessoryPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        payload -> payload.hand() == InteractionHand.OFF_HAND,
        ByteBufCodecs.BOOL,
        UseAccessoryPacket::shield,
        (offHand, shield) -> new UseAccessoryPacket(offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, shield)
    );

    @Override
    public Type<UseAccessoryPacket> type() {
        return TYPE;
    }

    public static void execute(UseAccessoryPacket payload, ServerPlayer player) {
        if (payload.shield()) {
            AccessoryItemInteractions.startUsingShield(player);
        } else {
            AccessoryItemInteractions.equipFromUse(player, payload.hand());
        }
    }
}
