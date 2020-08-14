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

package XFactHD.advtech.client;

import XFactHD.advtech.client.render.*;
import XFactHD.advtech.client.utils.blockColoring.*;
import XFactHD.advtech.client.utils.itemColoring.*;
import XFactHD.advtech.client.utils.statemapping.*;
import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.energy.*;
import XFactHD.advtech.common.blocks.machine.*;
import XFactHD.advtech.common.blocks.material.*;
import XFactHD.advtech.common.blocks.light.*;
import XFactHD.advtech.common.blocks.storage.*;
import XFactHD.advtech.common.blocks.transport.*;
import XFactHD.advtech.common.items.energy.*;
import XFactHD.advtech.common.items.material.*;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.utilClasses.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.*;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Locale;

public class ClientManager
{
    private static BlockColors blockColors = null;
    private static ItemColors itemColors = null;

    public static void registerBasicModels()
    {
        registerGenericItemModel(Content.itemBattery, ItemBattery.EnumType.values().length, "energy/battery");
        registerGenericItemBlockModel(Content.blockMetal, BlockMetal.EnumType.values().length, "blockMetal");
        registerGenericItemBlockModel(Content.blockOre, BlockOre.EnumType.values().length, "blockOre");
        registerGenericItemBlockModel(Content.blockStorage, BlockStorage.EnumType.values().length, "blockStorage");
        registerGenericItemBlockModel(Content.blockCable, 3, "energy/itemCable");
        registerItemBlockModel(Content.blockEnergyVoid, 0, "normal");
        registerItemBlockModel(Content.blockQuarry, 0, "normal");
        registerItemBlockModelInventory(Content.blockQuarry, 1, "machine/itemQuarryFrame");
        registerItemBlockModel(Content.blockChemoLuminator, 0, "normal");
        registerGenericItemBlockModel(Content.blockFluidTank, 4, "blockFluidTank");
        registerItemBlockModel(Content.blockWirelessFeeder, 0, "normal");
        registerItemBlockModel(Content.blockWirelessNode, 0, "energy/item_wireless_feeder");
        registerItemModelInventory(Content.itemTool, 0, "tool/wrench");
        registerItemBlockModel(Content.blockOilRefinery, 0, "facing=north,tower=base");
        registerItemBlockModel(Content.blockGasLantern, 0, "blockGasLanternWall", "active=true,facing=north");
        registerItemBlockModel(Content.blockGasLantern, 1, "blockGasLantern", "active=true,auto=false");
    }

    public static void registerAdvancedModels()
    {
        for (ItemMaterial.EnumType type : ItemMaterial.EnumType.values())
        {
            String name = type.getName().toLowerCase(Locale.ENGLISH);
            String variant = name;
            if (name.contains("ingot") || name.contains("zncds")) { variant = "ingot"; }
            else if (name.contains("nugget")) { variant = "nugget"; }
            else if (name.contains("dust") || name.equals("sulfur")) { variant = "dust"; }
            else if (name.contains("plate")) { variant = "plate"; }
            else if (name.contains("wirecoil")) { variant = "wirecoil"; }
            else if (name.contains("coil")) { variant = "coil"; }
            else if (name.contains("clump") || name.equals("coke")) { variant = "clump"; }
            registerItemModelInventory(Content.itemMaterial, type.ordinal(), "material/" + variant);
        }

        for (ItemMaterialLight.EnumType type : ItemMaterialLight.EnumType.values())
        {
            String name = type.getName().toLowerCase(Locale.ENGLISH);
            String variant = name;
            if (name.contains("lamp")) { variant = "lamp"; }
            else if (name.contains("led") && !(name.contains("cluster") || name.contains("assembly"))) { variant = "led"; }
            else if (name.contains("ledcluster")) { variant = "led_cluster"; }
            registerItemModelInventory(Content.itemMaterialLight, type.ordinal(), "material/" + variant);
        }

        for (BlockSolarPanel.EnumType type : BlockSolarPanel.EnumType.values())
        {
            String facing = type.getName().contains("angled") ? "facing=west," : "";
            registerItemBlockModel(Content.blockSolarPanel, type.ordinal(), facing + "type=" + type.getName());
        }

        for (EnumDyeColor color : EnumDyeColor.values())
        {
            registerItemBlockModel(Content.blockConcrete, color.ordinal(), "color=" + color.getName());
        }

        for (ItemBattery.EnumType type : ItemBattery.EnumType.values())
        {
            String location = "blockBatteryPack_" + type.getName();
            registerItemBlockModel(Content.blockBatteryPack, type.getMeta(), location, SideSetting.getStateForIndices(0, 0, 0, 0, 0, 0));
        }

        for (BlockFluidPipe.EnumType type : BlockFluidPipe.EnumType.values())
        {
            registerItemBlockModel(Content.blockFluidPipe, type.ordinal(), "type=" + type.getName());
        }
    }

    public static void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityQuarry.class, new RenderTileQuarry());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidPipe.class, new RenderTileFluidPipe());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidTank.class, new RenderTileFluidTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOilRefineryMaster.class, new RenderTileRefineryBase());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOilRefineryTower.class, new RenderTileRefineryTower());
    }

    public static void registerStateMappers()
    {
        //BlockBase Statemappers
        ModelLoader.setCustomStateMapper(Content.blockMetal, StateMapperIgnoreAll.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.blockOre, StateMapperIgnoreAll.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.blockStorage, StateMapperIgnoreAll.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.blockConcrete, new StateMapperBlockConcrete());
        ModelLoader.setCustomStateMapper(Content.blockSolarPanel, new StateMapperBlockSolarPanel());
        ModelLoader.setCustomStateMapper(Content.blockCable, new StateMapperBlockCable());
        ModelLoader.setCustomStateMapper(Content.blockBatteryPack, new StateMapperBlockBatteryPack());
        ModelLoader.setCustomStateMapper(Content.blockQuarry, new StateMapperBlockQuarry());
        ModelLoader.setCustomStateMapper(Content.blockChemoLuminator, StateMapperIgnoreAll.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.blockFluidPipe, new StateMapperBlockFluidPipe());
        ModelLoader.setCustomStateMapper(Content.blockFluidTank, StateMapperIgnoreAll.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.blockGasLantern, new StateMapperBlockGasLantern());

        //BlockFluid Statemappers
        ModelLoader.setCustomStateMapper(Content.fluidHydrogenPeroxide.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidOxalAcid.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidSulfuricAcid.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidBiogas.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidOxygen.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidHydrogen.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidOil.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidNaturalGas.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidGasoline.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidPetroleum.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidDiesel.getBlock(), StateMapperBlockFluid.INSTANCE);
        ModelLoader.setCustomStateMapper(Content.fluidLubricant.getBlock(), StateMapperBlockFluid.INSTANCE);
    }

    public static void registerColorHandlers()
    {
        registerItemColorHandler(new ColorHandlerItemMaterial(), Content.itemMaterial);
        registerItemColorHandler(new ColorHandlerItemMaterialLight(), Content.itemMaterialLight);
        registerItemColorHandler(new ColorHandlerItemBattery(), Content.itemBattery);

        registerMultiColorHandler(new ColorHandlerBlockOre(), Content.blockOre);
        registerMultiColorHandler(new ColorHandlerBlockMetal(), Content.blockMetal);
        registerMultiColorHandler(new ColorHandlerBlockStorage(), Content.blockStorage);
        registerMultiColorHandler(new ColorHandlerBlockCable(), Content.blockCable);
        registerMultiColorHandler(new ColorHandlerBlockChemoLuminator(), Content.blockChemoLuminator);
        //registerMultiColorHandler(new ColorHandlerBlockFluidPipe(), Content.blockFluidPipe);
        registerMultiColorHandler(new ColorHandlerBlockFluidTank(), Content.blockFluidTank);

        registerBlockColorHandler(new ColorHandlerBlockFluidColored(), Content.fluidHydrogenPeroxide.getBlock());
        registerBlockColorHandler(new ColorHandlerBlockFluidColored(), Content.fluidOxalAcid.getBlock());
    }

    //Helper methods
    private static void registerItemBlockModel(Block block, int meta, String variant)
    {
        registerItemBlockModel(block, meta, block.getRegistryName().getResourcePath(), variant);
    }

    private static void registerItemBlockModelInventory(Block block, int meta, String location)
    {
        registerItemBlockModel(block, meta, location, "inventory");
    }

    private static void registerGenericItemBlockModel(Block block, int subTypeAmount, String location)
    {
        for (int i = 0; i < subTypeAmount; i++)
        {
            registerItemBlockModel(block, i, location, "normal");
        }
    }

    private static void registerItemBlockModel(Block block, int meta, String location, String variant)
    {
        //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(block), meta, location, variant);
    }

    private static void registerItemModel(Item item, int meta, String variant)
    {
        registerItemModel(item, meta, item.getRegistryName().getResourcePath(), variant);
    }

    private static void registerItemModelInventory(Item item, int meta, String location)
    {
        registerItemModel(item, meta, location, "inventory");
    }

    private static void registerGenericItemModel(Item item, int subTypeAmount, String location)
    {
        for (int i = 0; i < subTypeAmount; i++)
        {
            registerItemModel(item, i, location, "inventory");
        }
    }

    private static void registerItemModel(Item item, int meta, String location, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, location), variant));
    }

    private static void registerBlockColorHandler(IBlockColor handler, Block block)
    {
        if (blockColors == null) { blockColors = Minecraft.getMinecraft().getBlockColors(); }
        blockColors.registerBlockColorHandler(handler, block);
    }

    private static void registerItemColorHandler(IItemColor handler, Item item)
    {
        if (itemColors == null) { itemColors = Minecraft.getMinecraft().getItemColors(); }
        itemColors.registerItemColorHandler(handler, item);
    }

    private static <T extends IBlockColor & IItemColor> void registerMultiColorHandler(T handler, Block block)
    {
        registerBlockColorHandler(handler, block);
        registerItemColorHandler(handler, Item.getItemFromBlock(block));
    }
}