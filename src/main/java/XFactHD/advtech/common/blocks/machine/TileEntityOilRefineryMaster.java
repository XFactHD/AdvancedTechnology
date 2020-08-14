/*  Copyright (C) <2016>  <XFactHD>

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

import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.TileEntityOrientable;
import XFactHD.advtech.common.items.material.ItemMaterial;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyConsumer;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerRefinery;
import XFactHD.advtech.common.utils.caps.inventory.ItemHandlerRefinery;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

//Oil and energy go here, coke dust is removed here
public class TileEntityOilRefineryMaster extends TileEntityOrientable implements ITickable
{
    private static final int BASE_CONSUMPTION_ENERGY = 100;
    private static final int BASE_CONSUMPTION_OIL = 15;
    private static final int BASE_PRODUCTION_GAS = 2;
    private static final int BASE_PRODUCTION_GASOLINE = 3;
    private static final int BASE_PRODUCTION_PETROLEUM = 2;
    private static final int BASE_PRODUCTION_DIESEL = 3;
    private static final int BASE_PRODUCTION_LUBRICANT = 4;
    private static final int BASE_CHANCE_COKE_DUST = 50;
    private boolean active = false;
    private CombinedEnergyConsumer consumer = new CombinedEnergyConsumer(250000, 5000);
    private ItemHandlerRefinery handlerCoke = new ItemHandlerRefinery();
    private FluidHandlerRefinery handlerOil = new FluidHandlerRefinery(5000, true, Content.fluidOil);
    private FluidHandlerRefinery handlerGas = new FluidHandlerRefinery(5000, false, null);
    private FluidHandlerRefinery handlerGasoline = new FluidHandlerRefinery(5000, false, null);
    private FluidHandlerRefinery handlerPetroleum = new FluidHandlerRefinery(5000, false, null);
    private FluidHandlerRefinery handlerDiesel = new FluidHandlerRefinery(5000, false, null);
    private FluidHandlerRefinery handlerLubricant = new FluidHandlerRefinery(5000, false, null);

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            if (consumer.getEnergyStored() >= BASE_CONSUMPTION_ENERGY && canProcess())
            {
                setActive(true);
                consumer.removePowerInternal(BASE_CONSUMPTION_ENERGY, false);
                handlerOil.drainInternal(BASE_CONSUMPTION_OIL, true);
                handlerGas.fillInternal      (new FluidStack(Content.fluidNaturalGas, BASE_PRODUCTION_GAS),       true);
                handlerGasoline.fillInternal (new FluidStack(Content.fluidGasoline,   BASE_PRODUCTION_GASOLINE),  true);
                handlerPetroleum.fillInternal(new FluidStack(Content.fluidPetroleum,  BASE_PRODUCTION_PETROLEUM), true);
                handlerDiesel.fillInternal   (new FluidStack(Content.fluidDiesel,     BASE_PRODUCTION_DIESEL),    true);
                handlerLubricant.fillInternal(new FluidStack(Content.fluidLubricant,  BASE_PRODUCTION_LUBRICANT), true);
                if (worldObj.rand.nextInt(100) > BASE_CHANCE_COKE_DUST)
                {
                    handlerCoke.addStack(new ItemStack(Content.itemMaterial, 1, ItemMaterial.EnumType.COKE_DUST.getMeta()), false);
                }
            }
            else
            {
                setActive(false);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (isEnergyConsumerCapability(capability))
        {
            return side == getFacing().getOpposite();
        }
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return side == getFacing().rotateY() || side == getFacing().rotateYCCW();
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (side == getFacing().getOpposite() && isEnergyConsumerCapability(capability))
        {
            return (T) consumer;
        }
        else if (side == getFacing().rotateY() || side == getFacing().rotateYCCW())
        {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            {
                return (T) handlerCoke;
            }
            else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            {
                return (T) handlerOil;
            }
            return super.getCapability(capability, side);
        }
        return super.getCapability(capability, side);
    }

    public CombinedEnergyConsumer getEnergyHandler()
    {
        return consumer;
    }

    public ItemHandlerRefinery getHandlerCoke()
    {
        return handlerCoke;
    }

    public FluidHandlerRefinery getHandlerOil()
    {
        return handlerOil;
    }

    public FluidHandlerRefinery getHandlerGas()
    {
        return handlerGas;
    }

    public FluidHandlerRefinery getHandlerGasoline()
    {
        return handlerGasoline;
    }

    public FluidHandlerRefinery getHandlerPetroleum()
    {
        return handlerPetroleum;
    }

    public FluidHandlerRefinery getHandlerDiesel()
    {
        return handlerDiesel;
    }

    public FluidHandlerRefinery getHandlerLubricant()
    {
        return handlerLubricant;
    }

    private boolean canProcess()
    {
        return handlerOil.getFluidAmount() >= BASE_CONSUMPTION_OIL &&
               5000 - handlerGas.getFluidAmount() >= BASE_PRODUCTION_GAS &&
               5000 - handlerGasoline.getFluidAmount() >= BASE_PRODUCTION_GASOLINE &&
               5000 - handlerPetroleum.getFluidAmount() >= BASE_PRODUCTION_PETROLEUM &&
               5000 - handlerDiesel.getFluidAmount() >= BASE_PRODUCTION_DIESEL &&
               5000 - handlerLubricant.getFluidAmount() >= BASE_PRODUCTION_LUBRICANT &&
               handlerCoke.addStack(new ItemStack(Content.itemMaterial, 1, ItemMaterial.EnumType.COKE_DUST.getMeta()), true);
    }

    private void setActive(boolean active)
    {
        if (active != this.active)
        {
            this.active = active;
            notifyBlockUpdate();
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        active = nbt.getBoolean("active");
        consumer.deserializeNBT(nbt.getCompoundTag("energy"));
        handlerCoke.deserializeNBT(nbt.getCompoundTag("coke"));
        handlerOil.readFromNBT(nbt.getCompoundTag("oil"));
        handlerGas.readFromNBT(nbt.getCompoundTag("gas"));
        handlerGasoline.readFromNBT(nbt.getCompoundTag("gasoline"));
        handlerPetroleum.readFromNBT(nbt.getCompoundTag("petroleum"));
        handlerDiesel.readFromNBT(nbt.getCompoundTag("diesel"));
        handlerLubricant.readFromNBT(nbt.getCompoundTag("lubricant"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setBoolean("active", active);
        nbt.setTag("energy", consumer.serializeNBT());
        nbt.setTag("coke", handlerCoke.serializeNBT());
        nbt.setTag("oil", handlerOil.writeToNBT(new NBTTagCompound()));
        nbt.setTag("gas", handlerGas.writeToNBT(new NBTTagCompound()));
        nbt.setTag("gasoline", handlerGasoline.writeToNBT(new NBTTagCompound()));
        nbt.setTag("petroleum", handlerPetroleum.writeToNBT(new NBTTagCompound()));
        nbt.setTag("diesel", handlerDiesel.writeToNBT(new NBTTagCompound()));
        nbt.setTag("lubricant", handlerLubricant.writeToNBT(new NBTTagCompound()));
    }
}