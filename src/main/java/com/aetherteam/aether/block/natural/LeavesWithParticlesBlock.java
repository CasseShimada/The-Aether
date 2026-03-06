package com.aetherteam.aether.block.natural;

import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Supplier;

public class LeavesWithParticlesBlock extends LeavesBlock {
    private final Supplier<? extends ParticleOptions> particle;
    private final MapCodec<? extends LeavesBlock> codec = MapCodec.unit(this);

    public LeavesWithParticlesBlock(Supplier<? extends ParticleOptions> particle, Properties properties) {
        super(0.01F, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
        this.particle = particle;
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return this.codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AetherBlockStateProperties.DOUBLE_DROPS);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (level.isClientSide()) {
            if (random.nextInt(10) == 0) {
                for (int i = 0; i < 15; i++) {
                    double x = pos.getX() + (random.nextFloat() - 0.5) * 8.0;
                    double y = pos.getY() + (random.nextFloat() - 0.5) * 8.0;
                    double z = pos.getZ() + (random.nextFloat() - 0.5) * 8.0;
                    double dx = (random.nextFloat() - 0.5) * 0.5;
                    double dy = (random.nextFloat() - 0.5) * 0.5;
                    double dz = (random.nextFloat() - 0.5) * 0.5;
                    level.addParticle(this.particle.get(), x, y, z, dx, dy, dz);
                }
            }
        }
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - 0.05;
        double z = pos.getZ() + random.nextDouble();
        level.addParticle(this.particle.get(), x, y, z, 0.0, 0.0, 0.0);
    }
}
