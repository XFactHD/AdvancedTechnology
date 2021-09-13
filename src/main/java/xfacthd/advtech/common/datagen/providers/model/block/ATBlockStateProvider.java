package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.datagen.builders.model.ItemModelBEWLRBuilder;

public abstract class ATBlockStateProvider extends BlockStateProvider
{
    public ATBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, AdvancedTechnology.MODID, fileHelper);
    }

    @Override
    public final String getName() { return AdvancedTechnology.MODID + "." + getProviderName(); }

    protected abstract String getProviderName();

    protected final void simpleBlockWithItem(Block block, ModelFile modelFile)
    {
        simpleBlock(block, modelFile);
        simpleBlockItem(block, modelFile);
    }

    protected final void itemBlockWithBEWLR(Block block, ModelFile parentModel)
    {
        itemModels().getBuilder(block.getRegistryName().getPath())
                .customLoader(ItemModelBEWLRBuilder::new)
                .model(itemModels().nested().parent(parentModel));
    }
}