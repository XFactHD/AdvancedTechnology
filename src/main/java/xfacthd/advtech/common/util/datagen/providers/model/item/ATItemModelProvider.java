package xfacthd.advtech.common.util.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import xfacthd.advtech.AdvancedTechnology;

public abstract class ATItemModelProvider extends ItemModelProvider
{
    public ATItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, AdvancedTechnology.MODID, existingFileHelper);
    }

    @Override
    public final String getName() { return AdvancedTechnology.MODID + "." + getProviderName(); }

    protected abstract String getProviderName();

    @SuppressWarnings("UnusedReturnValue")
    protected final ItemModelBuilder simpleItem(String name, ResourceLocation texture)
    {
        return withExistingParent(name, "item/generated").texture("layer0", texture);
    }
}