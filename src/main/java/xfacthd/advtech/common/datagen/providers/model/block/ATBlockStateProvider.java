package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import xfacthd.advtech.AdvancedTechnology;

public abstract class ATBlockStateProvider extends BlockStateProvider
{
    public ATBlockStateProvider(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        super(gen, AdvancedTechnology.MODID, fileHelper);
    }

    @Override
    public final String getName() { return AdvancedTechnology.MODID + "." + getProviderName(); }

    protected abstract String getProviderName();
}