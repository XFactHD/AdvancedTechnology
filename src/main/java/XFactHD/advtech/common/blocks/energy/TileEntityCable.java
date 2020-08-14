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

package XFactHD.advtech.common.blocks.energy;

import XFactHD.advtech.common.blocks.TileEntityBase;
import XFactHD.advtech.common.blocks.energy.BlockCable.EnumType;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyCable;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.ArrayList;
import java.util.List;

public class TileEntityCable extends TileEntityBase implements ITickable
{
    private EnumType type;
    private CombinedEnergyCable cable = new CombinedEnergyCable(this, 500);
    private List<EnumFacing> ignoreList = new ArrayList<>();

    public TileEntityCable()
    {
        cable = new CombinedEnergyCable(this, EnumType.COPPER.getMaxTransfer());
    }

    public TileEntityCable(EnumType type)
    {
        this.type = type;
        cable = new CombinedEnergyCable(this, type.getMaxTransfer());
    }

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            for (EnumFacing side : EnumFacing.values())
            {
                transferEnergy(side);
            }
            ignoreList.clear();
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean hasCapability(Capability<?> cap, EnumFacing facing)
    {
        return (facing != null && cap == CapabilityEnergy.ENERGY || cap == TeslaCapabilities.CAPABILITY_CONSUMER ||
               cap == TeslaCapabilities.CAPABILITY_PRODUCER) || super.hasCapability(cap, facing);
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T getCapability(Capability<T> cap, EnumFacing facing)
    {
        if (facing != null && (cap == CapabilityEnergy.ENERGY || cap == TeslaCapabilities.CAPABILITY_CONSUMER || cap == TeslaCapabilities.CAPABILITY_PRODUCER))
        {
            cable.setCurrentSide(facing);
            return (T)cable;
        }
        return super.getCapability(cap, facing);
    }

    public void addSideToIgnore(EnumFacing side)
    {
        ignoreList.add(side);
    }

    private void transferEnergy(EnumFacing side)
    {
        TileEntity te = worldObj.getTileEntity(pos.offset(side));
        if (te != null && (!ignoreList.contains(side) || te instanceof TileEntityCable))
        {
            if (te instanceof TileEntityCable)
            {
                long toTransfer = ((TileEntityCable)te).cable.givePower(cable.getMaxTransfer(), true);
                ((TileEntityCable)te).cable.givePower(cable.takePower(toTransfer, false), false);
            }
            else if (te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite()))
            {
                long toTransfer = te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite()).givePower(cable.getMaxTransfer(), true);
                te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite()).givePower(cable.takePower(toTransfer, false), false);
            }
            else if (te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite()))
            {
                int toTransfer = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).receiveEnergy(cable.getMaxTransfer(), true);
                te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).receiveEnergy(cable.extractEnergy(toTransfer, false), false);
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        type = EnumType.values()[nbt.getInteger("type")];
        cable.deserializeNBT(nbt.getCompoundTag("storage"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("type", type.ordinal());
        nbt.setTag("storage", cable.serializeNBT());
    }
}