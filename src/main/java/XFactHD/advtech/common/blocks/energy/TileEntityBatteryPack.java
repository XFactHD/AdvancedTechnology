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

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.api.block.IInventoryTile;
import XFactHD.advtech.common.blocks.TileEntityBase;
import XFactHD.advtech.common.items.energy.ItemBattery;
import XFactHD.advtech.common.net.PacketBatteryPackSideSetting;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyStorage;
import XFactHD.advtech.common.utils.caps.inventory.ItemHandlerBatteryPack;
import XFactHD.advtech.common.utils.utilClasses.SideSetting;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class TileEntityBatteryPack extends TileEntityBase implements ITickable, IInventoryTile
{
    private ItemBattery.EnumType type = null;
    private CombinedEnergyStorage storage;
    private ItemHandlerBatteryPack inv;
    private HashMap<EnumFacing, SideSetting> settings = new HashMap<>();

    public TileEntityBatteryPack()
    {
        storage = new CombinedEnergyStorage(0, 0, 0);
        inv = new ItemHandlerBatteryPack(ItemBattery.EnumType.NICD, this);
    }

    public TileEntityBatteryPack(ItemBattery.EnumType type)
    {
        this.type = type;
        storage = new CombinedEnergyStorage(type.getCapacity(), type.getTransfer() * 5, type.getTransfer() * 5);
        inv = new ItemHandlerBatteryPack(type, this);
        for (EnumFacing facing : EnumFacing.values())
        {
            settings.put(facing, SideSetting.NONE);
        }
    }

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            if (isEnergyHandler(inv.getStackInSlot(3)))
            {
                chargeStack(inv.getStackInSlot(3));
            }
            for (EnumFacing side : EnumFacing.values())
            {
                if (settings.get(side) == SideSetting.OUT)
                {
                    TileEntity te = worldObj.getTileEntity(pos.offset(side));
                    if (te != null && te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite()))
                    {
                        ITeslaConsumer consumer = te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite());
                        long maxTransfer = consumer.givePower(storage.getMaxExtract(), true);
                        consumer.givePower(storage.removePowerInternal(maxTransfer, false), false);
                    }
                    else if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite()))
                    {
                        IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                        int maxTransfer = energy.receiveEnergy((int) Math.min(Integer.MAX_VALUE, storage.getMaxExtract()), true);
                        energy.receiveEnergy((int) storage.removePowerInternal(maxTransfer, false), false);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (capability == TeslaCapabilities.CAPABILITY_CONSUMER)
        {
            return settings.get(side) == SideSetting.IN;
        }
        else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER)
        {
            return settings.get(side) == SideSetting.OUT;
        }
        else if (capability == CapabilityEnergy.ENERGY)
        {
            return settings.get(side) != SideSetting.NONE;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && settings.get(side) == SideSetting.IN)
        {
            storage.setSetting(settings.get(side));
            return (T)storage;
        }
        else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER && settings.get(side) == SideSetting.OUT)
        {
            storage.setSetting(settings.get(side));
            return (T)storage;
        }
        else if (capability == CapabilityEnergy.ENERGY && settings.get(side) != SideSetting.NONE)
        {
            storage.setSetting(settings.get(side));
            return (T)storage;
        }
        return super.getCapability(capability, side);
    }

    public void cycleSetting(EnumFacing side)
    {
        settings.put(side, settings.get(side).next());
        markDirty();
        notifyBlockUpdate();
    }

    public void setSetting(EnumFacing side, SideSetting setting)
    {
        if (worldObj.isRemote)
        {
            AdvancedTechnology.NET.sendMessageToServer(new PacketBatteryPackSideSetting(pos, side, setting));
        }
        else
        {
            settings.put(side, setting);
            markDirty();
            notifyBlockUpdate();
        }
    }

    public SideSetting getSetting(EnumFacing side)
    {
        return settings.get(side);
    }

    @Override
    public void notifyInventoryUpdate()
    {
        if (!worldObj.isRemote)
        {
            storage.setCapacity(type.getCapacity() * getFilledSlots());
        }
    }

    private int getFilledSlots()
    {
        int count = 1;
        for (int i = 0; i < 4; i++)
        {
            if (inv.getStackInSlot(i) != null)
            {
                count += 1;
            }
        }
        return count;
    }

    public ItemHandlerBatteryPack getItemHandler()
    {
        return inv;
    }

    public int getEnergyStored()
    {
        return storage.getEnergyStored();
    }

    public int getMaxEnergyStored()
    {
        return storage.getMaxEnergyStored();
    }

    private boolean isEnergyHandler(ItemStack stack)
    {
        if (stack == null) { return false; }
        return stack.hasCapability(CapabilityEnergy.ENERGY, null) ||
                (stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null) && stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null));
    }

    @Override
    public IItemHandler getInventory()
    {
        return getItemHandler();
    }

    private void chargeStack(@Nonnull ItemStack stack)
    {
        if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null))
        {
            ITeslaConsumer consumer = stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
            long maxTransfer = consumer.givePower(storage.getMaxExtract(), true);
            consumer.givePower(storage.removePowerInternal(maxTransfer, false), false);
            if (stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null))
            {
                ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
                if (holder.getStoredPower() == holder.getCapacity())
                {
                    if (inv.getStackInSlot(4) == null)
                    {
                        inv.setStackInSlot(4, stack);
                        inv.setStackInSlot(3, null);
                    }
                }
                return;
            }
            if (consumer.givePower(1, true) == 0)
            {
                if (inv.getStackInSlot(4) == null)
                {
                    inv.setStackInSlot(4, stack);
                    inv.setStackInSlot(3, null);
                }
            }
        }
        else if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
        {
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
            int maxTransfer = energy.receiveEnergy((int) Math.min(Integer.MAX_VALUE, storage.getMaxExtract()), true);
            energy.receiveEnergy((int) storage.takePower(maxTransfer, false), false);
            if (energy.getEnergyStored() == energy.getMaxEnergyStored())
            {
                if (inv.getStackInSlot(4) == null)
                {
                    inv.setStackInSlot(4, stack);
                    inv.setStackInSlot(3, null);
                }
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("type", type.getMeta());
        nbt.setTag("storage", storage.serializeNBT());
        nbt.setTag("inv", inv.serializeNBT());
        for (EnumFacing facing : EnumFacing.values())
        {
            nbt.setInteger(facing.getName(), settings.get(facing).ordinal());
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        type = ItemBattery.EnumType.values()[nbt.getInteger("type")];
        if (storage.getMaxEnergyStored() == 0)
        {
            storage.setCapacity(type.getCapacity());
            storage.setMaxTransfer(type.getTransfer() * 5);
            inv.setType(type);
        }
        inv.deserializeNBT(nbt.getCompoundTag("inv"));
        storage.deserializeNBT(nbt.getCompoundTag("storage"));
        for (EnumFacing facing : EnumFacing.values())
        {
            settings.put(facing, SideSetting.values()[nbt.getInteger(facing.getName())]);
        }
    }
}