package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record NukeAccessoriesPacket() implements CustomPacketPayload {
    public static final Type<NukeAccessoriesPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "nuke_accessories"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NukeAccessoriesPacket> STREAM_CODEC = StreamCodec.unit(new NukeAccessoriesPacket());

    @Override
    public Type<NukeAccessoriesPacket> type() {
        return TYPE;
    }

    public static void execute(NukeAccessoriesPacket payload, ServerPlayer player) {
        if (!player.getAbilities().instabuild) {
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(player);
        if (accessories != null) {
            accessories.clearAccessories(true);
        }
    }
}
