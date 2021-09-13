package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.ATContent;

public class StorageBlockStateProvider extends ATBlockStateProvider
{
    public StorageBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, fileHelper); }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile energyCubeModel = models().getExistingFile(modLoc("block/storage/block_energy_cube"));
        simpleBlock(ATContent.BLOCK_ENERGY_CUBE.get(), energyCubeModel);
        itemBlockWithBEWLR(ATContent.BLOCK_ENERGY_CUBE.get(), energyCubeModel);

        ModelFile fluidTankModel = models().getExistingFile(modLoc("block/storage/block_fluid_tank"));
        simpleBlock(ATContent.BLOCK_FLUID_TANK.get(), fluidTankModel);
        itemBlockWithBEWLR(ATContent.BLOCK_FLUID_TANK.get(), fluidTankModel);
    }

    @Override
    protected String getProviderName() { return "storage_block_states"; }
}