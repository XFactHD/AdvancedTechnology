package xfacthd.advtech.common.util.datagen.providers.model.block;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.*;
import xfacthd.advtech.common.ATContent;

public class DebugBlockStateProvider extends ATBlockStateProvider
{
    public DebugBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) { super(gen, exFileHelper); }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile energySourceModel = models()
                .withExistingParent("block/debug/block_energy_source", "block/cube_all")
                .texture("all", modLoc("block/debug/block_energy_source"));
        simpleState(ATContent.blockCreativeEnergySource, energySourceModel);
    }

    private void simpleState(Block block, ModelFile model)
    {
        ConfiguredModel[] confModel = ConfiguredModel.builder().modelFile(model).build();
        getVariantBuilder(block).partialState().setModels(confModel);

        simpleBlockItem(block, model);
    }

    @Override
    protected String getProviderName() { return "debug_block_models"; }
}