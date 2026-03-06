package com.aetherteam.aether.event;

import net.minecraft.world.entity.Entity;

/**
 * ValkyrieTeleportEvent is fired before a Valkyrie teleports.
 * <br>
 * This event is cancellable.<br>
 * If the event is not canceled, the entity will be teleported.
 * <br>
 * This event is fired by Aether's local event dispatch.<br>
 * <br>
 * This event is only fired on the server side.<br>
 * <br>
 * If this event is canceled, the entity will not be teleported.
 */
public class ValkyrieTeleportEvent {
    private final Entity entity;
    private boolean canceled;
    private double targetX;
    private double targetY;
    private double targetZ;

    public ValkyrieTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
        this.entity = entity;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public double getTargetX() {
        return this.targetX;
    }

    public void setTargetX(double targetX) {
        this.targetX = targetX;
    }

    public double getTargetY() {
        return this.targetY;
    }

    public void setTargetY(double targetY) {
        this.targetY = targetY;
    }

    public double getTargetZ() {
        return this.targetZ;
    }

    public void setTargetZ(double targetZ) {
        this.targetZ = targetZ;
    }
}
