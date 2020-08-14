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

package XFactHD.advtech.common.blocks.material;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockStorage extends BlockBase
{
    public BlockStorage()
    {
        super("blockStorage", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
        for (int i = 0; i < EnumType.values().length; i++)
        {
            OreDictionary.registerOre(EnumType.values()[i].getOreDict(), new ItemStack(this, 1, i));
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.STORAGE_TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.STORAGE_TYPE, EnumType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.STORAGE_TYPE).ordinal();
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return false;
    }

    public enum EnumType implements IStringSerializable
    {
        SULFUR("blockSulfur"),
        COKE("blockCoke");

        private String oreDict;

        EnumType(String oreDict)
        {
            this.oreDict = oreDict;
        }

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }

        public String getOreDict()
        {
            return oreDict;
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.getName();
            }
            return strings;
        }
    }
}