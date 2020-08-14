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

import XFactHD.advtech.client.models.ModelBlockConcrete;
import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum ModelManager
{
    INSTANCE;

    @SubscribeEvent
    public void bakeModels(ModelBakeEvent event)
    {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            ModelResourceLocation location = new ModelResourceLocation(Content.blockConcrete.getRegistryName(), "color=" + color.getName());
            IBakedModel model = registry.getObject(location);
            IBakedModel bakedModel = new ModelBlockConcrete(model);
            registry.putObject(location, bakedModel);
        }
    }
}