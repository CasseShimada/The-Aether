package com.aetherteam.aether.accessories.networking.server;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.network.AetherPayloadContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

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

    public static void execute(ToggleAccessoryRenderPacket payload, AetherPayloadContext context) {
        AccessoriesCapability capability = AccessoriesCapability.get(context.player());
        if (capability == null) {
            return;
        }

        var container = capability.getContainer(() -> payload.slotName());
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
