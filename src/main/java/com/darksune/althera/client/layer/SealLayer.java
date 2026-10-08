package com.darksune.althera.client.layer;

import com.darksune.althera.Althera;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SealLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation SEAL = ResourceLocation.fromNamespaceAndPath(Althera.MOD_ID, "textures/overlay/player/seal/seal_mark.png");

    public SealLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        poseStack.pushPose();

        // Attach to arm transform
        this.getParentModel().rightArm.translateAndRotate(poseStack);

        // Position on arm
        poseStack.translate(0.0F, 0.5F, -0.15F);

        // Size of seal
        poseStack.scale(0.25F, 0.25F, 0.25F);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        VertexConsumer consumer =
                buffer.getBuffer(RenderType.entityTranslucent(SEAL));

        // Quad
        addVertex(consumer, matrix, pose, -1F, -1F, 0F, 0F, 1F, packedLight);
        addVertex(consumer, matrix, pose,  1F, -1F, 0F, 1F, 1F, packedLight);
        addVertex(consumer, matrix, pose,  1F,  1F, 0F, 1F, 0F, packedLight);
        addVertex(consumer, matrix, pose, -1F,  1F, 0F, 0F, 0F, packedLight);

        poseStack.popPose();
    }

    private static void addVertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            int light) {

        consumer.addVertex(matrix, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0F, 0F, 1F);
    }
}
