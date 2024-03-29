package xfacthd.advtech.common.util.data;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = AdvancedTechnology.MODID)
public class RecipeReloadListener implements PreparableReloadListener
{
    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager manager, ProfilerFiller prepProfiler, ProfilerFiller reloadProfiler, Executor bgExec, Executor gameExec)
    {
        return CompletableFuture.runAsync(() ->
        {
            if (EffectiveSide.get().isServer())
            {
                rebuildRecipeLists(ServerLifecycleHooks.getCurrentServer().getRecipeManager());
            }
        }, gameExec);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRecipesReloaded(final RecipesUpdatedEvent event) { rebuildRecipeLists(event.getRecipeManager()); }

    private void rebuildRecipeLists(RecipeManager recipeManager)
    {
        Collection<Recipe<?>> recipes = recipeManager.getRecipes();

        CrusherRecipe.RECIPES = filterByType(recipes, CrusherRecipe.class, ATContent.RECIPE_TYPE_CRUSHER.get());
        AlloySmelterRecipe.RECIPES = filterByType(recipes, AlloySmelterRecipe.class, ATContent.RECIPE_TYPE_ALLOY_SMELTER.get());
        MetalPressRecipe.RECIPES = filterByType(recipes, MetalPressRecipe.class, ATContent.RECIPE_TYPE_METAL_PRESS.get());
        LiquifierRecipe.RECIPES = filterByType(recipes, LiquifierRecipe.class, ATContent.RECIPE_TYPE_LIQUIFIER.get());
    }

    private <R extends Recipe<?>> List<R> filterByType(Collection<Recipe<?>> recipes, Class<R> recipeClass, RecipeType<R> recipeType)
    {
        return recipes.stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .map(recipeClass::cast)
                .collect(Collectors.toList());
    }
}