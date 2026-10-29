package com.darksune.althera.client.hud;

import com.darksune.althera.Althera;
import com.darksune.althera.common.attachment.HeroData;
import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.HeroEntity;
import com.darksune.althera.common.system.HeroStatsSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@EventBusSubscriber(modid = Althera.MOD_ID, value = Dist.CLIENT)
public final class SummonHudRenderer {

    private static final float PORTRAIT_SCALE = 38.0F;
    private static final float PORTRAIT_YAW = 200.0F;
    private static final float HEAD_CENTER_ABOVE_PIVOT = 4.0F;
    private static final float DEFAULT_HEAD_PIVOT = 24.0F;

    private static final int COLOR_SHADOW = 0x66000000;
    private static final int COLOR_GOLD = 0xFFC9A24A;
    private static final int COLOR_GOLD_DARK = 0xFF6B5321;
    private static final int COLOR_NAVY_TOP = 0xFF22345A;
    private static final int COLOR_NAVY_BOTTOM = 0xFF0C1626;
    private static final int COLOR_HEALTH_TOP = 0xFFFF6B6B;
    private static final int COLOR_HEALTH_BOTTOM = 0xFFB3202A;
    private static final int COLOR_HEALTH_TEXT = 0xFFB8B8;
    private static final int COLOR_LEVEL_TEXT = 0xFFE7C46A;
    private static final int COLOR_INTERVENTION = 0xFF5BE37A;
    private static final int COLOR_INTERVENTION_USED = 0xFF263A2C;
    private static final int COLOR_DIM = 0x66000000;
    private static final int COLOR_DEFEATED = 0xAA3A0000;

    @Nullable
    private static HeroEntity portrait;
    @Nullable
    private static ResourceLocation portraitHeroId;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        final HeroData heroData = HeroData.get(mc.player);

        if (heroData.isHiddenHud() || heroData.getHeroDefinition() == null) return;

        final GuiGraphics gui = event.getGuiGraphics();
        final HeroHudLayout layout = HeroHudLayout.current(mc);

        renderPortraitFrame(gui, mc, heroData, layout);
        renderHealth(gui, mc, heroData, layout);
        renderInterventions(gui, heroData, layout);
    }

    private static void renderPortraitFrame(final GuiGraphics gui, final Minecraft mc, final HeroData heroData,
                                            final HeroHudLayout layout) {
        final int x = layout.portraitX();
        final int y = layout.portraitY();
        final int size = HeroHudLayout.PORTRAIT_SIZE;

        gui.fill(x - 1, y - 1, x + size + 1, y + size + 1, COLOR_SHADOW);
        gui.fill(x, y, x + size, y + size, COLOR_GOLD);
        gui.fill(x + 1, y + 1, x + size - 1, y + size - 1, COLOR_GOLD_DARK);
        gui.fillGradient(x + 2, y + 2, x + size - 2, y + size - 2, COLOR_NAVY_TOP, COLOR_NAVY_BOTTOM);

        final HeroEntity hero = portrait(mc, heroData);
        if (hero != null) {
            gui.enableScissor(x + 2, y + 2, x + size - 2, y + size - 2);
            renderPortrait(gui, mc, hero, x + size / 2, y + size / 2);
            gui.disableScissor();
        }

        final String level = String.valueOf(heroData.getLevel());
        final int badgeWidth = mc.font.width(level) + 6;
        final int badgeX = x + (size - badgeWidth) / 2;
        final int badgeY = y + size - 5;

        gui.pose().pushPose();
        gui.pose().translate(0, 0, 200);

        if (heroData.isDefeated()) {
            gui.fill(x + 2, y + 2, x + size - 2, y + size - 2, COLOR_DEFEATED);
        } else if (!heroData.isSummoned()) {
            gui.fill(x + 2, y + 2, x + size - 2, y + size - 2, COLOR_DIM);
        }

        gui.fill(badgeX, badgeY, badgeX + badgeWidth, badgeY + 11, COLOR_GOLD);
        gui.fill(badgeX + 1, badgeY + 1, badgeX + badgeWidth - 1, badgeY + 10, COLOR_NAVY_BOTTOM);
        gui.drawString(mc.font, level, badgeX + 3, badgeY + 2, COLOR_LEVEL_TEXT);
        gui.pose().popPose();
    }

    private static void renderHealth(final GuiGraphics gui, final Minecraft mc, final HeroData heroData,
                                     final HeroHudLayout layout) {
        final double maxHealth = HeroStatsSystem.getMaxHealth(heroData);
        final float ratio = maxHealth > 0 ? Mth.clamp((float) (heroData.getHealth() / maxHealth), 0, 1) : 0;
        final int filled = Math.round(HeroHudLayout.BAR_WIDTH * ratio);
        final int x = layout.barX();
        final int y = layout.healthY();
        final String label = (int) Math.ceil(heroData.getHealth()) + "/" + (int) Math.ceil(maxHealth);

        gui.fill(x - 2, y - 2, x + HeroHudLayout.BAR_WIDTH + 2, y + HeroHudLayout.HEALTH_HEIGHT + 2, COLOR_SHADOW);
        gui.fill(x - 1, y - 1, x + HeroHudLayout.BAR_WIDTH + 1, y + HeroHudLayout.HEALTH_HEIGHT + 1, COLOR_GOLD_DARK);
        gui.fillGradient(x, y, x + HeroHudLayout.BAR_WIDTH, y + HeroHudLayout.HEALTH_HEIGHT, COLOR_NAVY_TOP, COLOR_NAVY_BOTTOM);

        if (filled > 0) {
            gui.fillGradient(x, y, x + filled, y + HeroHudLayout.HEALTH_HEIGHT, COLOR_HEALTH_TOP, COLOR_HEALTH_BOTTOM);
            gui.fill(x, y, x + filled, y + 1, 0x55FFFFFF);
        }

        gui.drawString(
                mc.font,
                label,
                layout.labelX(mc.font, label),
                y + (HeroHudLayout.HEALTH_HEIGHT - mc.font.lineHeight) / 2 + 1,
                COLOR_HEALTH_TEXT
        );
    }

    private static void renderInterventions(final GuiGraphics gui, final HeroData heroData, final HeroHudLayout layout) {
        final int max = HeroStatsSystem.getMaxInterventions();
        final int remaining = Math.max(0, max - heroData.getInterventions());

        for (int i = 0; i < max; i++) {
            final int x = layout.mirrored() ? layout.barX() + HeroHudLayout.BAR_WIDTH - 7 - i * 9 : layout.barX() + i * 9;
            drawDiamond(gui, x, layout.interventionsY(), i < remaining ? COLOR_INTERVENTION : COLOR_INTERVENTION_USED);
        }
    }

    private static void drawDiamond(final GuiGraphics gui, final int x, final int y, final int color) {
        final int[] widths = {1, 3, 5, 7, 5, 3, 1};

        for (int row = 0; row < widths.length; row++) {
            final int left = x + (7 - widths[row]) / 2;
            gui.fill(left - 1, y + row, left + widths[row] + 1, y + row + 1, COLOR_SHADOW);
            gui.fill(left, y + row, left + widths[row], y + row + 1, color);
        }
    }

    @Nullable
    private static HeroEntity portrait(final Minecraft mc, final HeroData heroData) {
        final ResourceLocation heroId = heroData.getHeroDefinition().getId();

        if (portrait == null || portrait.level() != mc.level || !heroId.equals(portraitHeroId)) {
            portrait = AltheraEntities.HERO.get().create(mc.level);
            portraitHeroId = heroId;
            if (portrait == null) return null;
            portrait.setHeroId(heroId);
        }

        if (portrait.isDisguised() != heroData.isDisguised()) {
            portrait.setDisguised(heroData.isDisguised());
        }
        portrait.tickCount = mc.player.tickCount;
        return portrait;
    }

    private static void renderPortrait(final GuiGraphics gui, final Minecraft mc, final HeroEntity hero,
                                       final int centerX, final int centerY) {
        hero.yBodyRot = PORTRAIT_YAW;
        hero.yBodyRotO = PORTRAIT_YAW;
        hero.setYRot(PORTRAIT_YAW);
        hero.yHeadRot = PORTRAIT_YAW;
        hero.yHeadRotO = PORTRAIT_YAW;
        hero.setXRot(0.0F);

        final float modelScale = hero.getModelScale();
        final float headCenter = (headPivot(mc, hero) + HEAD_CENTER_ABOVE_PIVOT) / 16.0F * modelScale;

        InventoryScreen.renderEntityInInventory(gui, centerX, centerY, PORTRAIT_SCALE / modelScale,
                new Vector3f(0.0F, headCenter, 0.0F), new Quaternionf().rotateZ((float) Math.PI), null, hero);
    }

    @SuppressWarnings("unchecked")
    private static float headPivot(final Minecraft mc, final HeroEntity hero) {
        if (!(mc.getEntityRenderDispatcher().getRenderer(hero) instanceof GeoEntityRenderer<?> renderer)) {
            return DEFAULT_HEAD_PIVOT;
        }

        final GeoEntityRenderer<HeroEntity> heroRenderer = (GeoEntityRenderer<HeroEntity>) renderer;
        final GeoModel<HeroEntity> model = heroRenderer.getGeoModel();

        return model.getBakedModel(model.getModelResource(hero, heroRenderer))
                .getBone("head")
                .map(GeoBone::getPivotY)
                .orElse(DEFAULT_HEAD_PIVOT);
    }
}
