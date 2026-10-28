package com.darksune.althera.client.seal;

import com.darksune.althera.Althera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SealTattoos {

    public static final int GLOW_TICKS = 30;
    private static final int RISE_TICKS = 4;
    private static final int FADE_TICKS = 10;

    private static final Map<UUID, Tattoo> TATTOOS = new HashMap<>();

    private SealTattoos() {}

    public record Tattoo(int tattoo, int color, long glowStart) {

        public ResourceLocation texture() {
            return ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID,
                    "textures/overlay/player/seal/seal_tattoo_" + tattoo + ".png");
        }

        public float glow(float partialTick) {
            final float elapsed = elapsedTicks(glowStart, partialTick);
            if (elapsed < 0.0F || elapsed >= GLOW_TICKS) return 0.0F;

            final float rise = Mth.clamp(elapsed / RISE_TICKS, 0.0F, 1.0F);
            final float fade = Mth.clamp((GLOW_TICKS - elapsed) / FADE_TICKS, 0.0F, 1.0F);
            final float pulse = 0.8F + 0.2F * Mth.sin(elapsed * 0.6F);
            return rise * fade * pulse;
        }

        public float raise(float partialTick) {
            final float elapsed = elapsedTicks(glowStart, partialTick);
            if (elapsed < 0.0F || elapsed >= GLOW_TICKS) return 0.0F;

            final float up = Mth.clamp(elapsed / RISE_TICKS, 0.0F, 1.0F);
            final float down = Mth.clamp((GLOW_TICKS - elapsed) / RISE_TICKS, 0.0F, 1.0F);
            return Mth.sin(Math.min(up, down) * Mth.HALF_PI);
        }
    }

    public static void update(UUID playerId, int tattoo, int color, boolean cast) {
        final Tattoo previous = TATTOOS.get(playerId);
        final long glowStart = cast ? gameTime() : previous != null ? previous.glowStart : Long.MIN_VALUE / 2;
        TATTOOS.put(playerId, new Tattoo(tattoo, color, glowStart));
    }

    @Nullable
    public static Tattoo get(Player player) {
        return TATTOOS.get(player.getUUID());
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        TATTOOS.clear();
    }

    private static float elapsedTicks(long start, float partialTick) {
        return gameTime() - start + partialTick;
    }

    private static long gameTime() {
        final var level = Minecraft.getInstance().level;
        return level != null ? level.getGameTime() : 0L;
    }
}
