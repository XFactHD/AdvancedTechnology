package xfacthd.advtech.common.util.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.material.BlockOre;
import xfacthd.advtech.common.block.material.BlockStorage;
import xfacthd.advtech.common.data.subtypes.Materials;

public class MaterialBlockStateProvider extends ATBlockStateProvider
{
    public MaterialBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        for (Materials material : Materials.values())
        {
            if (material.hasOre())
            {
                BlockOre ore = ATContent.blockOre.get(material);

                ModelFile oreModel = models()
                        .withExistingParent("block/material/block_ore_" + material.getName(), "block/cube_all")
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
                BlockStorage storage = ATContent.blockStorage.get(material);
                String texture = material.isMetal() ? "block_metal" : "block_storage";

                ModelFile storageModel = models()
                        .withExistingParent("block/material/block_" + material.getName(), "block/cube_all")
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