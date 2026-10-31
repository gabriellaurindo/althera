package com.darksune.althera.client.input;

import com.darksune.althera.client.screen.HeroScreen;
import com.darksune.althera.client.screen.HeroSelectionOpener;
import com.darksune.althera.client.screen.SealWheelScreen;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.system.HeroCommandSystem;
import com.darksune.althera.network.packet.DisguisePacket;
import com.darksune.althera.network.packet.MarkTargetPacket;
import com.darksune.althera.network.packet.SummonPacket;
import com.darksune.althera.network.packet.UseCommandSealSkillPacket;
import com.darksune.althera.network.packet.UseUltimateSkillPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class KeyActions {

    public static void handleSummon() {
        Minecraft.getInstance().getConnection().send(new SummonPacket());
    }

    public static void handleDisguise() {
        Minecraft.getInstance().getConnection().send(new DisguisePacket());
    }

    public static void handleHero() {
        Minecraft mc = Minecraft.getInstance();
        HeroData heroData = HeroData.get(mc.player);
        if (mc.screen == null && heroData.getHeroDefinition() != null) {
            mc.setScreen(new HeroScreen());
        } else if (mc.screen == null) {
            HeroSelectionOpener.open();
        } else {
            mc.setScreen(null);
        }
    }

    public static void handleSealWheel() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && mc.player != null) {
            mc.setScreen(new SealWheelScreen());
        }
    }

    public static void handleSealTap() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        Entity target = pickTarget(mc.player, HeroCommandSystem.MARK_RANGE);
        if (target != null) {
            mc.getConnection().send(new MarkTargetPacket(target.getId()));
        }
    }

    private static Entity pickTarget(final LocalPlayer player, final double range) {
        Vec3 start = player.getEyePosition();
        HitResult blockHit = player.pick(range, 1.0F, false);
        Vec3 end = blockHit.getType() == HitResult.Type.MISS
                ? start.add(player.getViewVector(1.0F).scale(range))
                : blockHit.getLocation();

        AABB searchBox = player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, start, end, searchBox,
                entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable(),
                start.distanceToSqr(end));

        return entityHit == null ? null : entityHit.getEntity();
    }

    public static void handleCommandSealSkill(final CommandSealSkillType skillType) {
        Minecraft.getInstance().getConnection().send(new UseCommandSealSkillPacket(skillType));
    }

    public static void handleUltimateSkill() {
        Minecraft.getInstance().getConnection().send(new UseUltimateSkillPacket());
    }
}
