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

package XFactHD.advtech.common.items.energy;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.items.ItemBase;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyStorageItem;
import XFactHD.advtech.common.utils.helpers.LogHelper;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemBattery extends ItemBase
{
    public ItemBattery()
    {
        super("itemBattery", 16, AdvancedTechnology.creativeTab, EnumType.getAsStringArray());
        addPropertyOverride(new ResourceLocation("battery"), new PropertyOverrideBattery());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public double getDurabilityForDisplay(ItemStack stack)
    {
        float stored = stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
        float max = stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
        return 1 - stored / max;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        if (!stack.hasTagCompound()) { stack.setTagCompound(new NBTTagCompound()); }
        float stored = stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
        float max = stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
        stack.getTagCompound().setInteger("lastSegment", Math.round(10 * (stored / max)));

        //TODO: debug code, charges the battery from nothing
        //stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(EnumType.values()[stack.getMetadata()].getTransfer(), false);

        CombinedEnergyStorageItem storage = (CombinedEnergyStorageItem) stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (storage.shouldLoadFromStack()) { storage.loadFromItemStack(); }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if (!oldStack.hasTagCompound() || !newStack.hasTagCompound()) { return slotChanged; }
        float stored = newStack.getTagCompound().getInteger("storedEnergy");
        float max = newStack.getTagCompound().getInteger("maxEnergy");
        int segment = Math.round(10 * (stored / max));
        return slotChanged || segment != oldStack.getTagCompound().getInteger("lastSegment");
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new CombinedEnergyStorageItem(stack, nbt);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        int stored = stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
        int max = stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
        tooltip.add(I18n.format("desc.advtech:stored.name") + " " + stored + "/" + max + "T");
    }

    public enum EnumType implements IStringSerializable
    {
        NICD(2000, 20),       //Nickel Ingot, Cadmium Ingot, Water Bucket
        NIMH(32000, 150),     //Nickel Ingot, Water Bucket, Iron Dust
        PB(480000, 500),      //Lead Ingot, Sulfuric Acid, Lead Ingot
        LIION(1500000, 1000); //Lithium Ingot, Electrolite Bottle, Coal Dust

        private int capacity;
        private int transfer;

        EnumType(int capacity, int transfer)
        {
            this.capacity = capacity;
            this.transfer = transfer;
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

        public int getTransfer()
        {
            return transfer;
        }

        public int getMeta()
        {
            return ordinal();
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.toString().toLowerCase(Locale.ENGLISH);
            }
            return strings;
        }
    }

    private static class PropertyOverrideBattery implements IItemPropertyGetter
    {
        @Override
        @SuppressWarnings("ConstantConditions")
        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
        {
            float stored = stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
            float max = stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
            return Math.round(10 * (stored / max));
        }
    }
}