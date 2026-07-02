package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.entity.projectile.crystal.ThunderCrystal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;

public final class EntityLightningHooks {
    private EntityLightningHooks() {
    }

    /**
     * Prevents lightning from damaging dungeon keys.
     *
     * @param entity The {@link Entity}.
     * @return Whether lightning hit a key item, as a {@link Boolean}.
     */
    public static boolean lightningHitKeys(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return itemEntity.getItem().is(AetherTags.Items.DUNGEON_KEYS);
        } else {
            return false;
        }
    }

    /**
     * Prevents lightning summoned by Thunder Crystals from damaging items.
     *
     * @param entity    The {@link Entity} struck by the lightning bolt.
     * @param lightning The {@link LightningBolt} that struck the entity.
     * @return Whether the lightning was from a {@link ThunderCrystal} and hit an item, as a {@link Boolean}.
     */
    public static boolean thunderCrystalHitItems(Entity entity, LightningBolt lightning) {
        if (entity instanceof ItemEntity) {
            if (lightning.hasAttached(AetherDataAttachments.LIGHTNING_TRACKER)) {
                return lightning.getAttachedOrCreate(AetherDataAttachments.LIGHTNING_TRACKER).getOwner(lightning.level()) instanceof ValkyrieQueen;
            }
        }
        return false;
    }
}
