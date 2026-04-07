package com.darksune.althera;

import com.darksune.althera.common.registry.AltheraRegistries;
import com.darksune.althera.data.config.AltheraConfig;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.UUID;

@EventBusSubscriber
@Mod(Althera.MOD_ID)
public final class Althera {

    public static final String MOD_ID = "althera";

    public Althera(final IEventBus modEventBus, final ModContainer modContainer) {
        AltheraRegistries.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, AltheraConfig.SPEC);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;

        Level level = zombie.level();
        if (level.isClientSide) return;

        if (!zombie.getTags().contains("friendly_summon")) return;

        // ⏱️ cooldown (a cada 2 segundos)
        if (zombie.tickCount % 40 != 0) return;

        // 🧠 pega dono
        if (!zombie.getPersistentData().hasUUID("owner")) return;

        UUID ownerId = zombie.getPersistentData().getUUID("owner");
        Player owner = level.getPlayerByUUID(ownerId);

        if (owner == null) return;

        double distance = zombie.distanceTo(owner);

        // 📏 só teleporta se estiver longe
        if (distance > 30) { // pode trocar pra 50 se quiser
            zombie.teleportTo(
                    owner.getX() + (level.getRandom().nextDouble() - 0.5) * 2,
                    owner.getY(),
                    owner.getZ() + (level.getRandom().nextDouble() - 0.5) * 2
            );
        }
    }
}
