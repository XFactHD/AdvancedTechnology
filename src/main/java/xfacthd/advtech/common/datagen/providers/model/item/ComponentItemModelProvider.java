package xfacthd.advtech.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.data.subtypes.Component;

public class ComponentItemModelProvider extends ATItemModelProvider
{
    public ComponentItemModelProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper);
    }

    @Override
    protected void registerModels()
    {
        ModelFile builtin = getExistingFile(new ResourceLocation("minecraft", "item/generated"));

        for (Component component : Component.values())
        {
            String name = component.getSerializedName();
            if (component == Component.TRANSMISSION_COIL || component == Component.RECEPTION_COIL)
            {
                getBuilder(name)
                        .parent(builtin)
                        .texture("layer0", modLoc("item/component/item_coil"))
                        .texture("layer1", modLoc("item/component/item_coil_metal"));
            }
            else
            {
                getBuilder(name).parent(builtin).texture("layer0", modLoc("item/component/item_" + name));
            }
        }
    }

    @Override
    protected String getProviderName() { return "componen_item_models"; }
}