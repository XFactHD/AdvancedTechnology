package xfacthd.advtech.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.util.Utils;
import xfacthd.advtech.common.tileentity.debug.TileEntityCreativeFluidSource;

public class RenderCreativeFluidSource extends TileEntityRenderer<TileEntityCreativeFluidSource>
{
    private static final float MIN_WH =  5F/16F; //Width/height of cutout
    private static final float MAX_WH = 11F/16F; //Width/height of cutout
    private static final float MIN_D = -0.01F/16F; //Depth
    private static final float MAX_D = 16.01F/16F; //Depth

    public RenderCreativeFluidSource(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(TileEntityCreativeFluidSource te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay)
    {
        FluidStack fluid = te.getContents();
        if (fluid.isEmpty()) { return; }

        TextureAtlasSprite sprite = RenderFluidTank.getStillTexture(fluid);
        int[] col = Utils.splitRGBA(fluid.getFluid().getAttributes().getColor(fluid));

        IVertexBuilder builder = buffer.getBuffer(Atlases.getTranslucentCullBlockType());
        Matrix4f matrix = stack.getLast().getMatrix();
        Matrix3f normal = stack.getLast().getNormal();

        float minU = sprite.getInterpolatedU(5);
        float maxU = sprite.getInterpolatedU(11);
        float minV = sprite.getInterpolatedV(5);
        float maxV = sprite.getInterpolatedV(11);

        //Top
        builder.pos(matrix, MAX_WH, MAX_D, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(minU, minV).overlay(overlay).lightmap(240).normal(normal,  0,  1, 0).endVertex();
        builder.pos(matrix, MIN_WH, MAX_D, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, minV).overlay(overlay).lightmap(240).normal(normal,  0,  1, 0).endVertex();
        builder.pos(matrix, MIN_WH, MAX_D, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, maxV).overlay(overlay).lightmap(240).normal(normal,  0,  1, 0).endVertex();
        builder.pos(matrix, MAX_WH, MAX_D, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(minU, maxV).overlay(overlay).lightmap(240).normal(normal,  0,  1, 0).endVertex();

        //Bottom
        builder.pos(matrix, MIN_WH, MIN_D, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(minU, minV).overlay(overlay).lightmap(240).normal(normal,  0, -1, 0).endVertex();
        builder.pos(matrix, MAX_WH, MIN_D, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, minV).overlay(overlay).lightmap(240).normal(normal,  0, -1, 0).endVertex();
        builder.pos(matrix, MAX_WH, MIN_D, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, maxV).overlay(overlay).lightmap(240).normal(normal,  0, -1, 0).endVertex();
        builder.pos(matrix, MIN_WH, MIN_D, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(minU, maxV).overlay(overlay).lightmap(240).normal(normal,  0, -1, 0).endVertex();

        //North
        builder.pos(matrix, MAX_WH, MIN_WH, MIN_D).color(col[0], col[1], col[2], col[3]).tex(minU, minV).overlay(overlay).lightmap(240).normal(normal,  0,  0, -1).endVertex();
        builder.pos(matrix, MIN_WH, MIN_WH, MIN_D).color(col[0], col[1], col[2], col[3]).tex(maxU, minV).overlay(overlay).lightmap(240).normal(normal,  0,  0, -1).endVertex();
        builder.pos(matrix, MIN_WH, MAX_WH, MIN_D).color(col[0], col[1], col[2], col[3]).tex(maxU, maxV).overlay(overlay).lightmap(240).normal(normal,  0,  0, -1).endVertex();
        builder.pos(matrix, MAX_WH, MAX_WH, MIN_D).color(col[0], col[1], col[2], col[3]).tex(minU, maxV).overlay(overlay).lightmap(240).normal(normal,  0,  0, -1).endVertex();

        //South
        builder.pos(matrix, MIN_WH, MIN_WH, MAX_D).color(col[0], col[1], col[2], col[3]).tex(minU, minV).overlay(overlay).lightmap(240).normal(normal,  0,  0,  1).endVertex();
        builder.pos(matrix, MAX_WH, MIN_WH, MAX_D).color(col[0], col[1], col[2], col[3]).tex(maxU, minV).overlay(overlay).lightmap(240).normal(normal,  0,  0,  1).endVertex();
        builder.pos(matrix, MAX_WH, MAX_WH, MAX_D).color(col[0], col[1], col[2], col[3]).tex(maxU, maxV).overlay(overlay).lightmap(240).normal(normal,  0,  0,  1).endVertex();
        builder.pos(matrix, MIN_WH, MAX_WH, MAX_D).color(col[0], col[1], col[2], col[3]).tex(minU, maxV).overlay(overlay).lightmap(240).normal(normal,  0,  0,  1).endVertex();

        //East
        builder.pos(matrix, MAX_D, MIN_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(minU, minV).overlay(overlay).lightmap(240).normal(normal,  1,  0,  0).endVertex();
        builder.pos(matrix, MAX_D, MIN_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, minV).overlay(overlay).lightmap(240).normal(normal,  1,  0,  0).endVertex();
        builder.pos(matrix, MAX_D, MAX_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, maxV).overlay(overlay).lightmap(240).normal(normal,  1,  0,  0).endVertex();
        builder.pos(matrix, MAX_D, MAX_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(minU, maxV).overlay(overlay).lightmap(240).normal(normal,  1,  0,  0).endVertex();

        //West
        builder.pos(matrix, MIN_D, MIN_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(minU, minV).overlay(overlay).lightmap(240).normal(normal, -1,  0,  0).endVertex();
        builder.pos(matrix, MIN_D, MIN_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, minV).overlay(overlay).lightmap(240).normal(normal, -1,  0,  0).endVertex();
        builder.pos(matrix, MIN_D, MAX_WH, MAX_WH).color(col[0], col[1], col[2], col[3]).tex(maxU, maxV).overlay(overlay).lightmap(240).normal(normal, -1,  0,  0).endVertex();
        builder.pos(matrix, MIN_D, MAX_WH, MIN_WH).color(col[0], col[1], col[2], col[3]).tex(minU, maxV).overlay(overlay).lightmap(240).normal(normal, -1,  0,  0).endVertex();
    }
}