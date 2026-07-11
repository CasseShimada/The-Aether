package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.ClientGrabItemPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

public record OpenAccessoriesPacket(ItemStack carryStack) implements CustomPacketPayload {
    public static final Type<OpenAccessoriesPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "open_accessories"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAccessoriesPacket> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        OpenAccessoriesPacket::carryStack,
        OpenAccessoriesPacket::new);

    @Override
    public Type<OpenAccessoriesPacket> type() {
        return TYPE;
    }

    public static void execute(OpenAccessoriesPacket payload, ServerPlayer player) {
        ItemStack itemStack = player.isCreative() ? payload.carryStack() : player.containerMenu.getCarried();
        player.containerMenu.setCarried(ItemStack.EMPTY);
        player.openMenu(new SimpleMenuProvider((id, inventory, menuPlayer) -> new AetherAccessoriesMenu(id, inventory), Component.translatable("container.crafting")));
        if (!itemStack.isEmpty()) {
            player.containerMenu.setCarried(itemStack);
            AetherPacketSender.sendToPlayer(player, new ClientGrabItemPacket(itemStack));
        }
    }
}
