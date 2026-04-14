package com.darksune.althera.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class SummonedZombieRenderer extends ZombieRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("althera", "textures/entity/summoned_zombie.png");

    public SummonedZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie entity) {
        return TEXTURE;
    }
}