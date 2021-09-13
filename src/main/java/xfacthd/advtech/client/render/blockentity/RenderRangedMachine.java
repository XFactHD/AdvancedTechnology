package xfacthd.advtech.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.advtech.client.util.IRangedMachine;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.util.Utils;

public class RenderRangedMachine<T extends BlockEntityMachine & IRangedMachine> implements BlockEntityRenderer<T>
{
    private static final ResourceLocation WHITE_LOCATION = Utils.modLocation("white");
    private static TextureAtlasSprite WHITE = null;

    public RenderRangedMachine(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(T be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        if (be.showArea()) { renderArea(be, buffer, matrix, normal); }
        if (be.showScanPos()) { renderScanPos(be, buffer, matrix, normal); }
    }

    private void renderArea(T be, MultiBufferSource buffer, Matrix4f matrix, Matrix3f normal)
    {
        int radius = be.getRadius();
        boolean centered = be.isAreaCentered();
        int color = be.getAreaTint();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int startX;
        int startZ;
        int endX;
        int endZ;

        if (centered)
        {
            startX = -radius;
            startZ = -radius;
            endX = radius + 1;
            endZ = radius + 1;
        }
        else
        {
            Direction facing = be.getAreaOffsetDir();

            int x1 = 0;
            int z1 = 0;
            int x2 = 0;
            int z2 = 0;

            switch (facing)
            {
                case NORTH, SOUTH -> {
                    x1 = -radius;
                    z1 = Math.max(facing.getStepZ(), 0);
                    x2 = radius + 1;
                    z2 = facing.getStepZ() * (radius * 2 + 1) + z1;
                }
                case EAST, WEST -> {
                    x1 = Math.max(facing.getStepX(), 0);
                    z1 = -radius;
                    x2 = facing.getStepX() * (radius * 2 + 1) + x1;
                    z2 = radius + 1;
                }
            }

            startX = Math.min(x1, x2);
            startZ = Math.min(z1, z2);
            endX = Math.max(x1, x2);
            endZ = Math.max(z1, z2);
        }

        int botY = be.getAreaOffsetY();
        int topY = botY + be.getHeight();

        drawFrame(buffer, matrix, normal, startX, startZ, endX, endZ, botY, topY, r, g, b);
        drawSurfaces(buffer, matrix, normal, startX, startZ, endX, endZ, botY, topY, r, g, b);
    }

    private void renderScanPos(T be, MultiBufferSource buffer, Matrix4f matrix, Matrix3f normal)
    {
        int color = be.getScanPosTint();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        BlockPos scanPos = be.getScanPos();
        int scanX = scanPos.getX() - be.getBlockPos().getX();
        int scanZ = scanPos.getZ() - be.getBlockPos().getZ();
        int scanY = scanPos.getY() - be.getBlockPos().getY();

        drawFrame(buffer, matrix, normal, scanX, scanZ, scanX + 1, scanZ + 1, scanY, scanY + 1, r, g, b);
    }

    private void drawFrame(MultiBufferSource buffer, Matrix4f matrix, Matrix3f normal, int startX, int startZ, int endX, int endZ, int botY, int topY, int r, int g, int b)
    {
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());

        //Bottom frame
        drawLine(builder, matrix, normal, startX, botY, startZ,   endX, botY, startZ, r, g, b);
        drawLine(builder, matrix, normal, startX, botY,   endZ,   endX, botY,   endZ, r, g, b);
        drawLine(builder, matrix, normal, startX, botY, startZ, startX, botY,   endZ, r, g, b);
        drawLine(builder, matrix, normal,   endX, botY, startZ,   endX, botY,   endZ, r, g, b);

        //Top frame
        drawLine(builder, matrix, normal, startX, topY, startZ,   endX, topY, startZ, r, g, b);
        drawLine(builder, matrix, normal, startX, topY,   endZ,   endX, topY,   endZ, r, g, b);
        drawLine(builder, matrix, normal, startX, topY, startZ, startX, topY,   endZ, r, g, b);
        drawLine(builder, matrix, normal,   endX, topY, startZ,   endX, topY,   endZ, r, g, b);

        //Vertical lines
        drawLine(builder, matrix, normal, startX, botY, startZ, startX, topY, startZ, r, g, b);
        drawLine(builder, matrix, normal, startX, botY,   endZ, startX, topY,   endZ, r, g, b);
        drawLine(builder, matrix, normal,   endX, botY, startZ,   endX, topY, startZ, r, g, b);
        drawLine(builder, matrix, normal,   endX, botY,   endZ,   endX, topY,   endZ, r, g, b);
    }

    private void drawLine(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, int x1, int y1, int z1, int x2, int y2, int z2, int r, int g, int b)
    {
        float nX = (float)(x2 - x1);
        float nY = (float)(y2 - y1);
        float nZ = (float)(z2 - z1);
        float sqrt = Mth.sqrt(nX * nX + nY * nY + nZ * nZ);
        nX = nX / sqrt;
        nY = nY / sqrt;
        nZ = nZ / sqrt;

        builder.vertex(matrix, x1, y1, z1).color(r, g, b, 0xFF).normal(normal, nX, nY, nZ).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(r, g, b, 0xFF).normal(normal, nX, nY, nZ).endVertex();
    }

    private void drawSurfaces(MultiBufferSource buffer, Matrix4f matrix, Matrix3f normal, int startX, int startZ, int endX, int endZ, int botY, int topY, int r, int g, int b)
    {
        VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());

        float minU = WHITE.getU0();
        float maxU = WHITE.getU1();
        float minV = WHITE.getV0();
        float maxV = WHITE.getV1();

        //Bottom
        builder.vertex(matrix, startX, botY, startZ).color(r, g, b, 0xAA).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, -1, 0).endVertex();
        builder.vertex(matrix,   endX, botY, startZ).color(r, g, b, 0xAA).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, -1, 0).endVertex();
        builder.vertex(matrix,   endX, botY,   endZ).color(r, g, b, 0xAA).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, -1, 0).endVertex();
        builder.vertex(matrix, startX, botY,   endZ).color(r, g, b, 0xAA).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, -1, 0).endVertex();

        //Top
        builder.vertex(matrix,   endX, topY, startZ).color(r, g, b, 0xAA).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix, startX, topY, startZ).color(r, g, b, 0xAA).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix, startX, topY,   endZ).color(r, g, b, 0xAA).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(matrix,   endX, topY,   endZ).color(r, g, b, 0xAA).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 1, 0).endVertex();

        //North
        builder.vertex(matrix, startX, botY, startZ).color(r, g, b, 0xAA).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, -1).endVertex();
        builder.vertex(matrix, startX, topY, startZ).color(r, g, b, 0xAA).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, -1).endVertex();
        builder.vertex(matrix,   endX, topY, startZ).color(r, g, b, 0xAA).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, -1).endVertex();
        builder.vertex(matrix,   endX, botY, startZ).color(r, g, b, 0xAA).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, -1).endVertex();

        //South
        builder.vertex(matrix,   endX, botY,   endZ).color(r, g, b, 0xAA).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(matrix,   endX, topY,   endZ).color(r, g, b, 0xAA).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(matrix, startX, topY,   endZ).color(r, g, b, 0xAA).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(matrix, startX, botY,   endZ).color(r, g, b, 0xAA).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0, 0, 1).endVertex();

        //West
        builder.vertex(matrix, startX, botY,   endZ).color(r, g, b, 0xAA).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, -1, 0, 0).endVertex();
        builder.vertex(matrix, startX, topY,   endZ).color(r, g, b, 0xAA).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, -1, 0, 0).endVertex();
        builder.vertex(matrix, startX, topY, startZ).color(r, g, b, 0xAA).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, -1, 0, 0).endVertex();
        builder.vertex(matrix, startX, botY, startZ).color(r, g, b, 0xAA).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, -1, 0, 0).endVertex();

        //East
        builder.vertex(matrix,   endX, botY, startZ).color(r, g, b, 0xAA).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(matrix,   endX, topY, startZ).color(r, g, b, 0xAA).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(matrix,   endX, topY,   endZ).color(r, g, b, 0xAA).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(matrix,   endX, botY,   endZ).color(r, g, b, 0xAA).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 1, 0, 0).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(T be) { return be.showArea(); }

    public static void registerTextures(TextureStitchEvent.Pre event) { event.addSprite(WHITE_LOCATION); }

    public static void retrieveSprites(TextureAtlas map) { WHITE = map.getSprite(WHITE_LOCATION); }
}