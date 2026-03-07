package com.aetherteam.aether.entity.ai.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A path navigator that doesn't require the entity to be on the ground to update the path.
 */
public class FallPathNavigation extends GroundPathNavigation {
    private static final int MAX_SAFE_GROUND_DROP = 1;

    public FallPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    /**
     * [CODE COPY] - {@link PathNavigation#followThePath()}.
     * Modified to prevent spinning.
     */
    @Override
    protected void followThePath() {
        if (this.path == null || this.path.isDone()) {
            return;
        }

        Vec3 vec3 = this.getTempMobPos();
        Vec3i vec3i = this.path.getNextNodePos();
        if (this.mob.onGround() && !this.isPathSegmentSafe(vec3, vec3i, MAX_SAFE_GROUND_DROP)) {
            this.stop();
            return;
        }

        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
        double d0 = Math.abs(this.mob.getX() - ((double) vec3i.getX() + (this.mob.getBbWidth() + 1) / 2D));
        double d1 = Math.abs(this.mob.getY() - (double) vec3i.getY());
        double d2 = Math.abs(this.mob.getZ() - ((double) vec3i.getZ() + (this.mob.getBbWidth() + 1) / 2D));

        // Keep the airborne-pathing threshold behavior aligned with the 1.21.1 baseline.
        float fallDistance = this.mob.getMaxFallDistance();
        boolean flag = d0 <= (double) this.maxDistanceToWaypoint && d2 <= (double) this.maxDistanceToWaypoint && d1 < fallDistance;
        if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
            this.path.advance();
        }

        this.doStuckDetection(vec3);
    }

    private boolean isPathSegmentSafe(Vec3 currentPos, Vec3i nextNodePos, int maxDrop) {
        int currentY = Mth.floor(currentPos.y());
        if (nextNodePos.getY() < currentY - maxDrop) {
            return false;
        }

        if (!this.hasSafeSupport(nextNodePos.getX(), nextNodePos.getY(), nextNodePos.getZ(), maxDrop)) {
            return false;
        }

        double nextCenterX = nextNodePos.getX() + 0.5D;
        double nextCenterZ = nextNodePos.getZ() + 0.5D;
        double dx = nextCenterX - currentPos.x();
        double dz = nextCenterZ - currentPos.z();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance <= 0.001D) {
            return true;
        }

        int steps = Math.max(1, Mth.ceil(horizontalDistance / 0.5D));
        int sampleY = Mth.floor(Math.max(currentPos.y(), nextNodePos.getY()));
        for (int i = 1; i <= steps; i++) {
            double progress = (double) i / (double) steps;
            int sampleX = Mth.floor(Mth.lerp(progress, currentPos.x(), nextCenterX));
            int sampleZ = Mth.floor(Mth.lerp(progress, currentPos.z(), nextCenterZ));
            if (!this.hasSafeSupport(sampleX, sampleY, sampleZ, maxDrop)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasSafeSupport(int x, int y, int z, int maxDrop) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y - 1, z);
        for (int i = 0; i <= maxDrop && mutable.getY() >= this.level.getMinY(); i++) {
            if (!this.level.getFluidState(mutable).isEmpty() || !this.level.getBlockState(mutable).getCollisionShape(this.level, mutable).isEmpty()) {
                return true;
            }
            mutable.move(0, -1, 0);
        }
        return false;
    }

    /**
     * [CODE COPY] - {@link net.minecraft.world.entity.ai.navigation.PathNavigation#shouldTargetNextNodeInDirection(Vec3)}.
     */
    private boolean shouldTargetNextNodeInDirection(Vec3 pVec) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 vec3 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!pVec.closerThan(vec3, 2.0)) {
                return false;
            } else if (this.canMoveDirectly(pVec, this.path.getNextEntityPos(this.mob))) {
                return true;
            } else {
                Vec3 vec31 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vec32 = vec3.subtract(pVec);
                Vec3 vec33 = vec31.subtract(pVec);
                double d0 = vec32.lengthSqr();
                double d1 = vec33.lengthSqr();
                boolean flag = d1 < d0;
                boolean flag1 = d0 < 0.5;
                if (!flag && !flag1) {
                    return false;
                } else {
                    Vec3 vec34 = vec32.normalize();
                    Vec3 vec35 = vec33.normalize();
                    return vec35.dot(vec34) < 0.0;
                }
            }
        }
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }
}
