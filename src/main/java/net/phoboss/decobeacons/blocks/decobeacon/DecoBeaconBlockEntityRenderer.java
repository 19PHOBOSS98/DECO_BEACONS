package net.phoboss.decobeacons.blocks.decobeacon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class DecoBeaconBlockEntityRenderer implements BlockEntityRenderer<DecoBeaconBlockEntity> {
    public static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");
    public DecoBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(DecoBeaconBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }

        long l = entity.getLevel().getGameTime();
        List<DecoBeaconBlockEntity.DecoBeamSegment> list = entity.getDecoBeamSegments();
        int k = 0;

        for(int m = 0; m < list.size(); ++m) {
            DecoBeaconBlockEntity.DecoBeamSegment decoBeamSegment = list.get(m);
            //renderBeam(matrices, vertexConsumers, tickDelta, l, k, m == list.size() - 1 ? 1024 : decoBeamSegment.getHeight(), decoBeamSegment.getColor());
            renderBeam(matrices, vertexConsumers, tickDelta, l, k, decoBeamSegment.getHeight(), decoBeamSegment.getColor());
            k += decoBeamSegment.getHeight();
        }
    }

    private static void renderBeam(
            PoseStack matrices, MultiBufferSource vertexConsumers, float tickDelta, long worldTime, int yOffset, int maxY, float[] color
    ) {
        renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0F, worldTime, yOffset, maxY, color, 0.2F, 0.25F);
    }
    public static void renderBeam(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            ResourceLocation textureId,
            float tickDelta,
            float heightScale,
            long worldTime,
            int yOffset,
            int maxY,
            float[] color,
            float innerRadius,
            float outerRadius
    ) {
        int i = yOffset + maxY;
        matrices.pushPose();
        matrices.translate(0.5, 0.0, 0.5);
        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = Mth.frac(g * 0.2F - (float)Mth.floor(g * 0.1F));
        float j = color[0];
        float k = color[1];
        float l = color[2];
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float m = 0.0F;
        float p = 0.0F;
        float q = -innerRadius;
        float r = 0.0F;
        float s = 0.0F;
        float t = -innerRadius;
        float u = 0.0F;
        float v = 1.0F;
        float w = -1.0F + h;
        float x = (float)maxY * heightScale * (0.5F / innerRadius) + w;
        renderBeamLayer(
                matrices,
                vertexConsumers.getBuffer(RenderType.beaconBeam(textureId, false)),
                j,
                k,
                l,
                1.0F,
                yOffset,
                i,
                0.0F,
                innerRadius,
                innerRadius,
                0.0F,
                q,
                0.0F,
                0.0F,
                t,
                0.0F,
                1.0F,
                x,
                w
        );
        matrices.popPose();
        m = -outerRadius;
        float n = -outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        u = 0.0F;
        v = 1.0F;
        w = -1.0F + h;
        x = (float)maxY * heightScale + w;
        renderBeamLayer(
                matrices,
                vertexConsumers.getBuffer(RenderType.beaconBeam(textureId, true)),
                j,
                k,
                l,
                0.125F,
                yOffset,
                i,
                m,
                n,
                outerRadius,
                p,
                q,
                outerRadius,
                outerRadius,
                outerRadius,
                0.0F,
                1.0F,
                x,
                w
        );
        matrices.popPose();
    }

    private static void renderBeamLayer(
            PoseStack matrices,
            VertexConsumer vertices,
            float red,
            float green,
            float blue,
            float alpha,
            int yOffset,
            int height,
            float x1,
            float z1,
            float x2,
            float z2,
            float x3,
            float z3,
            float x4,
            float z4,
            float u1,
            float u2,
            float v1,
            float v2
    ) {
        PoseStack.Pose entry = matrices.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            VertexConsumer vertices,
            float red,
            float green,
            float blue,
            float alpha,
            int yOffset,
            int height,
            float x1,
            float z1,
            float x2,
            float z2,
            float u1,
            float u2,
            float v1,
            float v2
    ) {
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    /**
     * @param v the top-most coordinate of the texture region
     * @param u the left-most coordinate of the texture region
     */
    private static void renderBeamVertex(
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            VertexConsumer vertices,
            float red,
            float green,
            float blue,
            float alpha,
            int y,
            float x,
            float z,
            float u,
            float v
    ) {
        vertices.vertex(positionMatrix, x, (float)y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public boolean rendersOutsideBoundingBox(DecoBeaconBlockEntity beaconBlockEntity) {
        return true;
    }
    public boolean isInRenderDistance(DecoBeaconBlockEntity beaconBlockEntity, Vec3 vec3d) {
        return Vec3.atCenterOf(beaconBlockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(vec3d.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

}
