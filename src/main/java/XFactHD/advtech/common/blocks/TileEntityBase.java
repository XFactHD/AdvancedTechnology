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

package XFactHD.advtech.common.blocks;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public abstract class TileEntityBase extends TileEntity
{
    public abstract void writeCustomNBT(NBTTagCompound nbt);

    public abstract void readCustomNBT(NBTTagCompound nbt);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeCustomNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        if (worldObj != null)
        {
            worldObj.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbt = super.getUpdateTag();
        writeCustomNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        super.handleUpdateTag(tag);
        if (worldObj != null)
        {
            worldObj.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCustomNBT(nbt);
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readCustomNBT(pkt.getNbtCompound());
        if (worldObj != null)
        {
            worldObj.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    public void notifyBlockUpdate()
    {
        markDirty();
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
    }

    public void notifyInventoryUpdate()
    {

    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    protected final IBlockState getState()
    {
        return worldObj.getBlockState(pos);
    }

    protected final boolean isEnergyConsumerCapability(Capability<?> cap)
    {
        return cap == TeslaCapabilities.CAPABILITY_CONSUMER || cap == CapabilityEnergy.ENERGY;
    }

    protected final boolean isEnergyProviderCapability(Capability<?> cap)
    {
        return cap == TeslaCapabilities.CAPABILITY_PRODUCER || cap == CapabilityEnergy.ENERGY;
    }
}