package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.world.entity.LivingEntity;

public final class ArmorAbilityHooks {
    private ArmorAbilityHooks() {
    }

    /**
     * Cancels fall damage if the wearer either has Sentry Boots, a full Gravitite Armor set, or a full Valkyrie Armor set.
     *
     * @param entity The {@link LivingEntity} wearing the armor.
     * @return Whether the wearer's fall damage should be cancelled, as a {@link Boolean}.
     */
    public static boolean fallCancellation(LivingEntity entity) {
        return EquipmentUtil.hasSentryBoots(entity) || EquipmentUtil.hasFullGravititeSet(entity) || EquipmentUtil.hasFullValkyrieSet(entity);
    }
}
