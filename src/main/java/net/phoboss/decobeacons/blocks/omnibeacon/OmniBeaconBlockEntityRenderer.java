package net.phoboss.decobeacons.blocks.omnibeacon;

import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class OmniBeaconBlockEntityRenderer implements BlockEntityRenderer<OmniBeaconBlockEntity> {
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

    public OmniBeaconBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(OmniBeaconBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }

        long l = entity.getWorld().getTime();
        List<DecoBeaconBlockEntity.DecoBeamSegment> list = entity.getOmniBeamSegments();
        if(list.isEmpty()){
            return;
        }
        float k = 0;

        DecoBeaconBlockEntity.DecoBeamSegment omniBeamSegment = list.get(0);
        renderBeam(matrices, vertexConsumers, tickDelta, l, k, list.size() == 1 ? omniBeamSegment.getHeight() : omniBeamSegment.getHeight()-0.5f, omniBeamSegment.getColor(),entity.getBeamDirection());
        k += omniBeamSegment.getHeight()-0.5f;

        for(int m = 1; m < list.size(); ++m) {
            omniBeamSegment = list.get(m);
            renderBeam(matrices, vertexConsumers, tickDelta, l, k,
                    m == list.size() - 1 ? omniBeamSegment.getHeight()+0.5f : omniBeamSegment.getHeight(), omniBeamSegment.getColor(),entity.getBeamDirection());
            k += omniBeamSegment.getHeight();
        }
    }

    private static void renderBeam(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, float yOffset, float maxY, float[] color, Vector3f beamDirection
    ) {
        renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0F, worldTime, yOffset, maxY, color, 0.2F, 0.25F,beamDirection);
    }


    public static void renderBeam(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Identifier textureId,
            float tickDelta,
            float heightScale,
            long worldTime,
            float yOffset,
            float maxY,
            float[] color,
            float innerRadius,
            float outerRadius,
            Vector3f beamDirection
    ) {
        float i = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        //matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
        //matrices.multiply(getQuatFrom2Vectors(new Vector3f(0,1,0), beamDirection));
        matrices.multiply(new Quaternionf().rotateTo(new Vector3f(0,1,0), beamDirection));
        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2F - (float)MathHelper.floor(g * 0.1F));
        float j = color[0];
        float k = color[1];
        float l = color[2];
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 2.25F - 45.0F));

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
                vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)),
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
        //matrices.pop();
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
                vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)),
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
        matrices.pop();
        matrices.pop();
    }

    private static void renderBeamLayer(
            MatrixStack matrices,
            VertexConsumer vertices,
            float red,
            float green,
            float blue,
            float alpha,
            float yOffset,
            float height,
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
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
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
            float yOffset,
            float height,
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
            float y,
            float x,
            float z,
            float u,
            float v
    ) {
        vertices.vertex(positionMatrix, x, (float)y, z)
                .color(red, green, blue, alpha)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
    }

    public boolean rendersOutsideBoundingBox(OmniBeaconBlockEntity beaconBlockEntity) {
        return true;
    }
    public boolean isInRenderDistance(OmniBeaconBlockEntity beaconBlockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(beaconBlockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), (double)this.getRenderDistance());
    }


    @Override
    public int getRenderDistance() {
        return 256;
    }
}
