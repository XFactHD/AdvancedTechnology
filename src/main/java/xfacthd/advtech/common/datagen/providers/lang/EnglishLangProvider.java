package xfacthd.advtech.common.datagen.providers.lang;

import net.minecraft.data.DataGenerator;
import org.apache.commons.lang3.StringUtils;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.gui.machine.ScreenPlanter;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.client.gui.utility.ScreenChunkLoader;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.generator.*;
import xfacthd.advtech.common.blockentity.machine.*;
import xfacthd.advtech.common.blockentity.utility.*;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.item.storage.*;
import xfacthd.advtech.common.util.StatusMessages;

public class EnglishLangProvider extends ATLanguageProvider
{
    public EnglishLangProvider(DataGenerator gen) { super(gen, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(ItemGroups.MATERIAL_GROUP.getDisplayName(), "AdvancedTechnology - Materials");
        add(ItemGroups.TOOL_GROUP.getDisplayName(), "AdvancedTechnology - Tools");
        add(ItemGroups.MACHINE_GROUP.getDisplayName(), "AdvancedTechnology - Machines");

        for (MaterialType material : MaterialType.values())
        {
            String name = StringUtils.capitalize(material.getSerializedName());

            if (material.hasOre())
            {
                addBlock(ATContent.ORE_BLOCKS.get(material), name + " Ore");
            }

            if (material.hasBlock())
            {
                addBlock(ATContent.STORAGE_BLOCKS.get(material), "Block of " + name);
            }

            if (material.hasPowder())
            {
                addItem(ATContent.POWDER_ITEMS.get(material), name + " Powder");
            }

            if (material.hasIngot())
            {
                addItem(ATContent.INGOT_ITEMS.get(material), name + " Ingot");
            }

            if (material.hasChunk())
            {
                addItem(ATContent.MATERIAL_ITEMS.get(material), name);
            }

            if (material.hasNugget())
            {
                addItem(ATContent.NUGGET_ITEMS.get(material), name + " Nugget");
            }

            if (material.hasGear())
            {
                addItem(ATContent.GEAR_ITEMS.get(material), name + " Gear");
            }

            if (material.hasPlate())
            {
                addItem(ATContent.PLATE_ITEMS.get(material), name + " Plate");
            }
        }

        addBlock(ATContent.BLOCK_CREATIVE_ENERGY_SOURCE, "Creative Energy Source");
        addBlock(ATContent.BLOCK_CREATIVE_ITEM_SOURCE, "Creative Item Source");
        addBlock(ATContent.BLOCK_CREATIVE_FLUID_SOURCE, "Creative Fluid Source");

        add(ATContent.machineBlock(MachineType.CASING), "Machine Casing");
        add(ATContent.machineBlock(MachineType.ELECTRIC_FURNACE), "Electric Furnace");
        add(ATContent.machineBlock(MachineType.CRUSHER), "Crusher");
        add(ATContent.machineBlock(MachineType.ALLOY_SMELTER), "Alloy Smelter");
        add(ATContent.machineBlock(MachineType.METAL_PRESS), "Metal Press");
        add(ATContent.machineBlock(MachineType.LIQUIFIER), "Liquifier");
        add(ATContent.machineBlock(MachineType.CHARGER), "Charger");
        add(ATContent.machineBlock(MachineType.PLANTER), "Planter");
        add(ATContent.machineBlock(MachineType.HARVESTER), "Harvester");
        add(ATContent.machineBlock(MachineType.FERTILIZER), "Fertilizer");

        add(ATContent.machineBlock(MachineType.BURNER_GENERATOR), "Burner Generator");
        add(ATContent.machineBlock(MachineType.SOLAR_PANEL), "Solar Panel");

        add(ATContent.machineBlock(MachineType.CHUNK_LOADER), "Chunk Loader");
        add(ATContent.machineBlock(MachineType.ITEM_SPLITTER), "Item Splitter");

        addBlock(ATContent.BLOCK_ENERGY_CUBE, "Energy Cube");
        addBlock(ATContent.BLOCK_FLUID_TANK, "Fluid Tank");

        addItem(ATContent.ITEM_WRENCH, "Wrench");

        for (MachineLevel level : MachineLevel.values())
        {
            if (level == MachineLevel.BASIC) { continue; }

            String name = StringUtils.capitalize(level.getSerializedName());
            addItem(ATContent.UPGRADE_ITEMS.get(level), "Upgrade (" + name + ")");
        }

        for (Enhancement type : Enhancement.values())
        {
            String typeName = StringUtils.capitalize(type.getSerializedName()) + " Upgrade";
            for (int level = 0; level < type.getLevels(); level++)
            {
                String name = typeName + " (Level " + (level + 1) + ")";
                addItem(ATContent.ENHANCEMENT_ITEMS.get(type).get(level), name);
            }
        }

        addItem(ATContent.ITEM_PLATE_MOLD, "Plate Mold");
        addItem(ATContent.ITEM_GEAR_MOLD, "Gear Mold");
        addItem(ATContent.ITEM_ROD_MOLD, "Rod Mold");

        addItem(ATContent.COMPONENT_ITEMS.get(Component.TRANSMISSION_COIL), "Transmission Coil");
        addItem(ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL), "Reception Coil");
        addItem(ATContent.COMPONENT_ITEMS.get(Component.ELECTRIC_MOTOR), "Electric Motor");
        addItem(ATContent.COMPONENT_ITEMS.get(Component.ELECTRIC_GENERATOR), "Electric Generator");

        add(StatusMessages.UPGRADED, "Machine upgraded!");
        add(StatusMessages.CANT_UPGRADE, "Upgrade failed!");
        add(StatusMessages.INSTALLED, "Upgrade installed!");
        add(StatusMessages.CANT_INSTALL, "Can't install upgrade!");
        add(StatusMessages.NO_SUPPORT, "Machine does not support upgrades!");

        add(BlockEntityElectricFurnace.TITLE, "Electric Furnace");
        add(BlockEntityCrusher.TITLE, "Crusher");
        add(BlockEntityAlloySmelter.TITLE, "Alloy Smelter");
        add(BlockEntityMetalPress.TITLE, "Metal Press");
        add(BlockEntityLiquifier.TITLE, "Liquifier");
        add(BlockEntityCharger.TITLE, "Charger");
        add(BlockEntityPlanter.TITLE, "Planter");
        add(BlockEntityHarvester.TITLE, "Harvester");
        add(BlockEntityFertilizer.TITLE, "Fertilizer");
        add(BlockEntityBurnerGenerator.TITLE, "Burner Generator");
        add(BlockEntitySolarPanel.TITLE, "Solar Panel");
        add(BlockEntityChunkLoader.TITLE, "Chunk Loader");

        add(TabMachinePort.TITLE, "Port Settings");
        add(TabMachineUpgrades.TITLE, "Upgrades");
        add(TabRedstoneSettings.TITLE, "Redstone");
        add(TabMachinePort.BTN_TOOLTIP, "Active output");

        add(ScreenMachine.SHOW_AREA, "Show area");
        add(ScreenMachine.TANK_EMPTY, "Empty");
        add(ScreenPlanter.SET_FILTER, "Set filter");
        add(ScreenPlanter.CLEAR_FILTER, "Clear filter");
        add(ScreenChunkLoader.RADIUS, "Radius:");
        add(ScreenChunkLoader.CHUNKS, "Chunks loaded:");
        add(ScreenChunkLoader.SHOW, "Show chunks");
        add(ScreenChunkLoader.HIDE, "Hide chunks");

        add(BlockItemEnergyCube.STORED, "Stored:");

        add("jei_cat.advtech.crusher", "Crushing");
        add("jei_cat.advtech.alloy_smelter", "Alloy Smelting");
        add("jei_cat.advtech.metal_press", "Metal Forming");
        add("jei_cat.advtech.liquifier", "Liquification");
    }
}