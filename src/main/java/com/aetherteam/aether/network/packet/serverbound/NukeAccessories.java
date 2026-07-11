package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.network.AetherPayloadContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record NukeAccessories() implements CustomPacketPayload {
    public static final Type<NukeAccessories> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "nuke_accessories"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NukeAccessories> STREAM_CODEC = StreamCodec.unit(new NukeAccessories());

    @Override
    public Type<NukeAccessories> type() {
        return TYPE;
    }

    public static void execute(NukeAccessories payload, AetherPayloadContext context) {
        if (!context.player().getAbilities().instabuild) {
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(context.player());
        if (accessories != null) {
            accessories.clearAccessories(true);
        }
    }
}
