package com.aetherteam.aether.util;

import net.minecraft.world.phys.Vec3;

public final class EntityMotionUtil {
    private static final double DEFAULT_GROUNDED_Y_MOVEMENT = -0.0784000015258789D;

    private EntityMotionUtil() {
    }

    public static boolean isStationary(Vec3 motion) {
        return motion.x() == 0.0 && (motion.y() == DEFAULT_GROUNDED_Y_MOVEMENT || motion.y() == 0.0) && motion.z() == 0.0;
    }
}
