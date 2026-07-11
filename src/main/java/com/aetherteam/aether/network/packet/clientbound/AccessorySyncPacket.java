package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Synchronizes Aether accessory slot state from the server to clients.
 */
public record AccessorySyncPacket(int entityId, List<AccessorySyncPacket.SlotData> slots) implements CustomPacketPayload {
    public static final Type<AccessorySyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AccessorySyncPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AccessorySyncPacket decode(RegistryFriendlyByteBuf buf) {
            int entityId = buf.readVarInt();
            int size = buf.readVarInt();
            List<SlotData> slots = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                String slotName = buf.readUtf();
                List<ItemStack> equipped = readStackList(buf);
                List<ItemStack> cosmetic = readStackList(buf);

                int renderSize = buf.readVarInt();
                boolean[] renderFlags = new boolean[renderSize];
                for (int flag = 0; flag < renderSize; flag++) {
                    renderFlags[flag] = buf.readBoolean();
                }

                slots.add(new SlotData(slotName, equipped, cosmetic, renderFlags));
            }
            return new AccessorySyncPacket(entityId, slots);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, AccessorySyncPacket value) {
            buf.writeVarInt(value.entityId());
            buf.writeVarInt(value.slots().size());
            for (SlotData slot : value.slots()) {
                buf.writeUtf(slot.slotName());
                writeStackList(buf, slot.equipped());
                writeStackList(buf, slot.cosmetic());

                buf.writeVarInt(slot.renderFlags().length);
                for (boolean flag : slot.renderFlags()) {
                    buf.writeBoolean(flag);
                }
            }
        }

        private List<ItemStack> readStackList(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<ItemStack> stacks = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                stacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
            }
            return stacks;
        }

        private void writeStackList(RegistryFriendlyByteBuf buf, List<ItemStack> stacks) {
            buf.writeVarInt(stacks.size());
            for (ItemStack stack : stacks) {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
            }
        }
    };

    public AccessorySyncPacket {
        slots = List.copyOf(slots);
    }

    @Override
    public Type<AccessorySyncPacket> type() {
        return TYPE;
    }

    public static void execute(AccessorySyncPacket payload, @Nullable Player contextPlayer) {
        if (contextPlayer == null) {
            return;
        }

        Entity entity = contextPlayer.level().getEntity(payload.entityId());
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(livingEntity);
        if (accessories != null) {
            accessories.applyClientSync(payload);
        }
    }

    public record SlotData(String slotName, List<ItemStack> equipped, List<ItemStack> cosmetic, boolean[] renderFlags) {
        public SlotData {
            equipped = copyStacks(equipped);
            cosmetic = copyStacks(cosmetic);
            renderFlags = Arrays.copyOf(renderFlags, renderFlags.length);
        }

        private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
            List<ItemStack> copy = new ArrayList<>(stacks.size());
            for (ItemStack stack : stacks) {
                copy.add(stack.copy());
            }
            return List.copyOf(copy);
        }
    }
}
