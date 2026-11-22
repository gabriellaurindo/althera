package com.darksune.althera.client.particle;

import com.darksune.althera.common.particle.TintedParticleOptions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FlatParticle extends TextureSheetParticle {

    private static final int FULL_BRIGHT = 0xF000F0;

    private static final float RUNE_SPIN = 0.05F;

    private static final float FADE_SHARE = 0.25F;

    private final boolean expanding;

    private final float size;

    protected FlatParticle(ClientLevel level, double x, double y, double z, TintedParticleOptions options,
                           SpriteSet sprites, boolean expanding) {
        super(level, x, y, z);
        this.expanding = expanding;
        this.size = options.scale();
        this.lifetime = options.lifetime();
        this.rCol = options.red();
        this.gCol = options.green();
        this.bCol = options.blue();
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.roll = expanding ? 0.0F : random.nextFloat() * Mth.TWO_PI;
        this.oRoll = roll;
        pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        oRoll = roll;

        if (!expanding) {
            roll += RUNE_SPIN;
        }
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {

        float life = (age + partialTick) / lifetime;
        float half;
        float fade;

        if (expanding) {
            half = size * (0.15F + 0.85F * (1.0F - (1.0F - life) * (1.0F - life)));
            fade = 1.0F - life;
        } else {
            float grow = Math.min(1.0F, life / FADE_SHARE);
            half = size * (0.6F + 0.4F * grow);
            fade = life > 1.0F - FADE_SHARE ? (1.0F - life) / FADE_SHARE : Math.min(1.0F, life / FADE_SHARE);
        }

        Vec3 cam = camera.getPosition();
        float x = (float) (Mth.lerp(partialTick, xo, this.x) - cam.x);
        float y = (float) (Mth.lerp(partialTick, yo, this.y) - cam.y);
        float z = (float) (Mth.lerp(partialTick, zo, this.z) - cam.z);
        float angle = Mth.lerp(partialTick, oRoll, roll);
        float cos = Mth.cos(angle) * half;
        float sin = Mth.sin(angle) * half;
        float a = Mth.clamp(fade, 0.0F, 1.0F) * alpha;

        float[][] corners = {{-cos + sin, -sin - cos}, {-cos - sin, -sin + cos}, {cos - sin, sin + cos}, {cos + sin, sin - cos}};
        float[][] uv = {{getU0(), getV0()}, {getU0(), getV1()}, {getU1(), getV1()}, {getU1(), getV0()}};

        for (int i = 0; i < 4; i++) {
            vertex(consumer, x + corners[i][0], y, z + corners[i][1], uv[i][0], uv[i][1], a);
        }

        for (int i = 3; i >= 0; i--) {
            vertex(consumer, x + corners[i][0], y, z + corners[i][1], uv[i][0], uv[i][1], a);
        }
    }

    private void vertex(VertexConsumer consumer, float x, float y, float z, float u, float v, float alpha) {
        consumer.addVertex(x, y, z).setUv(u, v).setColor(rCol, gCol, bCol, alpha).setLight(FULL_BRIGHT);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites, boolean expanding) implements ParticleProvider<TintedParticleOptions> {

        @Override
        public Particle createParticle(TintedParticleOptions options, ClientLevel level, double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new FlatParticle(level, x, y, z, options, sprites, expanding);
        }
    }
}
