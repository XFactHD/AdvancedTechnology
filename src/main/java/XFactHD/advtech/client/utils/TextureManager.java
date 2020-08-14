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

import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.Utils;
import XFactHD.advtech.common.utils.utilClasses.TexConType;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

public enum  TextureManager
{
    INSTANCE;

    private HashMap<EnumDyeColor, HashMap<TexConType, TextureAtlasSprite>> concreteSprites = new HashMap<>();

    @SubscribeEvent
    public void stitch(TextureStitchEvent.Pre event)
    {
        stitchConcreteSprites(event);
    }

    private void stitchConcreteSprites(TextureStitchEvent.Pre event)
    {
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            HashMap<TexConType, TextureAtlasSprite> sprites = new HashMap<>();

            String col = Utils.toTitleCase(color.getUnlocalizedName());

            for (TexConType connection : TexConType.values())
            {
                String con = connection.toString().toUpperCase();
                String name = "blocks/material/concrete/blockConcrete_" + col + "_" + con;
                sprites.put(connection, event.getMap().registerSprite(new ResourceLocation(Reference.MOD_ID, name)));
            }
            concreteSprites.put(color, sprites);
        }
    }

    public TextureAtlasSprite getConcreteSprite(EnumDyeColor color, TexConType conType)
    {
        return concreteSprites.get(color).get(conType);
    }
}