package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.screen.inventory.SunAltarScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Opens {@link SunAltarScreen} from {@link com.aetherteam.aether.block.utility.SunAltarBlock}.
 */
public record OpenSunAltarPacket(Component name, int timeScale, BlockPos altarPos) implements CustomPacketPayload {
    public static final Type<OpenSunAltarPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "open_sun_altar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSunAltarPacket> STREAM_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC,
        OpenSunAltarPacket::name,
        ByteBufCodecs.INT,
        OpenSunAltarPacket::timeScale,
        BlockPos.STREAM_CODEC,
        OpenSunAltarPacket::altarPos,
        OpenSunAltarPacket::new);

    @Override
    public Type<OpenSunAltarPacket> type() {
        return TYPE;
    }
}
