package xfacthd.advtech.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import xfacthd.advtech.common.tileentity.utility.TileEntityChunkLoader;

public class RenderChunkLoader extends TileEntityRenderer<TileEntityChunkLoader>
{
    public RenderChunkLoader(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(TileEntityChunkLoader tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (!tile.showChunks()) { return; }

        stack.push();

        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

        int tileX = tile.getPos().getX();
        int tileY = tile.getPos().getY();
        int tileZ = tile.getPos().getZ();

        int centerX = tileX >> 4;
        int centerZ = tileZ >> 4;

        int chunkRadius = tile.getRadius();

        int startX = centerX - chunkRadius;
        int startZ = centerZ - chunkRadius;
        int endX = centerX + chunkRadius;
        int endZ = centerZ + chunkRadius;

        boolean active = tile.isActive();

        for (int x = startX; x <= endX; x++)
        {
            for (int z = startZ; z <= endZ; z++)
            {
                int cX = (x << 4) - tileX;
                int cZ = (z << 4) - tileZ;

                for (int y = 0; y < 255; y += 16)
                {
                    float red = active ? 0 : 1;
                    float green = active ? 1 : 0;
                    WorldRenderer.drawBoundingBox(stack, builder, cX, y - tileY, cZ, cX + 16, y + 16 - tileY, cZ + 16, red, green, 0, 1);
                }
            }
        }

        stack.pop();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityChunkLoader te) { return true; }
}