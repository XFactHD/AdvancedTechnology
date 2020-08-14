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

package XFactHD.advtech.common.crafting;

import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.items.energy.ItemBattery;
import XFactHD.advtech.common.items.material.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

public final class Crafting
{
    private static ItemStack coil            = new ItemStack(Content.itemMaterial, 2, ItemMaterial.EnumType.COIL.getMeta());
    private static ItemStack metal_axle      = new ItemStack(Content.itemMaterial, 2, ItemMaterial.EnumType.METAL_AXLE.getMeta());
    private static ItemStack electric_motor  = new ItemStack(Content.itemMaterial, 1, ItemMaterial.EnumType.ELECTRIC_MOTOR.getMeta());
    private static ItemStack heating_coil    = new ItemStack(Content.itemMaterial, 2, ItemMaterial.EnumType.HEATING_COIL.getMeta());
    private static ItemStack electrolite     = new ItemStack(Content.itemMaterial, 1, ItemMaterial.EnumType.ELECTROLITE_BOTTLE.getMeta());
    private static ItemStack reactionchamber = new ItemStack(Content.itemMaterial, 1, ItemMaterial.EnumType.REACTION_CHAMBER.getMeta());

    private static ItemStack filament        = new ItemStack(Content.itemMaterialLight, 4, ItemMaterialLight.EnumType.FILAMENT.getMeta());
    private static ItemStack lamp_white      = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_WHITE.getMeta());
    private static ItemStack lamp_orange     = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_ORANGE.getMeta());
    private static ItemStack lamp_magenta    = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_MAGENTA.getMeta());
    private static ItemStack lamp_lightblue  = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_LIGHTBLUE.getMeta());
    private static ItemStack lamp_yellow     = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_YELLOW.getMeta());
    private static ItemStack lamp_lime       = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_LIME.getMeta());
    private static ItemStack lamp_pink       = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_PINK.getMeta());
    private static ItemStack lamp_cyan       = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_CYAN.getMeta());
    private static ItemStack lamp_purple     = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_PURPLE.getMeta());
    private static ItemStack lamp_blue       = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_BLUE.getMeta());
    private static ItemStack lamp_green      = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_GREEN.getMeta());
    private static ItemStack lamp_red        = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LAMP_RED.getMeta());
    private static ItemStack ledAssembly     = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_ASSEMBLY.getMeta());
    private static ItemStack led_white       = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_WHITE.getMeta());
    private static ItemStack led_orange      = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_ORANGE.getMeta());
    private static ItemStack led_magenta     = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_MAGENTA.getMeta());
    private static ItemStack led_lightblue   = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_LIGHTBLUE.getMeta());
    private static ItemStack led_yellow      = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_YELLOW.getMeta());
    private static ItemStack led_lime        = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_LIME.getMeta());
    private static ItemStack led_pink        = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_PINK.getMeta());
    private static ItemStack led_cyan        = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_CYAN.getMeta());
    private static ItemStack led_purple      = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_PURPLE.getMeta());
    private static ItemStack led_blue        = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_BLUE.getMeta());
    private static ItemStack led_green       = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_GREEN.getMeta());
    private static ItemStack led_red         = new ItemStack(Content.itemMaterialLight, 1, ItemMaterialLight.EnumType.LED_RED.getMeta());

    private static ItemStack battery_nicd    = new ItemStack(Content.itemBattery, 1, ItemBattery.EnumType.NICD.getMeta());
    private static ItemStack battery_nimh    = new ItemStack(Content.itemBattery, 1, ItemBattery.EnumType.NIMH.getMeta());
    private static ItemStack battery_pb      = new ItemStack(Content.itemBattery, 1, ItemBattery.EnumType.PB.getMeta());
    private static ItemStack battery_liion   = new ItemStack(Content.itemBattery, 1, ItemBattery.EnumType.LIION.getMeta());

    private static ItemStack chemoluminator  = new ItemStack(Content.blockChemoLuminator, 1);

    private static ItemStack bucket_sulfuric_acid = new ItemStack(Items.BUCKET);

    static
    {
        bucket_sulfuric_acid.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(FluidRegistry.getFluidStack("sulfuricAcid", 1000), true);
    }

    public static void initCraftingTableRecipes()
    {
        addOreDictPackagingRecipe("Copper");
        addOreDictPackagingRecipe("Tin");
        addOreDictPackagingRecipe("Silver");
        addOreDictPackagingRecipe("Lead");
        addOreDictPackagingRecipe("Steel");
        addOreDictPackagingRecipe("Nickel");
        addOreDictPackagingRecipe("Bronze");
        addOreDictPackagingRecipe("Lithium");
        addOreDictPackagingRecipe("Cadmium");
        addOreDictPackagingRecipe("Zinc");
        addOreDictPackagingRecipe("Platinum");
        addOreDictPackagingRecipe("Tungsten");
        addOreDictPackagingRecipe("Neodymium");
        addOreDictBlockItemRecipeNineSlots("blockCoke", "materialCoke");
        addOreDictBlockItemRecipeNineSlots("blockSulfur", "dustSulfur");
        
        addShapedOreDictRecipe(coil,           "CCC", "III", "CCC", 'C', "coilCopper", 'I', "ingotIron");
        addShapedOreDictRecipe(metal_axle,     "   ", "III", "   ", 'I', "ingotIron");
        addShapedOreDictRecipe(electric_motor, "WNI", "ACA", "WNI", 'W', "nuggetCopper", 'I', "ingotIron", 'A', "materialMetalAxle", 'C', "materialCoil", 'N', "materialMagnet");
        addShapedOreDictRecipe(heating_coil,   "CCC", "III", "CCC", 'C', "coilTungsten", 'I', "ingotIron");
        addShapedOreDictRecipe(electrolite,    " R ", "RBR", " R ", 'R', "dustRedstone", 'B', "bottleWater");

        addShapedOreDictRecipe(filament,       "   ", "NNN", "   ", 'N', "coilTungsten");
        addShapedOreDictRecipe(lamp_white,     "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassWhite");
        addShapedOreDictRecipe(lamp_orange,    "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassOrange");
        addShapedOreDictRecipe(lamp_magenta,   "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassMagenta");
        addShapedOreDictRecipe(lamp_lightblue, "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassLightBlue");
        addShapedOreDictRecipe(lamp_yellow,    "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassYellow");
        addShapedOreDictRecipe(lamp_lime,      "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassLime");
        addShapedOreDictRecipe(lamp_pink,      "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassPink");
        addShapedOreDictRecipe(lamp_cyan,      "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassCyan");
        addShapedOreDictRecipe(lamp_purple,    "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassPurple");
        addShapedOreDictRecipe(lamp_blue,      "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassBlue");
        addShapedOreDictRecipe(lamp_green,     "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassGreen");
        addShapedOreDictRecipe(lamp_red,       "GGG", "GFG", "C C", 'C', "nuggetCopper", 'F', filament, 'G', "blockGlassRed");

        addShapedOreDictRecipe(ledAssembly,    "   ", "CZC", "   ", 'C', "nuggetCopper", 'Z', "materialSiC");
        addShapedOreDictRecipe(led_white,      "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassWhite");
        addShapedOreDictRecipe(led_orange,     "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassOrange");
        addShapedOreDictRecipe(led_magenta,    "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassMagenta");
        addShapedOreDictRecipe(led_lightblue,  "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassLightBlue");
        addShapedOreDictRecipe(led_yellow,     "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassYellow");
        addShapedOreDictRecipe(led_lime,       "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassLime");
        addShapedOreDictRecipe(led_pink,       "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassPink");
        addShapedOreDictRecipe(led_cyan,       "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassCyan");
        addShapedOreDictRecipe(led_purple,     "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassPurple");
        addShapedOreDictRecipe(led_blue,       "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassBlue");
        addShapedOreDictRecipe(led_green,      "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassGreen");
        addShapedOreDictRecipe(led_red,        "GGG", "GAG", "C C", 'C', "nuggetCopper", 'A', ledAssembly, 'G', "blockGlassRed");

        addShapedOreDictRecipe(battery_nicd,   "PNP", "PWP", "PCP", 'P', "ingotPlastic", 'N', "ingotNickel", 'W', Items.WATER_BUCKET, 'C', "ingotCadmium");
        addShapedOreDictRecipe(battery_nimh,   "PNP", "PWP", "PIP", 'P', "ingotPlastic", 'N', "ingotNickel", 'W', Items.WATER_BUCKET, 'I', "dustIron");
        addShapedOreDictRecipe(battery_pb,     "PLP", "PSP", "PLP", 'P', "ingotPlsatic", 'L', "ingotLead", 'S', bucket_sulfuric_acid);
        addShapedOreDictRecipe(battery_liion,  "PLP", "PEP", "PCP", 'P', "ingotPlastic", 'E', electrolite, 'C', "dustCoal");

        addShapedOreDictRecipe(chemoluminator, "IGI", "GRG", "IGI", 'I', "ingotIron", 'G', "blockGlass", 'R', reactionchamber);
    }

    public static void initFurnaceRecipes()
    {
        addOreDictFurnaceRecipe("Copper", 0);
        addOreDictFurnaceRecipe("Tin", 0);
        addOreDictFurnaceRecipe("Silver", 0);
        addOreDictFurnaceRecipe("Lead", 0);
        addOreDictFurnaceRecipe("Steel", 0);
        addOreDictFurnaceRecipe("Bronze", 0);
        addOreDictFurnaceRecipe("Lithium", 0);
        addOreDictFurnaceRecipe("Cadmium", 0);
        addOreDictFurnaceRecipe("Zinc", 0);
        addOreDictFurnaceRecipe("Platinum", 0);
        addOreDictFurnaceRecipe("Tungsten", 0);
    }

    public static void initMachineRecipes()
    {

    }

    //Internal Helpers
    private static void addShapedOreDictRecipe(ItemStack result, Object... ingredients)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(result, ingredients));
    }

    private static void addShapelessOreDictRecipe(ItemStack result, Object... ingredients)
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(result, ingredients));
    }

    private static void addOreDictFurnaceRecipe(String material, float xp)
    {
        addOreDictFurnaceRecipe("dust" + material, "ingot" + material, xp);
        if (!OreDictionary.getOres("ore" + material).isEmpty())
        {
            addOreDictFurnaceRecipe("ore" + material, "ingot" + material, xp);
        }
    }

    private static void addOreDictFurnaceRecipe(String input, String output, float xp)
    {
        for (ItemStack inputStack : OreDictionary.getOres(input))
        {
            for (ItemStack outputStack : OreDictionary.getOres(output))
            {
                GameRegistry.addSmelting(inputStack, outputStack, xp);
            }
        }
    }

    private static void addOreDictPackagingRecipe(String material)
    {
        String nugget = "nugget" + material;
        String ingot = "ingot" + material;
        String block = "block" + material;
        for (ItemStack stack : OreDictionary.getOres(nugget))
        {
            stack = new ItemStack(stack.getItem(), 9, stack.getMetadata());
            addShapelessOreDictRecipe(stack, ingot);
        }
        for (ItemStack stack : OreDictionary.getOres(ingot))
        {
            addShapedOreDictRecipe(stack, "NNN", "NNN", "NNN", 'N', nugget);
            ItemStack forBlock = new ItemStack(stack.getItem(), 9, stack.getMetadata());
            addShapelessOreDictRecipe(forBlock, block);
        }
        for (ItemStack stack : OreDictionary.getOres(block))
        {
            addShapedOreDictRecipe(stack, "III", "III", "III", 'I', ingot);
        }
    }

    private static void addOreDictBlockItemRecipeNineSlots(String blockKey, String itemKey)
    {
        List<ItemStack> blocks = OreDictionary.getOres(blockKey);
        List<ItemStack> items = OreDictionary.getOres(itemKey);
        for (ItemStack block : blocks)
        {
            for (ItemStack item : items)
            {
                GameRegistry.addShapedRecipe(block, "III", "III", "III", 'I', item);
                ItemStack newStack = new ItemStack(item.getItem(), 9, item.getMetadata());
                GameRegistry.addShapelessRecipe(newStack, block);
            }
        }
    }

    private static void addOreDictBlockItemRecipeFourSlots(String blockKey, String itemKey)
    {
        List<ItemStack> blocks = OreDictionary.getOres(blockKey);
        List<ItemStack> items = OreDictionary.getOres(itemKey);
        for (ItemStack block : blocks)
        {
            for (ItemStack item : items)
            {
                GameRegistry.addShapelessRecipe(block, item, item, item, item);
                ItemStack newStack = new ItemStack(item.getItem(), 4, item.getMetadata());
                GameRegistry.addShapelessRecipe(newStack, block);
            }
        }
    }

    //Machine Recipe Helpers
    private static void addCrusherRecipe(ItemStack input, ItemStack output, ItemStack secondary, float chance, int energy)
    {

    }

    private static void addAlloySmelterRecipe(ItemStack inputOne, ItemStack inputTwo, ItemStack inputThree, ItemStack output, ItemStack secondary, float chance, int energy)
    {

    }

    private static void addMetalPressRecipe(ItemStack input, ItemStack output, int energy)
    {

    }

    private static void addChemicalReactionChamberRecipe(FluidStack fluidInputOne, FluidStack fluidInputTwo, ItemStack itemInput, FluidStack fluidOutput, ItemStack itemOutput, float chance, int energy)
    {

    }
}