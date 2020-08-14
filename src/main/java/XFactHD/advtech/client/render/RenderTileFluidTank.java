/*  Copyright (C) <2016>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.client.render;

import XFactHD.advtech.client.utils.ClientUtils;
import XFactHD.advtech.common.blocks.storage.TileEntityFluidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderTileFluidTank extends TileEntitySpecialRenderer<TileEntityFluidTank>
{
    @Override
    public void renderTileEntityAt(TileEntityFluidTank te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        int capacity = te.getFluidHandler().getCapacity();
        FluidStack fluid = te.getFluidHandler().getFluid();
        if (fluid != null)
        {
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();

            buffer.setTranslation(x, y, z);

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString());
            TextureAtlasSprite flow =  Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getFlowing().toString());

            double posY = .1 + (.8 * ((float) fluid.amount / (float) capacity));
            double topV = 15 - (14 * (.8 * ((float) fluid.amount / (float) capacity)));
            float[] color = ClientUtils.getRGBAFloatArrayFromHexColor(fluid.getFluid().getColor(fluid));

            RenderHelper.disableStandardItemLighting();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos( 4F/16F, posY, 12F/16F).tex(still.getInterpolatedU( 4), still.getInterpolatedV( 4)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F, posY, 12F/16F).tex(still.getInterpolatedU(12), still.getInterpolatedV( 4)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F, posY,  4F/16F).tex(still.getInterpolatedU(12), still.getInterpolatedV(12)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos( 4F/16F, posY,  4F/16F).tex(still.getInterpolatedU( 4), still.getInterpolatedV(12)).color(color[0], color[1], color[2], color[3]).endVertex();
            tess.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(12F/16F, 1/16F, 12F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F,  posY, 12F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos( 4F/16F,  posY, 12F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos( 4F/16F, 1/16F, 12F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            tess.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos( 4F/16F, 1/16F, 4F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos( 4F/16F,  posY, 4F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F,  posY, 4F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F, 1/16F, 4F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            tess.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(12F/16F, 1/16F,  4F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F,  posY,  4F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F,  posY, 12F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(12F/16F, 1/16F, 12F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            tess.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(4F/16F, 1/16F, 12F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(4F/16F,  posY, 12F/16F).tex(flow.getInterpolatedU(12), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(4F/16F,  posY,  4F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(topV)).color(color[0], color[1], color[2], color[3]).endVertex();
            buffer.pos(4F/16F, 1/16F,  4F/16F).tex(flow.getInterpolatedU( 4), flow.getInterpolatedV(  15)).color(color[0], color[1], color[2], color[3]).endVertex();
            tess.draw();

            RenderHelper.enableStandardItemLighting();

            buffer.setTranslation(0, 0, 0);
        }
        GlStateManager.popMatrix();
    }
}