package com.aetherteam.aether.entity;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Lightning damage rules for Aether entities and items.
 */
public final class AetherLightningRules {
    private AetherLightningRules() {
    }

    public static boolean preventsItemDamage(Entity entity, ServerLevel level, LightningBolt lightningBolt) {
        if (!(entity instanceof ItemEntity itemEntity)) {
            return false;
        }
        return itemEntity.getItem().is(AetherTags.Items.DUNGEON_KEYS)
                || isOwnedByValkyrieQueen(level, lightningBolt);
    }

    private static boolean isOwnedByValkyrieQueen(ServerLevel level, LightningBolt lightningBolt) {
        return lightningBolt.hasAttached(AetherDataAttachments.LIGHTNING_TRACKER)
                && lightningBolt.getAttachedOrCreate(AetherDataAttachments.LIGHTNING_TRACKER).getOwner(level) instanceof ValkyrieQueen;
    }
}
