package com.aetherteam.aether.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FrozenParticle extends SingleQuadParticle {
    private final SpriteSet animatedSprite;
    private final float snowDigParticleScale;

    public FrozenParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        this(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, 1.0F, sprite);
    }

    public FrozenParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, float scale, SpriteSet sprite) {
        super(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, sprite.get(level.random));
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.xd += xSpeed;
        this.yd += ySpeed;
        this.zd += zSpeed;
        float f = 1.0F - (float) (Math.random() * 0.3);
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize *= 0.75F;
        this.quadSize *= scale;
        this.snowDigParticleScale = this.quadSize;
        this.lifetime = (int) (8.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime = (int) ((float) this.lifetime * scale);
        this.animatedSprite = sprite;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float f = ((float) this.age + partialTicks) / (float) this.lifetime * 32.0F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        return this.snowDigParticleScale * f;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.setSpriteFromAge(this.animatedSprite);

        this.yd -= 0.03;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.99;
        this.yd *= 0.99;
        this.zd *= 0.99;

        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    public record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource randomSource) {
            return new FrozenParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet());
        }
    }
}
