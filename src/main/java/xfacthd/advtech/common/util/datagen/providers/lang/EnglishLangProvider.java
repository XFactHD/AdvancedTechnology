package xfacthd.advtech.common.util.datagen.providers.lang;

import net.minecraft.data.DataGenerator;
import org.apache.commons.lang3.StringUtils;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.Components;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.tileentity.generator.*;
import xfacthd.advtech.common.tileentity.machine.*;
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

        add(ATContent.blockMachineCasing, "Machine Casing");
        add(ATContent.blockElectricFurnace, "Electric Furnace");
        add(ATContent.blockCrusher, "Crusher");
        add(ATContent.blockAlloySmelter, "Alloy Smelter");
        add(ATContent.blockMetalPress, "Metal Press");
        add(ATContent.blockBurnerGenerator, "Burner Generator");

        add(ATContent.blockEnergyCube, "Energy Cube");

        add(ATContent.itemWrench, "Wrench");

        for (MachineLevel level : MachineLevel.values())
        {
            if (level == MachineLevel.BASIC) { continue; }

            String name = StringUtils.capitalize(level.getName());
            add(ATContent.itemUpgrade.get(level), "Upgrade (" + name + ")");
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

        add(TileEntityElectricFurnace.TITLE, "Electric Furnace");
        add(TileEntityCrusher.TITLE, "Crusher");
        add(TileEntityAlloySmelter.TITLE, "Alloy Smelter");
        add(TileEntityMetalPress.TITLE, "Metal Press");
        add(TileEntityBurnerGenerator.TITLE, "Burner Generator");

        add(TabMachinePort.TITLE, "Port Settings");
        add(TabMachineUpgrades.TITLE, "Upgrades");
        add(TabRedstoneSettings.TITLE, "Redstone");

        add(TabMachinePort.BTN_TOOLTIP, "Active output");

        add("jei_cat.advtech.crusher", "Crushing");
        add("jei_cat.advtech.alloy_smelter", "Alloy Smelting");
        add("jei_cat.advtech.metal_press", "Metal Forming");
    }
}