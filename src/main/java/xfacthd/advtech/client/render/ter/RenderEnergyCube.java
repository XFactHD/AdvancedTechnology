package xfacthd.advtech.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import xfacthd.advtech.client.util.Utils;
import xfacthd.advtech.common.tileentity.energy.TileEntityEnergyCube;

public class RenderEnergyCube extends TileEntityRenderer<TileEntityEnergyCube>
{
    private static final ResourceLocation ENERGY_EMPTY = Utils.modLocation("block/energy/block_energy_cube_bar_empty");
    private static final ResourceLocation ENERGY_FULL = Utils.modLocation("block/energy/block_energy_cube_bar_full");

    private static TextureAtlasSprite spriteEmpty;
    private static TextureAtlasSprite spriteFull;

    public RenderEnergyCube(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(TileEntityEnergyCube cube, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int lightmap, int overlay)
    {
        stack.push();

        stack.translate(.5, .5, .5);
        stack.rotate(Vector3f.YP.rotationDegrees(180 - cube.getFacing().getHorizontalAngle()));
        stack.translate(-.5, -.5, -.5);

        int level = cube.getEnergyLevel();

        IVertexBuilder builder = buffer.getBuffer(Atlases.getCutoutBlockType());
        Matrix4f matrix = stack.getLast().getMatrix();
        Matrix3f normal = stack.getLast().getNormal();

        float swapPoint = (10F - level) / 10F;
        float swapY = (13F - (10F * swapPoint)) / 16F;
        float swapV = (10F * (1F - swapPoint)) + 3F;

        if (level < 10)
        {
            float minU = spriteEmpty.getInterpolatedU(3);
            float maxU = spriteEmpty.getInterpolatedU(13);
            float minV = spriteEmpty.getInterpolatedV(swapV);
            float maxV = spriteEmpty.getInterpolatedV(13);
            builder.pos(matrix, 13F/16F, 13F/16F, 0).color(255, 255, 255, 255).tex(maxU, maxV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.pos(matrix, 13F/16F,   swapY, 0).color(255, 255, 255, 255).tex(maxU, minV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.pos(matrix,  3F/16F,   swapY, 0).color(255, 255, 255, 255).tex(minU, minV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.pos(matrix,  3F/16F, 13F/16F, 0).color(255, 255, 255, 255).tex(minU, maxV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
        }

        if (level > 0)
        {
            float minU = spriteFull.getInterpolatedU(3);
            float maxU = spriteFull.getInterpolatedU(13);
            float minV = spriteFull.getInterpolatedV(3);
            float maxV = spriteFull.getInterpolatedV(swapV);
            builder.pos(matrix, 13F/16F,  swapY, 0).color(255, 255, 255, 255).tex(maxU, maxV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.pos(matrix, 13F/16F, 3F/16F, 0).color(255, 255, 255, 255).tex(maxU, minV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.pos(matrix,  3F/16F, 3F/16F, 0).color(255, 255, 255, 255).tex(minU, minV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
            builder.pos(matrix,  3F/16F,  swapY, 0).color(255, 255, 255, 255).tex(minU, maxV).overlay(overlay).lightmap(lightmap).normal(normal, 0, 0, -1).endVertex();
        }

        stack.pop();
    }

    public static void registerTextures(final TextureStitchEvent.Pre event)
    {
        event.addSprite(ENERGY_EMPTY);
        event.addSprite(ENERGY_FULL);
    }

    public static void retrieveSprites(final AtlasTexture map)
    {
        spriteEmpty = map.getSprite(ENERGY_EMPTY);
        spriteFull = map.getSprite(ENERGY_FULL);
    }
}