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

package XFactHD.advtech.common.blocks.machine;

import XFactHD.advtech.common.blocks.TileEntityBase;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyConsumer;
import XFactHD.advtech.common.utils.caps.inventory.ItemHandlerQuarry;
import com.mojang.authlib.GameProfile;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class TileEntityQuarry extends TileEntityBase implements ITickable
{
    private GameProfile owner = null;
    private WeakReference<EntityPlayer> fakePlayer = null;
    private int rangeNorth = 1;
    private int rangeEast = 1;
    private boolean running = false;
    private boolean frameBuilt = false;
    private BlockPos currentPos = null;
    private CombinedEnergyConsumer consumer = new CombinedEnergyConsumer(500000, 2500);
    private ItemHandlerQuarry itemHandler = new ItemHandlerQuarry();

    @Override
    public void update()
    {
        if (!worldObj.isRemote && running)
        {
            if (!frameBuilt)
            {
                buildFrame();
            }
            else
            {
                fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer)worldObj, owner));
            }
        }
    }

    private void buildFrame()
    {

    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return facing == EnumFacing.UP;
        }
        else if (capability == CapabilityEnergy.ENERGY || capability == TeslaCapabilities.CAPABILITY_CONSUMER)
        {
            return facing.getAxis() != EnumFacing.Axis.Y;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.UP)
        {
            return (T)itemHandler;
        }
        else if ((capability == CapabilityEnergy.ENERGY || capability == TeslaCapabilities.CAPABILITY_CONSUMER) && facing.getAxis() != EnumFacing.Axis.Y)
        {
            return (T)consumer;
        }
        return super.getCapability(capability, facing);
    }

    public int getRangeNorth()
    {
        return rangeNorth;
    }

    public void setRangeNorth(int rangeNorth)
    {
        this.rangeNorth = rangeNorth;
        notifyBlockUpdate();
    }

    public int getRangeEast()
    {
        return rangeEast;
    }

    public void setRangeEast(int rangeEast)
    {
        this.rangeEast = rangeEast;
        notifyBlockUpdate();
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
        notifyBlockUpdate();
    }

    public int getEnergyStored()
    {
        return consumer.getEnergyStored();
    }

    public int getMaxEnergyStored()
    {
        return consumer.getMaxEnergyStored();
    }

    public void setOwner(GameProfile owner)
    {
        this.owner = owner;
    }

    public GameProfile getOwner()
    {
        return owner;
    }

    @Override
    public void onLoad()
    {
        currentPos = pos.north();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        rangeNorth = nbt.getInteger("rangeNorth");
        rangeEast = nbt.getInteger("rangeEast");
        running = nbt.getBoolean("running");
        owner = new GameProfile(UUID.fromString(nbt.getString("owner")), nbt.getString("ownerName"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("rangeNorth", rangeNorth);
        nbt.setInteger("rangeEast", rangeEast);
        nbt.setBoolean("running", running);
        nbt.setString("owner", owner.getId().toString());
        nbt.setString("ownerName", owner.getName());
    }

    public ItemHandlerQuarry getItemHandler()
    {
        return itemHandler;
    }
}