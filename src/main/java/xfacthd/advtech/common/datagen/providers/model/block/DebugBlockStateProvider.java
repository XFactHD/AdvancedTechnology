package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.ATContent;

public class DebugBlockStateProvider extends ATBlockStateProvider
{
    public DebugBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) { super(gen, exFileHelper); }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile energySourceModel = models()
                .withExistingParent("block/debug/creative_energy_source", "block/cube_all")
                .texture("all", modLoc("block/debug/block_energy_source"));
        simpleState(ATContent.BLOCK_CREATIVE_ENERGY_SOURCE.get(), energySourceModel);

        ModelFile itemSourceModel = models()
                .withExistingParent("block/debug/creative_item_source", "block/cube_all")
                .texture("all", modLoc("block/debug/block_item_source"));
        simpleState(ATContent.BLOCK_CREATIVE_ITEM_SOURCE.get(), itemSourceModel);

        ModelFile fluidSourceModel = models()
                .withExistingParent("block/debug/creative_fluid_source", "block/cube_all")
                .texture("all", modLoc("block/debug/block_fluid_source"));
        simpleState(ATContent.BLOCK_CREATIVE_FLUID_SOURCE.get(), fluidSourceModel);
    }

    private void simpleState(Block block, ModelFile model)
    {
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    @Override
    protected String getProviderName() { return "debug_block_models"; }
}