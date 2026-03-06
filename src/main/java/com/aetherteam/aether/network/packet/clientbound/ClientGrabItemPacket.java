package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import com.aetherteam.aether.network.AetherPayloadContext;

public record ClientGrabItemPacket(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ClientGrabItemPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "grab_from_accessories_inventory"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientGrabItemPacket> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        ClientGrabItemPacket::stack,
        ClientGrabItemPacket::new);

    @Override
    public Type<ClientGrabItemPacket> type() {
        return TYPE;
    }

    public static void execute(ClientGrabItemPacket payload, AetherPayloadContext context) {
        if (context.player() != null) {
            context.player().containerMenu.setCarried(payload.stack());
        }
    }
}
