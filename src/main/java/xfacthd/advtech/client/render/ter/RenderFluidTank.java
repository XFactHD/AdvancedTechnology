package xfacthd.advtech.client.render.ter;

import com.google.common.cache.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.util.Utils;
import xfacthd.advtech.common.tileentity.storage.TileEntityFluidTank;

public class RenderFluidTank extends TileEntityRenderer<TileEntityFluidTank>
{
    private static final LoadingCache<ResourceLocation, TextureAtlasSprite> SPRITE_CACHE = buildCache();

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

    public RenderFluidTank(TileEntityRendererDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(TileEntityFluidTank te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        FluidStack fluid = te.getContents();
        float height = te.getFluidHeight();

        if (!fluid.isEmpty())
        {
            renderContents(fluid, height, stack, buffer, combinedLight, combinedOverlay);
        }
    }

    public static void renderContents(FluidStack fluid, float height, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        TextureAtlasSprite stillTex = getStillTexture(fluid);
        TextureAtlasSprite flowTex = getFlowingTexture(fluid);

        FluidAttributes attributes = fluid.getFluid().getAttributes();
        boolean gas = attributes.isGaseous(fluid) || attributes.isLighterThanAir();
        int[] color = Utils.splitRGBA(attributes.getColor(fluid));

        IVertexBuilder builder = buffer.getBuffer(Atlases.getTranslucentCullBlockType());
        Matrix4f matrix = stack.getLast().getMatrix();
        Matrix3f normal = stack.getLast().getNormal();

        drawTop(builder, matrix, normal, height, stillTex, color, gas, combinedOverlay, combinedLight);
        drawSides(builder, matrix, normal, height, flowTex, color, gas, combinedOverlay, combinedLight);
    }

    private static void drawTop(IVertexBuilder builder, Matrix4f matrix, Matrix3f normal, float height, TextureAtlasSprite tex, int[] color, boolean gas, int overlay, int light)
    {
        float minX = gas ? MAX_X : MIN_X;
        float maxX = gas ? MIN_X : MAX_X;
        float y = MIN_Y + (gas ? (1F - height) * (MAX_Y - MIN_Y): height * (MAX_Y - MIN_Y));
        float ny = gas ? -1 : 1;

        float minU = tex.getInterpolatedU(MIN_UV_T);
        float maxU = tex.getInterpolatedU(MAX_UV_T);
        float minV = tex.getInterpolatedV(MIN_UV_T);
        float maxV = tex.getInterpolatedV(MAX_UV_T);

        builder.pos(matrix, maxX, y, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(minU, minV).overlay(overlay).lightmap(light).normal(normal, 0, ny, 0).endVertex();
        builder.pos(matrix, minX, y, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, minV).overlay(overlay).lightmap(light).normal(normal, 0, ny, 0).endVertex();
        builder.pos(matrix, minX, y, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(normal, 0, ny, 0).endVertex();
        builder.pos(matrix, maxX, y, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(minU, maxV).overlay(overlay).lightmap(light).normal(normal, 0, ny, 0).endVertex();
    }

    private static void drawSides(IVertexBuilder builder, Matrix4f matrix, Matrix3f normal, float height, TextureAtlasSprite tex, int[] color, boolean gas, int overlay, int light)
    {
        float minY = gas ? MAX_Y - (height * (MAX_Y - MIN_Y)) : MIN_Y;
        float maxY = gas ? MAX_Y : MIN_Y + height * (MAX_Y - MIN_Y);

        float minU = tex.getInterpolatedU(MIN_U_S);
        float maxU = tex.getInterpolatedU(MAX_U_S);
        float minV = tex.getInterpolatedV(MIN_V_S);
        float maxV = tex.getInterpolatedV(MIN_V_S + height * (MAX_V_S - MIN_V_S));

        //North
        builder.pos(matrix, MIN_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(minU, minV).overlay(overlay).lightmap(light).normal(normal,  0, 0, -1).endVertex();
        builder.pos(matrix, MAX_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, minV).overlay(overlay).lightmap(light).normal(normal,  0, 0, -1).endVertex();
        builder.pos(matrix, MAX_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(normal,  0, 0, -1).endVertex();
        builder.pos(matrix, MIN_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(minU, maxV).overlay(overlay).lightmap(light).normal(normal,  0, 0, -1).endVertex();

        //South
        builder.pos(matrix, MAX_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(minU, minV).overlay(overlay).lightmap(light).normal(normal,  0, 0,  1).endVertex();
        builder.pos(matrix, MIN_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, minV).overlay(overlay).lightmap(light).normal(normal,  0, 0,  1).endVertex();
        builder.pos(matrix, MIN_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(normal,  0, 0,  1).endVertex();
        builder.pos(matrix, MAX_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(minU, maxV).overlay(overlay).lightmap(light).normal(normal,  0, 0,  1).endVertex();

        //East
        builder.pos(matrix, MAX_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(minU, minV).overlay(overlay).lightmap(light).normal(normal,  1, 0,  0).endVertex();
        builder.pos(matrix, MAX_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, minV).overlay(overlay).lightmap(light).normal(normal,  1, 0,  0).endVertex();
        builder.pos(matrix, MAX_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(normal,  1, 0,  0).endVertex();
        builder.pos(matrix, MAX_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(minU, maxV).overlay(overlay).lightmap(light).normal(normal,  1, 0,  0).endVertex();

        //West
        builder.pos(matrix, MIN_X, maxY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(minU, minV).overlay(overlay).lightmap(light).normal(normal, -1, 0,  0).endVertex();
        builder.pos(matrix, MIN_X, maxY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, minV).overlay(overlay).lightmap(light).normal(normal, -1, 0,  0).endVertex();
        builder.pos(matrix, MIN_X, minY, MIN_Z).color(color[0], color[1], color[2], color[3]).tex(maxU, maxV).overlay(overlay).lightmap(light).normal(normal, -1, 0,  0).endVertex();
        builder.pos(matrix, MIN_X, minY, MAX_Z).color(color[0], color[1], color[2], color[3]).tex(minU, maxV).overlay(overlay).lightmap(light).normal(normal, -1, 0,  0).endVertex();
    }

    private static TextureAtlasSprite getStillTexture(FluidStack fluid)
    {
        return SPRITE_CACHE.getUnchecked(fluid.getFluid().getAttributes().getStillTexture(fluid));
    }

    private static TextureAtlasSprite getFlowingTexture(FluidStack fluid)
    {
        return SPRITE_CACHE.getUnchecked(fluid.getFluid().getAttributes().getFlowingTexture(fluid));
    }

    public static void onResourceReload() { SPRITE_CACHE.invalidateAll(); } //TODO: implement reload listener

    private static LoadingCache<ResourceLocation, TextureAtlasSprite> buildCache ()
    {
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(new CacheLoader<ResourceLocation, TextureAtlasSprite>()
                {
                    @Override
                    public TextureAtlasSprite load(ResourceLocation key)
                    {
                        //noinspection deprecation
                        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(key);
                    }
                });
    }
}