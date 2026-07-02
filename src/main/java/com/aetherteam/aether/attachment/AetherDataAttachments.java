package com.aetherteam.aether.attachment;

import com.aetherteam.aether.Aether;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

public final class AetherDataAttachments {
    public static final AttachmentType<AetherPlayerAttachment> AETHER_PLAYER = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "aether_player"),
            builder -> builder.initializer(AetherPlayerAttachment::new).persistent(AetherPlayerAttachment.CODEC).copyOnDeath());

    public static final AttachmentType<MobAccessoryAttachment> MOB_ACCESSORY = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "mob_accessory"),
            builder -> builder.initializer(MobAccessoryAttachment::new).persistent(MobAccessoryAttachment.CODEC).copyOnDeath());

    public static final AttachmentType<AccessoryInventoryAttachment> ACCESSORY_INVENTORY = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_inventory"),
            builder -> builder.initializer(AccessoryInventoryAttachment::new).persistent(AccessoryInventoryAttachment.CODEC).copyOnDeath());

    public static final AttachmentType<PhoenixArrowAttachment> PHOENIX_ARROW = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "phoenix_arrow"),
            builder -> builder.initializer(PhoenixArrowAttachment::new).persistent(PhoenixArrowAttachment.CODEC));

    public static final AttachmentType<LightningTrackerAttachment> LIGHTNING_TRACKER = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "lightning_tracker"),
            builder -> builder.initializer(LightningTrackerAttachment::new).persistent(LightningTrackerAttachment.CODEC));

    public static final AttachmentType<DroppedItemAttachment> DROPPED_ITEM = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "dropped_item"),
            builder -> builder.initializer(DroppedItemAttachment::new).persistent(DroppedItemAttachment.CODEC));

    public static final AttachmentType<AetherTimeAttachment> AETHER_TIME = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Aether.MODID, "aether_time"),
            builder -> builder.initializer(AetherTimeAttachment::new).persistent(AetherTimeAttachment.CODEC));

    private AetherDataAttachments() {
    }

    /**
     * Triggers static attachment registration from the mod initializer.
     */
    public static void bootstrap() {
    }
}
