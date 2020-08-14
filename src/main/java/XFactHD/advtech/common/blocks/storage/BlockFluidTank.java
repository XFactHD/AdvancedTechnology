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

package XFactHD.advtech.common.blocks.storage;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockFluidTank extends BlockBase
{
    public BlockFluidTank()
    {
        super("blockFluidTank", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        setSoundType(SoundType.GLASS);
        registerTileEntity(TileEntityFluidTank.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FLUID_TANK_TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.FLUID_TANK_TYPE, EnumType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FLUID_TANK_TYPE).ordinal();
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        if (heldItem != null && heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            if (!world.isRemote)
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityFluidTank)
                {
                    IFluidHandler handler = ((TileEntityFluidTank)te).getFluidHandler();
                    return FluidUtil.interactWithFluidHandler(heldItem, handler, player);
                }
            }
            return true;
        }
        else if (!world.isRemote)
        {
            player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_FLUID_TANK, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBlockWrenched(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, boolean sneaking)
    {
        if (sneaking)
        {
            ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityFluidTank)
            {
                stack.setTagCompound(((TileEntityFluidTank)te).writeTankToNBT());
            }
            if (world.destroyBlock(pos, false))
            {
                dropBlockAsItemSpecial(world, pos, stack);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFluidTank && stack.hasTagCompound())
        {
            ((TileEntityFluidTank)te).readTankFromNBT(stack.getTagCompound());
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFluidTank)
        {
            stack.setTagCompound(((TileEntityFluidTank)te).writeTankToNBT());
        }
        return stack;
    }

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return new AxisAlignedBB(.1875, 0, .1875, .8125, 1, .8125);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityFluidTank(state.getValue(PropertyHolder.FLUID_TANK_TYPE));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            FluidStack fluid = FluidRegistry.getFluidStack(stack.getTagCompound().getString("FluidName"), stack.getTagCompound().getInteger("Amount"));
            tooltip.add(I18n.format("desc.advtech:fluid.name") + " " + (fluid != null ? fluid.getLocalizedName() : I18n.format("desc.advtech:empty.name")) + " mB");
            tooltip.add(I18n.format("desc.advtech:amount.name") + " " + (fluid != null ? Integer.toString(stack.getTagCompound().getInteger("Amount")) : "0") + " mB");
        }
        else
        {
            tooltip.add(I18n.format("desc.advtech:fluid.name") + " " + I18n.format("desc.advtech:empty.name"));
            tooltip.add(I18n.format("desc.advtech:amount.name") + " " + "0 mB");
        }
    }

    public enum EnumType implements IStringSerializable
    {
        BASIC(8000),
        IMPROVED(32000),
        ADVANCED(128000),
        ULTIMATE(512000);

        private int capacity;

        EnumType(int capacity)
        {
            this.capacity = capacity;
        }

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }

        public int getCapacity()
        {
            return capacity;
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