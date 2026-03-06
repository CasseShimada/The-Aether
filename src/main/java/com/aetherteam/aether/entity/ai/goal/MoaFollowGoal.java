package com.aetherteam.aether.entity.ai.goal;

import com.aetherteam.aether.entity.passive.Moa;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * [CODE COPY] - {@link TemptGoal}.
 * Modified to handle following a player by checking {@link Moa#getFollowing()} instead of checking for a temptation item.
 */
public class MoaFollowGoal extends TemptGoal {
    private final Moa moa;
    private final double speedModifier;
    @Nullable
    private Player player;
    private int calmDown;
    private boolean isRunning;

    public MoaFollowGoal(Moa moa, double speedModifier) {
        super(moa, speedModifier, (livingEntity) -> false, false);
        this.moa = moa;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.player = this.moa.getFollowing() == null ? null : this.moa.level().getPlayerInAnyDimension(this.moa.getFollowing());
            if (this.player != null && this.player.level() != this.moa.level()) {
                this.player = null;
            }
            if (this.player != null) {
                if (this.moa.distanceToSqr(this.player) >= 6.25) {
                    this.mob.getMoveControl().setWantedPosition(this.player.getX(), this.player.getY(), this.player.getZ(), this.speedModifier);
                }
            }
            return this.player != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.player = null;
        this.moa.getNavigation().stop();
        this.calmDown = reducedTickDelay(100);
        this.isRunning = false;
    }

    @Override
    public void tick() {
        if (this.player != null) {
            this.moa.getLookControl().setLookAt(this.player, (float) (this.moa.getMaxHeadYRot() + 20), (float) this.moa.getMaxHeadXRot());
            if (this.moa.distanceToSqr(this.player) < 6.25) {
                this.moa.getNavigation().stop();
            } else {
                this.moa.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }
}
