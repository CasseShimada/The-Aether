package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record ToggleAccessoryRenderPacket(String slotName, int slotIndex, boolean shouldRender) implements CustomPacketPayload {
    public static final Type<ToggleAccessoryRenderPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "toggle_accessory_render"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleAccessoryRenderPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ToggleAccessoryRenderPacket::slotName,
            ByteBufCodecs.INT,
            ToggleAccessoryRenderPacket::slotIndex,
            ByteBufCodecs.BOOL,
            ToggleAccessoryRenderPacket::shouldRender,
            ToggleAccessoryRenderPacket::new
    );

    @Override
    public Type<ToggleAccessoryRenderPacket> type() {
        return TYPE;
    }

    public static void execute(ToggleAccessoryRenderPacket payload, ServerPlayer player) {
        var accessories = AccessoriesAPI.getAccessories(player);
        if (accessories == null) {
            return;
        }

        var container = accessories.getContainer(() -> payload.slotName());
        if (container == null) {
            return;
        }

        int slotIndex = payload.slotIndex();
        if (slotIndex < 0 || slotIndex >= container.getAccessories().getContainerSize()) {
            return;
        }

        container.setShouldRender(slotIndex, payload.shouldRender());
    }
}
