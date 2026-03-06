package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import com.aetherteam.aether.network.AetherPayloadContext;

/**
 * Plays the Aether Portal sound on the client from {@link com.aetherteam.aether.block.portal.AetherPortalForcer}.
 */
public record PortalTravelSoundPacket() implements CustomPacketPayload {
    public static final Type<PortalTravelSoundPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "play_portal_travel_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PortalTravelSoundPacket> STREAM_CODEC = StreamCodec.unit(new PortalTravelSoundPacket());

    @Override
    public Type<PortalTravelSoundPacket> type() {
        return TYPE;
    }

    public static void execute(PortalTravelSoundPacket payload, AetherPayloadContext context) {
        Player player = context.player();
        if (player != null) {
            player.playSound(AetherSoundEvents.BLOCK_AETHER_PORTAL_TRAVEL.get(), 0.25F, player.level().getRandom().nextFloat() * 0.4F + 0.8F);
        }
    }
}
