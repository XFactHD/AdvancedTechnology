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
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.util.ClientUtils;
import xfacthd.advtech.client.util.FluidSpriteCache;
import xfacthd.advtech.common.blockentity.debug.BlockEntityCreativeFluidSource;

public class RenderCreativeFluidSource implements BlockEntityRenderer<BlockEntityCreativeFluidSource>
{
    private static final float MIN_WH =  5F/16F; //Width/height of cutout
    private static final float MAX_WH = 11F/16F; //Width/height of cutout
    private static final float MIN_D = -0.01F/16F; //Depth
    private static final float MAX_D = 16.01F/16F; //Depth

    public RenderCreativeFluidSource(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(BlockEntityCreativeFluidSource te, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay)
    {
        FluidStack fluid = te.getContents();
        if (fluid.isEmpty()) { return; }

        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluid);
        int[] col = ClientUtils.splitRGBA(fluid.getFluid().getAttributes().getColor(fluid));

        VertexConsumer builder = buffer.getBuffer(Sheets.translucentCullBlockSheet());
        Matrix4f matrix = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        float minU = sprite.getU(5);
        float maxU = sprite.getU(11);
        float minV = sprite.getV(5);
        float maxV = sprite.getV(11);

        //Top
        builder.vertex(matrix, MAX_WH, MAX_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
        builder.vertex(matrix, MIN_WH, MAX_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
        builder.vertex(matrix, MIN_WH, MAX_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
        builder.vertex(matrix, MAX_WH, MAX_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();

        //Bottom
        builder.vertex(matrix, MIN_WH, MIN_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
        builder.vertex(matrix, MAX_WH, MIN_D, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
        builder.vertex(matrix, MAX_WH, MIN_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
        builder.vertex(matrix, MIN_WH, MIN_D, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();

        //North
        builder.vertex(matrix, MAX_WH, MIN_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MIN_WH, MIN_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MIN_WH, MAX_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MAX_WH, MAX_WH, MIN_D).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();

        //South
        builder.vertex(matrix, MIN_WH, MIN_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MAX_WH, MIN_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MAX_WH, MAX_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MIN_WH, MAX_WH, MAX_D).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();

        //East
        builder.vertex(matrix, MAX_D, MIN_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX_D, MIN_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX_D, MAX_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX_D, MAX_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();

        //West
        builder.vertex(matrix, MIN_D, MIN_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN_D, MIN_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN_D, MAX_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN_D, MAX_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
    }
}