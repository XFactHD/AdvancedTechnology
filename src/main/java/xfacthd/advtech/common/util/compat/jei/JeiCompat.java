package xfacthd.advtech.common.util.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.AdvancedScreen;
import xfacthd.advtech.client.gui.generator.*;
import xfacthd.advtech.client.gui.machine.*;
import xfacthd.advtech.client.gui.storage.*;
import xfacthd.advtech.client.gui.utility.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.*;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.util.compat.jei.categories.*;
import xfacthd.advtech.common.util.compat.jei.gui_handlers.*;

@JeiPlugin
public class JeiCompat implements IModPlugin
{
    private static final ResourceLocation UID = new ResourceLocation(AdvancedTechnology.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() { return UID; }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new CategoryCrusher(guiHelper),
                new CategoryAlloySmelter(guiHelper),
                new CategoryMetalPress(guiHelper),
                new CategoryLiquifier(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(CrusherRecipe.RECIPES, CrusherRecipe.UID);
        registration.addRecipes(AlloySmelterRecipe.RECIPES, AlloySmelterRecipe.UID);
        registration.addRecipes(MetalPressRecipe.RECIPES, MetalPressRecipe.UID);
        registration.addRecipes(LiquifierRecipe.RECIPES, LiquifierRecipe.UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        //TODO: check what this is for and if we need it
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ATContent.machineBlock(MachineType.ELECTRIC_FURNACE)), VanillaRecipeCategoryUid.FURNACE);
        registration.addRecipeCatalyst(new ItemStack(ATContent.machineBlock(MachineType.CRUSHER)), CrusherRecipe.UID);
        registration.addRecipeCatalyst(new ItemStack(ATContent.machineBlock(MachineType.ALLOY_SMELTER)), AlloySmelterRecipe.UID);
        registration.addRecipeCatalyst(new ItemStack(ATContent.machineBlock(MachineType.METAL_PRESS)), MetalPressRecipe.UID);
        registration.addRecipeCatalyst(new ItemStack(ATContent.machineBlock(MachineType.LIQUIFIER)), LiquifierRecipe.UID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        IGuiContainerHandler<AdvancedScreen<?>> simpleGuiHandler = new AdvancedGuiHandler<>();
        registration.addGuiContainerHandler(ScreenElectricFurnace.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenCrusher.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenAlloySmelter.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenMetalPress.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenLiquifier.class, new LiquifierGuiHandler());
        registration.addGuiContainerHandler(ScreenCharger.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenPlanter.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenHarvester.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenFertilizer.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenBurnerGenerator.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenEnergyCube.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenChunkLoader.class, simpleGuiHandler);
        registration.addGuiContainerHandler(ScreenItemSplitter.class, simpleGuiHandler);
        //Other guis may need extended GuiHandlers because they contain fluid ingredients

        registration.addRecipeClickArea(ScreenElectricFurnace.class,    77, 45, 22, 16, VanillaRecipeCategoryUid.FURNACE);
        registration.addRecipeClickArea(ScreenCrusher.class,            77, 45, 22, 16, CrusherRecipe.UID);
        registration.addRecipeClickArea(ScreenAlloySmelter.class,       77, 45, 22, 16, AlloySmelterRecipe.UID);
        registration.addRecipeClickArea(ScreenMetalPress.class,         77, 45, 22, 16, MetalPressRecipe.UID);
        registration.addRecipeClickArea(ScreenLiquifier.class,          77, 45, 22, 16, LiquifierRecipe.UID);
    }
}