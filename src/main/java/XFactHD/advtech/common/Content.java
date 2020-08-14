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

package XFactHD.advtech.common;

import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.blocks.energy.*;
import XFactHD.advtech.common.blocks.light.*;
import XFactHD.advtech.common.blocks.machine.*;
import XFactHD.advtech.common.blocks.material.*;
import XFactHD.advtech.common.blocks.storage.*;
import XFactHD.advtech.common.blocks.transport.*;
import XFactHD.advtech.common.crafting.Crafting;
import XFactHD.advtech.common.fluids.FluidBase;
import XFactHD.advtech.common.items.ItemBase;
import XFactHD.advtech.common.items.energy.*;
import XFactHD.advtech.common.items.material.*;
import XFactHD.advtech.common.items.tool.*;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class Content
{
    public static BlockBase blockOre;
    public static BlockBase blockMetal;
    public static BlockBase blockStorage;
    public static BlockBase blockConcrete;
    public static BlockBase blockBatteryPack;
    public static BlockBase blockSolarPanel;
    public static BlockBase blockCable;
    public static BlockBase blockEnergyVoid;
    public static BlockBase blockCrusher;
    public static BlockBase blockElectricFurnace;
    public static BlockBase blockAlloyFurnace;
    public static BlockBase blockMetalPress;
    public static BlockBase blockChemoLuminator;
    public static BlockBase blockChemicalReactor;
    public static BlockBase blockQuarry;
    public static BlockBase blockFluidPipe;
    public static BlockBase blockItemPipe;
    public static BlockBase blockFluidTank;
    public static BlockBase blockWirelessFeeder;
    public static BlockBase blockWirelessNode;
    public static BlockBase blockOilRefinery;
    public static BlockBase blockGasLantern;

    public static ItemBase itemMaterial;
    public static ItemBase itemMaterialLight;
    public static ItemBase itemTool;
    public static ItemBase itemBattery;
    public static ItemBase itemUpgrade;

    public static FluidBase fluidHydrogenPeroxide;
    public static FluidBase fluidOxalAcid;
    public static FluidBase fluidSulfuricAcid;
    public static FluidBase fluidBiogas;
    public static FluidBase fluidOxygen;
    public static FluidBase fluidHydrogen;
    public static FluidBase fluidOil;
    public static FluidBase fluidNaturalGas;
    public static FluidBase fluidGasoline;
    public static FluidBase fluidPetroleum;
    public static FluidBase fluidDiesel;
    public static FluidBase fluidLubricant;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        BlockBase.setRegistry(event.getRegistry());

        blockOre =               new BlockOre();
        blockMetal =             new BlockMetal();
        blockStorage =           new BlockStorage();
        blockConcrete =          new BlockConcrete();
        blockBatteryPack =       new BlockBatteryPack();
        blockSolarPanel =        new BlockSolarPanel();
        blockCable =             new BlockCable();
        blockEnergyVoid =        new BlockEnergyVoid();
        //blockCrusher =         new BlockCrusher;
        blockElectricFurnace =   new BlockElectricFurnace();
        //blockAlloyFurnace =    new BlockAlloyFurnace;
        //blockMetalPress =      new BlockMetalPress;
        blockChemoLuminator =    new BlockChemoLuminator();
        //blockChemicalReactor = new BlockChemicalReactor;
        blockQuarry =            new BlockQuarry();
        blockFluidPipe =         new BlockFluidPipe();
        //blockItemPipe =        new BlockItemPipe;
        blockFluidTank =         new BlockFluidTank();
        blockWirelessFeeder =    new BlockWirelessFeeder();
        blockWirelessNode =      new BlockWirelessNode();
        blockOilRefinery =       new BlockOilRefinery();
        blockGasLantern =        new BlockGasLantern();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        BlockBase.registerItemBlocks(event.getRegistry());

        itemMaterial =      new ItemMaterial();
        itemMaterialLight = new ItemMaterialLight();
        itemTool =          new ItemTool();
        itemBattery =       new ItemBattery();
        itemUpgrade =       new ItemUpgrade();
    }

    public static void preInit()
    {
        fluidHydrogenPeroxide = new FluidBase.FluidColored("hydrogenPeroxide", -1, -1, true, false, 0x556ed3);
        fluidOxalAcid =         new FluidBase.FluidColored("oxalAcid",         -1, -1, true, false, 0xc5c8d3);
        fluidSulfuricAcid =     new FluidBase.FluidColored("sulfuricAcid",     -1, -1, true, false, 0x9fa100);
        fluidBiogas =           new FluidBase.FluidColored("biogas",         -500, -1, true,  true, 0x111111); //TODO: find a good color
        fluidOxygen =           new FluidBase.FluidColored("oxygen",         -500, -1, true,  true, 0x111111); //TODO: find a good color
        fluidHydrogen =         new FluidBase.FluidColored("hydrogen",       -500, -1, true,  true, 0x111111); //TODO: find a good color
        fluidOil =              new FluidBase.FluidColored("oil",              -1, -1, true, false, 0x111111); //TODO: find a good color
        fluidNaturalGas =       new FluidBase.FluidColored("naturalGas",     -500, -1, true,  true, 0x111111); //TODO: find a good color
        fluidGasoline =         new FluidBase.FluidColored("fuel",             -1, -1, true, false, 0x111111); //TODO: find a good color
        fluidPetroleum =        new FluidBase.FluidColored("petroleum",        -1, -1, true, false, 0x111111); //TODO: find a good color
        fluidDiesel =           new FluidBase.FluidColored("diesel",           -1, -1, true, false, 0x111111); //TODO: find a good color
        fluidLubricant =        new FluidBase.FluidColored("lubricant",        -1, -1, true, false, 0x111111); //TODO: find a good color

        OreDictionary.registerOre("bottleWater", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER));
    }

    public static void init()
    {
        Crafting.initCraftingTableRecipes();
        Crafting.initFurnaceRecipes();
        Crafting.initMachineRecipes();
    }

    public static void postInit()
    {

    }
}