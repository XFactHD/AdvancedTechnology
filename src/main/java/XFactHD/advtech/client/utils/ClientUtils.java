/*  Copyright (C) <2017>  <XFactHD>

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

package XFactHD.advtech.client.utils;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ClientUtils
{
    public static BakedQuad createQuadWithSpriteSimple(BakedQuad quad, TextureAtlasSprite sprite)
    {
        int[] vertexData = changeTextureUV(quad.getVertexData(), sprite);
        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getFace(), sprite, quad.shouldApplyDiffuseLighting(), quad.getFormat());
    }

    public static BakedQuad createQuadWithSpriteAbnormalUV(BakedQuad quad, TextureAtlasSprite sprite)
    {
        return new BakedQuadRetextured(quad, sprite);
    }

    private static int[] changeTextureUV(int[] vd, TextureAtlasSprite sprite)
    {
        int[] vertices = new int[28];
        vertices[0]  = vd[0]; //x
        vertices[1]  = vd[1]; //y
        vertices[2]  = vd[2]; //z
        vertices[3]  = vd[3]; //color
        vertices[4]  = Float.floatToRawIntBits(sprite.getInterpolatedU(16)); //u
        vertices[5]  = Float.floatToRawIntBits(sprite.getInterpolatedV(16)); //v
        vertices[6]  = vd[6]; //unused

        vertices[7]  = vd[7]; //x
        vertices[8]  = vd[8]; //y
        vertices[9]  = vd[9]; //z
        vertices[10] = vd[10]; //color
        vertices[11] = Float.floatToRawIntBits(sprite.getInterpolatedU(16)); //u
        vertices[12] = Float.floatToRawIntBits(sprite.getInterpolatedV(0)); //v
        vertices[13] = vd[13]; //unused

        vertices[14] = vd[14]; //x
        vertices[15] = vd[15]; //y
        vertices[16] = vd[16]; //z
        vertices[17] = vd[17]; //color
        vertices[18] = Float.floatToRawIntBits(sprite.getInterpolatedU(0)); //u
        vertices[19] = Float.floatToRawIntBits(sprite.getInterpolatedV(0)); //v
        vertices[20] = vd[20]; //unused

        vertices[21] = vd[21]; //x
        vertices[22] = vd[22]; //y
        vertices[23] = vd[23]; //z
        vertices[24] = vd[24]; //color
        vertices[25] = Float.floatToRawIntBits(sprite.getInterpolatedU(0)); //u
        vertices[26] = Float.floatToRawIntBits(sprite.getInterpolatedV(16)); //v
        vertices[27] = vd[27]; //unused
        return vertices;
    }

    public static float[] getRGBAFloatArrayFromHexColor(int color)
    {
        float[] floats = new float[4];
        floats[0] = (color >> 16 & 255) / 255.0F;
        floats[1] = (color >> 8 & 255) / 255.0F;
        floats[2] = (color & 255) / 255.0F;
        floats[3] = 1;
        return floats;
    }
}
