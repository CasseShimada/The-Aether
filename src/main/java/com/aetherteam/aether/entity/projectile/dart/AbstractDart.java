package com.aetherteam.aether.entity.projectile.dart;

import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class AbstractDart extends AbstractArrow {
    private int ticksInAir = 0;

    protected AbstractDart(EntityType<? extends AbstractDart> type, Level level) {
        super(type, level);
    }

    public AbstractDart(EntityType<? extends AbstractDart> type, Level level, LivingEntity shooter, ItemStack pickupItem, @Nullable ItemStack firedFromWeapon) {
        super(type, shooter, level, pickupItem, firedFromWeapon);
    }

    public AbstractDart(EntityType<? extends AbstractDart> type, Level level, LivingEntity shooter, Supplier<Item> pickupItem, @Nullable ItemStack firedFromWeapon) {
        super(type, shooter, level, new ItemStack(pickupItem.get()), firedFromWeapon);
    }

    public AbstractDart(EntityType<? extends AbstractDart> entityType, double x, double y, double z, Level level, ItemStack itemStack, ItemStack firedFromWeapon) {
        super(entityType, x, y, z, level, itemStack, firedFromWeapon);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.onGround()) {
            ++this.ticksInAir;
        }
        if (this.ticksInAir > 500) {
            if (!this.level().isClientSide()) {
                this.discard();
            }
        }
        if (this.isInLiquid()) {
            this.setNoGravity(false);
        }
    }

    /**
     * Handles shield damaging when this projectile hits an entity.
     *
     * @param result The {@link HitResult} of the projectile.
     */
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            if (entity instanceof Player player && player.isBlocking()) {
                player.getUseItem().hurtAndBreak(3, player, player.getUsedItemHand());
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.setNoGravity(false); // Restores gravity to the dart when it hits an entity.
    }

    /**
     * Restores gravity to the dart when it hits a block.
     *
     * @param result The {@link BlockHitResult} of the projectile.
     */
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.setNoGravity(false);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return AetherSoundEvents.ENTITY_DART_HIT.get();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("TicksInAir", this.ticksInAir);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.ticksInAir = input.getIntOr("TicksInAir", this.ticksInAir);
    }
}
