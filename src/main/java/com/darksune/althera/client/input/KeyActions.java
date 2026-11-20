package com.darksune.althera.client.input;

import com.darksune.althera.client.hud.SpellSlots;
import com.darksune.althera.client.screen.HeroScreen;
import com.darksune.althera.client.screen.SealWheelScreen;
import com.darksune.althera.client.screen.SpellTreeScreen;
import com.darksune.althera.client.screen.SpellWheelScreen;
import com.darksune.althera.common.commandseal.skill.CommandSealSkillType;
import com.darksune.althera.common.item.SpellBookItem;
import com.darksune.althera.common.spell.PlayerSpellData;
import com.darksune.althera.common.system.HeroCommandSystem;
import com.darksune.althera.network.packet.DisguisePacket;
import com.darksune.althera.network.packet.MarkTargetPacket;
import com.darksune.althera.network.packet.SelectSpellSlotPacket;
import com.darksune.althera.network.packet.SummonPacket;
import com.darksune.althera.network.packet.UseCommandSealSkillPacket;
import com.darksune.althera.network.packet.UseUltimateSkillPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
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
        if (mc.screen == null && mc.player != null) {
            mc.setScreen(new HeroScreen());
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

    public static void handleSpellWheel() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && mc.player != null && !SpellBookItem.isCastingWith(mc.player)) {
            mc.setScreen(new SpellWheelScreen());
        }
    }

    public static void handleSpellTree() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && mc.player != null && !SpellBookItem.isCastingWith(mc.player)) {
            mc.setScreen(new SpellTreeScreen());
        }
    }

    public static void cycleSpell(final int direction) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        selectSpellSlot(PlayerSpellData.get(mc.player).nextFilledSlot(direction));
    }

    public static boolean handleSpellScroll(final double delta) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (delta == 0.0D || mc.screen != null || player == null || !SpellBookItem.isHeldBy(player)
                || !AltheraKeybinds.isHeld(AltheraKeybinds.SPELL_SCROLL)) {
            return false;
        }

        cycleSpell(delta > 0.0D ? -1 : 1);
        return true;
    }

    public static void selectSpellSlot(final int slot) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || SpellBookItem.isCastingWith(player)) return;

        PlayerSpellData data = PlayerSpellData.get(player);
        data.setSelectedSlot(slot);
        mc.getConnection().send(new SelectSpellSlotPacket(slot));

        mc.gui.setOverlayMessage(Component.literal((slot + 1) + " · ").withStyle(ChatFormatting.GRAY)
                .append(SpellSlots.name(data, slot).copy().withStyle(ChatFormatting.LIGHT_PURPLE)), false);
    }
}
