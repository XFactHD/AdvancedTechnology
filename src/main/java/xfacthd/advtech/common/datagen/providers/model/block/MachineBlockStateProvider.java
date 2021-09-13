package xfacthd.advtech.common.datagen.providers.model.block;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.util.data.PropertyHolder;

public class MachineBlockStateProvider extends ATBlockStateProvider
{
    public MachineBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        ModelFile casingModel = casingModel();
        casingState(ATContent.machineBlock(MachineType.CASING), casingModel);

        ModelFile furnaceModel = machineModel("block_electric_furnace");
        ModelFile furnaceModelOn = machineModel("block_electric_furnace_on");
        machineState(ATContent.machineBlock(MachineType.ELECTRIC_FURNACE), furnaceModel, furnaceModelOn);

        ModelFile crusherModel = machineModel("block_crusher");
        ModelFile crusherModelOn = machineModel("block_crusher_on");
        machineState(ATContent.machineBlock(MachineType.CRUSHER), crusherModel, crusherModelOn);

        ModelFile alloySmelterModel = machineModel("block_alloy_smelter");
        ModelFile alloySmelterModelOn = machineModel("block_alloy_smelter_on");
        machineState(ATContent.machineBlock(MachineType.ALLOY_SMELTER), alloySmelterModel, alloySmelterModelOn);

        ModelFile metalPressModel = machineModel("block_metal_press");
        ModelFile metalPressModelOn = machineModel("block_metal_press_on");
        machineState(ATContent.machineBlock(MachineType.METAL_PRESS), metalPressModel, metalPressModelOn);

        ModelFile liquifierModel = machineModel("block_liquifier");
        ModelFile liquifierModelOn = machineModel("block_liquifier_on");
        machineState(ATContent.machineBlock(MachineType.LIQUIFIER), liquifierModel, liquifierModelOn);

        ModelFile chargerModel = machineModel("block_charger");
        ModelFile chargerModelOn = machineModel("block_charger_on");
        machineState(ATContent.machineBlock(MachineType.CHARGER), chargerModel, chargerModelOn);

        ModelFile planterModel = topModel("block_planter");
        ModelFile planterModelOn = topModel("block_planter_on");
        unrotatableMachineState(ATContent.machineBlock(MachineType.PLANTER), planterModel, planterModelOn);

        ModelFile harvesterModel = machineModel("block_harvester");
        ModelFile harvesterModelOn = machineModel("block_harvester_on");
        machineState(ATContent.machineBlock(MachineType.HARVESTER), harvesterModel, harvesterModelOn);

        ModelFile fertilizerModel = machineModel("block_fertilizer");
        ModelFile fertilizerModelOn = machineModel("block_fertilizer_on");
        machineState(ATContent.machineBlock(MachineType.FERTILIZER), fertilizerModel, fertilizerModelOn);



        ModelFile burnerGeneratorModel = machineModel("block_burner_generator");
        ModelFile burnerGeneratorModelOn = machineModel("block_burner_generator_on");
        machineState(ATContent.machineBlock(MachineType.BURNER_GENERATOR), burnerGeneratorModel, burnerGeneratorModelOn);

        ModelFile solarPanelModel = models().getExistingFile(modLoc("block/machine/block_solar_panel"));
        simpleState(ATContent.machineBlock(MachineType.SOLAR_PANEL), solarPanelModel);



        ModelFile chunkLoaderModel = pillarModel("block_chunk_loader");
        ModelFile chunkLoaderModelOn = pillarModel("block_chunk_loader_on");
        unrotatableMachineState(ATContent.machineBlock(MachineType.CHUNK_LOADER), chunkLoaderModel, chunkLoaderModelOn);

        ModelFile itemSplitterModel = topModel("block_item_splitter");
        machineStateNoActive(ATContent.machineBlock(MachineType.ITEM_SPLITTER), itemSplitterModel);
    }

    private ModelFile casingModel()
    {
        return models()
                .withExistingParent("block/machine/block_machine_frame", "block/cube")
                .texture("bottom", modLoc("block/machine/block_machine_bottom"))
                .texture("side", modLoc("block/machine/block_machine_side"))
                .texture("top", modLoc("block/machine/block_machine_top"))
                .texture("particle", modLoc("block/machine/block_machine_top"))
                .element()
                .cube("#bottom")
                .faces((dir, builder) ->
                {
                    if (dir == Direction.DOWN) { builder.texture("#bottom").end(); }
                    else if (dir == Direction.UP) { builder.texture("#top").end(); }
                    else { builder.texture("#side").end(); }
                })
                .end();
    }

    private ModelFile machineModel(String name)
    {
        return models()
                .withExistingParent("block/machine/" + name, "block/cube")
                .texture("bottom", modLoc("block/machine/block_machine_bottom"))
                .texture("side", modLoc("block/machine/block_machine_side"))
                .texture("front", modLoc("block/machine/" + name))
                .texture("top", modLoc("block/machine/block_machine_top"))
                .texture("level", modLoc("block/machine/block_machine_level"))
                .texture("port", modLoc("block/machine/block_machine_port"))
                .texture("particle", modLoc("block/machine/block_machine_top"))
                .element()
                    .cube("#bottom")
                    .faces((dir, builder) ->
                    {
                        if (dir == Direction.DOWN) { builder.texture("#bottom"); }
                        else if (dir == Direction.UP) { builder.texture("#top"); }
                        else if (dir == Direction.NORTH) { builder.texture("#front"); }
                        else { builder.texture("#side"); }

                        builder.cullface(dir).end();
                    })
                    .end()
                .element()
                    .cube("#level")
                    .faces((dir, builder) -> builder.tintindex(6))
                    .end()
                .element()
                    .face(Direction.UP).texture("#port").tintindex(Direction.UP.get3DDataValue()).end()
                    .face(Direction.DOWN).texture("#port").tintindex(Direction.DOWN.get3DDataValue()).end()
                    .face(Direction.EAST).texture("#port").tintindex(Direction.EAST.get3DDataValue()).end()
                    .face(Direction.SOUTH).texture("#port").tintindex(Direction.SOUTH.get3DDataValue()).end()
                    .face(Direction.WEST).texture("#port").tintindex(Direction.WEST.get3DDataValue()).end()
                    //.allFacesExcept(Direction.NORTH, (dir, builder) -> //TODO: PR to Forge, see impl at the bottom
                    //{
                    //    builder.texture("#port").tintindex(dir.getIndex());
                    //})
                    .end();
    }

    private ModelFile pillarModel(String name)
    {
        return models()
                .withExistingParent("block/machine/" + name, "block/cube")
                .texture("bottom", modLoc("block/machine/block_machine_bottom"))
                .texture("side", modLoc("block/machine/" + name))
                .texture("top", modLoc("block/machine/block_machine_top"))
                .texture("level", modLoc("block/machine/block_machine_level"))
                .texture("particle", modLoc("block/machine/block_machine_top"))
                .element()
                    .cube("#bottom")
                    .faces((dir, builder) ->
                    {
                        if (dir == Direction.DOWN) { builder.texture("#bottom").end(); }
                        else if (dir == Direction.UP) { builder.texture("#top").end(); }
                        else { builder.texture("#side").end(); }
                    })
                    .end()
                .element()
                    .cube("#level")
                    .faces((dir, builder) -> builder.tintindex(6))
                    .end();
    }

    private ModelFile topModel(String name)
    {
        return models().withExistingParent("block/machine/" + name, "block/cube")
                .texture("bottom", modLoc("block/machine/block_machine_bottom"))
                .texture("side", modLoc("block/machine/block_machine_side"))
                .texture("top", modLoc("block/machine/" + name))
                .texture("level", modLoc("block/machine/block_machine_level"))
                .texture("port", modLoc("block/machine/block_machine_port"))
                .texture("particle", modLoc("block/machine/block_machine_top"))
                .element()
                    .cube("#bottom")
                    .faces((dir, builder) ->
                    {
                        if (dir == Direction.DOWN) { builder.texture("#bottom"); }
                        else if (dir == Direction.UP) { builder.texture("#top"); }
                        else { builder.texture("#side"); }
                    })
                    .end()
                .element()
                    .cube("#level")
                    .faces((dir, builder) -> builder.tintindex(6))
                    .end()
                .element()
                    .face(Direction.DOWN).texture("#port").tintindex(Direction.DOWN.get3DDataValue()).end()
                    .face(Direction.NORTH).texture("#port").tintindex(Direction.NORTH.get3DDataValue()).end()
                    .face(Direction.EAST).texture("#port").tintindex(Direction.EAST.get3DDataValue()).end()
                    .face(Direction.SOUTH).texture("#port").tintindex(Direction.SOUTH.get3DDataValue()).end()
                    .face(Direction.WEST).texture("#port").tintindex(Direction.WEST.get3DDataValue()).end()
                    //.allFacesExcept(Direction.UP, (dir, builder) ->
                    //{
                    //    builder.texture("#port").tintindex(dir.getIndex());
                    //})
                    .end();
    }

    private void casingState(BlockMachine block, ModelFile model)
    {
        ConfiguredModel confModel = ConfiguredModel.builder().modelFile(model).buildLast();
        getVariantBuilder(block).partialState().setModels(confModel);

        simpleBlockItem(block, model);
    }

    private void machineState(BlockMachine block, ModelFile modelOff, ModelFile modelOn)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        for (Direction facing : Direction.Plane.HORIZONTAL)
        {
            ConfiguredModel confOff = ConfiguredModel.builder()
                    .modelFile(modelOff)
                    .rotationY((int)facing.getOpposite().toYRot())
                    .buildLast();
            ConfiguredModel confOn =  ConfiguredModel.builder()
                    .modelFile(modelOn)
                    .rotationY((int)facing.getOpposite().toYRot())
                    .buildLast();

            builder.addModels(builder
                     .partialState()
                     .with(PropertyHolder.FACING_HOR, facing)
                     .with(PropertyHolder.ACTIVE, false), confOff);
            builder.addModels(builder
                    .partialState()
                    .with(PropertyHolder.FACING_HOR, facing)
                    .with(PropertyHolder.ACTIVE, true), confOn);
        }

        simpleBlockItem(block, modelOff);
    }

    private void machineStateNoActive(BlockMachine block, ModelFile modelOff)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        for (Direction facing : Direction.Plane.HORIZONTAL)
        {
            ConfiguredModel confOff = ConfiguredModel.builder()
                    .modelFile(modelOff)
                    .rotationY((int)facing.getOpposite().toYRot())
                    .buildLast();

            builder.addModels(builder
                    .partialState()
                    .with(PropertyHolder.FACING_HOR, facing), confOff);
        }

        simpleBlockItem(block, modelOff);
    }

    private void unrotatableMachineState(BlockMachine block, ModelFile modelOff, ModelFile modelOn)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        ConfiguredModel confOff = ConfiguredModel.builder()
                .modelFile(modelOff)
                .buildLast();
        ConfiguredModel confOn = ConfiguredModel.builder()
                .modelFile(modelOn)
                .buildLast();

        builder.addModels(builder
                .partialState()
                .with(PropertyHolder.ACTIVE, false), confOff);
        builder.addModels(builder
                .partialState()
                .with(PropertyHolder.ACTIVE, true), confOn);

        simpleBlockItem(block, modelOff);
    }

    private void simpleState(Block block, ModelFile model)
    {
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    @Override
    public String getProviderName() { return "machine_block_models"; }

    //private void allFacesExcept(Direction except, BiConsumer<Direction, FaceBuilder> action)
    //{
    //    Arrays.stream(Direction.values()).filter(dir -> dir != except).forEach(d -> action.accept(d, face(d)));
    //}
}