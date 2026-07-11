package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.perk.types.MoaSkins;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public record RegisterMoaSkinsPacket() implements CustomPacketPayload {
    public static final Type<RegisterMoaSkinsPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "register_moa_skin"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RegisterMoaSkinsPacket> STREAM_CODEC = StreamCodec.unit(new RegisterMoaSkinsPacket());

    @Override
    public Type<RegisterMoaSkinsPacket> type() {
        return TYPE;
    }

    public static void execute(RegisterMoaSkinsPacket payload, @Nullable Player contextPlayer) {
        if (contextPlayer != null) {
            MoaSkins.registerMoaSkins(contextPlayer.level());
        }
    }
}
