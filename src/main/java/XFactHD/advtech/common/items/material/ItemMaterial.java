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

package XFactHD.advtech.common.items.material;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.items.ItemBase;

public class ItemMaterial extends ItemBase
{
    public ItemMaterial()
    {
        super("itemMaterial", 64, AdvancedTechnology.creativeTab, EnumType.getAsStringArray(), EnumType.getOreDictNames());
    }

    public enum EnumType
    {
        COPPER_INGOT("CopperIngot", "ingotCopper"),
        TIN_INGOT("TinIngot", "ingotTin"),
        SILVER_INGOT("SilverIngot", "ingotSilver"),
        LEAD_INGOT("LeadIngot", "ingotLead"),
        STEEL_INGOT("SteelIngot", "ingotSteel"),
        BRONZE_INGOT("BronzeIngot", "ingotBronze"),
        NICKEL_INGOT("NickelIngot", "ingotNickel"),
        LITHIUM_INGOT("LithiumIngot", "ingotLithium"),
        CADMIUM_INGOT("CadmiumIngot", "ingotCadmium"),
        ZINC_INGOT("ZincIngot", "ingotZinc"),
        PLATINUM_INGOT("PlatinumIngot", "ingotPlatinum"),
        TUNGSTEN_INGOT("TungstenIngot", "ingotTungsten"),
        NEODYMIUM_INGOT("NeodymiumIngot", "ingotNeodymium"),

        COPPER_NUGGET("CopperNugget", "nuggetCopper"),
        TIN_NUGGET("TinNugget", "nuggetTin"),
        SILVER_NUGGET("SilverNugget", "nuggetSilver"),
        LEAD_NUGGET("LeadNugget", "nuggetLead"),
        STEEL_NUGGET("SteelNugget", "nuggetSteel"),
        BRONZE_NUGGET("BronzeNugget", "nuggetBronze"),
        NICKEL_NUGGET("NickelNugget", "nuggetNickel"),
        LITHIUM_NUGGET("LithiumNugget", "nuggetLithium"),
        CADMIUN_NUGGET("CadmiumNugget", "nuggetCadmium"),
        ZINC_NUGGET("ZincNugget", "nuggetZinc"),
        PLATINUM_NUGGET("PlatinumNugget", "nuggetPlatinum"),
        TUNGSTEN_NUGGET("TungstenNugget", "nuggetTungsten"),
        NEODYMIUM_NUGGET("NeodymiumNugget", "nuggetNeodymium"),
        IRON_NUGGET("IronNugget", "nuggetIron"),

        COPPER_PLATE("CopperPlate", "plateCopper"),
        TIN_PLATE("TinPlate", "plateTin"),
        SILVER_PLATE("SilverPlate", "plateSilver"),
        LEAD_PLATE("LeadPlate", "plateLead"),
        STEEL_PLATE("SteelPlate", "plateSteel"),
        BRONZE_PLATE("BronzePlate", "plateBronze"),
        NICKEL_PLATE("NickelPlate", "plateNickel"),
        LITHIUM_PLATE("LithiumPlate", "plateLithium"),
        CADMIUM_PLATE("CadmiumPlate", "plateCadmium"),
        ZINC_PLATE("ZincPlate", "plateZinc"),
        PLATINUM_PLATE("PlatinumPlate", "platePlatinum"),
        TUNGSTEN_PLATE("TungstenPlate", "plateTungsten"),
        NEODYMIUM_PLATE("NeodymiumPlate", "plateNeodymium"),
        IRON_PLATE("IronPlate", "plateIron"),
        GOLD_PLATE("GoldPlate", "plateGold"),

        COPPER_DUST("CopperDust", "dustCopper"),
        TIN_DUST("TinDust", "dustTin"),
        SILVER_DUST("SilverDust", "dustSilver"),
        LEAD_DUST("LeadDust", "dustLead"),
        STEEL_DUST("SteelDust", "dustSteel"),
        BRONZE_DUST("BronzeDust", "dustBronze"),
        NICKEL_DUST("NickelDust", "dustNickel"),
        LITHIUM_DUST("LithiumDust", "dustLithium"),
        CADMIUM_DUST("CadmiumDust", "dustCadmium"),
        ZINC_DUST("ZincDust", "dustZinc"),
        PLATINUM_DUST("PlatinumDust", "dustPlatinum"),
        TUNGSTEN_DUST("TungstenDust", "dustTungsten"),
        NEODYMIUM_DUST("NeodymiumDust", "dustNeodymium"),
        IRON_DUST("IronDust", "dustIron"),
        GOLD_DUST("GoldDust", "dustGold"),

        COPPER_WIRECOIL("CopperWireCoil", "coilCopper"),
        TIN_WIRECOIL("TinWireCoil", "coilTin"),
        SILVER_WIRECOIL("SilverWireCoil", "coilSilver"),
        STEEL_WIRECOIL("SteelWireCoil", "coilSteel"),
        TUNGSTEN_WIRECOIL("TungstenWireCoil", "coilTungsten"),
        GOLD_WIRECOIL("GoldWireCoil", "coilGold"),

        SULFUR("Sulfur", "dustSulfur"),
        RUBBER("Rubber", "materialRubber"),
        COAL_DUST("CoalDust", "dustCoal"),
        MAGNET("Magnet", "materialMagnet"),
        ANODE_SLUDGE("AnodeSludge", ""),

        RAW_SILICON("RawSilicon", "materialSilicon"),
        SILICON_BOULE("SiliconBoule", ""),
        SILICON_WAFER("SiliconWafer", ""),
        SILICON_CARBIDE("SiliconCarbide", "materialSiC"),
        ZNCDS_MIXTURE("ZnCdSIngot", "materialZnCdS"),
        COIL("Coil", "materialCoil"),
        METAL_AXLE("MetalAxle", "materialMetalAxle"),
        ELECTRIC_MOTOR("ElectricMotor", ""),
        HEATING_COIL("HeatingCoil", ""),
        COKE("Coke", "materialCoke"),
        COKE_DUST("CokeDust", "dustCoke"),
        GLASS_LENS("GlassLens", ""),
        ELECTROLITE_BOTTLE("ElectroliteBottle", ""),
        PLASTIC_INGOT("PlasticIngot", "ingotPlastic"),
        REACTION_CHAMBER("ReactionChamber", "");

        private String name;
        private String oreDict;

        EnumType(String name, String oreDict)
        {
            this.name = name;
            this.oreDict = oreDict;
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.name;
            }
            return strings;
        }

        public static String[] getOreDictNames()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.oreDict;
            }
            return strings;
        }

        public String getName()
        {
            return name;
        }

        public int getMeta()
        {
            return ordinal();
        }
    }
}