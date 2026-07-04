package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.packet.PhoenixArrowSyncPacket;
import com.aetherteam.aether.network.packet.SyncPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

/**
 * Fabric attachment state storing whether a shot {@link AbstractArrow} was fired from a Phoenix Bow. This attachment works for all arrow types.
 *
 * @see com.aetherteam.aether.event.hooks.WeaponAbilityHooks#phoenixArrowHit(HitResult, Projectile)
 * @see com.aetherteam.aether.mixin.mixins.common.AbstractArrowMixin
 * @see com.aetherteam.aether.mixin.mixins.client.TippableArrowRendererMixin
 */
public class PhoenixArrowAttachment implements AttachmentSyncable {
    private boolean isPhoenixArrow;
    private int fireTime;

    /**
     * Stores the following methods as able to be synced between client and server and vice-versa.
     */
    private final Map<String, SyncField> syncFields = Map.ofEntries(
            Map.entry("setPhoenixArrow", new SyncField(Type.BOOLEAN, (object) -> this.setPhoenixArrow((boolean) object), this::isPhoenixArrow))
    );

    public static final Codec<PhoenixArrowAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("is_phoenix_arrow").forGetter(PhoenixArrowAttachment::isPhoenixArrow),
            Codec.INT.fieldOf("fire_time").forGetter(PhoenixArrowAttachment::getFireTime)
    ).apply(instance, PhoenixArrowAttachment::new));

    public PhoenixArrowAttachment() {
        this(false, 0);
    }

    private PhoenixArrowAttachment(boolean isPhoenixArrow, int fireTime) {
        this.setPhoenixArrow(isPhoenixArrow);
        this.setFireTime(fireTime);
    }

    @Override
    public Map<String, SyncField> getSyncFields() {
        return this.syncFields;
    }

    public void setPhoenixArrow(boolean isPhoenixArrow) {
        this.isPhoenixArrow = isPhoenixArrow;
    }

    /**
     * @return Whether an arrow is a Phoenix Arrow, as a {@link Boolean}.
     */
    public boolean isPhoenixArrow() {
        return this.isPhoenixArrow;
    }

    public void setFireTime(int time) {
        this.fireTime = time;
    }

    /**
     * @return How many ticks an entity shot by a Phoenix Arrow should stay on fire, as an {@link Integer}.
     */
    public int getFireTime() {
        return this.fireTime;
    }

    @Override
    public SyncPacket<?> getSyncPacket(int entityID, String key, Type type, Object value) {
        return new PhoenixArrowSyncPacket(entityID, key, type, value);
    }
}
