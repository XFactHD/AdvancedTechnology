package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import xfacthd.advtech.common.ATContent;

public class StorageBlockStateProvider extends ATBlockStateProvider
{
    public StorageBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, fileHelper); }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile energyCubeModel = models().getExistingFile(modLoc("block/storage/block_energy_cube"));
        simpleBlockWithItem(ATContent.blockEnergyCube, energyCubeModel);

        ModelFile fluidTankModel = models().getExistingFile(modLoc("block/storage/block_fluid_tank"));
        simpleBlockWithItem(ATContent.blockFluidTank, fluidTankModel);
    }

    private void simpleBlockWithItem(Block block, ModelFile modelFile)
    {
        simpleBlock(block, modelFile);
        simpleBlockItem(block, modelFile);
    }

    @Override
    protected String getProviderName() { return "storage_block_states"; }
}