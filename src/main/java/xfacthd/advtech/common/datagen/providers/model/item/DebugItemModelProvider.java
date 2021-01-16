package xfacthd.advtech.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

public class DebugItemModelProvider extends ATItemModelProvider
{
    public DebugItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        ModelFile builtin = getExistingFile(new ResourceLocation("minecraft", "item/generated"));

        getBuilder("item_block_remover").parent(builtin).texture("layer0", mcLoc("item/diamond_pickaxe"));
    }

    @Override
    protected String getProviderName() { return "debug_item_models"; }
}