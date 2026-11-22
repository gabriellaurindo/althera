package com.darksune.althera.client.particle;

import com.darksune.althera.common.particle.TintedParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class TintedParticle extends TextureSheetParticle {

    private static final int FULL_BRIGHT = 0xF000F0;

    public enum Motion {
        GLOW,
        EMBER,
        FROST,
        SPARK
    }

    private final SpriteSet sprites;

    private final Motion motion;

    private final float baseSize;

    private final float spin;

    protected TintedParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
                             TintedParticleOptions options, SpriteSet sprites, Motion motion) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.motion = motion;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.lifetime = Math.max(1, Math.round(options.lifetime() * (0.8F + random.nextFloat() * 0.4F)));
        this.baseSize = 0.1F * options.scale() * (0.85F + random.nextFloat() * 0.3F);
        this.quadSize = baseSize;
        this.rCol = options.red();
        this.gCol = options.green();
        this.bCol = options.blue();
        this.hasPhysics = false;
        this.friction = motion == Motion.SPARK ? 0.7F : 0.92F;
        this.gravity = motion == Motion.FROST ? 0.015F : motion == Motion.EMBER ? -0.01F : 0.0F;
        this.spin = motion == Motion.FROST ? (random.nextFloat() - 0.5F) * 0.2F : 0.0F;
        this.roll = random.nextFloat() * Mth.TWO_PI;
        this.oRoll = roll;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        oRoll = roll;
        roll += spin;

        float life = (float) age / lifetime;
        alpha = motion == Motion.SPARK ? (random.nextFloat() < 0.3F ? 0.4F : 1.0F) * (1.0F - life) : 1.0F - life * life;
        quadSize = switch (motion) {
            case EMBER -> baseSize * (1.0F - life * 0.7F);
            case SPARK -> baseSize * (1.0F - life * 0.5F);
            default -> baseSize * (0.6F + 0.4F * Mth.sin(Mth.PI * Math.min(1.0F, life * 2.0F + 0.5F)));
        };

        if (motion == Motion.SPARK) {
            xd += (random.nextDouble() - 0.5D) * 0.02D;
            yd += (random.nextDouble() - 0.5D) * 0.02D;
            zd += (random.nextDouble() - 0.5D) * 0.02D;
        }

        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    public record Provider(SpriteSet sprites, Motion motion) implements ParticleProvider<TintedParticleOptions> {

        @Override
        public Particle createParticle(TintedParticleOptions options, ClientLevel level, double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new TintedParticle(level, x, y, z, vx, vy, vz, options, sprites, motion);
        }
    }
}
