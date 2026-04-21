package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.aetherteam.aether.network.AetherPayloadContext;
import com.aetherteam.aether.util.ClientReflection;

/**
 * Opens {@link SunAltarScreen} from {@link com.aetherteam.aether.block.utility.SunAltarBlock}.
 */
public record OpenSunAltarPacket(Component name, int timeScale) implements CustomPacketPayload {
    public static final Type<OpenSunAltarPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "open_sun_altar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSunAltarPacket> STREAM_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC,
        OpenSunAltarPacket::name,
        ByteBufCodecs.INT,
        OpenSunAltarPacket::timeScale,
        OpenSunAltarPacket::new);

    @Override
    public Type<OpenSunAltarPacket> type() {
        return TYPE;
    }

    public static void execute(OpenSunAltarPacket payload, AetherPayloadContext context) {
        if (context.player() != null) {
            invokeClientScreen(payload.name(), payload.timeScale());
        }
    }

    private static void invokeClientScreen(Component name, int timeScale) {
        ClientReflection.openSunAltarScreen(name, timeScale);
    }
}
