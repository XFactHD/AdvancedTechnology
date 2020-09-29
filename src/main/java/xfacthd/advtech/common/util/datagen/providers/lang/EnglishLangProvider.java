package xfacthd.advtech.common.util.datagen.providers.lang;

import net.minecraft.data.DataGenerator;
import org.apache.commons.lang3.StringUtils;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.client.gui.machine.ScreenPlanter;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.client.gui.utility.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.tileentity.generator.*;
import xfacthd.advtech.common.tileentity.machine.*;
import xfacthd.advtech.common.tileentity.utility.*;
import xfacthd.advtech.common.util.StatusMessages;

public class EnglishLangProvider extends ATLanguageProvider
{
    public EnglishLangProvider(DataGenerator gen) { super(gen, "en_us"); }

    @Override
    protected void addTranslations()
    {
        add(ItemGroups.MATERIAL_GROUP.getTranslationKey(), "AdvancedTechnology - Materials");
        add(ItemGroups.TOOL_GROUP.getTranslationKey(), "AdvancedTechnology - Tools");
        add(ItemGroups.MACHINE_GROUP.getTranslationKey(), "AdvancedTechnology - Machines");

        for (Materials material : Materials.values())
        {
            String name = StringUtils.capitalize(material.getName());

            if (material.hasOre())
            {
                add(ATContent.blockOre.get(material), name + " Ore");
            }

            if (material.hasBlock())
            {
                add(ATContent.blockStorage.get(material), "Block of " + name);
            }

            if (material.hasPowder())
            {
                add(ATContent.itemPowder.get(material), name + " Powder");
            }

            if (material.hasIngot() && material.isMetal())
            {
                add(ATContent.itemIngot.get(material), name + " Ingot");
            }

            if (material.hasIngot() && !material.isMetal())
            {
                add(ATContent.itemMaterial.get(material), name);
            }

            if (material.hasNugget())
            {
                add(ATContent.itemNugget.get(material), name + " Nugget");
            }

            if (material.hasGear())
            {
                add(ATContent.itemGear.get(material), name + " Gear");
            }

            if (material.hasPlate())
            {
                add(ATContent.itemPlate.get(material), name + " Plate");
            }
        }

        add(ATContent.blockCreativeEnergySource, "Creative Energy Source");
        add(ATContent.blockCreativeItemSource, "Creative Item Source");
        add(ATContent.blockCreativeFluidSource, "Creative Fluid Source");

        add(ATContent.blockMachineCasing, "Machine Casing");
        add(ATContent.blockElectricFurnace, "Electric Furnace");
        add(ATContent.blockCrusher, "Crusher");
        add(ATContent.blockAlloySmelter, "Alloy Smelter");
        add(ATContent.blockMetalPress, "Metal Press");
        add(ATContent.blockLiquifier, "Liquifier");
        add(ATContent.blockPlanter, "Planter");
        add(ATContent.blockHarvester, "Harvester");
        add(ATContent.blockFertilizer, "Fertilizer");

        add(ATContent.blockBurnerGenerator, "Burner Generator");

        add(ATContent.blockEnergyCube, "Energy Cube");
        add(ATContent.blockFluidTank, "Fluid Tank");

        add(ATContent.blockChunkLoader, "Chunk Loader");

        add(ATContent.itemWrench, "Wrench");

        for (MachineLevel level : MachineLevel.values())
        {
            if (level == MachineLevel.BASIC) { continue; }

            String name = StringUtils.capitalize(level.getName());
            add(ATContent.itemUpgrade.get(level), "Upgrade (" + name + ")");
        }

        for (Enhancement type : Enhancement.values())
        {
            String typeName = StringUtils.capitalize(type.getName()) + " Upgrade";
            for (int level = 0; level < type.getLevels(); level++)
            {
                String name = typeName + " (Level " + (level + 1) + ")";
                add(ATContent.itemEnhancement.get(type).get(level), name);
            }
        }

        add(ATContent.itemPlateMold, "Plate Mold");
        add(ATContent.itemGearMold, "Gear Mold");
        add(ATContent.itemRodMold, "Rod Mold");

        add(ATContent.itemComponent.get(Components.TRANSMISSION_COIL), "Transmission Coil");
        add(ATContent.itemComponent.get(Components.RECEPTION_COIL), "Reception Coil");
        add(ATContent.itemComponent.get(Components.ELECTRIC_MOTOR), "Electric Motor");
        add(ATContent.itemComponent.get(Components.ELECTRIC_GENERATOR), "Electric Generator");

        add(StatusMessages.UPGRADED, "Machine upgraded!");
        add(StatusMessages.CANT_UPGRADE, "Upgrade failed!");
        add(StatusMessages.INSTALLED, "Upgrade installed!");
        add(StatusMessages.CANT_INSTALL, "Can't install upgrade!");
        add(StatusMessages.NO_SUPPORT, "Machine does not support upgrades!");

        add(TileEntityElectricFurnace.TITLE, "Electric Furnace");
        add(TileEntityCrusher.TITLE, "Crusher");
        add(TileEntityAlloySmelter.TITLE, "Alloy Smelter");
        add(TileEntityMetalPress.TITLE, "Metal Press");
        add(TileEntityLiquifier.TITLE, "Liquifier");
        add(TileEntityPlanter.TITLE, "Planter");
        add(TileEntityHarvester.TITLE, "Harvester");
        add(TileEntityFertilizer.TITLE, "Fertilizer");
        add(TileEntityBurnerGenerator.TITLE, "Burner Generator");
        add(TileEntityChunkLoader.TITLE, "Chunk Loader");

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

        add("jei_cat.advtech.crusher", "Crushing");
        add("jei_cat.advtech.alloy_smelter", "Alloy Smelting");
        add("jei_cat.advtech.metal_press", "Metal Forming");
        add("jei_cat.advtech.liquifier", "Liquification");
    }
}