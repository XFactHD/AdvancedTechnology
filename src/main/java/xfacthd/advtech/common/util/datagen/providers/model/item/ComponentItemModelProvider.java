package xfacthd.advtech.common.util.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import xfacthd.advtech.common.data.subtypes.Components;

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

        for (Components component : Components.values())
        {
            String name = "item_" + component.getName();
            if (component == Components.TRANSMISSION_COIL || component == Components.RECEPTION_COIL)
            {
                getBuilder(name)
                        .parent(builtin)
                        .texture("layer0", modLoc("item/component/item_coil"))
                        .texture("layer1", modLoc("item/component/item_coil_metal"));
            }
            else
            {
                getBuilder(name).parent(builtin).texture("layer0", modLoc("item/component/" + name));
            }
        }
    }

    @Override
    protected String getProviderName() { return "componen_item_models"; }
}