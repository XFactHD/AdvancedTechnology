package xfacthd.advtech.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import xfacthd.advtech.common.blockentity.utility.BlockEntityChunkLoader;

public class RenderChunkLoader implements BlockEntityRenderer<BlockEntityChunkLoader>
{
    public RenderChunkLoader(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(BlockEntityChunkLoader tile, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (!tile.showChunks()) { return; }

        stack.pushPose();

        VertexConsumer builder = buffer.getBuffer(RenderType.LINES);

        int tileX = tile.getBlockPos().getX();
        int tileY = tile.getBlockPos().getY();
        int tileZ = tile.getBlockPos().getZ();

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
                    LevelRenderer.renderLineBox(stack, builder, cX, y - tileY, cZ, cX + 16, y + 16 - tileY, cZ + 16, red, green, 0, 1);
                }
            }
        }

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntityChunkLoader be) { return be.showChunks(); }
}