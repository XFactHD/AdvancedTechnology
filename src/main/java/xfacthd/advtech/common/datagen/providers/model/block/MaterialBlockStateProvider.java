package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MaterialType;

public class MaterialBlockStateProvider extends ATBlockStateProvider
{
    public MaterialBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        for (MaterialType material : MaterialType.values())
        {
            if (material.hasOre())
            {
                Block ore = ATContent.ORE_BLOCKS.get(material).get();

                ModelFile oreModel = models()
                        .withExistingParent("block/material/ore_" + material.getSerializedName(), "block/cube_all")
                            .texture("base", modLoc("block/material/block_ore_stone"))
                            .texture("overlay", modLoc("block/material/block_ore_chunk"))
                            .texture("particle", mcLoc("block/stone"))
                            .element()
                                .cube("#base")
                                .allFaces((dir, builder) -> builder.tintindex(1))
                                .end()
                            .element()
                                .cube("#overlay")
                                .allFaces((dir, builder) -> builder.tintindex(0))
                                .end();

                getVariantBuilder(ore).partialState().setModels(ConfiguredModel.builder().modelFile(oreModel).build());
                simpleBlockItem(ore, oreModel);
            }

            if (material.hasBlock())
            {
                Block storage = ATContent.STORAGE_BLOCKS.get(material).get();
                String texture = material.isMetal() ? "block_metal" : "block_storage";

                ModelFile storageModel = models()
                        .withExistingParent("block/material/" + material.getName(MaterialCategory.BLOCK), "block/cube_all")
                            .texture("all", modLoc("block/material/" + texture))
                            .texture("particle", modLoc("block/material/" + texture))
                            .element()
                                .cube("#all")
                                .allFaces((dir, builder) -> builder.tintindex(0))
                                .end();

                getVariantBuilder(storage).partialState().setModels(ConfiguredModel.builder().modelFile(storageModel).build());
                simpleBlockItem(storage, storageModel);
            }
        }
    }

    @Override
    public String getProviderName() { return "material_block_models"; }
}