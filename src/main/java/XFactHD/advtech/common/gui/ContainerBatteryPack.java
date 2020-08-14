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

package XFactHD.advtech.common.gui;

import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.energy.TileEntityBatteryPack;
import XFactHD.advtech.common.utils.utilClasses.SlotItemHandlerSpecial;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.function.Function;

public class ContainerBatteryPack extends ContainerPlayerInv
{
    private static final Function<ItemStack, Boolean> CAPACITY_SLOT_CONDITION = new Function<ItemStack, Boolean>() {
        @Override
        @SuppressWarnings("ConstantConditions")
        public Boolean apply(ItemStack stack)
        {
            return stack.getTagCompound().getInteger("storedEnergy") == 0;
        }
    };
    private static final Function<ItemStack, Boolean> CHARGING_SLOT_CONDITION = new Function<ItemStack, Boolean>() {
        @Override
        public Boolean apply(ItemStack stack)
        {
            return stack.hasCapability(CapabilityEnergy.ENERGY, null) ||
                   (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null) && stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null));
        }
    };
    private static final ItemStack[] FILTER = new ItemStack[]
    { new ItemStack(Content.itemBattery, 1, 0), new ItemStack(Content.itemBattery, 1, 1),
    new ItemStack(Content.itemBattery, 1, 2), new ItemStack(Content.itemBattery, 1, 3) };
    private TileEntityBatteryPack te;
    private int stored = 0;
    private int capacity = 0;

    public ContainerBatteryPack(EntityPlayer player, TileEntityBatteryPack te)
    {
        super(player, 0, 21);
        this.te = te;
        addSlotToContainer(new SlotItemHandlerSpecial(te.getItemHandler(), 0,  17, 35,  true, CAPACITY_SLOT_CONDITION, FILTER));
        addSlotToContainer(new SlotItemHandlerSpecial(te.getItemHandler(), 1,  17, 53,  true, CAPACITY_SLOT_CONDITION, FILTER));
        addSlotToContainer(new SlotItemHandlerSpecial(te.getItemHandler(), 2,  17, 71,  true, CAPACITY_SLOT_CONDITION, FILTER));
        addSlotToContainer(new SlotItemHandlerSpecial(te.getItemHandler(), 3, 143, 17, false, CHARGING_SLOT_CONDITION));
        addSlotToContainer(new SlotItemHandlerSpecial(te.getItemHandler(), 4, 143, 71,  true, FALSE_CONDITION));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (stored != te.getEnergyStored())
        {
            stored = te.getEnergyStored();
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 0, stored);
            }
        }
        if (capacity != te.getMaxEnergyStored())
        {
            capacity = te.getMaxEnergyStored();
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 1, capacity);
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id == 0) { stored = data; }
        else if (id == 1) { capacity = data; }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int getEnergyStored()
    {
        return stored;
    }
}