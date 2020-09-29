package xfacthd.advtech.common.util.data;

import net.minecraft.item.crafting.*;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.data.recipes.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID)
public class RecipeReloadListener implements IResourceManagerReloadListener
{
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        if (EffectiveSide.get().isServer())
        {
            rebuildRecipeLists(ServerLifecycleHooks.getCurrentServer().getRecipeManager());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRecipesReloaded(final RecipesUpdatedEvent event) { rebuildRecipeLists(event.getRecipeManager()); }

    private void rebuildRecipeLists(RecipeManager recipeManager)
    {
        Collection<IRecipe<?>> recipes = recipeManager.getRecipes();

        CrusherRecipe.RECIPES = filterByType(recipes, CrusherRecipe.class, CrusherRecipe.TYPE);
        AlloySmelterRecipe.RECIPES = filterByType(recipes, AlloySmelterRecipe.class, AlloySmelterRecipe.TYPE);
        MetalPressRecipe.RECIPES = filterByType(recipes, MetalPressRecipe.class, MetalPressRecipe.TYPE);
        LiquifierRecipe.RECIPES = filterByType(recipes, LiquifierRecipe.class, LiquifierRecipe.TYPE);
    }

    private <R extends IRecipe<?>> List<R> filterByType(Collection<IRecipe<?>> recipes, Class<R> recipeClass, IRecipeType<R> recipeType)
    {
        return recipes.stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .map(recipeClass::cast)
                .collect(Collectors.toList());
    }
}