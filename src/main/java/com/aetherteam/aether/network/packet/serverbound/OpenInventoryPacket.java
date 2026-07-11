package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.network.packet.clientbound.ClientGrabItemPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.aetherteam.aether.network.AetherPacketSender;

public record OpenInventoryPacket(ItemStack carryStack) implements CustomPacketPayload {
    public static final Type<OpenInventoryPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "open_vanilla_inventory"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInventoryPacket> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        OpenInventoryPacket::carryStack,
        OpenInventoryPacket::new);

    @Override
    public Type<OpenInventoryPacket> type() {
        return TYPE;
    }

    public static void execute(OpenInventoryPacket payload, ServerPlayer player) {
        Player playerEntity = player;
        if (playerEntity.level().getServer() != null && playerEntity instanceof ServerPlayer serverPlayer) {
            ItemStack itemStack = serverPlayer.isCreative() ? payload.carryStack() : serverPlayer.containerMenu.getCarried();
            serverPlayer.containerMenu.setCarried(ItemStack.EMPTY);
            serverPlayer.doCloseContainer();
            if (!itemStack.isEmpty()) {
                if (!serverPlayer.isCreative()) {
                    serverPlayer.containerMenu.setCarried(itemStack);
                    AetherPacketSender.sendToPlayer(serverPlayer, new ClientGrabItemPacket(itemStack));
                }
            }
        }
    }
}
