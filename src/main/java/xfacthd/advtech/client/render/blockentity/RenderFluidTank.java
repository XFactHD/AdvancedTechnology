package xfacthd.advtech.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.util.ClientUtils;
import xfacthd.advtech.client.util.FluidSpriteCache;
import xfacthd.advtech.common.blockentity.storage.BlockEntityFluidTank;

public class RenderFluidTank implements BlockEntityRenderer<BlockEntityFluidTank>
{
    private static final float MIN_X =  3.01F/16F;
    private static final float MAX_X = 12.99F/16F;
    private static final float MIN_Y =  0.01F/16F;
    private static final float MAX_Y = 15.99F/16F;
    private static final float MIN_Z =  3.01F/16F;
    private static final float MAX_Z = 12.99F/16F;

    private static final float MIN_UV_T =  3.01F;
    private static final float MAX_UV_T = 12.99F;
    private static final float MIN_U_S  =  3.01F / 2F;
    private static final float MAX_U_S  = 12.99F / 2F;
    private static final float MIN_V_S  =  0.01F / 2F;
    private static final float MAX_V_S  = 15.99F / 2F;

    public RenderFluidTank(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(BlockEntityFluidTank be, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        FluidStack fluid = be.getContents();
        float height = be.getFluidHeight();

        if (!fluid.isEmpty())
        {
            renderContents(fluid, height, stack, buffer, combinedLight, combinedOverlay);
        }
    }

    public static void renderContents(FluidStack fluid, float height, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        TextureAtlasSprite stillTex = FluidSpriteCache.getStillTexture(fluid);
        TextureAtlasSprite flowTex = FluidSpriteCache.getFlowingTexture(fluid);

        FluidAttributes attributes = fluid.getFluid().getAttributes();
        boolean gas = attributes.isGaseous(fluid) || attributes.isLighterThanAir();
        int[] color = ClientUtils.splitRGBA(attributes.getColor(fluid));

        VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());
        Matrix4f matrix = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        drawTop(builder, matrix, normal, height, stillTex, color, gas, combinedOverlay, combinedLight);
        drawSides(builder, matrix, normal, height, flowTex, color, gas, combinedOverlay, combinedLight);
    }

    private static void drawTop(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, float height, TextureAtlasSprite tex, int[] color, boolean gas, int overlay, int light)
    {
        float minX = gas ? MAX_X : MIN_X;
        float maxX = gas ? MIN_X : MAX_X;
        float y = MIN_Y + (gas ? (1F - height) * (MAX_Y - MIN_Y): height * (MAX_Y - MIN_Y));
        float ny = gas ? -1 : 1;

        float minU = tex.getU(MIN_UV_T);
        float maxU = tex.getU(MAX_UV_T);
        float minV = tex.getV(MIN_UV_T);
        float maxV = tex.getV(MAX_UV_T);

        builder.vertex(matrix, maxX, y, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normal, 0, ny, 0).endVertex();
        builder.vertex(matrix, minX, y, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normal, 0, ny, 0).endVertex();
        builder.vertex(matrix, minX, y, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normal, 0, ny, 0).endVertex();
        builder.vertex(matrix, maxX, y, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normal, 0, ny, 0).endVertex();
    }

    private static void drawSides(VertexConsumer builder, Matrix4f matrix, Matrix3f normal, float height, TextureAtlasSprite tex, int[] color, boolean gas, int overlay, int light)
    {
        float minY = gas ? MAX_Y - (height * (MAX_Y - MIN_Y)) : MIN_Y;
        float maxY = gas ? MAX_Y : MIN_Y + height * (MAX_Y - MIN_Y);

        float minU = tex.getU(MIN_U_S);
        float maxU = tex.getU(MAX_U_S);
        float minV = tex.getV(MIN_V_S);
        float maxV = tex.getV(MIN_V_S + height * (MAX_V_S - MIN_V_S));

        //North
        builder.vertex(matrix, MIN_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0, -1).endVertex();
        builder.vertex(matrix, MAX_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0, -1).endVertex();
        builder.vertex(matrix, MAX_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0, -1).endVertex();
        builder.vertex(matrix, MIN_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0, -1).endVertex();

        //South
        builder.vertex(matrix, MAX_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0,  1).endVertex();
        builder.vertex(matrix, MIN_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0,  1).endVertex();
        builder.vertex(matrix, MIN_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0,  1).endVertex();
        builder.vertex(matrix, MAX_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normal,  0, 0,  1).endVertex();

        //East
        builder.vertex(matrix, MAX_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normal,  1, 0,  0).endVertex();
        builder.vertex(matrix, MAX_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normal,  1, 0,  0).endVertex();
        builder.vertex(matrix, MAX_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normal,  1, 0,  0).endVertex();
        builder.vertex(matrix, MAX_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normal,  1, 0,  0).endVertex();

        //West
        builder.vertex(matrix, MIN_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normal, -1, 0,  0).endVertex();
        builder.vertex(matrix, MIN_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normal, -1, 0,  0).endVertex();
        builder.vertex(matrix, MIN_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normal, -1, 0,  0).endVertex();
        builder.vertex(matrix, MIN_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normal, -1, 0,  0).endVertex();
    }
}