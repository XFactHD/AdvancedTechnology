package xfacthd.advtech.common.util.datagen.providers.model.block;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.BlockMachine;
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
        casingState(ATContent.blockMachineCasing, casingModel);

        ModelFile furnaceModel = machineModel("block_electric_furnace");
        ModelFile furnaceModelOn = machineModel("block_electric_furnace_on");
        machineState(ATContent.blockElectricFurnace, furnaceModel, furnaceModelOn);

        ModelFile crusherModel = machineModel("block_crusher");
        ModelFile crusherModelOn = machineModel("block_crusher_on");
        machineState(ATContent.blockCrusher, crusherModel, crusherModelOn);

        ModelFile alloySmelterModel = machineModel("block_alloy_smelter");
        ModelFile alloySmelterModelOn = machineModel("block_alloy_smelter_on");
        machineState(ATContent.blockAlloySmelter, alloySmelterModel, alloySmelterModelOn);

        ModelFile metalPressModel = machineModel("block_metal_press");
        ModelFile metalPressModelOn = machineModel("block_metal_press_on");
        machineState(ATContent.blockMetalPress, metalPressModel, metalPressModelOn);



        ModelFile burnerGeneratorModel = machineModel("block_burner_generator");
        ModelFile burnerGeneratorModelOn = machineModel("block_burner_generator_on");
        machineState(ATContent.blockBurnerGenerator, burnerGeneratorModel, burnerGeneratorModelOn);
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
                    .face(Direction.UP).texture("#port").tintindex(Direction.UP.getIndex()).end()
                    .face(Direction.DOWN).texture("#port").tintindex(Direction.DOWN.getIndex()).end()
                    .face(Direction.EAST).texture("#port").tintindex(Direction.EAST.getIndex()).end()
                    .face(Direction.SOUTH).texture("#port").tintindex(Direction.SOUTH.getIndex()).end()
                    .face(Direction.WEST).texture("#port").tintindex(Direction.WEST.getIndex()).end()
                    //.allFacesExcept(Direction.NORTH, (dir, builder) -> //TODO: PR to Forge, see impl at the bottom
                    //{
                    //    builder.texture("#port").tintindex(dir.getIndex());
                    //})
                    //.cube("#port")
                    //.faces((dir, builder) -> builder.tintindex(dir.getIndex()))
                    .end();
    }

    private void casingState(BlockMachine block, ModelFile model)
    {
        ConfiguredModel[] confModel = ConfiguredModel.builder().modelFile(model).build();
        getVariantBuilder(block).partialState().setModels(confModel);

        simpleBlockItem(block, model);
    }

    private void machineState(BlockMachine block, ModelFile modelOff, ModelFile modelOn)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        for (Direction facing : Direction.Plane.HORIZONTAL)
        {
            ConfiguredModel[] confOff = ConfiguredModel.builder()
                    .modelFile(modelOff)
                    .rotationY((int)facing.getOpposite().getHorizontalAngle())
                    .build();
            ConfiguredModel[] confOn =  ConfiguredModel.builder()
                    .modelFile(modelOn)
                    .rotationY((int)facing.getOpposite().getHorizontalAngle())
                    .build();

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

    @Override
    public String getProviderName() { return "machine_block_models"; }

    //private void allFacesExcept(Direction except, BiConsumer<Direction, FaceBuilder> action)
    //{
    //    Arrays.stream(Direction.values()).filter(dir -> dir == except).forEach(d -> action.accept(d, face(d)));
    //}
}