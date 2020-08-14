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

package XFactHD.advtech.client.models;

import XFactHD.advtech.client.utils.ClientUtils;
import XFactHD.advtech.client.utils.TextureManager;
import XFactHD.advtech.common.utils.utilClasses.TexConType;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class ModelBlockConcrete implements IBakedModel
{
    private IBakedModel baseModel;

    public ModelBlockConcrete(IBakedModel baseModel)
    {
        this.baseModel = baseModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (state == null || side == null) { return baseModel.getQuads(state, side, rand); }
        boolean up    = state.getValue(PropertyHolder.CONNECTED_U);
        boolean down  = state.getValue(PropertyHolder.CONNECTED_D);
        boolean north = state.getValue(PropertyHolder.CONNECTED_N);
        boolean south = state.getValue(PropertyHolder.CONNECTED_S);
        boolean west  = state.getValue(PropertyHolder.CONNECTED_W);
        boolean east  = state.getValue(PropertyHolder.CONNECTED_E);
        if (!up && !down && !north && !south && !west && !east) { return baseModel.getQuads(state, side, rand); }

        TextureAtlasSprite spriteForSide;
        List<BakedQuad> quads = new ArrayList<>();
        EnumDyeColor color = state.getValue(PropertyHolder.COLOR);
        TexConType conType;

        switch (side)
        {
            case UP:    conType = TexConType.getForBooleans(south, north, west, east); break;
            case DOWN:  conType = TexConType.getForBooleans(north, south, west, east); break;
            case NORTH: conType = TexConType.getForBooleans(down, up, east, west); break;
            case SOUTH: conType = TexConType.getForBooleans(down, up, west, east); break;
            case EAST:  conType = TexConType.getForBooleans(down, up, south, north); break;
            case WEST:  conType = TexConType.getForBooleans(down, up, north, south); break;
            default: return baseModel.getQuads(state, side, rand);
        }

        spriteForSide = TextureManager.INSTANCE.getConcreteSprite(color, conType);
        quads.add(ClientUtils.createQuadWithSpriteSimple(baseModel.getQuads(state, side, rand).get(0), spriteForSide));
        return quads;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return baseModel.getOverrides();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return baseModel.getParticleTexture();
    }
}