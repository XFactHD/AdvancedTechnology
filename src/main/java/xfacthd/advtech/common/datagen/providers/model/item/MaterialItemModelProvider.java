package xfacthd.advtech.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.data.subtypes.MaterialType;

public class MaterialItemModelProvider extends ATItemModelProvider
{
    public MaterialItemModelProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, fileHelper); }

    @Override
    protected void registerModels()
    {
        ModelFile builtin = getExistingFile(new ResourceLocation("minecraft", "item/generated"));

        for (MaterialType material : MaterialType.values())
        {
            if (material.hasPowder())
            {
                getBuilder("powder_" + material.getSerializedName()).parent(builtin).texture("layer0", modLoc("item/material/item_powder"));
            }

            if (material.hasIngot())
            {
                getBuilder("ingot_" + material.getSerializedName()).parent(builtin).texture("layer0", modLoc("item/material/item_ingot"));
            }

            if (material.hasChunk())
            {
                getBuilder("material_" + material.getSerializedName()).parent(builtin).texture("layer0", modLoc("item/material/item_" + material.getSerializedName()));
            }

            if (material.hasNugget())
            {
                getBuilder("nugget_" + material.getSerializedName()).parent(builtin).texture("layer0", modLoc("item/material/item_nugget"));
            }

            if (material.hasGear())
            {
                getBuilder("gear_" + material.getSerializedName()).parent(builtin).texture("layer0", modLoc("item/material/item_gear"));
            }

            if (material.hasPlate())
            {
                getBuilder("plate_" + material.getSerializedName()).parent(builtin).texture("layer0", modLoc("item/material/item_plate"));
            }
        }
    }

    @Override
    public String getProviderName() { return "material_item_models"; }
}