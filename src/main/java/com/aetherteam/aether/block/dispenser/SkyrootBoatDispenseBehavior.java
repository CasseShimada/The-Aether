package com.aetherteam.aether.block.dispenser;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.miscellaneous.SkyrootBoat;
import com.aetherteam.aether.entity.miscellaneous.SkyrootChestBoat;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;

public class SkyrootBoatDispenseBehavior extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior;
    private final boolean isChestBoat;

    public SkyrootBoatDispenseBehavior() {
        this(false);
    }

    public SkyrootBoatDispenseBehavior(boolean p_235892_) {
        this.defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
        this.isChestBoat = p_235892_;
    }

    public ItemStack execute(BlockSource source, ItemStack stack) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        ServerLevel serverLevel = source.level();
        Vec3 vec3 = source.center();
        double d0 = 0.5625 + (double) AetherEntityTypes.SKYROOT_BOAT.getWidth() / 2.0;
        double d1 = vec3.x() + (double) direction.getStepX() * d0;
        double d2 = vec3.y() + (double) ((float) direction.getStepY() * 1.125F);
        double d3 = vec3.z() + (double) direction.getStepZ() * d0;
        BlockPos blockpos = source.pos().relative(direction);
        AbstractBoat boat = this.isChestBoat ? new SkyrootChestBoat(serverLevel, d1, d2, d3) : new SkyrootBoat(serverLevel, d1, d2, d3);
        EntityType.createDefaultStackConfig(serverLevel, stack, null).apply(boat);
        boat.setYRot(direction.toYRot());
        double d4;
        if (serverLevel.getFluidState(blockpos).is(FluidTags.WATER)) {
            d4 = 1.0;
        } else {
            if (!serverLevel.getBlockState(blockpos).isAir() || !serverLevel.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(source, stack);
            }

            d4 = 0.0;
        }

        boat.setPos(d1, d2 + d4, d3);
        serverLevel.addFreshEntity(boat);
        stack.shrink(1);
        return stack;
    }

    protected void playSound(BlockSource source) {
        source.level().levelEvent(1000, source.pos(), 0);
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(AetherItems.SKYROOT_BOAT.get(), new SkyrootBoatDispenseBehavior());
        DispenserBlock.registerBehavior(AetherItems.SKYROOT_CHEST_BOAT.get(), new SkyrootBoatDispenseBehavior(true));
    }
}
