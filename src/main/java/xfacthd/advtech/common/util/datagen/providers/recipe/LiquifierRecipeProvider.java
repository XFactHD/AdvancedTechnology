package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.common.util.datagen.builders.recipe.LiquifierRecipeBuilder;

import java.util.function.Consumer;

public class LiquifierRecipeProvider extends ATRecipeProvider
{
    public LiquifierRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.LAVA, 1000))
                .input(Tags.Items.STONE, 1)
                .energy(300000)
                .build(consumer, name("stone_to_lava"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.LAVA, 1000))
                .input(Tags.Items.COBBLESTONE, 1)
                .energy(300000)
                .build(consumer, name("cobblestone_to_lava"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.LAVA, 1000))
                .input(Tags.Items.OBSIDIAN, 1)
                .energy(300000)
                .build(consumer, name("obsidian_to_lava"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.LAVA, 1000))
                .input(Tags.Items.NETHERRACK, 1)
                .energy(60000)
                .build(consumer, name("netherrack_to_lava"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.LAVA, 1000))
                .input(Blocks.MAGMA_BLOCK, 1)
                .energy(40000)
                .build(consumer, name("magma_block_to_lava"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.WATER, 125))
                .input(Items.SNOWBALL, 1)
                .energy(200)
                .build(consumer, name("snowball_to_water"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.WATER, 500))
                .input(Blocks.SNOW_BLOCK, 1)
                .energy(800)
                .build(consumer, name("snow_block_to_water"));

        LiquifierRecipeBuilder.liquifierRecipe(new FluidStack(Fluids.WATER, 1000))
                .input(Blocks.ICE, 1)
                .energy(1600)
                .build(consumer, name("ice_to_water"));
    }

    @Override
    protected String getRecipePrefix() { return "liquifier"; }
}