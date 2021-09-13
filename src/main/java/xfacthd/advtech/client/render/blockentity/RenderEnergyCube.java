package xfacthd.advtech.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;
import xfacthd.advtech.common.util.Utils;

public class RenderEnergyCube implements BlockEntityRenderer<BlockEntityEnergyCube>
{
    private static final ResourceLocation ENERGY_EMPTY = Utils.modLocation("block/storage/block_energy_cube_bar_empty");
    private static final ResourceLocation ENERGY_FULL = Utils.modLocation("block/storage/block_energy_cube_bar_full");

    private static TextureAtlasSprite spriteEmpty;
    private static TextureAtlasSprite spriteFull;

    public RenderEnergyCube(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(BlockEntityEnergyCube cube, float partialTicks, PoseStack stack, MultiBufferSource buffer, int lightmap, int overlay)
    {
        stack.pushPose();

        stack.translate(.5, .5, .5);
        stack.mulPose(Vector3f.YP.rotationDegrees(180 - cube.getFacing().toYRot()));
        stack.translate(-.5, -.5, -.5);

        int level = cube.getEnergyLevel();

        renderEnergyBar(buffer, stack, level, overlay, lightmap);

        stack.popPose();
    }

    public static void renderEnergyBar(MultiBufferSource buffer, PoseStack stack, int level, int overlay, int lightmap)
    {
        VertexConsumer builder = buffer.getBuffer(Sheets.cutoutBlockSheet());
        Matrix4f matrix = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        float swapPoint = (10F - level) / 10F;
        float swapY = (13F - (10F * swapPoint)) / 16F;
        float swapV = (10F * (1F - swapPoint)) + 3F;

        if (level < 10)
        {
            float minU = spriteEmpty.getU(3);
            float maxU = spriteEmpty.getU(13);
            float minV = spriteEmpty.getV(swapV);
            float maxV = spriteEmpty.getV(13);
            builder.vertex(matrix, 13F/16F, 13F/16F, 0).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.vertex(matrix, 13F/16F,   swapY, 0).color(255, 255, 255, 255).uv(maxU, minV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.vertex(matrix,  3F/16F,   swapY, 0).color(255, 255, 255, 255).uv(minU, minV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.vertex(matrix,  3F/16F, 13F/16F, 0).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
        }

        if (level > 0)
        {
            float minU = spriteFull.getU(3);
            float maxU = spriteFull.getU(13);
            float minV = spriteFull.getV(3);
            float maxV = spriteFull.getV(swapV);
            builder.vertex(matrix, 13F/16F,  swapY, 0).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.vertex(matrix, 13F/16F, 3F/16F, 0).color(255, 255, 255, 255).uv(maxU, minV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.vertex(matrix,  3F/16F, 3F/16F, 0).color(255, 255, 255, 255).uv(minU, minV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.vertex(matrix,  3F/16F,  swapY, 0).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(overlay).uv2(lightmap).normal(normal, 0, 0, -1).endVertex();
        }
    }

    public static void registerTextures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(ENERGY_EMPTY);
        event.addSprite(ENERGY_FULL);
    }

    public static void retrieveSprites(final TextureAtlas map)
    {
        spriteEmpty = map.getSprite(ENERGY_EMPTY);
        spriteFull = map.getSprite(ENERGY_FULL);
    }
}