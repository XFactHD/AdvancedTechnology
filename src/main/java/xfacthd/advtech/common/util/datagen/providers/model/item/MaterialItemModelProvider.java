package xfacthd.advtech.common.util.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import xfacthd.advtech.common.data.subtypes.Materials;

public class MaterialItemModelProvider extends ATItemModelProvider
{
    public MaterialItemModelProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, fileHelper); }

    @Override
    protected void registerModels()
    {
        ModelFile builtin = getExistingFile(new ResourceLocation("minecraft", "item/generated"));

        for (Materials material : Materials.values())
        {
            if (material.hasPowder())
            {
                getBuilder("item_powder_" + material.getName()).parent(builtin).texture("layer0", modLoc("item/material/item_powder"));
            }

            if (material.hasIngot() && material.isMetal())
            {
                getBuilder("item_ingot_" + material.getName()).parent(builtin).texture("layer0", modLoc("item/material/item_ingot"));
            }
            else if (material.hasIngot())
            {
                getBuilder("item_" + material.getName()).parent(builtin).texture("layer0", modLoc("item/material/item_" + material.getName()));
            }

            if (material.hasNugget())
            {
                getBuilder("item_nugget_" + material.getName()).parent(builtin).texture("layer0", modLoc("item/material/item_nugget"));
            }

            if (material.hasGear())
            {
                getBuilder("item_gear_" + material.getName()).parent(builtin).texture("layer0", modLoc("item/material/item_gear"));
            }

            if (material.hasPlate())
            {
                getBuilder("item_plate_" + material.getName()).parent(builtin).texture("layer0", modLoc("item/material/item_plate"));
            }
        }
    }

    @Override
    public String getProviderName() { return "material_item_models"; }
}