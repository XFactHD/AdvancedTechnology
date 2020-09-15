package xfacthd.advtech.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.advtech.client.util.IRangedMachine;
import xfacthd.advtech.client.util.Utils;
import xfacthd.advtech.common.tileentity.TileEntityMachine;

public class RenderRangedMachine<T extends TileEntityMachine & IRangedMachine> extends TileEntityRenderer<T>
{
    private static final ResourceLocation WHITE_LOCATION = Utils.modLocation("white");
    private static TextureAtlasSprite WHITE = null;

    public RenderRangedMachine(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(T te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (!te.showArea()) { return; }

        int radius = te.getRadius();
        boolean centered = te.isAreaCentered();
        int color = te.getAreaTint();
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
            Direction facing = te.getAreaOffsetDir();

            int x1 = 0;
            int z1 = 0;
            int x2 = 0;
            int z2 = 0;

            switch (facing)
            {
                case NORTH:
                case SOUTH:
                {
                    x1 = -radius;
                    z1 = Math.max(facing.getZOffset(), 0);
                    x2 = radius + 1;
                    z2 = facing.getZOffset() * (radius * 2 + 1) + z1;
                    break;
                }
                case EAST:
                case WEST:
                {
                    x1 = Math.max(facing.getXOffset(), 0);
                    z1 = -radius;
                    x2 = facing.getXOffset() * (radius * 2 + 1) + x1;
                    z2 = radius + 1;
                    break;
                }
            }

            startX = Math.min(x1, x2);
            startZ = Math.min(z1, z2);
            endX = Math.max(x1, x2);
            endZ = Math.max(z1, z2);
        }

        int botY = te.getAreaOffsetY();
        int topY = botY + te.getHeight();
        Matrix4f matrix = stack.getLast().getMatrix();

        drawFrame(buffer, matrix, startX, startZ, endX, endZ, botY, topY, r, g, b);
        drawSurfaces(buffer, matrix, startX, startZ, endX, endZ, botY, topY, r, g, b);
    }

    private void drawFrame(IRenderTypeBuffer buffer, Matrix4f matrix, int startX, int startZ, int endX, int endZ, int botY, int topY, int r, int g, int b)
    {
        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

        //Bottom frame
        builder.pos(matrix, startX, botY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, botY, startZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix, startX, botY,   endZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, botY,   endZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix, startX, botY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix, startX, botY,   endZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix,   endX, botY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, botY,   endZ).color(r, g, b, 0xFF).endVertex();

        //Top frame
        builder.pos(matrix, startX, topY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, topY, startZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix, startX, topY,   endZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, topY,   endZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix, startX, topY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix, startX, topY,   endZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix,   endX, topY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, topY,   endZ).color(r, g, b, 0xFF).endVertex();

        //Vertical lines
        builder.pos(matrix, startX, botY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix, startX, topY, startZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix, startX, botY,   endZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix, startX, topY,   endZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix,   endX, botY, startZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, topY, startZ).color(r, g, b, 0xFF).endVertex();

        builder.pos(matrix,   endX, botY,   endZ).color(r, g, b, 0xFF).endVertex();
        builder.pos(matrix,   endX, topY,   endZ).color(r, g, b, 0xFF).endVertex();
    }

    private void drawSurfaces(IRenderTypeBuffer buffer, Matrix4f matrix, int startX, int startZ, int endX, int endZ, int botY, int topY, int r, int g, int b)
    {
        IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());

        float minU = WHITE.getMinU();
        float maxU = WHITE.getMaxU();
        float minV = WHITE.getMinV();
        float maxV = WHITE.getMaxV();

        //Bottom
        builder.pos(matrix, startX, botY, startZ).color(r, g, b, 0xAA).tex(minU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, botY, startZ).color(r, g, b, 0xAA).tex(maxU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, botY,   endZ).color(r, g, b, 0xAA).tex(maxU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, botY,   endZ).color(r, g, b, 0xAA).tex(minU, maxV).lightmap(240).normal(0, 0, 0).endVertex();

        //Top
        builder.pos(matrix,   endX, topY, startZ).color(r, g, b, 0xAA).tex(minU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, topY, startZ).color(r, g, b, 0xAA).tex(maxU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, topY,   endZ).color(r, g, b, 0xAA).tex(maxU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, topY,   endZ).color(r, g, b, 0xAA).tex(minU, maxV).lightmap(240).normal(0, 0, 0).endVertex();

        //North
        builder.pos(matrix, startX, botY, startZ).color(r, g, b, 0xAA).tex(minU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, topY, startZ).color(r, g, b, 0xAA).tex(minU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, topY, startZ).color(r, g, b, 0xAA).tex(maxU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, botY, startZ).color(r, g, b, 0xAA).tex(maxU, minV).lightmap(240).normal(0, 0, 0).endVertex();

        //South
        builder.pos(matrix,   endX, botY,   endZ).color(r, g, b, 0xAA).tex(minU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, topY,   endZ).color(r, g, b, 0xAA).tex(minU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, topY,   endZ).color(r, g, b, 0xAA).tex(maxU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, botY,   endZ).color(r, g, b, 0xAA).tex(maxU, minV).lightmap(240).normal(0, 0, 0).endVertex();

        //West
        builder.pos(matrix, startX, botY,   endZ).color(r, g, b, 0xAA).tex(minU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, topY,   endZ).color(r, g, b, 0xAA).tex(minU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, topY, startZ).color(r, g, b, 0xAA).tex(maxU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix, startX, botY, startZ).color(r, g, b, 0xAA).tex(maxU, minV).lightmap(240).normal(0, 0, 0).endVertex();

        //East
        builder.pos(matrix,   endX, botY, startZ).color(r, g, b, 0xAA).tex(minU, minV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, topY, startZ).color(r, g, b, 0xAA).tex(minU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, topY,   endZ).color(r, g, b, 0xAA).tex(maxU, maxV).lightmap(240).normal(0, 0, 0).endVertex();
        builder.pos(matrix,   endX, botY,   endZ).color(r, g, b, 0xAA).tex(maxU, minV).lightmap(240).normal(0, 0, 0).endVertex();
    }



    @Override
    public boolean isGlobalRenderer(T te) { return true; }



    public static void registerTextures(TextureStitchEvent.Pre event) { event.addSprite(WHITE_LOCATION); }

    public static void retrieveSprites(AtlasTexture map) { WHITE = map.getSprite(WHITE_LOCATION); }
}