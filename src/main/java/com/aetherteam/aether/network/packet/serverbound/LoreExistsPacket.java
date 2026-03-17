package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.aetherteam.aether.network.AetherPayloadContext;

/**
 * Communicates whether a language entry for lore exists from the client to the server.
 */
public record LoreExistsPacket(int playerID, ItemStack itemStack, boolean exists) implements CustomPacketPayload {
    public static final Type<LoreExistsPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "check_for_lore_entry"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LoreExistsPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        LoreExistsPacket::playerID,
        ItemStack.OPTIONAL_STREAM_CODEC,
        LoreExistsPacket::itemStack,
        ByteBufCodecs.BOOL,
        LoreExistsPacket::exists,
        LoreExistsPacket::new);

    @Override
    public Type<LoreExistsPacket> type() {
        return TYPE;
    }

    public static void execute(LoreExistsPacket payload, AetherPayloadContext context) {
        Player playerEntity = context.player();
        if (playerEntity.level().getServer() != null
            && playerEntity.level().getEntity(payload.playerID()) instanceof ServerPlayer serverPlayer
            && playerEntity.containerMenu instanceof LoreBookMenu menu) {
            Aether.LOGGER.info("Book of Lore server packet: player='{}', payloadStack={}, exists={}, menuStack={}",
                    serverPlayer.getScoreboardName(), LoreBookMenu.describeStack(payload.itemStack()), payload.exists(),
                    LoreBookMenu.describeStack(menu.getSlot(0).getItem()));
            menu.setLoreEntryExists(payload.exists());

            if (payload.exists() && !payload.itemStack().isEmpty()) {
                ItemStack current = menu.getSlot(0).getItem();
                if (ItemStack.isSameItemSameComponents(current, payload.itemStack())) {
                    Aether.LOGGER.info("Book of Lore advancement trigger accepted for player='{}' with {}",
                            serverPlayer.getScoreboardName(), LoreBookMenu.describeStack(payload.itemStack()));
                    AetherAdvancementTriggers.LORE_ENTRY.get().trigger(serverPlayer, payload.itemStack());
                } else {
                    Aether.LOGGER.warn("Book of Lore advancement trigger rejected because menu stack {} did not match payload {}",
                            LoreBookMenu.describeStack(current), LoreBookMenu.describeStack(payload.itemStack()));
                }
            }
        } else {
            Aether.LOGGER.warn("Book of Lore server packet could not be applied: sender='{}', payloadPlayerId={}, stack={}, exists={}",
                    playerEntity.getScoreboardName(), payload.playerID(), LoreBookMenu.describeStack(payload.itemStack()), payload.exists());
        }
    }
}
