package xfacthd.advtech.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.datagen.builders.model.ItemModelBEWLRBuilder;

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

    protected final void itemWithBEWLR(Item item, ModelBuilder<?> model)
    {
        //noinspection ConstantConditions
        getBuilder(item.getRegistryName().getPath())
                .customLoader(ItemModelBEWLRBuilder::new)
                .model(model);
    }
}