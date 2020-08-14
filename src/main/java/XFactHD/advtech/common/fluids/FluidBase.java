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

package XFactHD.advtech.common.fluids;

import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidBase extends Fluid
{
    public FluidBase(String name, ResourceLocation still, ResourceLocation flowing, boolean hasBucket)
    {
        super("advtech:" + name, still, flowing);
        FluidRegistry.registerFluid(this);
        setBlock(new BlockFluidClassic(this, Material.WATER));
        getBlock().setRegistryName(Reference.MOD_ID, "block" + Utils.toTitleCase(name));
        GameRegistry.register(getBlock());
        if (hasBucket) { FluidRegistry.addBucketForFluid(this); }
    }

    public class FluidSimple extends FluidBase
    {
        public FluidSimple(String fluidName, int density, int temperature, boolean hasBucket, boolean gas)
        {
            super(fluidName, new ResourceLocation(Reference.MOD_ID, "fluids/" + fluidName + "_still"),
                  new ResourceLocation(Reference.MOD_ID, "fluids/" + fluidName + "_flowing"), hasBucket);
            if (density != -1)
            {
                setDensity(density);
            }
            if (temperature != -1)
            {
                setTemperature(temperature);
            }
            setGaseous(gas);
        }
    }

    public static class FluidColored extends FluidBase
    {
        private int color;

        public FluidColored(String fluidName, int density, int temperature, boolean hasBucket, boolean gas, int color)
        {
            super(fluidName, new ResourceLocation(Reference.MOD_ID, "fluids/fluidColored_still"),
                  new ResourceLocation(Reference.MOD_ID, "fluids/fluidColored_flowing"), hasBucket);
            if (density != -1)
            {
                setDensity(density);
            }
            if (temperature != -1)
            {
                setTemperature(temperature);
            }
            setGaseous(gas);
        }

        @Override
        public int getColor()
        {
            return color;
        }
    }
}