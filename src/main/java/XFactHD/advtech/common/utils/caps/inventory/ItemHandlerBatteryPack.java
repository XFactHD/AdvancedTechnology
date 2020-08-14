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

package XFactHD.advtech.common.utils.caps.inventory;

import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.energy.TileEntityBatteryPack;
import XFactHD.advtech.common.items.energy.ItemBattery;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerBatteryPack extends ItemStackHandler
{
    private ItemBattery.EnumType type;
    private TileEntityBatteryPack te;

    public ItemHandlerBatteryPack(ItemBattery.EnumType type, TileEntityBatteryPack te)
    {
        super(5);
        this.type = type;
        this.te = te;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (slot == 4) { return stack; }
        if (slot == 3) { if (!isEnergyHandler(stack)) { return stack; } }
        if (stack == null) { return null; }
        if (slot < 3)
        {
            if (stack.getItem() != Content.itemBattery || stack.getMetadata() != type.getMeta()) { return stack; }
        }
        return super.insertItem(slot, stack, simulate);
    }

    private boolean isEnergyHandler(ItemStack stack)
    {
        if (stack == null) { return false; }
        return stack.hasCapability(CapabilityEnergy.ENERGY, null) ||
               (stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null) && stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null));
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        te.notifyInventoryUpdate();
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack)
    {
        return 1;
    }

    public void setType(ItemBattery.EnumType type)
    {
        this.type = type;
    }
}